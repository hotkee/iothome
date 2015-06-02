package com.example.iot_2;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class ShareSensor extends BmobObject implements Serializable {
	/* �����Ӧ�û��˺� ��Ӧ�� ��������� */
	private String HostUser;
	private String User;
	private String DeviceID;
	private String SensorID;
	private String Name;// ����ı��������緿��ĵ�
	private String State;//������״̬
	private BmobFile imageView;

	public BmobFile getImageView() {
		return imageView;
	}

	public void setImageView(BmobFile imageView) {
		this.imageView = imageView;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getHostUser() {
		return HostUser;
	}

	public void setHostUser(String HostUser) {
		this.HostUser = HostUser;
	}

	public String getUser() {
		return User;
	}

	public void setUser(String User) {
		this.User = User;
	}

	public String getDeviceID() {
		return DeviceID;
	}

	public void setDeviceID(String DeviceID) {
		this.DeviceID = DeviceID;
	}

	public String getSensorID() {
		return SensorID;
	}

	public void setSensorID(String SensorID) {
		this.SensorID = SensorID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}
}
