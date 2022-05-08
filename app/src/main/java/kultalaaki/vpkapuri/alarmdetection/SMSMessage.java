/*
 * Created by Kultala Aki on 8/1/21, 7:16 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/1/21, 7:16 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

public class SMSMessage {

    private String sender, message, timeStamp, detectedSender;

    public SMSMessage(String sender, String message, String timeStamp) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
        this.detectedSender = detectedSender(sender);
    }

    private String detectedSender(String sender) {
        // Todo make number recognition here. Compare sender number to those that
        // are saved to SharedPreferences.
        return "member";
    }

    public String getSender() {
        return this.sender;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public String getDetectedSender() { return this.detectedSender; }
}
