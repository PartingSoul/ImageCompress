package com.parting_soul.imagecompress.core;

import com.parting_soul.imagecompress.bean.Photo;

import java.util.List;

/**
 * @author parting_soul
 * @date 2019/4/3
 */
public interface IImageCompress {

    /**
     * 压缩图片
     */
    void compress();

    /**
     * 及时销毁，防止内存泄漏引发的空指针
     */
    void destroy();

    /**
     * 版本信息
     *
     * @return
     */
    String getVersion();

    interface OnCompressResultCallback {

        /**
         * 开始压缩
         *
         * @param compress
         */
        void onStart(IImageCompress compress);

        /**
         * 压缩成功
         *
         * @param lists
         */
        void onCompressSuccess(List<Photo> lists);

        /**
         * 部分图片压缩失败
         *
         * @param lists
         * @param error
         */
        void onCompressFailed(List<Photo> lists, String error);
    }
}
