package com.sasisa.chat.sasisachat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cherry on 31.08.2015.
 */
public class HttpGetter {
    public static final int SUCCESS_AUTORIZATION = 0;
    public static final int SERVER_CONNECTION_ERROR = 1;
    public static final int INCORRECT_LOGIN_PASS = 2;

    public String username = "";

    private List<String> cookies;
    private static volatile HttpGetter instance;

    public static HttpGetter getInstance() {
        HttpGetter localInstance = instance;
        if (localInstance == null) {
            synchronized (HttpGetter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new HttpGetter();
                }
            }
        }
        return localInstance;
    }

    private HttpGetter() {
    }
}
