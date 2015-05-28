package com.example.iot_2;

import android.widget.ImageView;

public class itemOfList {
	private Sensor sensor; //传感器编号
	private String stateString; //传感器状态
	private ImageView imageView; //传感器图像

	public itemOfList() {

	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public String getStateString() {
		return stateString;
	}

	public void setStateString(String stateString) {
		this.stateString = stateString;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	public String toString() {
		return "item_list [sensor=" + sensor.getName() + ", stateString=" + stateString
				+ ", imageView=" + imageView + "]";
	}

}
