package kultalaaki.vpkapuri.json;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import kultalaaki.vpkapuri.FireAlarm;

public class FireAlarmJsonWriter {

    public void writeJsonStream(OutputStream out, List<FireAlarm> alarms) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        writeAlarmsArray(writer, alarms);
        writer.close();
    }

    public void writeAlarmsArray(JsonWriter writer, List<FireAlarm> alarms) throws IOException {
        writer.beginArray();
        for (FireAlarm fireAlarm : alarms) {
            writeAlarm(writer, fireAlarm);
        }
        writer.endArray();
    }

    public void writeAlarm(JsonWriter writer, FireAlarm fireAlarm) throws IOException {
        writer.beginObject();
        writer.name("viesti").value(fireAlarm.getViesti());
        writer.name("tehtäväluokka").value(fireAlarm.getTehtavaluokka());
        writer.name("kiireellisyystunnus").value(fireAlarm.getKiireellisyystunnus());
        writer.name("osoite").value(fireAlarm.getOsoite());
        writer.name("timestamp").value(fireAlarm.getTimeStamp());
        writer.name("kommentti").value(fireAlarm.getKommentti());
        writer.name("vastaus").value(fireAlarm.getVastaus());
        writer.name("optional2").value(fireAlarm.getOptionalField2());
        writer.name("optional3").value(fireAlarm.getOptionalField3());
        writer.name("optional4").value(fireAlarm.getOptionalField4());
        writer.name("optional5").value(fireAlarm.getOptionalField5());
        writer.endObject();
    }
}
