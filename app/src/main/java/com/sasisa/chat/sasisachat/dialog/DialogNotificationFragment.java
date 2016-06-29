package com.sasisa.chat.sasisachat.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.ApplicationSettings;
import com.sasisa.chat.sasisachat.DialogListActivity;
import com.sasisa.chat.sasisachat.GlobalMediaPlayer;
import com.sasisa.chat.sasisachat.Parser;
import com.sasisa.chat.sasisachat.R;

public class DialogNotificationFragment extends Fragment{
    private int currentNewMessagesCount;
    private ImageView backgroundImage;
    private TextView countMessagesText;

    public static DialogNotificationFragment newInstance() {
        DialogNotificationFragment fragment = new DialogNotificationFragment();
        return fragment;
    }

    public DialogNotificationFragment() {
        currentNewMessagesCount = 0;
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
        View view = inflater.inflate(R.layout.fragment_dialog_notification, container, false);
        backgroundImage = (ImageView) view.findViewById(R.id.background_image_view);
        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DialogListActivity.class);
                startActivity(intent);
            }
        });
        countMessagesText = (TextView) view.findViewById(R.id.count_messages_text);

        view.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void checkCountMessagesFromPage(String page) {
        int count = Parser.parseMessagesCount(page);
        if (count != currentNewMessagesCount) {
            updateCountMessages(count);
        }
    }

    private void updateCountMessages(int count) {
        countMessagesText.setText(String.valueOf(count));

        try {
            if (count > currentNewMessagesCount) {
                getView().setVisibility(View.VISIBLE);
                if (ApplicationSettings.getInstance().isPlayMusic()) {
                    GlobalMediaPlayer.getInstance().playAudio(getActivity().getAssets().openFd("music/new_dialog.mp3"));
                }
            } else if (count == 0) {
                getView().setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            GlobalMediaPlayer.getInstance().releaseMP();
        }
        currentNewMessagesCount = count;
    }
}
