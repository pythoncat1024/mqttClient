package com.example.mqttdemo1;

import android.app.Application;

import com.apkfuns.logutils.LogUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.getLogConfig().configShowBorders(false);
    }
}
