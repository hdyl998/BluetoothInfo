package com.fengfz.bmc.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.hdyl.bluetoothinfo.R;
import com.fengfz.bmc.finddevice.FindDeviceActivity;

//启动activity
public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
		findView();
		initilizeViewAndData();
		setOnListener();
	}

	protected void findView() {
	}

	protected void initilizeViewAndData() {

		new Thread() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					Thread.currentThread().sleep(0000);// ////////要改
					finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startActivity(new Intent(StartActivity.this, FindDeviceActivity.class));
			};
		}.start();
	}

	protected void setOnListener() {

	}
}
