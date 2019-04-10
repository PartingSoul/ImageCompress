package com.parting_soul.imagecompress.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * @author parting_soul
 * @date 2019/4/9
 */
public class FilePath {

    public static String getFileDirPath(Context context) {
        File root = null;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            root = context.getExternalCacheDir();
            if (root != null && !root.exists()) {
                root.mkdirs();
            }
        }
        if (root == null || !root.exists()) {
            root = context.getCacheDir();
        }
        return root.getAbsolutePath();
    }

}
