package com.acme.international.trading.cashbacktracker.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.acme.international.trading.cashbacktracker.R;

/**
 * Created by ye1.chen on 3/28/16.
 */
public class FboxNotification {

    public void showNotification(Context mContext, int size) {
        Notification notif = buildNotification(mContext, size);
        NotificationManager notifMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMgr.notify(1, notif);
    }

    private Notification buildNotification(Context mContext, int size) {
        String text;
        if(size == 1)
            text = mContext.getResources().getString(R.string.notification_body_1) + " " + String.valueOf(size) + " " + mContext.getResources().getString(R.string.notification_body_2);
        else
            text = mContext.getResources().getString(R.string.notification_body_1) + " " + String.valueOf(size) + " " + mContext.getResources().getString(R.string.notification_body_2);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(R.mipmap.ic_launcher);

        mBuilder.setContentTitle(mContext.getResources().getString(R.string.app_full_name))
                .setContentText(text)
                .setOngoing(false)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setWhen(0);
        return mBuilder.build();
    }
}
