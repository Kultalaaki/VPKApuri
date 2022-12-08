/*
 * Created by Kultala Aki on 21/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 21/7/2022
 */

package kultalaaki.vpkapuri.soundcontrols;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

/**
 * Class to look what settings user has set to alarms
 * Set alarm volume and vibration settings
 */
public class AlarmSoundSettingsManager {

    private final Context context;
    private final SharedPreferences preferences;

    /**
     * Constructor
     *
     * @param context     Application context
     * @param preferences Shared preferences
     */
    public AlarmSoundSettingsManager(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    /**
     * Check alarming settings and set alarm volume
     *
     * @return Volume
     */
    public int getAlarmSoundVolume() {
        RingerModeManager ringerModeManager = new RingerModeManager(context);
        int ringerMode = ringerModeManager.getRingerMode();
        if (ringerMode == 0) {
            // Phone is in silent
            // Are we allowed to alarm through silent mode
            if (preferences.getBoolean("throughSilentMode", false)) {
                // Phone is in silent and user has set that we can not alarm through silent mode
                // Set alarmsound volume to 0
                return 0;
            }
            return getVolume();
        } else if (ringerMode == 1) {
            // Phone is in vibrate
            // Are we allowed to alarm through vibrate mode
            if (preferences.getBoolean("throughVibrateMode", false)) {
                // Phone is in vibrate and user has set that we can not alarm through vibrate mode
                // Set alarm sound volume to 0
                return 0;
            }
            return getVolume();
        }
        return getVolume();
    }

    /**
     * Set volume how user has settings
     *
     * @return Volume
     */
    private int getVolume() {
        if (getUserSetSoundmode() == 2) {
            return 0;
        } else if (getUserSetSoundmode() == 3) {
            // App is in night mode set volume to 10
            return adjustVolume(10);
        }
        return adjustVolume(getSeekbarValue());
    }

    /**
     * Adjust volume
     *
     * @param seekbarValue seekbarValue from settings
     * @return Channel volume
     */
    public int adjustVolume(int seekbarValue) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = 0;
        if (audioManager != null) {
            volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            double aani = (double) volume / 100 * seekbarValue;
            volume = (int) aani;
        }

        return volume;
    }

    /**
     * Get user set sound mode
     * 2 = Silent mode: silent
     * 3 = Night mode: 10% volume
     *
     * @return selected sound mode
     */
    private int getUserSetSoundmode() {
        return preferences.getInt("aaneton_profiili", -1);
    }

    /**
     * Seekbar value from settings
     *
     * @return user set value
     */
    private int getSeekbarValue() {
        return preferences.getInt("SEEKBAR_VALUE", -1);
    }
}
