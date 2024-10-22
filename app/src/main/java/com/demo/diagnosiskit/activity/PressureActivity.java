package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.DiagnosisKit.R;
import com.demo.DiagnosisKit.samples.PressureDemo;

public class PressureActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PressureActivity";
    private TextView tv_tempera_data, tv_memory_data, tv_io_data, tv_cpu_data;
    private Button btn_show_pressure;
    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private static Context mContext = null;
    private PressureDemo pressureDemo;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_system_pressure;
    }

    @Override
    protected void initViewAndData() {
        mContext = getApplicationContext();
        initUi();
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("PressureActivity");
        handlerThread.start();
        callbackHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {}
            }
        };
        pressureDemo = new PressureDemo(mContext, callbackHandler, uiHandler);
    }

    @Override
    public void finish() {
        pressureDemo.unSubscribe();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        pressureDemo.unSubscribe();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register_system_pressure:
                Toast.makeText(PressureActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                pressureDemo.subscribe();
                break;
        }
    }

    public class UiHandler extends Handler {
        public static final int UPDATE_CPU_UI = 11;
        public static final int UPDATE_MEM_UI = 12;
        public static final int UPDATE_TEM_UI = 13;
        public static final int UPDATE_IO_UI = 14;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            String updateMsg = ((String) msg.obj).trim();
            switch (msg.what) {
                case UPDATE_CPU_UI:
                    Log.d(TAG, DEBUG_CLIENT + "update cpu:" + updateMsg);
                    tv_cpu_data.setText(updateMsg);
                    break;
                case UPDATE_MEM_UI:
                    Log.d(TAG, DEBUG_CLIENT + "update memory:" + updateMsg);
                    tv_memory_data.setText(updateMsg);
                    break;
                case UPDATE_TEM_UI:
                    Log.d(TAG, DEBUG_CLIENT + "update tempera:" + updateMsg);
                    tv_tempera_data.setText(updateMsg);
                    break;
                case UPDATE_IO_UI:
                    Log.d(TAG, DEBUG_CLIENT + "update IO:" + updateMsg);
                    tv_io_data.setText(updateMsg);
                    break;
                default:
                    Log.d(TAG, DEBUG_CLIENT + "PressureActivity not support msg:" + msg.what);
                    break;
            }
        }
    }

    private void initUi() {
        tv_tempera_data = (TextView) findViewById(R.id.text_temperature_info);
        tv_tempera_data.setFocusableInTouchMode(false);
        tv_memory_data = (TextView) findViewById(R.id.text_memory_info);
        tv_memory_data.setFocusableInTouchMode(false);
        tv_io_data = (TextView) findViewById(R.id.text_io_info);
        tv_io_data.setFocusableInTouchMode(false);
        tv_cpu_data = (TextView) findViewById(R.id.text_cpu_info);
        tv_cpu_data.setFocusableInTouchMode(false);
        btn_show_pressure = (Button) findViewById(R.id.btn_register_system_pressure);
        btn_show_pressure.setOnClickListener(this);
    }
}