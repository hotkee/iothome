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
	// �ӷ�������÷����ȥ�Ĵ���������,������ShareSensor����
	private ArrayList<Sensor> SensorList;

	// �������б�
	private ListView shareListView;
	private ArrayAdapter<String> mAdapter;
	private List<String> SensorNameList = new ArrayList<String>();// SensorNameList����

	// �û��б�
	private ListView userListView;
	private ArrayAdapter<String> userAdapter;
	private List<String> userNameList = new ArrayList<String>();// SensorNameList����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addsharesensor);

		// ��ȡ�����User
		getUserName();

		// ����ȡ��sensor�ļ��뵽�б���ͼ
		shareListView = (ListView) findViewById(R.id.mysensorlist);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				SensorNameList);
		shareListView.setAdapter(mAdapter);
		shareListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// ��ȡ���´�����
		getShareSensor();

		// ����ȡ��user�ļ��뵽�б���ͼ
		userListView = (ListView) findViewById(R.id.myfriendlist);
		userAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, userNameList);
		userListView.setAdapter(userAdapter);
		userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// ��ȡ�ɷ�����û�
		getAllUser();

		Button shareButton = (Button) findViewById(R.id.shareButton);
		shareButton.setOnClickListener(shareButton_ClickListener);

	}

	private void getUserName() {
		/* ==============================��ȡ���������========================= */
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		user = new User();
		user.setUsername(bundle.getString("Username"));
		Log.i("������Ϣ", "�û���Ϣ�ǣ�" + user.getUsername().toString());
	}

	// ����û����µĴ�����+����ʾ���б�
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
					Log.i("������Ϣ", s.getObjectId() + ":" + ":" + s.getDeviceID()
							+ ":" + s.getSensorID());
				}
				Log.i("������Ϣ", "SensorList.size������" + SensorList.size());
				/*
				 * ����õ�����Ϣ���뵽�豸�����б�
				 */
				SensorNameList.clear();// ���
				for (int i = 0; i < SensorList.size(); i++) {
					Sensor B = (Sensor) SensorList.get(i);
					Log.i("������Ϣ", "�����" + B.getUser() + "��" + B.getName());

					SensorNameList.add(B.getName());

				}
				// ˢ��������������listView��ͼ
				mAdapter.notifyDataSetChanged();

				/*
				 * ע�⣡��
				 * �������û�ж�listviewѡ��Ļ���shareListView.getCheckedItemPositions
				 * ()�޷��ҳ�û��ѡ���ѡ��
				 */
				Log.i("������Ϣ",
						"shareListView.getCount()" + shareListView.getCount());
				for (int i = 0; i < shareListView.getCount(); i++) {
					shareListView.setItemChecked(i, false);
				}
			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
				DisplayToast("��������Ϣ��ѯʧ�ܣ�" + msg);
			}
		});
	}

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

	// ��ȡȫ���û���
	private void getAllUser() {
		BmobQuery<User> query = new BmobQuery<User>();
		query.findObjects(this, new FindListener<User>() {
			@Override
			public void onSuccess(List<User> object) {
				// TODO Auto-generated method stub
				userNameList.clear();
				for (int i = 0; i < object.size(); i++) {
					// Log.i("������Ϣ",object.get(i).getUsername());
					if (object.get(i).getUsername().equals(user.getUsername()))
						continue;
					userNameList.add(object.get(i).getUsername());
				}
				// ˢ��������������listView��ͼ
				userAdapter.notifyDataSetChanged();
				Log.i("������Ϣ",
						"userListView.getCount()" + userListView.getCount());
				for (int i = 0; i < userListView.getCount(); i++) {
					userListView.setItemChecked(i, false);
				}
			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
				DisplayToast("��ѯʧ�ܣ�" + msg);
			}
		});
	}

	//
	private OnClickListener shareButton_ClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SparseBooleanArray b = userListView.getCheckedItemPositions();
			Log.i("������Ϣ", "userListView.getCheckedItemPositions  " + b.size());

			for (int i = 0; i < b.size(); i++) {
				if (b.get(i) == true) {// ���û���ѡ��
					SparseBooleanArray a = shareListView
							.getCheckedItemPositions();
					Log.i("������Ϣ",
							"shareListView.getCheckedItemPositions  "
									+ a.size());
					for (int j = 0; j < a.size(); j++) {
						if (a.get(j) == true) {// ���Ӷ�Ӧ��User���µ�ShareSensor��¼
							ShareSensor shareSensor = new ShareSensor();
							shareSensor.setHostUser(user.getUsername());
							shareSensor.setUser(userNameList.get(i));
							shareSensor.setDeviceID(SensorList.get(j)
									.getDeviceID());
							shareSensor.setSensorID(SensorList.get(j)
									.getSensorID());
							shareSensor.setName(SensorList.get(j).getName());

							Log.i("������Ϣ", "׼������");
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
				DisplayToast("����ɹ�");
			}

			@Override
			public void onFailure(int code, String arg0) {
				// TODO Auto-generated method stub
				// ���ʧ��
				DisplayToast("����ʧ��" + arg0);
			}
		});
	}

}
