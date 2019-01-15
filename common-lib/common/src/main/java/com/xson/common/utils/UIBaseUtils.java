package com.xson.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

/**
 * @author Jecelyin Peng <jecelyin@gmail.com>
 */

public class UIBaseUtils {

    public static Bitmap viewToBitmap(View view) {
        return viewToBitmap(view, 0f);
    }

    public static Bitmap viewToBitmap(View view, float scale) {
        Bitmap b;
        if (view.getMeasuredWidth() == 0 || view.getMeasuredHeight() == 0 || view.getParent() == null) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }

        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        view.layout(0, 0, width, height);

        if (scale > 0) {
            width = (int) (width * scale);
            height = (int) (height * scale);
        }

        b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        b.setDensity(view.getResources().getDisplayMetrics().densityDpi);
        Canvas c = new Canvas(b);
        if (scale > 0f)
            c.scale(scale, scale);

        view.draw(c);

        return b;
    }

    public static float dp2px(Context context, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static float sp2px(Context context, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static int dp2pxInt(Context context, float dp) {
        return (int)dp2px(context,dp);
    }
    public static void toast(Context context, int resId) {
        toast(context, context.getString(resId));
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
