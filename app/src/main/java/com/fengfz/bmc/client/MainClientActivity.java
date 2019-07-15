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
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hdyl.bluetoothinfo.R;
import com.fengfz.bmc.client.SetDialog.IListener;
import com.fengfz.bmc.common.BaseActivity;
import com.fengfz.bmc.common.Consts;
import com.fengfz.bmc.common.MyDialog;
import com.fengfz.bmc.common.StringAdapter;
import com.fengfz.bmc.utls.CalcTool;
import com.fengfz.bmc.utls.SPUtils;
import com.fengfz.bmc.utls.Utls;
import com.fengfz.bmc.view.MyProgressView;

//客户端
@SuppressLint("HandlerLeak")
public class MainClientActivity extends BaseActivity implements OnClickListener {

	TextView tvInfo;
	String mMac;
	private ReadThread mreadThread = null;;
	private ClientThread clientConnectThread = null;
	private GetDataThread getDataThread = null;

	private BluetoothSocket socket = null;
	private int mRoundtime = 1500;// 每1.5秒向控制器发送一个请求

	private OutputStream outputStream;
	private StringAdapter mAdapter;// 适配器
	private List<String> msgList = new ArrayList<String>();

	ListView listView;// 列表View
	View[] viewList = new View[3];// 三个列表
	ImageView ivBattery, ivWarm;// 电池
	AnimationDrawable adWarm;

	Status status;
	TextView tvStatus, tvSpeedRound, tvSpeend, tvW, tvError1, tvError2;// 状态，每秒转速，速度，功率，错误一，错误二
	TextView tvSpeedDanwei;// 速度单位
	boolean mIsRemind = false;// 是否正在报警

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.btn_c_open:
				Utls.printLog("开机");
				controlOrder(0x01);
				break;
			case R.id.btn_c_open_light:
				Utls.printLog("开灯");
				controlOrder(0x03);
				break;
			case R.id.btn_c_close:
				Utls.printLog("关机");
				controlOrder(0x02);
				break;
			case R.id.btn_c_show:
				break;
			case R.id.btn_c_close_light:
				Utls.printLog("关灯");
				controlOrder(0x04);
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
			case R.id.ll_title_back:// 结束
				finish();
				break;
			case R.id.btn_c_send:// 发送指令
				Utls.printLog("设置参数");
				requestOthersTrue();//
				break;
			case R.id.iv_m_setting:// 设置请求周期
				SetDialog dialog = new SetDialog(this, mRoundtime);
				dialog.setListener(new IListener() {

					@Override
					public void clickOK(int a) {
						mRoundtime = a;
						SPUtils.put(MainClientActivity.this, "time", mRoundtime);// 保存数据
					}
				});
				dialog.show();
				break;
			case R.id.btn_check0:// 检查0
				check(0x05);
				Utls.showToast(this, "请转动手把");
				break;
			case R.id.btn_check1:// 检查1
				check(0x06);
				Utls.showToast(this, "请踩脚蹬");
				break;
			case R.id.btn_check2:// 检查2
				check(0x07);
				Utls.showToast(this, "请捏刹车");
				break;
			case R.id.btn_check3:// 退出检查
				check(0x08);
				Utls.showToast(this, "退出检测");
				break;
		}
	}

	SeekBar pbSudu, pbA, pbV, pbRate;
	TextView tvSudu, tvA, tvV, tvRate;
	int baseSudu = 10, baseA = 5, baseV = 22, baseRate = 1;// 偏移量，设置数据里的偏移量
	MyProgressView mViewA, mViewV, mViewSudu;// 电流，电压，速度条状图
	TextView tvViewA, tvViewV, tvViewSudu;// 电流，电压，速度值

	@Override
	protected void findView() {
		// 设置布局文件
		setContentView(R.layout.activity_main_client_true);
		// 查找ViewID
		pbSudu = (SeekBar) findViewById(R.id.seekBar1_shudu);
		pbA = (SeekBar) findViewById(R.id.seekBar1_xianliu);
		pbV = (SeekBar) findViewById(R.id.seekBar1_dianya);
		pbRate = (SeekBar) findViewById(R.id.seekBar1_bizhi);
		// 设置最大值
		pbSudu.setMax(32);
		pbA.setMax(15);
		pbV.setMax(24);
		pbRate.setMax(4);
		// 设置滑动进度条移动时的事件
		pbRate.setOnSeekBarChangeListener(listenerDrag);
		pbSudu.setOnSeekBarChangeListener(listenerDrag);
		pbV.setOnSeekBarChangeListener(listenerDrag);
		pbA.setOnSeekBarChangeListener(listenerDrag);
		// 查找ViewID
		tvSudu = (TextView) findViewById(R.id.tv_c_sudu);
		tvA = (TextView) findViewById(R.id.tv_c_xianliu);
		tvV = (TextView) findViewById(R.id.tv_c_dianya);
		tvRate = (TextView) findViewById(R.id.tv_c_bizhi);

		tvSudu.setText(baseSudu + "");
		tvA.setText(baseA + "");
		tvV.setText(baseV + "");
		tvRate.setText(baseRate + "");
		// 接收消息列表
		listView = (ListView) findViewById(R.id.lv_receive);

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
		findViewById(R.id.ll_check).setVisibility(View.VISIBLE);
		View viewSet = findViewById(R.id.iv_m_setting);
		viewSet.setVisibility(View.VISIBLE);
		viewSet.setOnClickListener(this);
		findViewById(R.id.btn_c_send).setOnClickListener(this);// 发送指令
		findViewById(R.id.btn_c_open).setOnClickListener(this);// 开机
		findViewById(R.id.btn_c_open_light).setOnClickListener(this);// 开灯
		findViewById(R.id.btn_c_close).setOnClickListener(this);// 关机
		findViewById(R.id.btn_c_close_light).setOnClickListener(this);// 关灯
		findViewById(R.id.iv_c_clear).setOnClickListener(this);// 清空
		findViewById(R.id.iv_c_help).setOnClickListener(this);// 帮助
		findViewById(R.id.btn_check0).setOnClickListener(this);// 检查1
		findViewById(R.id.btn_check1).setOnClickListener(this);// 检查2
		findViewById(R.id.btn_check2).setOnClickListener(this);// 检查3
		findViewById(R.id.btn_check3).setOnClickListener(this);// 退出检查
		ivWarm = (ImageView) findViewById(R.id.iv_m_warm);
		adWarm = (AnimationDrawable) ivWarm.getBackground();// 得到背景
		viewList[0] = findViewById(R.id.info_show);// 显示列表1
		viewList[1] = findViewById(R.id.list_show);// 显示列表2
		viewList[2] = findViewById(R.id.item_control);// 显示列表3
		tvInfo = (TextView) findViewById(R.id.tv_main_info);
		findViewById(R.id.ll_title_back).setOnClickListener(this);// 回退
		((TextView) findViewById(R.id.tv_title_back)).setText(R.string.main_title_client2);
		ivBattery = (ImageView) findViewById(R.id.iv_m_battery);// 电池电量的

		tvStatus = (TextView) findViewById(R.id.iv_m_status);
		tvSpeedRound = (TextView) findViewById(R.id.iv_m_rv);
		tvW = (TextView) findViewById(R.id.iv_m_w_man);
		tvError1 = (TextView) findViewById(R.id.iv_m_error1);
		tvError2 = (TextView) findViewById(R.id.iv_m_error2);

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
		mRoundtime = (Integer) SPUtils.get(this, "time", 1500);

		Bundle bundle = getIntent().getExtras();
		mMac = bundle.getString("mac");// 得到MAC 地址
		mAdapter = new StringAdapter(this, msgList);
		listView.setAdapter(mAdapter);
		clientConnectThread = new ClientThread();
		clientConnectThread.start();

		// //////////////////这里是处理界面底部菜单
		int arr[] = { R.id.radio_control, R.id.radio_data, R.id.radio_show };
		int width = Utls.dp2px(this, 25);
		Rect rect = new Rect(0, 0, width, width);
		for (int j = 0; j < arr.length; j++) {
			RadioButton view = (RadioButton) findViewById(arr[j]);
			Drawable[] drawables = view.getCompoundDrawables();
			for (int i = 0; i < 4; i++) {
				if (drawables[i] != null)
					drawables[i].setBounds(rect);
			}
			view.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
		}
		status = new Status();
		status.UpdateUI();
	}

	@Override
	protected void setListener() {
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				int index = 0;
				switch (arg1) {
					case R.id.radio_show:
						index = 0;
						break;
					case R.id.radio_data:
						index = 1;
						break;
					case R.id.radio_control:
						index = 2;
						break;
				}
				for (int i = 0; i < viewList.length; i++) {
					if (index == i) {
						viewList[i].setVisibility(View.VISIBLE);
					} else {
						viewList[i].setVisibility(View.GONE);
					}
				}
			}
		});
	}

	/* 停止客户端连接 */
	private void shutdownClient() {

		if (getDataThread != null) {
			getDataThread.isStop = true;
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
		if (outputStream != null)
			try {
				outputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
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
			String startFlag=new String(new byte[]{0x3A,0x1A},0,2);
			String endFlag=new String(new byte[]{0x0D,0x0A},0,2);
			String tempString="";
			while (true) {
				try {
					// Read from the InputStream
					if ((bytes = mmInStream.read(buffer)) > 0) {

						String string = new String(buffer, 0, bytes);
						Utls.printLog(Utls.bytes2HexString(string.getBytes()));
						tempString+=string;

						int indexStart = tempString.indexOf(startFlag);
						if (indexStart != -1) {
							tempString = tempString.substring(indexStart);
							int indexEnd = tempString.indexOf(endFlag);
							if (indexEnd > -1) {
								indexEnd += 2;
								Message msg = handler.obtainMessage();// 获取消息队列中的msg
								msg.what = 1;// 消息编号为1
								msg.obj = tempString.substring(0, indexEnd).getBytes();
								handler.sendMessage(msg);
								tempString = tempString.substring(indexEnd);
							}
						}
					}
				} catch (IOException e) {
					Utls.printLog(e.toString());
					try {
						mmInStream.close(); // 把流关闭后释放资源
					} catch (IOException e1) {
						e1.printStackTrace();
						Utls.printLog(e1.toString());
					}
					String msg = "连接已经断开";
					Utls.showToast(getApplicationContext(), msg);
					setTextInfo(msg);
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

				outputStream = socket.getOutputStream();
				setTextInfo("已经连接上服务端！可以发送信息。");
				// 启动接受数据
				mreadThread = new ReadThread();
				mreadThread.start();
				getDataThread = new GetDataThread();// 启动获得数据线程
				getDataThread.start();
			} catch (IOException e) {
				setTextInfo("连接服务端异常！请打开服务端或重新连接试一试。");
			}
		}
	};

	// 每一秒钟获取数据线程
	private class GetDataThread extends Thread {
		public boolean isStop = false;

		@Override
		public void run() {
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
					Utls.printLog("收到数据：" + Utls.bytes2HexString(by));
					msgList.add(Utls.bytes2HexString(by));// 收到的数组中加一个数据
					mAdapter.notifyDataSetChanged();// 通知数据改变
					listView.setSelection(msgList.size() - 1);// 设置选中最后一个
					checkDataFromServerTrue(by);// 检查来自服务端的数据实机协议
					break;
				case 2:
					requestGet();// 请求获得数据
					break;
				case 3:
					ivWarm.setVisibility(View.VISIBLE);
					adWarm.start();// 启动动画
					break;
				case 4:
					ivWarm.setVisibility(View.GONE);
					adWarm.stop();// 关闭动画
					break;
			}

		}
	};

	// 客户端发送数据
	private void   sendMessageHandle(byte[] bytes) {
		Utls.printLog("发送数据" + Utls.bytes2HexString(bytes));
		if (outputStream == null) {
			Utls.showToast(getApplicationContext(), "没有连接");
			return;
		}
		try {
			outputStream.write(bytes);
			outputStream.flush();

		} catch (IOException e) {
			if (getDataThread != null)
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
		Utls.printLog("请求回传");
		sendMessageHandle(getBytes(fun, null));
	}

	// /////////////////////控制命令///////////////////////////
	// 开机,关机，开灯，关灯 分别对应 0x01,0x02,0x03,0x04
	private void controlOrder(int b) {
		byte fun = 0x47;
		sendMessageHandle(getBytes(fun, new byte[] { (byte) b }));
	}

	// ////////////////////////////////检查命令/////////////////////////////
	// 手把检测,扭矩检测,刹车检测,退出检测 分别对应 0x05,0x06,0x07,0x08
	private void check(int b) {
		byte fun = 0x47;
		sendMessageHandle(getBytes(fun, new byte[] { (byte) b }));
	}

	// ////////////////////////////////////////参数配置///////////////////////

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

	// 分析从服务器中得到的数据
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

					Status laStatus = status;
					status = new Status();
					status.lastStatus = laStatus;// 保存上一个索引，判定是否更新界面
					// 状态量
					status.mA = b[16] * 0.36735f;// 电机电流净值 I_pj
					status.mV = b[7] / (51 * 0.0745545f);// dyv_s 电池电压值
					double r = 1000.0 / (b[14] * 256 + b[13]) * 60;// 转/分
					status.mRS = (float) r;// 转速度，转每分
					status.mS = (float) (r * 0.508 * 3.1415926);// 单位，米每秒
					// 故障码
					status.error1 = b[17];
					status.error2 = b[18];

					// ////////////行驶状态/////////
					// if (b[11] == 0)// 电机运行标志 run run=0——滑行；run=1——驱动
					// status.runStatus = 0;// 滑行
					// else if (b[11] == 1) {
					// if (b[12] == 0)// run=1时：njwg=1助力；njwg=0电动
					// status.runStatus = 1;// 电动
					// else if (b[12] == 1)//
					// status.runStatus =2;// 助力
					// }
					// 以上注释掉的语句其实 就是这样
					status.runStatus = (byte) (b[11] + b[12]);
					double n = 0;
					if (b[8] >= 47)// 当小于47时人力做功为0，不可能为负
						n = (b[8] - 46) * 0.06392;// 人力扭矩值
					status.workWart = n * r;// 人力做功
					status.UpdateUI();

				} else
					Utls.showToast(this, "指令未定义");

			} else {
				Utls.showToast(this, "数据校验失败");
			}
		} else
			Utls.showToast(this, "收到服务端给的数据长度不正确");

	}

	// 分析从服务器中得到的数据,模拟客户端

	class Status {
		float mS;// 当前速度
		float mV;// 当前电压
		float mA;// 当前电流

		float mRS;// 转速
		double workWart;// 功率

		byte error1 = -1;// 故障标志位1
		byte error2 = -1;// 故障标志位2

		byte battery = 5;// 电池电量 值为1-5

		byte runStatus = -1;// 运行状态
		Status lastStatus = null;// 上一个状态

		// 更新界面UI
		public void UpdateUI() {
			if (lastStatus == null) {
				mViewA.setProgress((int) mA);
				tvViewA.setText(mA + "");
				mViewV.setProgress((int) mV);
				tvViewV.setText(mV + "");
				mViewSudu.setProgress((int) mS);
				tvViewSudu.setText(mS + "");
				ivBattery.setImageBitmap(Utls.getBatteryBitmap(getApplication(), 5 - battery));// 电量
			} else {
				if (lastStatus.mA != this.mA) {
					mViewA.setProgress((int) mA);
					tvViewA.setText(mA + "");
				}
				if (lastStatus.mV != this.mV) {
					mViewV.setProgress((int) mV);
					tvViewV.setText(mV + "");
				}
				if (lastStatus.mS != this.mS) {
					mViewSudu.setProgress((int) mS);
					tvViewSudu.setText(mS + "");
				}
				if (lastStatus.battery != this.battery)
					ivBattery.setImageBitmap(Utls.getBatteryBitmap(getApplication(), 5 - battery));// 电量
			}
			if (runStatus >= 0 && runStatus < 3) {
				tvStatus.setText(Consts.status[runStatus]);// 状态
			}
			tvSpeedRound.setText(mRS + "");// 转速度
			tvW.setText(workWart + "");// 功率
			if (error1 >= 0 && error1 < Consts.error1.length) {// 取值0.1.2.3.4.5.6.7
				tvError1.setText(Consts.error1[error1]);
				if (mIsRemind == false) {// 没有正在报警
					mIsRemind = true;
					handler.sendEmptyMessage(3);// 提示需要报警
				}
			} else {
				tvError1.setText("-");
				if (mIsRemind == true) {// 正在报警
					mIsRemind = false;
					handler.sendEmptyMessage(4);
				}
			}
			if (error2 >= 3 && error2 <= 7) {// 取值3.4.5.6.7
				tvError2.setText(Consts.error2[error2 - 3]);
				if (mIsRemind == false) {// 没有正在报警
					mIsRemind = true;
					handler.sendEmptyMessage(3);
				}
			} else {
				tvError2.setText("-");
				if (mIsRemind == true) {// 正在报警
					mIsRemind = false;
					handler.sendEmptyMessage(4);
				}
			}
		}

	}
}
