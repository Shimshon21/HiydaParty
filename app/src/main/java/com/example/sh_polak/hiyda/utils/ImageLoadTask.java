package com.example.sh_polak.hiyda.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hackeru on 24/09/2017.
 */

public class ImageLoadTask extends AsyncTask <String, Void, Bitmap> {

    //Mini fly-weight
    private static Map<String, Bitmap> flyweightImgs = new HashMap();

    public static void loadImages(final List<Map> results, final Runnable callback) {
        new Thread() {
            public void run() {
                for (Map r : results) {
                    try {
                        String url = r.get("PartyImage").toString();//put image url in string url if does skip
                        if (flyweightImgs.get(url) != null) continue;//if the url is not exist in fltweight if yes
                        else
                            flyweightImgs.put(url, BitmapFactory.decodeStream(new URL(url).openStream()));//if not exist than put in fltweight
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                callback.run();//???
            }
        }.start();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {//not used!!!!
        String url = urls[0];//given image URL
        Bitmap bmp = flyweightImgs.get(url);//check if already exists
        if (bmp != null) return bmp;//if already exists - reuse
        try {//if not -> load, store and return
            bmp = BitmapFactory.decodeStream(new URL(url).openStream());//Load remotely
            flyweightImgs.put(url, bmp);//Store locally
            return bmp;//return to use
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getSingleImage(String... urls) {//method get single image
        String url = urls[0];//given image URL
        Bitmap bmp = flyweightImgs.get(url);//check if already exists
        if (bmp != null) return bmp;//if already exists - reuse
        try {//if not -> load, store and return
            bmp = BitmapFactory.decodeStream(new URL(url).openStream());//Load remotely
            flyweightImgs.put(url, bmp);//Store locally
            return bmp;//return to use
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

