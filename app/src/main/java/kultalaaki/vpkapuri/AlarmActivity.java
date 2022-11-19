/*
 * Created by Kultala Aki on 2/14/21 9:02 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 2/14/21 8:59 PM
 */

package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kultalaaki.vpkapuri.Fragments.AlarmButtonsFragment;
import kultalaaki.vpkapuri.Fragments.AlarmFragment;
import kultalaaki.vpkapuri.Fragments.AnswerOHTOFragment;
import kultalaaki.vpkapuri.Fragments.ResponderFragment;
import kultalaaki.vpkapuri.Fragments.StationboardButtonsFragment;
import kultalaaki.vpkapuri.Fragments.WebviewFragment;
import kultalaaki.vpkapuri.services.SMSBackgroundService;


public class AlarmActivity extends AppCompatActivity
        implements AlarmButtonsFragment.Listener, StationboardButtonsFragment.OnFragmentInteractionListener, AlarmFragment.Listener, AnswerOHTOFragment.OnFragmentInteractionListener, ResponderFragment.OnFragmentInteractionListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    boolean koneluku, asemataulu, responderFragmentShowing;
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

        constraintLayout = findViewById(R.id.activity_halytys);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadhalytysFragment();
        if (asemataulu) {
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

    @Override
    protected void onPause() {
        super.onPause();
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    public void changeLayout() {
        if (findViewById(R.id.responder_view) == null) {
            constraintSet = new ConstraintSet();
            //constraintSet.clone(constraintLayout);
            constraintSet.load(this, R.layout.halytys_activity_ohto);
            constraintSet.applyTo(constraintLayout);
        }
    }

    public void changeLayoutBack() {
        constraintSet = new ConstraintSet();
        //constraintSet.clone(constraintLayout);
        constraintSet.load(this, R.layout.activity_halytys);
        constraintSet.applyTo(constraintLayout);
    }

    public void hiljenna() {
        AlarmFragment alarmFragment = (AlarmFragment)
                getSupportFragmentManager().findFragmentByTag("alarmFragment");
        if (alarmFragment != null) {
            alarmFragment.lopetaPuhe();
        }
    }

    public void autoAukaisuPuhu() {
        AlarmFragment alarmFragment = (AlarmFragment)
                getSupportFragmentManager().findFragmentByTag("alarmFragment");
        //Log.i("AlarmActivity", alarmFragment.toString());
        if (alarmFragment != null) {
            alarmFragment.txtToSpeech();
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


    public void loadAsematauluButtons() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        StationboardButtonsFragment stationboardButtonsFragment = new StationboardButtonsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_up);
        fragmentTransaction.add(R.id.HalytysAlaosa, stationboardButtonsFragment, "stationboardButtonsFragment").commit();
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
        if (!responderFragmentShowing) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            ResponderFragment responderFragment = new ResponderFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (findViewById(R.id.responder_view) != null) {
                fragmentTransaction.replace(R.id.responder_view, responderFragment, "ResponderFragment").commit();
            } else {
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_down, R.animator.slide_out_down);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.HalytysYlaosa, responderFragment, "ResponderFragment").commit();
            }
            preferences.edit().putBoolean("responderFragmentShowing", true).commit();
        }
    }

    public void loadhalytysFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        AlarmFragment alarmFragment = new AlarmFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_down, R.animator.slide_out_down);
        fragmentTransaction.add(R.id.HalytysYlaosa, alarmFragment, "alarmFragment").commit();
    }

    public void loadhalytysButtonsFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        AlarmButtonsFragment alarmButtonsFragment = new AlarmButtonsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_up);
        fragmentTransaction.add(R.id.HalytysAlaosa, alarmButtonsFragment, "alarmButtonsFragment").commit();
    }

    public void waitForFragment() {
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
            AlarmFragment alarmFragment = (AlarmFragment)
                    getSupportFragmentManager().findFragmentByTag("alarmFragment");
            if (alarmFragment != null) {
                alarmFragment.txtToSpeech();
            }
            AlarmButtonsFragment alarmButtonsFragment = (AlarmButtonsFragment)
                    getSupportFragmentManager().findFragmentByTag("alarmButtonsFragment");
            if (alarmButtonsFragment != null) {
                alarmButtonsFragment.setTextHiljennaPuhe();
            }
        }, 1000);
        action = null;
        type = null;
    }

    @SuppressLint("ApplySharedPref")
    public void getParameters(String action, String type) {

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Intent stopAlarm = new Intent(AlarmActivity.this, SMSBackgroundService.class);
                AlarmActivity.this.stopService(stopAlarm);
                if (koneluku) {
                    waitForFragment();
                }
            }
        }
    }

    /**
     * StationboardButtonsFragment methods
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

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void showToast(String headText, String toastText) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView head = (TextView) layout.findViewById(R.id.head_text);
        head.setText(headText);
        TextView toastMessage = (TextView) layout.findViewById(R.id.toast_text);
        toastMessage.setText(toastText);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        if (mediaScanIntent.resolveActivity(getPackageManager()) != null) {
            this.sendBroadcast(mediaScanIntent);
        } else {
            notifyMediaStoreScanner(f);
        }
    }

    public final void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            getApplicationContext().sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
        }
    }
}
