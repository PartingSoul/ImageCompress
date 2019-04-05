package com.parting_soul.imagecompress.core;

/**
 * 单张图片压缩回调
 *
 * @author parting_soul
 * @date 2019/4/4
 */
public interface OnCompressResultCallback {

    void onSuccess(String compressPath);

    void onFailed(String error);
}
