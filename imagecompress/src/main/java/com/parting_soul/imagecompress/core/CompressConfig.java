package com.parting_soul.imagecompress.core;

import android.content.Context;

import com.parting_soul.imagecompress.utils.FilePath;

/**
 * 图片压缩配置
 *
 * @author parting_soul
 * @date 2019/4/3
 */
public class CompressConfig {

    /**
     * 长或宽不超过的最大像素,单位px
     */
    private int maxPixel = 1200;

    /**
     * 压缩后图片的最大大小 单位B
     */
    private long mMaxSize = 200 * 1024;

    /**
     * 启用质量压缩
     */
    private boolean enableQualityCompress = true;

    /**
     * 启用像素压缩
     */
    private boolean enablePixelCompress = true;

    /**
     * 压缩后缓存图片目录，非文件路径(全路径)
     */
    private String cacheDir;

    private Context mContext;


    private CompressConfig(Context context) {
        this.mContext = context;
        this.cacheDir = FilePath.getFileDirPath(context);
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public long getMaxSize() {
        return mMaxSize;
    }

    public boolean isEnableQualityCompress() {
        return enableQualityCompress;
    }

    public boolean isEnablePixelCompress() {
        return enablePixelCompress;
    }

    public int getMaxPixel() {
        return maxPixel;
    }

    public Context getContext() {
        return mContext;
    }

    public static class Builder {
        private CompressConfig config;

        public Builder(Context context) {
            this.config = new CompressConfig(context);
        }

        /**
         * 图片压缩后的最大大小
         *
         * @param maxSize
         * @return
         */
        public Builder maxSize(long maxSize) {
            config.mMaxSize = maxSize;
            return this;
        }

        /**
         * 图片压缩后长或宽不超过的最大像素,单位px
         *
         * @param maxPixel
         * @return
         */
        public Builder maxPixel(int maxPixel) {
            config.maxPixel = maxPixel;
            return this;
        }

        /**
         * 压缩图片存储缓存路径
         *
         * @param cacheDir
         * @return
         */
        public Builder cacheDir(String cacheDir) {
            config.cacheDir = cacheDir;
            return this;
        }

        /**
         * 启用质量压缩
         *
         * @param enable
         * @return
         */
        public Builder enableQualityCompress(boolean enable) {
            config.enableQualityCompress = enable;
            return this;
        }

        /**
         * 启用像素压缩
         *
         * @param enable
         * @return
         */
        public Builder enablePixelCompress(boolean enable) {
            config.enablePixelCompress = enable;
            return this;
        }

        public CompressConfig create() {
            return config;
        }

    }

}
