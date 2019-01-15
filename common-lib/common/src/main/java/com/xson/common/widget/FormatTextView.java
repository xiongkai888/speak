package com.xson.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xson.common.R;
import com.xson.common.utils.L;


/**
 * Created by xson on 15-9-25.
 */
public class FormatTextView extends TextView {
    private CharSequence mFormatText;

    public FormatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FormatText);
        int mFormatTextId = a.getResourceId(
                R.styleable.FormatText_formatText, 0);

        String defaultValue = a.getString(R.styleable.FormatText_defaultValue);
        String separator = a.getString(R.styleable.FormatText_separator); // 参数多个，需要分隔符
        mFormatText = mFormatTextId > 0 ? a.getResources().getText(mFormatTextId).toString() : a.getString(R.styleable.FormatText_formatText);
        a.recycle();

        if (TextUtils.isEmpty(mFormatText)) {
            mFormatText = getText();
        }

        if (!TextUtils.isEmpty(separator) && !TextUtils.isEmpty(defaultValue)) {
            String[] defaultValues = defaultValue.split(separator);
            textValue(defaultValues);
        } else {
            setTextValue(defaultValue);
        }

    }

    private void textValue(Object[] formatArgs) {
        if (!TextUtils.isEmpty(mFormatText)) {
            int size = formatArgs.length;
            Object value;
            for (int i = 0; i < size; i++) {
                value = formatArgs[i];
                if (value == null)
                    value = "";
                formatArgs[i] = value;
            }
            setText(Html.fromHtml(String.format(mFormatText.toString(), formatArgs)));
        }
    }

    public void setTextValue(String... formatArgs) {
        textValue(formatArgs);
    }

    public void setFormatText(CharSequence formatText) {
        mFormatText = formatText;
    }


    public void setFormatText(int formatTextId) {
        setFormatText(getContext().getString(formatTextId));
    }
}
