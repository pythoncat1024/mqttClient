package org.victor.mqttcat.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apkfuns.logutils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.victor.mqttcat.R;
import org.victor.mqttcat.model.DataRepository;
import org.victor.mqttcat.model.Msg;
import org.victor.mqttcat.ui.adapter.ReceivedAdapter;
import org.victor.mqttcat.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashFragment extends Fragment implements FragmentBack{


    private RecyclerView rvReceived;
    private View connectStateV;

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

        connectStateV = view.findViewById(R.id.connect_tv);
        rvReceived = view.findViewById(R.id.rv_received);
        rvReceived.setLayoutManager(new LinearLayoutManager(requireContext()));
        final ReceivedAdapter adapter = new ReceivedAdapter();
        rvReceived.setAdapter(adapter);

        MqttAndroidClient mqttClient = DataRepository.getMqttClient(requireContext());
        boolean connect = mqttClient.isConnected();
        updateUI(connect);
        LogUtils.e("我来了 connect?=%s", mqttClient.isConnected());
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                ToastUtils.show(requireContext(), "连接丢失！！！");
                LogUtils.e("lost connect");
                updateUI(false);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                LogUtils.e("msg arrived: %s, %s", topic, new String(message.getPayload()));
                Msg msg = new Msg();
                msg.topic = topic;
                msg.content = new String(message.getPayload());
                msg.qos = message.getQos();
                adapter.addMsg(msg);
                LogUtils.i(msg);
                ToastUtils.show(requireContext(), "收到话题:" + topic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                LogUtils.e("deliveryComplete: %s", token);
            }
        });
    }


    private void updateUI(boolean connect) {
        if (connect) {
            rvReceived.setVisibility(View.VISIBLE);
            connectStateV.setVisibility(View.GONE);
        } else {
            rvReceived.setVisibility(View.GONE);
            connectStateV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DataRepository.disconnect();
        requireActivity().finish();
    }
}
