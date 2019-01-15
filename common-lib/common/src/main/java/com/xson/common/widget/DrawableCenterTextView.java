package com.xson.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.Gravity;

import java.lang.ref.WeakReference;

/**
 * @author Jecelyin Peng <jecelyin@gmail.com>
 */

public class DrawableCenterTextView extends FormatTextView {

    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private int mDrawablePadding;
    private SpannableStringBuilder mText;

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(getGravity() | Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        mDrawableLeft = left;
        mDrawableRight = right;
        updateTextDrawable();

        if (mText != null)setText(mText);

        super.setCompoundDrawables(null, top, null, bottom);
    }

    @Override
    public void setCompoundDrawablesRelative(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        mDrawableLeft = start;
        mDrawableRight = end;
        updateTextDrawable();

        if (mText != null)setText(mText);

        super.setCompoundDrawablesRelative(null, top, null, bottom);
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(pad);
        mDrawablePadding = pad;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mText = new SpannableStringBuilder(text);
        updateTextDrawable();
        super.setText(mText, type);
    }

    private void updateTextDrawable() {
        if (mDrawableLeft == null && mDrawableRight == null)
            return;

        if (mText == null || mText.length() == 0)
            return;

        final String placeHolder = "\uFFFC";

        ImageSpan dms;

        int len = mText.length();

        if (mDrawableLeft != null) {
            if(mText.charAt(0) != placeHolder.charAt(0)) {
                mText.insert(0, placeHolder);

                dms = new ImageSpan(mDrawableLeft, mDrawablePadding);
                mText.setSpan(dms, 0, placeHolder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (mDrawableRight != null) {
            if(mText.charAt(len-1) != placeHolder.charAt(len-1)) {
                mText.append(placeHolder);

                dms = new ImageSpan(mDrawableRight, mDrawablePadding);
                mText.setSpan(dms, len, mText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public static class ImageSpan extends ReplacementSpan {
        private final int mPadding;
        private final Drawable mDrawable;

        public ImageSpan(Drawable d, int padding) {
            mDrawable = d;
            mPadding = padding;
        }

        /**
         * Your subclass must implement this method to provide the bitmap
         * to be drawn.  The dimensions of the bitmap must be the same
         * from each call to the next.
         */
        public Drawable getDrawable() {
            return mDrawable;
        }

        @Override
        public int getSize(Paint paint, CharSequence text,
                           int start, int end,
                           Paint.FontMetricsInt fm) {
            Drawable d = getCachedDrawable();
            Rect rect = d.getBounds();

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }

            return rect.right + mPadding;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text,
                         int start, int end, float x,
                         int top, int y, int bottom, Paint paint) {
            Drawable b = getCachedDrawable();
            canvas.save();

            int transY = bottom - b.getBounds().bottom;
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int descent = fontMetricsInt.descent;
            transY += descent;
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }

        private Drawable getCachedDrawable() {
            WeakReference<Drawable> wr = mDrawableRef;
            Drawable d = null;

            if (wr != null)
                d = wr.get();

            if (d == null) {
                d = getDrawable();
                mDrawableRef = new WeakReference<Drawable>(d);
            }

            return d;
        }

        private WeakReference<Drawable> mDrawableRef;
    }

}
