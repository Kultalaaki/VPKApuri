/*
 * Created by Kultala Aki on 11/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 11/7/2022
 */

package kultalaaki.vpkapuri.soundcontrols;

import android.content.Context;
import android.media.AudioManager;

// Look what ringer mode is active in phone
// Return ringer mode back after alarm
public class RingerModeManager {

    private final AudioManager audioManager;
    private final int ringerModeBeforeAlarm;

    public RingerModeManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ringerModeBeforeAlarm = audioManager.getRingerMode();
    }

    // Set ringer mode to silent
    public void setRingerModeSilent() {
        audioManager.setRingerMode(0);
    }

    // Set ringer mode back after alarm
    public void returnRingerMode() {
        audioManager.setRingerMode(ringerModeBeforeAlarm);
    }
}
