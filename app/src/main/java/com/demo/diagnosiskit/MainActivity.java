package com.demo.DiagnosisKit;

import static com.demo.DiagnosisKit.Permission.RequestCode;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.demo.DiagnosisKit.activity.BaseActivity;
import com.demo.DiagnosisKit.activity.Dex2oatActivity;
import com.demo.DiagnosisKit.activity.PerformanceActivity;
import com.demo.DiagnosisKit.activity.PowerQueryActivity;
import com.demo.DiagnosisKit.activity.PowerThermalActivity;
import com.demo.DiagnosisKit.activity.PressureActivity;
import com.demo.DiagnosisKit.activity.ReliabilityActivity;
import com.demo.DiagnosisKit.service.ForgroundService;
import com.demo.DiagnosisKit.service.InnerService;
import com.demo.DiagnosisKit.service.PublicService;
import com.hihonor.mcs.system.diagnosis.BuildConfig;
import com.hihonor.mcs.system.diagnosis.manager.KitCapability;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public static final String DATA_TITLE_KEY = "data_title";
    public static final String DATA_CONTENT_KEY = "data_content";
    private static Context mContext = null;
    private Button btn_power_thermal_activity, btn_performance_fault_activity, btn_pressure_info_activity,
        btn_reliability_fault_activity, btn_power_query_activity, btn_app_state_activity;
    private TextView tv_support;
    private ImageView iv_background;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewAndData() {
        startService(new Intent(this, PublicService.class));
        startService(new Intent(this, InnerService.class));
        startService(new Intent(this, ForgroundService.class));
        mContext = getApplicationContext();
        initUi();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "拒绝的权限名称：" + permissions[i]);
                    Log.e(TAG, "拒绝的权限结果：" + grantResults[i]);
                } else {
                    Log.e(TAG, "授权的权限名称：" + permissions[i]);
                    Log.e(TAG, "授权的权限结果：" + grantResults[i]);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reliability_fault_activity:
                goToActivity(ReliabilityActivity.class, null, null);
                break;
            case R.id.btn_pressure_info_activity:
                goToActivity(PressureActivity.class, null, null);
                break;
            case R.id.btn_performance_fault_activity:
                goToActivity(PerformanceActivity.class, null, null);
                break;
            case R.id.btn_power_thermal_activity:
                goToActivity(PowerThermalActivity.class, null, null);
                break;
            case R.id.btn_power_query_activity:
                goToActivity(PowerQueryActivity.class, "功耗拆解数据", "功耗拆解数据查询");
                break;
            case R.id.btn_app_state_activity:
                goToActivity(Dex2oatActivity.class, "DEX2OAT查询", "Dex2oat信息查询");
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void goToActivity(Class classz, String dataTitle, String dataContent) {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(dataTitle) && !TextUtils.isEmpty(dataContent)) {
            intent.putExtra(DATA_TITLE_KEY, dataTitle);
            intent.putExtra(DATA_CONTENT_KEY, dataContent);
        }
        intent.setClass(MainActivity.this, classz);
        startActivity(intent);
    }

    private void initUi() {
        btn_reliability_fault_activity = (Button) findViewById(R.id.btn_reliability_fault_activity);
        btn_reliability_fault_activity.setOnClickListener(this);
        btn_pressure_info_activity = (Button) findViewById(R.id.btn_pressure_info_activity);
        btn_pressure_info_activity.setOnClickListener(this);
        btn_performance_fault_activity = (Button) findViewById(R.id.btn_performance_fault_activity);
        btn_performance_fault_activity.setOnClickListener(this);
        btn_power_thermal_activity = (Button) findViewById(R.id.btn_power_thermal_activity);
        btn_power_thermal_activity.setOnClickListener(this);
        btn_power_query_activity = (Button) findViewById(R.id.btn_power_query_activity);
        btn_power_query_activity.setOnClickListener(this);
        btn_app_state_activity = (Button) findViewById(R.id.btn_app_state_activity);
        btn_app_state_activity.setOnClickListener(this);
        iv_background = (ImageView) findViewById(R.id.image_background);
        iv_background.setImageResource(R.drawable.introduction);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Permission permission = new Permission();
            permission.checkPermissions(this, mContext);
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        } else {
            Permission permission = new Permission();
            permission.checkPermissions(this, mContext);
        }
        tv_support = findViewById(R.id.tv_support_info);
        if (KitCapability.supportDiagKit(mContext)) {
            tv_support.setText("* 本手机支持DiagnosisKit\nSDK版本号:" + BuildConfig.versionName);
        } else {
            tv_support.setText("* 本手机不支持DiagnosisKit");
        }
    }
}
