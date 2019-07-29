package com.fengfz.bmc.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.hdyl.bttech.R;
import com.fengfz.bmc.common.BaseActivity;
import com.fengfz.bmc.common.Consts;
import com.fengfz.bmc.common.MyDialog;
import com.fengfz.bmc.common.StringAdapter;
import com.fengfz.bmc.utls.Utls;
import com.fengfz.bmc.view.MyProgressView;

public class ServerActivity extends BaseActivity implements OnClickListener {

	TextView tvInfo;// 信息
	private ReadThread mreadThread = null;
	private RandomThread mRandomThread = null;// 随机改变数字的线程
	private ServerThread startServerThread = null;
	private BluetoothSocket socket = null;
	private BluetoothServerSocket mserverSocket = null;

	private int updateTime = 3000;// 每5秒更新一次
	MathionStatus status = new MathionStatus();

	private StringAdapter mAdapter;
	private List<String> msgList = new ArrayList<String>();

	ListView listView;
	View viewList;// 列表

	TextView tvJi, tvLight;// 开关机，开关灯

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.btn_c_open:
				status.setOpenJi(true);
				status.updateUI();
				break;
			case R.id.btn_c_open_light:
				status.setOpenLight(true);
				status.updateUI();
				break;
			case R.id.btn_c_close:
				status.setOpenJi(false);
				status.updateUI();
				break;
			case R.id.btn_c_show:
				if (viewList.getVisibility() == View.GONE)
					viewList.setVisibility(View.VISIBLE);
				else
					viewList.setVisibility(View.GONE);
				break;
			case R.id.btn_c_close_light:
				status.setOpenLight(false);
				status.updateUI();
				break;
			case R.id.iv_c_clear:
				msgList.clear();
				mAdapter.notifyDataSetChanged();
				Utls.showToast(getApplicationContext(), "已清空消息");
				break;
			case R.id.iv_c_help:
				MyDialog dlgDialog = new MyDialog(this, R.string.help_title, R.string.help_2);
				dlgDialog.showInfoDialog();
				break;
			case R.id.ll_title_back:
				finish();
				break;
			case R.id.btn_c_send:// 修改服务器的值
				status.maxA = pbA.getProgress() + baseA;
				status.maxS = pbSudu.getProgress() + baseSudu;
				status.maxV = pbV.getProgress() + baseV;
				status.mRate = pbRate.getProgress() + baseRate;
				Utls.showToast(getApplicationContext(), "设置成功");
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

		// 设置最大值
		mViewA.setMaxProgress(20);
		mViewSudu.setMaxProgress(42);
		mViewV.setMaxProgress(46);

		tvViewA = (TextView) findViewById(R.id.iv_s_a);
		tvViewV = (TextView) findViewById(R.id.iv_s_v);
		tvViewSudu = (TextView) findViewById(R.id.iv_s_s);

		TextView view = (TextView) findViewById(R.id.btn_c_send);
		view.setOnClickListener(this);// 设置服务器参数
		view.setText("设置服务器参数");
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
		((TextView) findViewById(R.id.tv_title_back)).setText(R.string.main_title_server);
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
		mAdapter = new StringAdapter(this, msgList);
		listView.setAdapter(mAdapter);
		status.updateUI2();
		startServerThread = new ServerThread();
		startServerThread.start();
		mRandomThread = new RandomThread();
		mRandomThread.start();
	}

	@Override
	protected void setListener() {

	}

	/* 停止服务器 */
	private void shutdownServer() {
		if (mRandomThread != null) {
			mRandomThread.isStop = true;
			mRandomThread.interrupt();
			mRandomThread = null;
		}
		if (startServerThread != null) {
			startServerThread.interrupt();
			startServerThread = null;
		}
		if (mreadThread != null) {
			mreadThread.interrupt();
			mreadThread = null;
		}
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
			if (mserverSocket != null) {
				mserverSocket.close();/* 关闭服务器 */
				mserverSocket = null;
			}
		} catch (IOException e) {
			Log.e("server", "mserverSocket.close()", e);
		}
	}

	@Override
	protected void onDestroy() {
		shutdownServer();// 关闭服务器
		super.onDestroy();
	}

	// 模拟服务端的电流，电压，速度变化的线程
	class RandomThread extends Thread {
		public boolean isStop;// 停止标志

		@Override
		public void run() {
			isStop = false;
			while (!isStop) {
				try {
					Thread.sleep(updateTime);// 每1.5秒更新一次
					handler.sendEmptyMessage(2);
				} catch (Exception e) {
				}
			}

		}
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
	// 服务器线程
	class ServerThread extends Thread {
		@Override
		public void run() {

			try {
				/*
				 * 创建一个蓝牙服务器 参数分别：服务器名称、UUID
				 */
				mserverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(Consts.PROTOCOL_SCHEME_RFCOMM, UUID.fromString(Consts.UUID));
				setTextInfo("请稍候，正在等待客户端的连接...");
				/* 接受客户端的连接请求 */
				socket = mserverSocket.accept();// 阻塞线程
				Utls.printLog("accept success !");

				setTextInfo("客户端已经连接上！可以发送信息。");

				// 启动接受数据
				mreadThread = new ReadThread();
				mreadThread.start();
			} catch (IOException e) {
				Utls.printLog(e.toString());
				e.printStackTrace();
			}
		}

	}

	// 设置文本信息
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
					tvInfo.setText((String) msg.obj);
					break;
				case 1:
					byte[] by = (byte[]) msg.obj;
					msgList.add(Utls.bytes2HexString(by));
					mAdapter.notifyDataSetChanged();
					listView.setSelection(msgList.size() - 1);
					checkDataFromClient(by);// 检查来自客户端的数据
					break;
				case 2:
					status.updateUI2();// 更新UI
					break;
			}

		}

	};

	// 检查从客户端收到的数据,长度为12位
	private void checkDataFromClient(byte[] b) {
		if (b.length == 12) {
			int sum = 0;// 总和
			for (int i = 1; i < 8; i++) {
				sum += b[i];
			}
			byte l = (byte) (sum & 0xFF);// sumL
			byte h = (byte) ((sum >> 8) & 0xFF);// sumH
			if (h == b[9] && l == b[8]) {// 校验成功
				switch (b[2]) {
					case 0x47:// 命令

						switch (b[4]) {
							case 0x01:// 开机
								status.setOpenJi(true);
								Utls.showToast(this, "收到开机指令");
								break;
							case 0x02:// 关机
								status.setOpenJi(false);
								Utls.showToast(this, "收到关机指令");
								break;
							case 0x03:// 开灯
								status.setOpenLight(true);
								Utls.showToast(this, "收到开灯指令");
								break;
							case 0x04:// 关灯
								status.setOpenLight(false);
								Utls.showToast(this, "收到关灯指令");
								break;
						}
						status.updateUI();// 更新界面
						break;
					case 0x48:// 请求回传
						sendMsgBack();
						break;
					case 0x49:// 配置参数
						status.maxS = b[4] + baseSudu;// 最大速度
						status.maxA = b[5] + baseA;// ;CalcTool.byte2A(b[5]);// 最大电流
						status.maxV = b[6] + baseV;// CalcTool.byte2V(b[6]);// 最大电压
						status.mRate = b[7] + baseRate;

						pbA.setProgress(b[5]);
						pbV.setProgress(b[6]);
						pbSudu.setProgress(b[4]);
						pbRate.setProgress(b[7]);

						tvA.setText(status.maxA + "");
						tvV.setText(status.maxV + "");
						tvSudu.setText(status.maxS + "");
						tvRate.setText(status.mRate + "");

						Utls.showToast(this, "收到配置参数指令：最大速度为：" + status.maxS + "电流为：" + status.maxA + "电压为：" + status.maxV + "助力值为：" + status.mRate);
						break;
					default:

						Utls.showToast(this, "指令未定义");
						break;
				}

			} else {
				Utls.showToast(this, "数据校验失败");
			}
		} else
			Utls.showToast(this, "收到客户端给的数据长度不正确");
	}

	// 回传数据 ，这里的数据因为是测试数据所有自定义了协议
	private void sendMsgBack() {
		byte b[] = new byte[12];
		b[0] = 0x3A;
		b[1] = 0x1A;
		b[2] = 0x48;// 回传参数
		if (status.isOpenJi) {// 开机
			b[3] = 0x01;
		} else {
			b[3] = 0x02;
		}
		if (status.isOpenLight) {// 开灯
			b[4] = 0x01;
		} else {
			b[4] = 0x02;
		}
		b[5] = (byte) status.mA;// 当前电流
		b[6] = (byte) status.mV;// 当前电压
		b[7] = (byte) status.mS;// 当前速度

		int sum = 0;
		// 此处的累加校验和是从第2个字节（0x1A）到第7个字节（数据字节4）的累加。
		for (int i = 1; i <= 7; i++) {
			sum += b[i];
		}
		b[8] = (byte) (sum & 0xFF);// sumL
		b[9] = (byte) ((sum >> 8) & 0xFF);// sumH

		b[10] = 0x0D;// 用0x0D和0x0A作为一个数据帧的结束。
		b[11] = 0x0A;
		sendMessageHandle(b);
	}

	// 发送数据
	private void sendMessageHandle(byte[] msg) {
		if (socket == null) {
			Utls.showToast(getApplicationContext(), "没有连接");
			return;
		}
		try {
			OutputStream os = socket.getOutputStream();
			os.write(msg);
		} catch (IOException e) {
			Utls.showToast(getApplicationContext(), "发送异常" + e.toString());
		}
	}

	// 状态类
	class MathionStatus {

		boolean isOpenJi;// 是否开机
		boolean isOpenLight;// 是否开灯
		// 当前值
		int mV = 10;// 电压
		int mA = 10;// 电流
		int mS = 10;// 速度

		int maxV = 46;// 最大电压
		int maxA = 20;// 最大电流
		int maxS = 42;// 最大速度
		int mRate = 1;// 助力比值

		public void updateUI() {
			if (isOpenJi) {
				tvJi.setText("开机");
			} else
				tvJi.setText("关机");
			if (isOpenLight) {
				tvLight.setText("开灯");
			} else
				tvLight.setText("关灯");
		}

		// 服务端模拟电压随机值并更新UI
		public void updateUI2() {
			Random random = new Random();// 随机电压、电流速度
			mA = random.nextInt(maxA);
			mV = random.nextInt(maxV);
			mS = random.nextInt(maxS);

			mViewA.setProgress(mA);
			mViewV.setProgress(mV);
			mViewSudu.setProgress(mS);

			tvViewA.setText(mA + "");
			tvViewV.setText(mV + "");
			tvViewSudu.setText(mS + "");
		}

		public boolean isOpenJi() {
			return isOpenJi;
		}

		public void setOpenJi(boolean isOpenJi) {
			this.isOpenJi = isOpenJi;
		}

		public boolean isOpenLight() {
			return isOpenLight;
		}

		public void setOpenLight(boolean isOpenLight) {
			this.isOpenLight = isOpenLight;
		}

	}
}
