package com.fangdr.widget.wheel;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.fangdr.widget.R;
import com.fangdr.widget.wheel.adapters.AbstractWheelTextAdapter;

/*
 * Copyright (C) 2015 David Pizarro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class WheelDialog implements View.OnClickListener, OnWheelChangedListener {

    private static final String LOG_TAG = WheelDialog.class.getSimpleName();
    private final Dialog mDialog;
    private final TextAdapter mAdapter;

    private OnValueConfirmListener mListener;
    private WheelView mWheelView;
    private View mHiddenPanelPickerUI;
    private final Context mContext;
    private int mSelectedPosition;
    private TextView mTitleTextView;

    /**
     * Default constructor
     */
    public WheelDialog(Context context) {
        mContext = context;
        View view = createView();
        mDialog = new Dialog(mContext, R.style.PickerUI_Dialog);
        mDialog.setContentView(view);
        mWheelView.addChangingListener(this);
        mAdapter = new TextAdapter(context);
        mWheelView.setViewAdapter(mAdapter);
    }

    private View createView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.wheel_picker, null);
        mWheelView = (WheelView) view.findViewById(R.id.wheel_view);
        mWheelView.setVisibleItems(5); // Number of items
        mWheelView.setWheelForeground(R.drawable.wheel_val_holo);

        mHiddenPanelPickerUI = view.findViewById(R.id.hidden_panel);
        mHiddenPanelPickerUI.setVisibility(View.GONE);

        mTitleTextView = (TextView) view.findViewById(R.id.title_textView);

        view.findViewById(R.id.mask_view).setOnClickListener(this);
        view.findViewById(R.id.cancel_textView).setOnClickListener(this);
        view.findViewById(R.id.ok_textView).setOnClickListener(this);

        return view;
    }

    public void setTitle(@StringRes int resId) {
        mTitleTextView.setText(resId);
    }

    public void setTitle(String text) {
        mTitleTextView.setText(text);
    }

    /**
     * Hide the panel and clear blur image.
     */
    public void hide() {
        Animation bottomDown = AnimationUtils
                .loadAnimation(mContext, R.anim.picker_panel_bottom_down);
        mHiddenPanelPickerUI.startAnimation(bottomDown);

        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Hide the panel
                mHiddenPanelPickerUI.setVisibility(View.GONE);
                mDialog.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    /**
     * Method to set items to show in panel.
     * In this method, by default, the 'which' is 0 and the position is the half of the elements.
     *
     * @param items elements to show in panel
     */
    public void setItems(String[] items) {
        mAdapter.setData(items);
    }


    /**
     * Method to slide up the panel. Panel displays with an animation, and when it starts, the item
     * of the center is
     * selected.
     */
    public void show() {
        mDialog.show();
        //必须在show之后
        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mHiddenPanelPickerUI.setVisibility(View.VISIBLE);
        Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.picker_panel_bottom_up);
        mHiddenPanelPickerUI.startAnimation(bottomUp);

        mWheelView.setCurrentItem(mSelectedPosition);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancel_textView || i == R.id.mask_view) {
            hide();

        } else if (i == R.id.ok_textView) {
            hide();
            if (mListener != null && mAdapter.getItemsCount() > 0)
                mListener.onResult(mSelectedPosition, mAdapter.data[mSelectedPosition]);

        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        mSelectedPosition = newValue;
    }

    public void setSelectedPosition(int mSelectedPosition) {
        this.mSelectedPosition = mSelectedPosition;
    }

    public void setOnValueConfirmListener(OnValueConfirmListener l) {
        mListener = l;
    }

    public interface OnValueConfirmListener {
        void onResult(int position, String text);
    }

    private static class TextAdapter extends AbstractWheelTextAdapter {
        String[] data;

        protected TextAdapter(Context context) {
            super(context);
        }

        @Override
        protected CharSequence getItemText(int index) {
            return data[index];
        }

        @Override
        public int getItemsCount() {
            return data == null ? 0 : data.length;
        }

        @Override
        public void onCenterItemChanged(int oldIndex, View oldView, int newIndex, View newView) {

        }

        public void setData(String[] data) {
            this.data = data;
            notifyDataChangedEvent();
        }
    }
}
