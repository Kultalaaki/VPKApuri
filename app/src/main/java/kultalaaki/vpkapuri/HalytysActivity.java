package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class HalytysActivity extends AppCompatActivity
        implements HalytysButtonsFragment.Listener, AsematauluButtonsFragment.OnFragmentInteractionListener, HalytysFragment.Listener, AnswerOHTOFragment.OnFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    boolean koneluku, autoAukaisu, asemataulu, responderFragmentShowing;
    private String currentPhotoPath;
    private String viesti = "", tunnus = "", kiireellisyysLuokka = "", osoite = "", numero = "", aikaleima = "";
    SharedPreferences preferences;
    private TextToSpeech textToSpeech;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        asemataulu = preferences.getBoolean("asemataulu", false);
        preferences.edit().putBoolean("responderFragmentShowing", false).commit();
        if (!asemataulu) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_halytys);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        koneluku = preferences.getBoolean("koneluku", false);
        autoAukaisu = preferences.getBoolean("autoAukaisu", false);

        FireAlarmViewModel mViewModel = ViewModelProviders.of(this).get(FireAlarmViewModel.class);
        mViewModel.getLastEntry().observe(this, new Observer<List<FireAlarm>>() {
            @Override
            public void onChanged(List<FireAlarm> fireAlarms) {
                if (!fireAlarms.isEmpty()) {
                    FireAlarm currentAlarm = fireAlarms.get(0);
                    viesti = currentAlarm.getViesti();
                    tunnus = currentAlarm.getTunnus();
                    kiireellisyysLuokka = currentAlarm.getLuokka();
                    osoite = currentAlarm.getOsoite();
                    numero = currentAlarm.getOptionalField2();
                    aikaleima = currentAlarm.getTimeStamp();
                    AsematauluButtonsFragment asematauluButtonsFragment = (AsematauluButtonsFragment)
                            getSupportFragmentManager().findFragmentByTag("asematauluButtonsFragment");
                    HalytysFragment halytysFragment = (HalytysFragment)
                            getSupportFragmentManager().findFragmentByTag("halytysFragment");
                    AnswerOHTOFragment answerOHTOFragment = (AnswerOHTOFragment)
                            getSupportFragmentManager().findFragmentByTag("answerOHTOFragment");
                    HalytysButtonsFragment halytysButtonsFragment = (HalytysButtonsFragment)
                            getSupportFragmentManager().findFragmentByTag("halytysButtonsFragment");
                    if (tunnus.equals("OHTO Hälytys")) {
                        loadOHTOAnswer();
                    } else {
                        if (asemataulu) {
                            if (asematauluButtonsFragment == null) {
                                loadAsematauluButtons();
                            }
                        } else {
                            if (halytysButtonsFragment == null) {
                                loadhalytysButtonsFragment();
                            }
                        }
                    }

                    if (halytysFragment != null) {
                        // Notify. Uses viesti, tunnus, aikaleima ja kiireellisyysluokka.
                        halytysFragment.setTexts(viesti, tunnus, kiireellisyysLuokka, aikaleima);
                    }

                    if (halytysButtonsFragment != null) {
                        // Notify. Osoite.
                        halytysButtonsFragment.setOsoite(osoite);
                    }

                    if (asematauluButtonsFragment != null) {
                        // Notify. Osoite.
                        asematauluButtonsFragment.setOsoite(osoite);
                    }

                    if (answerOHTOFragment != null) {
                        // Notify. Numero,
                        answerOHTOFragment.setNumero(numero);
                    }

                }
            }
        });

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Intent stopAlarm = new Intent(HalytysActivity.this, IsItAlarmService.class);
                HalytysActivity.this.stopService(stopAlarm);
                if (koneluku && !autoAukaisu) {
                    //waitForFragment();
                    startTextToSpeech();
                    // Hälynappulat setTextHiljenna();
                }
            } else if ("automaattinen".equals(type)) {
                preferences.edit().putBoolean("showHiljenna", true).commit();
                // halytysButtonsFragment.autoAukaisu();
                //waitForButtonsFragment();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadhalytysFragment();
        if (asemataulu) {
            // TODO: Asemataulu käytössä
            loadAsematauluButtons();
            if (findViewById(R.id.responder_view) != null) {
                loadResponderFragment();
            }
        } else {
            loadhalytysButtonsFragment();
        }
        checkDoNotDisturb();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (palautaMediaVolBoolean) {
            AudioManager ad = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (ad != null) {
                ad.setStreamVolume(AudioManager.STREAM_MUSIC, palautaMediaVol, 0);
            }

        }
        preferences.edit().putBoolean("HalytysOpen", false).commit();
    }

    public String returnOsoite() {
        return osoite;
    }

    public String returnViesti() {
        return viesti;
    }

    public String returnTunnus() {
        return tunnus;
    }

    public String returnKiireellisyysLuokka() {
        return kiireellisyysLuokka;
    }

    public String returnNumero() {
        return numero;
    }

    public String returnAikaleima() {
        return aikaleima;
    }

    public void avaaWebSivu(String url) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        WebviewFragment webviewFragment = WebviewFragment.newInstance(url);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_down, R.animator.slide_out_left);
        fragmentTransaction.replace(R.id.HalytysYlaosa, webviewFragment, "webviewFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    void loadAsematauluButtons() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        AsematauluButtonsFragment asematauluButtonsFragment = new AsematauluButtonsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_up);
        fragmentTransaction.add(R.id.HalytysAlaosa, asematauluButtonsFragment, "asematauluButtonsFragment").commit();
    }

    public void loadOHTOAnswer() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        AnswerOHTOFragment answerOHTOFragment = new AnswerOHTOFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.HalytysAlaosa, answerOHTOFragment, "answerOHTOFragment").commit();
    }

    @SuppressLint("ApplySharedPref")
    public void loadResponderFragment() {
        responderFragmentShowing = preferences.getBoolean("responderFragmentShowing", false);
        if (!responderFragmentShowing) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            ResponderFragment responderFragment = new ResponderFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (findViewById(R.id.responder_view) != null) {
                fragmentTransaction.replace(R.id.responder_view, responderFragment, "ResponderFragment").commit();
                preferences.edit().putBoolean("responderFragmentShowing", true).commit();
            } else {
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_down, R.animator.slide_out_down);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.HalytysYlaosa, responderFragment, "ResponderFragment").commit();
                preferences.edit().putBoolean("responderFragmentShowing", true).commit();
            }
        }
    }

    public void updateAddress(String updatedAddress) {
        if (asemataulu) {
            AsematauluButtonsFragment asematauluButtonsFragment = (AsematauluButtonsFragment)
                    getSupportFragmentManager().findFragmentByTag("asematauluButtonsFragment");
            if (asematauluButtonsFragment != null) {
                asematauluButtonsFragment.updateAddress(updatedAddress);
            }
        } else {
            HalytysButtonsFragment halytysButtonsFragment = (HalytysButtonsFragment)
                    getSupportFragmentManager().findFragmentByTag("halytysButtonsFragment");
            if (halytysButtonsFragment != null) {
                halytysButtonsFragment.updateAddress(updatedAddress);
            }
        }
    }

    public void loadhalytysFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        HalytysFragment halytysFragment = new HalytysFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_down, R.animator.slide_out_down);
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.HalytysYlaosa, halytysFragment, "halytysFragment").commit();
    }

    public void loadManpowerFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        ManpowerFragment manpowerFragment = new ManpowerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        if (findViewById(R.id.responder_view) != null) {
            fragmentTransaction.replace(R.id.responder_view, manpowerFragment, "manpowerFragment").commit();
        } else {
            fragmentTransaction.replace(R.id.HalytysYlaosa, manpowerFragment, "manpowerFragment").commit();
        }
    }

    public void loadhalytysButtonsFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        HalytysButtonsFragment halytysButtonsFragment = new HalytysButtonsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_up);
        fragmentTransaction.add(R.id.HalytysAlaosa, halytysButtonsFragment, "halytysButtonsFragment").commit();
    }

    /**
     * AsematauluButtonsFragment methods below this
     * <p>
     * <--Methods to take picture and add it to gallery-->
     * openCamera
     * createImageFile
     * galleryAddPic
     * onActivityResult
     */

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Camera", "Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "kultalaaki.vpkapuri",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy_H:mm:ss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File storageApuri = getExternalFilesDir("VPK Apuri");

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //setPic();
            galleryAddPic();
        }
    }

    /**
     * Text to speech
     */
    private int palautaMediaVol, tekstiPuheeksiVol;
    private boolean palautaMediaVolBoolean = false;

    public void startTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Kieli ei ole tuettu.", Toast.LENGTH_LONG).show();
                    }

                    txtToSpeechVolume();
                } else {
                    Toast.makeText(getApplicationContext(), "Virhe", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void txtToSpeechVolume() {

        AudioManager ad = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (ad != null) {
            palautaMediaVol = ad.getStreamVolume(AudioManager.STREAM_MUSIC);
            palautaMediaVolBoolean = true;
            ad.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 0);
            // teksti puheeksi äänenvoimakkuus
            try {
                SharedPreferences prefe_general = PreferenceManager.getDefaultSharedPreferences(this);
                tekstiPuheeksiVol = prefe_general.getInt("tekstiPuheeksiVol", -1);
                tekstiPuheeksiVol = saadaAani(tekstiPuheeksiVol);
                ad.setStreamVolume(AudioManager.STREAM_MUSIC, tekstiPuheeksiVol, 0);
                puhu();
            } catch (Exception e) {
                Log.i("VPK Apuri", "Teksti puheeksi äänenvoimakkuuden lukeminen asetuksista epäonnistui.");
            }
        }
    }

    private int saadaAani(int voima) {

        final AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            tekstiPuheeksiVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            double aani = (double) tekstiPuheeksiVol / 100 * voima;
            tekstiPuheeksiVol = (int) aani;
        }
        if (tekstiPuheeksiVol == 0) {
            return 1;
        } else if (tekstiPuheeksiVol == 1) {
            return 1;
        } else if (tekstiPuheeksiVol == 2) {
            return 2;
        } else if (tekstiPuheeksiVol == 3) {
            return 3;
        } else if (tekstiPuheeksiVol == 4) {
            return 4;
        } else if (tekstiPuheeksiVol == 5) {
            return 5;
        } else if (tekstiPuheeksiVol == 6) {
            return 6;
        } else if (tekstiPuheeksiVol == 7) {
            return 7;
        }

        return tekstiPuheeksiVol;
    }

    private void puhu() {
        String puheeksi = tunnus + " " + viesti;
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech.playSilentUtterance(1000, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            textToSpeech.playSilence(1000, TextToSpeech.QUEUE_FLUSH, null);
        }
        textToSpeech.speak(puheeksi, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void lopetaPuhe() {
        AudioManager ad = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (ad != null) {
            ad.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }
    }

    /**
     * Other methods
     */
    private void checkDoNotDisturb() {
        boolean disturb = preferences.getBoolean("DoNotDisturb", false);
        boolean asemataulu = preferences.getBoolean("asemataulu", false);
        if (!disturb && !asemataulu) {
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                    Toast.makeText(this, "Sovelluksella ei ole lupaa säädellä Älä häiritse tilaa.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
