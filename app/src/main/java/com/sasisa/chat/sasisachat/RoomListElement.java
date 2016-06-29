package com.sasisa.chat.sasisachat;

/**
 * Created by cherry on 31.08.2015.
 */
public class RoomListElement {
    private boolean isOpened;
    private String roomName;
    private String url;
    private String peoplesCount;

    public RoomListElement(String roomName, String url) {
        this.roomName = roomName;
        this.url = url;
        peoplesCount = "0";
    }

    public void setOpened(boolean state) {
        isOpened = state;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public void setPeoplesCount(String peoplesCount) {
        this.peoplesCount = peoplesCount;
    }

    public String getPeoplesCount() {
        return peoplesCount;
    }

    @Override
    public String toString() {
        return roomName;
    }
}
