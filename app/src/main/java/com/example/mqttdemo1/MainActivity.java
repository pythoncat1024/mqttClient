package com.example.mqttdemo1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LQH";
    Button bt_connect;
    Button bt_sub;
    Button bt_pub;
    TextView textView;
    private EditText et_serverIP;
    String log;
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_serverIP = findViewById(R.id.et_server_ip);
        bt_connect = findViewById(R.id.bt_connect);
        bt_pub = findViewById(R.id.bt_pub);
        bt_sub = findViewById(R.id.bt_sub);
        textView = findViewById(R.id.textView);
        log = "Log:\n\n";
        textView.setText(log);
        findViewById(R.id.bt_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log = "";
                textView.setText(log);
            }
        });
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientId = MqttClient.generateClientId();
                //创建客户端
                // 192.168.42.148
                // inet addr:192.168.42.208  Bcast:192.168.42.255
                String serverIP = "192.168.42.208";
                String inputStr = et_serverIP.getText().toString().trim();
                if (!TextUtils.isEmpty(inputStr) && inputStr.length() > 3) {
                    serverIP = inputStr;
                }
                String serverURI = "tcp://" + serverIP + ":1883";
                LogUtils.e("serverURI=%s", serverURI);
                client = new MqttAndroidClient(MainActivity.this, serverURI, clientId);
                // 连接
                try {
                    IMqttToken token = client.connect();
                    token.setActionCallback(new IMqttActionListener() {
                        //两个响应函数
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d(TAG, "onSuccess");
                            Toast.makeText(MainActivity.this, "connect succeed", Toast.LENGTH_SHORT).show();
                            log += "Connect succeed!\n\n";
                            textView.setText(log);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d(TAG, "onFailure");
                            Toast.makeText(MainActivity.this, "not connect", Toast.LENGTH_SHORT).show();
                            log += "Connect failed!\n\n";
                            textView.setText(log);
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                //设置几个回调函数
                client.setCallback(new MqttCallback() {

                    //连接断开
                    @Override
                    public void connectionLost(Throwable cause) {
                        Toast.makeText(MainActivity.this, "connectionLost", Toast.LENGTH_SHORT).show();
                        LogUtils.e(cause);
                    }

                    //接收信息
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        log = log + "Received msg: " + new String(message.getPayload()) + "\n\n";
                        textView.setText(log);
                        LogUtils.e(log);
                    }

                    //发布信息成功
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        Toast.makeText(MainActivity.this, "published", Toast.LENGTH_SHORT).show();
                        log = log + "Published\n\n";
                        textView.setText(log);
                        try {
                            MqttMessage message = token.getMessage();
                            byte[] payload = message.getPayload();
                            LogUtils.e("deliveryComplete %s, qos=%s", new String(payload), message.getQos());
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        bt_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String topic = "temperature";
                int qos = 1;
                try {
                    // 订阅
                    IMqttToken subToken = client.subscribe(topic, qos);
                    subToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The message was published
                            Toast.makeText(MainActivity.this, "subscribe succeed", Toast.LENGTH_SHORT).show();
                            log = log + "Subscribe topic: " + topic + " succeed!\n\n";
                            textView.setText(log);
                            try {
                                byte[] payload = asyncActionToken.getResponse().getPayload();
                                // todo: 获取订阅消息内容，参考发布的按钮逻辑！！！！
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // The subscription could not be performed, maybe the user was not
                            // authorized to subscribe on the specified topic e.g. using wildcards
                            Toast.makeText(MainActivity.this, "subscribe failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        bt_pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "Question";
                String payload = "What's the temperature?";
                try {
                    MqttMessage message = new MqttMessage(payload.getBytes());
                    // 发布
                    client.publish(topic, message);
                    log = log + "Publish:\n" + "  topic:" + topic + "\n  payload:" + payload + "\n\n";
                    textView.setText(log);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}