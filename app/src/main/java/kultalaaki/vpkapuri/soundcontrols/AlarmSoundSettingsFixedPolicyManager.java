package kultalaaki.vpkapuri.soundcontrols;

import android.content.Context;
import android.content.SharedPreferences;

import kultalaaki.vpkapuri.util.Constants;

public class AlarmSoundSettingsFixedPolicyManager {

    private final Context context;
    private final SharedPreferences preferences;

    /**
     * Constructor
     *
     * @param context     Application context
     * @param preferences Shared preferences
     */
    public AlarmSoundSettingsFixedPolicyManager(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    /**
     * Check alarming settings and set alarm volume
     *
     * @return Volume
     */
    public float getAlarmSoundVolume() {
        RingerModeManager ringerModeManager = new RingerModeManager(context);
        int ringerMode = ringerModeManager.getRingerMode();
        if (ringerMode == 0) {
            // Phone is in silent
            // Are we allowed to alarm through silent mode
            if (preferences.getBoolean("throughSilentMode", false)) {
                // Phone is in silent and user has set that we can not alarm through silent mode
                // Set alarmsound volume to 0
                return 0.0F;
            }
            return getVolume();
        } else if (ringerMode == 1) {
            // Phone is in vibrate
            // Are we allowed to alarm through vibrate mode
            if (preferences.getBoolean("throughVibrateMode", false)) {
                // Phone is in vibrate and user has set that we can not alarm through vibrate mode
                // Set alarm sound volume to 0
                return 0.0F;
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
    private float getVolume() {
        if (getUserSetSoundmode() == Constants.SOUND_PROFILE_SILENT) {
            return 0.0F;
        } else if (getUserSetSoundmode() == Constants.SOUND_PROFILE_NIGHT_MODE) {
            // App is in night mode set volume to 0.1
            return 0.1F;
        }
        return adjustVolume(getSeekbarValue());
    }

    /**
     * Adjust volume
     *
     * @param seekbarValue seekbarValue from settings
     * @return Channel volume
     */
    public float adjustVolume(int seekbarValue) {
        int streamMaxVol = 10;
        int dividedSeekbarValue = seekbarValue / 10;
        double volumeInPercent = (double) streamMaxVol / 100 * dividedSeekbarValue;

        return (float) volumeInPercent;
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
