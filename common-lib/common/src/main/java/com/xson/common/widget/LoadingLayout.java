package com.xson.common.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xson.common.R;


/**
 *
 * R.layout.loading_layout
 * 请求数据时的界面展示类
 * 加载数据的数据的时候先显示一个loading界面，成功加载数据后显示内容，失败展示失败的原因
 * Created by xson on 15-10-20.
 */
public class LoadingLayout extends FrameLayout {
    private View loadingLayout;
    private View errorLayout;
    private View mEmptyLayout;
    private OnTryLoadListener mOnTryLoadListener;
    private TextView errorTextView;
    private View mContentView;
    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = findViewById(android.R.id.content);
    }

    private void ensureLoadingLayout() {
        if(loadingLayout == null) {
            loadingLayout = inflate(getContext(), R.layout.ssrl_loading, null);
            addView(loadingLayout, generateChildLayoutParams());
//            loadingLayout.setLayoutParams(generateChildLayoutParams());
        }
    }

    private void ensureEmptyLayout() {
        if(mEmptyLayout == null) {
            mEmptyLayout = inflate(getContext(), R.layout.ssrl_empty, null);
            addView(mEmptyLayout, generateChildLayoutParams());
        }
    }

    private void hideLoadingLayout() {
        if(loadingLayout != null)
            loadingLayout.setVisibility(GONE);
    }

    public void showLoadingLayout() {
        hideErrorLayout();
        hideContentView();
        hideEmptyLayout();
        ensureLoadingLayout();
        loadingLayout.setVisibility(VISIBLE);
    }


    public boolean showError(String errorMessage) {
        hideLoadingLayout();
        hideContentView();
        hideEmptyLayout();
        ensureErrorLayout();
        errorLayout.setVisibility(VISIBLE);

        if (!TextUtils.isEmpty(errorMessage)) {
            errorTextView.setText(errorMessage);
        }

        Button button = (Button) errorLayout.findViewById(R.id.ssrl___try_btn);
        button.setText(R.string.refresh);
        return true;
    }

    public void showTips(String tips, int  buttonTitle) {
        hideLoadingLayout();
        hideContentView();
        hideEmptyLayout();
        ensureErrorLayout();
        errorLayout.setVisibility(VISIBLE);

        if (!TextUtils.isEmpty(tips)) {
            errorTextView.setText(tips);
        }
        Button button = (Button) errorLayout.findViewById(R.id.ssrl___try_btn);
        button.setText(buttonTitle);
        errorTextView.setCompoundDrawables(null, null, null, null);
    }

    private void hideErrorLayout() {
        if(errorLayout != null)
            errorLayout.setVisibility(GONE);
    }

    private void hideEmptyLayout() {
        if(mEmptyLayout != null)
            mEmptyLayout.setVisibility(GONE);
    }





    private void ensureErrorLayout() {
        if(errorLayout == null) {
            errorLayout = inflate(getContext(), R.layout.ssrl_error, null);
            addView(errorLayout, generateChildLayoutParams());
//            loadingLayout.setLayoutParams(generateChildLayoutParams());
            errorTextView = (TextView)errorLayout.findViewById(R.id.ssrl___error_tv);

            if(mOnTryLoadListener != null) {
                View tryAgainButton = errorLayout.findViewById(R.id.ssrl___try_btn);
                tryAgainButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingLayout();
                        mOnTryLoadListener.onTryRefresh();
                    }
                });
            }
        }
    }


    public void setOnTryLoadListener(OnTryLoadListener onTryLoadListener) {
        this.mOnTryLoadListener = onTryLoadListener;

        if (errorLayout != null) {
            View tryAgainButton = errorLayout.findViewById(R.id.ssrl___try_btn);
            tryAgainButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnTryLoadListener.onTryRefresh();
                }
            });

        }
    }

    /**
     * 显示内容区
     */
    public void showContentView() {
        hideLoadingLayout();
        hideErrorLayout();
        hideEmptyLayout();
        mContentView.setVisibility(VISIBLE);
    }

    private void hideContentView() {
        mContentView.setVisibility(GONE);
    }


    protected LayoutParams generateChildLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }


    public void showEmpty() {
        hideLoadingLayout();
        hideErrorLayout();
        hideContentView();
        ensureEmptyLayout();
        mEmptyLayout.setVisibility(VISIBLE);
    }
}
