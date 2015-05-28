package com.example.iot_2;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;

public class ImageDownLoad {
	public ImageDownLoad() {

	}

	public static void downLoad(final String pathString,
			final ImageCallBack callBack) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				//强制转换数组
				byte[] data = (byte[])msg.obj;
				callBack.loadImage(data);
				
			}
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(pathString);
				HttpResponse httpResponse = null;

				try {
					httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						byte[] data = EntityUtils.toByteArray(httpResponse
								.getEntity());
						Message message = Message.obtain();
						message.obj = data;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
					}

				}

			}
		}).start();
	}

	// 定义图片下载的接口
	public interface ImageCallBack {
		public void loadImage(byte[] data);
	}
}