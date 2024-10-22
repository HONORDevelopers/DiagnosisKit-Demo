package com.demo.DiagnosisKit.faults;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.POWER_SERVICE;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.demo.DiagnosisKit.service.InnerService;

import java.util.List;

public class SimulatePowerAnomaly {
    private static final String TAG = "SimulatePowerAnomaly";
    private Context context;

    public SimulatePowerAnomaly(Context context) {
        this.context = context;
    }

    /* 模拟蓝牙扫描异常 */
    public void bluetoothScan(Activity activity) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, DEBUG_CLIENT + "do not have permission android.permission.BLUETOOTH_SCAN");
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.BLUETOOTH_SCAN}, 0);
            new Thread(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 3600; i++) {
                            Log.d(TAG, DEBUG_CLIENT + "BLUETOOTH START SCAN " + i + " times");
                            bluetoothAdapter.startDiscovery();
                            Thread.sleep(5000);
                            bluetoothAdapter.cancelDiscovery();
                            Thread.sleep(5000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Log.d(TAG, DEBUG_CLIENT + "have permission android.permission.BLUETOOTH_SCAN");
            new Thread(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 3600; i++) {
                            Log.d(TAG, DEBUG_CLIENT + "BLUETOOTH START SCAN " + i + " times");
                            bluetoothAdapter.startDiscovery();
                            Thread.sleep(5000);
                            bluetoothAdapter.cancelDiscovery();
                            Thread.sleep(5000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /* 模拟alarm频繁唤醒异常 */
    public void alarmWakeup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.setAction("TEST_ALARM");
                PendingIntent alarmIntent =
                    PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, alarmIntent);
                try {
                    Thread.sleep(3600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* 模拟持锁阻止系统休眠异常 */
    public void wakeClockSleep() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag")
                PowerManager.WakeLock wakeLock =
                    powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TEST_WAKE_LOCK_PARTIAL_WAKE_LOCK");
                wakeLock.acquire();
                try {
                    Thread.sleep(3600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* 模拟持锁阻止系统灭屏（低亮度） */
    public void wakeClockScreenOff1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag")
                PowerManager.WakeLock wakeLock =
                    powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "TEST_WAKE_LOCK_SCREEN_DIM_WAKE_LOCK");
                wakeLock.acquire();
                try {
                    Thread.sleep(3600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* 模拟持锁阻止系统灭屏 */
    public void wakeClockScreenOff2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag")
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                                                                          "TEST_WAKE_LOCK_SCREEN_BRIGHT_WAKE_LOCK");
                wakeLock.acquire();
                try {
                    Thread.sleep(3600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* 模拟GPS定位异常 */
    public void gpsLocate() {
        LocationManager gpslocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG, DEBUG_CLIENT + gpslocationManager.getAllProviders().toString());
        Location gpslocation = gpslocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (gpslocation != null) {
            Log.d(TAG, DEBUG_CLIENT + "last gpslocation is not null");
            Log.d(TAG, DEBUG_CLIENT + "last gpslocation latitude is " + gpslocation.getLatitude());
            Log.d(TAG, DEBUG_CLIENT + "last gpslocation longitude is " + gpslocation.getLongitude());
            Log.d(TAG, DEBUG_CLIENT + "last gpslocation Altitude is " + gpslocation.getAltitude());
            Log.d(TAG, DEBUG_CLIENT + "last gpslocation Speed is " + gpslocation.getSpeed());
        }
        LocationListener gpslocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, DEBUG_CLIENT + "gpslocation changed latitude is " + location.getLatitude());
                Log.d(TAG, DEBUG_CLIENT + "gpslocation changed longitude is " + location.getLongitude());
                Log.d(TAG, DEBUG_CLIENT + "gpslocation changed Altitude is " + location.getAltitude());
                Log.d(TAG, DEBUG_CLIENT + "gpslocation changed Speed is " + location.getSpeed());
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
                Log.d(TAG, DEBUG_CLIENT + "gpslocation provider enabled");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LocationListener.super.onStatusChanged(provider, status, extras);
                Log.d(TAG, DEBUG_CLIENT + "gpslocation status changed");
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                Log.d(TAG, DEBUG_CLIENT + "gpslocation provider disabled");
            }
        };
        gpslocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpslocationListener);
    }

    /* 模拟network定位 */
    public void networkLocation() {
        LocationManager networklocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.d(TAG, DEBUG_CLIENT + networklocationManager.getAllProviders().toString());
        Location networklocation = networklocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (networklocation != null) {
            Log.d(TAG, DEBUG_CLIENT + "last networklocation is not null");
            Log.d(TAG, DEBUG_CLIENT + "last networklocation latitude is " + networklocation.getLatitude());
            Log.d(TAG, DEBUG_CLIENT + "last networklocation longitude is " + networklocation.getLongitude());
            Log.d(TAG, DEBUG_CLIENT + "last networklocation Altitude is " + networklocation.getAltitude());
            Log.d(TAG, DEBUG_CLIENT + "last networklocation Speed is " + networklocation.getSpeed());
        }
        LocationListener networklocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, DEBUG_CLIENT + "networklocation changed latitude is " + location.getLatitude());
                Log.d(TAG, DEBUG_CLIENT + "networklocation changed longitude is " + location.getLongitude());
                Log.d(TAG, DEBUG_CLIENT + "networklocation changed Altitude is " + location.getAltitude());
                Log.d(TAG, DEBUG_CLIENT + "networklocation changed Speed is " + location.getSpeed());
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
                Log.d(TAG, DEBUG_CLIENT + "networklocation provider enabled");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LocationListener.super.onStatusChanged(provider, status, extras);
                Log.d(TAG, DEBUG_CLIENT + "networklocation status changed");
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                Log.d(TAG, DEBUG_CLIENT + "networklocation provider disabled");
            }
        };
        networklocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0,
                                                      networklocationListener);
    }

    /* 模拟频繁自启动 */
    public void oftenReboot() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String processName = "com.demo.diagnosiskit:secondProcess";
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                for (int i = 1; i < 3600; i++) {
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                        activityManager.getRunningAppProcesses();
                    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                        if (runningAppProcessInfo.processName.equals(processName)) {
                            int pid = runningAppProcessInfo.pid;
                            Log.d(TAG, DEBUG_CLIENT + "run: child process pid is " + pid);
                            Process.killProcess(pid);
                            Log.d(TAG, DEBUG_CLIENT + "run: child process restart " + i + " times");
                        }
                    }
                    SystemClock.sleep(5000);
                    Intent intentInnerService = new Intent(context, InnerService.class);
                    context.startService(intentInnerService);
                }
                try {
                    Thread.sleep(3600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
