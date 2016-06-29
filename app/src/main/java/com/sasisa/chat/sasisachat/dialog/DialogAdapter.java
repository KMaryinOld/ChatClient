package com.sasisa.chat.sasisachat.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.ImageCache;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.UserInfoActivity;
import com.sasisa.chat.sasisachat.ViewDialogActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by cherry on 16.09.2015.
 */
public class DialogAdapter extends BaseAdapter {
    private Context mContext;
    private Vector<UserDialog> mDialogs;

    public DialogAdapter(Context c, Vector<UserDialog> dialogs) {
        mContext = c;
        mDialogs = dialogs;
    }

    @Override
    public int getCount() {
        return mDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return mDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.dialog_list_row, parent, false);
        final ImageView avatarView = (ImageView) rowView.findViewById(R.id.user_avatar_image);
        TextView usernameView = (TextView) rowView.findViewById(R.id.username_textview);
        TextView lastMessageView = (TextView) rowView.findViewById(R.id.lastmessage_textview);

        final UserDialog currentDialog = mDialogs.get(position);

        usernameView.setText(currentDialog.getUsername());
        lastMessageView.setText(currentDialog.getLastMessageText());
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("userid", currentDialog.getUserID());
                intent.putExtra("username", currentDialog.getUsername());
                mContext.startActivity(intent);
            }
        });
        if (currentDialog.isNewMessage()) {
            //((RelativeLayout)lastMessageView.getParent()).setBackgroundResource(R.color.guestroom_item_lightgreen);
        }
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = ImageCache.getInstance().getBitmap(currentDialog.getAvatarURL());
                if (bitmap != null) {
                    return bitmap;
                }
                try {
                    URL url = new URL(currentDialog.getAvatarURL());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    ImageCache.getInstance().addBitmap(currentDialog.getAvatarURL(), bitmap);
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    avatarView.setImageBitmap(bitmap);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);

        return rowView;
    }

    public void add(UserDialog dialog) {
        mDialogs.add(dialog);
        notifyDataSetChanged();
    }
}
