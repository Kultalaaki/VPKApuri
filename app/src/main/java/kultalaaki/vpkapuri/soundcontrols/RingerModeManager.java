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

    private final Context context;
    private AudioManager audioManager;
    private int ringerModeBeforeAlarm;

    public RingerModeManager(Context context) {
        this.context = context;
    }

    /**
     * @return 0 when phone is in silent mode
     * 1 when phone is in vibrate
     * 2 when phone is in normal
     */
    public int getRingerMode() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return this.audioManager.getRingerMode();
    }

    public void setRingerModeSilent() {
        ringerModeBeforeAlarm = audioManager.getRingerMode();
        this.audioManager.setRingerMode(0);
    }

    public void returnRingerMode() {
        this.audioManager.setRingerMode(this.ringerModeBeforeAlarm);
    }
}
