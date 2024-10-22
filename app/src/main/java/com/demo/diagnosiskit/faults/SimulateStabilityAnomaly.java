package com.demo.DiagnosisKit.faults;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.demo.DiagnosisKit.IBinderAnr;
import com.demo.DiagnosisKit.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SimulateStabilityAnomaly {
    private static final String TAG = "SimulateStabilityAnomaly";
    private IBinderAnr mBinderAnrService = null;
    private Context context;
    private Activity activity;

    public SimulateStabilityAnomaly(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        if (mBinderAnrService == null) {
            context.bindService(new Intent(context, BinderAnr.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mBinderAnrService = IBinderAnr.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mBinderAnrService = null;
                }
            }, BinderAnr.BIND_AUTO_CREATE);
        }
    }

    /* 模拟threadLeak */
    public void threadLeak() {
        for (int i = 0; i < 1200; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(3600000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /* 模拟fdleak */
    public void fdLeak() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10000; i++) {
                        String filename = i + ".txt";
                        File file = new File("/sdcard/Download/", filename);
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();
                        OutputStream outputStream = new FileOutputStream(file);

                        outputStream.write("123".getBytes(StandardCharsets.UTF_8));
                    }
                } catch (IOException e) {}
                try {
                    Thread.sleep(3600000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* 模拟crash */
    public void crash() {
        throw new RuntimeException("中文测试");
    }

    /* 模拟tombstone */
    public void tombstone() {
        int pid2 = android.os.Process.myPid();
        Process.sendSignal(pid2, 6);
    }

    /* 模拟anr */
    public void anr() {
        if (mBinderAnrService != null) {
            try {
                mBinderAnrService.syncInvoke("anr_sync", 6000);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /* 模拟memory leak */
    private List<char[]> testList = new ArrayList<>();

    public void memoryLeak() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    testList.add(new char[1024 * 1024]);
                }
            }
        }).start();
    }

    /* 模拟binder fault */
    private int choice = -1;
    private Bitmap[] mCopyArray = null;

    public void showBinderChoice() {
        final String[] items = {"KILLED(BINDER)", "KILLED(SYSTEM.EXIT)", "KILLED(KILLPROCESS)",
                                "KILLED(CHILD)",  "KILLED(SIG9)",        "KILLED(BG)"};
        AlertDialog.Builder binderChoiceDialog = new AlertDialog.Builder(activity);
        binderChoiceDialog.setTitle("请选择KILLED要触发的场景");
        choice = 0;
        binderChoiceDialog.setItems(items, (dialog, which) -> {
            choice = which;
            if (choice == -1) {
                return;
            }
            switch (items[choice]) {
                case "KILLED(BINDER)":
                    Log.d(TAG, DEBUG_CLIENT + "R.id.binderkill");
                    Toast.makeText(context, "binder测试", Toast.LENGTH_SHORT).show();
                    testTooManyBinderKill();
                    binderChoiceDialog.create().dismiss();
                    break;
                case "KILLED(SYSTEM.EXIT)":
                    Toast.makeText(context, "system.exit(0)测试！", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                    break;
                case "KILLED(KILLPROCESS)":
                    Toast.makeText(context, "killProcess测试", Toast.LENGTH_SHORT).show();
                    int pid1 = Process.myPid();
                    android.os.Process.killProcess(pid1);
                    break;
                case "KILLED(CHILD)":
                    Toast.makeText(context, "kill子进程测试", Toast.LENGTH_SHORT).show();
                    String processName = "com.demo.diagnosiskit:secondProcess";
                    ActivityManager activityManager =
                        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                        activityManager.getRunningAppProcesses();
                    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                        if (runningAppProcessInfo.processName.equals(processName)) {
                            int pid = runningAppProcessInfo.pid;
                            Log.d(TAG, DEBUG_CLIENT + "run: child process pid is " + pid);
                            Process.killProcess(pid);
                            break;
                        }
                    }
                    break;
                case "KILLED(SIG9)":
                    Toast.makeText(context, "SIG9测试", Toast.LENGTH_SHORT).show();
                    int pid3 = Process.myPid();
                    Process.sendSignal(pid3, 9);
                    break;
                case "KILLED(BG)":
                    Toast.makeText(context, "killBackgroundProcesses测试", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                                ActivityManager activityManager =
                                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                                activityManager.killBackgroundProcesses("com.demo.diagnosiskit");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
            }
        });
        binderChoiceDialog.create().setCanceledOnTouchOutside(true);
        binderChoiceDialog.create().setCancelable(true);
        binderChoiceDialog.show();
    }

    /* 模拟binder太多被杀 */
    public void testTooManyBinderKill() {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        while (true) {
            AppOpsManager.OnOpChangedListener callback = (op, packageName) -> {};
            appOpsManager.startWatchingMode(AppOpsManager.OPSTR_USE_FINGERPRINT, "com.android.deskclock", callback);
        }
    }
}
