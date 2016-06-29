package com.sasisa.chat.sasisachat.emotions;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.RoomActivity;

import java.io.IOException;
import java.util.Vector;

public class EmotionsPopupFragment extends Fragment {
    private static volatile EmotionsPopupFragment instance;

    public static EmotionsPopupFragment getInstance() {
        EmotionsPopupFragment localInstance = instance;
        if (localInstance == null) {
            synchronized (EmotionsPopupFragment.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new EmotionsPopupFragment();
                }
            }
        }
        return localInstance;
    }

    public EmotionsPopupFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emotions_popup, container, false);
        GridView emotionsGrid = (GridView) v.findViewById(R.id.emotionsgrid);
        String[] list;
        try {
            list = getActivity().getAssets().list("emotions");
        } catch (IOException e) {
            list = new String[0];
        }
        final Vector<EmotionObject> emotions = new Vector<>();
        for (String name : list) {
            try {
                emotions.add(new EmotionObject(name, BitmapFactory.decodeStream(getActivity().getAssets().open("emotions/" + name))));
            } catch (Exception e) {

            }
        }
        emotionsGrid.setAdapter(new EmotionsAdapter(getActivity(), emotions));
        emotionsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EmotionObject currentEmotion = emotions.get(position);
                String emotionName = currentEmotion.getImageName().substring(currentEmotion.getImageName().indexOf('_') + 1,
                        currentEmotion.getImageName().lastIndexOf('.'));
                Activity activity = getActivity();
                if (activity instanceof RoomActivity) {
                    ((RoomActivity) activity).addEmotion(emotionName);
                    ((RoomActivity) activity).hideEmotionsFragment();
                }
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
