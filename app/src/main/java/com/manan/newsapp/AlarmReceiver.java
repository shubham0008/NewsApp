package com.manan.newsapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.manan.newsapp.MainFeedScreen.MainFeedActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder( context )
                            .setSmallIcon( R.drawable.ic_search )
                            .setContentTitle( context.getResources().getString( R.string.app_name ) )
                            .setContentText( context.getResources().getString( R.string.dummy ) );
            Intent resultIntent = new Intent( context, MainFeedActivity.class );
            TaskStackBuilder stackBuilder = TaskStackBuilder.create( context );
            stackBuilder.addParentStack( MainFeedActivity.class );
            stackBuilder.addNextIntent( resultIntent );
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
            mBuilder.setContentIntent( resultPendingIntent );
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
            mNotificationManager.notify( 1, mBuilder.build() );

    }
}
