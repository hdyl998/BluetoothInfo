package com.hdyl.bluetoothinfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.fengfz.bmc.finddevice.DeviceItem;
import com.fengfz.bmc.finddevice.DeviceListAdapter;
import com.hdyl.bluetoothinfo.utils.ToastUtils;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyBindView;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyBufferKnifeUtils;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyOnClick;
import com.hdyl.bluetoothinfo.utils.loopdo.MyCountDownTimer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    AppCompatActivity mContext = this;
    @MyBindView(R.id.toolBar)
    Toolbar toolBar;
    @MyBindView(R.id.tvNotSupport)
    TextView tvNotSupport;//您的设备不支持蓝牙
    @MyBindView(R.id.aSwitch)
    Switch aSwitch;//蓝牙开关
    @MyBindView(R.id.btnSearch)
    Button btnSearch;//查找设备
    @MyBindView(R.id.progressBar)
    ProgressBar progressBar;
    @MyBindView(R.id.btnDeviceVisiable)
    Button btnDeviceVisiable;//设备可见
    @MyBindView(R.id.tvDeviceVisiable)
    TextView tvDeviceVisiable;//88
    @MyBindView(R.id.llContent)
    LinearLayout llContent;

    @MyBindView(R.id.listView)
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyBufferKnifeUtils.inject(this);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.manager:
                        ToastUtils.show("敬请期待");
                        break;
                }
                return true;
            }
        });

        if (btAdapter==null) {
            tvNotSupport.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
            return;
        }
        llContent.setVisibility(View.VISIBLE);
        tvNotSupport.setVisibility(View.GONE);

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btAdapter.isEnabled()) {
                    btAdapter.disable();
                } else {
                    btAdapter.enable();
                }
                updateStatus();
            }
        });
        aSwitch.setTextOn("蓝牙状态--开");
        aSwitch.setTextOff("蓝牙状态--关");
        updateStatus();
        initReceiver();

        adapter = new DeviceListAdapter(mContext, listDeviceItems);
        listView.setAdapter(adapter);
        //短按操作
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!checkBtAvaible()) {
                    return;
                }
                DeviceItem item = listDeviceItems.get(position);
                //查询最新的状态
                new AlertDialog.Builder(mContext).setTitle("信息")
                        .setMessage(item.toString())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
        //长按弹出菜单
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!checkBtAvaible()) {
                    return true;
                }
                DeviceItem item = listDeviceItems.get(position);
                //查询最新的状态
                showHandleMenu(item.getDevice());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

    public static final int HANDLE_ID_BOND = 0;
    public static final int HANDLE_ID_DEL = 1;
    public static final int HANDLE_ID_CONNECT_CLIENT = 2;
    public static final int HANDLE_ID_CONNECT_SERVER = 3;

    private void showHandleMenu(BluetoothDevice device) {

        String[] menus = null;
        int[] handleId = null;
        switch (device.getBondState()) {
            case BluetoothDevice.BOND_NONE:
                menus = new String[]{"配对"};
                handleId = new int[]{HANDLE_ID_BOND};
                break;
            case BluetoothDevice.BOND_BONDED:
                menus = new String[]{"删除配对信息", "连接蓝牙设备(客户端模式)", "连接蓝牙设备(服务端模式)"};
                handleId = new int[]{HANDLE_ID_DEL, HANDLE_ID_CONNECT_CLIENT, HANDLE_ID_CONNECT_SERVER};
                break;
            case BluetoothDevice.BOND_BONDING:
                ToastUtils.show("正在配对中...");
                break;
        }
        if (menus == null) {
            return;
        }

        new AlertDialog.Builder(mContext)
                .setTitle("菜单")
                .setItems(menus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            switch (which) {
                                case HANDLE_ID_BOND:
                                    doBind(device);
                                    break;
                                case HANDLE_ID_DEL:
                                    removedBind(device);
                                    break;
                                case HANDLE_ID_CONNECT_CLIENT:
                                    Intent intent = new Intent(mContext, MsgActivity.class);
                                    intent.putExtra(MsgActivity.KEY_DATA, device);
                                    intent.putExtra(MsgActivity.KEY_TYPE, true);
                                    startActivity(intent);
                                    break;
                                case HANDLE_ID_CONNECT_SERVER:
                                    Intent intent2 = new Intent(mContext, MsgActivity.class);
                                    intent2.putExtra(MsgActivity.KEY_DATA, device);
                                    intent2.putExtra(MsgActivity.KEY_TYPE, false);
                                    startActivity(intent2);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtils.show("操作失败");
                        }

                    }
                }).show();
    }

    private void doBind(BluetoothDevice device) throws Exception {


        //配对方法二：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            device.createBond();
        } else {
            //配对方法一：
            Method createBondMethod = device.getClass().getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(device);

        }
    }


    private void removedBind(BluetoothDevice device) throws Exception {

        //删除配对信息：
        Method createBondMethod = device.getClass().getMethod("removeBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);

    }


    private void initReceiver() {

        // 注册广播
        registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));// 找到设备
        registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));// 查找设备完成

    }

    private BluetoothAdapter btAdapter = BTManager.get().getAdapter();


    /***
     * 更新状态
     */
    private void updateStatus() {
        aSwitch.setChecked(btAdapter.isEnabled());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//加载menu文件到布局
        return true;
    }

    private DeviceListAdapter adapter;
    private List<DeviceItem> listDeviceItems = new ArrayList<>();// 设备列表数据

    @Override
    protected void onDestroy() {
        unregisterReceiver(bReceiver);// 取消广播的注册
        if (downTimer != null)
            downTimer.onDestory();
        super.onDestroy();
    }

    // 广播
    BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    ToastUtils.show("开始扫描");
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    DeviceItem item = new DeviceItem(device.getName(), device.getAddress());
                    item.setDevice(device);
                    listDeviceItems.add(item);
                    adapter.notifyDataSetChanged();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    ToastUtils.show("扫描结束");
                    updateFindingUi(false);
                    break;

            }
        }
    };

    boolean isFindingDevice = false;

    private void updateFindingUi(boolean isFinding) {
        this.isFindingDevice = isFinding;
        if (isFinding) {
            progressBar.setVisibility(View.VISIBLE);
            btnSearch.setText("取消查找");
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            btnSearch.setText("查找设备");
        }
    }


    @MyOnClick({R.id.btnSearch, R.id.btnSearch})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDeviceVisiable:
                if (!checkBtAvaible()) {
                    return;
                }
                deviceVisiable();
                startDownTimer();
                break;
            case R.id.btnSearch://查找设备,取消查找
                if (!checkBtAvaible()) {
                    return;
                }
                //正在查找设备
                if (btAdapter.isDiscovering()) {
                    btAdapter.cancelDiscovery();
                    updateFindingUi(false);
                } else {//没在在查找设备
                    btAdapter.startDiscovery();
                    updateFindingUi(true);
                }
                break;

        }
    }

    private boolean checkBtAvaible() {
        if (!btAdapter.isEnabled()) {
            ToastUtils.show("蓝牙不可用,请先打开蓝牙");
            return false;
        }
        return true;
    }


    MyCountDownTimer downTimer;

    private static final int TOTAL_TIME = 200;

    private void startDownTimer() {
        if (downTimer == null) {
            downTimer = new MyCountDownTimer(TOTAL_TIME);
            downTimer.setOnTimerEvent(new MyCountDownTimer.OnTimerEvent() {
                @Override
                public void onStart(int leftSeconds) {
                    tvDeviceVisiable.setText(leftSeconds + "s");
                }

                @Override
                public void onTicker(int leftSeconds) {
                    tvDeviceVisiable.setText(leftSeconds + "s");
                }

                @Override
                public void onFinish() {
                    tvDeviceVisiable.setText(null);
                }
            });
        }
        downTimer.start();

    }

    private void deviceVisiable() {
        try {
            Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, TOTAL_TIME);// 使蓝牙设备可见，方便配对
            startActivity(in);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(e.toString());
        }

    }
}
