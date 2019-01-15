package com.xson.common.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.xson.common.R;
import com.xson.common.widget.NetworkImageView;

/**
 * @author Milk <249828165@qq.com>
 */
public class ImageHelper {
    private static final String TAG = "Image";
    public static boolean DEBUG = false;

    public static void load(Context context, String imageUrl, final ImageView imageView) {
        load(context, imageUrl, imageView, null);
    }

    public static void load(Context context, String imageUrl, ImageView imageView, Callback callback) {
        load(context, imageUrl, imageView, callback, true);
    }

    public static void load(Context context, String imageUrl, ImageView imageView, int errorResId) {
        load(context, imageUrl, imageView, null, true, R.drawable.image_loading, errorResId);
    }

    public static void load(Context context, String imageUrl, ImageView imageView, Callback callback, boolean fit) {
        load(context, imageUrl, imageView, callback, fit, R.drawable.image_loading, R.drawable.image_loading_error);
    }
    public static void load(final Context context, String imageUrl, ImageView imageView, Callback callback, boolean fit, int loadingResId, int errorResId) {
       if (DEBUG) {
           Log.d(TAG, "load image url= " + imageUrl);
       }
        if (imageView == null)
            return;

        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setImageResource(errorResId);
            return;
        }

        if(imageUrl.startsWith("/")) {
            imageUrl = "file://" + imageUrl;
        }

        if(imageView instanceof NetworkImageView) {
            ((NetworkImageView)imageView).setStatus(NetworkImageView.StatusEnum.LOADING);
        }

        final String finalImageUrl = imageUrl;
        ImageCallback imageCallback = new ImageCallback(imageView, callback) {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                if (getCallback() != null)
                    getCallback().onError();

                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (getCallback() != null)
                    getCallback().onSuccess();
                return false;
            }
        };

        try{
            ViewTarget.setTagId(R.id.glide_tag);
        }catch (Exception ignored) {}

        Glide
            .with(context)
            .load(imageUrl)
            .placeholder(loadingResId)
            .error(errorResId)
            .crossFade()
            .listener(imageCallback)
                .dontAnimate() // 关掉动画，否则列表套列表的图片显示会变形
            .into(imageView);
//picasso 不会旋转IOS 90度拍的图片
//        RequestCreator creator = Picasso.with(context)
//                .load(imageUrl)
//                .placeholder(loadingResId)
//                .error(errorResId);
//        if(fit)
//            creator.fit();
//        creator.into(imageView, imageCallback);
    }

    public static void cancelRequest(ImageView iv) {
        Glide.clear(iv);
//        Picasso.with(iv.getContext()).cancelRequest(iv);
    }

    public interface Callback {
        void onSuccess();

        void onError();
    }
//Glide
    private static abstract class ImageCallback implements RequestListener<String, GlideDrawable> {
        private final Callback callback;
        private final ImageView imageView;

        public ImageCallback(ImageView imageView, Callback callback) {
            this.imageView = imageView;
            this.callback = callback;
        }

        public Callback getCallback() {
            return callback;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    //Picasso
//    private static abstract class ImageCallback implements com.squareup.picasso.Callback {
//        private final Callback callback;
//        private final ImageView imageView;
//
//        public ImageCallback(ImageView imageView, Callback callback) {
//            this.imageView = imageView;
//            this.callback = callback;
//        }
//
//        public Callback getCallback() {
//            return callback;
//        }
//
//        public ImageView getImageView() {
//            return imageView;
//        }
//    }
}
