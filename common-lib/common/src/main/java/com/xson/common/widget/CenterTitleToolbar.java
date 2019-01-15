package com.xson.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.xson.common.R;
import com.xson.common.utils.L;


/**
 * @author Milk <249828165@qq.com>
 */
public class CenterTitleToolbar extends Toolbar {
    private Drawable mDropShadowDrawable;
    private float mElevation;
    private int mTitleTextAppearance;
    private TextView mTitleTextView;
    private int mTitleTextColor;
    private CharSequence mTitleText;

    public CenterTitleToolbar(Context context) {
        this(context, null);
    }

    public CenterTitleToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public CenterTitleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CenterTitleToolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar,
                defStyleAttr, defStyleRes);

        mTitleTextAppearance = a.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        a.recycle();

        a = context.getTheme().obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        mElevation = a.getDimension(R.styleable.ActionBar_elevation, 0);
        a.recycle();

        L.d("mElevation=" + mElevation);

        if (mElevation > 0) {
            mDropShadowDrawable = context.getResources().getDrawable(R.drawable.toolbar_dropshadow);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setSaveFromParentEnabled(false);
        }
    }

    public CharSequence getTitle() {
        return mTitleText;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            ensureTitleView();
            if (mTitleTextView.getParent() == null) {
                LayoutParams lp = new LayoutParams(Gravity.CENTER);
                mTitleTextView.setLayoutParams(lp);
                mTitleTextView.setGravity(Gravity.CENTER);
                addView(mTitleTextView, lp);
            }
        } else if (mTitleTextView != null && mTitleTextView.getParent() != null) {
            removeView(mTitleTextView);
        }
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
        mTitleText = title;
    }

    @Override
    public void setTitleTextColor(int color) {
        mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }

    public void setTitleTextAppearance(Context context, int resId) {
        mTitleTextAppearance = resId;
        if (mTitleTextView != null) {
            mTitleTextView.setTextAppearance(context, resId);
        }
    }

    private void ensureTitleView() {
        if (mTitleTextView == null) {
            final Context context = getContext();
            mTitleTextView = new TextView(context);
            mTitleTextView.setSingleLine();
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            if (mTitleTextAppearance != 0) {
                mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
            }
            if (mTitleTextColor != 0) {
                mTitleTextView.setTextColor(mTitleTextColor);
            }
        }
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDropShadowDrawable != null && mElevation > 0) {
//            canvas.save();
            int h = getMeasuredHeight();
            int y = (int) (h - mElevation);
            int w = getMeasuredWidth();
//            canvas.translate(0, -y);
            mDropShadowDrawable.setBounds(0, y, w, h);
            mDropShadowDrawable.draw(canvas);
//            canvas.translate(0, y);
//            canvas.restore();
        }
    }
}
