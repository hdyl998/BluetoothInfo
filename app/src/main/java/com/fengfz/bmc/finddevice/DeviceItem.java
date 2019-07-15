package com.fengfz.bmc.finddevice;

//设备条目
public class DeviceItem {

	public String name;// 名字
	public String addr; // 地址
	public DeviceItem(String name,String addr){
		this.name=name;
		this.addr=addr;
	}
}
