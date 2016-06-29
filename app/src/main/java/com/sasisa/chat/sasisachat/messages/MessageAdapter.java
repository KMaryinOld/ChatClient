package com.sasisa.chat.sasisachat.messages;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.ApplicationSettings;
import com.sasisa.chat.sasisachat.HttpGetter;
import com.sasisa.chat.sasisachat.Parser;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.RoomActivity;
import com.sasisa.chat.sasisachat.connection.SasisaRestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by cherry on 07.09.2015.
 */
public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private Vector<TextView> mMessages;

    public MessageAdapter(Context c, Vector<String> messages) {
        mContext = c;
        mMessages = new Vector<>();
        for (String s : messages) {
            mMessages.add(createTextViewWithString(s));
        }
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mMessages.get(position);
    }

    public void add(String message) {
        if (ApplicationSettings.getInstance().isRevertChatText()) {
            mMessages.insertElementAt(createTextViewWithString(message), 0);
            //if (mMessages.size() > 100) {
            //    mMessages.remove(mMessages.size() - 1);
            //}
        } else {
            mMessages.add(createTextViewWithString(message));
            //if (mMessages.size() > 100) {
            //    mMessages.remove(0);
            //}
        }
        notifyDataSetChanged();
    }

    private TextView createTextViewWithString(String text) {
        TextView textView = new TextView(mContext);
        URLImageParser p = new URLImageParser(textView, mContext);
        textView.setTextIsSelectable(true);

        List<String> usernames = Parser.getUsernamesFromText(text);
        List<String> textAdmins = new ArrayList<>();
        for (String u : usernames) {
            for (String a : ApplicationSettings.getInstance().getAdministrationList()) {
                if (u.contains(a)) {
                    textAdmins.add(a);
                }
            }
        }

        SpannableString htmlSpan = new SpannableString(Html.fromHtml(text, p, null));
        for (String u : textAdmins) {
            int startIndex = htmlSpan.toString().indexOf(u);
            int endIndex = startIndex + u.length();
            htmlSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, ApplicationSettings.
                            getInstance().getCurrentTheme().getTitleTextColor())),startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        String username = ApplicationSettings.getInstance().getUsername();

        textView.setText(htmlSpan);

        textView.setMovementMethod(new MessageLinkMovementMethod((RoomActivity) mContext));

        if (Parser.isHaveUnderlineUsername(text, ApplicationSettings.getInstance().getUsername())) {
            textView.setBackgroundColor(0xFFFFC311);
        } else if (text.contains(username)) {
            textView.setBackgroundColor(ContextCompat.getColor(mContext,
                    ApplicationSettings.getInstance().getCurrentTheme().getUsernameTextColor()));
        }
        return textView;
    }
}