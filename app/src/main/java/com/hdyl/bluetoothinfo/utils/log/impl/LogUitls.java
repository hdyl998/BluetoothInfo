package com.hdyl.bluetoothinfo.utils.log.impl;


import com.hdyl.bluetoothinfo.appconfig.IAppConfigFactory;
import com.hdyl.bluetoothinfo.utils.log.AndroidLogAdapter;
import com.hdyl.bluetoothinfo.utils.log.LogLevel;
import com.hdyl.bluetoothinfo.utils.log.Logger;

/**
 * @类名:LogUitls
 * @功能描述:Log日志工具类
 * @作者:XuanKe'Huang
 * @时间:2015-1-20 下午4:37:18
 * @Copyright 2014
 */
public class LogUitls {


    private final static ILogAdapter logAdaper;

    static {
        if (IAppConfigFactory.getConfig().isDebug()) {
            logAdaper = new OutLogAdapter();
            Logger.init("lgdx")                 // default PRETTYLOGGER or use just init()
                    .methodCount(3)                 // default 2
                    .hideThreadInfo()               // default shown
                    .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                    .methodOffset(3)                // default 0
                    .logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter
        } else {
            logAdaper = new EmptyLogAdapter();
        }
    }

    public static ILogAdapter getLogAdaper() {
        return logAdaper;
    }

    /***
     * 打印一个可对象，优先判定是否是json，如果是json，打印成json格式
     *
     * @param tag
     * @param o
     */
    public static void print(String tag, Object o) {
        logAdaper.print(tag, o);
    }

    /***
     * 缺省tag 打印日志
     *
     * @param o
     */
    public static void print(Object o) {
        logAdaper.print(o);
    }


    /***
     * 打印字符串
     *
     * @param tag
     */
    public static void printString(String tag, String string) {
        logAdaper.printString(tag, string);
    }

}
