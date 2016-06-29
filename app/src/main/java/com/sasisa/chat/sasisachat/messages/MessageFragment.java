package com.sasisa.chat.sasisachat.messages;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private String mMessageText;

    public static MessageFragment newInstance(String messageText) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("messageText", messageText);
        fragment.setArguments(args);
        return fragment;
    }

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessageText = getArguments().getString("messageText");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        TextView tw = (TextView)view.findViewById(R.id.fragment_message_text);
        tw.setText(mMessageText);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
    }

    @Override
    public String toString() {
        return mMessageText;
    }
}
