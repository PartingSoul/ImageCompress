package com.parting_soul.imagecompress.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.parting_soul.imagecompress.utils.Constants;
import com.parting_soul.imagecompress.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author parting_soul
 * @date 2019/4/2
 */
public class CompressUtils {
    private static final String TAG = "CompressUtils";
    private Handler mHandler;
    private CompressConfig mCompressConfig;


    public CompressUtils(CompressConfig config) {
        this.mHandler = new Handler();
        this.mCompressConfig = config;
    }


    /**
     * 压缩
     *
     * @param sourcePath
     * @param callback
     */
    public void compress(final String sourcePath, @NonNull final OnCompressResultCallback callback) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (mCompressConfig.isEnablePixelCompress()) {
                    try {
                        compressImageByPixel(sourcePath, callback);
                    } catch (FileNotFoundException e) {
                        sendCompressMessage(false, null, "图片压缩失败", callback);
                    }
                } else {
                    compressImageByQuality(BitmapFactory.decodeFile(sourcePath), sourcePath, callback);
                }
            }
        });
    }

    /**
     * 质量压缩
     *
     * @param sourceBitmap
     * @param sourcePath
     * @param callback
     */
    private void compressImageByQuality(Bitmap sourceBitmap, final String sourcePath, @NonNull final OnCompressResultCallback callback) {
        if (sourceBitmap == null) {
            sendCompressMessage(false, null, "原图片不存在", callback);
            return;
        }

        //旋转对应的角度，方式图片压缩后JPEG头部丢失图片信息
        sourceBitmap = rotateImage(getImageRotateDegree(sourcePath), sourceBitmap);

        int quality = 100;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sourceBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);

        //不断压缩至设置的最大大小
        while (bos.toByteArray().length > mCompressConfig.getMaxSize()) {
            //清除输出流
            bos.reset();
            quality -= 5;
            if (quality < 5) {
                //图片最低质量为5
                break;
            }
            sourceBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            Log.d(TAG, "compress quality =  " + quality + " imageSize = " + bos.toByteArray().length / 1024 + "kb");
        }

        //输出图片
        File thumbnailFile = getThumbnailFile(sourcePath, mCompressConfig);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(thumbnailFile);
            fos.write(bos.toByteArray());
            bos.flush();
            fos.flush();

            //图片压缩成功
            sendCompressMessage(true, thumbnailFile.getAbsolutePath(), null, callback);
            Log.d(TAG, "compress img path = " + thumbnailFile.getAbsolutePath());
        } catch (Exception e) {
            // 图片压缩失败
            sendCompressMessage(false, null, "图片压缩失败", callback);
        } finally {
            sourceBitmap.recycle();
            FileUtils.closeQuietly(fos);
            FileUtils.closeQuietly(bos);
        }

    }


    /**
     * 像素压缩
     *
     * @param sourcePath
     * @param callback
     * @throws FileNotFoundException
     */
    private void compressImageByPixel(String sourcePath, OnCompressResultCallback callback) throws FileNotFoundException {
        if (sourcePath == null) {
            sendCompressMessage(false, null, "压缩的文件不存在", callback);
            return;
        }

        //返回一个描述原Bitmap大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourcePath, options);

        int sourceWidth = options.outWidth;
        int sourceHeight = options.outHeight;

        Log.d(TAG, "compressImageByPixel  width " + sourceWidth + " height = " + sourceHeight);


        int inSampleSize = 1;
        if (sourceWidth >= sourceHeight && sourceWidth > mCompressConfig.getMaxPixel()) {
            inSampleSize = (int) (sourceWidth * 1.0 / mCompressConfig.getMaxPixel() + 1);
        } else if (sourceWidth < sourceHeight && sourceHeight > mCompressConfig.getMaxPixel()) {
            inSampleSize = (int) (sourceHeight * 1.0 / mCompressConfig.getMaxPixel() + 1);
        }

        //按照比例缩放
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(sourcePath, options);

        Log.d(TAG, "compressImageByPixel  newWidth " + bitmap.getWidth() + " newHeight = " + bitmap.getHeight());

        if (mCompressConfig.isEnableQualityCompress()) {
            // 质量压缩
            compressImageByQuality(bitmap, sourcePath, callback);
        } else {
            //旋转对应的角度，方式图片压缩后JPEG头部丢失图片信息
            bitmap = rotateImage(getImageRotateDegree(sourcePath), bitmap);

            File thumbnailFile = getThumbnailFile(sourcePath, mCompressConfig);
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            sendCompressMessage(true, thumbnailFile.getAbsolutePath(), "", callback);
            bitmap.recycle();
        }
    }

    /**
     * 发送压缩消息
     *
     * @param isSuccess
     * @param compressPath
     * @param error
     * @param callback
     */
    private void sendCompressMessage(final boolean isSuccess, final String compressPath, final String error, final OnCompressResultCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    callback.onSuccess(compressPath);
                } else {
                    callback.onFailed(error);
                }
            }
        });
    }

    /**
     * 获取缩略图文件
     *
     * @param imgPath
     * @param config
     * @return
     */
    private static File getThumbnailFile(String imgPath, CompressConfig config) {
        File sourceFile = new File(imgPath);

        if (TextUtils.isEmpty(config.getCacheDir())) {
            return sourceFile;
        }

        File cacheDir = new File(config.getCacheDir());
        if (!cacheDir.mkdir() && !cacheDir.exists()
                || cacheDir.exists() && !cacheDir.isDirectory()) {
            return sourceFile;
        }
        Log.d(TAG, "CompressCacheDir =  " + cacheDir.getAbsolutePath());

        return new File(config.getCacheDir(), Constants.COMPRESS_IMG_PREFIX + sourceFile.getName());
    }


    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 获取图片旋转角度
     * Exif是一种图像文件格式，它的数据存储与JPEG格式是完全相同的。实际上Exif格式就是在JPEG格式头部插入了数码照片的信息
     *
     * @param path
     * @return
     */
    public static int getImageRotateDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 图片旋转指定角度
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.d(TAG, "rotateImage  angle = " + angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
