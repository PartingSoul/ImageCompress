package com.parting_soul.imagecompress.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author parting_soul
 * @date 2019/4/4
 */
public class FileUtils {

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeable = null;
            }
        }
    }
}
