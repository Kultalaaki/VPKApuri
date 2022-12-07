/*
 * Created by Kultala Aki on 8/1/21, 7:16 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/1/21, 7:16 PM
 */

package kultalaaki.vpkapuri.alarms;

/**
 * SMS message object.
 */
public class SMSMessage {

    private String sender;
    private final String message, timeStamp;
    private int senderID = 0;

    public SMSMessage(String sender, String message, String timeStamp) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    /**
     *
     * @return Sender of message.
     */
    public String getSender() {
        return this.sender;
    }

    /**
     *
     * @return Message text.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     *
     * @return Timestamp when system received message
     */
    public String getTimeStamp() {
        return this.timeStamp;
    }

    /**
     *
     * @param sender Set sender number again after formatting.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     *
     * @param id AlarmNumberDetector.numberID is responsible of determining id.
     */
    public void setSenderID(int id) {
        this.senderID = id;
    }

    /**
     *
     * @return Id is used for determining who is sender. Fire rescue sender, vapepa alarm sender or
     * member coming to alarm
     */
    public int getSenderID() {
        return this.senderID;
    }
}
