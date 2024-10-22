package com.demo.DiagnosisKit.samples;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;

import android.content.Context;
import android.util.Log;

import com.hihonor.mcs.system.diagnosis.core.configuration.ConfigFactory;
import com.hihonor.mcs.system.diagnosis.manager.Configurator;

/* 动态配置能力使用示例 */
public class DynamicConfigDemo {
    private static final String TAG = "DynamicConfigDemo";
    private Context context;

    public DynamicConfigDemo(Context context) {
        this.context = context;
    }

    public void enableConfig() {
        config(true);
    }

    public void disableConfig() {
        config(false);
    }

    private void config(boolean enable) {
         try {
             Configurator.getInstance(context)
                     .add(ConfigFactory.buildFdLeakConfig(enable))
                     .add(ConfigFactory.buildJavaMemoryLeakConfig(enable))
                     .add(ConfigFactory.buildThreadLeakConfig(enable))
                     .add(ConfigFactory.buildDmaBufMemoryLeakConfig(enable))
                     .add(ConfigFactory.buildNativeMemoryLeakConfig(enable))
                     .add(ConfigFactory.buildOOMCrashConfig(enable))
                     .submit();
         } catch (RuntimeException e) {
             Log.e(TAG, DEBUG_CLIENT + ",DEBUG_CONFIG exception:" + e.getMessage());
         }
    }
}
