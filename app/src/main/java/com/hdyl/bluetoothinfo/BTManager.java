package com.hdyl.bluetoothinfo;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by liugd on 2019/7/15.
 */

public class BTManager {

    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();


    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    /***
     * 是否支持蓝牙
     * @return
     */
    public boolean isSupport() {
        return adapter != null;
    }

    private final static BTManager instance = new BTManager();

    public static BTManager getInstance() {
        return instance;
    }
}
