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
 * �����ܣ�������ʾ
 * 2015-5-9
 */
public class Activity_DetailSensor extends Activity {

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x01: // ����ˢ��
				//getSensor();
				break;
			case 0x02: // �ٴ���������ʶ�������˻��Ի�ʹ��
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

		final Sensor detail_sensor = (Sensor) bundle.getSerializable("detail_sensor"); // ����Sensor����
		DrawView.detail_sensor = detail_sensor;
		// DisplayToast(detail_sensor.getDeviceID() + detail_sensor.getName());

		
		setContentView(R.layout.activity_detailsensor);
		Switch switch1 = (Switch)findViewById(R.id.switch1);
		final PutToYeelink postSwitch = new PutToYeelink();
		postSwitch.deviceID=detail_sensor.getDeviceID();
		postSwitch.sensorID=detail_sensor.getSensorID();
		
		//��ȡ��ǰ״̬
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
                    Log.i("������Ϣ","ѡ���");
                } else 
                {  
//                	act.setaction("0");               	
                	postSwitch.param = "{\"value\":"+"0"+"}";
                	Log.i("������Ϣ","ѡ��ر�");
                }
				Thread POSTthread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						postSwitch.sendPOST();
					}					
				});
	        	POSTthread.start();
        		Log.i("������Ϣ","����Yeelink�ɹ�");
			}			
		});

	}

	private void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

}
