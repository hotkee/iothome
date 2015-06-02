package com.example.iot_2;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Activity_AddShareSensor extends Activity {

	private User user;
	// 从服务器获得分享出去的传感器数组,内容是ShareSensor对象
	private ArrayList<Sensor> SensorList;

	// 传感器列表
	private ListView shareListView;
	private ArrayAdapter<String> mAdapter;
	private List<String> SensorNameList = new ArrayList<String>();// SensorNameList数组

	// 用户列表
	private ListView userListView;
	private ArrayAdapter<String> userAdapter;
	private List<String> userNameList = new ArrayList<String>();// SensorNameList数组

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addsharesensor);

		// 获取传入的User
		getUserName();

		// 将获取到sensor的加入到列表视图
		shareListView = (ListView) findViewById(R.id.mysensorlist);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				SensorNameList);
		shareListView.setAdapter(mAdapter);
		shareListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// 获取名下传感器
		getShareSensor();

		// 将获取到user的加入到列表视图
		userListView = (ListView) findViewById(R.id.myfriendlist);
		userAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, userNameList);
		userListView.setAdapter(userAdapter);
		userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// 获取可分享的用户
		getAllUser();

		Button shareButton = (Button) findViewById(R.id.shareButton);
		shareButton.setOnClickListener(shareButton_ClickListener);

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
	private void getShareSensor() {
		BmobQuery<Sensor> query = new BmobQuery<Sensor>();
		query.addWhereEqualTo("User", user.getUsername());
		query.findObjects(this, new FindListener<Sensor>() {
			@Override
			public void onSuccess(List<Sensor> object) {
				// TODO Auto-generated method stub
				SensorList = new ArrayList<Sensor>();
				for (int i = 0; i < object.size(); i++) {
					Sensor s = (Sensor) object.get(i);
					SensorList.add(s);
					Log.i("调试信息", s.getObjectId() + ":" + ":" + s.getDeviceID()
							+ ":" + s.getSensorID());
				}
				Log.i("调试信息", "SensorList.size数据量" + SensorList.size());
				/*
				 * 将获得到的信息加入到设备名称列表
				 */
				SensorNameList.clear();// 清空
				for (int i = 0; i < SensorList.size(); i++) {
					Sensor B = (Sensor) SensorList.get(i);
					Log.i("调试信息", "分享给" + B.getUser() + "的" + B.getName());

					SensorNameList.add(B.getName());

				}
				// 刷新适配器，更新listView视图
				mAdapter.notifyDataSetChanged();

				/*
				 * 注意！！
				 * 由于如果没有对listview选择的话，shareListView.getCheckedItemPositions
				 * ()无法找出没有选择的选项
				 */
				Log.i("调试信息",
						"shareListView.getCount()" + shareListView.getCount());
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

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

	// 获取全部用户名
	private void getAllUser() {
		BmobQuery<User> query = new BmobQuery<User>();
		query.findObjects(this, new FindListener<User>() {
			@Override
			public void onSuccess(List<User> object) {
				// TODO Auto-generated method stub
				userNameList.clear();
				for (int i = 0; i < object.size(); i++) {
					// Log.i("调试信息",object.get(i).getUsername());
					if (object.get(i).getUsername().equals(user.getUsername()))
						continue;
					userNameList.add(object.get(i).getUsername());
				}
				// 刷新适配器，更新listView视图
				userAdapter.notifyDataSetChanged();
				Log.i("调试信息",
						"userListView.getCount()" + userListView.getCount());
				for (int i = 0; i < userListView.getCount(); i++) {
					userListView.setItemChecked(i, false);
				}
			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
				DisplayToast("查询失败：" + msg);
			}
		});
	}

	//
	private OnClickListener shareButton_ClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SparseBooleanArray b = userListView.getCheckedItemPositions();
			Log.i("调试信息", "userListView.getCheckedItemPositions  " + b.size());

			for (int i = 0; i < b.size(); i++) {
				if (b.get(i) == true) {// 该用户被选中
					SparseBooleanArray a = shareListView
							.getCheckedItemPositions();
					Log.i("调试信息",
							"shareListView.getCheckedItemPositions  "
									+ a.size());
					for (int j = 0; j < a.size(); j++) {
						if (a.get(j) == true) {// 增加对应的User名下的ShareSensor记录
							ShareSensor shareSensor = new ShareSensor();
							shareSensor.setHostUser(user.getUsername());
							shareSensor.setUser(userNameList.get(i));
							shareSensor.setDeviceID(SensorList.get(j)
									.getDeviceID());
							shareSensor.setSensorID(SensorList.get(j)
									.getSensorID());
							shareSensor.setName(SensorList.get(j).getName());

							Log.i("调试信息", "准备保存");
							SaveshareSensor(shareSensor);
						}
					}
				}
			}

		}
	};

	private void SaveshareSensor(ShareSensor shareSensor) {
		shareSensor.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				DisplayToast("保存成功");
			}

			@Override
			public void onFailure(int code, String arg0) {
				// TODO Auto-generated method stub
				// 添加失败
				DisplayToast("保存失败" + arg0);
			}
		});
	}

}
