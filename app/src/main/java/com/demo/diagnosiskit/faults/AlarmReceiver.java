package com.demo.DiagnosisKit.faults;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARM_TEST", DEBUG_CLIENT + "onReceive");
    }
}
