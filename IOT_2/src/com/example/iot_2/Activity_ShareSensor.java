package com.example.iot_2;

import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_ShareSensor extends Activity {

	/*
	 * 分享界面-->增加分享界面:0x06
	 */

	private User user;

	private ArrayList<ShareSensor> shareSensorList;// 从服务器获得分享出去的传感器数组,内容是ShareSensor对象

	// 组合列表
	private ListView shareListView;
	// 方法一
	// private List< Map<String, Object>> shareListItemMaps=new
	// ArrayList<Map<String,Object>>();
	// private List<CheckBox> shareList=new ArrayList<CheckBox>();
	// private SimpleAdapter adapter;

	// 方法二
	private ArrayAdapter<String> mAdapter;
	// shareSensorNameList数组
	private List<String> shareSensorNameList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharesensor);

		// 获取传入的User
		getUserName();
		// DisplayToast("传入"+user.getUsername());

		// 分享传感器列表视图
		shareListView = (ListView) findViewById(R.id.sharesensorlist);

		// adapter =new SimpleAdapter(this, shareListItemMaps,
		// R.layout.sharesensoritems, new String[]{"title","check"}, new
		// int[]{R.id.itemText,R.id.itemCheckBox});
		// shareListView.setAdapter(adapter);

		// 方法二
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				shareSensorNameList);
		shareListView.setAdapter(mAdapter);
		shareListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// shareListView.setOnItemClickListener(shareList_ClickListener);

		// 获取分享的传感器
		getShareSensor();

		// 添加分享按钮
		Button addShareSensorButton = (Button) findViewById(R.id.addShareSensorButton);
		addShareSensorButton
				.setOnClickListener(addShareSensorButton_ClickListener);
		// 删除分享按钮
		Button removeSensorButton = (Button) findViewById(R.id.removeSensorButton);
		removeSensorButton.setOnClickListener(removeSensorButton_ClickListener);

	}

	private void getUserName() {
		/* ==============================获取传入的数据========================= */
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		user = new User();
		user.setUsername(bundle.getString("Username"));
		Log.i("调试信息", "用户信息是：" + user.getUsername().toString());
	}

	// 获得用户名下的传感器+并显示到列表
	public void GetShareSensor(View view) {
		getShareSensor();
	}

	private void getShareSensor() {
		BmobQuery<ShareSensor> query = new BmobQuery<ShareSensor>();
		query.addWhereEqualTo("HostUser", user.getUsername());
		query.findObjects(this, new FindListener<ShareSensor>() {
			@Override
			public void onSuccess(List<ShareSensor> object) {
				// TODO Auto-generated method stub
				shareSensorList = new ArrayList<ShareSensor>();
				for (int i = 0; i < object.size(); i++) {
					ShareSensor s = (ShareSensor) object.get(i);
					shareSensorList.add(s);
					Log.i("调试信息", s.getObjectId() + ":" + ":" + s.getDeviceID()
							+ ":" + s.getSensorID());
				}
				Log.i("调试信息", "数据量" + shareSensorList.size());
				/*
				 * 将获得到的信息加入到设备名称列表
				 */
				shareSensorNameList.clear();// 清空
				// shareListItemMaps.clear();//清空
				for (int i = 0; i < shareSensorList.size(); i++) {
					ShareSensor B = (ShareSensor) shareSensorList.get(i);
					// shareSensorNameList.add("分享给"+B.getUser()+"的"+B.getName());
					Log.i("调试信息", "分享给" + B.getUser() + "的" + B.getName());

					/*
					 * Map<String, Object> map = new HashMap<String,Object>();
					 * map.put("title", "分享给"+B.getUser()+":"+B.getName());
					 * shareListItemMaps.add(map);
					 */
					shareSensorNameList.add("分享给" + B.getUser() + ":"
							+ B.getName());

				}
				// 刷新适配器，更新listView视图
				// adapter.notifyDataSetChanged();
				mAdapter.notifyDataSetChanged();

				/*
				 * 注意！！
				 * 由于如果没有对listview选择的话，shareListView.getCheckedItemPositions
				 * ()无法找出没有选择的选项
				 */
				for (int i = 0; i < shareListView.getCount(); i++) {
					shareListView.setItemChecked(i, false);
				}
			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
				DisplayToast("传感器信息查询失败：" + msg);
			}
		});
	}

	private OnClickListener addShareSensorButton_ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Activity_ShareSensor.this,
					Activity_AddShareSensor.class);
			Bundle bundle = new Bundle();
			bundle.putCharSequence("Username", user.getUsername());
			intent.putExtras(bundle);
			startActivityForResult(intent, 0x06);
		}
	};
	private OnClickListener removeSensorButton_ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// int i =shareListView.getCheckedItemCount();
			// int a=shareListView.getCheckedItemPosition();
			// shareListView.getch

			SparseBooleanArray b = shareListView.getCheckedItemPositions();
			Log.i("调试信息", "" + b.size());
			for (int i = 0; i < b.size(); i++) {
				if (b.get(i) == true) {
					// Log.i("调试信息",(String)
					// shareListView.getItemAtPosition(i));
					// 删除对应的ShareSensor
					ShareSensor shareSensor = shareSensorList.get(i);
					removeShareSensor(shareSensor);
				}

			}

		}
	};

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

//	private OnItemClickListener shareList_ClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// 
//
//		}
//
//	};

	private void removeShareSensor(ShareSensor shareSensor) {// 删除Bmob对应表中的选项!
		Log.i("调试信息", "ObjectId" + shareSensor.getObjectId());
		ShareSensor sensor = new ShareSensor();
		sensor.setObjectId(shareSensor.getObjectId());
		sensor.delete(this, new DeleteListener() {

			@Override
			public void onSuccess() {
				DisplayToast("删除成功");
				// 删完之后要重新获取一遍
				getShareSensor();
			}

			@Override
			public void onFailure(int code, String msg) {
				DisplayToast("删除失败：" + msg);
			}
		});
	}

}
