package com.example.iot_2;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_login extends ActionBarActivity {

	private EditText name_EditText;
	private EditText password_EditText;

	private String username;
	private String password;
	private String email = "";

	public User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Bmob.initialize(this, "c3cf3923b488c52d36b1e2f3260b4f1c");
		Button loginButton = (Button) findViewById(R.id.loginButton);
		Button registerButton = (Button) findViewById(R.id.registerButton);
		// 登陆
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				name_EditText = (EditText) findViewById(R.id.name_EditText);
				password_EditText = (EditText) findViewById(R.id.password_EditText);

				username = name_EditText.getText().toString();
				password = password_EditText.getText().toString();

				user = new User();
				user.setUsername(username);
				user.setPassword(password);
				// 登录操作
				login(user);
			}
		});
		// 注册
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 进入注册界面
				Intent intent = new Intent(Activity_login.this,
						Activity_register.class);
				startActivityForResult(intent, 0x01);
			}
		});
	}

	/* 该方法是获得注册之后的用户名 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x01 && resultCode == 0x01) {
			Bundle bundle = data.getExtras();
			name_EditText = (EditText) findViewById(R.id.name_EditText);
			password_EditText = (EditText) findViewById(R.id.password_EditText);

			username = bundle.getString("Username");
			email = bundle.getString("Email");

			name_EditText.setText(username);

			// 注册成功后，将密码输入框获得焦点
			password_EditText.setFocusable(true);
			password_EditText.requestFocus();

		}
		if (requestCode == 0x02 && resultCode == 0x02) {
			password_EditText = (EditText) findViewById(R.id.password_EditText);
			// 退出登录后，将密码输入框清空，获得焦点
			password_EditText.setFocusable(true);
			password_EditText.requestFocus();
			password_EditText.setText("");
		}
	}

	private void login(final User user) {
		user.login(this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.i("调试信息", user.getUsername() + "登陆成功:");
				DisplayToast("用户：" + user.getUsername() + "登陆成功:");

				// 进入主界面,并将用户信息传入主界面
				Intent intent = new Intent(Activity_login.this,
						Activity_Main.class);

				Bundle bundle = new Bundle();
				bundle.putCharSequence("Username", username);
				bundle.putCharSequence("Email", email);
				bundle.putCharSequence("Password", password);
				intent.putExtras(bundle);

				startActivityForResult(intent, 0x02);

			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				Log.i("调试信息", "登陆失败:" + msg);
				DisplayToast("登陆失败:" + msg);
			}
		});
	}

	public void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
