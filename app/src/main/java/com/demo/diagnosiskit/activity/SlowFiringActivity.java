package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.Utils.workTime;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import androidx.annotation.Nullable;

import com.demo.DiagnosisKit.R;
import com.demo.DiagnosisKit.samples.PerformanceDemo;

public class SlowFiringActivity extends Activity {
    private static final String TAG = "SlowFiringActivity";
    private EditText text_slow_firing;
    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private static Context mContext = null;
    private PerformanceDemo performanceDemo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(getLayoutResID());
        initViewAndData();
        workTime(1 * 2000);
    }

    protected int getLayoutResID() {
        workTime(1 * 2000);
        return R.layout.activity_slow_firing;
    }

    protected void initViewAndData() {
        text_slow_firing = findViewById(R.id.et_text_info_show);
        text_slow_firing.setFocusableInTouchMode(false);
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("SlowFiringActivity");
        handlerThread.start();
        callbackHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {}
            }
        };
        performanceDemo = new PerformanceDemo(mContext, callbackHandler, uiHandler);
        performanceDemo.subscribe();
    }

    public class UiHandler extends Handler {
        public static final int UPDATE_SLOW_FIRING = 20;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SLOW_FIRING: {
                    String str = ((String) msg.obj).replace(",", "\n");
                    Log.d(TAG, DEBUG_CLIENT + " update slow firing:" + str);
                    text_slow_firing.setText(str);
                    break;
                }
            }
        }
    }

    @Override
    public void finish() {
        performanceDemo.unSubscribe();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        performanceDemo.unSubscribe();
        super.onDestroy();
    }
}
