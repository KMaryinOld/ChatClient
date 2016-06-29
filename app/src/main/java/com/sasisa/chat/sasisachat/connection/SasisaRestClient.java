package com.sasisa.chat.sasisachat.connection;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cherry on 28.09.2015.
 */
public class SasisaRestClient {
    private static final String GOODWIN_SERVER = "http://goodwin.airsoftru.com";
    private static final int REQUEST_TIMEOUT = 30*1000;
    private AsyncHttpClient client;
    private SyncHttpClient syncClient;

    private static volatile SasisaRestClient instance;

    public static SasisaRestClient getInstance() {
        SasisaRestClient localInstance = instance;
        if (localInstance == null) {
            synchronized (SasisaRestClient.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SasisaRestClient();
                }
            }
        }
        return localInstance;
    }

    private SasisaRestClient() {
        client = new AsyncHttpClient();
        syncClient = new SyncHttpClient();
        initializeClient();
    }

    private void initializeClient() {
        client.setTimeout(REQUEST_TIMEOUT);
        client.setEnableRedirects(true);
        client.setCookieStore(ApplicationCookies.getInstance().getCookiesStore());

        syncClient.setTimeout(REQUEST_TIMEOUT);
        syncClient.setEnableRedirects(true);
        syncClient.setCookieStore(ApplicationCookies.getInstance().getCookiesStore());
    }

    public void autorizeUser(String login, String pass, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("id", login);
        params.add("ps", pass);
        client.post("http://wap.sasisa.ru/auth_sasisa.php", params, responseHandler);
    }

    public void dwnloadObiavPage(String midURL, ResponseHandlerInterface responseHandler) {
        client.get(midURL, responseHandler);
    }

    public void dwnloadGuestRoomPage(ResponseHandlerInterface responseHandler) {
        client.get("http://wap.sasisa.ru/chat/enter.php", responseHandler);
    }

    public void dwnloadNewsPage(ResponseHandlerInterface responseHandler) {
        client.get("http://wap.sasisa.ru/chat/im.php", responseHandler);
    }

    public void dwnloadUserInfoPage(String userid, ResponseHandlerInterface responseHandler) {
        client.get(String.format("http://wap.sasisa.ru/chat/inside.php?nk=%s", userid), responseHandler);
    }

    public void dwnloadDialogUserPage(String sel, ResponseHandlerInterface responseHandler) {
        client.get(String.format("http://wap.sasisa.ru/chat/im.php?sel=%s", sel), responseHandler);
    }

    public void sendDialogMessage(String sel, String text, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("msg", text);

        client.post(String.format("http://wap.sasisa.ru/chat/im.php?action=send&sel=%s", sel), params, responseHandler);
    }

    public void callModerator(String rm, String message, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("text", message);
        params.add("trun", "0");

        client.post(String.format("http://wap.sasisa.ru/chat/moder_help.php?rm=%s", rm), params, responseHandler);
    }

    public void changeTopic(String rm, String topic, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("top", topic);
        params.add("top2t", "0");
        client.post(String.format("http://wap.sasisa.ru/chat/topic.php?rm=%s", rm), params, responseHandler);
    }

    public void sendChatMessage(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        client.post(url, params, responseHandler);
    }

    public void getRoomPage(String url, ResponseHandlerInterface responseHandler) {
        syncClient.setCookieStore(ApplicationCookies.getInstance().getCookiesStore());
        syncClient.get(url, responseHandler);
    }

    public void getRoomPageAsync(String url, ResponseHandlerInterface responseHandler) {
        client.get(url, responseHandler);
    }

    public void dwnloadUsersRoomList(String roomURL, ResponseHandlerInterface responseHandler) {
        String rm = "0";
        Pattern p = Pattern.compile("rm=([\\d]+)");
        Matcher m = p.matcher(roomURL);
        if (m.find()) {
            rm = m.group(1);
        }
        client.get(String.format("http://wap.sasisa.ru/chat/whoroom.php?rm=%s", rm), responseHandler);
    }

    public void logout(ResponseHandlerInterface responseHandler) {
        client.get("http://wap.sasisa.ru/chat/enter.php?&site_logout=1", responseHandler);
    }

    public void commitPlusReputation(String url, String why, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.put("why", why);
        client.post(url, params, responseHandler);
    }

    public void loadGhostroomMessages(int offset, ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.put("action", "get_messages");
        params.put("offset", offset);
        client.get(String.format("%s/rest_api/sasisa/", GOODWIN_SERVER), params, responseHandler);
    }
}
