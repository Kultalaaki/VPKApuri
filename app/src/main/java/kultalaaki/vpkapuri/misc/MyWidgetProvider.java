/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.misc;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import kultalaaki.vpkapuri.R;

public class MyWidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    SharedPreferences sharedPreferences;
    private final SoundControls soundControls = new SoundControls();

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent active = new Intent(context, MyWidgetProvider.class);
        active.setAction(ACTION_WIDGET_REFRESH);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.nappain, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    @SuppressLint("ApplySharedPref")
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {

                if (sharedPreferences.getInt("aaneton_profiili", -1) == 3) {
                    soundControls.setNormal(context);
                } else if (sharedPreferences.getInt("aaneton_profiili", -1) == 1) {
                    soundControls.setSilent(context);
                } else {
                    soundControls.setNightMode(context);
                }
            } else {
                super.onReceive(context, intent);
            }
        }
    }
}
