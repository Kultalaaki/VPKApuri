/*
 * Created by Kultala Aki on 14/8/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 14/8/2022
 */

package kultalaaki.vpkapuri.soundcontrols;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import kultalaaki.vpkapuri.util.Constants;

/**
 * TextToSpeech speaks given text
 */
public class SpeakText {

    private final Context context;
    private final SharedPreferences preferences;
    private AudioManager audioManager;
    private AlarmSoundSettingsManager alarmSoundSettingsManager;
    private TextToSpeech textToSpeech;
    private String textToSpeak;
    private int volume;
    private int returnVolume;

    /**
     * If you call it from fragment, make sure to give getActivity() as context.
     *
     * @param context     Activity context.
     * @param preferences SharedPreferences
     */
    public SpeakText(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
        this.alarmSoundSettingsManager = new AlarmSoundSettingsManager(context, preferences);
    }

    /**
     * Calling this initializes text to speech and starts speech. Set text before using this.
     */
    public void initSpeakText() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Todo: inform user. Language is not supported.
                }
                volume = preferences.getInt("tekstiPuheeksiVol", -1);
                alarmSoundSettingsManager = new AlarmSoundSettingsManager(context, preferences);
                volume = alarmSoundSettingsManager.getVolume(volume);

                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                returnVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                textToSpeech.playSilentUtterance(1000, TextToSpeech.QUEUE_FLUSH, null);
                textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, Constants.UTTERANCE_ID_ALARM);
                Log.i("VPK Apuri", "init completed");
            } else {
                // Todo: inform user. Error. Could not initialize text to speech.
            }
        });
    }

    /**
     * @param textToSpeak string to speak.
     */
    public void setTextToSpeak(String textToSpeak) {
        this.textToSpeak = textToSpeak;
    }

    /**
     * Stop text to speech and reset stream volume to what it was before.
     */
    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, returnVolume, 0);
        }
    }
}
