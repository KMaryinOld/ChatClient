package com.sasisa.chat.sasisachat;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sasisa.chat.sasisachat.emotions.EmotionObject;

import java.util.Vector;

/**
 * Created by cherry on 06.09.2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Vector<EmotionObject> mEmotions;

    public ImageAdapter(Context c, Vector<EmotionObject> emotions) {
        mContext = c;
        mEmotions = emotions;
    }

    @Override
    public int getCount() {
        return mEmotions.size();
    }

    @Override
    public Object getItem(int position) {
        return mEmotions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(mEmotions.get(position).getImageFile());

        return imageView;
    }
}
