package com.xson.common.helper;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by chunlin on 2016/8/18.
 */
public class NetworkHelper {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isAvailable()) {
            return true;
        }
        return false;
    }
}