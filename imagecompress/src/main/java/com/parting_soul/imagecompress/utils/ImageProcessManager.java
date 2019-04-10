package com.parting_soul.imagecompress.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 图片处理
 *
 * @author parting_soul
 * @date 2019/4/8
 */
public class ImageProcessManager {
    private Handler mHandler = new Handler();
    private String cacheImgPath;

    public ImageProcessManager(Context context) {
        this.cacheImgPath = FilePath.getFileDirPath(context);
    }

    /**
     * 将网络图片下载到本地，转化成本地路径
     *
     * @param imgUrl
     * @param callback
     */
    public void getImageFilePath(final String imgUrl, final OnGetImageFileCallback callback) {
        getImageFilePath(imgUrl, null, callback);
    }

    /**
     * 将网络图片下载到本地，转化成本地路径
     *
     * @param imgUrl
     * @param outputFile
     * @param callback
     */
    public void getImageFilePath(final String imgUrl, File outputFile, final OnGetImageFileCallback callback) {
        if (TextUtils.isEmpty(imgUrl) || TextUtils.isEmpty(cacheImgPath)) {
            sendDownloadMessage(false, null, callback);
            return;
        }
        final File file = outputFile == null ? new File(cacheImgPath, "img" + System.currentTimeMillis() + ".jpg") : outputFile;
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = false;
                try {
                    isSuccess = HttpUtils.httpGetMethod(imgUrl, new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                }
                sendDownloadMessage(isSuccess, file.getAbsolutePath(), callback);
            }
        });
    }


    /**
     * 将Bitmap转化成本地路径
     *
     * @param bitmap
     * @param callback
     * @param isRecycle
     */
    public void getImageFilePath(final Bitmap bitmap, final OnGetImageFileCallback callback, final boolean isRecycle) {
        getImageFilePath(bitmap, null, callback, isRecycle);
    }

    /**
     * 将Bitmap转化成本地路径
     *
     * @param bitmap
     * @param outputFile
     * @param callback
     * @param isRecycle
     */
    public void getImageFilePath(final Bitmap bitmap, File outputFile, final OnGetImageFileCallback callback, final boolean isRecycle) {
        if (bitmap == null) {
            sendDownloadMessage(false, null, callback);
            return;
        }
        final File file = outputFile == null ? new File(cacheImgPath, "img" + System.currentTimeMillis() + ".jpg") : outputFile;
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = false;
                try {
                    isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                    if (isRecycle) {
                        bitmap.recycle();
                    }
                } catch (FileNotFoundException e) {
                }
                sendDownloadMessage(isSuccess, file.getAbsolutePath(), callback);
            }
        });
    }

    /**
     * 发送压缩消息
     *
     * @param isSuccess
     * @param outputFilePath
     */
    private void sendDownloadMessage(final boolean isSuccess, final String outputFilePath, final OnGetImageFileCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    callback.onSuccess(outputFilePath);
                } else {
                    callback.onError();
                }
            }
        });
    }

    /**
     * 设置图片缓存路径
     *
     * @param cacheImgPath
     */
    public void setCacheImgPath(String cacheImgPath) {
        this.cacheImgPath = cacheImgPath;
    }


    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public interface OnGetImageFileCallback {
        void onSuccess(String filePath);

        void onError();
    }

}
