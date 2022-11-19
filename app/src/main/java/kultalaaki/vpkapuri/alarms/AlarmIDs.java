/*
 * Created by Kultala Aki on 1/8/2021, 10:25 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 9/7/2022
 */

package kultalaaki.vpkapuri.alarms;


import java.util.HashMap;

/**
 * Contains alarm ids in HashMap
 */
public class AlarmIDs {

    /**
     * @return HashMap containing rescue alarm ids.
     */
    public HashMap<String, String> rescueAlarmIDs() {
        HashMap<String, String> rescueAlarmIDs = new HashMap<>();

        rescueAlarmIDs.put("103", "PALOHÄLYTYS");
        rescueAlarmIDs.put("104", "SÄTEILYHÄLYTYS");
        rescueAlarmIDs.put("105", "HISSIHÄLYTYS");
        rescueAlarmIDs.put("106", "LAITEVIKA");
        rescueAlarmIDs.put("107", "YHTEYSVIKA");
        rescueAlarmIDs.put("108", "HUOLTO");
        rescueAlarmIDs.put("200", "TIELIIKENNE: MUU TAI ONNETTOMUUDEN UHKA");
        rescueAlarmIDs.put("201", "TIELIIKENNE: PELTIKOLARI, SUISTUMINEN");
        rescueAlarmIDs.put("202", "TIELIIKENNE: PIENI");
        rescueAlarmIDs.put("203", "TIELIIKENNE: KESKISUURI");
        rescueAlarmIDs.put("204", "TIELIIKENNE: SUURI");
        rescueAlarmIDs.put("205", "TIELIIKENNE: ELÄIN OSALLISENA");
        rescueAlarmIDs.put("206", "TIELIIKENNE: MAANALLA:PIENI");
        rescueAlarmIDs.put("207", "TIELIIKENNE: MAANALLA:KESKISUURI");
        rescueAlarmIDs.put("208", "TIELIIKENNE: MAANALLA:SUURI");
        rescueAlarmIDs.put("210", "RAIDELIIKENNE: MUU");
        rescueAlarmIDs.put("211", "RAIDELIIKENNE: PELTIKOLARI");
        rescueAlarmIDs.put("212", "RAIDELIIKENNE: PIENI");
        rescueAlarmIDs.put("213", "RAIDELIIKENNE: KESKISUURI");
        rescueAlarmIDs.put("214", "RAIDELIIKENNE: SUURI");
        rescueAlarmIDs.put("215", "RAIDELIIKENNE: ELÄINOSALLISENA");
        rescueAlarmIDs.put("216", "RAIDELIIKENNE: MAANALLA: PIENI");
        rescueAlarmIDs.put("217", "RAIDELIIKENNE: MAANALLA: KESKISUURI");
        rescueAlarmIDs.put("218", "RAIDELIIKENNE: MAANALLA: SUURI");
        rescueAlarmIDs.put("220", "VESILIIKENNE: MUU");
        rescueAlarmIDs.put("221", "VESILIIKENNE: PIENI");
        rescueAlarmIDs.put("222", "VESILIIKENNE: KESKISUURI");
        rescueAlarmIDs.put("223", "VESILIIKENNE: SUURI");
        rescueAlarmIDs.put("231", "ILMALIIKENNEONNETTOMUUS: PIENI");
        rescueAlarmIDs.put("232", "ILMALIIKENNEONNETTOMUUS: KESKISUURI");
        rescueAlarmIDs.put("233", "ILMALIIKENNEONNETTOMUUS: SUURI");
        rescueAlarmIDs.put("234", "ILMALIIKENNEVAARA: PIENI");
        rescueAlarmIDs.put("235", "ILMALIIKENNEVAARA: KESKISUURI");
        rescueAlarmIDs.put("236", "ILMALIIKENNEVAARA: SUURI");
        rescueAlarmIDs.put("271", "MAASTOLIIKENNEONNETTOMUUS");
        rescueAlarmIDs.put("H351", "VARIKKO-/ ASEMAVALMIUS");
        rescueAlarmIDs.put("H352", "VALMIUSSIIRTO");
        rescueAlarmIDs.put("401", "RAKENNUSPALO: PIENI");
        rescueAlarmIDs.put("402", "RAKENNUSPALO: KESKISUURI");
        rescueAlarmIDs.put("403", "RAKENNUSPALO: SUURI");
        rescueAlarmIDs.put("404", "RAKENNUSPALO: MAANALLA: PIENI");
        rescueAlarmIDs.put("405", "RAKENNUSPALO: MAANALLA: KESKISUURI");
        rescueAlarmIDs.put("406", "RAKENNUSPALO: MAANALLA: SUURI");
        rescueAlarmIDs.put("411", "LIIKENNEVÄLINEPALO: PIENI");
        rescueAlarmIDs.put("412", "LIIKENNEVÄLINEPALO: KESKISUURI");
        rescueAlarmIDs.put("413", "LIIKENNEVÄLINEPALO: SUURI");
        rescueAlarmIDs.put("414", "LIIKENNEVÄLINEPALO: MAANALLA: PIENI");
        rescueAlarmIDs.put("415", "LIIKENNEVÄLINEPALO: MAANALLA: KESKISUURI");
        rescueAlarmIDs.put("416", "LIIKENNEVÄLINEPALO: MAANALLA: SUURI");
        rescueAlarmIDs.put("420", "SAVUHAVAINTO");
        rescueAlarmIDs.put("421", "MAASTOPALO: PIENI");
        rescueAlarmIDs.put("422", "MAASTOPALO: KESKISUURI");
        rescueAlarmIDs.put("423", "MAASTOPALO: SUURI");
        rescueAlarmIDs.put("424", "TURVETUOTANTOALUEPALO: PIENI");
        rescueAlarmIDs.put("425", "TURVETUOTANTOALUEPALO: KESKISUURI");
        rescueAlarmIDs.put("426", "TURVETUOTANTOALUEPALO: SUURI");
        rescueAlarmIDs.put("431", "TULIPALO, MUU: PIENI");
        rescueAlarmIDs.put("432", "TULIPALO, MUU: KESKISUURI");
        rescueAlarmIDs.put("433", "TULIPALO, MUU: SUURI");
        rescueAlarmIDs.put("434", "TULIPALO, MUU: MAANALLA: PIENI");
        rescueAlarmIDs.put("435", "TULIPALO, MUU: MAANALLA: KESKISUURI");
        rescueAlarmIDs.put("436", "TULIPALO, MUU: MAANALLA: SUURI");
        rescueAlarmIDs.put("441", "RÄJÄHDYS/ SORTUMA: PIENI");
        rescueAlarmIDs.put("442", "RÄJÄHDYS/ SORTUMA: KESKISUURI");
        rescueAlarmIDs.put("443", "RÄJÄHDYS/ SORTUMA: SUURI");
        rescueAlarmIDs.put("444", "RÄJÄHDYS-/ SORTUMAVAARA");
        rescueAlarmIDs.put("451", "VAARALLISENAINEEN ONNETTOMUUS: PIENI");
        rescueAlarmIDs.put("452", "VAARALLISENAINEEN ONNETTOMUUS: KESKISUURI");
        rescueAlarmIDs.put("453", "VAARALLISENAINEEN ONNETTOMUUS: SUURI");
        rescueAlarmIDs.put("455", "VAARALLISENAINEEN ONNETTOMUUS: ONNETTOMUUSVAARA");
        rescueAlarmIDs.put("461", "VAHINGONTORJUNTA: PIENI");
        rescueAlarmIDs.put("462", "VAHINGONTORJUNTA: KESKISUURI");
        rescueAlarmIDs.put("463", "VAHINGONTORJUNTA: SUURI");
        rescueAlarmIDs.put("471", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: MAALLA: PIENI");
        rescueAlarmIDs.put("472", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: MAALLA: KESKISUURI");
        rescueAlarmIDs.put("473", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: MAALLA: SUURI");
        rescueAlarmIDs.put("474", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: PIENI");
        rescueAlarmIDs.put("475", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: KESKISUURI");
        rescueAlarmIDs.put("476", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: SUURI");
        rescueAlarmIDs.put("477", "ÖLJYVAHINKO/ YMPÄRISTÖONNETTOMUUS: ONNETTOMUUSVAARA");
        rescueAlarmIDs.put("480", "IHMISENPELASTAMINEN: MUU");
        rescueAlarmIDs.put("481", "IHMISENPELASTAMINEN: ETSINTÄ");
        rescueAlarmIDs.put("482", "IHMISENPELASTAMINEN: AVUNANTO");
        rescueAlarmIDs.put("483", "IHMISENPELASTAMINEN: VEDESTÄ");
        rescueAlarmIDs.put("484", "IHMISENPELASTAMINEN: PINTAPELASTUS");
        rescueAlarmIDs.put("485", "IHMISENPELASTAMINEN: MAASTOSTA");
        rescueAlarmIDs.put("486", "IHMISENPELASTAMINEN: PURISTUKSISTA");
        rescueAlarmIDs.put("487", "IHMISENPELASTAMINEN: YLHÄÄLTÄ/ ALHAALTA");
        rescueAlarmIDs.put("490", "EPÄSELVÄ ONNETTOMUUS");
        rescueAlarmIDs.put("491", "LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: PIENI");
        rescueAlarmIDs.put("492", "LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: KESKISUURI");
        rescueAlarmIDs.put("493", "LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: SUURI");
        rescueAlarmIDs.put("550", "AVUNANTO: MUU");
        rescueAlarmIDs.put("551", "VIRKA-APUTEHTÄVÄ");
        rescueAlarmIDs.put("552", "AVUNANTOTEHTÄVÄ");
        rescueAlarmIDs.put("553", "UHKA-/ VARUILLAOLO");
        rescueAlarmIDs.put("554", "TARKISTUS-/ VARMISTUS");
        rescueAlarmIDs.put("580", "ELÄINTEHTÄVÄ: MUU");
        rescueAlarmIDs.put("581", "ELÄIMENPELASTAMINEN");
        rescueAlarmIDs.put("901", "PELASTUSTOIMI POIKKEUSOLOISSA");

        return rescueAlarmIDs;
    }

    /**
     * @return HashMap containing ambulance alarm ids.
     */
    public HashMap<String, String> ambulanceAlarmIDs() {
        HashMap<String, String> ambulanceAlarmIDs = new HashMap<>();

        ambulanceAlarmIDs.put("700", "Eloton");
        ambulanceAlarmIDs.put("701", "Elvytys");
        ambulanceAlarmIDs.put("702", "Tajuttomuus");
        ambulanceAlarmIDs.put("703", "Hengitysvaikeus");
        ambulanceAlarmIDs.put("704", "Rintakipu");
        ambulanceAlarmIDs.put("705", "PEH; Muu äkillisesti heikentynyt yleistila");
        ambulanceAlarmIDs.put("706", "Aivohalvaus");
        ambulanceAlarmIDs.put("710", "Tukehtuminen");
        ambulanceAlarmIDs.put("711", "Ilmatie-este");
        ambulanceAlarmIDs.put("712", "Jääminen suljettuun tilaan");
        ambulanceAlarmIDs.put("713", "Hirttäytyminen, Kuristuminen");
        ambulanceAlarmIDs.put("714", "Hukuksiin joutuminen");
        ambulanceAlarmIDs.put("741", "Putoaminen");
        ambulanceAlarmIDs.put("744", "Haava");
        ambulanceAlarmIDs.put("745", "Kaatuminen");
        ambulanceAlarmIDs.put("746", "Isku");
        ambulanceAlarmIDs.put("747", "Vamma; muu");
        ambulanceAlarmIDs.put("751", "Kaasumyrkytys");
        ambulanceAlarmIDs.put("752", "Myrkytys");
        ambulanceAlarmIDs.put("753", "Sähköisku");
        ambulanceAlarmIDs.put("755", "Palovamma, lämpöhalvaus");
        ambulanceAlarmIDs.put("756", "Alilämpöisyys");
        ambulanceAlarmIDs.put("757", "Onnettomuus; muu");
        ambulanceAlarmIDs.put("761", "Verenvuoto, Suusta");
        ambulanceAlarmIDs.put("762", "Verenvuoto, Gynekologinen/ urologinen");
        ambulanceAlarmIDs.put("763", "Vernevuoto, Korva/ nenä");
        ambulanceAlarmIDs.put("764", "Säärihaava/ Muu");
        ambulanceAlarmIDs.put("770", "Sairauskohtaus");
        ambulanceAlarmIDs.put("771", "Sokeritasapainon häiriö");
        ambulanceAlarmIDs.put("772", "Kouristelu");
        ambulanceAlarmIDs.put("773", "Yliherkkyysreaktio");
        ambulanceAlarmIDs.put("774", "Heikentynytyleistila, muusairaus");
        ambulanceAlarmIDs.put("775", "Oksentelu, Ripuli");
        ambulanceAlarmIDs.put("781", "Vatsakipu");
        ambulanceAlarmIDs.put("782", "Pää-/ Niskasärky");
        ambulanceAlarmIDs.put("783", "Selkä-/ raaja-/ vartalokipu");
        ambulanceAlarmIDs.put("784", "Aistioire");
        ambulanceAlarmIDs.put("785", "Mielenterveysongelma");
        ambulanceAlarmIDs.put("790", "Hälytys puhelun aikana");
        ambulanceAlarmIDs.put("791", "Synnytys");
        ambulanceAlarmIDs.put("792", "Varallaolo, valmiussiirto");
        ambulanceAlarmIDs.put("793", "Hoitolaitossiirto");
        ambulanceAlarmIDs.put("794", "Muu sairaankuljetustehtävä");
        ambulanceAlarmIDs.put("796", "Monipotilastilanne/ Suuronnettomuus");

        return ambulanceAlarmIDs;
    }
}
