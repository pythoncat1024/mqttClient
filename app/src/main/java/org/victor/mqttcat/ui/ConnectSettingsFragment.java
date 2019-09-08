package org.victor.mqttcat.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.apkfuns.logutils.LogUtils;

import org.victor.mqttcat.R;
import org.victor.mqttcat.model.DataRepository;
import org.victor.mqttcat.utils.NavUtils;
import org.victor.mqttcat.utils.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectSettingsFragment extends Fragment implements FragmentBack {


    private EditText etClientID;
    private EditText etServerIP;

    public ConnectSettingsFragment() {
        // Required empty public constructor
    }

    public static ConnectSettingsFragment newInstance() {

        Bundle args = new Bundle();
        ConnectSettingsFragment fragment = new ConnectSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvTitle = view.findViewById(R.id.title_text);
        tvTitle.setText("设置 serverIP 和 clientID");
        etClientID = view.findViewById(R.id.et_client_id);
        etServerIP = view.findViewById(R.id.et_server);
        setListener(view);
    }

    private void setListener(View view) {
        view.findViewById(R.id.title_back)
                .setOnClickListener(v -> onBackPressed());

        view.findViewById(R.id.title_done)
                .setOnClickListener(v -> {
                    String clientID = etClientID.getText().toString().trim();
                    String serverIP = etServerIP.getText().toString().trim();
                    if (TextUtils.isEmpty(clientID) || TextUtils.isEmpty(serverIP)) {
                        ToastUtils.show(requireContext(), "serverIP 以及 client 君不能为空");
                    }
                    DataRepository.saveClientID(requireContext(), clientID);
                    DataRepository.saveServerIP(requireContext(), serverIP);
                    onBackPressed();
                });

    }

    private Fragment get() {
        return this;
    }

    @Override
    public void onBackPressed() {

        LogUtils.e("back..");
        FragmentActivity context = getActivity();
        if (context == null) {
            LogUtils.e("context== null");
        } else {
            NavUtils.closeFragment(context, MeFragment.newInstance());
        }
    }
}
