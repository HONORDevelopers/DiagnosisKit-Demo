package com.demo.DiagnosisKit.service;

import static android.app.Notification.PRIORITY_MIN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.demo.DiagnosisKit.R;

public class ForgroundService extends Service {
    public ForgroundService() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForeground() {
        String channelId = null;
        channelId = createNotificationChannel("kim.hsl", "ForegroundService");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        Notification notification = builder.setOngoing(true)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setPriority(PRIORITY_MIN)
                                        .setCategory(Notification.CATEGORY_SERVICE)
                                        .build();
        startForeground(1, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
}