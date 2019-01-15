package com.xson.common.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.xson.common.R;
import com.xson.common.utils.ImageUtils;
import com.xson.common.utils.L;
import com.xson.common.utils.UIBaseUtils;
import com.xson.common.view.BottomActionSheet;

import java.io.File;
import java.io.IOException;

/**
 * @author Jecelyin Peng <jecelyin@gmail.com>
 */

public class ImageCameraAction implements BottomActionSheet.ActionSheetListener {
    private static final int CHOOSE_FROM_GALLAY = 55;
    private static final int CHOOSE_FROM_CAMERA = 56;
    private static final int RESULT_FROM_CROP = 57;
    private FragmentActivity context;
    private File tempImage;
    private File croppedImage;
    private int cropWidth, cropHeight;

    public ImageCameraAction(FragmentActivity activity) {
        context = activity;
    }

    public void show() {
        show(context.getSupportFragmentManager());
    }

    public void show(FragmentManager fm) {
        new BottomActionSheet.Builder(context, fm)
                .setCancelableOnTouchOutside(true)
                .setCancelButtonTitle(android.R.string.cancel)
                .setOtherButtonTitles(context.getString(R.string.img_from_album),
                        context.getString(R.string.img_from_camera))
                .setListener(this)
                .show();
    }

    @Override
    public void onDismiss(BottomActionSheet BottomActionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(BottomActionSheet BottomActionSheet, int index) {
        BottomActionSheet.dismiss();
        if (index == 0) {
            startImagePick();
        } else {
            startActionCamera();
        }
    }

    /**
     * 注意不能使用私有目录，不然剪切App可能访问不了
     *
     * @return
     */
    private File getTempFile() {
        try {
            File file = File.createTempFile(
                    "tk_image_temp",  /* prefix */
                    ".jpg",         /* suffix */
                    Environment.getExternalStorageDirectory()      /* directory */
            );
            return file;
        } catch (Exception e) {
            L.e(e);
            try {
                File file = File.createTempFile(
                        "tk_image_temp",  /* prefix */
                        ".jpg",         /* suffix */
                        null      /* directory */
                );
                return file;
            } catch (IOException e1) {
                L.e(e1);
            }
            return null;
        }
    }

    /**
     * 选择图片裁剪
     */
    private void startImagePick() {
        Intent intent = ImageUtils.getImagePickerIntent();
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityForResult(intent, CHOOSE_FROM_GALLAY);
        } else {
            UIBaseUtils.toast(context, R.string.cannot_find_associated_app);
        }
    }

    /**
     * 相机拍照
     */
    private void startActionCamera() {
        tempImage = getTempFile();
        if (tempImage == null) {
            UIBaseUtils.toast(context, R.string.cannot_create_temp_file);
            return;
        }
        Intent intent = ImageUtils.getImageCaptureIntent(Uri.fromFile(tempImage));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityForResult(intent, CHOOSE_FROM_CAMERA);
        } else {
            UIBaseUtils.toast(context, R.string.cannot_find_associated_app);
        }
    }

    private void startActionCrop(String image) {
        File imageFile = new File(image);
        if (!imageFile.exists()) {
            UIBaseUtils.toast(context, R.string.image_not_exists);
            return;
        }
        croppedImage = getTempFile();
        if (croppedImage == null) {
            UIBaseUtils.toast(context, R.string.cannot_create_temp_file);
            return;
        }
        Uri uriImageFile =Uri.fromFile(imageFile);
        Uri uriCropp =Uri.fromFile(croppedImage);
        Intent intent = ImageUtils.getImageCropIntent(uriImageFile, uriCropp);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityForResult(intent, RESULT_FROM_CROP);
        } else {
            UIBaseUtils.toast(context, R.string.cannot_find_associated_app);
        }
    }

    public void setCropSize(int cropWidth, int cropHeight) {
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
    }

    public boolean hasResult(int requestCode) {
        return requestCode == CHOOSE_FROM_CAMERA
                || requestCode == CHOOSE_FROM_GALLAY
                || requestCode == RESULT_FROM_CROP;
    }

    public File onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            close();
            return null;
        }

        boolean crop = cropWidth > 0 && cropHeight > 0;

        String image;
        switch (requestCode) {
            case CHOOSE_FROM_GALLAY:
                image = ImageUtils.getImageFileFromPickerResult(context, data);
                if (image == null) {
                    UIBaseUtils.toast(context, R.string.image_not_exists);
                    return null;
                }

                if (crop) {
                    startActionCrop(image);
                    return null;
                }

                return new File(image);
            case CHOOSE_FROM_CAMERA:
                //注意小米拍照后data 为null
                if (tempImage == null) {
                    UIBaseUtils.toast(context, R.string.camera_failure_messgae);
                    return null;
                }
                image = tempImage.getPath();

                if (crop) {
                    startActionCrop(image);
                    return null;
                }

                return new File(image);
            case RESULT_FROM_CROP:
                return croppedImage;
        }
        return null;
    }

    public void close() {
        if (tempImage != null) {
            tempImage.delete();
            tempImage = null;
        }
        if (croppedImage != null) {
            croppedImage.delete();
            croppedImage = null;
        }
    }

}
