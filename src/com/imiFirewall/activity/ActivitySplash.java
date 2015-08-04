package com.imiFirewall.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.imiFirewall.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;

public class ActivitySplash extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);
		//设置参数
		new Thread(new Runnable() {
			@Override
			public void run() {
				initPrefers();
			}
		}).start();;
		
		//拷贝权限说明
		new Thread(new Runnable() {
			@Override
			public void run() {
				initAPP();
			}
		}).start();;

		//设置软件欢迎界面
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Intent main_intent = new Intent();
				main_intent.setClass(ActivitySplash.this, ActivityMain.class);
				startActivity(main_intent);
				finish();
			}

		}, 2000);

	}
	
	private void initPrefers() {
		final SharedPreferences sharedPreferences = getSharedPreferences("com.imiFirewall_preferences", MODE_PRIVATE);
		final Editor editor = sharedPreferences.edit();
		if (!sharedPreferences.getBoolean("Network", false)) {
			editor.putBoolean("Network", true);
		}
		if (!sharedPreferences.getBoolean("Spam", false)) {
			editor.putBoolean("Spam", true);
		}
		if (!sharedPreferences.getBoolean("Soft", false)) {
			editor.putBoolean("Soft", true);
		}
		if (!sharedPreferences.getBoolean("network_warn", false)) {
			editor.putBoolean("network_warn", true);
		}
		editor.commit();
	}
	
	private void initAPP() {
		initDir();
		initPerms();
	}
	
	//初始化目录
	private void initDir() {
		String dirPath = Environment.getExternalStorageDirectory() + "/imiFirewall";
		File directory = new File(dirPath);
		if (!directory.exists())
			directory.mkdir();
	}
	
	//复制权限表
	private void initPerms() {
		String dirPath = Environment.getExternalStorageDirectory() + "/imiFirewall/permission.json";
		File fileDes = new File(dirPath);
		if (!fileDes.exists()) {
			InputStream in = (InputStream) ActivitySplash.this.getResources().openRawResource(R.raw.permission);
			try {
				FileOutputStream out = new FileOutputStream(fileDes);
				byte[] buff = new byte[1024];
				int count = 0;
				while ((count = in.read(buff)) > 0)
					out.write(buff, 0, count);
				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}