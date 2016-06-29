package com.sasisa.chat.sasisachat.dialog;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.ViewDialogActivity;

public class NewMessageDialogFragment extends Fragment {

    public static NewMessageDialogFragment newInstance() {
        NewMessageDialogFragment fragment = new NewMessageDialogFragment();
        return fragment;
    }

    public NewMessageDialogFragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_message_dialog, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
