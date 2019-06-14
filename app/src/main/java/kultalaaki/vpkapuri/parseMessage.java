package kultalaaki.vpkapuri;

import java.util.ArrayList;

class parseMessage {

    private static ArrayList<String> kunnat = new ArrayList<>(), halytunnukset = new ArrayList<>(), halytekstit = new ArrayList<>();

    String osoite(String message) {

        String osoite = "";
        //String[] palautus = new String[5];
        int length = message.length();
        //int pituus = strings[0].length();
        int halytunnusSijainti = 0;
        int listaPaikka = 0;
        ArrayList<String> viestinSanat = new ArrayList<>();
        ArrayList<String> sanatYksinaan = new ArrayList<>();
        StringBuilder viestinLauseet = new StringBuilder();
        StringBuilder viestiTeksti = new StringBuilder();
        StringBuilder sanatYksitellen = new StringBuilder();
        String sana;
        String sanaYksin;
        String halytysLuokka = "";
        String kiireellisyysLuokka = "";
        char merkki;
        boolean loytyi = false;

        for (int o = 0; o <= length - 1; o++) {
            viestiTeksti.append(message.charAt(o));
        }

        // Katkotaan viesti sanoihin
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[.,/:; ]")) {
                sanaYksin = sanatYksitellen.toString();
                if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                    sanatYksinaan.add(sanaYksin);
                }
                sanatYksitellen.delete(0, sanatYksitellen.length());
            } else {
                sanatYksitellen.append(message.charAt(i));
            }
        }

        // Katkotaan viesti osiin puolipilkkujen mukaan
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[;]")) {
                sana = viestinLauseet.toString();
                if (sana.length() > 1 || sana.matches("[0-9]")) {
                    viestinSanat.add(sana);
                }
                viestinLauseet.delete(0, viestinLauseet.length());
            } else {
                viestinLauseet.append(message.charAt(i));
            }
        }

        String kommentti = "";
        // Etsitään mikä lause sisältää kunnan
        try {
            outer:
            for (String valmisSana : viestinSanat) {
                //String pieni = valmisSana.toLowerCase();
                for (String kunta : kunnat) {
                    if (valmisSana.contains(kunta)) {
                        osoite = valmisSana;
                        break outer;
                    }
                }
            }

            // Kiireellisyysluokan kirjaimen etsiminen
            for (String luokkaKirjain : viestinSanat) {
                if (luokkaKirjain.trim().equals("A") || luokkaKirjain.trim().equals("B") || luokkaKirjain.trim().equals("C") || luokkaKirjain.trim().equals("D")) {
                    kiireellisyysLuokka = luokkaKirjain;
                    break;
                }
            }

            // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
            for (String valmisSana : sanatYksinaan) {
                if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                    //String osaSana = valmisSana.substring(0,3);
                    if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {

                        if (valmisSana.substring(0, 3).equals("H35")) {
                            valmisSana = "H351";
                            halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf("H351");
                            loytyi = true;
                            break;
                        }
                        halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                        listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                        loytyi = true;
                        break;
                    }
                }
            }

            if (loytyi) {
                halytysLuokka = halytekstit.get(listaPaikka);
            } else {
                halytysLuokka = "Ei löytynyt listalta";
            }

            //palautus[0] = osoite;
            //palautus[1] = sanatYksinaan.get(halytunnusSijainti);
            //palautus[3] = halytysLuokka;

        } catch (ArrayIndexOutOfBoundsException e) {
            kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        } catch (Exception e) {
            kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        }

        viestinSanat.clear();
        sanatYksinaan.clear();

        return osoite;
    }

    String tunnus(String message) {

        String osoite = "";
        //String[] palautus = new String[5];
        int length = message.length();
        //int pituus = strings[0].length();
        int halytunnusSijainti = 0;
        int listaPaikka = 0;
        ArrayList<String> viestinSanat = new ArrayList<>();
        ArrayList<String> sanatYksinaan = new ArrayList<>();
        StringBuilder viestinLauseet = new StringBuilder();
        StringBuilder viestiTeksti = new StringBuilder();
        StringBuilder sanatYksitellen = new StringBuilder();
        String sana;
        String sanaYksin;
        String halytysLuokka = "";
        String kiireellisyysLuokka = "";
        char merkki;
        boolean loytyi = false;

        for (int o = 0; o <= length - 1; o++) {
            viestiTeksti.append(message.charAt(o));
        }

        // Katkotaan viesti sanoihin
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[.,/:; ]")) {
                sanaYksin = sanatYksitellen.toString();
                if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                    sanatYksinaan.add(sanaYksin);
                }
                sanatYksitellen.delete(0, sanatYksitellen.length());
            } else {
                sanatYksitellen.append(message.charAt(i));
            }
        }

        // Katkotaan viesti osiin puolipilkkujen mukaan
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[;]")) {
                sana = viestinLauseet.toString();
                if (sana.length() > 1 || sana.matches("[0-9]")) {
                    viestinSanat.add(sana);
                }
                viestinLauseet.delete(0, viestinLauseet.length());
            } else {
                viestinLauseet.append(message.charAt(i));
            }
        }

        String kommentti = "";
        // Etsitään mikä lause sisältää kunnan
        try {
            outer:
            for (String valmisSana : viestinSanat) {
                //String pieni = valmisSana.toLowerCase();
                for (String kunta : kunnat) {
                    if (valmisSana.contains(kunta)) {
                        osoite = valmisSana;
                        break outer;
                    }
                }
            }

            // Kiireellisyysluokan kirjaimen etsiminen
            for (String luokkaKirjain : viestinSanat) {
                if (luokkaKirjain.trim().equals("A") || luokkaKirjain.trim().equals("B") || luokkaKirjain.trim().equals("C") || luokkaKirjain.trim().equals("D")) {
                    kiireellisyysLuokka = luokkaKirjain;
                    break;
                }
            }

            // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
            for (String valmisSana : sanatYksinaan) {
                if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                    //String osaSana = valmisSana.substring(0,3);
                    if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {

                        if (valmisSana.substring(0, 3).equals("H35")) {
                            valmisSana = "H351";
                            halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf("H351");
                            loytyi = true;
                            break;
                        }
                        halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                        listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                        loytyi = true;
                        break;
                    }
                }
            }

            if (loytyi) {
                halytysLuokka = halytekstit.get(listaPaikka);
            } else {
                halytysLuokka = "Ei löytynyt listalta";
            }

            //palautus[0] = osoite;
            //palautus[1] = sanatYksinaan.get(halytunnusSijainti);
            //palautus[3] = halytysLuokka;

        } catch (ArrayIndexOutOfBoundsException e) {
            kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        } catch (Exception e) {
            kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        }

        viestinSanat.clear();
        sanatYksinaan.clear();

        return osoite;
    }

    String tekstiSelvennys(String message) {

        String osoite = "";
        //String[] palautus = new String[5];
        int length = message.length();
        //int pituus = strings[0].length();
        int halytunnusSijainti = 0;
        int listaPaikka = 0;
        ArrayList<String> viestinSanat = new ArrayList<>();
        ArrayList<String> sanatYksinaan = new ArrayList<>();
        StringBuilder viestinLauseet = new StringBuilder();
        StringBuilder viestiTeksti = new StringBuilder();
        StringBuilder sanatYksitellen = new StringBuilder();
        String sana;
        String sanaYksin;
        String halytysLuokka = "";
        String kiireellisyysLuokka = "";
        char merkki;
        boolean loytyi = false;

        for (int o = 0; o <= length - 1; o++) {
            viestiTeksti.append(message.charAt(o));
        }

        // Katkotaan viesti sanoihin
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[.,/:; ]")) {
                sanaYksin = sanatYksitellen.toString();
                if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                    sanatYksinaan.add(sanaYksin);
                }
                sanatYksitellen.delete(0, sanatYksitellen.length());
            } else {
                sanatYksitellen.append(message.charAt(i));
            }
        }

        // Katkotaan viesti osiin puolipilkkujen mukaan
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[;]")) {
                sana = viestinLauseet.toString();
                if (sana.length() > 1 || sana.matches("[0-9]")) {
                    viestinSanat.add(sana);
                }
                viestinLauseet.delete(0, viestinLauseet.length());
            } else {
                viestinLauseet.append(message.charAt(i));
            }
        }

        String kommentti = "";
        // Etsitään mikä lause sisältää kunnan
        try {
            outer:
            for (String valmisSana : viestinSanat) {
                //String pieni = valmisSana.toLowerCase();
                for (String kunta : kunnat) {
                    if (valmisSana.contains(kunta)) {
                        osoite = valmisSana;
                        break outer;
                    }
                }
            }

            // Kiireellisyysluokan kirjaimen etsiminen
            for (String luokkaKirjain : viestinSanat) {
                if (luokkaKirjain.trim().equals("A") || luokkaKirjain.trim().equals("B") || luokkaKirjain.trim().equals("C") || luokkaKirjain.trim().equals("D")) {
                    kiireellisyysLuokka = luokkaKirjain;
                    break;
                }
            }

            // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
            for (String valmisSana : sanatYksinaan) {
                if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                    //String osaSana = valmisSana.substring(0,3);
                    if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {

                        if (valmisSana.substring(0, 3).equals("H35")) {
                            valmisSana = "H351";
                            halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf("H351");
                            loytyi = true;
                            break;
                        }
                        halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                        listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                        loytyi = true;
                        break;
                    }
                }
            }

            if (loytyi) {
                halytysLuokka = halytekstit.get(listaPaikka);
            } else {
                halytysLuokka = "Ei löytynyt listalta";
            }

            //palautus[0] = osoite;
            //palautus[1] = sanatYksinaan.get(halytunnusSijainti);
            //palautus[3] = halytysLuokka;

        } catch (ArrayIndexOutOfBoundsException e) {
            kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        } catch (Exception e) {
            kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        }

        viestinSanat.clear();
        sanatYksinaan.clear();

        return osoite;
    }

    String kiireellisyysTunnus(String message) {

        String osoite = "";
        //String[] palautus = new String[5];
        int length = message.length();
        //int pituus = strings[0].length();
        int halytunnusSijainti = 0;
        int listaPaikka = 0;
        ArrayList<String> viestinSanat = new ArrayList<>();
        ArrayList<String> sanatYksinaan = new ArrayList<>();
        StringBuilder viestinLauseet = new StringBuilder();
        StringBuilder viestiTeksti = new StringBuilder();
        StringBuilder sanatYksitellen = new StringBuilder();
        String sana;
        String sanaYksin;
        String halytysLuokka = "";
        String kiireellisyysLuokka = "";
        char merkki;
        boolean loytyi = false;

        for (int o = 0; o <= length - 1; o++) {
            viestiTeksti.append(message.charAt(o));
        }

        // Katkotaan viesti sanoihin
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[.,/:; ]")) {
                sanaYksin = sanatYksitellen.toString();
                if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                    sanatYksinaan.add(sanaYksin);
                }
                sanatYksitellen.delete(0, sanatYksitellen.length());
            } else {
                sanatYksitellen.append(message.charAt(i));
            }
        }

        // Katkotaan viesti osiin puolipilkkujen mukaan
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[;]")) {
                sana = viestinLauseet.toString();
                if (sana.length() > 1 || sana.matches("[0-9]")) {
                    viestinSanat.add(sana);
                }
                viestinLauseet.delete(0, viestinLauseet.length());
            } else {
                viestinLauseet.append(message.charAt(i));
            }
        }

        String kommentti = "";
        // Etsitään mikä lause sisältää kunnan
        try {
            outer:
            for (String valmisSana : viestinSanat) {
                //String pieni = valmisSana.toLowerCase();
                for (String kunta : kunnat) {
                    if (valmisSana.contains(kunta)) {
                        osoite = valmisSana;
                        break outer;
                    }
                }
            }

            // Kiireellisyysluokan kirjaimen etsiminen
            for (String luokkaKirjain : viestinSanat) {
                if (luokkaKirjain.trim().equals("A") || luokkaKirjain.trim().equals("B") || luokkaKirjain.trim().equals("C") || luokkaKirjain.trim().equals("D")) {
                    kiireellisyysLuokka = luokkaKirjain;
                    break;
                }
            }

            // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
            for (String valmisSana : sanatYksinaan) {
                if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                    //String osaSana = valmisSana.substring(0,3);
                    if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {

                        if (valmisSana.substring(0, 3).equals("H35")) {
                            valmisSana = "H351";
                            halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf("H351");
                            loytyi = true;
                            break;
                        }
                        halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                        listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                        loytyi = true;
                        break;
                    }
                }
            }

            if (loytyi) {
                halytysLuokka = halytekstit.get(listaPaikka);
            } else {
                halytysLuokka = "Ei löytynyt listalta";
            }

            //palautus[0] = osoite;
            //palautus[1] = sanatYksinaan.get(halytunnusSijainti);
            //palautus[3] = halytysLuokka;

        } catch (ArrayIndexOutOfBoundsException e) {
            kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        } catch (Exception e) {
            kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        }

        viestinSanat.clear();
        sanatYksinaan.clear();

        return osoite;
    }

    String address(String message) {

        String osoite = "";
        //String[] palautus = new String[5];
        int length = message.length();
        //int pituus = strings[0].length();
        int halytunnusSijainti = 0;
        int listaPaikka = 0;
        ArrayList<String> viestinSanat = new ArrayList<>();
        ArrayList<String> sanatYksinaan = new ArrayList<>();
        StringBuilder viestinLauseet = new StringBuilder();
        StringBuilder viestiTeksti = new StringBuilder();
        StringBuilder sanatYksitellen = new StringBuilder();
        String sana;
        String sanaYksin;
        String halytysLuokka = "";
        String kiireellisyysLuokka = "";
        char merkki;
        boolean loytyi = false;

        for (int o = 0; o <= length - 1; o++) {
            viestiTeksti.append(message.charAt(o));
        }

        // Katkotaan viesti sanoihin
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[.,/:; ]")) {
                sanaYksin = sanatYksitellen.toString();
                if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                    sanatYksinaan.add(sanaYksin);
                }
                sanatYksitellen.delete(0, sanatYksitellen.length());
            } else {
                sanatYksitellen.append(message.charAt(i));
            }
        }

        // Katkotaan viesti osiin puolipilkkujen mukaan
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[;]")) {
                sana = viestinLauseet.toString();
                if (sana.length() > 1 || sana.matches("[0-9]")) {
                    viestinSanat.add(sana);
                }
                viestinLauseet.delete(0, viestinLauseet.length());
            } else {
                viestinLauseet.append(message.charAt(i));
            }
        }

        String kommentti = "";
        // Etsitään mikä lause sisältää kunnan
        try {
            outer:
            for (String valmisSana : viestinSanat) {
                //String pieni = valmisSana.toLowerCase();
                for (String kunta : kunnat) {
                    if (valmisSana.contains(kunta)) {
                        osoite = valmisSana;
                        break outer;
                    }
                }
            }

            // Kiireellisyysluokan kirjaimen etsiminen
            for (String luokkaKirjain : viestinSanat) {
                if (luokkaKirjain.trim().equals("A") || luokkaKirjain.trim().equals("B") || luokkaKirjain.trim().equals("C") || luokkaKirjain.trim().equals("D")) {
                    kiireellisyysLuokka = luokkaKirjain;
                    break;
                }
            }

            // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
            for (String valmisSana : sanatYksinaan) {
                if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                    //String osaSana = valmisSana.substring(0,3);
                    if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {

                        if (valmisSana.substring(0, 3).equals("H35")) {
                            valmisSana = "H351";
                            halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf("H351");
                            loytyi = true;
                            break;
                        }
                        halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                        listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                        loytyi = true;
                        break;
                    }
                }
            }

            if (loytyi) {
                halytysLuokka = halytekstit.get(listaPaikka);
            } else {
                halytysLuokka = "Ei löytynyt listalta";
            }

            //palautus[0] = osoite;
            //palautus[1] = sanatYksinaan.get(halytunnusSijainti);
            //palautus[3] = halytysLuokka;

        } catch (ArrayIndexOutOfBoundsException e) {
            kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        } catch (Exception e) {
            kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
            //palautus[1] = "Tapahtui virhe.";
            //palautus[3] = "Katso arkistosta hälytyksen kommentti.";
        }

        viestinSanat.clear();
        sanatYksinaan.clear();

        return osoite;
    }

    private boolean rajaapoisvuosiluvut(String vuosiluku) {
        return (vuosiluku.length() < 4 || !vuosiluku.equals("2018")) && !vuosiluku.equals("2019") && !vuosiluku.equals("2020") && !vuosiluku.equals("2021") && !vuosiluku.equals("2022");
    }

    public void lisaaKunnatErica() {
        //ArrayList<String> kunnat = new ArrayList<>();
        kunnat.add("Akaa");
        kunnat.add("Alajärvi");
        kunnat.add("Alavieska");
        kunnat.add("Alavus");
        kunnat.add("Asikkala");
        kunnat.add("Askola");
        kunnat.add("Aura");
        kunnat.add("Brändö");
        kunnat.add("Eckerö");
        kunnat.add("Enonkoski");

        kunnat.add("Enontekiö");
        kunnat.add("Espoo");
        kunnat.add("Eura");
        kunnat.add("Eurajoki");
        kunnat.add("Evijärvi");
        kunnat.add("Finström");
        kunnat.add("Forssa");
        kunnat.add("Föglö");
        kunnat.add("Geta");
        kunnat.add("Haapajärvi");

        kunnat.add("Haapavesi");
        kunnat.add("Hailuoto");
        kunnat.add("Halsua");
        kunnat.add("Hamina");
        kunnat.add("Hammarland");
        kunnat.add("Hankasalmi");
        kunnat.add("Hanko");
        kunnat.add("Harjavalta");
        kunnat.add("Hartola");
        kunnat.add("Hattula");

        kunnat.add("Hausjärvi");
        kunnat.add("Heinola");
        kunnat.add("Heinävesi");
        kunnat.add("Helsinki");
        kunnat.add("Hirvensalmi");
        kunnat.add("Hollola");
        kunnat.add("Honkajoki");
        kunnat.add("Huittinen");
        kunnat.add("Humppila");
        kunnat.add("Hyrynsalmi");

        kunnat.add("Hyvinkää");
        kunnat.add("Hämeenkyrö");
        kunnat.add("Hämeenlinna");
        kunnat.add("Ii");
        kunnat.add("Iisalmi");
        kunnat.add("Iitti");
        kunnat.add("Ikaalinen");
        kunnat.add("Ilmajoki");
        kunnat.add("Ilomantsi");
        kunnat.add("Imatra");

        kunnat.add("Inari");
        kunnat.add("Pohjois-lapin seutukunta");
        kunnat.add("Pedersöre");
        kunnat.add("Inkoo");
        kunnat.add("Isojoki");
        kunnat.add("Isokyrö");
        kunnat.add("Janakkala");
        kunnat.add("Joensuu");
        kunnat.add("Jokioinen");
        kunnat.add("Jomala");
        kunnat.add("Joroinen");

        kunnat.add("Joutsa");
        kunnat.add("Juuka");
        kunnat.add("Juupajoki");
        kunnat.add("Juva");
        kunnat.add("Jyväskylä");
        kunnat.add("Jämijärvi");
        kunnat.add("Jämsä");
        kunnat.add("Järvenpää");
        kunnat.add("Kaarina");
        kunnat.add("Kaavi");

        kunnat.add("Kajaani");
        kunnat.add("Kalajoki");
        kunnat.add("Kangasala");
        kunnat.add("Kangasniemi");
        kunnat.add("Kankaanpää");
        kunnat.add("Kannonkoski");
        kunnat.add("Kannus");
        kunnat.add("Karijoki");
        kunnat.add("Karkkila");
        kunnat.add("Karstula");

        kunnat.add("Karvia");
        kunnat.add("Kaskinen");
        kunnat.add("Kauhajoki");
        kunnat.add("Kauhava");
        kunnat.add("Kauniainen");
        kunnat.add("Kaustinen");
        kunnat.add("Keitele");
        kunnat.add("Kemi");
        kunnat.add("Kemijärvi");
        kunnat.add("Keminmaa");

        kunnat.add("Kemiönsaari");
        kunnat.add("Kempele");
        kunnat.add("Kerava");
        kunnat.add("Keuruu");
        kunnat.add("Kihniö");
        kunnat.add("Kinnula");
        kunnat.add("Kirkkonummi");
        kunnat.add("Kitee");
        kunnat.add("Kittilä");
        kunnat.add("Kiuruvesi");

        kunnat.add("Kivijärvi");
        kunnat.add("Kokemäki");
        kunnat.add("Kokkola");
        kunnat.add("Kolari");
        kunnat.add("Konnevesi");
        kunnat.add("Kontiolahti");
        kunnat.add("Korsnäs");
        kunnat.add("Koski tl");
        kunnat.add("Kotka");
        kunnat.add("Kouvola");

        kunnat.add("Kristiinankaupunki");
        kunnat.add("Kruunupyy");
        kunnat.add("Kuhmo");
        kunnat.add("Kuhmoinen");
        kunnat.add("Kumlinge");
        kunnat.add("Kuopio");
        kunnat.add("Kuortane");
        kunnat.add("Kurikka");
        kunnat.add("Kustavi");
        kunnat.add("Kuusamo");

        kunnat.add("Kyyjärvi");
        kunnat.add("Kärkölä");
        kunnat.add("Kärsämäki");
        kunnat.add("Kökar");
        kunnat.add("Lahti");
        kunnat.add("Laihia");
        kunnat.add("Laitila");
        kunnat.add("Lapinjärvi");
        kunnat.add("Lapinlahti");
        kunnat.add("Lappajärvi");

        kunnat.add("Lappeenranta");
        kunnat.add("Lapua");
        kunnat.add("Laukaa");
        kunnat.add("Lemi");
        kunnat.add("Lemland");
        kunnat.add("Lempäälä");
        kunnat.add("Leppävirta");
        kunnat.add("Lestijärvi");
        kunnat.add("Lieksa");
        kunnat.add("Lieto");

        kunnat.add("Liminka");
        kunnat.add("Liperi");
        kunnat.add("Lohja");
        kunnat.add("Loimaa");
        kunnat.add("Loppi");
        kunnat.add("Loviisa");
        kunnat.add("Luhanka");
        kunnat.add("Lumijoki");
        kunnat.add("Lumparland");
        kunnat.add("Luoto");

        kunnat.add("Luumäki");
        kunnat.add("Maalahti");
        kunnat.add("Maarianhamina");
        kunnat.add("Marttila");
        kunnat.add("Masku");
        kunnat.add("Merijärvi");
        kunnat.add("Merikarvia");
        kunnat.add("Miehikkälä");
        kunnat.add("Mikkeli");
        kunnat.add("Muhos");

        kunnat.add("Multia");
        kunnat.add("Muonio");
        kunnat.add("Mustasaari");
        kunnat.add("Muurame");
        kunnat.add("Mynämäki");
        kunnat.add("Myrskylä");
        kunnat.add("Mäntsälä");
        kunnat.add("Mänttä-vilppula");
        kunnat.add("Mänttä");
        kunnat.add("Vilppula");
        kunnat.add("Mäntyharju");
        kunnat.add("Naantali");

        kunnat.add("Nakkila");
        kunnat.add("Nivala");
        kunnat.add("Nokia");
        kunnat.add("Nousiainen");
        kunnat.add("Nurmes");
        kunnat.add("Nurmijärvi");
        kunnat.add("Närpiö");
        kunnat.add("Orimattila");
        kunnat.add("Oripää");
        kunnat.add("Orivesi");

        kunnat.add("Oulainen");
        kunnat.add("Oulu");
        kunnat.add("Outokumpu");
        kunnat.add("Padasjoki");
        kunnat.add("Paimio");
        kunnat.add("Paltamo");
        kunnat.add("Parainen");
        kunnat.add("Parikkala");
        kunnat.add("Parkano");
        kunnat.add("Pedersören kunta");
        kunnat.add("Pedersöre");

        kunnat.add("Pelkosenniemi");
        kunnat.add("Pello");
        kunnat.add("Perho");
        kunnat.add("Pertunmaa");
        kunnat.add("Petäjävesi");
        kunnat.add("Pieksämäki");
        kunnat.add("Pielavesi");
        kunnat.add("Pietarsaari");
        kunnat.add("Pihtipudas");
        kunnat.add("Pirkkala");

        kunnat.add("Polvijärvi");
        kunnat.add("Pomarkku");
        kunnat.add("Pori");
        kunnat.add("Pornainen");
        kunnat.add("Porvoo");
        kunnat.add("Posio");
        kunnat.add("Pudasjärvi");
        kunnat.add("Pukkila");
        kunnat.add("Punkalaidun");
        kunnat.add("Puolanka");

        kunnat.add("Puumala");
        kunnat.add("Pyhtää");
        kunnat.add("Pyhäjoki");
        kunnat.add("Pyhäjärvi");
        kunnat.add("Pyhäntä");
        kunnat.add("Pyhäranta");
        kunnat.add("Pälkäne");
        kunnat.add("Pöytyä");
        kunnat.add("Raahe");
        kunnat.add("Raasepori");

        kunnat.add("Raisio");
        kunnat.add("Rantasalmi");
        kunnat.add("Ranua");
        kunnat.add("Rauma");
        kunnat.add("Rautalampi");
        kunnat.add("Rautavaara");
        kunnat.add("Rautjärvi");
        kunnat.add("Reisjärvi");
        kunnat.add("Riihimäki");
        kunnat.add("Ristijärvi");

        kunnat.add("Rovaniemi");
        kunnat.add("Ruokolahti");
        kunnat.add("Ruovesi");
        kunnat.add("Rusko");
        kunnat.add("Rääkkylä");
        kunnat.add("Saarijärvi");
        kunnat.add("Salla");
        kunnat.add("Salo");
        kunnat.add("Saltvik");
        kunnat.add("Sastamala");

        kunnat.add("Sauvo");
        kunnat.add("Savitaipale");
        kunnat.add("Savonlinna");
        kunnat.add("Savukoski");
        kunnat.add("Seinäjoki");
        kunnat.add("Sievi");
        kunnat.add("Siikainen");
        kunnat.add("Siikajoki");
        kunnat.add("Siikalatva");
        kunnat.add("Siilinjärvi");

        kunnat.add("Simo");
        kunnat.add("Sipoo");
        kunnat.add("Siuntio");
        kunnat.add("Sodankylä");
        kunnat.add("Soini");
        kunnat.add("Somero");
        kunnat.add("Sonkajärvi");
        kunnat.add("Sotkamo");
        kunnat.add("Sottunga");
        kunnat.add("Sulkava");

        kunnat.add("Sund");
        kunnat.add("Suomussalmi");
        kunnat.add("Suonenjoki");
        kunnat.add("Sysmä");
        kunnat.add("Säkylä");
        kunnat.add("Taipalsaari");
        kunnat.add("Taivalkoski");
        kunnat.add("Taivassalo");
        kunnat.add("Tammela");
        kunnat.add("Tampere");

        kunnat.add("Tervo");
        kunnat.add("Tervola");
        kunnat.add("Teuva");
        kunnat.add("Tohmajärvi");
        kunnat.add("Toholampi");
        kunnat.add("Toivakka");
        kunnat.add("Tornio");
        kunnat.add("Turku");
        kunnat.add("Tuusniemi");
        kunnat.add("Tuusula");

        kunnat.add("Tyrnävä");
        kunnat.add("Ulvila");
        kunnat.add("Urjala");
        kunnat.add("Utajärvi");
        kunnat.add("Utsjoki");
        kunnat.add("Uurainen");
        kunnat.add("Uusikaarlepyy");
        kunnat.add("Uusikaupunki");
        kunnat.add("Vaala");
        kunnat.add("Vaasa");

        kunnat.add("Valkeakoski");
        kunnat.add("Valtimo");
        kunnat.add("Vantaa");
        kunnat.add("Varkaus");
        kunnat.add("Vehmaa");
        kunnat.add("Vesanto");
        kunnat.add("Vesilahti");
        kunnat.add("Veteli");
        kunnat.add("Vieremä");
        kunnat.add("Vihti");

        kunnat.add("Viitasaari");
        kunnat.add("Vimpeli");
        kunnat.add("Virolahti");
        kunnat.add("Virrat");
        kunnat.add("Vårdö");
        kunnat.add("Vöyri");
        kunnat.add("Ylitornio");
        kunnat.add("Ylivieska");
        kunnat.add("Ylöjärvi");
        kunnat.add("Ypäjä");

        kunnat.add("Ähtäri");
        kunnat.add("Äänekoski");
        //Toast.makeText(aktiivinenHaly.this, "Kuntia listassa " + kunnat.size() + ".",Toast.LENGTH_LONG).show();
    }

    public void lisaaHalyTunnukset() {

        halytunnukset.add("103");
        halytekstit.add("PALOHÄLYTYS");
        halytunnukset.add("104");
        halytekstit.add("SÄTEILYHÄLYTYS");
        halytunnukset.add("105");
        halytekstit.add("HISSIHÄLYTYS");
        halytunnukset.add("106");
        halytekstit.add("LAITEVIKA");
        halytunnukset.add("107");
        halytekstit.add("YHTEYSVIKA");
        halytunnukset.add("108");
        halytekstit.add("HUOLTO");
        halytunnukset.add("200");
        halytekstit.add("TIELIIKENNE: MUU TAI ONNETTOMUUDEN UHKA");
        halytunnukset.add("201");
        halytekstit.add("TIELIIKENNE: PELTIKOLARI, SUISTUMINEN");
        halytunnukset.add("202");
        halytekstit.add("TIELIIKENNE: PIENI");
        halytunnukset.add("203");
        halytekstit.add("TIELIIKENNE: KESKISUURI");
        halytunnukset.add("204");
        halytekstit.add("TIELIIKENNE: SUURI");
        halytunnukset.add("205");
        halytekstit.add("TIELIIKENNE: ELÄIN OSALLISEENA");
        halytunnukset.add("206");
        halytekstit.add("TIELIIKENNE: MAAN ALLA: PIENI");
        halytunnukset.add("207");
        halytekstit.add("TIELIIKENNE: MAAN ALLA: KESKISUURI");
        halytunnukset.add("208");
        halytekstit.add("TIELIIKENNE: MAAN ALLA: SUURI");
        halytunnukset.add("210");
        halytekstit.add("RAIDELIIKENNE: MUU");
        halytunnukset.add("211");
        halytekstit.add("RAIDELIIKENNE: PELTIKOLARI");
        halytunnukset.add("212");
        halytekstit.add("RAIDELIIKENNE: PIENI");
        halytunnukset.add("213");
        halytekstit.add("RAIDELIIKENNE: KESKISUURI");
        halytunnukset.add("214");
        halytekstit.add("RAIDELIIKENNE: SUURI");
        halytunnukset.add("215");
        halytekstit.add("RAIDELIIKENNE: ELÄIN OSALLISENA");
        halytunnukset.add("216");
        halytekstit.add("RAIDELIIKENNE: MAAN ALLA: PIENI");
        halytunnukset.add("217");
        halytekstit.add("RAIDELIIKENNE: MAAN ALLA: KESKISUURI");
        halytunnukset.add("218");
        halytekstit.add("RAIDELIIKENNE: MAAN ALLA: SUURI");
        halytunnukset.add("220");
        halytekstit.add("VESILIIKENNE: MUU");
        halytunnukset.add("221");
        halytekstit.add("VESILIIKENNE: PIENI");
        halytunnukset.add("222");
        halytekstit.add("VESILIIKENNE: KESKISUURI");
        halytunnukset.add("223");
        halytekstit.add("VESILIIKENNE: SUURI");
        halytunnukset.add("231");
        halytekstit.add("ILMALIIKENNEONNETTOMUUS: PIENI");
        halytunnukset.add("232");
        halytekstit.add("ILMALIIKENNEONNETTOMUUS: KESKISUURI");
        halytunnukset.add("233");
        halytekstit.add("ILMALIIKENNEONNETTOMUUS: SUURI");
        halytunnukset.add("234");
        halytekstit.add("ILMALIIKENNE VAARA: PIENI");
        halytunnukset.add("235");
        halytekstit.add("ILMALIIKENNE VAARA: KESKISUURI");
        halytunnukset.add("236");
        halytekstit.add("ILMALIIKENNE VAARA: SUURI");
        halytunnukset.add("271");
        halytekstit.add("MAASTOLIIKENNEONNETTOMUUS");
        halytunnukset.add("H351");
        halytekstit.add("VARIKKO TAI ASEMAVALMIUS");
        halytunnukset.add("401");
        halytekstit.add("RAKENNUSPALO: PIENI");
        halytunnukset.add("402");
        halytekstit.add("RAKENNUSPALO: KESKISUURI");
        halytunnukset.add("403");
        halytekstit.add("RAKENNUSPALO: SUURI");
        halytunnukset.add("404");
        halytekstit.add("RAKENNUSPALO: MAAN ALLA: PIENI");
        halytunnukset.add("405");
        halytekstit.add("RAKENNUSPALO: MAAN ALLA: KESKISUURI");
        halytunnukset.add("406");
        halytekstit.add("RAKENNUSPALO: MAAN ALLA: SUURI");
        halytunnukset.add("411");
        halytekstit.add("LIIKENNEVÄLINEPALO: PIENI");
        halytunnukset.add("412");
        halytekstit.add("LIIKENNEVÄLINEPALO: KESKISUURI");
        halytunnukset.add("413");
        halytekstit.add("LIIKENNEVÄLINEPALO: SUURI");
        halytunnukset.add("414");
        halytekstit.add("LIIKENNEVÄLINEPALO: MAAN ALLA: PIENI");
        halytunnukset.add("415");
        halytekstit.add("LIIKENNEVÄLINEPALO: MAAN ALLA: KESKISUURI");
        halytunnukset.add("416");
        halytekstit.add("LIIKENNEVÄLINEPALO: MAAN ALLA: SUURI");
        halytunnukset.add("420");
        halytekstit.add("SAVUHAVAINTO");
        halytunnukset.add("421");
        halytekstit.add("MAASTOPALO: PIENI");
        halytunnukset.add("422");
        halytekstit.add("MAASTOPALO: KESKISUURI");
        halytunnukset.add("423");
        halytekstit.add("MAASTOPALO: SUURI");
        halytunnukset.add("424");
        halytekstit.add("TURVETUOTANTOALUEPALO: PIENI");
        halytunnukset.add("425");
        halytekstit.add("TURVETUOTANTOALUEPALO: KESKISUURI");
        halytunnukset.add("426");
        halytekstit.add("TURVETUOTANTOALUEPALO: SUURI");
        halytunnukset.add("431");
        halytekstit.add("TULIPALO, MUU: PIENI");
        halytunnukset.add("432");
        halytekstit.add("TULIPALO, MUU: KESKISUURI");
        halytunnukset.add("433");
        halytekstit.add("TULIPALO, MUU: SUURI");
        halytunnukset.add("434");
        halytekstit.add("TULIPALO, MUU: MAAN ALLA: PIENI");
        halytunnukset.add("435");
        halytekstit.add("TULIPALO, MUU: MAAN ALLA: KESKISUURI");
        halytunnukset.add("436");
        halytekstit.add("TULIPALO, MUU: MAAN ALLA: SUURI");
        halytunnukset.add("441");
        halytekstit.add("RÄJÄHDYS/SORTUMA: PIENI");
        halytunnukset.add("442");
        halytekstit.add("RÄJÄHDYS/SORTUMA: KESKISUURI");
        halytunnukset.add("443");
        halytekstit.add("RÄJÄHDYS/SORTUMA: SUURI");
        halytunnukset.add("444");
        halytekstit.add("RÄJÄHDYS-/SORTUMAVAARA");
        halytunnukset.add("451");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: PIENI");
        halytunnukset.add("452");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: KESKISUURI");
        halytunnukset.add("453");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: SUURI");
        halytunnukset.add("455");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: ONNETTOMUUSVAARA");
        halytunnukset.add("461");
        halytekstit.add("VAHINGONTORJUNTA: PIENI");
        halytunnukset.add("462");
        halytekstit.add("VAHINGONTORJUNTA: KESKISUURI");
        halytunnukset.add("463");
        halytekstit.add("VAHINGONTORJUNTA: SUURI");
        halytunnukset.add("471");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: MAALLA: PIENI");
        halytunnukset.add("472");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: MAALLA: KESKISUURI");
        halytunnukset.add("473");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: MAALLA: SUURI");
        halytunnukset.add("474");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: PIENI");
        halytunnukset.add("475");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: KESKISUURI");
        halytunnukset.add("476");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: SUURI");
        halytunnukset.add("477");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: ONNETTOMUUSVAARA");
        halytunnukset.add("480");
        halytekstit.add("IHMISEN PELASTAMINEN: MUU");
        halytunnukset.add("481");
        halytekstit.add("IHMISEN PELASTAMINEN: ETSINTÄ");
        halytunnukset.add("482");
        halytekstit.add("IHMISEN PELASTAMINEN: AVUNANTO");
        halytunnukset.add("483");
        halytekstit.add("IHMISEN PELASTAMINEN: VEDESTÄ");
        halytunnukset.add("484");
        halytekstit.add("IHMISEN PELASTAMINEN: PINTAPELASTUS");
        halytunnukset.add("485");
        halytekstit.add("IHMISEN PELASTAMINEN: MAASTOSTA");
        halytunnukset.add("486");
        halytekstit.add("IHMISEN PELASTAMINEN: PURISTUKSISTA");
        halytunnukset.add("487");
        halytekstit.add("IHMISEN PELASTAMINEN: YLHÄÄLTÄ/ALHAALTA");
        halytunnukset.add("490");
        halytekstit.add("EPÄSELVÄ ONNETTOMUUS");
        halytunnukset.add("491");
        halytekstit.add("LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: PIENI");
        halytunnukset.add("492");
        halytekstit.add("LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: KESKISUURI");
        halytunnukset.add("493");
        halytekstit.add("LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: SUURI");
        halytunnukset.add("550");
        halytekstit.add("AVUNANTO: MUU");
        halytunnukset.add("551");
        halytekstit.add("VIRKA-APUTEHTÄVÄ");
        halytunnukset.add("552");
        halytekstit.add("AVUNANTOTEHTÄVÄ");
        halytunnukset.add("553");
        halytekstit.add("UHKA-/VARUILLAOLO");
        halytunnukset.add("554");
        halytekstit.add("TARKISTUS-/VARMISTUS");
        halytunnukset.add("580");
        halytekstit.add("ELÄINTEHTÄVÄ: MUU");
        halytunnukset.add("581");
        halytekstit.add("ELÄIMEN PELASTAMINEN");
        halytunnukset.add("700");
        halytekstit.add("Eloton");
        halytunnukset.add("701");
        halytekstit.add("Elvytys");
        halytunnukset.add("702");
        halytekstit.add("Tajuttomuus");
        halytunnukset.add("703");
        halytekstit.add("Hengitysvaikeus");
        halytunnukset.add("704");
        halytekstit.add("Rintakipu");
        halytunnukset.add("705");
        halytekstit.add("PEH; Muu äkillisesti heikentynyt yleistila");
        halytunnukset.add("706");
        halytekstit.add("Aivohalvaus");
        halytunnukset.add("710");
        halytekstit.add("Tukehtuminen");
        halytunnukset.add("711");
        halytekstit.add("Ilmatie-este");
        halytunnukset.add("712");
        halytekstit.add("Jääminen suljettuun tilaan");
        halytunnukset.add("713");
        halytekstit.add("Hirttäytyminen, Kuristuminen");
        halytunnukset.add("714");
        halytekstit.add("Hukuksiin joutuminen");
        halytunnukset.add("741");
        halytekstit.add("Putoaminen");
        halytunnukset.add("744");
        halytekstit.add("Haava");
        halytunnukset.add("745");
        halytekstit.add("Kaatuminen");
        halytunnukset.add("746");
        halytekstit.add("Isku");
        halytunnukset.add("747");
        halytekstit.add("Vamma; muu");
        halytunnukset.add("751");
        halytekstit.add("Kaasumyrkytys");
        halytunnukset.add("752");
        halytekstit.add("Myrkytys");
        halytunnukset.add("753");
        halytekstit.add("Sähköisku");
        halytunnukset.add("755");
        halytekstit.add("Palovamma, lämpöhalvaus");
        halytunnukset.add("756");
        halytekstit.add("Alilämpöisyys");
        halytunnukset.add("757");
        halytekstit.add("Onnettomuus; muu");
        halytunnukset.add("761");
        halytekstit.add("Verenvuoto, Suusta");
        halytunnukset.add("762");
        halytekstit.add("Verenvuoto, Gynekologinen/urologinen");
        halytunnukset.add("763");
        halytekstit.add("Vernevuoto, Korva/nenä");
        halytunnukset.add("764");
        halytekstit.add("Säärihaava/Muu");
        halytunnukset.add("770");
        halytekstit.add("Sairauskohtaus");
        halytunnukset.add("771");
        halytekstit.add("Sokeritasapainon häiriö");
        halytunnukset.add("772");
        halytekstit.add("Kouristelu");
        halytunnukset.add("773");
        halytekstit.add("Yliherkkyysreaktio");
        halytunnukset.add("774");
        halytekstit.add("Heikentynyt yleistila, muu sairaus");
        halytunnukset.add("775");
        halytekstit.add("Oksentelu, Ripuli");
        halytunnukset.add("781");
        halytekstit.add("Vatsakipu");
        halytunnukset.add("782");
        halytekstit.add("Pää-/Niskasärky");
        halytunnukset.add("783");
        halytekstit.add("Selkä-/raaja-/vartalokipu");
        halytunnukset.add("784");
        halytekstit.add("Aistioire");
        halytunnukset.add("785");
        halytekstit.add("Mielenterveysongelma");
        halytunnukset.add("790");
        halytekstit.add("Hälytys puhelun aikana");
        halytunnukset.add("791");
        halytekstit.add("Synnytys");
        halytunnukset.add("792");
        halytekstit.add("Varallaolo, valmiussiirto");
        halytunnukset.add("793");
        halytekstit.add("Hoitolaitossiirto");
        halytunnukset.add("794");
        halytekstit.add("Muu sairaankuljetustehtävä");
        halytunnukset.add("796");
        halytekstit.add("Monipotilastilanne/Suuronnettomuus");
        halytunnukset.add("901");
        halytekstit.add("PELASTUSTOIMI POIKKEUSOLOISSA");
    }
}
