package com.hdyl.bttech.utils.log.impl;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/1/25.
 */

public abstract class ILogAdapter {

    /***
     * 打印一个可对象，优先判定是否是json，如果是json，打印成json格式
     *
     * @param tag
     * @param o
     */
    void print(String tag, Object o) {

    }

    /***
     * 缺省tag 打印日志
     *
     * @param o
     */
    void print(Object o) {

    }


    /***
     * 打印字符串
     *
     * @param tag
     */
    void printString(String tag, String string) {

    }


    public List<LogInfoItem> getLogInfoItems() {
        return null;
    }


    public void clearLogInfoItems() {

    }

    public void setAdapter(BaseAdapter adapter) {
    }
}
