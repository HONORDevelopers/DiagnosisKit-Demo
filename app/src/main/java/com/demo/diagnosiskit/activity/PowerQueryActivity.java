package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

import com.demo.DiagnosisKit.MainActivity;
import com.demo.DiagnosisKit.R;
import com.demo.DiagnosisKit.samples.PowerQueryDemo;

import java.util.HashMap;

public class PowerQueryActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PowerQueryActivity";
    private static Context context;
    public static final int QUERY_DEVICE_POWER = 1;
    private LinearLayout ll_cpu, ll_display, ll_gpu, ll_audio, ll_camera, ll_gnss, ll_sensor, ll_modem, ll_wifi,
        ll_bluetooth, ll_others;
    private Button btn_query;
    private TextView tv_title, tv_cpu, tv_display, tv_gpu, tv_audio, tv_camera, tv_gnss, tv_sensor, tv_modem, tv_wifi,
        tv_bluetooth, tv_others, tv_unit_info;
    private EditText et_info;
    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private PowerQueryDemo powerQueryDemo;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_query;
    }

    @Override
    protected void initViewAndData() {
        context = getApplicationContext();
        initUi();
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("PowerQueryActivity");
        handlerThread.start();
        callbackHandler = new Handler(handlerThread.getLooper()) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case QUERY_DEVICE_POWER: {
                        powerQueryDemo = PowerQueryDemo.getInstance(context, uiHandler);
                        powerQueryDemo.query(PowerQueryActivity.this);
                        break;
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                Log.d(TAG, DEBUG_CLIENT + "power query start");
                query();
                break;
        }
    }

    public class UiHandler extends Handler {
        public static final int UPDATE_POWER_QUERY_UI = 20;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_POWER_QUERY_UI:
                    buildData((HashMap<String, String>) msg.obj);
                    break;
                default:
                    Log.d(TAG, DEBUG_CLIENT + "PowerQueryActivity not support msg:" + msg.what);
                    break;
            }
        }
    }

    private void query() {
        new Thread(() -> {
            callbackHandler.obtainMessage(QUERY_DEVICE_POWER).sendToTarget();
            SystemClock.sleep(1000);
        }).start();
    }

    private void buildData(HashMap<String, String> msgMap) {
        if (msgMap.isEmpty()) {
            return;
        }
        ll_cpu.setVisibility(View.VISIBLE);
        tv_cpu.setText(msgMap.get("CPU"));
        ll_display.setVisibility(View.VISIBLE);
        tv_display.setText(msgMap.get("Display"));
        ll_gpu.setVisibility(View.VISIBLE);
        tv_gpu.setText(msgMap.get("GPU"));
        ll_audio.setVisibility(View.VISIBLE);
        tv_audio.setText(msgMap.get("Audio"));
        ll_camera.setVisibility(View.VISIBLE);
        tv_camera.setText(msgMap.get("Camera"));
        ll_gnss.setVisibility(View.VISIBLE);
        tv_gnss.setText(msgMap.get("Gnss"));
        ll_sensor.setVisibility(View.VISIBLE);
        tv_sensor.setText(msgMap.get("Sensor"));
        ll_modem.setVisibility(View.VISIBLE);
        tv_modem.setText(msgMap.get("Modem"));
        ll_wifi.setVisibility(View.VISIBLE);
        tv_wifi.setText(msgMap.get("WIFI"));
        ll_bluetooth.setVisibility(View.VISIBLE);
        tv_bluetooth.setText(msgMap.get("Bluetooth"));
        ll_others.setVisibility(View.VISIBLE);
        tv_others.setText(msgMap.get("Others"));
        tv_unit_info.setVisibility(View.VISIBLE);
    }

    private void initUi() {
        ll_cpu = findViewById(R.id.ll_cpu);
        ll_display = findViewById(R.id.ll_display);
        ll_gpu = findViewById(R.id.ll_gpu);
        ll_audio = findViewById(R.id.ll_audio);
        ll_camera = findViewById(R.id.ll_camera);
        ll_gnss = findViewById(R.id.ll_gnss);
        ll_sensor = findViewById(R.id.ll_sensor);
        ll_modem = findViewById(R.id.ll_modem);
        ll_wifi = findViewById(R.id.ll_wifi);
        ll_bluetooth = findViewById(R.id.ll_bluetooth);
        ll_others = findViewById(R.id.ll_others);
        findViewById(R.id.btn_query).setOnClickListener(this);
        btn_query = findViewById(R.id.btn_query);
        tv_title = findViewById(R.id.tv_title);
        tv_unit_info = findViewById(R.id.tv_unit_info);
        et_info = findViewById(R.id.consumption_text_info_show);
        et_info.setFocusableInTouchMode(false);

        tv_cpu = findViewById(R.id.tv_cpu);
        tv_display = findViewById(R.id.tv_display);
        tv_gpu = findViewById(R.id.tv_gpu);
        tv_audio = findViewById(R.id.tv_audio);
        tv_camera = findViewById(R.id.tv_camera);
        tv_gnss = findViewById(R.id.tv_gnss);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_modem = findViewById(R.id.tv_modem);
        tv_wifi = findViewById(R.id.tv_wifi);
        tv_bluetooth = findViewById(R.id.tv_bluetooth);
        tv_others = findViewById(R.id.tv_others);
        Intent intent = getIntent();
        if (intent != null) {
            if (!TextUtils.isEmpty(intent.getStringExtra(MainActivity.DATA_TITLE_KEY))) {
                tv_title.setText(intent.getStringExtra(MainActivity.DATA_TITLE_KEY));
            }
            if (!TextUtils.isEmpty(intent.getStringExtra(MainActivity.DATA_CONTENT_KEY))) {
                btn_query.setText(intent.getStringExtra(MainActivity.DATA_CONTENT_KEY));
            }
        }
    }
}
