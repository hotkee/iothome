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
	// 二维码添加传感器
	private Button addPhotoButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addsensor);

		/* ==============================获取传入的数据========================= */
		Intent intent = getIntent();
		final Bundle bundle1 = intent.getExtras();

		sensor = new Sensor(); // 创建Sensor对象
		sensor.setUser(bundle1.getString("Username").toString());

		deviceName = (Spinner) findViewById(R.id.deviceName);
		DeviceText = (EditText) findViewById(R.id.DeviceID_EditText);
		SensorText = (EditText) findViewById(R.id.SensorID_EditText);
		SensorName = (EditText) findViewById(R.id.SensorName_EditText);

		// 设置列表项事件监听器
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
				//调用相机，拍照二维码
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
		case "开关":
			return "12913";
		case "温度传感器":
			return "13516";
		case "湿度传感器":
			return "";
		case "光照传感器":
			return "";
		case "CO2传感器":
			return "";
		}
		return "";
	}

	// 检查同一个账号 有没有注册过该传感器
	private void CheckSensor() {
		/***************************************************/
		/****** 此处使用复合查询！ （或） ***********/
		/** 查询使用同一个SensorID 或者 同一个SensorName的传感器 **/
		/****** 若返回值大于0，则不能添加 **********/
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
		mainQuery.or(queries);// 或运算！！
		mainQuery.findObjects(this, new FindListener<Sensor>() {
			@Override
			public void onSuccess(List<Sensor> object) {
				if (object.size() >= 1) {
					AlertDialog alert = new AlertDialog.Builder(
							Activity_AddSensor.this).create();
					alert.setTitle("添加传感器节点失败！");
					alert.setMessage("本账户已添加该传感器，是否重新添加新的传感器？");
					alert.setButton(DialogInterface.BUTTON_NEGATIVE, "否",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = getIntent();
									setResult(0x03, intent);
									finish();
								}
							});
					alert.setButton(DialogInterface.BUTTON_POSITIVE, "是",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									SensorText.setText("");
								}
							});
					alert.show();
				} else if (object.size() == 0) {
					saveSensor(); // 保存传感器信息至账号
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
				Log.i("调试信息",
						"添加DevSen数据成功，返回objectId为：" + sensor.getObjectId());

				AlertDialog alert = new AlertDialog.Builder(
						Activity_AddSensor.this).create();
				alert.setTitle("添加传感器节点成功！");
				alert.setMessage("是否继续添加？");
				alert.setButton(DialogInterface.BUTTON_NEGATIVE, "否",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// saveSensor(); //保存传感器信息至账号
								Intent intent = getIntent();
								setResult(0x03, intent);
								finish();
							}
						});
				alert.setButton(DialogInterface.BUTTON_POSITIVE, "是",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// saveSensor(); //保存传感器信息至账号
								SensorText.setText("");
							}
						});
				alert.show();
				// DisplayToast("传感器信息保存成功");
			}

			@Override
			public void onFailure(int code, String msg) {
				Log.i("调试信息", "创建数据失败：" + msg);

				AlertDialog alert = new AlertDialog.Builder(
						Activity_AddSensor.this).create();
				alert.setTitle("添加传感器节点失败！");
				alert.setMessage("网络故障，是否重新添加？");
				alert.setButton(DialogInterface.BUTTON_NEGATIVE, "否",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = getIntent();
								setResult(0x03, intent);
								finish();
							}
						});
				alert.setButton(DialogInterface.BUTTON_POSITIVE, "是",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								saveSensor(); // 保存传感器信息至账号
							}
						});
				alert.show();

				// DisplayToast("传感器信息保存失败");
			}

		});
	}

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

}