package com.xson.common.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.text.TextUtils;

/**
 * @author Jecelyin Peng <jecelyin@gmail.com>
 *  给BitmapDrawable添加小红点
 */

public class BadgeBitmapDrawable extends BitmapDrawable {
    private static final float DEFAULT_CORNER_RADIUS = 360;
    private static final float DEFAULT_TEXT_SIZE = 24;
    private static final int DEFAULT_BADGE_COLOR = Color.RED;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int MIN_BADGE_HEIGHT = 12;
    private static final int MIN_BADGE_WIDTH = 12;
    private int badgeHeight;
    private int badgeWidth;
    private boolean hiddenBadge = false;

    private static class Config {
        private float cornerRadius;
        private String text;
        private int badgeColor = DEFAULT_BADGE_COLOR;
        private int textColor = DEFAULT_TEXT_COLOR;
        private float textSize = DEFAULT_TEXT_SIZE;
        private Typeface typeface = Typeface.DEFAULT_BOLD;
        private Bitmap bitmap;
        private Resources res;
        private float badgeX = 0.75f;
        private float badgeY = 0;
    }
    private Config config;
    private float[] outerR = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float[] outerROfText1 = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float[] outerROfText2 = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private Paint paint;
    private Paint.FontMetrics fontMetrics;
    private ShapeDrawable badgeDrawable;

    public static class Builder {
        private Config config;

        public Builder() {
            config = new Config();
        }

        public Builder cornerRadius(float radius) {
            config.cornerRadius = radius;
            return this;
        }

        public Builder typeFace(Typeface typeface) {
            config.typeface = typeface;
            return this;
        }

        public Builder bitmap(Resources res, int bitmapRes) {
            config.res = res;
            config.bitmap = BitmapFactory.decodeResource(res, bitmapRes);
            return this;
        }

        public Builder bitmap(Resources res, Bitmap bitmap) {
            config.res = res;
            config.bitmap = bitmap;
            return this;
        }

        public Builder badgePosition(float x, float y) {
            config.badgeX = x;
            config.badgeY = y;
            return this;
        }

        public Builder text(String text1) {
            config.text = text1;
            return this;
        }

        public Builder badgeColor(int color) {
            config.badgeColor = color;
            return this;
        }

        public Builder textColor(int color) {
            config.textColor = color;
            return this;
        }

        public Builder textSize(float size) {
            config.textSize = size;
            return this;
        }

        public BadgeBitmapDrawable build() {
            return new BadgeBitmapDrawable(config);
        }
    }

    private static class BadgeDrawable extends ShapeDrawable {
        public BadgeDrawable(Shape s) {
            super(s);
        }
    }

    private BadgeBitmapDrawable(Config config) {
        super(config.res, config.bitmap);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(config.typeface);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);

        this.config = config;

        setCornerRadius(config.cornerRadius);
        RoundRectShape shape = new RoundRectShape(outerR, null, null);
        badgeDrawable = new BadgeDrawable(shape);

        setTextSize(config.textSize);
        measureBadge();
    }

    private void measureBadge() {
        if (!TextUtils.isEmpty(config.text)) {
            int textWidth = (int) paint.measureText(config.text);
            badgeHeight = (int) (config.textSize * 1.4f);
            badgeWidth = (int) (textWidth + config.textSize * 0.4f);
        } else {
            badgeHeight = MIN_BADGE_HEIGHT;
            badgeWidth = MIN_BADGE_WIDTH;
        }


        setCornerRadius(DEFAULT_CORNER_RADIUS);
        badgeDrawable.setBounds(0, 0, badgeWidth, badgeHeight);
    }

    public void setHiddenBadge(boolean b) {
        hiddenBadge = b;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        super.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        super.setColorFilter(colorFilter);
    }

    public void setCornerRadius(float radius) {
        if (config.cornerRadius != radius) {
            config.cornerRadius = radius;
            outerR[0] = outerR[1] = outerR[2] = outerR[3] =
                    outerR[4] = outerR[5] = outerR[6] = outerR[7] = config.cornerRadius;

            outerROfText1[0] = outerROfText1[1] = outerROfText1[6] = outerROfText1[7] = config.cornerRadius;
            outerROfText1[2] = outerROfText1[3] = outerROfText1[4] = outerROfText1[5] = 0f;

            outerROfText2[0] = outerROfText2[1] = outerROfText2[6] = outerROfText2[7] = 0f;
            outerROfText2[2] = outerROfText2[3] = outerROfText2[4] = outerROfText2[5] = config.cornerRadius;
            invalidateSelf();
        }
    }

    public void setBadgeColor(int color) {
        config.badgeColor = color;
        invalidateSelf();
    }

    public int getBadgeColor() {
        return config.badgeColor;
    }

    public void setTextColor(int color) {
        config.textColor = color;
        invalidateSelf();
    }

    public int getTextColor() {
        return config.textColor;
    }

    public void setTextSize(float textSize) {
        config.textSize = textSize;
        paint.setTextSize(textSize);
        fontMetrics = paint.getFontMetrics();
        measureBadge();
        invalidateSelf();
    }

    public float getTextSize() {
        return config.textSize;
    }

    public void setText(String text) {
        config.text = text;
        measureBadge();
        invalidateSelf();
    }

    public String getText() {
        return config.text;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (hiddenBadge)
            return;

        Rect rect = getBounds();
        float x = rect.width() * config.badgeX;
        float y = rect.height() * config.badgeY;

        canvas.save();
        canvas.translate(x, y);
        Rect bounds = badgeDrawable.getBounds();

//        int marginTopAndBottom = (int) (bounds.height() * config.badgeY);
//        int marginLeftAndRight = (int) (bounds.width() * config.badgeX);
//
//        badgeDrawable.setBounds(
//                bounds.left + marginLeftAndRight,
//                bounds.top + marginTopAndBottom,
//                bounds.right - marginLeftAndRight,
//                bounds.bottom - marginTopAndBottom);
        badgeDrawable.getPaint().setColor(config.badgeColor);
        badgeDrawable.draw(canvas);

        float textCx = bounds.centerX();
        float textCy = bounds.centerY();
        float textCyOffset = (-fontMetrics.ascent) / 2f - 2;

        paint.setColor(config.textColor);
        if (!TextUtils.isEmpty(config.text)) {
            canvas.drawText(
                    config.text,
                    textCx,
                    textCy + textCyOffset,
                    paint);
        }
        canvas.restore();
    }
}
