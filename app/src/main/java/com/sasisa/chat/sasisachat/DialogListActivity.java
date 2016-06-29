package com.sasisa.chat.sasisachat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;
import com.sasisa.chat.sasisachat.dialog.DialogAdapter;
import com.sasisa.chat.sasisachat.dialog.UserDialog;
import com.sasisa.chat.sasisachat.menu.RoomsListRoomAdapter;
import com.sasisa.chat.sasisachat.theme.Theme;

import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class DialogListActivity extends SasisaAppCompatActivity {
    private ListView mDialogsList;
    private ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private DrawerLayout mDrawerLayout;
    private ListView mRoomsList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_list);

        initializeDrawerMenu();

        mDialogsList = (ListView) findViewById(R.id.dialog_list);
        downloadDialogs();
        mDialogsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDialog dialog = (UserDialog) parent.getItemAtPosition(position);
                Intent intent = new Intent(DialogListActivity.this, ViewDialogActivity.class);
                intent.putExtra("sel", dialog.getUserID());

                startActivity(intent);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.loaddialoglist_progressbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshdialoglist);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                downloadDialogs();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(R.color.blue, R.color.green, R.color.yellow, R.color.red);

        initializeTheme();
    }

    private void initializeTheme() {
        Theme theme = ApplicationSettings.getInstance().getCurrentTheme();

        findViewById(R.id.acrivity_dialog_drawer_layout).setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomBackgroundColor()));
    }

    private void initializeDrawerMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.acrivity_dialog_drawer_layout);
        mRoomsList = (ListView) findViewById(R.id.rooms_list_activity_room);
        final Vector<RoomListElement> rooms = new Vector<>();
        GuestRoomActivity.fillRoomsVector(rooms);
        mRoomsList.setAdapter(new RoomsListRoomAdapter(this, rooms));
        GuestRoomActivity.setListViewHeightBasedOnChildren(mRoomsList);
        mRoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomListElement clickedRoom = rooms.get(position);
                Intent intent = new Intent(DialogListActivity.this, RoomActivity.class);
                intent.putExtra("rmurl", clickedRoom.getURL());
                intent.putExtra("title", clickedRoom.getRoomName());

                startActivity(intent);
                DialogListActivity.this.finish();
            }
        });

        LinearLayout backToGuestroomLayout = (LinearLayout) findViewById(R.id.backtoguestroom_layout);
        backToGuestroomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout viewMessagesLayout = (LinearLayout) findViewById(R.id.viewmessages_layout);
        viewMessagesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialogListActivity.this, DialogListActivity.class);
                startActivity(intent);
                DialogListActivity.this.finish();
            }
        });

        LinearLayout settingsLayout = (LinearLayout) findViewById(R.id.settings_layout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialogListActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_menu,
                R.string.closed_menu);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void downloadDialogs(){
        SasisaRestClient.getInstance().dwnloadNewsPage(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Vector<UserDialog> dialogs = Parser.parseUserDialogs(responseString);
                DialogAdapter adapter = new DialogAdapter(DialogListActivity.this, dialogs);
                mDialogsList.setAdapter(adapter);

                mProgressBar.setVisibility(View.GONE);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_dialog_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
