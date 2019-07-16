package com.hdyl.bluetoothinfo;

/**
 * Created by liugd on 2019/7/16.
 */

public class MsgItem {


    private boolean isMine;

    private String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isMine() {
        return isMine;
    }
}
