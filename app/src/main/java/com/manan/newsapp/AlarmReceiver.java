package com.manan.newsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.manan.newsapp.MainFeedScreen.MainFeedActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.manan.newsNpp.channelId";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainFeedActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainFeedActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
       String title = intent.getExtras().getString( "title" );
        String body = intent.getExtras().getString( "content" );

        Notification.Builder builder = new Notification.Builder(context);

        Notification notification = builder.setContentTitle(title)
                .setContentText(body)
                .setTicker("New News Alert!")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notification);
    }
}
