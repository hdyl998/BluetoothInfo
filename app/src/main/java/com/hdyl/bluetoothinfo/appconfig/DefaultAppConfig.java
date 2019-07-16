package com.hdyl.bluetoothinfo.appconfig;

/**
 * Note：None
 * Created by Liuguodong on 2019/1/4 10:07
 * E-Mail Address：986850427@qq.com
 */
public class DefaultAppConfig implements IBaseAppConfig {
    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean isTestFun() {
        return true;
    }
}
