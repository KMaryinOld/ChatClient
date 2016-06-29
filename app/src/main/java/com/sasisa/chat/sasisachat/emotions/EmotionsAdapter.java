package com.sasisa.chat.sasisachat.emotions;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by cherry on 24.09.2015.
 */
public class EmotionsAdapter extends BaseAdapter {
    Vector<EmotionObject> mEmotions;
    Context mContext;

    public EmotionsAdapter(Context c, Vector<EmotionObject> emotions) {
        mContext = c;
        mEmotions = emotions;
        Collections.sort(mEmotions, new Comparator<EmotionObject>() {
            @Override
            public int compare(EmotionObject lhs, EmotionObject rhs) {
                return -lhs.getImageName().compareTo(rhs.getImageName());
            }
        });
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
        ImageView view = new ImageView(mContext);
        view.setMinimumWidth(64);
        view.setMinimumHeight(64);
        view.setImageBitmap(mEmotions.get(position).getImageFile());

        return view;
    }
}
