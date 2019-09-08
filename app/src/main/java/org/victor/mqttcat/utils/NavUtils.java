package org.victor.mqttcat.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.apkfuns.logutils.LogUtils;

import org.victor.mqttcat.BottomNavActivity;
import org.victor.mqttcat.R;

public class NavUtils {

    private NavUtils() {
    }

    public static void forwardFragment(@NonNull FragmentActivity context,
                                       @NonNull Fragment fragment, @Nullable String tag) {

        LogUtils.i("context: " + context + ", f= " + fragment.getClass().getSimpleName());
        if (context instanceof BottomNavActivity) {
            final int containerID = R.id.nav_fragment_container;
            FragmentManager fragmentManager = context.getSupportFragmentManager();
            //            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            //            LogUtils.e("当前回退栈数量 a：%s, fn=%s", backStackEntryCount, fragmentManager.getFragments().size());
            fragmentManager.beginTransaction()
                    .replace(containerID, fragment, tag)
                    .commit();
            //            int backStackEntryCount2 = fragmentManager.getBackStackEntryCount();
            //            LogUtils.e("当前回退栈数量 b：%s, fn=%s", backStackEntryCount2, fragmentManager.getFragments().size());

        } else {
            throw new RuntimeException("need another container id");
        }
    }

    public static void closeFragment(@NonNull FragmentActivity context,
                                     @NonNull Fragment fragmentShow) {
        forwardFragment(context, fragmentShow, null);
    }
}
