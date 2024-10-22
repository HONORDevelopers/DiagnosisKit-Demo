package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.content.Context;
import android.content.Intent;
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

import com.demo.DiagnosisKit.MainActivity;
import com.demo.DiagnosisKit.R;
import com.demo.DiagnosisKit.samples.Dex2oatDemo;
import com.hihonor.mcs.system.diagnosis.core.status.DexoptStatus;

import java.util.ArrayList;
import java.util.List;

public class Dex2oatActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "Dex2oatActivity";
    public static final int QUERY_APP_DEXOPT = 2;
    private LinearLayout ll_pathname, ll_status, ll_reason, ll_artfilesize, ll_odexfilesize, ll_vdexfilesize,
        ll_instructionset;
    private Button btn_query;
    private TextView tv_title, tv_pathname, tv_status, tv_reason, tv_artfilesize, tv_odexfilesize, tv_vdexfilesize,
        tv_instructionset;
    private EditText et_info;

    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private Dex2oatDemo dex2oatDemo;
    private Context context;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_query_dex;
    }

    @Override
    protected void initViewAndData() {
        context = getApplicationContext();
        initUi();
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("Dex2oatActivity");
        handlerThread.start();
        callbackHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case QUERY_APP_DEXOPT:
                        dex2oatDemo = Dex2oatDemo.getInstance(context, uiHandler);
                        dex2oatDemo.query(Dex2oatActivity.this);
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                Log.d(TAG, DEBUG_CLIENT + "query start ");
                query();
                break;
        }
    }

    private void query() {
        new Thread(() -> {
            callbackHandler.obtainMessage(QUERY_APP_DEXOPT).sendToTarget();
            SystemClock.sleep(1000);
        }).start();
    }

    public class UiHandler extends Handler {
        public static final int UPDATE_DEX2OAT_UI = 20;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DEX2OAT_UI:
                    List<DexoptStatus> dexoptStatusList = (ArrayList<DexoptStatus>) msg.obj;
                    ll_pathname.setVisibility(View.VISIBLE);
                    ll_status.setVisibility(View.VISIBLE);
                    ll_reason.setVisibility(View.VISIBLE);
                    ll_artfilesize.setVisibility(View.VISIBLE);
                    ll_odexfilesize.setVisibility(View.VISIBLE);
                    ll_vdexfilesize.setVisibility(View.VISIBLE);
                    ll_instructionset.setVisibility(View.VISIBLE);

                    dexoptStatusList.forEach(dexopt -> {
                        tv_pathname.setText(dexopt.getPathName());
                        tv_status.setText(dexopt.getStatus());
                        tv_reason.setText(dexopt.getReason());
                        tv_artfilesize.setText(dexopt.getArtFileSize() + "");
                        tv_odexfilesize.setText(dexopt.getOdexFileSize() + "");
                        tv_vdexfilesize.setText(dexopt.getVdexFileSize() + "");
                        tv_instructionset.setText(dexopt.getInstructionSet());
                    });
                    break;
                default:
                    Log.d(TAG, DEBUG_CLIENT + "Dex2oatActivity not support msg:" + msg.what);
                    break;
            }
        }
    }

    private void initUi() {
        ll_pathname = findViewById(R.id.ll_pathname);
        ll_status = findViewById(R.id.ll_status);
        ll_reason = findViewById(R.id.ll_reason);
        ll_artfilesize = findViewById(R.id.ll_artfilesize);
        ll_odexfilesize = findViewById(R.id.ll_odexfilesize);
        ll_vdexfilesize = findViewById(R.id.ll_vdexfilesize);
        ll_instructionset = findViewById(R.id.ll_instructionset);

        tv_pathname = findViewById(R.id.tv_pathname);
        tv_status = findViewById(R.id.tv_status);
        tv_reason = findViewById(R.id.tv_reason);
        tv_artfilesize = findViewById(R.id.tv_artfilesize);
        tv_odexfilesize = findViewById(R.id.tv_odexfilesize);
        tv_vdexfilesize = findViewById(R.id.tv_vdexfilesize);
        tv_instructionset = findViewById(R.id.tv_instructionset);

        btn_query = findViewById(R.id.btn_query);
        btn_query.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        et_info = findViewById(R.id.consumption_text_info_show);
        et_info.setFocusableInTouchMode(false);
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
