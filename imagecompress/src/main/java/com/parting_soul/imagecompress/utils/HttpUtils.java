package com.parting_soul.imagecompress.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author parting_soul
 * @date 2019/4/8
 */
public class HttpUtils {

    /**
     * 采用http协议的Get方法从网络获取图片并且写入缓存
     *
     * @param path         http路径
     * @param outputStream 写入缓存的输出流
     * @return boolean 下载成功 返回true
     */
    public static boolean httpGetMethod(String path, OutputStream outputStream) {
        boolean isSuccess = false;
        HttpURLConnection conn = null;
        InputStream in = null;
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            isSuccess = true;
        } catch (Exception e) {
        } finally {
            if (conn != null) conn.disconnect();
            FileUtils.closeQuietly(in);
            FileUtils.closeQuietly(out);
        }
        return isSuccess;
    }

    /**
     * 利用Http的Post方法获取资源
     *
     * @param path   网络地址
     * @param param  网络地址代封装中的参数
     * @param encode 请求的编码方式
     * @return String
     */
    public static String httpPostMethod(String path, String param, String encode) {
        String result = "";
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            //设置可以写入输出流
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            //设置请求头
            conn.setRequestProperty("Accept-Charset", encode);
            conn.setRequestProperty("contentType", encode);
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);

            //将请求参数写入输出流
            PrintWriter writer = new PrintWriter(conn.getOutputStream());
            writer.print(param);
            writer.flush();
            writer.close();

            //从输入流读出数据
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), encode));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            conn.disconnect();
            FileUtils.closeQuietly(reader);
        }
        return result;
    }

}
