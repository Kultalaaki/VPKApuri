/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "firealarm_table")
public class FireAlarm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String tunnus; // 401, 402, 403 etc.
    private String luokka; // A, B, C, D
    private String viesti; // message
    private String osoite; // address
    private String kommentti; // Comment, added from archive if user writes something
    private String vastaus; // What is answered to this alarmdetection message. Not in use yet.
    private String timeStamp; // Self explanatory
    private String optionalField2; // Stores phone number
    private String optionalField3;
    private String optionalField4;
    private String optionalField5;

    public FireAlarm(String tunnus, String luokka, String viesti, String osoite, String kommentti, String vastaus,
                     String timeStamp, String optionalField2, String optionalField3, String optionalField4, String optionalField5) {
        this.tunnus = tunnus;
        this.luokka = luokka;
        this.viesti = viesti;
        this.osoite = osoite;
        this.kommentti = kommentti;
        this.vastaus = vastaus;
        this.timeStamp = timeStamp;
        this.optionalField2 = optionalField2;
        this.optionalField3 = optionalField3;
        this.optionalField4 = optionalField4;
        this.optionalField5 = optionalField5;
    }

    public void setTunnus(String tunnus) {
        this.tunnus = tunnus;
    }

    public void setLuokka(String luokka) {
        this.luokka = luokka;
    }

    public void setViesti(String viesti) {
        this.viesti = viesti;
    }

    public void setOsoite(String osoite) {
        this.osoite = osoite;
    }

    public void setKommentti(String kommentti) {
        this.kommentti = kommentti;
    }

    public void setVastaus(String vastaus) {
        this.vastaus = vastaus;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setOptionalField2(String optionalField2) {
        this.optionalField2 = optionalField2;
    }

    public void setOptionalField3(String optionalField3) {
        this.optionalField3 = optionalField3;
    }

    public void setOptionalField4(String optionalField4) {
        this.optionalField4 = optionalField4;
    }

    public void setOptionalField5(String optionalField5) {
        this.optionalField5 = optionalField5;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTunnus() {
        return tunnus;
    }

    public String getLuokka() {
        return luokka;
    }

    public String getViesti() {
        return viesti;
    }

    public String getOsoite() {
        return osoite;
    }

    public String getKommentti() {
        return kommentti;
    }

    public String getVastaus() {
        return vastaus;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getOptionalField2() {
        return optionalField2;
    }

    public String getOptionalField3() {
        return optionalField3;
    }

    public String getOptionalField4() {
        return optionalField4;
    }

    public String getOptionalField5() {
        return optionalField5;
    }
}
