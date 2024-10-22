package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.content.Context;
import android.content.Intent;
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
import com.demo.DiagnosisKit.samples.PerformanceDemo;

public class PerformanceActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PerformanceActivity";
    private static Context mContext = null;
    private EditText text_list_swipe_junk_info_show, text_app_junk_info_show, text_dynamic_junk_info_show;
    private Button btn_show_performance_info, btn_firing_timeout_activity, btn_list_swipe_lag_imitate;
    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private PerformanceDemo performanceDemo;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_performance;
    }

    @Override
    protected void initViewAndData() {
        mContext = getApplicationContext();
        initUi();
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("PerformanceActivity");
        handlerThread.start();
        callbackHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {}
            }
        };
        performanceDemo = new PerformanceDemo(mContext, callbackHandler, uiHandler);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_performance:
                Toast.makeText(PerformanceActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                performanceDemo.subscribe();
                break;
            case R.id.firing_timeout_activity:
                startActivity(new Intent(PerformanceActivity.this, SlowFiringActivity.class));
                break;
            case R.id.list_swipe_lag_imitate:
                startActivity(new Intent(PerformanceActivity.this, ListSwipeLagImitateActivity.class));
                break;
        }
    }

    public class UiHandler extends Handler {
        public static final int UPDTAE_LIST_SWIPELAG_UI = 31;
        public static final int UPDATE_APP_SKIP_UI = 32;
        public static final int UPDATE_ANIMATION_JANK_UI = 33;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            String msgShow = ((String) msg.obj).replace(",", "\n");
            switch (msg.what) {
                case UPDTAE_LIST_SWIPELAG_UI:
                    Log.d(TAG, DEBUG_CLIENT + "update list swipelag:" + msgShow);
                    text_list_swipe_junk_info_show.setText(msgShow);
                    break;
                case UPDATE_APP_SKIP_UI:
                    Log.d(TAG, DEBUG_CLIENT + "update app skip jank:" + msgShow);
                    text_app_junk_info_show.setText(msgShow);
                    break;
                case UPDATE_ANIMATION_JANK_UI:
                    Log.d(TAG, DEBUG_CLIENT + "update animation jank:" + msgShow);
                    text_dynamic_junk_info_show.setText(msgShow);
                    break;
                default:
                    Log.d(TAG, DEBUG_CLIENT + "PerformanceActivity not support msg:" + +msg.what);
                    break;
            }
        }
    }

    private void initUi() {
        text_list_swipe_junk_info_show = (EditText) findViewById(R.id.text_list_swipe_junk_info_show);
        text_app_junk_info_show = (EditText) findViewById(R.id.text_app_junk_info_show);
        text_dynamic_junk_info_show = (EditText) findViewById(R.id.text_dynamic_junk_info_show);
        text_dynamic_junk_info_show.setFocusableInTouchMode(false);
        text_app_junk_info_show.setFocusableInTouchMode(false);
        text_list_swipe_junk_info_show.setFocusableInTouchMode(false);
        btn_show_performance_info = (Button) findViewById(R.id.register_performance);
        btn_show_performance_info.setOnClickListener(this);
        btn_firing_timeout_activity = findViewById(R.id.firing_timeout_activity);
        btn_firing_timeout_activity.setOnClickListener(this);
        btn_list_swipe_lag_imitate = findViewById(R.id.list_swipe_lag_imitate);
        btn_list_swipe_lag_imitate.setOnClickListener(this);
    }
}