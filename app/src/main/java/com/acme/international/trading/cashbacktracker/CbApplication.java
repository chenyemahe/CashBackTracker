package com.acme.international.trading.cashbacktracker;

import android.app.Application;
import android.content.Intent;

import com.acme.international.trading.cashbacktracker.service.CbService;

/**
 * Created by ye1.chen on 4/12/16.
 */
public class CbApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, CbService.class);
        startService(intent);
    }
}
