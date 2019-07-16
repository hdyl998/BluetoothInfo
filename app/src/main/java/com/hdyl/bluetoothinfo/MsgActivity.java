package com.hdyl.bluetoothinfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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

import com.hdyl.bluetoothinfo.utils.ToastUtils;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyBindView;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyBufferKnifeUtils;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyOnClick;
import com.hdyl.bluetoothinfo.utils.log.impl.LogUitls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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


    MsgAdapter adapter;

    List<MsgItem> lists = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        MyBufferKnifeUtils.inject(this);

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


        adapter = new MsgAdapter(this, lists);
        listView.setAdapter(adapter);
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


    //https://blog.csdn.net/wqfae01/article/details/78875749

    /***
     * 创建一个服务端
     */
    private void createServer() {
        try {
            setMsgText("开始创建服务端...");
            mServerSocket = btAdapter.listenUsingRfcommWithServiceRecord(btAdapter.getName(), UUID.fromString(Constants.UUID));
            acceptThread = new AcceptThread();
            acceptThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            setMsgText("创建服务端失败.." + e.toString());
        }
    }

    BluetoothAdapter btAdapter = BTManager.get().getAdapter();


    private BluetoothServerSocket mServerSocket = null;
    private ClientConnectThread clientConnectThread;

    private void createClient() {
        clientConnectThread = new ClientConnectThread();
        clientConnectThread.start();
    }


    public class ClientConnectThread extends Thread {
        @Override
        public void run() {
            // TODO 自动生成的方法存根
            try {

                setMsgText("正在连接服务端");
                //获取BluetoothSocket实例并连接服务器，该处的uuid需与服务器短
                //的uuid一致才能连接成功,connect()是回阻塞的。
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.UUID));
                socket.connect();
                Log.d("TAG", "连接成功");
                setMsgText("正在成功");
                read = new ReadThread();
                read.start();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            super.run();
        }
    }


    @MyOnClick({R.id.btnSend})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                String str = editText.getText().toString().trim();
                if (str.length() == 0) {
                    ToastUtils.show("不能发送空消息");
                    return;
                }
                write(str);
                break;
        }
    }

    BluetoothSocket socket;
    ReadThread read;

    public class AcceptThread extends Thread {
        @Override
        public void run() {
            // TODO 自动生成的方法存根
            try {
                //该方法是服务器阻塞等待客户端的连接，
                //监听到有客户端连接返回一个BluetoothSocket的实例
                socket = mServerSocket.accept();
                LogUitls.print("Server", "连接成功！");
                setMsgText("连接客户端成功！");
                //开启读取线程读取客户端发来的数据
                read = new ReadThread();
                read.start();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();

                setMsgText("连接客户端出错..." + e);
            }
            super.run();
        }
    }

    public class ReadThread extends Thread {
        @Override
        public void run() {
            // TODO 自动生成的方法存根
            if (socket.isConnected()) {
                try {
                    //获取socket的InputStream并不断读取数据
                    InputStream in = socket.getInputStream();
                    byte[] buff = new byte[1024];
                    while (!isInterrupted()) {
                        int size = in.read(buff);
                        if (size > 0) {
                            Log.d("RECVDATA", String.valueOf(buff));
                            MsgItem item = new MsgItem();
                            item.setMine(false);
                            item.setMsg(new String(buff, 0, size));
                            addMsg(item);
                        }
                    }
                    in.close();
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }


    private void addMsg(MsgItem item) {
        if (isMainThread()) {
            adapter.add(item);
        } else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.add(item);
                }
            });
        }
    }

    /**
     * 发送数据：
     *
     * @param str
     */
    public void write(String str) {
        if (socket != null && socket.isConnected()) {
            try {
//获取socket的OutputStream并写入数据
                OutputStream out = socket.getOutputStream();
                out.write(str.getBytes());
                out.close();

                MsgItem item = new MsgItem();
                item.setMine(true);
                item.setMsg(str);
                addMsg(item);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();

                ToastUtils.show("发送数据失败");
            }
        } else {
            ToastUtils.show("当前没有连接");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (read != null) {
            read.interrupt();
            read = null;
        }

        try {

            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            if (mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (clientConnectThread != null) {
            clientConnectThread.interrupt();
        }


    }
}
