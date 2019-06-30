package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class HalytysActivity extends AppCompatActivity
        implements HalytysButtonsFragment.Listener, AsematauluButtonsFragment.OnFragmentInteractionListener, HalytysFragment.Listener, AnswerOHTOFragment.OnFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    boolean koneluku, autoAukaisu, asemataulu, responderFragmentShowing;
    private String action, type, currentPhotoPath;
    SharedPreferences preferences;
    FragmentManager fragmentManager;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        asemataulu = preferences.getBoolean("asemataulu", false);
        preferences.edit().putBoolean("responderFragmentShowing", false).commit();
        if (!asemataulu) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_halytys);
        Intent intent = getIntent();
        action = intent.getAction();
        type = intent.getType();
        koneluku = preferences.getBoolean("koneluku", false);
        autoAukaisu = preferences.getBoolean("autoAukaisu", false);

        constraintLayout = findViewById(R.id.activity_halytys);

    }

    public void changeLayout() {
        constraintSet = new ConstraintSet();
        //constraintSet.clone(constraintLayout);
        constraintSet.load(this, R.layout.halytys_activity_ohto);
        constraintSet.applyTo(constraintLayout);
    }

    public void changeLayoutBack() {
        constraintSet = new ConstraintSet();
        //constraintSet.clone(constraintLayout);
        constraintSet.load(this, R.layout.activity_halytys);
        constraintSet.applyTo(constraintLayout);
    }

    public void hiljenna() {
        HalytysFragment halytysFragment = (HalytysFragment)
                getSupportFragmentManager().findFragmentByTag("halytysFragment");
        if (halytysFragment != null) {
            halytysFragment.lopetaPuhe();
        }
    }

    public void autoAukaisuPuhu() {
        HalytysFragment halytysFragment = (HalytysFragment)
                getSupportFragmentManager().findFragmentByTag("halytysFragment");
        //Log.i("HalytysActivity", halytysFragment.toString());
        if (halytysFragment != null) {
            halytysFragment.txtToSpeech();
        }
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
        getParameters(action, type);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void loadAsematauluButtons() {
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
        fragmentTransaction.replace(R.id.HalytysAlaosa, answerOHTOFragment, "answerOHTOFragment").commit();
    }

    @SuppressLint("ApplySharedPref")
    public void loadResponderFragment() {
        responderFragmentShowing = preferences.getBoolean("responderFragmentShowing", false);
        if(!responderFragmentShowing) {
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
        if(findViewById(R.id.responder_view) != null) {
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

    public void waitForFragment() {
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                HalytysFragment halytysFragment = (HalytysFragment)
                        getSupportFragmentManager().findFragmentByTag("halytysFragment");
                //Log.i("HalytysActivity", halytysFragment.toString());
                if (halytysFragment != null) {
                    halytysFragment.txtToSpeech();
                }
                HalytysButtonsFragment halytysButtonsFragment = (HalytysButtonsFragment)
                        getSupportFragmentManager().findFragmentByTag("halytysButtonsFragment");
                if (halytysButtonsFragment != null) {
                    halytysButtonsFragment.setTextHiljennaPuhe();
                }
            }
        }, 1000);
        action = null;
        type = null;
    }

    public void waitForButtonsFragment() {
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                HalytysButtonsFragment halytysButtonsFragment = (HalytysButtonsFragment)
                        getSupportFragmentManager().findFragmentByTag("halytysButtonsFragment");
                if (halytysButtonsFragment != null) {
                    halytysButtonsFragment.autoAukaisu();
                }
            }
        }, 1000);
        action = null;
        type = null;
    }

    @SuppressLint("ApplySharedPref")
    public void getParameters(String action, String type) {

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Intent stopAlarm = new Intent(HalytysActivity.this, IsItAlarmService.class);
                HalytysActivity.this.stopService(stopAlarm);
                if (koneluku && !autoAukaisu) {
                    waitForFragment();
                }
            } else if ("automaattinen".equals(type)) {
                preferences.edit().putBoolean("showHiljenna", true).commit();
                waitForButtonsFragment();
            }
        }
    }

    /**
     * AsematauluButtonsFragment methods below this
     *
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
}
