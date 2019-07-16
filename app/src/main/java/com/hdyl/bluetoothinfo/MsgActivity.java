package com.hdyl.bluetoothinfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hdyl.bluetoothinfo.utils.bufferknife.MyBindView;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyOnClick;

import java.io.IOException;
import java.util.UUID;

/**
 * Note：None
 * Created by Liuguodong on 2019/7/16 15:50
 * E-Mail Address：986850427@qq.com
 */
public class MsgActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String KEY_DATA = "DATA";
    public static final String KEY_TYPE = "TYPE";
    @MyBindView(R.id.toolBar)
    Toolbar toolBar;
    @MyBindView(R.id.tvInfo)
    TextView tvInfo;//8888
    @MyBindView(R.id.listView)
    ListView listView;
    @MyBindView(R.id.editText)
    EditText editText;
    @MyBindView(R.id.btnSend)
    Button btnSend;//发送
    @MyBindView(R.id.llContent)
    LinearLayout llContent;
    private BluetoothDevice device;
    private boolean isClient;


    AcceptThread acceptThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        device = getIntent().getParcelableExtra(KEY_DATA);


        isClient = getIntent().getBooleanExtra(KEY_TYPE, false);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setLogo(null);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (isClient) {
            toolbar.setTitle(device.getName() + "[客户端模式]");
            createClient();
        } else {
            toolbar.setTitle(device.getName() + "[服务端模式]");
            createServer();
        }
    }

    private void setMsgText(String text) {
        if (isMainThread()) {
            tvInfo.setText(text);
        } else {
            this.runOnUiThread(() ->
                    tvInfo.setText(text)
            );
        }
    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }




    /***
     * 创建一个服务端
     */
    private void createServer() {
        try {
            setMsgText("开始创建服务端");
            mServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(btAdapter.getName(), UUID.fromString(Constants.UUID));
            acceptThread = new AcceptThread();
            acceptThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            setMsgText("创建服务端失败.."+e.toString());
        }
    }

    BluetoothAdapter btAdapter = BTManager.get().getAdapter();


    private BluetoothServerSocket mServerSocket = null;

    private void createClient() {

    }

    @MyOnClick({R.id.btnSend})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:


                break;
        }
    }

    public class AcceptThread extends Thread {
        @Override
        public void run() {
            // TODO 自动生成的方法存根
            try {
                //该方法是服务器阻塞等待客户端的连接，
                //监听到有客户端连接返回一个BluetoothSocket的实例
                socket = mServerSocket.accept();
                Log.d("Server", "以连接");
                //开启读取线程读取客户端发来的数据
                read = new readThread();
                read.start();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            super.run();
        }
    }

}
