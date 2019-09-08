package org.victor.mqttcat.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkfuns.logutils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.victor.mqttcat.App;
import org.victor.mqttcat.utils.ToastUtils;

public class DataRepository {

    private static final String SP = "local-sp";
    private static final String KEY_CLIENT_ID = "KEY_CLIENT_ID";
    private static final String KEY_SERVER_IP = "KEY_SERVER_IP";


    public static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(SP, Context.MODE_PRIVATE);
    }

    private static MqttAndroidClient mClient;

    public static boolean hasLocalStore(@NonNull Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
        return sp.getString(KEY_CLIENT_ID, null) != null;
    }


    public static synchronized MqttAndroidClient getMqttClient(Context context) {
        if (mClient == null) {
            String serverIP = getServerIP(context);
            String clientId = getClientId(context);
            if (serverIP == null || clientId == null) {
                String msg = "serverIP or clientID == null !!!";
                ToastUtils.show(context, msg);
                throw new RuntimeException(msg);
            }
            final String serverURI = "tcp://" + serverIP + ":1883";
            mClient = new MqttAndroidClient(context, serverURI, clientId);
        }
        return mClient;
    }

    @Nullable
    public static MqttAndroidClient cachedClient() {
        return mClient;
    }

    public static void clearClient() {
        mClient = null;
    }

    public static synchronized MqttAndroidClient resetMqttClient(Context context,
                                                                 String serverUri,
                                                                 String clientID) {
        if (mClient != null && mClient.isConnected()) {
            try {
                mClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        mClient = null;
        mClient = new MqttAndroidClient(context, serverUri, clientID);
        return mClient;
    }


    public static void saveServerIP(Context context, String serverIp) {
        getSP(context).edit().putString(KEY_SERVER_IP, serverIp).apply();
    }

    public static void saveClientID(Context context, String clientID) {
        getSP(context).edit().putString(KEY_CLIENT_ID, clientID).apply();
    }


    @Nullable
    public static String getServerIP(Context context) {
        return getSP(context).getString(KEY_SERVER_IP, null);
    }

    @Nullable
    public static String getClientId(@NonNull Context context) {
        return getSP(context).getString(KEY_CLIENT_ID, null);
    }



    public static void disconnect(){
        MqttAndroidClient mqttClient = DataRepository.cachedClient();
        if (mqttClient != null && mqttClient.isConnected()) {
            // 在 client.connect 的时候，会 bind service
            mqttClient.unregisterResources();
            // 调用 unregisterResources 会去 unbind service
            try {
                IMqttDeliveryToken[] pendingDeliveryTokens = mqttClient.getPendingDeliveryTokens();
                if (pendingDeliveryTokens != null) {
                    LogUtils.e("length: %s", pendingDeliveryTokens.length);
                }
                String clientId = mqttClient.getClientId();
                LogUtils.e("disconnect...");
                IMqttToken disconnect = mqttClient.disconnect();
                disconnect.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        LogUtils.e("disconnect-- success");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        LogUtils.e("disconnect-- failure");
                    }
                });

                // 断开连接之后，之前的 mqttClient 对象就不可用了。
                // 如果不清除。返回键只是退出了当前栈，并没有关闭当前进程，导致 之前的 client 还存在
                DataRepository.clearClient();
                App.setMqttConnected(false);
                //                System.exit(0);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
