package com.example.iot_2;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

/*
 * 增添功能：数据显示
 * 2015-5-9
 */
public class Activity_DetailSensor extends Activity {

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x01: // 下拉刷新
				//getSensor();
				break;
			case 0x02: // 再次启动语音识别，用于人机对话使用
				//voiceStartButton.performClick();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		final Sensor detail_sensor = (Sensor) bundle.getSerializable("detail_sensor"); // 创建Sensor对象
		DrawView.detail_sensor = detail_sensor;
		// DisplayToast(detail_sensor.getDeviceID() + detail_sensor.getName());

		
		setContentView(R.layout.activity_detailsensor);
		Switch switch1 = (Switch)findViewById(R.id.switch1);
		final PutToYeelink postSwitch = new PutToYeelink();
		postSwitch.deviceID=detail_sensor.getDeviceID();
		postSwitch.sensorID=detail_sensor.getSensorID();
		
		//获取当前状态
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JSONObject jsonObject=postSwitch.GET();
				//jsonObject.getString("");
			}					
		});
		switch1.setText(detail_sensor.getName());
		switch1.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				
				if (isChecked) 
                {  
//                	act.setaction("1");                   
					postSwitch.param = "{\"value\":"+"1"+"}";
                    Log.i("调试信息","选择打开");
                } else 
                {  
//                	act.setaction("0");               	
                	postSwitch.param = "{\"value\":"+"0"+"}";
                	Log.i("调试信息","选择关闭");
                }
				Thread POSTthread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						postSwitch.sendPOST();
					}					
				});
	        	POSTthread.start();
        		Log.i("调试信息","发送Yeelink成功");
			}			
		});

	}

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

}
