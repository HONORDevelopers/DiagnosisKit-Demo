package com.demo.DiagnosisKit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class InnerService extends Service {
    public InnerService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Intent localIntent = new Intent();
        localIntent.setClass(this, InnerService.class);
        this.startService(localIntent);
    }
}