package com.example.iot_2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.example.iot_2.ImageDownLoad.ImageCallBack;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Main extends Activity implements
		SwipeRefreshLayout.OnRefreshListener, SpeechSynthesizerListener {
	/*
	 * intent 登录界面-->注册界面:0x01 登录界面-->主界面:0x02 主界面-->添加传感器界面:0x03
	 * 主界面-->传感器详细界面:0x04
	 * 
	 * 主界面-->分享界面:0x05 分享界面-->增加分享界面:0x06
	 */

	private static final int REFRESH_COMPLETE = 0X01;
	private static final int REORDER_COMPLETE = 0x02;
	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	// sensorNameList数组
	private List<String> sensorNameList = new ArrayList<String>();
	// 选项卡控件
	private TabHost tabHost;
	// 从服务器获得传感器数组,内容是Sensor对象
	private ArrayList<Sensor> sensorlist = new ArrayList<Sensor>();
	// 用户信息
	private User user;
	// 语音识别启动按钮
	private ImageButton voiceStartButton;
	// 传感器控制“指令”集合
	private List<String> sensorActNameList = new ArrayList<String>();
	// 语音合成
	private SpeechSynthesizer speechSynthesizer;
	// 传感器详细列表适配器
	private MyAdapter adapterOfListView = new MyAdapter();
	private List<itemOfList> listOfMyAdapter = new ArrayList<itemOfList>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE: // 下拉刷新
				getSensor();
				break;
			case REORDER_COMPLETE: // 再次启动语音识别，用于人机对话使用
				voiceStartButton.performClick();
				break;
			}
		};
	};

	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainui);

		Bmob.initialize(this, "c3cf3923b488c52d36b1e2f3260b4f1c");// Bmob初始化

		tabInit();// 初始化选项卡
		getUser();// 获得登录用户的信息
		getSensor(); // 初始化传感器信息
		down_pullInit();// 初始化下拉刷新功能
		// 添加设备
		Button addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(addButton_ClickListener);
		// 分享设备
		Button addShareButton = (Button) findViewById(R.id.myShareButton);
		addShareButton.setOnClickListener(addShareButton_ClickListener);
		// 退出登录
		Button loginoutButton = (Button) findViewById(R.id.loginout);
		loginoutButton.setOnClickListener(loginoutButton_ClickListener);
		/*
		 * 添加语音控制功能 2015-1-30 黄帅
		 */
		voiceStartButton = (ImageButton) findViewById(R.id.startVoiceButton);
		voiceStartButton.setOnClickListener(voiceStartButton_ClickListener);
		/*
		 * 添加语义识别功能 2015-1-31 黄帅
		 */
		sensorActNameList.add("开");
		sensorActNameList.add("关");
		/*
		 * 语音合成功能 2015-1-31 黄帅
		 */
		speechSynthesizer = new SpeechSynthesizer(getApplicationContext(),
				"holder", this);
		speechSynthesizer.setApiKey("BOFojfDwIwSKyT59S3lonDPE",
				"iRau8PzBfH2peKhCQlKaUFUum3AIX3dt");
		speechSynthesizer.speak("你好，主人");

		speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 对音频播报的音频流进行设置
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0"); // 0-女声，1-男声
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); // 中级音量
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); // 中速
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); // 中调
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, "1");// MP3压缩
		speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, "4");// MP3压缩的16K

		speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	/*
	 * 初始化选项卡
	 */
	private void tabInit() {
		// 获取并初始化TabHost组件
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		// 添加标签页
		LayoutInflater inflater = LayoutInflater.from(this);
		inflater.inflate(R.layout.tab0, tabHost.getTabContentView());
		inflater.inflate(R.layout.tab1, tabHost.getTabContentView());
		inflater.inflate(R.layout.tab2, tabHost.getTabContentView());

		tabHost.addTab(tabHost.newTabSpec("0").setIndicator("语音")
				.setContent(R.id.LinearLayout0)); // 语音界面
		tabHost.addTab(tabHost.newTabSpec("1").setIndicator("设备")
				.setContent(R.id.LinearLayout1)); // 设备界面
		tabHost.addTab(tabHost.newTabSpec("2").setIndicator("设置")
				.setContent(R.id.TableLayout2)); // 设置界面
	}

	/*
	 * 初始化下拉刷新
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	private void down_pullInit() {
		mListView = (ListView) findViewById(R.id.id_listview);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		mSwipeLayout.setOnRefreshListener(this);
		// 设置刷新时显示的颜色
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		/*
		 * madapter为纯文字型适配器
		 */
		// 设置madapter为普通显示模式
		// mAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, sensorNameList);
		// 设置madapter为多选模式
		// mAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_multiple_choice,sensorNameList);
		// mListView.setAdapter(mAdapter);
		// mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// 添加列表视图选项点击监听事件
		mListView.setOnItemClickListener(mDeviceClickListener);
	}

	public void onRefresh() {
		try {
			mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000); // 刷新效果的时间
		} catch (Exception ep) {
			Log.i("调试信息", "刷新失败");
		}

	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		// 列表视图按键监听
		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int pos,
				long id) {
			// sensorNameList和sensorlist一一对应
			Sensor detail_sensor = (Sensor) sensorlist.get(pos);
			// 打开Activity_DetailSensor
			Intent intent = new Intent(Activity_Main.this,
					Activity_DetailSensor.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("detail_sensor", detail_sensor);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0x04);
		}
	};

	// 获得用户名下的传感器+并显示到列表
	private void getSensor() {
		// 查找自己的传感器
		BmobQuery<Sensor> query = new BmobQuery<Sensor>();
		query.addWhereEqualTo("User", user.getUsername());
		query.findObjects(this, new FindListener<Sensor>() {
			@Override
			public void onSuccess(List<Sensor> object) {
				// TODO Auto-generated method stub
				sensorlist.clear();
				listOfMyAdapter.clear();

				for (int i = 0; i < object.size(); i++) {
					Sensor s = (Sensor) object.get(i);
					sensorlist.add(s);
					//Log.i("调试信息","file"+s.getImageView().getUrl().toString());
					itemOfList mitemOfList = new itemOfList();
					mitemOfList.setSensor(s);
					mitemOfList.setStateString("off");
					listOfMyAdapter.add(mitemOfList);


				}
				Log.i("调试信息", "数据量" + sensorlist.size());
				// 将获得到的信息加入到设备名称列表
				// sensorNameList.clear();
				// for (int i = 0; i < sensorlist.size(); i++) {
				// Sensor B = (Sensor) sensorlist.get(i);
				// sensorNameList.add(B.getName());
				// }
				// // mAdapter.notifyDataSetChanged();
				// // 绑定adapterOfListView数据源
				// adapterOfListView.bindData(listOfMyAdapter);
				// adapterOfListView.notifyDataSetChanged();
				// mListView.setAdapter(adapterOfListView);
				// // 停止刷新效果
				// mSwipeLayout.setRefreshing(false);
			}

			@Override
			public void onError(int code, String msg) {
				DisplayToast("传感器信息查询失败：" + msg);
				mSwipeLayout.setRefreshing(false);
			}
		});
		// 查找分享给自己的传感器
		BmobQuery<ShareSensor> query_ShareSensor = new BmobQuery<ShareSensor>();
		query_ShareSensor.addWhereEqualTo("User", user.getUsername());
		query_ShareSensor.findObjects(this, new FindListener<ShareSensor>() {
			@Override
			public void onSuccess(List<ShareSensor> object) {
				// TODO Auto-generated method stub
				for (int i = 0; i < object.size(); i++) {
					ShareSensor shareSensor = (ShareSensor) object.get(i);

					Sensor s = new Sensor();
					s.setDeviceID(shareSensor.getDeviceID());
					s.setSensorID(shareSensor.getSensorID());
					s.setUser(shareSensor.getUser());
					s.setName(shareSensor.getHostUser() + "的"
							+ shareSensor.getName());

					sensorlist.add(s);

					itemOfList mitemOfList = new itemOfList();
					mitemOfList.setSensor(s);
					mitemOfList.setStateString("off");
					listOfMyAdapter.add(mitemOfList);
				}
				Log.i("调试信息", "收到的分享的数据量" + object.size());

				// 将获得到的信息加入到设备名称列表
				sensorNameList.clear();
				for (int i = 0; i < sensorlist.size(); i++) {
					Sensor B = (Sensor) sensorlist.get(i);
					sensorNameList.add(B.getName());
				}
				// mAdapter.notifyDataSetChanged();
				// 绑定adapterOfListView数据源
				adapterOfListView.bindData(listOfMyAdapter);
				adapterOfListView.notifyDataSetChanged();
				mListView.setAdapter(adapterOfListView);
				// 停止刷新效果
				mSwipeLayout.setRefreshing(false);
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				DisplayToast("传感器信息查询失败：" + arg1);
			}
		});

	}

	private void getUser() {
		// 获取传入的登录用户的信息
		Intent intent = getIntent();
		final Bundle bundle = intent.getExtras();

		user = new User();
		user.setUsername(bundle.getString("Username"));
		user.setPassword(bundle.getString("Password"));
		user.setEmail(bundle.getString("Email"));
		// Log.i("调试信息", "登录用户是：" + user.getUsername().toString()
		// + user.getPassword().toString() + user.getEmail().toString());
	}

	// 添加设备按钮监听事件
	private OnClickListener addButton_ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Activity_Main.this,
					Activity_AddSensor.class);
			Bundle bundle = new Bundle();
			bundle.putCharSequence("Username", user.getUsername());
			intent.putExtras(bundle);
			startActivityForResult(intent, 0x03);
		}
	};
	// 分享设备按钮监听事件
	private OnClickListener addShareButton_ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Activity_Main.this,
					Activity_ShareSensor.class);
			Bundle bundle = new Bundle();
			bundle.putCharSequence("Username", user.getUsername());
			intent.putExtras(bundle);
			startActivityForResult(intent, 0x05);
		}
	};

	// 退出登录按钮监听事件
	private OnClickListener loginoutButton_ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = getIntent();
			setResult(0x02, intent);
			finish();
		}
	};

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

	/*
	 * 手势检测
	 */
	@SuppressWarnings("deprecation")
	private GestureDetector detector = new GestureDetector(
			new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					if ((e2.getRawX() - e1.getRawX()) > 40) {
						showNext();
						return true;
					}

					if ((e1.getRawX() - e2.getRawX()) > 40) {
						showPre();
						return true;
					}
					return super.onFling(e1, e2, velocityX, velocityY);
				}
			});

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	// 当前索引;
	private int indexOfPage = 0;

	/*
	 * 显示下一个页面
	 */
	protected void showNext() {
		// 三元表达式控制3个页面的循环.
		// tabHost.setCurrentTab(i = i == 2 ? i = 0 : ++i);
		indexOfPage = indexOfPage == 2 ? indexOfPage = 0 : ++indexOfPage;
		tabHost.setCurrentTabByTag(indexOfPage + "");
		Log.i("kennet", indexOfPage + "");
	}

	/*
	 * 显示前一个页面
	 */
	protected void showPre() {
		// 三元表达式控制3个页面的循环.
		indexOfPage = indexOfPage == 0 ? indexOfPage = 2 : --indexOfPage;
		tabHost.setCurrentTabByTag(indexOfPage + "");
		// tabHost.setCurrentTab(i = i == 0 ? i = 2 : --i);
	}

	/*
	 * 语音控制功能 2014-1-30 黄帅
	 */
	private BaiduASRDigitalDialog mDialog = null;
	private OnClickListener voiceStartButton_ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mDialog != null) {
				mDialog.dismiss();
			}
			Bundle params = new Bundle();
			params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
					"BOFojfDwIwSKyT59S3lonDPE");
			params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
					"iRau8PzBfH2peKhCQlKaUFUum3AIX3dt");
			params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
					BaiduASRDigitalDialog.THEME_RED_DEEPBG);
			params.putBoolean(BaiduASRDigitalDialog.PARAM_PROMPT_SOUND_ENABLE,
					true);
			mDialog = new BaiduASRDigitalDialog(Activity_Main.this, params);
			mDialog.setDialogRecognitionListener(mRecognitionListener);
			mDialog.show();
		}
	};
	// 用户语音的文本
	private String recogniText = "";
	private DialogRecognitionListener mRecognitionListener = new DialogRecognitionListener() {
		@Override
		public void onResults(Bundle results) {
			ArrayList<String> rs = results != null ? results
					.getStringArrayList(RESULTS_RECOGNITION) : null;
			if (rs != null && rs.size() > 0) {
				recogniText = rs.get(0);
				Log.i("调试日志", rs.get(0));
				new Thread(new Runnable() {
					public void run() {
						// 判断用户语句中是否有控制命令
						judge();
					}
				}).start();
			}
		}
	};

	// 说笑话次数，为了演示效果而添加的
	private int xiaohuaState = 1;

	private void judge() {
		int len_sensorActNameList = sensorActNameList.size();

		for (int i = 0; i < len_sensorActNameList; i++) {
			// 找指令
			if (recogniText.contains(sensorActNameList.get(i))) {
				// Log.i("调试日志","操作："+recogniText.substring(0,2));
				int len_sensorNameList = sensorNameList.size();
				for (int j = 0; j < len_sensorNameList; j++) {
					// 找传感器
					if (recogniText.contains(sensorNameList.get(j))) {
						if (sensorActNameList.get(i).equals("开")) {
							Log.i("调试日志", "开" + sensorlist.get(j).getName());
							sendToTeelink(sensorlist.get(j), "1");
						} else if (sensorActNameList.get(i).equals("关")) {
							Log.i("调试日志", "关" + sensorlist.get(j).getName());
							sendToTeelink(sensorlist.get(j), "0");
						}
						// 运行到这里，表示已经找到“操作指令”和“传感器”，并已发送指令
						return;
					}
				}
				// 找到语音指令，但没找到设备，语音返回提示信息，要求重新
				speechSynthesizer.speak("主人，你要操作哪个？");
				// 此处延时：因为要等“主人，你要操作哪个？”这句话说完
				mHandler.sendEmptyMessageDelayed(REORDER_COMPLETE, 3500);
				return;
			}
		}
		Log.i("调试信息", "recogniText" + recogniText.toString());
		// 演示效果--说笑话
		if (recogniText.contains("笑话") && (xiaohuaState==1))
		{		
			Log.i("调试信息", "进入");
			speechSynthesizer.speak("主人，我的笑话很冷，加热很耗电哦");
			xiaohuaState++;
			return;
		}
		if (recogniText.contains("没事你说吧")) {
			speechSynthesizer.speak("警告，您的电量只剩百分之一");
			return;
		}
		// 没有找到操作符，就聊天
		// Log.i("调试信息", "准备聊天");
		// sendPOSTToChatRobot(recogniText);
		sendGETToChatRobot(recogniText);
	}

	private PutToYeelink post = new PutToYeelink();

	// 发送操作指令到Yeelink
	private void sendToTeelink(Sensor open_sensor, String act) {
		post.deviceID = open_sensor.getDeviceID();
		post.sensorID = open_sensor.getSensorID();
		post.param = "{\"value\":" + act + "}";
		Thread POSTthread = new Thread(new POSTThread(), "threadPOST");
		POSTthread.start();
	}

	// POST到Yeelink线程
	class POSTThread implements Runnable {
		@Override
		public void run() {
			post.sendPOST();
			speechSynthesizer.speak("主人，我完成好了");
		}
	}

	private String robotReceiveText = "我身体有问题，暂时没法和你聊天";

	// 发送post请求到聊天机器人服务器
	private String sendPOSTToChatRobot(String sendString) {
		String target = "http://www.xiaohuangji.com/ajax.php"; // 目标地址

		HttpClient httpclient = new DefaultHttpClient(); // 创建HttpClient对象
		HttpPost httpRequest = new HttpPost(target); // 创建HttpPost对象

		List<NameValuePair> params = new ArrayList<NameValuePair>(); // 将要传递的参数保存到List集合中
		params.add(new BasicNameValuePair("param", "post")); // 标记参数
		params.add(new BasicNameValuePair("para", sendString));// 内容

		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // 添加请求参数
			HttpResponse httpResponse = httpclient.execute(httpRequest); // 发送请求
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) // 如果请求成功
			{
				// 取得返回的数据
				robotReceiveText = EntityUtils.toString(
						httpResponse.getEntity(), "utf-8"); // 获取服务器的响应内容
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int ret = speechSynthesizer.speak(robotReceiveText
								.toString());
						if (ret != 0) {
							DisplayToast("开启合成器失败！");
						}
					}
				}).start();
			}
			Log.i("调试日志", robotReceiveText);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return robotReceiveText;
	}

	// 发送get请求到聊天机器人服务器
	private String sendGETToChatRobot(String sendString) {
		String APIKEY = "0f6bebe4a29cd5624db9d8f1dac5e767";
		// String INFO = URLEncoder.encode(sendString, "utf-8");
		String target = "http://www.tuling123.com/openapi/api?key=" + APIKEY
				+ "&info=" + sendString; // 目标地址

		// HttpGet对象
		HttpGet httpRequest = new HttpGet(target);
		// HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();

		try {
			// 获得HttpResponse对象
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的数据
				String robotReceiveTextJSON = EntityUtils.toString(
						httpResponse.getEntity(), "utf-8");
				// 解析返回的JSON数据
				robotReceiveText = parseJson(robotReceiveTextJSON);
			}
			Log.i("调试日志", robotReceiveText);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			robotReceiveText = "我身体有问题，暂时没法和你聊天";
		} catch (IOException e) {
			e.printStackTrace();
			robotReceiveText = "我身体有问题，暂时没法和你聊天";
		}

		try {
			// 语音合成 聊天机器人返回的聊天信息
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int ret = speechSynthesizer.speak(robotReceiveText
							.toString());
					if (ret != 0) {
						DisplayToast("开启合成器失败！");
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return robotReceiveText;
	}

	// 解析JSON数据
	private String parseJson(String strResult) {
		String result = "";
		try {
			JSONObject jsonObj = new JSONObject(strResult);
			result = jsonObj.getString("text");
			Log.i("调试日志", "返回数据" + result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	// 语音合成函数
	@Override
	public void onBufferProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancel(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(SpeechSynthesizer arg0, SpeechError arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewDataArrive(SpeechSynthesizer arg0, byte[] arg1,
			boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeechFinish(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeechPause(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		Log.i("调试信息", "onSpeechPause" + arg0.toString());
	}

	@Override
	public void onSpeechProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeechResume(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSpeechStart(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartWorking(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * 功能：屏蔽后退键 黄帅 2015-3-13
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// onSpeechPause(speechSynthesizer);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 传感器列表适配器 黄帅 2015-4-8
	 */
	public class MyAdapter extends BaseAdapter {
		private List<itemOfList> list;

		// 绑定数据源
		public void bindData(List<itemOfList> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(Activity_Main.this).inflate(
						R.layout.item, parent, false);
			} else {
				view = convertView;
			}
			// 此处应该从view里面获取ID号！！！
			// 传感器名字
			TextView subjecTextView = (TextView) view
					.findViewById(R.id.textView1);
			// 传感器状态
			TextView summaryTextView = (TextView) view
					.findViewById(R.id.textView2);

			String string_subjectString = list.get(position).getSensor()
					.getName();
			String string_summaryString = list.get(position).getStateString();

			// 设置字体大小
			subjecTextView.setTextSize(15.0f);
			subjecTextView.setText(string_subjectString);
			summaryTextView.setText(string_summaryString);

			//获取图片
			final ImageView imageView = (ImageView) view
					.findViewById(R.id.imageView1);

//			if(list.get(position).getSensor().getImageView())
//			{
//				String imageURL = "http://file.bmob.cn/"+list.get(position).getSensor().getImageView().getUrl();
//				ImageDownLoad.downLoad(imageURL,
//						new ImageCallBack() {
//
//							@Override
//							public void loadImage(byte[] data) {
//								// TODO Auto-generated method stub
//								Bitmap bitmap = BitmapFactory.decodeByteArray(data,
//										0, data.length);
//								imageView.setImageBitmap(bitmap);
//							}
//						});
//			}		

			return view;
		}
	}
}
