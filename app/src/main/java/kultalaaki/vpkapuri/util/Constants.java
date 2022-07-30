/*
 * Created by Kultala Aki on 29/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 29/7/2022
 */

package kultalaaki.vpkapuri.util;

/**
 * Store VPK Apuri constants here
 */
public class Constants {

    /**
     * Vibration patterns and amplitudes
     */
    public static long[] PULSE_PATTERN = new long[]{0, 80, 80, 150, 1670};
    public static int[] PULSE_AMPLITUDE = new int[]{0, 50, 10, 255, 0};
    public static long[] HURRY_PATTERN = new long[]{50, 100, 50, 100, 50, 100, 50, 100};
    public static int[] HURRY_AMPLITUDE = new int[]{0, 255, 100, 255, 0, 255, 100, 255};
    public static long[] SLOW_PATTERN = new long[]{0, 800, 800, 800, 800, 800, 800};
    public static int[] SLOW_AMPLITUDE = new int[]{0, 50, 200, 50, 200, 50, 200};
    public static long[] SOS_PATTERN = new long[]{0, 100, 50, 100, 50, 100, 300, 200, 50, 200, 50, 200, 300, 100, 50, 100, 50, 100, 1500};
    public static int[] SOS_AMPLITUDE = new int[]{0, 150, 0, 150, 0, 150, 0, 200, 0, 200, 0, 200, 0, 150, 0, 150, 0, 150, 0};
    public static long[] VIRVE_PATTERN = new long[]{0, 100, 50, 100, 250, 100, 50, 100, 250, 100, 50, 100, 250, 100, 50, 100, 250};
    public static int[] VIRVE_AMPLITUDE = new int[]{0, 150, 0, 150, 0, 255, 0, 255, 0, 180, 0, 180, 0, 100, 0, 100, 0};
    public static long[] VIBRATION_NOTIFICATION_PATTERN = new long[]{50, 100, 50, 100, 50, 100, 50, 100};
    public static int[] VIBRATION_NOTIFICATION_AMPLITUDE = new int[]{0, 150, 100, 150, 0, 150, 100, 150};

    /**
     * Notification ids
     */
    public static int ALARM_NOTIFICATION_ID = 264981;
    public static int NOTIFICATION_ID = 15245;
}
