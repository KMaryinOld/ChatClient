package com.sasisa.chat.sasisachat.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.ImageCache;
import com.sasisa.chat.sasisachat.Parser;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.UserInfoActivity;
import com.sasisa.chat.sasisachat.messages.URLImageParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class DialogMessagesAdapter extends BaseAdapter {
    private Context mContext;
    private Vector<UserDialogMessage> mMessages;
    private Vector<View> views;

    public DialogMessagesAdapter(Context c, Vector<UserDialogMessage> messages) {
        mContext = c;
        mMessages = messages;

        views = new Vector<>();
        for (UserDialogMessage m : messages) {
            addView(m);
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
        return views.get(position);
    }

    private void addView(final UserDialogMessage currentMessage) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.dialog_list_row, null, false);
        final ImageView avatarView = (ImageView) rowView.findViewById(R.id.user_avatar_image);
        TextView usernameView = (TextView) rowView.findViewById(R.id.username_textview);
        TextView lastMessageView = (TextView) rowView.findViewById(R.id.lastmessage_textview);

        usernameView.setText(currentMessage.username);
        URLImageParser p = new URLImageParser(rowView, mContext);
        String messageText = Parser.replaceImageLinks(currentMessage.messageText);

        SpannableString htmlSpan = new SpannableString(Html.fromHtml(messageText, p, null));

        lastMessageView.setText(htmlSpan);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("userid", currentMessage.userID);
                intent.putExtra("username", currentMessage.username);
                mContext.startActivity(intent);
            }
        });
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    if (currentMessage.imgURL.charAt(0) == 'h' && currentMessage.imgURL.charAt(1) == 't' &&
                            currentMessage.imgURL.charAt(2) == 't' && currentMessage.imgURL.charAt(3) == 'p') {
                        //do nothing
                    } else if (currentMessage.imgURL.charAt(0) == '/') {
                        currentMessage.imgURL = String.format("http://wap.sasisa.ru%s", currentMessage.imgURL);
                    } else {
                        currentMessage.imgURL = String.format("http://wap.sasisa.ru/chat/%s", currentMessage.imgURL);
                    }
                    Bitmap bitmap = ImageCache.getInstance().getBitmap(currentMessage.imgURL);
                    if (bitmap != null) {
                        return bitmap;
                    }
                    URL url = new URL(currentMessage.imgURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    ImageCache.getInstance().addBitmap(currentMessage.imgURL, bitmap);
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
        views.add(rowView);
    }
}
