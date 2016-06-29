package com.sasisa.chat.sasisachat;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.settings.SettingsListAdapter;
import com.sasisa.chat.sasisachat.settings.SettingsListElement;
import com.sasisa.chat.sasisachat.theme.Theme;

import java.util.Vector;

public class SettingsActivity extends SasisaAppCompatActivity {
    private boolean activitySeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Vector<SettingsListElement> settings = new Vector<>();
        settings.add(new SettingsListElement("playmusic", "Воспроизводить звуки", SettingsListElement.SETTINGS_ELEMENT_SEEKBAR,
                ApplicationSettings.getInstance().isPlayMusic()));
        settings.add(new SettingsListElement("revertmessages", "Реверсивно отображать сообщения чата", SettingsListElement.SETTINGS_ELEMENT_SEEKBAR,
                ApplicationSettings.getInstance().isRevertChatText()));

        settings.add(new SettingsListElement("currenttheme", "Цветовая гамма", SettingsListElement.SETTINGS_ELEMENT_SPINNER,
                ApplicationSettings.getInstance().getCurrentThemeNumber(), ApplicationSettings.getInstance().getApplicationThemes()));

        ListView lw = (ListView) findViewById(R.id.settings_listview);
        lw.setAdapter(new SettingsListAdapter(this, settings));

        findViewById(R.id.view_licenses_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, LicensesActivity.class);
                startActivity(intent);
            }
        });

        initializeTheme();

        activitySeen = false;
    }

    private void initializeTheme() {
        Theme theme = ApplicationSettings.getInstance().getCurrentTheme();
        findViewById(R.id.settings_activity_layout).setBackgroundColor(ContextCompat.getColor(this, theme.getGuestRoomBackgroundColor()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (activitySeen) {
                Intent intent = new Intent(getApplicationContext(), GuestRoomActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void makeActivity() {
        activitySeen = true;
    }
}
