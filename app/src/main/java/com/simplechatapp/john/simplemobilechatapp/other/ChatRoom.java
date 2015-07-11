package com.simplechatapp.john.simplemobilechatapp.other;

/**
 * Created by John on 7/6/2015.
 */
public class ChatRoom {

    private int roomID;
    private String roomName;

    public ChatRoom(int roomID, String roomName) {
        this.roomID = roomID;
        this.roomName = roomName;
    }

    public ChatRoom() {
        this.roomID = 0;
        this.roomName = "";
    }

    public int getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
