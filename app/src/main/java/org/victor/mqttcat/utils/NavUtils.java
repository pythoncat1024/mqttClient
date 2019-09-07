package org.victor.mqttcat.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.victor.mqttcat.BottomNavActivity;
import org.victor.mqttcat.R;

public class NavUtils {

    private NavUtils() {
    }

    public static void forwardFragment(@NonNull FragmentActivity context,
                                       @NonNull Fragment fragment, @Nullable String tag) {

        if (context instanceof BottomNavActivity) {
            final int containerID = R.id.nav_fragment_container;
            context.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerID, fragment, tag)
                    .addToBackStack(null)
                    .commit();
        } else {
            throw new RuntimeException("need another container id");
        }
    }

    public static void closeFragment(@NonNull FragmentActivity context,
                                     @NonNull Fragment fragment) {

        if (context instanceof BottomNavActivity) {
            context.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        } else {
            throw new RuntimeException("need another container id");
        }
    }
}
