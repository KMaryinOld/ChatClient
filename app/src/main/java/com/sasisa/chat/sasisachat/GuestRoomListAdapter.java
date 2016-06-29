package com.sasisa.chat.sasisachat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.dialog.UserDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by cherry on 22.09.2015.
 */
public class GuestRoomListAdapter extends BaseAdapter {
    private Context mContext;
    private Vector<RoomListElement> mRows;
    private int mBackgroundResource;

    public GuestRoomListAdapter(Context c, Vector<RoomListElement> rows, int backgroundResource) {
        mContext = c;
        mRows = rows;
        mBackgroundResource = backgroundResource;
    }

    @Override
    public int getCount() {
        return mRows.size();
    }

    @Override
    public Object getItem(int position) {
        return mRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.menu_rooms_list_row, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.piximagedialog);
        imageView.setBackgroundResource(mBackgroundResource);
        TextView textView = (TextView) rowView.findViewById(R.id.stringtext_textview);

        RoomListElement currentRow = mRows.get(position);
        textView.setText(currentRow.getRoomName());

        return rowView;
    }

    public void add(RoomListElement row) {
        mRows.add(row);
        notifyDataSetChanged();
    }
}
