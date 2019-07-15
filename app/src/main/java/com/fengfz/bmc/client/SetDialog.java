package com.fengfz.bmc.client;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hdyl.bluetoothinfo.R;

//设置对话框
public class SetDialog extends Dialog implements android.view.View.OnClickListener {
	IListener listener;
	TextView tvProgress;
	SeekBar sbBar;

	public SetDialog(Context context, final int progress) {// 内容标题
		super(context, R.style.dialog);// 样式
		this.setContentView(R.layout.dialog_set);// 布局

		findViewById(R.id.iv_dialog_close).setOnClickListener(this);
		findViewById(R.id.btn_dialog_save).setOnClickListener(this);
		tvProgress = (TextView) findViewById(R.id.tv_dialog_time);
		sbBar = (SeekBar) findViewById(R.id.sb_dialog);
		tvProgress.setText(progress + "");
		sbBar.setProgress(progress - 300);
		sbBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				tvProgress.setText((arg1 + 300) + "");

			}
		});
	}

	// 设置事件
	public void setListener(IListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.btn_dialog_save:
				if (listener != null)
					listener.clickOK(sbBar.getProgress() + 300);// 最小请求周期为300
				break;
			case R.id.iv_dialog_close:

				break;
		}
		dismiss();

	}

	public interface IListener {
		void clickOK(int a);
	}
}
