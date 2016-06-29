package com.sasisa.chat.sasisachat;

import android.content.Context;
import android.content.SharedPreferences;

import com.sasisa.chat.sasisachat.theme.LightBlueTheme;
import com.sasisa.chat.sasisachat.theme.LightGreenTheme;
import com.sasisa.chat.sasisachat.theme.Theme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cherry on 27.09.2015.
 */
public class ApplicationSettings {
    private Context mContext;

    private boolean playMusic = true;
    private boolean revertChatText = false;
    private String username;
    private int currentTheme;

    private List<String> administrationList;
    private List<Theme> applicationThemes;

    private static volatile ApplicationSettings instance;

    public static ApplicationSettings getInstance() {
        ApplicationSettings localInstance = instance;
        if (localInstance == null) {
            synchronized (ApplicationSettings.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ApplicationSettings();
                }
            }
        }
        return localInstance;
    }

    private ApplicationSettings() {
    }

    public void initialize(Context context) {
        mContext = context;

        SharedPreferences preferences = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        username = preferences.getString("username", "");
        playMusic = preferences.getBoolean("playmusic", true);
        revertChatText = preferences.getBoolean("revertchattext", true);
        currentTheme = preferences.getInt("currenttheme", 0);

        administrationList = new ArrayList<>();
        initializeAdministrationList();

        applicationThemes = new ArrayList<>();
        initializeApplicationThemes();
    }

    private void initializeAdministrationList() {
        String[] administration = mContext.getResources().getStringArray(R.array.administration_list);
        for (String s : administration) {
            administrationList.add(s);
        }
    }

    private void initializeApplicationThemes() {
        applicationThemes.add(new LightGreenTheme());
        applicationThemes.add(new LightBlueTheme());
    }

    public boolean isPlayMusic() {
        //SharedPreferences.Editor editor = getSharedPreferences(element.name, Context.MODE_PRIVATE).edit();
        //editor.putBoolean(element.name, (Boolean)element.value);
        return playMusic;
    }

    public void setPlayingMusic(boolean value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putBoolean("playmusic", value).apply();
        playMusic = value;
    }

    public boolean isRevertChatText() {
        return revertChatText;
    }

    public void setRevertChatText(boolean value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putBoolean("revertchattext", value).apply();
        revertChatText = value;
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putString("username", username).apply();
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getAdministrationList() {
        return administrationList;
    }

    public Theme getCurrentTheme() {
        return applicationThemes.get(currentTheme);
    }

    public List<Theme> getApplicationThemes() {
        return applicationThemes;
    }

    public int getCurrentThemeNumber() {
        return currentTheme;
    }

    public void setCurrentTheme(int currentTheme) {
        this.currentTheme = currentTheme;
        SharedPreferences.Editor editor = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putInt("currenttheme", currentTheme).apply();
    }
}
