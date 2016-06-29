package com.sasisa.chat.sasisachat.theme;

import com.sasisa.chat.sasisachat.R;

/**
 * Created by cherry on 09.12.2015.
 */
public class LightBlueTheme extends Theme {
    public LightBlueTheme() {
        this.titleBackgroundColor = R.color.titlebackgroundcolor_LightBlue;
        this.titleTextColor = R.color.titletextcolor_LightBlue;
        this.borderColor = R.color.border_color_LightBlue;
        this.roomChatColor = R.color.chatroom_color_LightBlue;
        this.guestRoomBackgroundColor = R.color.guestroom_background_color_LightBlue;
        this.guestRoomSeparatorColor = R.color.guestroom_separator_color_LightBlue;
        this.guestRoomItemDark = R.color.guestroom_item_darkblue_LightBlue;
        this.guestRoomItemLight = R.color.guestroom_item_lightblue_LightBlue;

        this.usernameTextColor = R.color.username_chat_color;
    }

    public String toString() {
        return "Светло-голубая тема";
    }
}
