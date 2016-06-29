package com.sasisa.chat.sasisachat.dialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cherry on 15.09.2015.
 */
public class UserDialog {
    private String mUserID;
    private String mUserImageURL;
    private String mUsername;

    private String mLastMessageImgURL;
    private String mLastMessageText;
    private boolean isNewMessage;

    public UserDialog(String userid, String tableContent) {
        mUserID = userid;
        initializeContentDialog(tableContent);
    }

    private void initializeContentDialog(String tableContent) {
        initializeUserImageURL(tableContent);
        initializeUsername(tableContent);
        initializeDialogLastMessage(tableContent);
    }

    private void initializeUserImageURL(String tableContent) {
        Pattern p = Pattern.compile("<img class=\"im_avatar_small\" src=\"(.+?)\" alt=\" \" />", Pattern.DOTALL);
        Matcher m = p.matcher(tableContent);
        if (m.find()) {
            mUserImageURL = m.group(1);
            if (mUserImageURL.charAt(0) == 'h' && mUserImageURL.charAt(1) == 't' && mUserImageURL.charAt(2) == 't' &&
                    mUserImageURL.charAt(3) == 'p') {
                //do nothing
            } else if (mUserImageURL.charAt(0) == '/') {
                mUserImageURL = String.format("http://wap.sasisa.ru%s", mUserImageURL);
            } else {
                mUserImageURL = String.format("http://wap.sasisa.ru/chat/%s", mUserImageURL);
            }
        } else {
            mUserImageURL = "http://placehold.it/50";
        }
    }

    private void initializeUsername(String tableContent) {
        Pattern p = Pattern.compile("<b>(.+?)</b>", Pattern.DOTALL);
        Matcher m = p.matcher(tableContent);
        if (m.find()) {
            mUsername = m.group(1);
        } else {
            mUsername = "User";
        }
    }

    private void initializeDialogLastMessage(String tableContent) {
        Pattern p = Pattern.compile("<td colspan=\"2\">(.+?)</td>", Pattern.DOTALL);
        Matcher m = p.matcher(tableContent);
        if (m.find()) {
            String content = m.group(1);

            p = Pattern.compile("<img src=\"(.+?)\" alt", Pattern.DOTALL);
            m = p.matcher(content);
            if (m.find()) {
                mLastMessageImgURL = m.group(1);
            } else {
                mLastMessageImgURL = "/chat/message_in.png";
            }

            p = Pattern.compile("<span class=\"text\">(.+?)</span>", Pattern.DOTALL);
            m = p.matcher(content);
            if (m.find()) {
                mLastMessageText = m.group(1).replaceAll("[\\s]{2,}", " ");
            } else {
                mLastMessageText = "";
            }

            if (content.contains("<div class=\"c2\"") || tableContent.contains("/chat/bullet.png")) {
                isNewMessage = true;
            } else {
                isNewMessage = false;
            }
        }
    }

    public String getAvatarURL() {
        return mUserImageURL;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getLastMessageText() {
        return mLastMessageText;
    }

    public String getUserID() {
        return mUserID;
    }

    public boolean isNewMessage() {
        return isNewMessage;
    }

    @Override
    public String toString() {
        return mUsername + " - " + mLastMessageText;
    }
}
