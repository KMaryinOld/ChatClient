package com.sasisa.chat.sasisachat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;
import com.sasisa.chat.sasisachat.dialog.DialogNotificationFragment;
import com.sasisa.chat.sasisachat.emotions.EmotionsPopupFragment;
import com.sasisa.chat.sasisachat.menu.RoomsListRoomAdapter;
import com.sasisa.chat.sasisachat.messages.MessageAdapter;
import com.sasisa.chat.sasisachat.room.MessagesFilterFragment;
import com.sasisa.chat.sasisachat.room.RoomCallModeratorFragment;
import com.sasisa.chat.sasisachat.room.RoomChangeTopicFragment;
import com.sasisa.chat.sasisachat.room.RoomUsersListFragment;
import com.sasisa.chat.sasisachat.theme.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class RoomActivity extends SasisaAppCompatActivity {
    private final int FILTER_OFF = 0;
    private final int FILTER_PRIVATE = 1;
    private final int FILTER_FROM_ME = 2;

    private String roomURL;
    private ListView mChatMessagesList;
    private MessageAdapter mMessagesAdapter;
    private EditText mMessageText;

    private String lastMessage;
    private boolean isMessageToUser;
    private String userIDmessageTo;
    private Spinner mMessageType;
    private boolean playMusic;
    private boolean isMafiaRoom;
    private boolean userScrollList;
    private boolean firstLoading;
    private boolean userDialogPopupMenuShowed;
    private DialogNotificationFragment countNewDialogMessagesFragment;

    private final Object lockUpdatingMessage = new Object();
    private final Object lockCheckingEmptyText = new Object();
    private final Object lockTimerTask = new Object();

    //UsersList objects
    private boolean listIsShowed;
    private RoomUsersListFragment listFragment;
    private RoomChangeTopicFragment changeTopicFragment;
    private boolean callModeratorIsShowed;
    private RoomCallModeratorFragment callModeratorFragment;
    private MessagesFilterFragment messagesFilterFragment;

    private boolean emotionsGridIsShowed;
    private boolean messagesFilterIsShowed;

    //drawer menu items
    private DrawerLayout mDrawerLayout;
    private ListView mRoomsList;
    private ListView mMafiaList;
    private ActionBarDrawerToggle mDrawerToggle;
    //Drawer Title
    private CharSequence drawerTitle;
    //Activity Title
    private CharSequence activityTitle;

    private boolean changeTopicIsShowed;
    private boolean canChangeTopic;
    private boolean canCallModerator;

    private int currentMessageFilter;

    private UpdateMessagesProcess updateProcess;
    private EmptyPrivateMessageChecker emptyPrivateMessageChecker;

    private InterstitialAd mInterstitialAd;
    private static final long INTERSTITIAL_AD_DELAY = 300;
    private long adSleepTime;
    private long lastTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        lastMessage = "";
        listIsShowed = false;
        listFragment = null;
        changeTopicFragment = null;
        callModeratorFragment = null;
        playMusic = ApplicationSettings.getInstance().isPlayMusic();
        isMafiaRoom = false;
        emotionsGridIsShowed = false;
        userScrollList = false;
        firstLoading = true;

        Bundle b = getIntent().getExtras();
        roomURL = b.getString("rmurl");
        String title = b.getString("title", "Комната");
        setTitle(title);
        if (b.getString("mafia") != null) {
            isMafiaRoom = true;
        }

        canCallModerator = false;
        canChangeTopic = false;
        changeTopicIsShowed = false;
        callModeratorIsShowed = false;
        messagesFilterIsShowed = false;
        userDialogPopupMenuShowed = false;
        drawerTitle = "Чат Sasisa.ru";
        activityTitle = "Комната";
        currentMessageFilter = 0;

        countNewDialogMessagesFragment = DialogNotificationFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.dialog_notification_layout, countNewDialogMessagesFragment).commit();

        mChatMessagesList = (ListView) findViewById(R.id.messages_list_view);
        mMessagesAdapter = new MessageAdapter(this, new Vector<String>());
        mChatMessagesList.setAdapter(mMessagesAdapter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.acrivity_room_drawer_layout);
        mRoomsList = (ListView) findViewById(R.id.rooms_list_activity_room);
        final Vector<RoomListElement> rooms = new Vector<>();
        GuestRoomActivity.fillRoomsVector(rooms);
        mRoomsList.setAdapter(new RoomsListRoomAdapter(this, rooms));
        GuestRoomActivity.setListViewHeightBasedOnChildren(mRoomsList);
        mRoomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomListElement clickedRoom = rooms.get(position);
                Intent intent = new Intent(RoomActivity.this, RoomActivity.class);
                intent.putExtra("rmurl", clickedRoom.getURL());
                intent.putExtra("title", clickedRoom.getRoomName());

                startActivity(intent);
                RoomActivity.this.finish();
            }
        });

        mMafiaList = (ListView) findViewById(R.id.mafia_list_activity_room);
        final Vector<RoomListElement> mafs = new Vector<>();
        GuestRoomActivity.fillMafiaVector(mafs);
        mMafiaList.setAdapter(new RoomsListRoomAdapter(this, mafs));
        GuestRoomActivity.setListViewHeightBasedOnChildren(mMafiaList);
        mMafiaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomListElement clickedRoom = mafs.get(position);
                Intent intent = new Intent(RoomActivity.this, RoomActivity.class);
                intent.putExtra("rmurl", clickedRoom.getURL());
                intent.putExtra("title", clickedRoom.getRoomName());
                intent.putExtra("mafia", "play");

                startActivity(intent);
                RoomActivity.this.finish();
            }
        });

        LinearLayout backToGuestroomLayout = (LinearLayout) findViewById(R.id.backtoguestroom_layout);
        backToGuestroomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToGuestRoom();
            }
        });

        LinearLayout viewMessagesLayout = (LinearLayout) findViewById(R.id.viewmessages_layout);
        viewMessagesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomActivity.this, DialogListActivity.class);
                startActivity(intent);
                RoomActivity.this.finish();
            }
        });

        LinearLayout settingsLayout = (LinearLayout) findViewById(R.id.settings_layout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_menu,
                R.string.closed_menu) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(activityTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                getSupportActionBar().setTitle(drawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //mUpdateTimer = Executors.newSingleThreadScheduledExecutor();
        //resultScheduledTask = mUpdateTimer.scheduleWithFixedDelay(mUpdateTask, 0, 5000, TimeUnit.MILLISECONDS);

        mMessageText = (EditText) findViewById(R.id.room_chat_text_editor);
        mMessageText.setOnTouchListener(new MessageTextOnTouchListener());

        findViewById(R.id.blackBackgroundLayout).setVisibility(FrameLayout.GONE);
        mMessageType = (Spinner) findViewById(R.id.message_type_spinner);
        mMessageType.setEnabled(false);
        isMessageToUser = false;

        //Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(checkerEmptyPrivateMessage, 0, 1, TimeUnit.MILLISECONDS);

        mChatMessagesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (ApplicationSettings.getInstance().isRevertChatText() && firstVisibleItem == 0 ||
                        !ApplicationSettings.getInstance().isRevertChatText() && firstVisibleItem + visibleItemCount == totalItemCount) {
                    userScrollList = false;
                } else {
                    userScrollList = true;
                }
            }
        });
        /*mChatMessagesList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.e("Scroll params", scrollX + ":" + scrollY);
                Log.e("List count", "" + mMessagesAdapter.getCount());
            }
        });*/

        /*AdView mAdView = (AdView) findViewById(R.id.roombottombanner);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("865593020076244").build();
        mAdView.loadAd(adRequest);*/

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1581341295962339/4921324407");
        adSleepTime = 0;

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                adSleepTime = 0;
                //requestNewInterstitial();
            }
        });

        //requestNewInterstitial();

        initializeTheme();
    }

    private void initializeTheme() {
        Theme theme = ApplicationSettings.getInstance().getCurrentTheme();

        findViewById(R.id.messages_layout).setBackgroundColor(ContextCompat.getColor(this, theme.getRoomChatColor()));
        findViewById(R.id.room_drawer_menu_layout).setBackgroundColor(ContextCompat.getColor(this, theme.getTitleBackgroundColor()));
    }

    private void updateChatList(final String page, final ProgressBar pb, Handler uiHandler) {
        page.replaceAll("<img src=\"/chat/images/sas.png\" alt=\" \" />", "");
        final String bannedMessage = Parser.getBannedMessage(page);

        if (page.contains(">Топик</a>")) {
            canChangeTopic = true;
        } else {
            canChangeTopic = false;
        }
        if (page.contains("Вызвать модера")) {
            canCallModerator = true;
        } else {
            canCallModerator = false;
        }
        final String title = Parser.parsePageTitle(page);
        final List<String> appendingText = getChatRoomCurrentText(page);

        if (page.contains("<b>&#8226; Выключен</b>")) {
            currentMessageFilter = FILTER_OFF;
        } else if (page.contains("<b>&#8226; Только приватные</b>")) {
            currentMessageFilter = FILTER_PRIVATE;
        } else if (page.contains("<b>&#8226; Мои или Адресованные на мой ник</b>")) {
            currentMessageFilter = FILTER_FROM_ME;
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
        for (String s : appendingText) {
            if (playMusic && !firstLoading) {
                if (Parser.isHaveUnderlineUsername(s, ApplicationSettings.getInstance().getUsername())) {
                    try {
                        GlobalMediaPlayer.getInstance().playAudio(getAssets().openFd("music/message.mp3"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.GONE);
                mChatMessagesList.setVisibility(View.VISIBLE);

                for (String s : appendingText) {
                    mMessagesAdapter.add(s);
                }

                if (bannedMessage != null) {
                    Toast.makeText(RoomActivity.this, bannedMessage, Toast.LENGTH_LONG).show();
                    //loadingDialog.cancel();
                    RoomActivity.this.finish();

                    return;
                }
                if (!userScrollList && !userDialogPopupMenuShowed) {
                    if (ApplicationSettings.getInstance().isRevertChatText()) {
                        mChatMessagesList.setSelection(0);
                    } else {
                        mChatMessagesList.setSelection(mChatMessagesList.getCount() - 1);
                    }
                }

                if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    activityTitle = title;
                    RoomActivity.this.getSupportActionBar().setTitle(title);
                }

                if (adSleepTime > INTERSTITIAL_AD_DELAY) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
                countNewDialogMessagesFragment.checkCountMessagesFromPage(page);
            }
        });


        firstLoading = false;

        long currentTimastamp = System.currentTimeMillis() / 1000L;
        if (lastTimestamp == 0) {
            lastTimestamp = currentTimastamp;
        }
        adSleepTime += currentTimastamp - lastTimestamp;
        lastTimestamp = currentTimastamp;
    }

    @Override
    protected void onPause() {
        stopThreads();
        super.onPause();
    }

    @Override
    protected void onStop() {
        stopThreads();
        super.onStop();
    }

    @Override
    protected void onResume() {
        runThreads();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isMafiaRoom) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_room, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        if (listFragment != null) {
            manager.remove(listFragment);
            listFragment = null;
        }
        if (changeTopicFragment != null) {
            manager.remove(changeTopicFragment);
            changeTopicFragment = null;
        }
        if (callModeratorFragment != null) {
            manager.remove(callModeratorFragment);
            callModeratorFragment = null;
        }
        if (messagesFilterFragment != null) {
            manager.remove(messagesFilterFragment);
            messagesFilterFragment = null;
        }
        manager.commit();

        FrameLayout bbl = (FrameLayout) findViewById(R.id.blackBackgroundLayout);
        switch (id) {
            case R.id.room_menu_action_show_users:
                bbl.setVisibility(View.VISIBLE);
                listFragment = RoomUsersListFragment.newInstance(roomURL);
                getSupportFragmentManager().beginTransaction().add(R.id.room_users_list_layout, listFragment, "userslist").commit();
                listIsShowed = true;
                break;
            case R.id.room_menu_action_back:
                backToGuestRoom();
                break;
            case R.id.room_menu_action_change_topic:
                if (!canChangeTopic) {
                    Toast.makeText(this, "Не хватает постов для смены топика", Toast.LENGTH_SHORT).show();
                } else {
                    bbl.setVisibility(View.VISIBLE);
                    changeTopicFragment = RoomChangeTopicFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().add(R.id.room_users_list_layout,
                            changeTopicFragment, "changetopic").commit();
                    changeTopicIsShowed = true;
                }
                break;
            case R.id.room_menu_action_call_moder:
                if (!canCallModerator) {
                    Toast.makeText(this, "Вы не можете вызвать модератора :(", Toast.LENGTH_SHORT).show();
                } else {
                    bbl.setVisibility(View.VISIBLE);
                    callModeratorFragment = RoomCallModeratorFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().add(R.id.room_users_list_layout,
                            callModeratorFragment, "callmoderator").commit();
                    callModeratorIsShowed = true;
                }
                break;
            case R.id.room_menu_action_messages_filter:
                bbl.setVisibility(View.VISIBLE);
                messagesFilterFragment = MessagesFilterFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.room_users_list_layout, messagesFilterFragment,
                        "filterfragment").commit();
                messagesFilterIsShowed = true;
                break;
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideUsersListFragment() {
        getSupportFragmentManager().beginTransaction().remove(listFragment).commit();
        listFragment = null;
        findViewById(R.id.blackBackgroundLayout).setVisibility(FrameLayout.GONE);
        listIsShowed = false;
    }

    public void hideEmotionsFragment() {
        if (emotionsGridIsShowed) {
            getFragmentManager().beginTransaction().remove(EmotionsPopupFragment.getInstance()).commit();
            emotionsGridIsShowed = false;
        }
    }

    public void hideEmotionsGrid() {
        hideEmotionsFragment();
        findViewById(R.id.room_emotions_layout).setVisibility(View.GONE);
    }

    public void cancelChangeTopic(View view) {
        hideChangeTopicFragment();
    }

    public void hideChangeTopicFragment() {
        getSupportFragmentManager().beginTransaction().remove(changeTopicFragment).commit();
        changeTopicFragment = null;
        findViewById(R.id.blackBackgroundLayout).setVisibility(FrameLayout.GONE);
        changeTopicIsShowed = false;
    }

    public void cancelCallModerator(View view) {
        hideCallModeratorFragment();
    }

    public void hideCallModeratorFragment() {
        getSupportFragmentManager().beginTransaction().remove(callModeratorFragment).commit();
        callModeratorFragment = null;
        findViewById(R.id.blackBackgroundLayout).setVisibility(FrameLayout.GONE);
        callModeratorIsShowed = false;
    }

    public void hideMessagesFilterFragment() {
        getSupportFragmentManager().beginTransaction().remove(messagesFilterFragment).commit();
        messagesFilterFragment = null;
        findViewById(R.id.blackBackgroundLayout).setVisibility(FrameLayout.GONE);
        messagesFilterIsShowed = false;
    }

    @Override
    public boolean onTouchEvent( MotionEvent event ) {
        return super.onTouchEvent(event);
    }

    private List<String> getChatRoomCurrentText(String page) {
        synchronized (lockUpdatingMessage) {

            String chatText = "";
            if (isMafiaRoom) {
                Pattern p = Pattern.compile("<div class=\"c1\">(.+?)</div>(.+?)<div class=\"c1\">(.+?)</div>", Pattern.DOTALL);
                Matcher m = p.matcher(page);
                if (m.find()) {
                    chatText = m.group(2);
                }
            } else {
                int startIndex = page.indexOf("Обновить</a>");
                int endIndex = page.indexOf("<div class=\"c1\"><a href=\"history.php?");
                if (endIndex == -1) {
                    endIndex = page.indexOf("<div class=\"c1\"><a href=\"enter.php?");
                }
                if (startIndex > -1 && endIndex > -1) {
                    chatText = page.substring(startIndex + 29, endIndex);
                }
            }

            //lass="c1"><b><a href="inside.php?rm=8&amp;nk=2147&amp;r=11238">[Система]</a></b>(18:37:45)&gt;<b> <i> <a href="/chat/intim.php?rm=10">Предлагаем пройти увлекательный квест от клуба Я и моя Семья! Ключ в интим \&quot;семья\&quot;</a> </i> </b> (Клуб: <a href="/chat/clubs/club.php?cid=21">*Я и моя Семья* </a>)<br /></div><div class="c10"><b><a href="inside.php?rm=8&amp;nk=2484036&amp;r=11238">АААрмия</a></b>(18:27:33)&gt;ррв<br /></div><div class="c1"><b><a href="inside.php?rm=8&amp;nk=2484036&amp;r=11238">АААрмия</a></b>(18:27:23)&gt;Все ми привет<br /></div><div class="c10"><b><a href="inside.php?rm=8&amp;nk=2147&amp;r=11238">[Система]</a></b>(18:22:10)&gt;<b> <i> <a href="/chat/intim.php?rm=10">Стартовал квест от клуба \&quot;Российская Федерация\&quot; Ключ в интим \&quot;Россия\&quot;</a> </i> </b> (Клуб: <a href="/chat/clubs/club.php?cid=45">Российская Федерация</a>)<br /></div><div class="c1"><b><a href="inside.php?rm=8&amp;nk=2147&amp;r=11238">[Система]</a></b>(18:20:37)&gt;<b> •••Стартовал квест от Реал клуба... Первый ключ в интим комнату --- РЕАЛ --- ответы принимаются пока не исчезнут вопросы. Удачи!!!•••</b> (Клуб: <a href="/chat/clubs/club.php?cid=70">Real_Life_Club (Собрание в 21.00 по Мск.)</a>)<br /></div><div class="c10"><b><a href="inside.php?rm=8&amp;nk=0&amp;r=11238">[Система]</a></b>(18:17:59)&gt;<br /> <a href="/chat/inside.php?nk=2463878">маччохамачо</a> и <a href="/chat/inside.php?nk=555853">(ЛИЯ)</a> <img src="/foto/foto/a817571/1408935322_6475.gif" alt=" " /> <br />  <i> Мы вам, супруги молодые, <br />  Желаем счастья и добра, <br />  Пусть будет светлою дорога, <br />  Пусть будет дружною семья! </i><br /></div><div class="c1"><b><a href="inside.php?rm=8&amp;nk=2147&amp;r=11238">[Система]</a></b>(18:15:39)&gt;<b>Стартовал Квест от Секс Клуба. Ключ от интима \&quot;69\&quot;...Удачки всем) </b> (Клуб: <a href="/chat/clubs/club.php?cid=17">SEX CLUB </a>)<br /></div><div class="c10"><b><a href="inside.php?rm=8&amp;nk=9999&amp;r=11238"></a></b>(18:12:21)&gt;В комнату вошёл <a href="/chat/inside.php?nk=2484811&amp;rm=8">Брат*Луны</a><br /></div><div class="c1"><b><a href="inside.php?rm=8&amp;nk=2147&amp;r=11238">[Система]</a></b>(18:03:41)&gt;<b>Стартовал <a href="/chat/clubs/events.php?cid=68&amp;mod=event&amp;id=11800"><i>квест!</i></a> Первый ключ в комнату интим - <u>Comedy</u> .Всем удачи ;)</b> (Клуб: <a href="/chat/clubs/club.php?cid=68">Comedy Club™</a>)<br /></div><div class="c10"><b><a href="inside.php?rm=8&amp;nk=249485&amp;r=11238">Приколист)</a></b>(18:02:01)&gt;- Правду лучше всего говорить из танка.<br /></div><div class="c1"><b><a href="inside.php?rm=8&amp;nk=9999&amp;r=11238"></a></b>(17:59:05)&gt;В комнату вошёл <a href="/chat/inside.php?nk=2484036&amp;rm=8">АААрмия</a><br /></div><div class="c10"><b><a href="inside.php?rm=8&amp;nk=1071482&amp;r=11238"><img src="/chat/images/sas.png" alt=" " /> Эмпатия</a></b>(17:52:35)&gt;<p align="center"><u><a style="text-decoration: underline" href="http://wap.sasisa.ru/forum/postslist.php?p=312121">http://wap.sasisa.ru/forum/postslist.php?p=312121</a> Активна Викторина форума &quot; Шансоновские посиделки&quot; <img src="smiles_fuuu/nya.png" alt=",nya," /></u></p><br /></div><div class="c1"><b><a href="inside.php?rm=8&amp;nk=9999&amp;r=11238"></a></b>(17:51:59)&gt;В комнату вошёл <a href="/chat/inside.php?nk=2464012&amp;rm=8">кар

            List<String> messages = Parser.parseMessages(chatText);
            List<String> returnMessages = new ArrayList<>();
            for (int i = messages.size() - 1; i >= 0; --i) {
                //returnMessages.add(Html.fromHtml(messages.get(i)).toString());
                returnMessages.add(messages.get(i));
                int startMessagePos = messages.get(i).indexOf("&gt;", 0);
                int endMessagePos = messages.get(i).lastIndexOf("div class=");
                String currentMessage;
                if (startMessagePos != -1) {
                    if (endMessagePos != -1) {
                        currentMessage = messages.get(i).substring(startMessagePos + 4, endMessagePos);
                    } else {
                        currentMessage = messages.get(i).substring(startMessagePos + 4);
                    }
                } else {
                    currentMessage = messages.get(i);
                }
                if (currentMessage.equals(lastMessage)) {
                    returnMessages.clear();
                }
            }
            if (messages.size() > 0) {
                int startMessagePos = messages.get(0).indexOf("&gt;", 0);
                int endMessagePos = messages.get(0).lastIndexOf("div class=");
                String currentMessage;
                if (startMessagePos != -1) {
                    if (endMessagePos != -1) {
                        currentMessage = messages.get(0).substring(startMessagePos + 4, endMessagePos);
                    } else {
                        currentMessage = messages.get(0).substring(startMessagePos + 4);
                    }
                } else {
                    currentMessage = messages.get(0);
                }
                lastMessage = currentMessage;
            }

            return returnMessages;
        }
    }

    private void sendMessage() {
        RequestParams params = new RequestParams();
        final String text = mMessageText.getText().toString();
        if (isMessageToUser) {
            String prvt;
            if (mMessageType.getSelectedItem().toString().equals("Всем")) {
                prvt = "0";
            } else {
                prvt = "1";
            }
            params.add("prvt", prvt);
            params.add("towhom", userIDmessageTo);
        }
        mMessageText.setText("");

        params.add("page_sm", "");
        params.add("msg", text);
        SasisaRestClient.getInstance().sendChatMessage(roomURL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });

        isMessageToUser = false;
        userIDmessageTo = "";
        mMessageType.setEnabled(false);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onClickSmilesButton(View view) {
        View focusView = RoomActivity.this.getCurrentFocus();
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (!emotionsGridIsShowed) {
            findViewById(R.id.room_emotions_layout).setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction().add(R.id.room_emotions_layout, EmotionsPopupFragment.getInstance()).commit();
            emotionsGridIsShowed = true;
        } else {
            hideEmotionsGrid();
        }
    }

    private class MessageTextOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                View view = RoomActivity.this.getCurrentFocus();
                /*if(event.getRawX() <= mMessageText.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()) {
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if (!emotionsGridIsShowed) {
                        findViewById(R.id.room_emotions_layout).setVisibility(View.VISIBLE);
                        getFragmentManager().beginTransaction().add(R.id.room_emotions_layout, EmotionsPopupFragment.getInstance()).commit();
                        emotionsGridIsShowed = true;
                    } else {
                        hideEmotionsGrid();
                    }
                    return true;
                } else {*/
                    if (emotionsGridIsShowed) {
                        hideEmotionsGrid();
                    }
                    if (event.getRawX() >= (mMessageText.getRight() - mMessageText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        sendMessage();
                        return true;
                    } else {
                        if (view != null) {
                            mMessageText.requestFocus();
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(mMessageText, InputMethodManager.SHOW_IMPLICIT);
                            return false;
                        }
                    }
                //}
            }
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (listIsShowed) {
                hideUsersListFragment();
                return true;
            } else if (changeTopicIsShowed) {
                hideChangeTopicFragment();
                return true;
            } else if (callModeratorIsShowed) {
                hideCallModeratorFragment();
                return true;
            } else if (messagesFilterIsShowed) {
                hideMessagesFilterFragment();
                return true;
            }
            if (emotionsGridIsShowed) {
                hideEmotionsGrid();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setPrivateMessage(Parser.URLParameters username) {
        synchronized (lockCheckingEmptyText) {
            try {
                EditText messageText = (EditText) findViewById(R.id.room_chat_text_editor);
                messageText.setText(String.format("%s, %s", username.name, messageText.getText()));
                messageText.setSelection(messageText.getText().length());
                findViewById(R.id.message_type_spinner).setEnabled(true);
                isMessageToUser = true;
                userIDmessageTo = username.value;

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(messageText, InputMethodManager.SHOW_IMPLICIT);
            } catch (NullPointerException e) {
                //System.out.println("User is headass");
            }
        }
    }

    public void unsetPrivateMessage() {
        View v = findViewById(R.id.message_type_spinner);
        if (v != null) {
            v.setEnabled(false);
            isMessageToUser = false;
            userIDmessageTo = "";
        }
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

    private void backToGuestRoom() {
        finish();
    }

    public void roomChangeTopic(View view) {
        if (changeTopicFragment != null) {
            changeTopicIsShowed = false;

            View v = changeTopicFragment.getView();
            final EditText text = (EditText) v.findViewById(R.id.topicname_edittext);
            Button changeButton = (Button) v.findViewById(R.id.changetopic_buttonchange);
            changeButton.setEnabled(false);
            Button backbutton = (Button) v.findViewById(R.id.changetopic_buttonback);
            backbutton.setEnabled(false);

            ProgressBar pb = (ProgressBar) findViewById(R.id.change_topic_progressbar);
            pb.setVisibility(View.VISIBLE);

            String[] params = roomURL.split("[&,?]");
            String rm = "0";
            for (String param : params) {
                String[] splitparams = param.split("=");
                if (splitparams[0].equals("rm")) {
                    rm = splitparams[1];
                }
            }
            SasisaRestClient.getInstance().changeTopic(rm, text.getText().toString(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onSuccess(statusCode, headers, responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }
    }

    public void roomCallModerator(View view) {
        if (callModeratorFragment != null) {
            callModeratorIsShowed = false;

            View v = callModeratorFragment.getView();
            final EditText text = (EditText) v.findViewById(R.id.messagetoadmin_edittext);
            Button callButton = (Button) v.findViewById(R.id.buttoncallmoder);
            callButton.setEnabled(false);
            Button backbutton = (Button) v.findViewById(R.id.buttoncancelcallmoder);
            backbutton.setEnabled(false);

            ProgressBar pb = (ProgressBar) findViewById(R.id.call_moderator_progressbar);
            pb.setVisibility(View.VISIBLE);
            String[] params = roomURL.split("[&,?]");
            String rm = "0";
            for (String param : params)
            {
                String[] splitparams = param.split("=");
                if (splitparams[0].equals("rm")) {
                    rm = splitparams[1];
                }
            }

            SasisaRestClient.getInstance().callModerator(rm, text.getText().toString(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onSuccess(statusCode, headers, responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }
    }

    @Override
    public void finish() {
        //resultScheduledTask.cancel(true);
        stopThreads();
        super.finish();
    }

    public void addEmotion(String emotion) {
        mMessageText.setText(String.format("%s .%s.", mMessageText.getText(), emotion));
        mMessageText.setSelection(mMessageText.length());
        getFragmentManager().beginTransaction().remove(EmotionsPopupFragment.getInstance()).commit();
        findViewById(R.id.room_emotions_layout).setVisibility(View.GONE);
    }

    public int getCurrentMessageFilter() {
        return currentMessageFilter;
    }

    public void changeFilter(final String filterType) {
        SasisaRestClient.getInstance().getRoomPageAsync(String.format("%s&%s", roomURL, filterType), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                startActivity(getIntent());
                finish();
            }
        });
    }


    private class UpdateMessagesProcess extends Thread {
        volatile boolean stopRequested = false;
        volatile boolean running = true;

        private ProgressBar pb;
        private Handler uiHandler;

        public UpdateMessagesProcess(ProgressBar pb, Handler uiHandler) {
            this.pb = pb;
            this.uiHandler = uiHandler;
        }

        public void stopThread() {
            stopRequested = true;
            while( running ) {
                updateProcess.interrupt();
                Thread.yield();
            }
        }

        public void run() {
            while(!stopRequested ) {
                synchronized (lockTimerTask) {
                    SasisaRestClient.getInstance().getRoomPage(String.format("%s&knopki", roomURL), new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            updateChatList(responseString, pb, uiHandler);
                        }
                    });
                }
            }
            running = false;
        }
    }

    private class EmptyPrivateMessageChecker extends Thread {
        volatile boolean stopRequested = false;
        volatile boolean running = true;

        private Handler uiHandler;

        public EmptyPrivateMessageChecker(Handler uiHandler) {
            this.uiHandler = uiHandler;
        }

        public void stopThread() {
            stopRequested = true;
            while( running ) {
                emptyPrivateMessageChecker.interrupt();
                Thread.yield();
            }
        }

        public void run() {
            while(!stopRequested ) {
                if (mMessageText.getText().length() == 0) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lockCheckingEmptyText) {
                                unsetPrivateMessage();
                            }
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
            running = false;
        }
    }

    private void runThreads() {
        final Handler uiHandler = new Handler();
        final ProgressBar pb = (ProgressBar) findViewById(R.id.loadingmessages_progressbar);
        if (updateProcess == null) {
            updateProcess = new UpdateMessagesProcess(pb, uiHandler);
            updateProcess.start();
        }

        if (emptyPrivateMessageChecker == null) {
            emptyPrivateMessageChecker = new EmptyPrivateMessageChecker(uiHandler);
            emptyPrivateMessageChecker.start();
        }
    }

    private void stopThreads() {
        if (updateProcess != null) {
            updateProcess.stopThread();
            updateProcess = null;
        }

        if (emptyPrivateMessageChecker != null) {
            emptyPrivateMessageChecker.stopThread();
            emptyPrivateMessageChecker = null;
        }
    }

    public void setStateShowPopupUserDialog(boolean state) {
        userDialogPopupMenuShowed = state;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
