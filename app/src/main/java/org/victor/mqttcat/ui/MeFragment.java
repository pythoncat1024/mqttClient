package org.victor.mqttcat.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apkfuns.logutils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.victor.mqttcat.App;
import org.victor.mqttcat.R;
import org.victor.mqttcat.model.DataRepository;
import org.victor.mqttcat.utils.NavUtils;
import org.victor.mqttcat.utils.ToastUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements FragmentBack {


    private TextView tvConnectState;
    private TextView tvClientID;
    private TextView tvPublish;
    private TextView tvSubscribe;

    private static final String ARG_CLIENT_ID = "arg_client_id";
    private static final String ARG_SERVER_IP = "arg_server_ip";
    private String clientID;
    private String serverIP;
    private MqttAndroidClient mqttClient;
    private IMqttToken connect;

    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance() {

        Bundle args = new Bundle();
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MeFragment newInstance(String clientId, String serverIP) {

        Bundle args = new Bundle();
        args.putString(ARG_CLIENT_ID, clientId);
        args.putString(ARG_SERVER_IP, serverIP);
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvConnectState = view.findViewById(R.id.connect_state);
        tvClientID = view.findViewById(R.id.client_id_tv);
        tvPublish = view.findViewById(R.id.publish_tv);
        tvSubscribe = view.findViewById(R.id.subscribe_tv);
        initUI();
        setListener();
    }

    private void initUI() {

        clientID = DataRepository.getClientId(requireContext());
        serverIP = DataRepository.getServerIP(requireContext());
        tvClientID.setText(getString(R.string.client_id_info, clientID));
        if (clientID == null) {
            tvConnectState.setText(R.string.connect_status_no);
            tvPublish.setEnabled(false);
            tvSubscribe.setEnabled(false);
            tvConnectState.setEnabled(false);
        } else if (serverIP == null) {
            tvConnectState.setText(R.string.connect_status_no);
            ToastUtils.show(requireContext(), "未设置 server ip !");
            tvPublish.setEnabled(false);
            tvSubscribe.setEnabled(false);
            tvConnectState.setEnabled(false);
        }
        if (clientID != null && serverIP != null) {
            tvConnectState.setText(R.string.connect_status_no);
            tvClientID.setText(getString(R.string.client_id_info, clientID));
            tvConnectState.setEnabled(true);
            tvPublish.setEnabled(false);
            tvSubscribe.setEnabled(false);
        }

        if (App.isConnected()) {
            tvConnectState.setEnabled(true);
            tvConnectState.setText(getString(R.string.connect_status_yes));
            tvPublish.setEnabled(true);
            tvSubscribe.setEnabled(true);
        }

    }

    private void setListener() {

        if (mqttClient != null) {
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    tvConnectState.setText(R.string.connect_status_no);
                    tvPublish.setEnabled(false);
                    App.setMqttConnected(false);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    LogUtils.e(topic);
                    LogUtils.v(message);
                    LogUtils.e("完成消息：%s", message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    LogUtils.d(token);
                }
            });
        }
        tvClientID.setOnClickListener(v -> {
            LogUtils.w("jump--");
            NavUtils.forwardFragment(requireActivity(), ConnectSettingsFragment.newInstance(), null);
        });
        tvConnectState.setOnClickListener(v -> {
            tvConnectState.setEnabled(false);

            LogUtils.e("0 server:%s, client:%s", serverIP, clientID);
            mqttClient = DataRepository.getMqttClient(requireContext());
            LogUtils.e("1 server:%s, client:%s", serverIP, clientID);
            if (mqttClient.isConnected()) {
                LogUtils.e("之前的连接未关闭，不必创建");
                // 如果多次连接，会导致同一个 topic 被多次收到！
                return;
            }
            try {
                connect = mqttClient.connect();
                connect.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        LogUtils.e("2 server:%s, client:%s", serverIP, clientID);
                        ToastUtils.show(requireContext(), "连接成功");
                        App.setMqttConnected(true);
                        tvConnectState.setText(R.string.connect_status_yes);

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        ToastUtils.show(requireContext(), "连接失败");
                        tvConnectState.setText(R.string.connect_status_no);
                        LogUtils.e(exception);
                    }
                });
            } catch (MqttException e) {
                ToastUtils.show(requireContext(), "连接失败");
                tvConnectState.setText(R.string.connect_status_no);
                e.printStackTrace();
                LogUtils.e(e);
            } catch (NullPointerException e) {
                LogUtils.w(e);
                ToastUtils.show(requireContext(), " 连接 异常");
                System.exit(1);
            }
            tvConnectState.setEnabled(true);
            tvPublish.setEnabled(true);
            tvSubscribe.setEnabled(true);
        });
        tvPublish.setOnClickListener(v -> {
            tvPublish.setEnabled(false);
            LogUtils.w("jump--");
            NavUtils.forwardFragment(requireActivity(), PublishFragment.newInstance(), null);
            tvPublish.setEnabled(true);
        });

        tvSubscribe.setOnClickListener(v -> {
            tvSubscribe.setEnabled(false);
            LogUtils.w("jump--");
            NavUtils.forwardFragment(requireActivity(), SubscribeFragment.newInstance(), null);
            tvSubscribe.setEnabled(true);
        });
    }


    @Override
    public void onBackPressed() {
        DataRepository.disconnect();
        requireActivity().finish();
    }
}
