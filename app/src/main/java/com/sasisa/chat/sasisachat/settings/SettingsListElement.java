package com.sasisa.chat.sasisachat.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cherry on 27.09.2015.
 */
public class SettingsListElement {
    public static final int SETTINGS_ELEMENT_SEEKBAR = 0;
    public static final int SETTINGS_ELEMENT_SPINNER = 1;

    public String name;
    public String showName;
    public int type;
    public Object value;
    public List<?> parameters;

    public SettingsListElement(String name, String showName, int type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.showName = showName;
        this.parameters = new ArrayList<>();
    }

    public SettingsListElement(String name, String showName, int type, Object value, List<?> parameters) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.showName = showName;
        this.parameters = new ArrayList<>();
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return name;
    }
}
