/*
 * Created by Kultala Aki on 16/8/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 16/8/2022
 */

package kultalaaki.vpkapuri.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import kultalaaki.vpkapuri.FrontpageActivity;
import kultalaaki.vpkapuri.R;

/**
 * Use this to show notification to user.
 */
public class MyNotifications {

    private final Context context;

    public MyNotifications(Context context) {
        this.context = context;
    }

    /**
     * Informational notification to user
     *
     * @param content Text to inform user what went wrong.
     */
    public void showInformationNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_INFORMATION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("VPK Apuri")
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notifactionManager = NotificationManagerCompat.from(context);
        notifactionManager.notify(Constants.INFORMATION_NOTIFICATION_ID, builder.build());
    }

    /**
     * Meant to show what sound profile user has set
     * @param content set content to show user
     */
    public void showSoundProfileNotification(String content) {
        Intent openFrontPage = new Intent(context, FrontpageActivity.class);
        PendingIntent pendingOpenFrontPage = PendingIntent.getActivity(context, 0, openFrontPage, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_SILENCE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("VPK Apuri")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingOpenFrontPage)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
    }
}
