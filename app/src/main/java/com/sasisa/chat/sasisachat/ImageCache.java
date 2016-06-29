package com.sasisa.chat.sasisachat;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

/**
 * Created by cherry on 20.09.2015.
 */
public class ImageCache {
    private static volatile ImageCache instance;

    private HashMap<String, Drawable> drawableHashMap;
    private HashMap<String, Bitmap> bitmapHashMap;

    public static ImageCache getInstance() {
        ImageCache localInstance = instance;
        if (localInstance == null) {
            synchronized (ImageCache.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ImageCache();
                }
            }
        }
        return localInstance;
    }

    private ImageCache() {
        drawableHashMap = new HashMap<>();
        bitmapHashMap = new HashMap<>();
    }

    public void addDrawable(String url, Drawable image) {
        drawableHashMap.put(url, image);
    }

    public void addBitmap(String url, Bitmap image) {
        bitmapHashMap.put(url, image);
    }

    public Drawable getDrawable(String url) {
        return drawableHashMap.get(url);
    }

    public Bitmap getBitmap(String url) {
        return bitmapHashMap.get(url);
    }
}
