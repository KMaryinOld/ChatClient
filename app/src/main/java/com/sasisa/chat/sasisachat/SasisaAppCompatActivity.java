package com.sasisa.chat.sasisachat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sasisa.chat.sasisachat.connection.ApplicationCookies;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;

import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

public class SasisaAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sasisa_app_compat);
        initialize();
    }

    @Override
    protected void onResume() {
        initialize();
        super.onResume();

        List<Cookie> cookies = ApplicationCookies.getInstance().getCookiesStore().getCookies();
        if (cookies.size() < 2) {
            Intent intent = new Intent(this, FullscreenLoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initialize() {
        Context appContext = getApplicationContext();
        ApplicationSettings.getInstance().initialize(appContext);
        ApplicationCookies.getInstance().initialize(appContext);
    }
}
