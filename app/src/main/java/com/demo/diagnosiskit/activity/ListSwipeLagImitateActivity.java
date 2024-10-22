package com.demo.DiagnosisKit.activity;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.Utils.workTime;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.demo.DiagnosisKit.R;
import com.demo.DiagnosisKit.samples.PerformanceDemo;

import java.util.ArrayList;

public class ListSwipeLagImitateActivity extends BaseActivity implements AbsListView.OnScrollListener {
    private static final String TAG = "ListSwipeLagImitateActivity";
    private EditText et_scroll_failure;
    private ListView lv_demo;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataSource = new ArrayList<>();
    private UiHandler uiHandler;
    private HandlerThread handlerThread;
    private Handler callbackHandler;
    private static Context mContext = null;
    private PerformanceDemo performanceDemo;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_listswipe_imitate;
    }

    @Override
    protected void initViewAndData() {
        mContext = getApplicationContext();
        initUi();
        uiHandler = new UiHandler();
        handlerThread = new HandlerThread("ListSwipeLagImitateActivity");
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        lv_demo.requestLayout();
        adapter.notifyDataSetChanged();
        workTime(1 * 200);
        lv_demo.requestLayout();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lv_demo.requestLayout();
        adapter.notifyDataSetChanged();
        workTime(1 * 200);
        lv_demo.requestLayout();
        adapter.notifyDataSetChanged();
    }

    class UiHandler extends Handler {
        public static final int UPDTAE_LIST_SWIPELAG_UI = 31;

        UiHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDTAE_LIST_SWIPELAG_UI:
                    String str = ((String) msg.obj).replace(",", "\n");
                    Log.d(TAG, DEBUG_CLIENT + "update list swipelag:" + str);
                    et_scroll_failure.setText(str);
                    break;
                default:
                    Log.d(TAG, DEBUG_CLIENT + "ListSwipeLagImitateActivity not support msg:" + msg.what);
                    break;
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

    private void initUi() {
        lv_demo = findViewById(R.id.lv_demo);
        for (int i = 0; i < 20; i++) {
            dataSource.add("数据" + i);
        }
        adapter = new ArrayAdapter<>(ListSwipeLagImitateActivity.this, R.layout.item_list, dataSource);
        et_scroll_failure = findViewById(R.id.et_text_laginfo_show);
        et_scroll_failure.setFocusableInTouchMode(false);
        lv_demo.setAdapter(adapter);
        lv_demo.setOnScrollListener(ListSwipeLagImitateActivity.this);
    }
}
