/*
 * Created by Kultala Aki on 23/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 23/7/2022
 */

package kultalaaki.vpkapuri.soundcontrols;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.IOException;

/**
 * Player for alarm sounds
 */
public class AlarmMediaPlayer {

    public MediaPlayer mediaPlayer;

    private final Context context;
    private final SharedPreferences preferences;
    private final Uri uri;

    private AudioManager audioManager;

    private int streamReturnValue;
    private boolean audioFocusDucked = false;

    private AudioFocusRequest focusRequest;

    private AlarmSoundSettingsManager alarmSoundSettingsManager;
    private AlarmSoundSettingsFixedPolicyManager fixedPolicyManager;
    private VibrateController vibrateController;

    /**
     * Constructor
     *
     * @param context     Application
     * @param preferences Shared preferences
     * @param uri         Alarm tone uri
     */
    public AlarmMediaPlayer(Context context, SharedPreferences preferences, Uri uri) {
        this.context = context;
        this.preferences = preferences;
        this.uri = uri;
    }

    /**
     * Check do not disturb permission
     *
     * @return have permission = true
     */
    public boolean isDoNotDisturbAllowed() {
        NotificationManager notificationManager =
                (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            // Do Not Disturb is not allowed
            // inform user that do not disturb is not allowed and that is reason why it is not working
            return notificationManager.isNotificationPolicyAccessGranted();
        }
        FirebaseCrashlytics.getInstance().log("AlarmMediaPlayer.java: Could not check do not disturb permission");
        return false;
    }

    /**
     * Request audio focus like Google want's us to do
     */
    public void audioFocusRequest() {
        alarmSoundSettingsManager = new AlarmSoundSettingsManager(context, preferences);
        fixedPolicyManager = new AlarmSoundSettingsFixedPolicyManager(context, preferences);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();

        AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener(i -> {
                    if (i == AudioManager.AUDIOFOCUS_LOSS) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            stopVibration();
                        }
                    } else if (i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            stopVibration();
                            audioFocusDucked = true;
                        }
                    } else if (i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, keep playing
                        if (audioManager.isVolumeFixed()) {
                            mediaPlayer.setVolume(0.1F, 0.1F);
                        } else {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                        }

                        stopVibration();
                    } else if (i == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary

                        // Problem: sometimes Android system calls this after alarm is
                        // long gone and media starts playing again.
                        // Wait for user experience from this. If no complaints, then leave it be.
                        if (audioManager.isVolumeFixed()) {
                            float vol = alarmVolumeFixedPolicyDevices();
                            mediaPlayer.setVolume(vol, vol);
                        } else {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmSoundSettingsManager.getAlarmSoundVolume(), 0);
                        }

                        if (audioFocusDucked) {
                            mediaPlayer.start();
                        }
                    }
                })
                .build();
        final Object focusLock = new Object();

        // requesting audio focus and processing the response
        int res = audioManager.requestAudioFocus(focusRequest);
        synchronized (focusLock) {
            if (res == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                // Not authorized to play. Use vibration to notify user.
                startVibrationNotification();
            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // Allowed to play
                prepareMediaPlayer(this.uri);
            } else if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                // Audio focus delayed. Use vibration
                startVibrationNotification();
            }
        }
    }

    /**
     * Prepare media player to play sound
     * Check vibration settings
     * Set stop time
     *
     * @param uri Sound to play
     */
    private void prepareMediaPlayer(Uri uri) {
        try {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            streamReturnValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(context, uri);
            if (audioManager.isVolumeFixed()) {
                float vol = alarmVolumeFixedPolicyDevices();
                mediaPlayer.setVolume(vol, vol);
            } else {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmSoundSettingsManager.getAlarmSoundVolume(), 0);
            }

            mediaPlayer.setAudioAttributes(audioAttributes);
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                Thread music = new Thread(mediaPlayer::start);
                music.start();
            });
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            startVibration();
            stoppingServiceTimer();
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    /**
     * Stop media player
     * Abandon audio focus
     */
    public void stopAlarmMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        stopVibration();
        if (audioManager.isVolumeFixed()) {
            mediaPlayer.setVolume(0.0F, 0.0F);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamReturnValue, 0);
        }

        audioManager.abandonAudioFocusRequest(focusRequest);
    }

    /**
     * Timer for stopping service
     */
    private void stoppingServiceTimer() {
        int stoppingTime = 60;
        String userSetStoppingTime = preferences.getString("stopTime", null);
        if (userSetStoppingTime != null) {
            stoppingTime = Integer.parseInt(userSetStoppingTime);
        }
        if (stoppingTime < 10) {
            stoppingTime = 10;
        }
        stoppingTime = stoppingTime * 1000;
        Handler handler = new Handler();
        handler.postDelayed(this::stopAlarmMedia, stoppingTime);
    }

    /**
     * Start vibration
     */
    private void startVibration() {
        vibrateController = new VibrateController(context, preferences);
        if (audioManager.isVolumeFixed()) {
            if (fixedPolicyManager.getAlarmSoundVolume() > 0.0F) {
                vibrateController.vibrateNotification();
            }
        } else {
            if (alarmSoundSettingsManager.getAlarmSoundVolume() > 0) {
                vibrateController.vibrate();
            }
        }
    }

    /**
     * Special vibration used when audio focus is not granted
     */
    private void startVibrationNotification() {
        vibrateController = new VibrateController(context, preferences);
        if (audioManager.isVolumeFixed()) {
            if (fixedPolicyManager.getAlarmSoundVolume() > 0.0F) {
                vibrateController.vibrateNotification();
            }
        } else {
            if (alarmSoundSettingsManager.getAlarmSoundVolume() > 0) {
                vibrateController.vibrateNotification();
            }
        }
    }

    /**
     * Stop vibration
     */
    private void stopVibration() {
        if (vibrateController != null) {
            vibrateController.stopVibration();
        }
    }

    /**
     * Get alarm volume as float value
     *
     * @return alarm volume
     */
    private float alarmVolumeFixedPolicyDevices() {
        return fixedPolicyManager.getAlarmSoundVolume();
    }

}