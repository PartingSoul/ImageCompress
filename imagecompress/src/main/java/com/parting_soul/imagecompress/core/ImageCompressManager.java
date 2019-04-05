package com.parting_soul.imagecompress.core;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.parting_soul.imagecompress.BuildConfig;
import com.parting_soul.imagecompress.bean.Photo;

import java.io.File;
import java.util.List;

/**
 * @author parting_soul
 * @date 2019/4/3
 */
public class ImageCompressManager implements IImageCompress {
    private static final String TAG = "ImageCompressManager";
    private CompressConfig mCompressConfig;
    private List<Photo> mImgLists;
    private CompressUtils mCompressUtils;
    private IImageCompress.OnCompressResultCallback mOnCompressResultCallback;
    private int finishCount;
    private long startCompressTime;

    private ImageCompressManager(List<Photo> imgLists, CompressConfig config, @NonNull IImageCompress.OnCompressResultCallback callback) {
        this.mImgLists = imgLists;
        this.mCompressConfig = config;
        this.mCompressUtils = new CompressUtils(config);
        this.mOnCompressResultCallback = callback;
    }


    @Override
    public void compress() {
        if (mImgLists == null) {
            mOnCompressResultCallback.onCompressFailed(null, "图片集合不存在");
            return;
        }

        for (Photo image : mImgLists) {
            if (image == null) {
                mOnCompressResultCallback.onCompressFailed(mImgLists, "某图片为空");
                return;
            }
        }

        this.finishCount = 0;
        //质量压缩
        startCompressTime = System.currentTimeMillis();
        //压缩前回调
        mOnCompressResultCallback.onStart(this);
        for (Photo photo : mImgLists) {
            doCompress(photo);
        }
    }

    /**
     * 单张图片开始压缩
     *
     * @param photo
     */
    private void doCompress(final Photo photo) {

        //图片路径不存在
        if (TextUtils.isEmpty(photo.getSourcePath())) {
            handleCallback("图片路径不存在");
            return;
        }

        // 文件不存在
        File file = new File(photo.getSourcePath());
        if (!file.exists() || !file.isFile()) {
            handleCallback(photo.getSourcePath() + "路径不存在");
            return;
        }

        // < 200KB,不压缩
        if (file.length() < mCompressConfig.getMaxSize()) {
            photo.setCompress(true);
            photo.setCompressPath(file.getAbsolutePath());
            handleCallback();
            return;
        }

        mCompressUtils.compress(photo.getSourcePath(), new com.parting_soul.imagecompress.core.OnCompressResultCallback() {
            @Override
            public void onSuccess(String compressPath) {
                photo.setCompress(true);
                photo.setCompressPath(compressPath);
                handleCallback();
            }

            @Override
            public void onFailed(String error) {
                handleCallback(error);
                Log.e(TAG, photo.getSourcePath() + " compress error " + error);
            }
        });
    }

    /**
     * 处理图片压缩结果回调
     *
     * @param error
     */
    private void handleCallback(String... error) {
        finishCount++;
        if (finishCount < mImgLists.size()) {
            return;
        }

        //全部图片压缩完成
        if (error.length > 0) {
            mOnCompressResultCallback.onCompressFailed(mImgLists, error[0]);
            return;
        }

        for (Photo photo : mImgLists) {
            if (!photo.isCompress()) {
                mOnCompressResultCallback.onCompressFailed(mImgLists, photo.getSourcePath() + "压缩失败");
                return;
            }
        }

        mOnCompressResultCallback.onCompressSuccess(mImgLists);

        Log.e(TAG, "压缩时间" + (System.currentTimeMillis() - startCompressTime) * 1.0 / 1000 + "s");
    }

    /**
     * 及时销毁，防止内存泄漏引发的空指针
     */
    @Override
    public void destroy() {
        mCompressUtils.destroy();
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static ImageCompressManager builder(List<Photo> imgLists, CompressConfig config, @NonNull IImageCompress.OnCompressResultCallback callback) {
        return new ImageCompressManager(imgLists, config, callback);
    }

}
