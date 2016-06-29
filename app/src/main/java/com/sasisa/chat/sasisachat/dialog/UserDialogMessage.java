package com.sasisa.chat.sasisachat.dialog;

/**
 * Created by cherry on 19.09.2015.
 */
public class UserDialogMessage {
    public String imgURL = "";
    public String username = "";
    public String messageText = "";
    public String userID = "";

    @Override
    public String toString() {
        return imgURL + " - " + username + " - " + messageText + " - " + userID;
    }
}
