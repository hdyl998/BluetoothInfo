package com.hdyl.bluetoothinfo.appconfig;

/**
 * Created by liugd on 2019/1/3.
 */

public class IAppConfigFactory {
    private static IBaseAppConfig config = new DefaultAppConfig();


    public static void setConfig(IBaseAppConfig config) {
        IAppConfigFactory.config = config;
    }

    public static IBaseAppConfig getConfig() {
        return config;
    }
}
