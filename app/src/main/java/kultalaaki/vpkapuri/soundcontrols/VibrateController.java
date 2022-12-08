/*
 * Created by Kultala Aki on 28/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 28/7/2022
 */

package kultalaaki.vpkapuri.soundcontrols;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.VibrationEffect;
import android.os.Vibrator;

import kultalaaki.vpkapuri.util.Constants;

/**
 * Vibration controller
 */
public class VibrateController {

    private Vibrator vibrator;
    private final Context context;
    private final SharedPreferences preferences;
    private long[] pattern;
    private int[] amplitude;

    /**
     * Constructor
     *
     * @param context     Application
     * @param preferences Shared preferences
     */
    public VibrateController(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    /**
     * Check if device has vibrator and amplitude controller
     */
    public void vibrate() {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Check if user wants vibration
        if (vibratePermission()) {
            // Check that phone has vibrator
            if (phoneHasVibrator()) {
                // Check if phone has amplitude control
                if (vibratorHasAmplitudeControl()) {
                    setVibratePattern();
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitude, 0));
                } else {
                    setVibratePattern();
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
                }
            }
        }
    }

    /**
     * Special vibration to use as notification
     */
    public void vibrateNotification() {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (phoneHasVibrator()) {
            pattern = Constants.VIBRATION_NOTIFICATION_PATTERN;
            amplitude = Constants.VIBRATION_NOTIFICATION_AMPLITUDE;
            if (vibratorHasAmplitudeControl()) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitude, -1));
            } else {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            }
        }
    }

    /**
     * Cancel vibration
     */
    public void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    /**
     * Check permission to use vibration
     *
     * @return boolean
     */
    private boolean vibratePermission() {
        return preferences.getBoolean("vibrate", true);
    }

    /**
     * Select correct vibration pattern
     */
    private void setVibratePattern() {
        String patternValue = preferences.getString("vibrate_pattern", null);
        int vibratePatternValue = 0;
        if (patternValue != null) {
            vibratePatternValue = Integer.parseInt(patternValue);
        }
        switch (vibratePatternValue) {
            case 0:
                pattern = Constants.PULSE_PATTERN;
                amplitude = Constants.PULSE_AMPLITUDE;
                break;
            case 1:
                pattern = Constants.HURRY_PATTERN;
                amplitude = Constants.HURRY_AMPLITUDE;
                break;
            case 2:
                pattern = Constants.SLOW_PATTERN;
                amplitude = Constants.SLOW_AMPLITUDE;
                break;
            case 3:
                pattern = Constants.SOS_PATTERN;
                amplitude = Constants.SOS_AMPLITUDE;
                break;
            case 4:
                pattern = Constants.VIRVE_PATTERN;
                amplitude = Constants.VIRVE_AMPLITUDE;
                break;
        }
    }

    /**
     * Check for vibrator
     *
     * @return boolean
     */
    private boolean phoneHasVibrator() {
        if (vibrator != null) {
            return vibrator.hasVibrator();
        }
        return false;
    }

    /**
     * Check for amplitude controller
     *
     * @return boolean
     */
    private boolean vibratorHasAmplitudeControl() {
        return vibrator.hasAmplitudeControl();
    }

}
