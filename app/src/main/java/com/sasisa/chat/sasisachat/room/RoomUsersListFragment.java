package com.sasisa.chat.sasisachat.room;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.HttpGetter;
import com.sasisa.chat.sasisachat.Parser;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.RoomActivity;
import com.sasisa.chat.sasisachat.UserInfoActivity;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RoomUsersListFragment extends ListFragment {
    private final int PRIVATE_MESSAGE_ID = 0;
    private final int SHOW_USER_INFO = 1;

    private String mRoomUrl;
    private List<Parser.URLParameters> mList;
    private Parser.URLParameters currentSelectedUser;

    private OnFragmentInteractionListener mListener;

    private ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    public static RoomUsersListFragment newInstance(String roomURL) {
        RoomUsersListFragment fragment = new RoomUsersListFragment();
        Bundle args = new Bundle();
        args.putString("roomURL", roomURL);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RoomUsersListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mList = null;
        currentSelectedUser = null;
        if (getArguments() != null) {
            mRoomUrl = getArguments().getString("roomURL");
        }

        SasisaRestClient.getInstance().dwnloadUsersRoomList(mRoomUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                List<Parser.URLParameters> usersInfoList = Parser.parseUsersPage(responseString);
                Collections.sort(usersInfoList, new Comparator<Parser.URLParameters>() {
                    @Override
                    public int compare(Parser.URLParameters o1, Parser.URLParameters o2) {
                        return o1.name.compareTo(o2.name);
                    }
                });
                setListAdapter(new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, usersInfoList.toArray()));
                mList = usersInfoList;
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Parser.URLParameters obj = (Parser.URLParameters) lv.getItemAtPosition(acmi.position);
        currentSelectedUser = obj;

        menu.setHeaderTitle(obj.name);
        menu.add(0, PRIVATE_MESSAGE_ID, 0, "Написать приватное сообщение");
        menu.add(0, SHOW_USER_INFO, 1, "Информация о пользователе");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (currentSelectedUser != null) {
            RoomActivity activity = (RoomActivity) getActivity();
            activity.hideUsersListFragment();
            if (item.getItemId() == PRIVATE_MESSAGE_ID) {
                activity.setPrivateMessage(currentSelectedUser);

                currentSelectedUser = null;
                return true;
            } else {
                Intent intent = new Intent(activity, UserInfoActivity.class);
                intent.putExtra("userid", currentSelectedUser.value);
                startActivity(intent);
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list_view, container, false);

        view.setBackgroundColor(Color.WHITE);
        registerForContextMenu(((RelativeLayout) view).getChildAt(0));

        progressBar = (ProgressBar) view.findViewById(R.id.loading_userslist_progress);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
        }

        if (mList != null) {
            RoomActivity activity = (RoomActivity) RoomUsersListFragment.this.getActivity();
            activity.setPrivateMessage(mList.get(position));
            activity.hideUsersListFragment();
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }
}
