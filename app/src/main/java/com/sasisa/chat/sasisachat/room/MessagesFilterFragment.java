package com.sasisa.chat.sasisachat.room;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.sasisa.chat.sasisachat.ApplicationSettings;
import com.sasisa.chat.sasisachat.HttpGetter;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.RoomActivity;
import com.sasisa.chat.sasisachat.theme.Theme;

public class MessagesFilterFragment extends Fragment {

    public static MessagesFilterFragment newInstance() {
        return new MessagesFilterFragment();
    }

    public MessagesFilterFragment() {
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
        getActivity().findViewById(R.id.blackBackgroundLayout).setVisibility(FrameLayout.VISIBLE);
        View view = inflater.inflate(R.layout.fragment_messages_filter, container, false);
        RadioButton offButton = (RadioButton) view.findViewById(R.id.room_filter_off_radiobutton);
        offButton.setOnClickListener(new ChangeFilterOnClickListener("filtroff"));
        RadioButton privateButton = (RadioButton) view.findViewById(R.id.room_filter_private_radiobutton);
        privateButton.setOnClickListener(new ChangeFilterOnClickListener("filtron"));
        RadioButton fromMeButton = (RadioButton) view.findViewById(R.id.room_filter_from_me_radiobutton);
        fromMeButton.setOnClickListener(new ChangeFilterOnClickListener("filtr2"));
        switch (((RoomActivity)getActivity()).getCurrentMessageFilter()) {
            case 0:
                offButton.setChecked(true);
                break;
            case 1:
                privateButton.setChecked(true);
                break;
            case 2:
                fromMeButton.setChecked(true);
                break;
        }

        Theme theme = ApplicationSettings.getInstance().getCurrentTheme();
        view.setBackgroundColor(ContextCompat.getColor(getContext(), theme.getGuestRoomBackgroundColor()));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ChangeFilterOnClickListener implements View.OnClickListener {
        private String mFilterType;

        public ChangeFilterOnClickListener(String filterType) {
            mFilterType = filterType;
        }

        @Override
        public void onClick(View v) {
            ((RoomActivity) getActivity()).changeFilter(mFilterType);
            getView().findViewById(R.id.change_filter_progressbar).setVisibility(View.VISIBLE);
        }
    }
}
