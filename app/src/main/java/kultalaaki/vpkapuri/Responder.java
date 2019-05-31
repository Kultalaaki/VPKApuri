package kultalaaki.vpkapuri;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "responder_table")
public class Responder {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String message;
    private String attributeLeader;
    private String attributeDriverLicense;
    private String attributeSmoke;
    private String attributeChemical;
    private String attributeOptional;
    private String attributeOptiona2;
    private String attributeOptiona3;
    private String attributeOptiona4;
    private String attributeOptiona5;

    public Responder(String name, String message, String attributeLeader, String attributeDriverLicense, String attributeSmoke, String attributeChemical, String attributeOptional,
                     String attributeOptiona2, String attributeOptiona3, String attributeOptiona4, String attributeOptiona5) {
        this.name = name;
        this.message = message;
        this.attributeLeader = attributeLeader;
        this.attributeDriverLicense = attributeDriverLicense;
        this.attributeSmoke = attributeSmoke;
        this.attributeChemical = attributeChemical;
        this.attributeOptional = attributeOptional;
        this.attributeOptiona2 = attributeOptiona2;
        this.attributeOptiona3 = attributeOptiona3;
        this.attributeOptiona4 = attributeOptiona4;
        this.attributeOptiona5 = attributeOptiona5;
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

    public String getMessage() {
        return message;
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

    public String getAttributeOptional() {
        return attributeOptional;
    }

    public String getAttributeOptiona2() {
        return attributeOptiona2;
    }

    public String getAttributeOptiona3() {
        return attributeOptiona3;
    }

    public String getAttributeOptiona4() {
        return attributeOptiona4;
    }

    public String getAttributeOptiona5() {
        return attributeOptiona5;
    }
}
