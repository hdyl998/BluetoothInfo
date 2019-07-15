package com.fengfz.bmc.finddevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.fengfz.bmc.about.AboutActivity;
import com.fengfz.bmc.chat.ChatActivity;
import com.fengfz.bmc.client.ClientActivity;
import com.fengfz.bmc.client.MainClientActivity;
import com.fengfz.bmc.common.BaseActivity;
import com.fengfz.bmc.server.ServerActivity;
import com.fengfz.bmc.utls.Utls;
import com.hdyl.bluetoothinfo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//查找设备ACTIVITY
public class FindDeviceActivity extends BaseActivity {

	private static final int REQUEST_ENABLE_BT = 1;

	private Button btnOn;
	private Button btnOff;
	private Button btnList;
	private Button btnFind;
	private Button btnStartServer, btnShow;// 启动服务器
	private View viewProgress;// 进度条VIEW,表示正在查找蓝牙设备

	private TextView text;
	private BluetoothAdapter myBluetoothAdapter;
	private Set<BluetoothDevice> pairedDevices;
	private ListView myListView;
	private DeviceListAdapter adapter;// 设备列表适配器
	private List<DeviceItem> listDeviceItems;// 设备列表数据
	private CheckBox checkBox;

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.btn_find_startserver:// 点击了开始服务器
				if (myBluetoothAdapter.isEnabled()) {
					Class<?> clazzClass = ServerActivity.class;
					if (checkBox.isChecked()) {
						clazzClass = ChatActivity.class;
						Utls.showToast(this, "启动聊天程序服务器");
					} else {
						Utls.showToast(this, "启动模拟服务器");
					}
					Intent intent = new Intent(this, clazzClass);
					startActivity(intent);
				} else {
					Utls.showToast(this, "请打开蓝牙后重试");
				}
				break;
			case R.id.btn_find_on:
				if (!myBluetoothAdapter.isEnabled()) {// 蓝牙没开启
					Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);// 请求蓝牙开启
					startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
					Utls.showToast(this, "打开蓝牙");
				} else {
					Utls.showToast(this, "蓝牙已经打开");
					text.setText(R.string.on);

				}
				break;
			case R.id.btn_find_off:
				setText2();
				myBluetoothAdapter.disable();
				text.setText(R.string.off);
				Utls.showToast(this, "关闭蓝牙");
				break;
			case R.id.btn_find_paired:

				if (myBluetoothAdapter.isEnabled()) {
					setText2();
					listDeviceItems.clear();// 清空列表
					pairedDevices = myBluetoothAdapter.getBondedDevices();
					for (BluetoothDevice device : pairedDevices)
						listDeviceItems.add(new DeviceItem(device.getName(), device.getAddress()));
					adapter.notifyDataSetChanged();// 通知数据改变了
					Utls.showToast(this, "显示已配对设备");
				} else {
					Utls.showToast(this, "请打开蓝牙后重试");
				}

				break;
			case R.id.btn_find_search:// 查找
				if (myBluetoothAdapter.isEnabled()) {
					if (myBluetoothAdapter.isDiscovering()) {
						myBluetoothAdapter.cancelDiscovery();
						viewProgress.setVisibility(View.INVISIBLE);
						btnFind.setText(R.string.find_findnew);
					} else {
						listDeviceItems.clear();
						adapter.notifyDataSetChanged();
						viewProgress.setVisibility(View.VISIBLE);
						btnFind.setText(R.string.find_cancel);
						myBluetoothAdapter.startDiscovery();
					}
				} else {
					Utls.showToast(this, "请打开蓝牙后重试");
				}

				break;
			case R.id.iv_main_about:// 关于
				Intent intentx = new Intent(this, AboutActivity.class);
				startActivity(intentx);
				break;
			case R.id.btn_find_show:// 设备可见
				Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);// 使蓝牙设备可见，方便配对
				startActivity(in);
				break;
		}
	}

	// 回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (myBluetoothAdapter.isEnabled()) {
				text.setText(R.string.on);
				Utls.showToast(this, "蓝牙打开请求成功");
			} else {
				text.setText(R.string.off);
				Utls.showToast(this, "蓝牙打开请求被拒绝");
			}
		}
	}

	// 设置为初始状态
	void setText2() {
		if (myBluetoothAdapter.isDiscovering()) {
			myBluetoothAdapter.cancelDiscovery();
			viewProgress.setVisibility(View.INVISIBLE);
			btnFind.setText(R.string.find_findnew);
		}
	}

	// 广播
	BroadcastReceiver bReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// add the name and the MAC address of the object to the
				// arrayAdapter
				listDeviceItems.add(new DeviceItem(device.getName(), device.getAddress()));
				adapter.notifyDataSetChanged();
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {// 查找完毕
				btnFind.setText(R.string.find_findnew);
				viewProgress.setVisibility(View.INVISIBLE);
			}
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(bReceiver);// 取消广播的注册
		super.onDestroy();
	}

	@Override
	protected void findView() {
		setContentView(R.layout.activity_find);
		text = (TextView) findViewById(R.id.tv_find_txt);
		btnOn = (Button) findViewById(R.id.btn_find_on);
		btnOff = (Button) findViewById(R.id.btn_find_off);
		btnList = (Button) findViewById(R.id.btn_find_paired);
		btnFind = (Button) findViewById(R.id.btn_find_search);
		myListView = (ListView) findViewById(R.id.lv_find_list);
		btnStartServer = (Button) findViewById(R.id.btn_find_startserver);
		viewProgress = findViewById(R.id.pb_find_progress);
		btnShow = (Button) findViewById(R.id.btn_find_show);
		checkBox = (CheckBox) findViewById(R.id.cb_find_chat);
	}

	@Override
	protected void initViewAndData() {
		viewProgress.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void setListener() {

		// 注册广播
		registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));// 找到设备
		registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));// 查找设备完成
		myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (myBluetoothAdapter == null) {
			btnOn.setEnabled(false);
			btnOff.setEnabled(false);
			btnList.setEnabled(false);
			btnFind.setEnabled(false);
			btnStartServer.setEnabled(false);
			btnShow.setEnabled(false);
			text.setText(R.string.error_not_support);
			Utls.showToast(this, "你的设备不支持蓝牙");
		} else {
			if (myBluetoothAdapter.isEnabled()) {// 设置字
				text.setText(R.string.on);
			} else {
				text.setText(R.string.off);
			}
			btnOn.setOnClickListener(this);
			btnOff.setOnClickListener(this);
			btnList.setOnClickListener(this);
			btnFind.setOnClickListener(this);
			btnStartServer.setOnClickListener(this);
			btnShow.setOnClickListener(this);
			// create the arrayAdapter that contains the BTDevices, and set it
			// to the ListView
			listDeviceItems=new ArrayList<DeviceItem>();
			adapter = new DeviceListAdapter(this, listDeviceItems);
			myListView.setAdapter(adapter);

			// 启动客户端
			myListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

					Class<?> clazzClass = MainClientActivity.class;
					if (checkBox.isChecked()) {
						clazzClass = ChatActivity.class;
						Utls.showToast(FindDeviceActivity.this, "启动聊天客户端");
					} else {
						Utls.showToast(FindDeviceActivity.this, "启动实机客户端");
					}
					Intent intent = new Intent(FindDeviceActivity.this, clazzClass);
					intent.putExtra("mac", listDeviceItems.get(arg2).addr);// 把设备的MAC传过去
					intent.putExtra("istest", false);
					startActivity(intent);
				}
			});
			// 启动模拟客户端
			myListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Class<?> clazzClass = ClientActivity.class;
					if (checkBox.isChecked()) {
						clazzClass = ChatActivity.class;
						Utls.showToast(FindDeviceActivity.this, "启动聊天客户端");
					} else {
						Utls.showToast(FindDeviceActivity.this, "启动模拟客户端");
					}
					Intent intent = new Intent(FindDeviceActivity.this, clazzClass);
					intent.putExtra("mac", listDeviceItems.get(arg2).addr);// 把设备的MAC传过去
					startActivity(intent);
					return true;
				}
			});
		}
		findViewById(R.id.iv_main_about).setOnClickListener(this);
	}
}
