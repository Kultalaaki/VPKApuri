/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 8.7.2019 22:01
 */

package kultalaaki.vpkapuri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "responder_table")
public class Responder {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String vacancyNumber;
    private String message;
    private String attributeLeader;
    private String attributeDriverLicense;
    private String attributeSmoke;
    private String attributeChemical;
    private String attributeOptionall;
    private String attributeOptional2;
    private String attributeOptional3;
    private String attributeOptional4;
    private String attributeOptional5;

    public Responder(String name, String vacancyNumber, String message, String attributeLeader, String attributeDriverLicense, String attributeSmoke, String attributeChemical, String attributeOptionall,
                     String attributeOptional2, String attributeOptional3, String attributeOptional4, String attributeOptional5) {
        this.name = name;
        this.vacancyNumber = vacancyNumber;
        this.message = message;
        this.attributeLeader = attributeLeader;
        this.attributeDriverLicense = attributeDriverLicense;
        this.attributeSmoke = attributeSmoke;
        this.attributeChemical = attributeChemical;
        this.attributeOptionall = attributeOptionall;
        this.attributeOptional2 = attributeOptional2;
        this.attributeOptional3 = attributeOptional3;
        this.attributeOptional4 = attributeOptional4;
        this.attributeOptional5 = attributeOptional5;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVacancyNumber() {
        return vacancyNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getAttributeOptionall() {
        return attributeOptionall;
    }

    public String getAttributeLeader() {
        return attributeLeader;
    }

    public String getAttributeDriverLicense() {
        return attributeDriverLicense;
    }

    public String getAttributeSmoke() {
        return attributeSmoke;
    }

    public String getAttributeChemical() {
        return attributeChemical;
    }

    public String getAttributeOptional1() {
        return attributeOptionall;
    }

    public String getAttributeOptional2() {
        return attributeOptional2;
    }

    public String getAttributeOptional3() {
        return attributeOptional3;
    }

    public String getAttributeOptional4() {
        return attributeOptional4;
    }

    public String getAttributeOptional5() {
        return attributeOptional5;
    }

    public void setMessage(String message) { this.message = message; }
}
