/*
 * Created by Kultala Aki on 8/1/21, 7:16 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/1/21, 7:16 PM
 */

package kultalaaki.vpkapuri.alarm;

public class SMSMessage {

    private String sender;
    private String message;
    private String received;

    public SMSMessage(String sender, String message, String received) {
        this.sender = sender;
        this.message = message;
        this.received = received;
    }

    public String getSender() {
        return this.sender;
    }

    public String getMessage() {
        return this.message;
    }

    public String getReceived() {
        return this.received;
    }
}
