package com.fengfz.bmc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hdyl.bluetoothinfo.R;
import com.fengfz.bmc.common.BaseActivity;
import com.fengfz.bmc.common.Consts;
import com.fengfz.bmc.common.MyDialog;
import com.fengfz.bmc.common.StringAdapter;
import com.fengfz.bmc.utls.CalcTool;
import com.fengfz.bmc.utls.Utls;
import com.fengfz.bmc.view.MyProgressView;

//客户端
@SuppressLint("HandlerLeak")
public class ClientActivity extends BaseActivity implements OnClickListener {

	TextView tvInfo;
	String mMac;
	private ReadThread mreadThread = null;;
	private ClientThread clientConnectThread = null;
	private GetDataThread getDataThread = null;

	private BluetoothSocket socket = null;
	private OutputStream outputStream;
	private int mRoundtime = 1500;// 每1.5秒向控制器发送一个请求

	private StringAdapter mAdapter;
	private List<String> msgList = new ArrayList<String>();

	ListView listView;
	View viewList;// 列表

	TextView tvJi, tvLight;// 开关机，开关灯
	Status status = new Status();

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.btn_c_open:
				requestOpen();
				break;
			case R.id.btn_c_open_light:
				requestOpenLight();
				break;
			case R.id.btn_c_close:
				requestClose();
				break;
			case R.id.btn_c_show:
				if (viewList.getVisibility() == View.GONE)
					viewList.setVisibility(View.VISIBLE);
				else
					viewList.setVisibility(View.GONE);
				break;
			case R.id.btn_c_close_light:
				requestCloseLight();
				break;
			case R.id.iv_c_clear:
				msgList.clear();
				mAdapter.notifyDataSetChanged();
				Utls.showToast(getApplicationContext(), "已清空消息");
				break;
			case R.id.iv_c_help:
				MyDialog dlgDialog = new MyDialog(this, R.string.help_title, R.string.help1);
				dlgDialog.showInfoDialog();
				break;
			case R.id.ll_title_back:
				finish();
				break;
			case R.id.btn_c_send:// 发送指令
				requestOthers();// 模拟机指令
				break;
		}

	}

	SeekBar pbSudu, pbA, pbV, pbRate;
	TextView tvSudu, tvA, tvV, tvRate;
	int baseSudu = 10, baseA = 5, baseV = 22, baseRate = 1;// 偏移量，设置数据里的偏移量
	MyProgressView mViewA, mViewV, mViewSudu;
	TextView tvViewA, tvViewV, tvViewSudu;

	@Override
	protected void findView() {
		setContentView(R.layout.activity_main_client);
		pbSudu = (SeekBar) findViewById(R.id.seekBar1_shudu);
		pbA = (SeekBar) findViewById(R.id.seekBar1_xianliu);
		pbV = (SeekBar) findViewById(R.id.seekBar1_dianya);
		pbRate = (SeekBar) findViewById(R.id.seekBar1_bizhi);

		pbSudu.setMax(32);
		pbA.setMax(15);
		pbV.setMax(24);
		pbRate.setMax(4);
		pbRate.setOnSeekBarChangeListener(listenerDrag);
		pbSudu.setOnSeekBarChangeListener(listenerDrag);
		pbV.setOnSeekBarChangeListener(listenerDrag);
		pbA.setOnSeekBarChangeListener(listenerDrag);

		tvSudu = (TextView) findViewById(R.id.tv_c_sudu);
		tvA = (TextView) findViewById(R.id.tv_c_xianliu);
		tvV = (TextView) findViewById(R.id.tv_c_dianya);
		tvRate = (TextView) findViewById(R.id.tv_c_bizhi);

		tvSudu.setText(baseSudu + "");
		tvA.setText(baseA + "");
		tvV.setText(baseV + "");
		tvRate.setText(baseRate + "");

		listView = (ListView) findViewById(R.id.lv_receive);
		tvJi = (TextView) findViewById(R.id.iv_s_ji);
		tvLight = (TextView) findViewById(R.id.iv_s_light);

		mViewA = (MyProgressView) findViewById(R.id.myProgressView_a);
		mViewV = (MyProgressView) findViewById(R.id.myProgressView_v);
		mViewSudu = (MyProgressView) findViewById(R.id.myProgressView_s);

		mViewA.setMaxProgress(20);
		mViewSudu.setMaxProgress(42);
		mViewV.setMaxProgress(46);

		tvViewA = (TextView) findViewById(R.id.iv_s_a);
		tvViewV = (TextView) findViewById(R.id.iv_s_v);
		tvViewSudu = (TextView) findViewById(R.id.iv_s_s);

		findViewById(R.id.btn_c_send).setOnClickListener(this);// 发送指令
		findViewById(R.id.btn_c_open).setOnClickListener(this);// 开机
		findViewById(R.id.btn_c_open_light).setOnClickListener(this);// 开灯
		findViewById(R.id.btn_c_close).setOnClickListener(this);// 关机
		findViewById(R.id.btn_c_close_light).setOnClickListener(this);// 关灯
		findViewById(R.id.btn_c_show).setOnClickListener(this);// 显示
		findViewById(R.id.iv_c_clear).setOnClickListener(this);// 清空
		findViewById(R.id.iv_c_help).setOnClickListener(this);// 帮助
		viewList = findViewById(R.id.list_show);// 显示列表
		tvInfo = (TextView) findViewById(R.id.tv_main_info);
		findViewById(R.id.ll_title_back).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title_back)).setText(R.string.main_title_client);
	}

	// 进度条拖动
	private OnSeekBarChangeListener listenerDrag = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
			switch (arg0.getId()) {
				case R.id.seekBar1_shudu:
					tvSudu.setText((arg0.getProgress() + baseSudu) + "");
					break;

				case R.id.seekBar1_xianliu:
					tvA.setText((arg0.getProgress() + baseA) + "");
					break;

				case R.id.seekBar1_dianya:
					tvV.setText((arg0.getProgress() + baseV) + "");
					break;
				case R.id.seekBar1_bizhi:
					tvRate.setText((arg0.getProgress() + baseRate) + "");
					break;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
		}
	};

	@Override
	protected void initViewAndData() {
		Bundle bundle = getIntent().getExtras();
		mMac = bundle.getString("mac");// 得到MAC 地址
		// 实机协议
		mAdapter = new StringAdapter(this, msgList);
		listView.setAdapter(mAdapter);
		clientConnectThread = new ClientThread();
		clientConnectThread.start();
	}

	@Override
	protected void setListener() {

	}

	/* 停止客户端连接 */
	private void shutdownClient() {

		if (getDataThread != null) {
			getDataThread.isStop = true;
			getDataThread.interrupt();
			getDataThread = null;
		}
		if (clientConnectThread != null) {
			clientConnectThread.interrupt();
			clientConnectThread = null;
		}
		if (mreadThread != null) {
			mreadThread.interrupt();
			mreadThread = null;
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
	}

	@Override
	protected void onDestroy() {
		shutdownClient();// 关闭客户端
		super.onDestroy();
	}

	// ///////////////////////////////////////////////
	// 读取数据线程
	class ReadThread extends Thread {
		@Override
		public void run() {

			byte[] buffer = new byte[1024];
			int bytes;
			InputStream mmInStream = null;

			try {
				mmInStream = socket.getInputStream();// 得到输入流
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while (true) {
				try {
					// Read from the InputStream
					if ((bytes = mmInStream.read(buffer)) > 0) {
						String string = new String(buffer, 0, bytes);
						Message msg = handler.obtainMessage();// 获取消息队列中的msg
						msg.what = 1;// 消息编号为1
						msg.obj = string.getBytes();// 得到字节数组
						handler.sendMessage(msg);
					}
				} catch (IOException e) {
					try {
						mmInStream.close(); // 把流关闭后释放资源
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

	// ///////////////////////////////////////////////
	// 客户端线程
	private class ClientThread extends Thread {
		@Override
		public void run() {
			try {
				// 创建一个Socket连接：只需要服务器在注册时的UUID号
				BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mMac);// 通过MAC
				// 得到设备
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Consts.UUID));

				setTextInfo("请稍候，正在连接服务器:" + mMac);
				socket.connect(); // 连接（阻塞方法，使用这个方法程序没连接前不会向下执行，超时会出异常）
				outputStream=socket.getOutputStream();
				setTextInfo("已经连接上服务端！可以发送信息。");
				// 启动接受数据
				mreadThread = new ReadThread();
				mreadThread.start();
				getDataThread = new GetDataThread();// 启动获得数据线程
				getDataThread.start();
			} catch (IOException e) {
				Log.e("connect", "", e);
				setTextInfo("连接服务端异常！请打开服务器端或断开连接重新试一试。");
			}
		}
	};

	// 每一秒钟获取数据线程
	private class GetDataThread extends Thread {
		public boolean isStop = false;

		@Override
		public void run() {
			isStop = false;
			while (!isStop) {
				try {
					Thread.sleep(mRoundtime);
				} catch (InterruptedException e) {
				}
				handler.sendEmptyMessage(2);
			}
		}
	}

	private void setTextInfo(final String string) {
		Message msg = handler.obtainMessage();
		msg.obj = string;
		msg.what = 0;
		handler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					tvInfo.setText((String) msg.obj);// 设置标题信息
					break;
				case 1:
					byte[] by = (byte[]) msg.obj;
					msgList.add(Utls.bytes2HexString(by));// 收到的数组中加一个数据
					mAdapter.notifyDataSetChanged();// 通知数据改变
					listView.setSelection(msgList.size() - 1);// 设置选中最后一个
					checkDataFromServer(by);// 检查来自服务端的数据
					break;
				case 2:
					requestGet();// 请求获得数据
					break;
			}

		}
	};

	// 客户端发送数据
	private void sendMessageHandle(byte[] bytes) {
		if (outputStream == null) {
			Utls.showToast(getApplicationContext(), "没有连接");
			return;
		}
		try {
			outputStream.write(bytes);
			outputStream.flush();
		} catch (IOException e) {
			getDataThread.isStop = true;
			Utls.showToast(getApplicationContext(), "发送异常" + e.toString());
		}
	}

	// 得到发送指令的bytes数组
	/**
	 * funCode是功能参数 params是参数
	 **/
	private byte[] getBytes(byte funCode, byte[] params) {
		byte[] b = new byte[12];
		b[0] = 0x3A;// 连续的0x3A、0x1A字节代表一个数据帧的开始。
		b[1] = 0x1A;

		// 脚标为2、4、5、6、7之间的数字要改变
		b[2] = funCode;// 0x48 0x49 功能区
		b[3] = 0x04;// Num=04 表示一个数据帧中有四个数据字节（固定了）
		// 参数为空表示没参数
		if (params != null) {
			// 这里把参数全部赋值给数组字段
			if (params.length <= 4) {
				for (int i = 0; i < params.length; i++) {
					b[4 + i] = params[i];
					// b[4];// 限速值
					// b[5];// 限流值
					// b[6];// 停机电压
					// b[7];// 助力比值
				}
			} else {
				Utls.showToast(this, "构造参数长度不正确");
			}
		}
		// 此处的累加校验和是从第2个字节（0x1A）到第7个字节（数据字节4）的累加。
		int sum = 0;
		for (int i = 1; i <= 7; i++) {
			sum += b[i];
		}
		b[8] = (byte) (sum & 0xFF);// sumL 低位
		b[9] = (byte) ((sum >> 8) & 0xFF);// sumH 高位

		b[10] = 0x0D;// 用0x0D和0x0A作为一个数据帧的结束。
		b[11] = 0x0A;
		return b;
	}

	// //////////////////////////////请求回传（PC闲置时自动发送）//////////////////////
	// 请求回传（待机时)
	private void requestGet() {
		byte fun = 0x48;
		sendMessageHandle(getBytes(fun, null));
	}

	// /////////////////////控制命令///////////////////////////
	// 开机
	private void requestOpen() {
		byte fun = 0x47;
		sendMessageHandle(getBytes(fun, new byte[] { 0x01 }));
	}

	// 关机
	private void requestClose() {
		byte fun = 0x47;
		sendMessageHandle(getBytes(fun, new byte[] { 0x02 }));
	}

	// 开灯
	private void requestOpenLight() {
		byte fun = 0x47;
		sendMessageHandle(getBytes(fun, new byte[] { 0x03 }));
	}

	// 关灯
	private void requestCloseLight() {
		byte fun = 0x47;
		sendMessageHandle(getBytes(fun, new byte[] { 0x04 }));
	}

	// ////////////////////////////////////////参数配置///////////////////////
	// 请求其它数据
	private void requestOthers() {
		byte speed = (byte) pbSudu.getProgress();// progressbar 的值 是 0-32 (十进制)
		byte stream = (byte) pbA.getProgress();// CalcTool.A2Byte(pbA.getProgress()
		// + baseA);//安培转为字节
		byte v = (byte) pbV.getProgress(); // CalcTool.V2Byte(pbV.getProgress()
		// + baseV);//电压转成字节
		byte rate = (byte) pbRate.getProgress();// (byte) (pbRate.getProgress()
		// + baseRate);//比率转成字节

		byte fun = 0x49;// 控制器发送参数配置响应到pc机（或手机）的数据帧使用功能码0x49[原为0x48
		byte[] bytes = new byte[4];
		bytes[0] = speed;
		bytes[1] = stream;
		bytes[2] = v;
		bytes[3] = rate;
		sendMessageHandle(getBytes(fun, bytes));
	}

	// 请求其它数据,真机的协议
	private void requestOthersTrue() {
		byte speed = (byte) pbSudu.getProgress();// progressbar 的值 是 0-32 (十进制)
		// //speed=速度值（km/h）-10
		byte stream = (byte) CalcTool.A2Byte(pbA.getProgress() + baseA);// 安培转为字节
		byte v = (byte) CalcTool.V2Byte(pbV.getProgress() + baseV);// 电压转成字节
		byte rate = (byte) (pbRate.getProgress() + baseRate);// 助力比值转成字节

		byte fun = 0x49;// 控制器发送参数配置响应到pc机（或手机）的数据帧使用功能码0x49[原为0x48
		byte[] bytes = new byte[4];
		bytes[0] = speed;
		bytes[1] = stream;
		bytes[2] = v;
		bytes[3] = rate;
		sendMessageHandle(getBytes(fun, bytes));
	}

	// 分析从服务器中得到的数据,模拟真机端
	private void checkDataFromServerTrue(byte[] b) {
		if (b.length == 24) {
			int sum = 0;// 总和
			for (int i = 1; i < 21; i++) {
				sum += b[i];
			}
			byte l = (byte) (sum & 0xFF);// sumL
			byte h = (byte) ((sum >> 8) & 0xFF);// sumH
			if (h == b[21] && l == b[20]) {// 校验成功

				if (b[2] == 0x48) {
					// if (b[3] == 0x01) {
					// status.setJi(true);
					// } else
					// status.setJi(false);
					//
					// if (b[4] == 0x01)
					// status.setLight(true);
					// else
					// status.setLight(false);
					//
					// status.mA = b[5];
					// status.mV = b[6];
					// status.mS = b[7];
					// status.UpdateUI();

				} else
					Utls.showToast(this, "指令未定义");

			} else {
				Utls.showToast(this, "数据校验失败");
			}
		} else
			Utls.showToast(this, "收到服务端给的数据长度不正确");

	}

	// 分析从服务器中得到的数据,模拟客户端
	private void checkDataFromServer(byte[] b) {
		if (b.length == 12) {
			int sum = 0;// 总和
			for (int i = 1; i < 8; i++) {
				sum += b[i];
			}
			byte l = (byte) (sum & 0xFF);// sumL
			byte h = (byte) ((sum >> 8) & 0xFF);// sumH
			if (h == b[9] && l == b[8]) {// 校验成功

				if (b[2] == 0x48) {
					if (b[3] == 0x01) {
						status.setJi(true);
					} else
						status.setJi(false);

					if (b[4] == 0x01)
						status.setLight(true);
					else
						status.setLight(false);

					status.mA = b[5];
					status.mV = b[6];
					status.mS = b[7];
					status.UpdateUI();

				} else
					Utls.showToast(this, "指令未定义");

			} else {
				Utls.showToast(this, "数据校验失败");
			}
		} else
			Utls.showToast(this, "收到服务端给的数据长度不正确");
	}

	class Status {
		boolean isJi;// 是否开机
		boolean isLight;// 是否开灯
		int mS;// 当前速度
		int mV;// 当前电压
		int mA;// 当前电流

		// 更新界面UI
		public void UpdateUI() {
			if (isJi)
				tvJi.setText("开机");
			else
				tvJi.setText("关机");
			if (isLight)
				tvLight.setText("开灯");
			else
				tvLight.setText("关灯");
			mViewA.setProgress(mA);
			mViewV.setProgress(mV);
			mViewSudu.setProgress(mS);

			tvViewA.setText(mA + "");
			tvViewV.setText(mV + "");
			tvViewSudu.setText(mS + "");
		}

		public boolean isJi() {
			return isJi;
		}

		public void setJi(boolean isJi) {
			this.isJi = isJi;
		}

		public boolean isLight() {
			return isLight;
		}

		public void setLight(boolean isLight) {
			this.isLight = isLight;
		}

		public int getmS() {
			return mS;
		}

		public void setmS(int mS) {
			this.mS = mS;
		}

		public int getmV() {
			return mV;
		}

		public void setmV(int mV) {
			this.mV = mV;
		}

		public int getmA() {
			return mA;
		}

		public void setmA(int mA) {
			this.mA = mA;
		}

	}
}
