package com.fengfz.bmc.about;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hdyl.bluetoothinfo.R;


public class AboutActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findView();
        initilizeViewAndData();
        setOnListener();
    }

    private void setOnListener() {
        // TODO Auto-generated method stub

    }

    private void initilizeViewAndData() {

    }

    private void findView() {
        findViewById(R.id.ll_title_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title_back)).setText("关于");
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ll_title_back:
                finish();
                break;
        }
    }
}
