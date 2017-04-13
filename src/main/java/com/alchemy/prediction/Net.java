package com.alchemy.prediction;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gejun on 2017/4/13.
 */

public class Net {
    public static String parseURL(String url){
        String html="";
        HttpURLConnection connection=null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            InputStream in = connection.getInputStream();
            html = readInputStream(in);
        } catch (Exception e) {
            Log.i("gejun","111"+e.toString());
            e.printStackTrace();
        } finally {
            if(connection != null) connection.disconnect();
        }
        return html;
    }

    public static String readInputStream(InputStream inputStream){
        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[1024];
        int bytesRead=0;
        try {
            while((bytesRead = is.read(buffer)) != -1){
                sb.append(new String(buffer, 0, bytesRead));
            }
        } catch (IOException e) {
            Log.i("gejun","222"+e.toString());
            e.printStackTrace();
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("gejun","333"+e.toString());
                }
            }
        }

        return sb.toString();
    }
}
