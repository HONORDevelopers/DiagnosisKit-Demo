package com.demo.DiagnosisKit.faults;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.demo.DiagnosisKit.IBinderAnr;

public class BinderAnr extends Service {
    private static final String TAG = "BinderAnr";

    public BinderAnr() {}

    @Override
    public IBinder onBind(Intent intent) {
        return new GlhStandAloneBinder();
    }

    class GlhStandAloneBinder extends IBinderAnr.Stub {
        @Override
        public long syncInvoke(String name, long time) throws RemoteException {
            Log.d(TAG, DEBUG_CLIENT + "syncInvoke start");
            SystemClock.sleep(time);
            Log.d(TAG, DEBUG_CLIENT + "syncInvoke end");
            return 0;
        }

        @Override
        public void asyncInvoke(String name, long time) throws RemoteException {
            Log.d(TAG, DEBUG_CLIENT + "asyncInvoke");
            SystemClock.sleep(time);
            Log.d(TAG, DEBUG_CLIENT + "asyncInvoke end");
        }
    }
}
