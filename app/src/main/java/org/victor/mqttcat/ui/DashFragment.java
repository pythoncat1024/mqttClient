package org.victor.mqttcat.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apkfuns.logutils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.victor.mqttcat.R;
import org.victor.mqttcat.model.DataRepository;
import org.victor.mqttcat.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashFragment extends Fragment {


    public DashFragment() {
        // Required empty public constructor
    }

    public static DashFragment newInstance() {

        Bundle args = new Bundle();

        DashFragment fragment = new DashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MqttAndroidClient mqttClient = DataRepository.getMqttClient(requireContext());

        LogUtils.e("我来了 connect?=%s", mqttClient.isConnected());
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                ToastUtils.show(requireContext(), "连接丢失！！！");
                LogUtils.e("lost connect");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                LogUtils.e("msg arrived: %s, %s", topic, new String(message.getPayload()));

                ToastUtils.show(requireContext(), "收到话题:" + topic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                LogUtils.e("deliveryComplete: %s", token);
            }
        });
    }
}
