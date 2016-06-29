package com.sasisa.chat.sasisachat.userinfo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.UserInfoActivity;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;

import cz.msebera.android.httpclient.Header;

public class PlusReputationFragment extends Fragment {
    private static final String ARG_PLUSURL = "url";

    private String plusURL;

    public static PlusReputationFragment newInstance(String url) {
        PlusReputationFragment fragment = new PlusReputationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLUSURL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public PlusReputationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plusURL = getArguments().getString(ARG_PLUSURL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_plus_reputation, container, false);
        final Button sendButton = (Button) view.findViewById(R.id.pluscommitbutton);
        final Button cancelButton = (Button) view.findViewById(R.id.pluscancelbutton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendButton.setEnabled(false);
                cancelButton.setEnabled(false);

                EditText text = (EditText) view.findViewById(R.id.pluscommentarytext);

                SasisaRestClient.getInstance().commitPlusReputation(plusURL, text.getText().toString(), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        ((UserInfoActivity)getActivity()).hideFragment();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        ((UserInfoActivity)getActivity()).refresh();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UserInfoActivity)getActivity()).hideFragment();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
