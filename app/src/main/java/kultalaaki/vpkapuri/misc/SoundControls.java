/*
 * Created by Kultala Aki on 3/6/21 3:31 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 3/6/21 3:31 PM
 */

package kultalaaki.vpkapuri.misc;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.util.Constants;
import kultalaaki.vpkapuri.util.MyNotifications;

public class SoundControls {

    private SharedPreferences sharedPreferences;

    /**
     * SoundControls setSilent() sets sound to silent for alarmdetection messages and alarmdetection phonecalls only.
     * When setSilent() is called, notification will be set indicating user that alarms are silent.
     *
     * @param context application context
     */
    @SuppressLint("ApplySharedPref")
    public void setSilent(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt("aaneton_profiili", Constants.SOUND_PROFILE_SILENT).apply();
        RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        text.setTextViewText(R.id.teksti, "Äänetön");

        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, text);

        MyNotifications notifications = new MyNotifications(context);
        notifications.showSoundProfileNotification("Hälytykset on hiljennetty");
    }

    /**
     * SoundControls setNightMode() sets sound to 10% volume for alarmdetection messages and alarmdetection phonecalls only.
     * When setNightMode() is called, notification will be set indicating user that alarms are 10% volume.
     *
     * @param context application context
     */
    @SuppressLint("ApplySharedPref")
    public void setNightMode(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt("aaneton_profiili", Constants.SOUND_PROFILE_NIGHT_MODE).apply();
        RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        text.setTextViewText(R.id.teksti, "Yötila");

        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, text);

        MyNotifications notifications = new MyNotifications(context);
        notifications.showSoundProfileNotification("Yötila. Hälytysten äänenvoimakkuus rajoitettu 10%.");
    }

    /**
     * SoundControls setNormal() sets sound to app setting volume for alarmdetection messages and alarmdetection phonecalls only.
     * When setNormal() is called, other notifications with MY_NOTIFICATION_ID are cancelled.
     *
     * @param context application context
     */
    @SuppressLint("ApplySharedPref")
    public void setNormal(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt("aaneton_profiili", Constants.SOUND_PROFILE_NORMAL).apply();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(Constants.NOTIFICATION_ID);
        RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        text.setTextViewText(R.id.teksti, "Normaali");

        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, text);
    }
}
