package com.fengfz.bmc.finddevice;

import android.bluetooth.BluetoothDevice;

//设备条目
public class DeviceItem {

    public String name;// 名字
    public String addr; // 地址

    public BluetoothDevice device;


    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public DeviceItem(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getBondedInfo() {
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            return "已配对";
        }
        return "可配对";
    }

    @Override
    public String toString() {
        return "DeviceItem{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", device=" + device +
                '}' + "配对状态:" + getBondedInfo();
    }
}
