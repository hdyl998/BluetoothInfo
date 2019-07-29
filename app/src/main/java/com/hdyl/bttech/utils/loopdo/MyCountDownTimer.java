package com.hdyl.bttech.utils.loopdo;

import android.os.Handler;

/**
 * 倒计时助手
 * <p>Created by Administrator on 2018/10/18.<p>
 * <p>佛祖保佑，永无BUG<p>
 */

public class MyCountDownTimer implements ILifeCircle{

    private int totalSeconds;

    private final int oneTickerSeconds;

    private int leftSeconds;

    private boolean isStop = false;

    private Handler mHandler = new Handler();

    private OnTimerEvent onTimerEvent;


    public MyCountDownTimer(int totalSeconds, int oneTickerSeconds) {
        if (oneTickerSeconds < 1) {
            oneTickerSeconds = 1;
        }
        this.totalSeconds = totalSeconds;
        this.oneTickerSeconds = oneTickerSeconds;
    }


    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public MyCountDownTimer(int totalSeconds) {
        this(totalSeconds, 1);
    }

    public void setOnTimerEvent(OnTimerEvent onTimerEvent) {
        this.onTimerEvent = onTimerEvent;
    }

    public void start() {
        if (onTimerEvent == null) {
            throw new RuntimeException("onTimerEvent is null");
        }
        leftSeconds = totalSeconds;
        isStop = false;
        mHandler.removeCallbacks(runnable);
        onTimerEvent.onStart(totalSeconds);
        mHandler.postDelayed(runnable, oneTickerSeconds * 1000);
    }


    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    /**
     * 销毁时调用
     */
    @Override
    public void onDestory() {
        cancel();
        onTimerEvent = null;
    }

    /**
     * 主动取消
     */
    public void cancel() {
        isStop = true;
        mHandler.removeCallbacks(runnable);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isStop) {
                return;
            }
            leftSeconds -= oneTickerSeconds;
            if (leftSeconds <= 0) {
                isStop = true;
                onTimerEvent.onFinish();
            } else {
                onTimerEvent.onTicker(leftSeconds);
                mHandler.postDelayed(runnable, oneTickerSeconds * 1000);
            }
        }
    };


    public interface OnTimerEvent {
        void onStart(int leftSeconds);

        void onTicker(int leftSeconds);

        void onFinish();
    }
}
