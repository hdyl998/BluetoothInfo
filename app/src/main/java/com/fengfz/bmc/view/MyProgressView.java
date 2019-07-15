package com.fengfz.bmc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
//自定义进度条，用于显示值
public class MyProgressView extends View {

	public MyProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public MyProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyProgressView(Context context) {
		super(context);
		init();
	}

	void init() {
		colors = new int[7];
		colors[0] = Color.parseColor("#063198");
		colors[1] = Color.parseColor("#0458a1");
		colors[2] = Color.parseColor("#03aab2");
		colors[3] = Color.parseColor("#1cb94b");
		colors[4] = Color.parseColor("#ddc65c");
		colors[5] = Color.parseColor("#dc4b2e");
		colors[6] = Color.parseColor("#d70218");
	}

	int colors[];
	Paint paint = new Paint();// 画刷
	int width, height;// 宽高
	int mMaxValue = 100;
	int mCurValue = 0;
	int colorBack = Color.parseColor("#99b3ec");

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w;
		height = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		float rate = mCurValue / (mMaxValue + 0.0f);
		float left = width * rate;

		int index = (int) (rate * colors.length);
		if (index == colors.length) {
			index = colors.length - 1;
		}
		paint.setColor(colorBack);
		canvas.drawRect(0, 0, width, height, paint);// 画背景
		paint.setColor(colors[index]);
		canvas.drawRect(0, 4, left, height-4, paint);
		super.onDraw(canvas);
	}
	public void setProgress(int curProgress) {
		if (curProgress > mMaxValue) {
			curProgress = mMaxValue;
		} else if (curProgress < 0) {
			curProgress = 0;
		}
		this.mCurValue = curProgress;
		this.invalidate();//重新绘制一次
	}

	public void setMaxProgress(int maxProgress) {
		if (maxProgress <= 0) {
			maxProgress = 100;
		}
		this.mMaxValue = maxProgress;
	}
}
