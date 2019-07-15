package com.fengfz.bmc.common;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.hdyl.bluetoothinfo.R;

//我的对话框
public class MyDialog extends Dialog implements android.view.View.OnClickListener {
	TextView tvOK, tvCancel;
	OnClickButtonListener listener;

	public MyDialog(Context context, String title, String content) {// 内容标题
		super(context, R.style.dialog);// 样式
		init(title, content);
	}

	public MyDialog(Context context, int titleId, int contentId) {// 内容标题
		super(context, R.style.dialog);// 样式
		Resources resources = context.getResources();
		init(resources.getString(titleId), resources.getString(contentId));
	}

	public MyDialog(Context context, String content) {// 内容标题
		super(context, R.style.dialog);// 样式
		init("消息", content);
	}


	// 初始化
	private void init(String title, String content) {
		this.setContentView(R.layout.dialog_my);// 布局
		((TextView) findViewById(R.id.text_dialog_title)).setText(title);
		((TextView) findViewById(R.id.text_dialog_content)).setText(content);
		tvOK = (TextView) findViewById(R.id.tv_dialog_ok);
		tvCancel = (TextView) findViewById(R.id.tv_dialog_cancel);
		tvOK.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// getWindow().setWindowAnimations(R.style.dialogWindowAnim2); //
		// 设置窗口弹出动画

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_dialog_ok:
				if (listener != null)
					listener.clickOKButton();
				break;
			case R.id.tv_dialog_cancel:
				if (listener != null)
					listener.clickCancelButton();
				break;
		}
		this.dismiss();
	}

	// 显示信息对话框,没有取消
	public void showInfoDialog() {
		tvCancel.setVisibility(View.GONE);
		show();
	}

	// 显示确认对话框
	public void showConfirmDialog(String OKString, String cancleString, OnClickButtonListener OKClickListener) {
		tvOK.setText(OKString);
		tvCancel.setText(cancleString);
		this.listener = OKClickListener;
		show();
	}

	// 显示确认对话框
	public void showConfirmDialog(int OKRes, int canRes, OnClickButtonListener OKClickListener) {
		tvOK.setText(OKRes);
		tvCancel.setText(canRes);
		this.listener = OKClickListener;
		show();
	}

	public void setOKString(String okString) {
		tvOK.setText(okString);
	}

	public void setCancelString(String cancelString) {
		tvCancel.setText(cancelString);
	}

	// 显示确认对话框
	public void showConfirmDialog(OnClickButtonListener OKClickListener) {
		this.listener = OKClickListener;
		show();
	}

	// 接口
	public interface OnClickButtonListener {
		void clickOKButton();

		void clickCancelButton();
	}
}
