package com.example.iot_2;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_register extends Activity {
	private User user;
	private String Username;
	private String Email;
	private String Password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		Bmob.initialize(this, "c3cf3923b488c52d36b1e2f3260b4f1c");

		user = new User();

		Button signButton = (Button) findViewById(R.id.signButton);
		signButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText user_Name = (EditText) findViewById(R.id.user_Name);
				EditText user_mail = (EditText) findViewById(R.id.user_mail);
				EditText user_password = (EditText) findViewById(R.id.user_password);

				Username = user_Name.getText().toString();
				Email = user_mail.getText().toString();
				Password = user_password.getText().toString();

				user.setUsername(Username);
				user.setEmail(Email);
				user.setPassword(Password);
				// user.setEmailVerified(true);说明已邮箱验证

				/* 在原型系统中，YEELINK数据库不向用户开放 */
				user.setYLname("recursion");
				user.setYLpassword("aaa111");
				user.setYLApi("9c198e5a1f9a785c89dfe48881504192");

				zhuce(user);
			}
		});

	}

	private void zhuce(final User user) {
		user.signUp(this, new SaveListener() {
			@Override
			public void onSuccess() {
				new AlertDialog.Builder(Activity_register.this)
						.setTitle("系统提示")
						.setMessage("用户创建成功！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Intent intent = getIntent();
										Bundle bundle = new Bundle();
										bundle.putCharSequence("Username",
												Username);
										bundle.putCharSequence("Email", Email);
										bundle.putCharSequence("Password",
												Password);
										intent.putExtras(bundle);
										setResult(0x01, intent);

										finish();
									}
								}).show();
				Log.i("调试信息", "添加数据成功，返回objectId为：" + user.getObjectId());

			}

			@Override
			public void onFailure(int code, String msg) {
				Log.i("调试信息", "创建数据失败：" + msg);
				DisplayToast("创建失败" + msg);
			}
		});
	}

	public void DisplayToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

}
