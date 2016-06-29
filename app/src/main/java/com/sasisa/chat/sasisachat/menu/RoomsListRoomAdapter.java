package com.sasisa.chat.sasisachat.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.RoomListElement;
import com.sasisa.chat.sasisachat.dialog.UserDialog;

import java.util.Vector;

/**
 * Created by cherry on 21.09.2015.
 */
public class RoomsListRoomAdapter extends BaseAdapter {
    private Context mContext;
    private Vector<RoomListElement> mRooms;

    public RoomsListRoomAdapter(Context c, Vector<RoomListElement> r) {
        mContext = c;
        mRooms = r;
    }

    @Override
    public int getCount() {
        return mRooms.size();
    }

    @Override
    public Object getItem(int position) {
        return mRooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.menu_rooms_list_row, parent, false);
        TextView roomname = (TextView) rowView.findViewById(R.id.stringtext_textview);
        roomname.setText(mRooms.get(position).getRoomName());
        return rowView;
    }

    public void add(RoomListElement room) {
        mRooms.add(room);
        notifyDataSetChanged();
    }
}
