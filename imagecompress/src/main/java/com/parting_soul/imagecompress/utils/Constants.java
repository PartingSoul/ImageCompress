package com.parting_soul.imagecompress.utils;

import android.os.Environment;

/**
 * @author parting_soul
 * @date 2019/4/4
 */
public class Constants {

    /**
     * 压缩图片默认存放路径
     */
    public static final String CACHE_PATH_COMPRESS_IMG = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/compress/";

    /**
     * 压缩图片文件名前缀
     */
    public static final String COMPRESS_IMG_PREFIX = "compress_";

}
