package com.sasisa.chat.sasisachat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sasisa.chat.sasisachat.connection.ApplicationCookies;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends SasisaAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationCookies.getInstance().initialize(getApplicationContext());
        ApplicationSettings.getInstance().initialize(getApplicationContext());
        setTitle("SasisaChat");
        Intent intent = new Intent(this, FullscreenLoginActivity.class);
        //intent.putExtra("rmurl", "1");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void enterButtonOnClick(View v) {
        EditText loginText = (EditText) findViewById(R.id.loginText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
    }
}
