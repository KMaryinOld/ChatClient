package com.sasisa.chat.sasisachat;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.sasisa.chat.sasisachat.GhostRoom.GhostRoomActivity;
import com.sasisa.chat.sasisachat.GhostRoom.GhostRoomListAdapter;
import com.sasisa.chat.sasisachat.connection.ApplicationCookies;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;

import cz.msebera.android.httpclient.Header;

public class FullscreenLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context appContext = getApplicationContext();
        ApplicationSettings.getInstance().initialize(appContext);
        ApplicationCookies.getInstance().initialize(appContext);

        if (ApplicationCookies.getInstance().getCookiesStore().getCookies().size() > 1) {
            Intent intent = new Intent(this, GhostRoomActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_login);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void enterButtonOnClick(View v) {
        final EditText loginText = (EditText) findViewById(R.id.loginText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);

        final ProgressDialog pd = new ProgressDialog(FullscreenLoginActivity.this);
        pd.setMessage("Выполняется вход...");
        pd.show();
        SasisaRestClient.getInstance().autorizeUser(loginText.getText().toString(), passwordText.getText().toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
                Toast.makeText(FullscreenLoginActivity.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                pd.dismiss();
                if (responseString.contains("Авторизация прошла успешно")) {
                    ApplicationSettings.getInstance().setUsername(loginText.getText().toString());
                    Intent intent = new Intent(FullscreenLoginActivity.this, GuestRoomActivity.class);
                    startActivity(intent);
                    finish();
                } else if (responseString.contains("Пользователь с таким логином не найден") ||
                        responseString.contains("Неверный логин или пароль")) {
                    Toast.makeText(FullscreenLoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
