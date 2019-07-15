package com.hdyl.bluetoothinfo.utils.log.impl;

import java.io.Serializable;

/**
 * Note：None
 * Created by Liuguodong on 2019/3/25 11:27
 * E-Mail Address：986850427@qq.com
 */
public class LogInfoItem implements Serializable {
    public String tag;
    public String text;

    public LogInfoItem(String tag, String text) {
        this.tag = tag;
        this.text = text;
    }


    public String getAllText(){
        return String.format("tag: %s ,text: %s",tag,text);
    }
}
