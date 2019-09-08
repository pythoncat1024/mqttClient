package org.victor.mqttcat;

import android.app.Application;

import com.apkfuns.logutils.LogUtils;
import com.facebook.stetho.Stetho;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.getLogConfig().configShowBorders(false);
        Stetho.initializeWithDefaults(this);
    }

    private static boolean isMqttConnected;

    public static boolean isConnected() {

        return isMqttConnected;
    }

    public static void setMqttConnected(boolean connected) {
        isMqttConnected = connected;
    }
}
