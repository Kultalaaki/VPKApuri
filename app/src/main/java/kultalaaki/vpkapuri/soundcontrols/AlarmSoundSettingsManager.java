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

    public AlarmSoundSettingsManager(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

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
            return soundMode();
        } else if (ringerMode == 1) {
            // Phone is in vibrate
            // Are we allowed to alarm through vibrate mode
            if (preferences.getBoolean("throughVibrateMode", false)) {
                // Phone is in vibrate and user has set that we can not alarm through vibrate mode
                // Set alarm sound volume to 0
                return 0;
            }
            return soundMode();
        }
        return soundMode();
    }

    private int soundMode() {
        if (getUserSetSoundmode() == 2) {
            return 0;
        } else if (getUserSetSoundmode() == 3) {
            // App is in night mode set volume to 10
            return getVolume(10);
        }
        return getVolume(getSeekbarValue());
    }

    public int getVolume(int value) {
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volume = 0;
        if (audioManager != null) {
            volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            double aani = (double) volume / 100 * value;
            volume = (int) aani;
        }

        return volume;
    }

    // Soundmodes
    // 2 = Silent mode: silent
    // 3 = Night mode: 10% volume in alarms
    private int getUserSetSoundmode() {
        return preferences.getInt("aaneton_profiili", -1);
    }

    private int getSeekbarValue() {
        return preferences.getInt("SEEKBAR_VALUE", -1);
    }
}
