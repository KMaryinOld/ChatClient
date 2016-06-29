package com.sasisa.chat.sasisachat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;
import com.sasisa.chat.sasisachat.menu.RoomsListRoomAdapter;

import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class NewsActivity extends SasisaAppCompatActivity {
    private String midURL;
    private TextView contentText;

    private DrawerLayout mDrawerLayout;
    private ListView mRoomsList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Bundle b = getIntent().getExtras();
        midURL = b.getString("midurl");

        initializeDrawerMenu();

        contentText = (TextView) findViewById(R.id.news_content_text);
        contentText.setMovementMethod(new ScrollingMovementMethod());
        final ProgressDialog pd = new ProgressDialog(NewsActivity.this);
        pd.setMessage("Загрузка данных...");
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                pd.dismiss();
                NewsActivity.this.finish();
            }
        });
        pd.show();
        SasisaRestClient.getInstance().dwnloadObiavPage(midURL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
                NewsActivity.this.finish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String HTMLContent = Parser.parseNewsContent(responseString);
                HTMLContent += "<style>body {background-color: #AEEF4D}</style>";
                WebView wv = (WebView) findViewById(R.id.obiavcontent_webview);
                wv.loadDataWithBaseURL("", Parser.replaceALinks(Parser.replaceImageLinks(HTMLContent)), "text/html", "UTF-8", "");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                        if (url != null && url.startsWith("http://")) {
                            new AlertDialog.Builder(NewsActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Открыть ссылку в браузере?")
                                    .setMessage("Хотите открыть ссылку в браузере?")
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            view.getContext().startActivity(
                                                    new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                        }

                                    })
                                    .setNegativeButton("Нет", null)
                                    .show();
                            return true;
                        } else {
                            return true;
                        }
                    }
                });

                pd.dismiss();
            }
        });
    }

    private void initializeDrawerMenu() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_news_drawer_layout);
        mRoomsList = (ListView) findViewById(R.id.rooms_list_activity_room);
        final Vector<RoomListElement> rooms = new Vector<>();
        GuestRoomActivity.fillRoomsVector(rooms);
        mRoomsList.setAdapter(new RoomsListRoomAdapter(this, rooms));
        GuestRoomActivity.setListViewHeightBasedOnChildren(mRoomsList);
        mRoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomListElement clickedRoom = rooms.get(position);
                Intent intent = new Intent(NewsActivity.this, RoomActivity.class);
                intent.putExtra("rmurl", clickedRoom.getURL());
                intent.putExtra("title", clickedRoom.getRoomName());

                startActivity(intent);
                NewsActivity.this.finish();
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
                Intent intent = new Intent(NewsActivity.this, DialogListActivity.class);
                startActivity(intent);
                NewsActivity.this.finish();
            }
        });

        LinearLayout settingsLayout = (LinearLayout) findViewById(R.id.settings_layout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_menu,
                R.string.closed_menu) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("Объявления");
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                getSupportActionBar().setTitle("Объявления");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
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
