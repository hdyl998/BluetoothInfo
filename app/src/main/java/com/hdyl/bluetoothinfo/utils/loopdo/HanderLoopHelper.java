//package com.hdyl.bluetoothinfo.utils.loopdo;
//
//import android.os.Handler;
//import android.os.Message;
//
//import com.hd.base.maininterface.IComCallBacks;
//
///**
// * 一个用handler的循环的工具，一般用于计时
// * Created by Administrator on 2018/3/20.
// */
//
//public class HanderLoopHelper implements ILifeCircle{
//
//    private int loopTimeMillis = 1000;//循环时间1秒
//
//    private boolean isStarted = false;//是否启动过
//
//    public void setLoopTimeMillis(int loopTimeMillis) {
//        this.loopTimeMillis = loopTimeMillis;
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            startLoop();
//            try {
//                if (loopCallBacks != null) {
//                    loopCallBacks.call(null);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//
//    public Handler getHandler() {
//        return handler;
//    }
//
//    public IComCallBacks loopCallBacks;
//
//    public void setLoopCallBacks(IComCallBacks comCallBacks) {
//        this.loopCallBacks = comCallBacks;
//    }
//
//    /***
//     * 开始循环
//     */
//    public void startLoop() {
//        isStarted = true;
//        stopLoop();
//        if (handler != null)
//            handler.sendEmptyMessageDelayed(0, loopTimeMillis);
//    }
//
//    /***
//     * 开始循环
//     */
//    public void startLoopRightNow() {
//        if (handler != null)
//            handler.handleMessage(null);//执行一次
//    }
//
//
//    /**
//     * 开始循环
//     *
//     * @param delayMillis 延时
//     */
//    public void startLoopDelayed(int delayMillis) {
//        isStarted = true;
//        stopLoop();
//        if (handler != null)
//            handler.sendEmptyMessageDelayed(0, delayMillis);
//    }
//
//
//    /***
//     * 停止循环
//     */
//    public void stopLoop() {
//        if (handler != null)
//            handler.removeCallbacksAndMessages(null);
//    }
//
//    /**
//     * 生命周期onPause
//     */
//    public void onPause() {
//        //只有手动调用过才停止
//        if (isStarted) {
//            stopLoop();
//        }
//    }
//
//
//    /***
//     * 生命周期onResume
//     */
//    public void onResume() {
//        //只有手动调用过才开启
//        if (isStarted) {
//            startLoopDelayed(500);
//        }
//    }
//
//
//    public void setStarted(boolean started) {
//        isStarted = started;
//    }
//
//    /***
//     * 生命周期onDestory
//     */
//    public void onDestory() {
//        handler = null;
//        setLoopCallBacks(null);
//    }
//}
