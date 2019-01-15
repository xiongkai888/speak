package com.fangdr.widget.wheel;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fangdr.widget.R;
import com.fangdr.widget.wheel.adapters.NumericWheelAdapter;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Milk
 */
public class WheelDateView extends LinearLayoutCompat {
    private final Drawable wheelBackground;
    private final Drawable wheelforeground;
    private final int shadowStartColor;
    private final int shadowMiddleColor;
    private final int shadowEndColor;
    private final int itemTextColor;
    private final int centerItemTextColor;
    private final int highlightItemTextColor;
    private final int itemTextSize;
    private final int centerItemTextSize;
    private final int highlightItemTextSize;
    private final WheelView monthWheelView;
    private final WheelView yearWheelView;
    private final WheelView dayWheelView;
    private int visibleItems = 0;
    private int year, month, day;
    private final static int START_YEAR = 1900;

    public WheelDateView(Context context) {
        this(context, null);
    }

    public WheelDateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.WheelView, defStyleAttr, 0);
        visibleItems = a.getInt(R.styleable.WheelView_visible_items_num, 0);
        wheelBackground = a.getDrawable(R.styleable.WheelView_wheel_background);
        wheelforeground = a.getDrawable(R.styleable.WheelView_wheel_foreground);
        shadowStartColor = a.getColor(R.styleable.WheelView_wheel_shadow_start_color, -1);
        shadowMiddleColor = a.getColor(R.styleable.WheelView_wheel_shadow_middle_color, -1);
        shadowEndColor = a.getColor(R.styleable.WheelView_wheel_shadow_end_color, -1);
        itemTextColor = a.getColor(R.styleable.WheelView_item_text_color, -1);
        itemTextSize = a.getDimensionPixelSize(R.styleable.WheelView_item_text_size, 16);
        centerItemTextColor = a.getColor(R.styleable.WheelView_center_item_text_color, itemTextColor);
        centerItemTextSize = a.getDimensionPixelSize(R.styleable.WheelView_center_item_text_size, itemTextSize);
        highlightItemTextColor = a.getColor(R.styleable.WheelView_highlight_item_text_color, itemTextColor);
        highlightItemTextSize = a.getDimensionPixelSize(R.styleable.WheelView_highlight_item_text_size, itemTextSize);
        a.recycle();

        setOrientation(HORIZONTAL);

        Calendar calendar = Calendar.getInstance();

        monthWheelView = makeWheelView(context, attrs);
        yearWheelView = makeWheelView(context, attrs);
        dayWheelView = makeWheelView(context, attrs);

        addView(yearWheelView, generateLayoutParam());
        addView(monthWheelView, generateLayoutParam());
        addView(dayWheelView, generateLayoutParam());

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays();
            }
        };

        // year
        year = calendar.get(Calendar.YEAR);
        int curYearIndex = year - START_YEAR;
        yearWheelView.setViewAdapter(new DateNumericAdapter(getContext(), START_YEAR, year + 10, curYearIndex, getContext().getString(R.string.x_year)));
        yearWheelView.setCurrentItem(curYearIndex);
        yearWheelView.addChangingListener(listener);

        // month
        month = calendar.get(Calendar.MONTH) + 1;
        monthWheelView.setViewAdapter(new DateNumericAdapter(getContext(), 1, 12, month - 1, getContext().getString(R.string.x_month)));
        monthWheelView.setCurrentItem(month - 1);
        monthWheelView.addChangingListener(listener);

        //day
        day = calendar.get(Calendar.DAY_OF_MONTH);
        updateDays();
        dayWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                day = newValue + 1;
            }
        });
        dayWheelView.setCurrentItem(day - 1);
    }

    public void setDate(Date date) {
        if (date == null) {
            return ;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // year
        year = calendar.get(Calendar.YEAR);
        int curYearIndex = year - START_YEAR;
        yearWheelView.setViewAdapter(new DateNumericAdapter(getContext(), START_YEAR, year + 10, curYearIndex, getContext().getString(R.string.x_year)));
        yearWheelView.setCurrentItem(curYearIndex);

        // month
        month = calendar.get(Calendar.MONTH) + 1;
        monthWheelView.setViewAdapter(new DateNumericAdapter(getContext(), 1, 12, month - 1, getContext().getString(R.string.x_month)));
        monthWheelView.setCurrentItem(month - 1);

        //day
        day = calendar.get(Calendar.DAY_OF_MONTH);
        updateDays();
        dayWheelView.setCurrentItem(day - 1);

    }

    public WheelView makeWheelView(Context context, AttributeSet attrs) {
        WheelView wheelView = new WheelView(context, attrs);
        if(visibleItems > 0)
            wheelView.setVisibleItems(visibleItems);
        if(wheelBackground != null)
            wheelView.setWheelBackground(wheelBackground);
        if(wheelforeground != null)
            wheelView.setWheelForeground(wheelforeground);
        if(shadowStartColor != -1 && shadowMiddleColor != -1 && shadowEndColor != -1)
            wheelView.setShadowColor(shadowStartColor, shadowMiddleColor, shadowEndColor);
        return wheelView;
    }

    private LayoutParams generateLayoutParam() {
        LayoutParams lp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        return lp;
    }

    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays() {
        year = START_YEAR + yearWheelView.getCurrentItem();
        month = monthWheelView.getCurrentItem() + 1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayWheelView.setViewAdapter(new DateNumericAdapter(getContext(), 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1, getContext().getString(R.string.x_day)));
        int curDay = Math.min(maxDays, dayWheelView.getCurrentItem() + 1);
        dayWheelView.setCurrentItem(curDay - 1);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current, String format) {
            super(context, minValue, maxValue, format);
            this.currentValue = current;
            setTextSize(itemTextSize);
            setTextColor(itemTextColor);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(highlightItemTextColor);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            if(cachedView == null) {
                cachedView = LayoutInflater.from(context).inflate(R.layout.wheel_date_item, parent, false);
            }
            return super.getItem(index, cachedView, parent);
        }

        @Override
        public void onCenterItemChanged(int oldIndex, View oldView, int newIndex, View newView) {
            TextView tv;
            if(oldView != null && oldView instanceof TextView) {
                tv = (TextView)oldView;
                changeTextColor(tv, itemTextColor);
                if (currentValue == oldIndex) {
                    ((TextView)oldView).setTextColor(highlightItemTextColor);
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize);
            }
            if(newView != null && newView instanceof TextView) {
                tv = (TextView) newView;
                changeTextColor(tv, centerItemTextColor);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, centerItemTextSize);
            }
        }

        private void changeTextColor(TextView textView, int color) {
//            SpannableStringBuilder text = new SpannableStringBuilder(textView.getText().toString());
//            text.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            textView.setText(text);
            textView.setTextColor(color);
        }
    }

}
