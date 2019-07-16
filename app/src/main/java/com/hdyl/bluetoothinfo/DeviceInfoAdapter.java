package com.hdyl.bluetoothinfo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.hdyl.bluetoothinfo.adapter.BaseViewHolder;
import com.hdyl.bluetoothinfo.adapter.SuperAdapter;

import java.util.List;

/**
 * Created by liugd on 2019/7/16.
 */

public class DeviceInfoAdapter extends SuperAdapter<BluetoothDevice> {


    public DeviceInfoAdapter(Context context, List<BluetoothDevice> data) {
        super(context, data, R.layout.item_list_device);
    }

    @Override
    protected void onBind(BaseViewHolder holder, BluetoothDevice item, int position) {
        holder.setText(R.id.tv_itema_name, item.getName());
        holder.setText(R.id.tv_itema_addr, item.getAddress());

        holder.setText(R.id.tvInfo, item.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对":"");

    }
}
