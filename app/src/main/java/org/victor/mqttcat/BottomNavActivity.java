package org.victor.mqttcat;

import android.app.AlarmManager;
import android.app.Service;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.apkfuns.logutils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.victor.mqttcat.model.DataRepository;
import org.victor.mqttcat.ui.DashFragment;
import org.victor.mqttcat.ui.HomeFragment;
import org.victor.mqttcat.ui.MeFragment;

public class BottomNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        LogUtils.e("on--create");
        initPermissions();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        CardView connectStateV = findViewById(R.id.connect_state);
        TextView tvConnectState = findViewById(R.id.connect_state_tv);
        connectStateV.setVisibility(View.GONE);
        //        FrameLayout container = findViewById(R.id.nav_fragment_container);
        final int containerID = R.id.nav_fragment_container;
        navView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_home:
                                replaceFragment(containerID, HomeFragment.newInstance());
                                break;
                            case R.id.navigation_dashboard:
                                replaceFragment(containerID, DashFragment.newInstance());
                                break;
                            case R.id.navigation_me:
                                replaceFragment(containerID, MeFragment.newInstance());
                                break;
                        }
                        return true;
                    }
                });

        // show default ui
        replaceFragment(containerID, HomeFragment.newInstance());
    }

    private void initPermissions() {
        AlarmManager alarmManager = (AlarmManager)
                this.getSystemService(Service.ALARM_SERVICE);
        LogUtils.e(alarmManager);
    }

    private void replaceFragment(@SuppressWarnings("SameParameterValue") @IdRes int resID,
                                 Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(resID, fragment)
                .commit();

    }

    @Override
    public void onBackPressed() {
        MqttAndroidClient mqttClient = DataRepository.cachedClient();
        if (mqttClient != null && mqttClient.isConnected()) {
            // 在 client.connect 的时候，会 bind service
            mqttClient.unregisterResources();
            // 调用 unregisterResources 会去 unbind service
            try {
                LogUtils.e("disconnect...");
                mqttClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        super.onBackPressed();
    }
}
