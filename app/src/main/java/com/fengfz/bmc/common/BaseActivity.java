package com.fengfz.bmc.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;


public abstract class BaseActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findView();
		initViewAndData();
		setListener();
	}
	protected abstract void findView();
	protected abstract void initViewAndData();
	protected abstract void setListener();
}
