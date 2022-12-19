package kultalaaki.vpkapuri.databasebackupandrestore;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import kultalaaki.vpkapuri.dbfirealarm.FireAlarm;

/**
 * Writes alarms to json array
 */
public class FireAlarmJSONWriter {

    /**
     * Handles creating writer and its properties
     *
     * @param out    outputstream
     * @param alarms list to write
     * @throws IOException handled by caller
     */
    public void writeJsonStream(OutputStream out, List<FireAlarm> alarms) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.setIndent("  ");
        writeAlarmsArray(writer, alarms);
        writer.close();
    }

    /**
     * Writes objects to json array
     *
     * @param writer handles writing to array
     * @param alarms list to give writer
     * @throws IOException caller has to handle
     */
    public void writeAlarmsArray(JsonWriter writer, List<FireAlarm> alarms) throws IOException {
        writer.beginArray();
        // Reverse list before writing to a file
        List<FireAlarm> inverted = invert(alarms);
        for (FireAlarm fireAlarm : inverted) {
            writeAlarm(writer, fireAlarm);
        }
        writer.endArray();
    }

    /**
     * Creates objects to store in array
     *
     * @param writer    writes to json
     * @param fireAlarm single alarm in list
     * @throws IOException handled by caller
     */
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

    /**
     * Inverts given list
     *
     * @param list to be inverted
     * @return inverted list
     */
    private List<FireAlarm> invert(List<FireAlarm> list) {
        List<FireAlarm> invertedList = new ArrayList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            invertedList.add(list.get(i));
        }

        return invertedList;
    }
}
