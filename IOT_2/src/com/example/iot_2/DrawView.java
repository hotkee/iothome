package com.example.iot_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.R.string;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/*
 * 功能：数据折线图显示
 * 黄帅
 * 2015-5-9
 */
public class DrawView extends View {

	static public Sensor detail_sensor;// 欲查询的传感器

	private int XPoint = 60; // 原点坐标
	private int YPoint = 1000;
	private int XScale = 8; // 刻度长度
	private int YScale = 20;
	private int XLength = 660; // 坐标轴长度
	private int YLength = 800;

	private int MaxDataSize = XLength / XScale;

	// private PutToYeelink yeelinker;

	private List<Integer> data = new ArrayList<Integer>();
	private String[] YLabel = new String[YLength / YScale];

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x1234) {
				DrawView.this.invalidate();
			}
		};
	};

	private JSONObject oldJsonObject = null;

	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		for (int i = 0; i < YLabel.length; i++) {
			YLabel[i] = (i) + "°C";
		}
		final PutToYeelink yeelinker = new PutToYeelink();
		yeelinker.deviceID = detail_sensor.getDeviceID();
		yeelinker.sensorID = detail_sensor.getSensorID();

		new Thread(new Runnable() {

			@Override
			public void run() {
				yeelinker.GET();
				Log.i("调试信息", "yeelinker.endTimeString"
						+ yeelinker.endTimeString);
				JSONArray actJsonArray = yeelinker.GetActList();
				Log.i("调试信息", "yeelinker.startTimeString"
						+ yeelinker.startTimeString);

				Log.i("调试信息", "actJsonArray.length()" + actJsonArray.length());
				for (int i = 0; i < actJsonArray.length(); i = i + 1) {
					try {
						if (data.size() >= MaxDataSize) {
							data.remove(0);
						}
						JSONObject jsonObj = actJsonArray.getJSONObject(i);
						Log.i("调试信息", "jsonObj" + jsonObj.getInt("value"));
						data.add(jsonObj.getInt("value"));
						handler.sendEmptyMessage(0x1234);

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				oldJsonObject = yeelinker.GET();
				try {
					Log.i("调试信息","1-oldJsonObject-timestamp"+oldJsonObject.getString("timestamp"));
					Log.i("调试信息","1-oldJsonObject-value"+oldJsonObject.getString("value"));
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 实时显示数据
				while (true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (data.size() >= MaxDataSize) {
						data.remove(0);
					}
					// 获取最后一个的数据
					try {
						JSONObject jsonObj = yeelinker.GET();
						Log.i("调试信息","2-JsonObject-timestamp"+jsonObj.getString("timestamp"));
						Log.i("调试信息","2-JsonObject-value"+jsonObj.getString("value"));
						
						if (!jsonObj.getString("timestamp").equals(oldJsonObject.getString("timestamp"))) 
						{
							data.add(jsonObj.getInt("value"));
							Log.i("调试信息","添加"+jsonObj.getInt("value"));
							handler.sendEmptyMessage(0x1234);
							oldJsonObject = jsonObj;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}
		}).start();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true); // 去锯齿
		paint.setColor(Color.BLUE);

		// 画Y轴
		// canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, paint);
		canvas.drawLine(XPoint, YPoint, XPoint, YPoint - YLength, paint);

		// Y轴箭头
		canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint - YLength
				+ 6, paint); // 箭头
		canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint - YLength
				+ 6, paint);

		// 添加刻度和文字
		for (int i = 0; i * YScale < YLength; i++) {
			canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i
					* YScale, paint); // 刻度

			canvas.drawText(YLabel[i], XPoint - 50, YPoint - i * YScale, paint);// 文字
		}

		// 画X轴
		canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, paint);
		// System.out.println("Data.size = " + data.size());
		if (data.size() > 1) {
			for (int i = 1; i < data.size(); i++) {
				canvas.drawLine(XPoint + (i - 1) * XScale,
						YPoint - data.get(i - 1) * YScale, XPoint + i * XScale,
						YPoint - data.get(i) * YScale, paint);
			}
		}
	}

}
