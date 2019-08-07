package com.hdyl.bttech;

/**
 * Created by liugd on 2019/8/3.
 */

public class Test {
    //byte数组转成16进制数据
    public static String bytes2HexString(byte[] b) {// 转16进制
        String str = "["+b.length+"] ";
        for (int i = 0; i < b.length; i++) {
            String string = String.format("%X", b[i]&0xff);
            if(string.length()==1){
                string="0"+string;
            }
            str += "0x" + string+" ";
        }
        return str;
    }

    public static void main(String[] args) {

        byte[]bytes=new byte[]{0x7a,0x1b};
        byte byteArray[]=new byte[bytes.length];
        System.arraycopy(bytes,0,byteArray,0,bytes.length);
        for (byte aByte : byteArray) {
            System.out.println(Integer.toHexString(aByte&0xff));
        }
        System.out.println(Integer.toHexString(((0xEF+0xBF+0xBD+0x02)^0xFF)& 0xFF));
    }
}
