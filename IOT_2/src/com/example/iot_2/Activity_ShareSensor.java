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
	 * �������-->���ӷ������:0x06
	 */

	private User user;

	private ArrayList<ShareSensor> shareSensorList;// �ӷ�������÷����ȥ�Ĵ���������,������ShareSensor����

	// ����б�
	private ListView shareListView;
	// ����һ
	// private List< Map<String, Object>> shareListItemMaps=new
	// ArrayList<Map<String,Object>>();
	// private List<CheckBox> shareList=new ArrayList<CheckBox>();
	// private SimpleAdapter adapter;

	// ������
	private ArrayAdapter<String> mAdapter;
	// shareSensorNameList����
	private List<String> shareSensorNameList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sharesensor);

		// ��ȡ�����User
		getUserName();
		// DisplayToast("����"+user.getUsername());

		// ���������б���ͼ
		shareListView = (ListView) findViewById(R.id.sharesensorlist);

		// adapter =new SimpleAdapter(this, shareListItemMaps,
		// R.layout.sharesensoritems, new String[]{"title","check"}, new
		// int[]{R.id.itemText,R.id.itemCheckBox});
		// shareListView.setAdapter(adapter);

		// ������
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				shareSensorNameList);
		shareListView.setAdapter(mAdapter);
		shareListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// shareListView.setOnItemClickListener(shareList_ClickListener);

		// ��ȡ����Ĵ�����
		getShareSensor();

		// ��ӷ���ť
		Button addShareSensorButton = (Button) findViewById(R.id.addShareSensorButton);
		addShareSensorButton
				.setOnClickListener(addShareSensorButton_ClickListener);
		// ɾ������ť
		Button removeSensorButton = (Button) findViewById(R.id.removeSensorButton);
		removeSensorButton.setOnClickListener(removeSensorButton_ClickListener);

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
					Log.i("������Ϣ", s.getObjectId() + ":" + ":" + s.getDeviceID()
							+ ":" + s.getSensorID());
				}
				Log.i("������Ϣ", "������" + shareSensorList.size());
				/*
				 * ����õ�����Ϣ���뵽�豸�����б�
				 */
				shareSensorNameList.clear();// ���
				// shareListItemMaps.clear();//���
				for (int i = 0; i < shareSensorList.size(); i++) {
					ShareSensor B = (ShareSensor) shareSensorList.get(i);
					// shareSensorNameList.add("�����"+B.getUser()+"��"+B.getName());
					Log.i("������Ϣ", "�����" + B.getUser() + "��" + B.getName());

					/*
					 * Map<String, Object> map = new HashMap<String,Object>();
					 * map.put("title", "�����"+B.getUser()+":"+B.getName());
					 * shareListItemMaps.add(map);
					 */
					shareSensorNameList.add("�����" + B.getUser() + ":"
							+ B.getName());

				}
				// ˢ��������������listView��ͼ
				// adapter.notifyDataSetChanged();
				mAdapter.notifyDataSetChanged();

				/*
				 * ע�⣡��
				 * �������û�ж�listviewѡ��Ļ���shareListView.getCheckedItemPositions
				 * ()�޷��ҳ�û��ѡ���ѡ��
				 */
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
			Log.i("������Ϣ", "" + b.size());
			for (int i = 0; i < b.size(); i++) {
				if (b.get(i) == true) {
					// Log.i("������Ϣ",(String)
					// shareListView.getItemAtPosition(i));
					// ɾ����Ӧ��ShareSensor
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

	private void removeShareSensor(ShareSensor shareSensor) {// ɾ��Bmob��Ӧ���е�ѡ��!
		Log.i("������Ϣ", "ObjectId" + shareSensor.getObjectId());
		ShareSensor sensor = new ShareSensor();
		sensor.setObjectId(shareSensor.getObjectId());
		sensor.delete(this, new DeleteListener() {

			@Override
			public void onSuccess() {
				DisplayToast("ɾ���ɹ�");
				// ɾ��֮��Ҫ���»�ȡһ��
				getShareSensor();
			}

			@Override
			public void onFailure(int code, String msg) {
				DisplayToast("ɾ��ʧ�ܣ�" + msg);
			}
		});
	}

}
