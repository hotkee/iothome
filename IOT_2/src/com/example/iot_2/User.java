package com.example.iot_2;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {
	public String YLname;
	private String YLpassword;
	private String YLApi;

	// YLname
	public String getYLname() {
		return YLname;
	}

	public void setYLname(String YLname) {
		this.YLname = YLname;
	}

	// YLpassword
	public String getYLpassword() {
		return YLpassword;
	}

	public void setYLpassword(String YLpassword) {
		this.YLpassword = YLpassword;
	}

	// YLApi
	public String getYLApi() {
		return YLApi;
	}

	public void setYLApi(String YLApi) {
		this.YLApi = YLApi;
	}

}
