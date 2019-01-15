package com.xson.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 *
 */

public class IntentUtil {

    public static void startActivity(Context context, Class clss) {
        Intent intent = new Intent(context, clss);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int type, Class clss) {
        Intent intent = new Intent(context, clss);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class clss, Bundle bundle) {
        Intent intent = new Intent(context, clss);
        intent.putExtra("bundle", bundle);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class clss,String value) {
        Intent intent = new Intent(context, clss);
        intent.putExtra("value",value);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity context, Class clss, int requestCode) {
        Intent intent = new Intent(context, clss);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity context, Class clss,String value, int requestCode) {
        Intent intent = new Intent(context, clss);
        intent.putExtra("type", requestCode);
        intent.putExtra("value",value);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Fragment context, Class clss, int requestCode) {
        Intent intent = new Intent(context.getContext(), clss);
        context.startActivityForResult(intent, requestCode);
    }
}
