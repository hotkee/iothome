package com.example.iot_2;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Activity_AddSensor extends Activity {
	private Spinner deviceName;
	private EditText DeviceText;
	private EditText SensorText;
	private EditText SensorName;
	private Button addButton;
	private Sensor sensor;
	// ��ά����Ӵ�����
	private Button addPhotoButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addsensor);

		/* ==============================��ȡ���������========================= */
		Intent intent = getIntent();
		final Bundle bundle1 = intent.getExtras();

		sensor = new Sensor(); // ����Sensor����
		sensor.setUser(bundle1.getString("Username").toString());

		deviceName = (Spinner) findViewById(R.id.deviceName);
		DeviceText = (EditText) findViewById(R.id.DeviceID_EditText);
		SensorText = (EditText) findViewById(R.id.SensorID_EditText);
		SensorName = (EditText) findViewById(R.id.SensorName_EditText);

		// �����б����¼�������
		deviceName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long id) {
				// TODO Auto-generated method stub
				String result = parent.getItemAtPosition(pos).toString();
				DeviceText.setText(getDeviceNum(result));

				SensorText.setFocusable(true);
				SensorText.requestFocus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
		addPhotoButton = (Button) findViewById(R.id.addPhotoButton);
		addPhotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//������������ն�ά��
				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				startActivity(intent);
			}
		});

		addButton = (Button) findViewById(R.id.addSensorButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sensor.setDeviceID(DeviceText.getText().toString());
				sensor.setSensorID(SensorText.getText().toString());
				sensor.setName(SensorName.getText().toString());
				CheckSensor();
			}
		});
	}

	private String getDeviceNum(String result) {
		switch (deviceName.getSelectedItem().toString()) {
		case "����":
			return "12913";
		case "�¶ȴ�����":
			return "13516";
		case "ʪ�ȴ�����":
			return "";
		case "���մ�����":
			return "";
		case "CO2������":
			return "";
		}
		return "";
	}

	// ���ͬһ���˺� ��û��ע����ô�����
	private void CheckSensor() {
		/***************************************************/
		/****** �˴�ʹ�ø��ϲ�ѯ�� ���� ***********/
		/** ��ѯʹ��ͬһ��SensorID ���� ͬһ��SensorName�Ĵ����� **/
		/****** ������ֵ����0��������� **********/
		/***********************************************/
		BmobQuery<Sensor> query1 = new BmobQuery<Sensor>();
		query1.addWhereEqualTo("User", sensor.getUser());
		query1.addWhereEqualTo("DeviceID", sensor.getDeviceID());
		query1.addWhereEqualTo("SensorID", sensor.getSensorID());

		BmobQuery<Sensor> query2 = new BmobQuery<Sensor>();
		query2.addWhereEqualTo("User", sensor.getUser());
		query2.addWhereEqualTo("Name", sensor.getName());

		List<BmobQuery<Sensor>> queries = new ArrayList<BmobQuery<Sensor>>();
		queries.add(query1);
		queries.add(query2);
		BmobQuery<Sensor> mainQuery = new BmobQuery<Sensor>();
		mainQuery.or(queries);// �����㣡��
		mainQuery.findObjects(this, new FindListener<Sensor>() {
			@Override
			public void onSuccess(List<Sensor> object) {
				if (object.size() >= 1) {
					AlertDialog alert = new AlertDialog.Builder(
							Activity_AddSensor.this).create();
					alert.setTitle("��Ӵ������ڵ�ʧ�ܣ�");
					alert.setMessage("���˻�����Ӹô��������Ƿ���������µĴ�������");
					alert.setButton(DialogInterface.BUTTON_NEGATIVE, "��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = getIntent();
									setResult(0x03, intent);
									finish();
								}
							});
					alert.setButton(DialogInterface.BUTTON_POSITIVE, "��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									SensorText.setText("");
								}
							});
					alert.show();
				} else if (object.size() == 0) {
					saveSensor(); // ���洫������Ϣ���˺�
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void saveSensor() {
		sensor.save(this, new SaveListener() {
			@Override
			public void onSuccess() {
				Log.i("������Ϣ",
						"���DevSen���ݳɹ�������objectIdΪ��" + sensor.getObjectId());

				AlertDialog alert = new AlertDialog.Builder(
						Activity_AddSensor.this).create();
				alert.setTitle("��Ӵ������ڵ�ɹ���");
				alert.setMessage("�Ƿ������ӣ�");
				alert.setButton(DialogInterface.BUTTON_NEGATIVE, "��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// saveSensor(); //���洫������Ϣ���˺�
								Intent intent = getIntent();
								setResult(0x03, intent);
								finish();
							}
						});
				alert.setButton(DialogInterface.BUTTON_POSITIVE, "��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// saveSensor(); //���洫������Ϣ���˺�
								SensorText.setText("");
							}
						});
				alert.show();
				// DisplayToast("��������Ϣ����ɹ�");
			}

			@Override
			public void onFailure(int code, String msg) {
				Log.i("������Ϣ", "��������ʧ�ܣ�" + msg);

				AlertDialog alert = new AlertDialog.Builder(
						Activity_AddSensor.this).create();
				alert.setTitle("��Ӵ������ڵ�ʧ�ܣ�");
				alert.setMessage("������ϣ��Ƿ�������ӣ�");
				alert.setButton(DialogInterface.BUTTON_NEGATIVE, "��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = getIntent();
								setResult(0x03, intent);
								finish();
							}
						});
				alert.setButton(DialogInterface.BUTTON_POSITIVE, "��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								saveSensor(); // ���洫������Ϣ���˺�
							}
						});
				alert.show();

				// DisplayToast("��������Ϣ����ʧ��");
			}

		});
	}

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

}