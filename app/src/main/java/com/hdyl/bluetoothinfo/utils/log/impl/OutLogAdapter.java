package com.hdyl.bluetoothinfo.utils.log.impl;

import android.util.Log;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSON;
import com.hdyl.bluetoothinfo.utils.log.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/25.
 */

public class OutLogAdapter extends ILogAdapter {

    private final static int LEN = 1000;

    private BaseAdapter adapter;
    private LinkedList<LogInfoItem> logInfoItems = new LinkedList<LogInfoItem>();

    @Override
    public void print(String tag, Object o) {


        if (o == null) {
            o = "NULL";
        }

        if (o instanceof String) {
//            String oo = o + "";
//            if (oo.startsWith("{") || oo.startsWith("[")) {
//                try {
//                    JSON.parse(oo);//表示是JSON
//                    Logger.t(tag).json((String) o);
//                } catch (Exception e) {
//                    Logger.t(tag).e(oo);//非JSON
//                }
//            } else {
//                Logger.t(tag).e(oo);
//            }
//            addLog(tag, o);
            Logger.t(tag).e(o + "");
            addLog(tag, o);
        } else if (o instanceof Integer || o instanceof Long || o instanceof Double || o instanceof Boolean || o instanceof Float || o instanceof Character) {
            Logger.t(tag).e(o + "");
            addLog(tag, o);
        } else {
            String text = JSON.toJSONString(o);
//            Logger.t(tag).json(text);
//            addLog(tag, text);
            Logger.t(tag).e(text);
            addLog(tag, o);
        }
    }

    private void addLog(String tag, Object text) {
        if (logInfoItems.size() + 1 >= LEN) {
            logInfoItems.clear();
        }
        logInfoItems.add(0, new LogInfoItem(tag, text.toString()));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void print(Object o) {
        print("lgdx", o);
    }

    @Override
    public void printString(String tag, String string) {
        if (string == null) {
            string = "null";
        }
        Log.e(tag, string);
        addLog(tag, string);
    }

    @Override
    public List<LogInfoItem> getLogInfoItems() {
        return logInfoItems;
    }

    @Override
    public void clearLogInfoItems() {
        logInfoItems.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
    }

}
