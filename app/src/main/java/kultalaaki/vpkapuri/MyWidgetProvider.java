/*
 * Created by Kultala Aki on 9.9.2017 9:28
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 18.8.2017 9:44
 */

package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    SharedPreferences aaneton;
    private static final int MY_NOTIFICATION_ID = 15245;

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent active = new Intent(context, MyWidgetProvider.class);
        active.setAction(ACTION_WIDGET_REFRESH);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.nappain, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    @SuppressLint("ApplySharedPref")
    public void onReceive(Context context, Intent intent) {

        aaneton = context.getSharedPreferences("kultalaaki.vpkapuri.aaneton", Activity.MODE_PRIVATE);

        if(intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {

                if (aaneton.getInt("aaneton_profiili", -1) == 3) {
                    aaneton.edit().putInt("aaneton_profiili", 1).commit();
                    //Toast.makeText(context, "Kytketty äänet päälle", Toast.LENGTH_LONG).show();
                    RemoteViews texta = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                    texta.setTextViewText(R.id.teksti, "Normaali");

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(MY_NOTIFICATION_ID);

                    ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
                    AppWidgetManager manager = AppWidgetManager.getInstance(context);
                    manager.updateAppWidget(thisWidget, texta);
                } else if (aaneton.getInt("aaneton_profiili", -1) == 1){
                    aaneton.edit().putInt("aaneton_profiili", 2).commit();
                    //Toast.makeText(context, "Kytketty äänetön tila", Toast.LENGTH_LONG).show();
                    RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                    text.setTextViewText(R.id.teksti, "Äänetön");

                    Intent hiljennys = new Intent(context, EtusivuActivity.class);
                    PendingIntent hiljennetty = PendingIntent.getActivity(context, 0, hiljennys, PendingIntent.FLAG_CANCEL_CURRENT);
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

                    ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
                    AppWidgetManager manager = AppWidgetManager.getInstance(context);
                    manager.updateAppWidget(thisWidget, text);
                } else {
                    aaneton.edit().putInt("aaneton_profiili", 3).commit();
                    //Toast.makeText(context, "Kytketty äänetön tila", Toast.LENGTH_LONG).show();
                    RemoteViews text = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                    text.setTextViewText(R.id.teksti, "Yötila");

                    Intent hiljennys = new Intent(context, EtusivuActivity.class);
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
            } else {
                //Toast.makeText(context, "Käynnistys", Toast.LENGTH_LONG).show();
                super.onReceive(context, intent);
            }
        }
    }
}
