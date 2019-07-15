package com.fengfz.bmc.utls;

//计算工具
public class CalcTool {

	private static double rateA = 0.36735f;
	private static double rateV = 0.0745545f;

	// 安培（电流）值转成byte
	public static byte A2Byte(int a) {
		return (byte) (a / rateA + 71);
	}

	// byte 转成安培（电流）
	public static float byte2A(byte b) {
		return (float) ((b - 71) * rateA);
	}
	//电压换算成字节
	public static byte V2Byte(int v) {
		return (byte) (v * rateV * 51);
	}
	//字节换算成电压
	public static float byte2V(byte b) {
		return (float) (b / rateV / 51);
	}
}
