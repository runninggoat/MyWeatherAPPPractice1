package com.myweather.gjj.myweather;

import android.util.Log;

import com.google.gson.Gson;
import com.myweather.gjj.myweather.models.JsonInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by 15018 on 2017/11/11.
 */

public class Util {

    public static String TAG = "Util";

    public static String getAPI(String urlPath) throws IOException {
        // 统一资源
        URL url = new URL(urlPath);
        // 连接类的父类，抽象类
        URLConnection urlConnection = url.openConnection();
        // http的连接类
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        // 设定请求的方法，默认是GET
//            httpURLConnection.setRequestMethod("POST");
        // 设置字符编码
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
        httpURLConnection.connect();

        int bodyLength = httpURLConnection.getContentLength();
//        Log.w(TAG, String.format("body length---->%d", bodyLength));
        System.out.println(String.format("body length---->%d", bodyLength));

        BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
        ArrayList<Byte> bodyBytes = new ArrayList<>();
        int size;
        int totalSize = 0;
        byte[] buf = new byte[1024];
        while ((size = bin.read(buf)) != -1) {
            for (int i=0; i<size; i++) {
                bodyBytes.add(buf[i]);
            }
            totalSize += size;
        }
        byte[] retBytes = new byte[totalSize];
        for (int i=0; i<totalSize; i++)
            retBytes[i] = bodyBytes.get(i);
        String retStr = new String(retBytes);
        return retStr;
    }

    public static JsonInfo parseStr(String jsonStr) {
        Gson gson = new Gson();
        JsonInfo jsonInfo = gson.fromJson(jsonStr, JsonInfo.class);
        return jsonInfo;
    }

}
