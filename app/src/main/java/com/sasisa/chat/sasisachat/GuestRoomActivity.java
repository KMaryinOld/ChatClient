package com.sasisa.chat.sasisachat;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;
import com.sasisa.chat.sasisachat.theme.Theme;
import com.sasisa.chat.sasisachat.widget.AccordionView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;

public class GuestRoomActivity extends SasisaAppCompatActivity {

    private ListView roomsList;
    private ListView mafiaList;
    private ListView newsList;
    private Vector<RoomListElement> rooms;
    private Vector<RoomListElement> mafs;
    private Vector<RoomListElement> news;

    private ProgressBar loadingNews;

    private final Object lockUpdatingNews = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_room);
        setTitle("Прихожая");

        TextView helloMessageTextView = (TextView) findViewById(R.id.hello_message_text_view);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy г.", new Locale("ru", "RU"));
        helloMessageTextView.append(df.format(c.getTime()));
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        helloMessageTextView.append(String.format("\r\n%s, %s :)", getMessageFromCurrentHour(currentHour),
                ApplicationSettings.getInstance().getUsername()));

        initializeRoomsVector();
        initializeMafsVector();
        news = new Vector<>();

        newsList = (ListView) findViewById(R.id.newsListView);
        newsList.setAdapter(new GuestRoomListAdapter(this, news, R.drawable.pixnews));

        newsList.setOnItemClickListener(new NewsItemClickListener());
        setListViewHeightBasedOnChildren(newsList);
        newsList.setVisibility(View.INVISIBLE);

        loadingNews = (ProgressBar) findViewById(R.id.loading_guestroom_news);

        mafiaList = (ListView) findViewById(R.id.playMafiaListView);
        mafiaList.setAdapter(new GuestRoomListAdapter(this, mafs, R.drawable.pixmafiagun));

        mafiaList.setOnItemClickListener(new MafsItemClickListener());
        setListViewHeightBasedOnChildren(mafiaList);

        roomsList = (ListView) findViewById(R.id.roomsListView);
        roomsList.setAdapter(new GuestRoomListAdapter(this, rooms, R.drawable.pixchatdialog));

        roomsList.setOnItemClickListener(new RoomItemClickListener());
        setListViewHeightBasedOnChildren(roomsList);

        initializeAttentionDeskVector();

        AdView mAdView = (AdView) findViewById(R.id.adbannerview);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("865593020076244").build();
        mAdView.loadAd(adRequest);

        initializeTheme();
    }

    private void initializeTheme() {
        Theme theme = ApplicationSettings.getInstance().getCurrentTheme();

        findViewById(R.id.guest_room_main_layout).setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomBackgroundColor()));
        AccordionView newsAccordion = (AccordionView) findViewById(R.id.news_accordion_view);
        newsAccordion.setHeaderBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemDark()),
                ContextCompat.getColor(this, theme.getBorderColor()));
        newsAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemDark()));

        AccordionView messagesAccordion = (AccordionView) findViewById(R.id.messages_accordion_view);
        messagesAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemLight()));
        messagesAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemLight()));

        AccordionView mafiaAccordion = (AccordionView) findViewById(R.id.mafia_accordion_view);
        mafiaAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemDark()));
        mafiaAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemDark()));

        AccordionView roomsAccordion = (AccordionView) findViewById(R.id.rooms_accordion_view);
        roomsAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemLight()));
        roomsAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemLight()));

        AccordionView settingsAccordion = (AccordionView) findViewById(R.id.settings_accordion_view);
        settingsAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemDark()));
        settingsAccordion.setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomItemDark()));
    }

    private String getMessageFromCurrentHour(int currentHour) {
        if (currentHour > 6 && currentHour < 11) {
            return "Доброе утро";
        } else if (currentHour >= 11 && currentHour < 17) {
            return "Добрый день";
        } else if (currentHour >= 17 && currentHour < 22) {
            return "Добрый вечер";
        } else {
            return "Доброй ночи";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guest_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Выполняется действие");
        pd.show();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SasisaRestClient.getInstance().logout(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    pd.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    pd.dismiss();
                    Intent intent = new Intent(GuestRoomActivity.this, FullscreenLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void fillRoomsVector(Vector<RoomListElement> rooms) {
        rooms.add(new RoomListElement("Инкогнито", "http://wap.sasisa.ru/chat/chat.php?rm=26"));
        rooms.add(new RoomListElement("Клуб Знатоков", "http://wap.sasisa.ru/chat/chat.php?rm=0"));
        rooms.add(new RoomListElement("Новички", "http://wap.sasisa.ru/chat/chat.php?rm=15"));
        rooms.add(new RoomListElement("->Секс Party<-", "http://wap.sasisa.ru/chat/chat.php?rm=1"));
        rooms.add(new RoomListElement("Кафешка**", "http://wap.sasisa.ru/chat/chat.php?rm=2"));
        rooms.add(new RoomListElement("Гадальня мудальня)", "http://wap.sasisa.ru/chat/chat.php?rm=3"));
        rooms.add(new RoomListElement("MusicRoom", "http://wap.sasisa.ru/chat/chat.php?rm=5"));
        rooms.add(new RoomListElement("Стриптиз-Бар", "http://wap.sasisa.ru/chat/chat.php?rm=6"));
        //rooms.add(new RoomListElement("что, КОМУ, куда?", "http://wap.sasisa.ru/chat/pof.php?rm=7"));
        rooms.add(new RoomListElement("Служивые", "http://wap.sasisa.ru/chat/chat.php?rm=8"));
        //rooms.add(new RoomListElement("Интимная", "http://wap.sasisa.ru/chat/intim.php?rm=10"));
        rooms.add(new RoomListElement("ЗАГС", "http://wap.sasisa.ru/chat/chat.php?rm=12"));
        rooms.add(new RoomListElement("Чердак", "http://wap.sasisa.ru/chat/chat.php?rm=18"));
        rooms.add(new RoomListElement("Беспредел", "http://wap.sasisa.ru/chat/chat.php?rm=9"));
    }

    private void initializeRoomsVector() {
        rooms = new Vector<>();

        fillRoomsVector(rooms);
    }

    private void initializeMafsVector() {
        mafs = new Vector<>();

        fillRoomsVector(mafs);
    }

    public static void fillMafiaVector(Vector<RoomListElement> mafs) {
        mafs.add(new RoomListElement("Русская братва", "http://wap.sasisa.ru/chat/maf.php?rm=1"));
        mafs.add(new RoomListElement("Сицилийская мафия", "http://wap.sasisa.ru/chat/maf.php?rm=2"));
        mafs.add(new RoomListElement("Японские якудзы", "http://wap.sasisa.ru/chat/maf.php?rm=3"));
        mafs.add(new RoomListElement("Китайская триада", "http://wap.sasisa.ru/chat/maf.php?rm=4"));
        mafs.add(new RoomListElement("Африканские каннибалы", "http://wap.sasisa.ru/chat/maf.php?rm=5"));
        mafs.add(new RoomListElement("Колумбийский кортель", "http://wap.sasisa.ru/chat/maf.php?rm=6"));
    }

    private void initializeAttentionDeskVector() {
        SasisaRestClient.getInstance().dwnloadGuestRoomPage(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingNews.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                synchronized (lockUpdatingNews) {
                    news.addAll(Parser.parseNewsList(responseString));
                }
                newsList.requestLayout();
                setListViewHeightBasedOnChildren(newsList);
                newsList.setVisibility(View.VISIBLE);
                loadingNews.setVisibility(View.INVISIBLE);
            }
        });

    }

    private class NewsItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RoomListElement clickedRoom = news.get(position);
            Intent intent = new Intent(GuestRoomActivity.this, NewsActivity.class);
            intent.putExtra("midurl", clickedRoom.getURL());

            startActivity(intent);
        }
    }

    private class MafsItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RoomListElement clickedRoom = mafs.get(position);
            Intent intent = new Intent(GuestRoomActivity.this, RoomActivity.class);
            intent.putExtra("rmurl", clickedRoom.getURL());
            intent.putExtra("title", clickedRoom.getRoomName());
            intent.putExtra("mafia", "play");

            startActivity(intent);
        }
    }

    private class RoomItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RoomListElement clickedRoom = rooms.get(position);
            Intent intent = new Intent(GuestRoomActivity.this, RoomActivity.class);
            intent.putExtra("rmurl", clickedRoom.getURL());
            intent.putExtra("title", clickedRoom.getRoomName());

            startActivity(intent);
        }
    }

    public void viewDialogsOnClick(View v) {
        Intent intent = new Intent(GuestRoomActivity.this, DialogListActivity.class);
        startActivity(intent);
    }

    public void viewSettingsOnClick(View v) {
        Intent intent = new Intent(GuestRoomActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
