package org.victor.mqttcat.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.MqttTraceHandler;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.victor.mqttcat.R;
import org.victor.mqttcat.model.DataRepository;
import org.victor.mqttcat.utils.NavUtils;
import org.victor.mqttcat.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublishFragment extends Fragment {


    public PublishFragment() {
        // Required empty public constructor
    }

    public static PublishFragment newInstance() {

        Bundle args = new Bundle();

        PublishFragment fragment = new PublishFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitle(view);

    }

    private void initTitle(@NonNull View view) {

        final RadioGroup rgQos = view.findViewById(R.id.rg_qos);
        final CheckedTextView cbRetained = view.findViewById(R.id.cb_retained);
        cbRetained.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbRetained.setChecked(!cbRetained.isChecked());
            }
        });
        final EditText etTopic = view.findViewById(R.id.et_pub_topic);
        final EditText etContent = view.findViewById(R.id.et_pub_content);
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

                        final String topic = etTopic.getText().toString();
                        final String content = etContent.getText().toString();
                        LogUtils.e("topic=%s,content=%s", topic, content);

                        if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(content)) {
                            ToastUtils.show(requireContext(), "话题及内容不能为空");
                        } else {

                            MqttAndroidClient mqttClient = DataRepository.getMqttClient(requireContext());
                            boolean retained = cbRetained.isChecked();
                            int qos;
                            int buttonId = rgQos.getCheckedRadioButtonId();
                            if (buttonId == R.id.rb_qos0) {
                                qos = 0;
                            } else if (buttonId == R.id.rb_qos1) {
                                qos = 1;
                            } else {
                                qos = 2;
                            }
                            try {
                                IMqttDeliveryToken token = mqttClient.publish(topic, content.getBytes(), qos, retained);
                                token.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        ToastUtils.show(requireContext(), "发布成功了");
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                        ToastUtils.show(requireContext(), "发布失败了");
                                        LogUtils.e(exception);
                                    }
                                });

                            } catch (MqttException e) {
                                e.printStackTrace();
                                ToastUtils.show(requireContext(), "发布失败了");
                            }
                            NavUtils.forwardFragment(requireActivity(), MeFragment.newInstance(), null);
                        }
                    }
                });
        TextView titleTv = view.findViewById(R.id.title_text);
        titleTv.setText("发布消息咯");
    }

    private Fragment get() {
        return this;
    }
}
