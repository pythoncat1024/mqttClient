package org.victor.mqttcat.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.apkfuns.logutils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.victor.mqttcat.R;
import org.victor.mqttcat.model.DataRepository;
import org.victor.mqttcat.utils.NavUtils;
import org.victor.mqttcat.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribeFragment extends Fragment {

    // subscribe

    public SubscribeFragment() {
        // Required empty public constructor
    }

    public static SubscribeFragment newInstance() {

        Bundle args = new Bundle();

        SubscribeFragment fragment = new SubscribeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscribe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText etTopic = view.findViewById(R.id.et_sub_topic);
        final RadioGroup rgQos = view.findViewById(R.id.rg_qos);

        view.findViewById(R.id.title_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requireActivity().onBackPressed();
                    }
                });

        view.findViewById(R.id.title_done)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int qos;
                        int buttonId = rgQos.getCheckedRadioButtonId();
                        if (buttonId == R.id.rb_qos0) {
                            qos = 0;
                        } else if (buttonId == R.id.rb_qos1) {
                            qos = 1;
                        } else {
                            qos = 2;
                        }
                        final String topic = etTopic.getText().toString();
                        if (TextUtils.isEmpty(topic)) {
                            ToastUtils.show(requireContext(), "话题不能为空");
                        } else {
                            MqttAndroidClient mqttClient = DataRepository.getMqttClient(requireContext());

                            try {
                                IMqttToken iMqttToken = mqttClient.subscribe(topic, qos);
                                iMqttToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        ToastUtils.show(requireContext(), "订阅话题: " + topic + " 成功了!");
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                        ToastUtils.show(requireContext(), "订阅话题: " + topic + " 失败了!");
                                        LogUtils.e(exception);
                                    }
                                });

                            } catch (MqttException e) {
                                e.printStackTrace();
                                ToastUtils.show(requireContext(), "订阅话题: " + topic + " 失败了!");
                            }
                            NavUtils.forwardFragment(requireActivity(),MeFragment.newInstance(),null);
                        }
                    }
                });

    }

    private Fragment get() {
        return this;
    }
}
