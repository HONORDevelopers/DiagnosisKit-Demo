package com.demo.DiagnosisKit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {
    // 需要申请权限的数组
    private String[] permissions = {
        Manifest.permission.ACCESS_FINE_LOCATION,    Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE,   Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,       Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
    };

    private List<String> permissionList = new ArrayList<>();

    public static int RequestCode = 100;

    public void checkPermissions(Activity activity, Context context) {
        requestPermission(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(context, "未获取访问所有权限", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < permissions.length; i++) {
                    if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(permissions[i]);
                    }
                }

                if (permissionList.size() > 0) {
                    requestPermission(activity);
                }
            }
        }
    }

    public void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, permissions, RequestCode);
    }
}