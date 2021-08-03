/*
 * Created by Kultala Aki on 3/6/21 3:31 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 3/6/21 3:31 PM
 */

package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

class SoundControls {

    private static final int MY_NOTIFICATION_ID = 15245;

    private SharedPreferences sharedPreferences;

    /**
     * SoundControls setSilent() sets sound to silent for alarmdetection messages and alarmdetection phonecalls only.
     * When setSilent() is called, notification will be set indicating user that alarms are silent.
     * @param context application context
     */
    @SuppressLint("ApplySharedPref")
    void setSilent(Context context) {
        // set silent mode
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt("aaneton_profiili", 2).commit();
        RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        text.setTextViewText(R.id.teksti, "Äänetön");
        //Toast.makeText(context,"Äänetön tila käytössä.", Toast.LENGTH_SHORT).show();

        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        Intent hiljennys = new Intent(context, FrontpageActivity.class);
        PendingIntent hiljennetty = PendingIntent.getActivity(context, 0, hiljennys, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.updateAppWidget(thisWidget, text);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "HILJENNYS")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("VPK Apuri")
                .setContentText("Hälytykset on hiljennetty.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(hiljennetty)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * SoundControls setNightMode() sets sound to 10% volume for alarmdetection messages and alarmdetection phonecalls only.
     * When setNightMode() is called, notification will be set indicating user that alarms are 10% volume.
     * @param context application context
     */
    @SuppressLint("ApplySharedPref")
    void setNightMode(Context context) {
        // set night mode
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt("aaneton_profiili", 3).commit();
        //Toast.makeText(context, "Yötila käytössä", Toast.LENGTH_LONG).show();
        RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        text.setTextViewText(R.id.teksti, "Yötila");

        Intent hiljennys = new Intent(context, FrontpageActivity.class);
        PendingIntent hiljennetty = PendingIntent.getActivity(context, 0, hiljennys, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "HILJENNYS")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("VPK Apuri")
                .setContentText("Yötila. Hälytysten äänenvoimakkuus rajoitettu 10%.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(hiljennetty)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());

        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, text);
    }

    /**
     * SoundControls setNormal() sets sound to app setting volume for alarmdetection messages and alarmdetection phonecalls only.
     * When setNormal() is called, other notifications with MY_NOTIFICATION_ID are cancelled.
     * @param context application context
     */
    @SuppressLint("ApplySharedPref")
    void setNormal(Context context) {
        // set normal mode
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt("aaneton_profiili", 1).commit();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(MY_NOTIFICATION_ID);
        RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        text.setTextViewText(R.id.teksti, "Normaali");
        //Toast.makeText(context,"Äänet kytketty.", Toast.LENGTH_SHORT).show();

        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thisWidget, text);
    }
}
