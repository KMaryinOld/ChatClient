package com.sasisa.chat.sasisachat.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.ApplicationSettings;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.SettingsActivity;
import com.sasisa.chat.sasisachat.theme.Theme;

import java.util.List;
import java.util.Vector;

/**
 * Created by cherry on 27.09.2015.
 */
public class SettingsListAdapter extends BaseAdapter{
    private Context context;
    private Vector<SettingsListElement> settings;

    public SettingsListAdapter(Context c, Vector<SettingsListElement> elements) {
        context = c;
        settings = elements;
    }

    @Override
    public int getCount() {
        return settings.size();
    }

    @Override
    public Object getItem(int position) {
        return settings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final SettingsListElement element = settings.get(position);
        View rowView = null;
        if (element.type == SettingsListElement.SETTINGS_ELEMENT_SEEKBAR) {
            rowView = inflater.inflate(R.layout.settings_row_swith, parent, false);
            TextView settingName = (TextView) rowView.findViewById(R.id.settings_row_spinner_textview);
            settingName.setText(element.showName);

            Switch sswitch = (Switch) rowView.findViewById(R.id.settings_row_spinner_switch);
            sswitch.setChecked((Boolean) element.value);
            sswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    element.value = isChecked;
                    switch (element.name) {
                        case "playmusic":
                            ApplicationSettings.getInstance().setPlayingMusic(isChecked);
                            break;
                        case "revertmessages":
                            ApplicationSettings.getInstance().setRevertChatText(isChecked);
                            break;
                    }
                    ((SettingsActivity) context).makeActivity();
                }
            });
        } else if (element.type == SettingsListElement.SETTINGS_ELEMENT_SPINNER) {
            rowView = inflater.inflate(R.layout.settings_row_spinner, parent, false);
            Spinner spinner = (Spinner) rowView.findViewById(R.id.choosing_theme_spinner);
            List<Theme> themes = (List<Theme>) element.parameters;
            spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, themes));
            spinner.setPrompt(element.showName);
            spinner.setSelection(ApplicationSettings.getInstance().getCurrentThemeNumber());
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ApplicationSettings.getInstance().setCurrentTheme(position);
                    ((SettingsActivity) context).makeActivity();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        return rowView;
    }
}
