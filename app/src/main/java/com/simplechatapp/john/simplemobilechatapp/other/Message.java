package com.simplechatapp.john.simplemobilechatapp.other;

/**
 * Created by John on 5/20/2015.
 */
public class Message {

    private String message;
    private String sender;
    private boolean isSelf;

    public Message(String message, String sender) {

        this.message = message;
        this.sender = sender;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public boolean isSelf() {
        return this.isSelf;
    }
    /**
     * Get the message
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Get the name of the sender of the message
     * @return the name of the sender of the message
     */
    public String getSender() {
        return this.sender;
    }
}
