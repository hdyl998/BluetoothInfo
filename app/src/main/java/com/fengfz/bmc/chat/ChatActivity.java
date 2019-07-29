package com.fengfz.bmc.chat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengfz.bmc.common.Consts;
import com.hdyl.bttech.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//聊天界面
public class ChatActivity extends Activity {

	private ListView mListView;
	private Button sendButton;
	private Button disconnectButton;
	private EditText editMsgView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	Context mContext;

	private BluetoothServerSocket mserverSocket = null;
	private ServerThread startServerThread = null;
	private ClientThread clientConnectThread = null;
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private ReadThread mreadThread = null;;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private String macString;
	private boolean mIsServer;// 是否是服务器的标志

	TextView tvChatInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		mContext = this;
		init();
	}

	private void init() {

		tvChatInfo = (TextView) findViewById(R.id.tv_chat_info);
		findViewById(R.id.ll_title_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		((TextView) findViewById(R.id.tv_title_back)).setText("蓝牙聊天程序");
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			mIsServer = true;
		} else {
			mIsServer = false;
			macString = bundle.getString("mac");
		}
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setFastScrollEnabled(true);
		editMsgView = (EditText) findViewById(R.id.MessageText);
		editMsgView.clearFocus();

		sendButton = (Button) findViewById(R.id.btn_msg_send);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				String msgText = editMsgView.getText().toString();
				if (msgText.length() > 0) {
					sendMessageHandle(msgText);
					editMsgView.setText("");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);
				} else
					Toast.makeText(mContext, "发送内容不能为空！", Toast.LENGTH_SHORT).show();
			}
		});

		disconnectButton = (Button) findViewById(R.id.btn_disconnect);
		disconnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mIsServer == false) {
					shutdownClient();
				} else {
					shutdownServer();
				}
				Toast.makeText(mContext, "已断开连接！", Toast.LENGTH_SHORT).show();
			}
		});

		if (mIsServer == false) {
			device = mBluetoothAdapter.getRemoteDevice(macString);
			clientConnectThread = new ClientThread();
			clientConnectThread.start();
		} else {
			startServerThread = new ServerThread();
			startServerThread.start();
		}

	}

	// 开启客户端
	private class ClientThread extends Thread {
		@Override
		public void run() {
			try {
				// 创建一个Socket连接：只需要服务器在注册时的UUID号
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Consts.UUID));
				// 连接
				Message msg2 = LinkDetectedHandler.obtainMessage();
				msg2.obj = "请稍候，正在连接服务器:" + macString;
				msg2.what = 0;
				LinkDetectedHandler.sendMessage(msg2);

				socket.connect();

				Message msg = LinkDetectedHandler.obtainMessage();
				msg.obj = "已经连接上服务端！可以发送信息。";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
				// 启动接受数据
				mreadThread = new ReadThread();
				mreadThread.start();
			} catch (IOException e) {
				Log.e("connect", "", e);
				Message msg = LinkDetectedHandler.obtainMessage();
				msg.obj = "连接服务端异常！断开连接重新试一试。";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
			}
		}
	};

	// 开启服务器
	private class ServerThread extends Thread {
		@Override
		public void run() {

			try {
				/*
				 * 创建一个蓝牙服务器 参数分别：服务器名称、UUID
				 */
				mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(Consts.PROTOCOL_SCHEME_RFCOMM, UUID.fromString(Consts.UUID));

				Log.d("server", "wait cilent connect...");

				Message msg = LinkDetectedHandler.obtainMessage();
				msg.obj = "请稍候，正在等待客户端的连接...";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);

				/* 接受客户端的连接请求 */
				socket = mserverSocket.accept();
				Log.d("server", "accept success !");

				Message msg2 = LinkDetectedHandler.obtainMessage();
				msg2.obj = "客户端已经连接上！可以发送信息。";
				msg2.what = 0;
				LinkDetectedHandler.sendMessage(msg2);
				// 启动接受数据
				mreadThread = new ReadThread();
				mreadThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private Handler LinkDetectedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case 0:
					tvChatInfo.setText((String) msg.obj);
					break;

				default:
					mDataArrays.add(new ChatMsgEntity("肖B", getDate(), (String) msg.obj, true));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mDataArrays.size() - 1);
					break;
			}
		}
	};

	/* 停止服务器 */
	private void shutdownServer() {
		new Thread() {
			@Override
			public void run() {
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
			};
		}.start();
	}

	/* 停止客户端连接 */
	private void shutdownClient() {
		new Thread() {
			@Override
			public void run() {
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket = null;
				}
			};
		}.start();
	}

	// 发送数据
	private void sendMessageHandle(String msg) {
		if (socket == null) {
			Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			OutputStream os = socket.getOutputStream();
			os.write(msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		mDataArrays.add(new ChatMsgEntity("必败", getDate(), msg, false));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mDataArrays.size() - 1);
	}

	// 读取数据
	private class ReadThread extends Thread {
		@Override
		public void run() {

			byte[] buffer = new byte[1024];
			int bytes;
			InputStream mmInStream = null;

			try {
				mmInStream = socket.getInputStream();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while (true) {
				try {
					// Read from the InputStream
					if ((bytes = mmInStream.read(buffer)) > 0) {
						String s = new String(buffer, 0, bytes);
						Message msg = LinkDetectedHandler.obtainMessage();
						msg.obj = s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);
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

	@Override
	protected void onDestroy() {
		if (mIsServer == false) {
			shutdownClient();
		} else {
			shutdownServer();
		}
		super.onDestroy();
	}

	/**
	 * 发送消息时，获取当前事件
	 *
	 * @return 当前时间
	 */
	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(new Date());
	}
}