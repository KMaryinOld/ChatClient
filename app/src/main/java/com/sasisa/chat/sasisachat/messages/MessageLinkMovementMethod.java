package com.sasisa.chat.sasisachat.messages;

import android.content.Intent;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.sasisa.chat.sasisachat.Parser;
import com.sasisa.chat.sasisachat.R;
import com.sasisa.chat.sasisachat.RoomActivity;
import com.sasisa.chat.sasisachat.UserInfoActivity;

import java.util.regex.Pattern;

/**
 * Created by cherry on 07.09.2015.
 */
public class MessageLinkMovementMethod extends LinkMovementMethod {
    private RoomActivity mActivity;

    public MessageLinkMovementMethod(RoomActivity activity) {
        mActivity = activity;
    }
    /*private static volatile MessageLinkMovementMethod instance;

    public static MessageLinkMovementMethod getInstance() {
        MessageLinkMovementMethod localInstance = instance;
        if (localInstance == null) {
            synchronized (MessageLinkMovementMethod.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MessageLinkMovementMethod();
                }
            }
        }
        return localInstance;
    }*/

    @Override
    public boolean onTouchEvent(final TextView widget, Spannable buffer, MotionEvent event ) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            final URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            //ClickableSpan[] s = buffer.getSpans((int)event.getX(), (int)event.getY(), ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if (!link[0].getURL().contains("inside.php")) {
                        return false;
                    }

                    int spanStart = buffer.getSpanStart(link[0]);
                    int spanEnd = buffer.getSpanEnd(link[0]);
                    final CharSequence st = buffer.subSequence(spanStart, spanEnd);

                    PopupMenu userActionPopupMenu = new PopupMenu(widget.getContext(), widget);
                    userActionPopupMenu.inflate(R.menu.menu_clickuseraction);
                    userActionPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            mActivity.setStateShowPopupUserDialog(false);
                        }
                    });
                    userActionPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            String urlLink = link[0].getURL();
                            String userID = "";
                            String[] queryParts = urlLink.split("\\?");
                            queryParts = queryParts[1].split("&");
                            for (String queryPart : queryParts) {
                                String[] leftRight = queryPart.split("=");
                                if (leftRight[0].equals("nk")) {
                                    userID = leftRight[1];
                                    break;
                                }
                            }
                            Parser.URLParameters currentSelectedUser = new Parser.URLParameters(urlLink, userID, st.toString());
                            switch (item.getItemId()) {
                                case R.id.private_message:

                                    if (urlLink.charAt(0) == 'h' && urlLink.charAt(1) == 't' &&
                                            urlLink.charAt(2) == 't' && urlLink.charAt(3) == 'p') {
                                        //do nothing
                                    } else if (urlLink.charAt(0) == '/') {
                                        urlLink = "http://wap.sasisa.ru" + urlLink;
                                    } else {
                                        urlLink = "http://wap.sasisa.ru/chat/" + urlLink;
                                    }
                                    RoomActivity currentRoom = (RoomActivity) widget.getContext();
                                    currentRoom.setPrivateMessage(currentSelectedUser);
                                    break;
                                case R.id.view_user_data:
                                    Intent intent = new Intent(widget.getContext(), UserInfoActivity.class);
                                    intent.putExtra("username", currentSelectedUser.name);
                                    intent.putExtra("userid", currentSelectedUser.value);
                                    widget.getContext().startActivity(intent);
                                    break;
                                default:
                                    return false;
                            }
                            mActivity.setStateShowPopupUserDialog(false);
                            return false;
                        }
                    });
                    userActionPopupMenu.show();

                    mActivity.setStateShowPopupUserDialog(true);

                    Selection.removeSelection(buffer);
                    return true;
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    class SensibleUrlSpan extends URLSpan {
        /** Pattern to match. */
        private Pattern mPattern;

        public SensibleUrlSpan(String url, Pattern pattern) {
            super(url);
            mPattern = pattern;
        }

        public boolean onClickSpan(View widget) {
            boolean matched = mPattern.matcher(getURL()).matches();
            if (matched) {
                super.onClick(widget);
            }
            return matched;
        }
    }

    private class MyClickableSpan extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            String text = ((TextView) widget).getText().toString();
            Log.e("click", text);
        }
    }
}
