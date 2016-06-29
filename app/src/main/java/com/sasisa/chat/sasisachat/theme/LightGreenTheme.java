package com.sasisa.chat.sasisachat.theme;

import com.sasisa.chat.sasisachat.R;

public class LightGreenTheme extends Theme {
    public LightGreenTheme() {
        this.titleBackgroundColor = R.color.titlebackgroundcolor_LightGreen;
        this.titleTextColor = R.color.titletextcolor_LightGreen;
        this.borderColor = R.color.border_color_LightGreen;
        this.roomChatColor = R.color.chatroom_color_LightGreen;
        this.guestRoomTextColor = R.color.guestroom_text_color_LightGreen;
        this.guestRoomBackgroundColor = R.color.guestroom_background_color_LightGreen;
        this.guestRoomSeparatorColor = R.color.guestroom_separator_color_LightGreen;
        this.guestRoomItemDark = R.color.guestroom_item_darkgreen_LightGreen;
        this.guestRoomItemLight = R.color.guestroom_item_lightgreen_LightGreen;
        
        this.usernameTextColor = R.color.username_chat_color;
    }

    public String toString() {
        return "Светло-зеленая тема";
    }
}
