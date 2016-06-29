package com.sasisa.chat.sasisachat.room;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sasisa.chat.sasisachat.ApplicationSettings;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.theme.Theme;

public class RoomCallModeratorFragment extends Fragment {
    public static RoomCallModeratorFragment newInstance() {
        RoomCallModeratorFragment fragment = new RoomCallModeratorFragment();
        return fragment;
    }

    public RoomCallModeratorFragment() {
        // Required empty public constructor
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
        Theme theme = ApplicationSettings.getInstance().getCurrentTheme();

        View view = inflater.inflate(R.layout.fragment_room_call_moderator, container, false);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), theme.getGuestRoomBackgroundColor()));
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
