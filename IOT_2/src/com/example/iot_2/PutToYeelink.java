package com.example.iot_2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PutToYeelink {

	public String deviceID; // 设备编号
	public String sensorID; // 传感器编号
	public String ApiKey = "9c198e5a1f9a785c89dfe48881504192";
	public String result;
	public String param;

	private String site;
	private String target;

	public String startTimeString = "";
	public String endTimeString = "";

	public void sendPOST() {
		site = "http://api.yeelink.net/";
		target = site + "v1.0/device/" + deviceID + "/sensor/" + sensorID
				+ "/datapoints";
		HttpClient httpclient = new DefaultHttpClient(); // 创建HttpClient对象
		HttpPost httpRequest = new HttpPost(target); // 创建HttpPost对象

		httpRequest.addHeader("Content-Type",
				"application/x-www-form-urlencoded");
		httpRequest.addHeader("U-ApiKey", ApiKey); // "9c198e5a1f9a785c89dfe48881504192");
		httpRequest.addHeader("Accept", "*/*");

		Log.i("调试信息", "目标地址" + target);
		Log.i("调试信息", "要发送的信息是：" + param);
		try {
			httpRequest.setEntity(new StringEntity(param));
			// httpRequest.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			// //添加请求参数
			HttpResponse httpResponse = httpclient.execute(httpRequest); // 发送请求
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) // 如果请求成功
			{
				// 取得返回的数据
				result = EntityUtils
						.toString(httpResponse.getEntity(), "utf-8"); // 获取服务器的响应内容
				Log.i("调试日志", "返回值是：" + result);
			} else
				Log.i("调试日志", "无返回值");

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public JSONObject GET() {
		site = "http://api.yeelink.net/";
		target = site + "v1.0/device/" + deviceID + "/sensor/" + sensorID
				+ "/datapoints";// /"+ApiKey;
		HttpClient httpclient = new DefaultHttpClient(); // 创建HttpClient对象
		HttpGet httpRequest = new HttpGet(target); // 创建HttpGet连接对象

		JSONObject jsonObj=null;
		try {
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) // 如果请求成功
			{
				// 取得返回的数据
				result = EntityUtils
						.toString(httpResponse.getEntity(), "utf-8"); // 获取服务器的响应内容
				try {
					 jsonObj = new JSONObject(result);
					 result = jsonObj.getString("value");
					 endTimeString = jsonObj.getString("timestamp");
				} catch (Exception e) {
					// TODO: handle exception
				}
				//parseJson(result);
				Log.i("调试日志", "返回值是：" + result);
			} else
				Log.i("调试日志", "无返回值");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	// 普通Json数据解析
	private void parseJson(String strResult) {
		try {
			// JSONObject jsonObj = new JSONObject(strResult);
			// result=jsonObj.getString("text");
			JSONObject jsonObj = new JSONObject(strResult);
			result = jsonObj.getString("value");
			endTimeString = jsonObj.getString("timestamp");
			Log.i("调试日志", "解析后的数据" + result);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 获取历史数据 黄帅 2015-5-9
	 */
	public JSONArray GetActList() {
		startTimeString = endTimeString.substring(0, 11);
		startTimeString += "00:00:00";

		site = "http://api.yeelink.net/";
		target = site + "v1.0/device/" + deviceID + "/sensor/" + sensorID
				+ ".json?start=" + startTimeString + "&end=" + endTimeString
				+ "&interval=1&page=1";

		JSONArray feedsArray = null;

		HttpClient httpclient = new DefaultHttpClient(); // 创建HttpClient对象
		HttpGet httpRequest = new HttpGet(target); // 创建HttpGet连接对象

		try {
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) // 如果请求成功
			{
				// 取得返回的数据
				String jsonString = EntityUtils.toString(
						httpResponse.getEntity(), "utf-8"); // 获取服务器的响应内容

				try {
					feedsArray = new JSONArray(jsonString);
				} catch (Exception e) {
					// TODO: handle exception
				}

				Log.i("调试日志", "返回值是：" + jsonString);
			} else {
				Log.i("调试日志", "无返回值");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return feedsArray;
	}
}
