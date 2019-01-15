package com.xson.common.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Milk <249828165@qq.com>
 */
public class NetworkImageView extends CheckableImageView {
    private StatusEnum status;
    private ScaleType srcScaleType;

    public enum StatusEnum {
        NONE,
        LOADING,
        FAILED
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        status = StatusEnum.NONE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);

        if(status == StatusEnum.NONE || status == null)
            srcScaleType = scaleType;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;

        if(status != StatusEnum.NONE) {
            //loading or loading failed status image's scale type
            setScaleType(ScaleType.CENTER);
        } else {
            setScaleType(srcScaleType);
        }
    }
}
