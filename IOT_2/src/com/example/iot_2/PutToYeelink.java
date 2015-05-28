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

	public String deviceID; // �豸���
	public String sensorID; // ���������
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
		HttpClient httpclient = new DefaultHttpClient(); // ����HttpClient����
		HttpPost httpRequest = new HttpPost(target); // ����HttpPost����

		httpRequest.addHeader("Content-Type",
				"application/x-www-form-urlencoded");
		httpRequest.addHeader("U-ApiKey", ApiKey); // "9c198e5a1f9a785c89dfe48881504192");
		httpRequest.addHeader("Accept", "*/*");

		Log.i("������Ϣ", "Ŀ���ַ" + target);
		Log.i("������Ϣ", "Ҫ���͵���Ϣ�ǣ�" + param);
		try {
			httpRequest.setEntity(new StringEntity(param));
			// httpRequest.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			// //����������
			HttpResponse httpResponse = httpclient.execute(httpRequest); // ��������
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) // �������ɹ�
			{
				// ȡ�÷��ص�����
				result = EntityUtils
						.toString(httpResponse.getEntity(), "utf-8"); // ��ȡ����������Ӧ����
				Log.i("������־", "����ֵ�ǣ�" + result);
			} else
				Log.i("������־", "�޷���ֵ");

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
		HttpClient httpclient = new DefaultHttpClient(); // ����HttpClient����
		HttpGet httpRequest = new HttpGet(target); // ����HttpGet���Ӷ���

		JSONObject jsonObj=null;
		try {
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) // �������ɹ�
			{
				// ȡ�÷��ص�����
				result = EntityUtils
						.toString(httpResponse.getEntity(), "utf-8"); // ��ȡ����������Ӧ����
				try {
					 jsonObj = new JSONObject(result);
					 result = jsonObj.getString("value");
					 endTimeString = jsonObj.getString("timestamp");
				} catch (Exception e) {
					// TODO: handle exception
				}
				//parseJson(result);
				Log.i("������־", "����ֵ�ǣ�" + result);
			} else
				Log.i("������־", "�޷���ֵ");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	// ��ͨJson���ݽ���
	private void parseJson(String strResult) {
		try {
			// JSONObject jsonObj = new JSONObject(strResult);
			// result=jsonObj.getString("text");
			JSONObject jsonObj = new JSONObject(strResult);
			result = jsonObj.getString("value");
			endTimeString = jsonObj.getString("timestamp");
			Log.i("������־", "�����������" + result);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ��ȡ��ʷ���� ��˧ 2015-5-9
	 */
	public JSONArray GetActList() {
		startTimeString = endTimeString.substring(0, 11);
		startTimeString += "00:00:00";

		site = "http://api.yeelink.net/";
		target = site + "v1.0/device/" + deviceID + "/sensor/" + sensorID
				+ ".json?start=" + startTimeString + "&end=" + endTimeString
				+ "&interval=1&page=1";

		JSONArray feedsArray = null;

		HttpClient httpclient = new DefaultHttpClient(); // ����HttpClient����
		HttpGet httpRequest = new HttpGet(target); // ����HttpGet���Ӷ���

		try {
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) // �������ɹ�
			{
				// ȡ�÷��ص�����
				String jsonString = EntityUtils.toString(
						httpResponse.getEntity(), "utf-8"); // ��ȡ����������Ӧ����

				try {
					feedsArray = new JSONArray(jsonString);
				} catch (Exception e) {
					// TODO: handle exception
				}

				Log.i("������־", "����ֵ�ǣ�" + jsonString);
			} else {
				Log.i("������־", "�޷���ֵ");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return feedsArray;
	}
}
