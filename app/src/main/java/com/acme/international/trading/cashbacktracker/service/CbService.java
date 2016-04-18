package com.acme.international.trading.cashbacktracker.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.acme.international.trading.cashbacktracker.CashbackProfile;
import com.acme.international.trading.cashbacktracker.CbUtils;
import com.acme.international.trading.cashbacktracker.notification.FboxNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ye1.chen on 4/12/16.
 */
public class CbService extends Service {

    private final int delay = 12 * 60 * 60000;
    private ScheduledFuture<?> futureTask;
    private ScheduledThreadPoolExecutor mExecutor;
    private final String TAG = "CbService";

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (futureTask != null)
            futureTask.cancel(true);
        mExecutor = new ScheduledThreadPoolExecutor(1);
        futureTask = mExecutor.schedule(new CheckCbStateTask(), delay, TimeUnit.MILLISECONDS);
        Log.d(TAG, "CbService started!");

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class CheckCbStateTask implements Runnable {

        @Override
        public void run() {
            ArrayList<CashbackProfile> list = (ArrayList<CashbackProfile>) CbUtils.getListOfUnpaidCbProfile(getApplicationContext());
            if(list != null) {
                Log.d(TAG, "Start cashback state checking, found unpaid cashback!");
                FboxNotification.showNotification(getApplicationContext(), list.size());
            }
            Log.d(TAG, "Cashback state checking runable stoped!");
            futureTask = mExecutor.schedule(new CheckCbStateTask(), delay, TimeUnit.MILLISECONDS);
        }
    }
}
