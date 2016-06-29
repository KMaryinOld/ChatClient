package com.sasisa.chat.sasisachat.GhostRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GhostRoomListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Map<Integer, Message> mMessages;

    public GhostRoomListAdapter(Context context, List<Message> messageList) {
        mInflater = LayoutInflater.from(context);

        mMessages = new TreeMap<>();
        for (Message message : messageList) {
            mMessages.put(message.id, message);
        }
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.values().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private class ViewHolder {

    }
}
