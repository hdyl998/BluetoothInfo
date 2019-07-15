package com.hdyl.bluetoothinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hdyl.bluetoothinfo.utils.ToastUtils;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyBindView;
import com.hdyl.bluetoothinfo.utils.bufferknife.MyBufferKnifeUtils;


public class MainActivity extends AppCompatActivity {


    @MyBindView(R.id.tvNotSupport)
    View tvNotSupport;

    @MyBindView(R.id.llContent)
    View llContent;


    AppCompatActivity mContext = this;

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

        if (BTManager.getInstance().isSupport()) {
            tvNotSupport.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
            return;
        }
        llContent.setVisibility(View.VISIBLE);
        tvNotSupport.setVisibility(View.GONE);

        


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//加载menu文件到布局
        return true;
    }
}
