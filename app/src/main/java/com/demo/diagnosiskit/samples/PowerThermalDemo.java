package com.demo.DiagnosisKit.samples;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.activity.PowerThermalActivity.UiHandler.UPDATE_POWER_THERMAL_UI;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.hihonor.mcs.system.diagnosis.core.powerthermal.PowerThermal;
import com.hihonor.mcs.system.diagnosis.core.powerthermal.PowerThermalCallback;
import com.hihonor.mcs.system.diagnosis.core.powerthermal.PowerThermalMetric;
import com.hihonor.mcs.system.diagnosis.core.powerthermal.PowerThermalPayload;
import com.hihonor.mcs.system.diagnosis.manager.FaultDiagnosis;

import java.text.SimpleDateFormat;
import java.util.List;

/* 订阅功耗故障能力使用示例 */
public class PowerThermalDemo {
    private static final String TAG = "PowerThermalDemo";
    private PowerCallbackImpl powerCallbackImpl = null;
    private Context context;
    private FaultDiagnosis faultDiagnosis;
    private Handler callbackHandler;
    private Handler uiHandler;

    public PowerThermalDemo(Context context, Handler callbackHandler, Handler uiHandler) {
        this.context = context;
        this.faultDiagnosis = FaultDiagnosis.getInstance(context);
        this.callbackHandler = callbackHandler;
        this.uiHandler = uiHandler;
    }

    public void subscribe() {
        // 获取功耗诊断的代码示例
        try {
            if (powerCallbackImpl != null) {
                faultDiagnosis.unSubscribePowerThermal(powerCallbackImpl);
            }
            PowerThermal powerThermal =
                new PowerThermal.Builder().withKind(PowerThermal.Kind.POWER_EXCESSIVE_DRAIN).build();
            powerCallbackImpl = new PowerCallbackImpl();
            faultDiagnosis.subscribePowerThermal(powerThermal, powerCallbackImpl, callbackHandler);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe() {
        try {
            faultDiagnosis.unSubscribePowerThermal(powerCallbackImpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PowerCallbackImpl implements PowerThermalCallback {
        @Override
        public void onPowerThermalReported(PowerThermalPayload powerThermalPayload) {
            List<PowerThermalMetric> powerThermalMetrics = powerThermalPayload.getPowerThermalMetrics();
            for (PowerThermalMetric powerThermalMetric : powerThermalMetrics) {
                Log.d(TAG, DEBUG_CLIENT + powerThermalMetric.toString());
                String powerType = "";
                try {
                    powerType = powerThermalMetric.getPowerType().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long happenTime = powerThermalMetric.getHappenTime();
                String happenTimeStr = getTime(happenTime * 1000);
                int pid = powerThermalMetric.getPid();
                String diagInfo = powerThermalMetric.getDiagInfo();
                StringBuffer sb = new StringBuffer();
                sb.append("发生时间: "
                          + "\n" + happenTimeStr + "\n")
                    .append("进程号: "
                            + "\n" + pid + "\n")
                    .append("功耗异常类型: "
                            + "\n" + powerType + "\n")
                    .append("诊断信息: "
                            + "\n" + diagInfo + "\n");
                uiHandler.obtainMessage(UPDATE_POWER_THERMAL_UI, sb.toString()).sendToTarget();
            }
        }
    }

    private String getTime(long timestamp) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
        return dateFormatter.format(timestamp);
    }
}
