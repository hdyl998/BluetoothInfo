package com.fengfz.bmc.common;

/**
 * 常量字段
 * @author
 *
 */
public class Consts {
	//服务器在注册时的UUID号
	public static String UUID="00001101-0000-1000-8000-00805F9B34FB";
	//代表服务器的名称
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	//三种状态
	public static final String status[]={"滑行","电动","助力"};
	//错误信息1
	public static final String error1[]={"手把高","扭矩高","霍尔错误","温度停机","欠压停机","堵转停机","电机缺相","MOS短路"};
	//错误信息2
	public static final String error2[]={"扭矩无","手把无","欠压保护","堵转保护","刹车无"};
}