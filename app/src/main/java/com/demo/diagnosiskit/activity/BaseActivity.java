package com.demo.DiagnosisKit.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        initViewAndData();
    }

    protected abstract int getLayoutResID();

    protected abstract void initViewAndData();
}
