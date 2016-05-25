package com.running.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ZhangHang on 2016/4/16.
 */
public class ImageLoader {

    public static Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        InputStream is = null;
        URL url = null;
        try {
            url = new URL(path);
            is = url.openStream();
            //BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

}
