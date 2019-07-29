package com.fengfz.bmc.utls;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.hdyl.bttech.R;

//工具类
public class Utls {
	private static Toast toast;
	public static void showToast(Context context,String text){
		if(toast==null)
			toast=Toast.makeText(context.getApplicationContext(), text, 0);
		else {
			toast.setText(text);
		}
		toast.show();

	}

	// 获得当前时间
	@SuppressLint("SimpleDateFormat")
	public static String getNowDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String currentDateString = format.format(new Date());
		return currentDateString;
	}

	public static void printLog(String string) {
		boolean mSwitch = true;
		if (mSwitch)
			Log.e("my", string);
	}
	//byte数组转成16进制数据
	public static String bytes2HexString(byte[] b) {// 转16进制
		String str = "["+b.length+"] ";
		for (int i = 0; i < b.length; i++) {
			String string = String.format("%X", b[i]&0xff);
			if(string.length()==1){
				string="0"+string;
			}
			str += "0x" + string+" ";
		}
		return str;
	}
	// BYTE数组转成整数的STRING
	public static String byte2IntString(byte[] bys) {
		String string = "";
		for (int i = 0; i < bys.length; i++) {
			string += bys[i] + " ";
		}
		return string;
	}
	/**
	 * dp转px
	 *
	 * @param context
	 * @param val
	 * @return
	 */
	public static int dp2px(Context context, float dpVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, context.getResources().getDisplayMetrics());
	}
	// 得到电量的位图
	public static Bitmap getBatteryBitmap(Context context,int num) {//num的值是0-4
		Bitmap bitmapBattery=BitmapFactory.decodeResource(context.getResources(), R.drawable.battery);
		int width=bitmapBattery.getWidth()/5;
		int height=bitmapBattery.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 设置一个iconSize大小的块的位图
		Canvas canvas = new Canvas(bitmap);// 获取bitMap的画布
		canvas.drawBitmap(bitmapBattery, new Rect(width * num, 0, width * (num + 1),  height), new Rect(0, 0, width, height), null);
		return bitmap;
	}
}
