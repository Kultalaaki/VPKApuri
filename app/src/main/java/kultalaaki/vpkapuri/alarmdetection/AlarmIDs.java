/*
 * Created by Kultala Aki on 8/1/21, 10:25 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/1/21, 10:25 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Alarm IDs in HashMap<String, String>.
 */
public class AlarmIDs {

    Map<String, String> alarmIDs;

    public AlarmIDs() {
        this.alarmIDs = new HashMap<>();
        addAlarmIDsToMap();
    }

    /**
     * @return Alarm ID text
     */
    public Map<String, String> getAlarmIDs() {
        return this.alarmIDs;
    }

    private void addAlarmIDsToMap() {
        alarmIDs.put("103", "PALOHÄLYTYS");
        alarmIDs.put("104", "SÄTEILYHÄLYTYS");
        alarmIDs.put("105", "HISSIHÄLYTYS");
        alarmIDs.put("106", "LAITEVIKA");
        alarmIDs.put("107", "YHTEYSVIKA");
        alarmIDs.put("108", "HUOLTO");
        alarmIDs.put("200", "TIELIIKENNE: MUU TAI ONNETTOMUUDEN UHKA");
        alarmIDs.put("201", "TIELIIKENNE: PELTIKOLARI, SUISTUMINEN");
        alarmIDs.put("202", "TIELIIKENNE: PIENI");
        alarmIDs.put("203", "TIELIIKENNE: KESKISUURI");
        alarmIDs.put("204", "TIELIIKENNE: SUURI");
        alarmIDs.put("205", "TIELIIKENNE: ELÄIN OSALLISENA");
        alarmIDs.put("206", "TIELIIKENNE: MAANALLA:PIENI");
        alarmIDs.put("207", "TIELIIKENNE: MAANALLA:KESKISUURI");
        alarmIDs.put("208", "TIELIIKENNE: MAANALLA:SUURI");
        alarmIDs.put("210", "RAIDELIIKENNE: MUU");
        alarmIDs.put("211", "RAIDELIIKENNE: PELTIKOLARI");
        alarmIDs.put("212", "RAIDELIIKENNE: PIENI");
        alarmIDs.put("213", "RAIDELIIKENNE: KESKISUURI");
        alarmIDs.put("214", "RAIDELIIKENNE: SUURI");
        alarmIDs.put("215", "RAIDELIIKENNE: ELÄINOSALLISENA");
        alarmIDs.put("216", "RAIDELIIKENNE: MAANALLA: PIENI");
        alarmIDs.put("217", "RAIDELIIKENNE: MAANALLA: KESKISUURI");
        alarmIDs.put("218", "RAIDELIIKENNE: MAANALLA: SUURI");
        alarmIDs.put("220", "VESILIIKENNE: MUU");
        alarmIDs.put("221", "VESILIIKENNE: PIENI");
        alarmIDs.put("222", "VESILIIKENNE: KESKISUURI");
        alarmIDs.put("223", "VESILIIKENNE: SUURI");
        alarmIDs.put("231", "ILMALIIKENNEONNETTOMUUS: PIENI");
        alarmIDs.put("232", "ILMALIIKENNEONNETTOMUUS: KESKISUURI");
        alarmIDs.put("233", "ILMALIIKENNEONNETTOMUUS: SUURI");
        alarmIDs.put("234", "ILMALIIKENNEVAARA: PIENI");
        alarmIDs.put("235", "ILMALIIKENNEVAARA: KESKISUURI");
        alarmIDs.put("236", "ILMALIIKENNEVAARA: SUURI");
        alarmIDs.put("271", "MAASTOLIIKENNEONNETTOMUUS");
        alarmIDs.put("H351", "VARIKKO TAI ASEMAVALMIUS");
        alarmIDs.put("H352", "VALMIUSSIIRTO");
        alarmIDs.put("401", "RAKENNUSPALO: PIENI");
        alarmIDs.put("402", "RAKENNUSPALO: KESKISUURI");
        alarmIDs.put("403", "RAKENNUSPALO: SUURI");
        alarmIDs.put("404", "RAKENNUSPALO: MAANALLA: PIENI");
        alarmIDs.put("405", "RAKENNUSPALO: MAANALLA: KESKISUURI");
        alarmIDs.put("406", "RAKENNUSPALO: MAANALLA: SUURI");
        alarmIDs.put("411", "LIIKENNEVÄLINEPALO: PIENI");
        alarmIDs.put("412", "LIIKENNEVÄLINEPALO: KESKISUURI");
        alarmIDs.put("413", "LIIKENNEVÄLINEPALO: SUURI");
        alarmIDs.put("414", "LIIKENNEVÄLINEPALO: MAANALLA: PIENI");
        alarmIDs.put("415", "LIIKENNEVÄLINEPALO: MAANALLA: KESKISUURI");
        alarmIDs.put("416", "LIIKENNEVÄLINEPALO: MAANALLA: SUURI");
        alarmIDs.put("420", "SAVUHAVAINTO");
        alarmIDs.put("421", "MAASTOPALO: PIENI");
        alarmIDs.put("422", "MAASTOPALO: KESKISUURI");
        alarmIDs.put("423", "MAASTOPALO: SUURI");
        alarmIDs.put("424", "TURVETUOTANTOALUEPALO: PIENI");
        alarmIDs.put("425", "TURVETUOTANTOALUEPALO: KESKISUURI");
        alarmIDs.put("426", "TURVETUOTANTOALUEPALO: SUURI");
        alarmIDs.put("431", "TULIPALO, MUU: PIENI");
        alarmIDs.put("432", "TULIPALO, MUU: KESKISUURI");
        alarmIDs.put("433", "TULIPALO, MUU: SUURI");
        alarmIDs.put("434", "TULIPALO, MUU: MAANALLA: PIENI");
        alarmIDs.put("435", "TULIPALO, MUU: MAANALLA: KESKISUURI");
        alarmIDs.put("436", "TULIPALO, MUU: MAANALLA: SUURI");
        alarmIDs.put("441", "RÄJÄHDYS/ SORTUMA: PIENI");
        alarmIDs.put("442", "RÄJÄHDYS/ SORTUMA: KESKISUURI");
        alarmIDs.put("443", "RÄJÄHDYS/ SORTUMA: SUURI");
        alarmIDs.put("444", "RÄJÄHDYS-/ SORTUMAVAARA");
        alarmIDs.put("451", "VAARALLISENAINEEN ONNETTOMUUS: PIENI");
        alarmIDs.put("452", "VAARALLISENAINEEN ONNETTOMUUS: KESKISUURI");
        alarmIDs.put("453", "VAARALLISENAINEEN ONNETTOMUUS: SUURI");
        alarmIDs.put("455", "VAARALLISENAINEEN ONNETTOMUUS: ONNETTOMUUSVAARA");
        alarmIDs.put("461", "VAHINGONTORJUNTA: PIENI");
        alarmIDs.put("462", "VAHINGONTORJUNTA: KESKISUURI");
        alarmIDs.put("463", "VAHINGONTORJUNTA: SUURI");
        alarmIDs.put("471", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: MAALLA: PIENI");
        alarmIDs.put("472", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: MAALLA: KESKISUURI");
        alarmIDs.put("473", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: MAALLA: SUURI");
        alarmIDs.put("474", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: PIENI");
        alarmIDs.put("475", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: KESKISUURI");
        alarmIDs.put("476", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: SUURI");
        alarmIDs.put("477", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: ONNETTOMUUSVAARA");
        alarmIDs.put("480", "IHMISENPELASTAMINEN: MUU");
        alarmIDs.put("481", "IHMISENPELASTAMINEN: ETSINTÄ");
        alarmIDs.put("482", "IHMISENPELASTAMINEN: AVUNANTO");
        alarmIDs.put("483", "IHMISENPELASTAMINEN: VEDESTÄ");
        alarmIDs.put("484", "IHMISENPELASTAMINEN: PINTAPELASTUS");
        alarmIDs.put("485", "IHMISENPELASTAMINEN: MAASTOSTA");
        alarmIDs.put("486", "IHMISENPELASTAMINEN: PURISTUKSISTA");
        alarmIDs.put("487", "IHMISENPELASTAMINEN: YLHÄÄLTÄ/ ALHAALTA");
        alarmIDs.put("490", "EPÄSELVÄ ONNETTOMUUS");
        alarmIDs.put("491", "LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: PIENI");
        alarmIDs.put("492", "LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: KESKISUURI");
        alarmIDs.put("493", "LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: SUURI");
        alarmIDs.put("550", "AVUNANTO: MUU");
        alarmIDs.put("551", "VIRKA-APUTEHTÄVÄ");
        alarmIDs.put("552", "AVUNANTOTEHTÄVÄ");
        alarmIDs.put("553", "UHKA-/ VARUILLAOLO");
        alarmIDs.put("554", "TARKISTUS-/ VARMISTUS");
        alarmIDs.put("580", "ELÄINTEHTÄVÄ: MUU");
        alarmIDs.put("581", "ELÄIMENPELASTAMINEN");
        alarmIDs.put("700", "Eloton");
        alarmIDs.put("701", "Elvytys");
        alarmIDs.put("702", "Tajuttomuus");
        alarmIDs.put("703", "Hengitysvaikeus");
        alarmIDs.put("704", "Rintakipu");
        alarmIDs.put("705", "PEH; Muu äkillisesti heikentynyt yleistila");
        alarmIDs.put("706", "Aivohalvaus");
        alarmIDs.put("710", "Tukehtuminen");
        alarmIDs.put("711", "Ilmatie-este");
        alarmIDs.put("712", "Jääminen suljettuun tilaan");
        alarmIDs.put("713", "Hirttäytyminen, Kuristuminen");
        alarmIDs.put("714", "Hukuksiin joutuminen");
        alarmIDs.put("741", "Putoaminen");
        alarmIDs.put("744", "Haava");
        alarmIDs.put("745", "Kaatuminen");
        alarmIDs.put("746", "Isku");
        alarmIDs.put("747", "Vamma; muu");
        alarmIDs.put("751", "Kaasumyrkytys");
        alarmIDs.put("752", "Myrkytys");
        alarmIDs.put("753", "Sähköisku");
        alarmIDs.put("755", "Palovamma, lämpöhalvaus");
        alarmIDs.put("756", "Alilämpöisyys");
        alarmIDs.put("757", "Onnettomuus; muu");
        alarmIDs.put("761", "Verenvuoto, Suusta");
        alarmIDs.put("762", "Verenvuoto, Gynekologinen/ urologinen");
        alarmIDs.put("763", "Vernevuoto, Korva/ nenä");
        alarmIDs.put("764", "Säärihaava/ Muu");
        alarmIDs.put("770", "Sairauskohtaus");
        alarmIDs.put("771", "Sokeritasapainon häiriö");
        alarmIDs.put("772", "Kouristelu");
        alarmIDs.put("773", "Yliherkkyysreaktio");
        alarmIDs.put("774", "Heikentynytyleistila, muusairaus");
        alarmIDs.put("775", "Oksentelu, Ripuli");
        alarmIDs.put("781", "Vatsakipu");
        alarmIDs.put("782", "Pää-/ Niskasärky");
        alarmIDs.put("783", "Selkä-/ raaja-/ vartalokipu");
        alarmIDs.put("784", "Aistioire");
        alarmIDs.put("785", "Mielenterveysongelma");
        alarmIDs.put("790", "Hälytys puhelun aikana");
        alarmIDs.put("791", "Synnytys");
        alarmIDs.put("792", "Varallaolo, valmiussiirto");
        alarmIDs.put("793", "Hoitolaitossiirto");
        alarmIDs.put("794", "Muu sairaankuljetustehtävä");
        alarmIDs.put("796", "Monipotilastilanne/ Suuronnettomuus");
        alarmIDs.put("901", "PELASTUSTOIMIPOIKKEUSOLOISSA");
    }
}
