package org.victor.mqttcat.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastUtils {

    private ToastUtils() {
    }

    private static Toast toast;


    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
        toast = null;
    }

    public static void show(Context context, CharSequence charSequence) {
        cancel();
        toast = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void show(Context context, @StringRes int charSequence) {
        cancel();
        toast = Toast.makeText(context, charSequence, Toast.LENGTH_SHORT);
        toast.show();
    }
}
