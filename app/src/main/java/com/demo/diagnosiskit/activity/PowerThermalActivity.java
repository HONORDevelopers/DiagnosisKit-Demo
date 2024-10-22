package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.DiagnosisKit.R;
import com.demo.DiagnosisKit.faults.SimulatePowerAnomaly;
import com.demo.DiagnosisKit.samples.PowerThermalDemo;

public class PowerThermalActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PowerThermalActivity";
    private EditText et_power_thermal_info;
    private Button btn_register_power, btn_often_reboot, btn_gps_locate, btn_wake_clock_screen_off2,
        btn_wake_clock_screen_off1, btn_wake_clock_sleep, btn_alarm_wakeup, btn_bluetooth_scan, btn_network_location;
    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private static Context context = null;
    private PowerThermalDemo powerThermalDemo;
    private SimulatePowerAnomaly simulatePowerAnomaly;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_power_thermal;
    }

    @Override
    protected void initViewAndData() {
        context = getApplicationContext();
        initUi();
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("PowerThermalActivity");
        handlerThread.start();
        callbackHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {}
            }
        };
        simulatePowerAnomaly = new SimulatePowerAnomaly(context);
        powerThermalDemo = new PowerThermalDemo(context, callbackHandler, uiHandler);
    }

    @Override
    public void finish() {
        powerThermalDemo.unSubscribe();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        powerThermalDemo.unSubscribe();
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register_power:
                Toast.makeText(PowerThermalActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                powerThermalDemo.subscribe();
                break;
            case R.id.btn_bluetooth_scan:
                Toast.makeText(PowerThermalActivity.this, "蓝牙频繁扫描测试！", Toast.LENGTH_SHORT).show();
                simulatePowerAnomaly.bluetoothScan(this);
                break;
            case R.id.btn_alarm_wakeup:
                Toast.makeText(PowerThermalActivity.this, "alarm频繁唤醒系统测试", Toast.LENGTH_SHORT).show();
                simulatePowerAnomaly.alarmWakeup();
                break;
            case R.id.btn_wake_clock_sleep:
                Toast.makeText(PowerThermalActivity.this, "持锁阻止系统休眠测试！", Toast.LENGTH_SHORT).show();
                simulatePowerAnomaly.wakeClockSleep();
                break;
            case R.id.btn_wake_clock_screen_off1:
                Toast.makeText(PowerThermalActivity.this, "持锁阻止系统灭屏（低亮度）测试！", Toast.LENGTH_SHORT)
                    .show();
                simulatePowerAnomaly.wakeClockScreenOff1();
                break;
            case R.id.btn_wake_clock_screen_off2:
                Toast.makeText(PowerThermalActivity.this, "持锁阻止系统灭屏测试！", Toast.LENGTH_SHORT).show();
                simulatePowerAnomaly.wakeClockScreenOff2();
                break;
            case R.id.btn_gps_locate:
                Toast.makeText(PowerThermalActivity.this, "GPS定位测试！", Toast.LENGTH_SHORT).show();
                simulatePowerAnomaly.gpsLocate();
                break;
            case R.id.btn_network_location:
                Toast.makeText(PowerThermalActivity.this, "network定位测试", Toast.LENGTH_SHORT).show();
                simulatePowerAnomaly.networkLocation();
                break;
            case R.id.btn_often_reboot:
                Toast.makeText(PowerThermalActivity.this, "频繁自启动测试！", Toast.LENGTH_SHORT).show();
                simulatePowerAnomaly.oftenReboot();
                break;
        }
    }

    public class UiHandler extends Handler {
        public static final int UPDATE_POWER_THERMAL_UI = 21;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_POWER_THERMAL_UI:
                    String result = ((String) msg.obj).replace(",", "\n");
                    Log.d(TAG, DEBUG_CLIENT + "update power thermal:" + result);
                    et_power_thermal_info.setText(result);
                    break;
                default:
                    Log.d(TAG, DEBUG_CLIENT + "PowerThermalActivity not support msg: " + msg.what);
                    break;
            }
        }
    }

    private void initUi() {
        et_power_thermal_info = (EditText) findViewById(R.id.text_power_info_show);
        et_power_thermal_info.setFocusableInTouchMode(false);
        btn_register_power = (Button) findViewById(R.id.btn_register_power);
        btn_register_power.setOnClickListener(this);
        btn_bluetooth_scan = (Button) findViewById(R.id.btn_bluetooth_scan);
        btn_bluetooth_scan.setOnClickListener(this);
        btn_alarm_wakeup = (Button) findViewById(R.id.btn_alarm_wakeup);
        btn_alarm_wakeup.setOnClickListener(this);
        btn_wake_clock_sleep = (Button) findViewById(R.id.btn_wake_clock_sleep);
        btn_wake_clock_sleep.setOnClickListener(this);
        btn_wake_clock_screen_off1 = (Button) findViewById(R.id.btn_wake_clock_screen_off1);
        btn_wake_clock_screen_off1.setOnClickListener(this);
        btn_wake_clock_screen_off2 = (Button) findViewById(R.id.btn_wake_clock_screen_off2);
        btn_wake_clock_screen_off2.setOnClickListener(this);
        btn_gps_locate = (Button) findViewById(R.id.btn_gps_locate);
        btn_gps_locate.setOnClickListener(this);
        btn_network_location = (Button) findViewById(R.id.btn_network_location);
        btn_network_location.setOnClickListener(this);
        btn_often_reboot = (Button) findViewById(R.id.btn_often_reboot);
        btn_often_reboot.setOnClickListener(this);
    }
}