package org.victor.mqttcat;

import android.app.Application;

import com.apkfuns.logutils.LogUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.getLogConfig().configShowBorders(false);
    }

    private static boolean isMqttConnected;

    public static boolean isConnected() {

        return isMqttConnected;
    }

    public static void setMqttConnected(boolean connected) {
        isMqttConnected = connected;
    }
}
