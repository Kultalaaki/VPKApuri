package kultalaaki.vpkapuri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "firealarm_table")
public class FireAlarm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String tunnus;
    private String luokka;
    private String viesti;
    private String osoite;
    private String kommentti;
    private String vastaus;
    private String optionalField;
    private String optionalField2;
    private String optionalField3;
    private String optionalField4;
    private String optionalField5;

    public FireAlarm(String tunnus, String luokka, String viesti, String osoite, String kommentti, String vastaus,
                     String optionalField, String optionalField2, String optionalField3, String optionalField4, String optionalField5) {
        this.tunnus = tunnus;
        this.luokka = luokka;
        this.viesti = viesti;
        this.osoite = osoite;
        this.kommentti = kommentti;
        this.vastaus = vastaus;
        this.optionalField = optionalField;
        this.optionalField2 = optionalField2;
        this.optionalField3 = optionalField3;
        this.optionalField4 = optionalField4;
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

    public String getOptionalField() {
        return optionalField;
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
