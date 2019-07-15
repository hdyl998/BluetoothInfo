package com.hdyl.bluetoothinfo;

import android.app.Application;

/**
 * Created by liugd on 2019/7/15.
 */

public class App extends Application {

    private static App app;


    public static Application getContext() {
        return app;
    }


    @Override
    public final void onCreate() {
        super.onCreate();
        app = this;
        initApp();
    }

    private void initApp() {
    }
}
