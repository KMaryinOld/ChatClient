package com.sasisa.chat.sasisachat.emotions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by cherry on 06.09.2015.
 */
public class EmotionObject {
    private Bitmap mImageFile;
    private String mImageName;

    public EmotionObject(String imageName, Bitmap bitmap) {
        mImageName = imageName;
        mImageFile = bitmap;
    }

    public Bitmap getImageFile() {
        return mImageFile;
    }

    public String getImageName() {
        return mImageName;
    }
}
