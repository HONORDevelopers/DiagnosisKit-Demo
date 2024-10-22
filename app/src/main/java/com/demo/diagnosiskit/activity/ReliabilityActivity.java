package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.demo.DiagnosisKit.R;
import com.demo.DiagnosisKit.faults.SimulateStabilityAnomaly;
import com.demo.DiagnosisKit.samples.DynamicConfigDemo;
import com.demo.DiagnosisKit.samples.StabilityDemo;

public class ReliabilityActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ReliabilityActivity";
    Button btn_crash, btn_anr, btn_tombstone, btn_fd_leak, btn_thread_leak, btn_oom, btn_register_reliability,
        btn_killed, btn_reliability_empty, btn_enable_dynamic_config, btn_disable_dynamic_config;
    EditText et_reliability_fault_Data;
    private static Context context = null;
    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private StabilityDemo stabilityDemo;
    private SimulateStabilityAnomaly simulateStabilityAnomaly;
    private DynamicConfigDemo dynamicConfigDemo;
    public static final int DEBUG_ENABLE_CONFIG = 10;
    public static final int DEBUG_DISABLE_CONFIG = 11;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_reliability;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simulateStabilityAnomaly = new SimulateStabilityAnomaly(context, this);
        dynamicConfigDemo = new DynamicConfigDemo(context);
    }

    @Override
    protected void initViewAndData() {
        context = getApplicationContext();
        initUi();
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("ReliabilityActivity");
        handlerThread.start();
        callbackHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DEBUG_ENABLE_CONFIG:
                        dynamicConfigDemo.enableConfig();
                        break;
                    case DEBUG_DISABLE_CONFIG:
                        dynamicConfigDemo.disableConfig();
                        break;
                }
            }
        };
        stabilityDemo = new StabilityDemo(context, callbackHandler, uiHandler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_killed_binder:
                simulateStabilityAnomaly.showBinderChoice();
                break;
            case R.id.btn_thread_leak:
                Toast.makeText(ReliabilityActivity.this, "线程泄露测试！", Toast.LENGTH_SHORT).show();
                simulateStabilityAnomaly.threadLeak();
                break;
            case R.id.btn_fd_leak:
                Toast.makeText(ReliabilityActivity.this, "句柄泄露测试！", Toast.LENGTH_SHORT).show();
                simulateStabilityAnomaly.fdLeak();
                break;
            case R.id.btn_crash:
                Toast.makeText(ReliabilityActivity.this, "CRASH测试", Toast.LENGTH_SHORT).show();
                simulateStabilityAnomaly.crash();
                break;
            case R.id.btn_tombstone:
                Toast.makeText(ReliabilityActivity.this, "TOMBSTONE测试", Toast.LENGTH_SHORT).show();
                simulateStabilityAnomaly.tombstone();
                break;
            case R.id.btn_anr:
                Toast.makeText(ReliabilityActivity.this, "ANR测试", Toast.LENGTH_SHORT).show();
                simulateStabilityAnomaly.anr();
                break;
            case R.id.btn_oom:
                Toast.makeText(ReliabilityActivity.this, "OOM测试", Toast.LENGTH_SHORT).show();
                simulateStabilityAnomaly.memoryLeak();
                break;
            case R.id.btn_register_reliability:
                Toast.makeText(ReliabilityActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                stabilityDemo.subscribe();
                break;
            case R.id.btn_enable_dynamic_config:
                Toast.makeText(ReliabilityActivity.this, "使能动态配置！", Toast.LENGTH_SHORT).show();
                callbackHandler.obtainMessage(DEBUG_ENABLE_CONFIG).sendToTarget();
                break;
            case R.id.btn_disable_dynamic_config:
                Toast.makeText(ReliabilityActivity.this, "取消使能动态配置！", Toast.LENGTH_SHORT).show();
                callbackHandler.obtainMessage(DEBUG_DISABLE_CONFIG).sendToTarget();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class UiHandler extends Handler {
        public static final int UPDATE_RELIABILITY_FAULT_UI = 1;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_RELIABILITY_FAULT_UI:
                    String str = "" + ((String) msg.obj).replace(",", "\n");
                    Log.d(TAG, DEBUG_CLIENT + "update reliability:" + str);
                    et_reliability_fault_Data.setText(str);
                    break;
                default:
                    Log.d(TAG, DEBUG_CLIENT + "ReliabilityActivity not support msg:" + msg.what);
                    break;
            }
        }
    }

    private void initUi() {
        btn_crash = (Button) findViewById(R.id.btn_crash);
        btn_crash.setOnClickListener(this);
        btn_anr = (Button) findViewById(R.id.btn_anr);
        btn_anr.setOnClickListener(this);
        btn_tombstone = (Button) findViewById(R.id.btn_tombstone);
        btn_tombstone.setOnClickListener(this);
        btn_fd_leak = (Button) findViewById(R.id.btn_fd_leak);
        btn_fd_leak.setOnClickListener(this);
        btn_thread_leak = (Button) findViewById(R.id.btn_thread_leak);
        btn_thread_leak.setOnClickListener(this);
        btn_oom = (Button) findViewById(R.id.btn_oom);
        btn_oom.setOnClickListener(this);
        btn_register_reliability = (Button) findViewById(R.id.btn_register_reliability);
        btn_register_reliability.setOnClickListener(this);
        btn_killed = (Button) findViewById(R.id.btn_killed_binder);
        btn_killed.setOnClickListener(this);
        btn_reliability_empty = (Button) findViewById(R.id.btn_reliability_empty);
        btn_reliability_empty.getBackground().setAlpha(0);
        btn_enable_dynamic_config = (Button) findViewById(R.id.btn_enable_dynamic_config);
        btn_enable_dynamic_config.setOnClickListener(this);
        btn_disable_dynamic_config = (Button) findViewById(R.id.btn_disable_dynamic_config);
        btn_disable_dynamic_config.setOnClickListener(this);
        et_reliability_fault_Data = (EditText) findViewById(R.id.reliability_text_info_show);
        et_reliability_fault_Data.setFocusableInTouchMode(false);
    }
}