package com.demo.DiagnosisKit.samples;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.activity.PowerQueryActivity.UiHandler.UPDATE_POWER_QUERY_UI;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import com.hihonor.mcs.system.diagnosis.core.resource.PowerUsageStats;
import com.hihonor.mcs.system.diagnosis.manager.ResourceDiagnosis;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/* 器件功耗查询能力使用示例 */
public class PowerQueryDemo {
    private static final String TAG = "PowerQueryDemo";
    private ResourceDiagnosis mResourceDiagnosis;
    private Context context;
    private Map<String, String> powerData = new HashMap<>();
    private Handler uihandler;
    private static PowerQueryDemo powerQueryDemo = null;

    public PowerQueryDemo(Context context, Handler uihandler) {
        this.context = context;
        this.mResourceDiagnosis = ResourceDiagnosis.getInstance(context);
        this.uihandler = uihandler;
    }

    public static PowerQueryDemo getInstance(Context context, Handler uiHandler) {
        synchronized (PowerQueryDemo.class) {
            if (powerQueryDemo == null) {
                powerQueryDemo = new PowerQueryDemo(context, uiHandler);
            }
            return powerQueryDemo;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void query(Activity activity) {
        try {
            Optional<PowerUsageStats> powerUs = mResourceDiagnosis.queryPowerUsage(LocalDateTime.now().minusMinutes(60),
                                                                                   LocalDateTime.now().minusMinutes(0));
            Log.e(TAG, DEBUG_CLIENT + " powerUs: " + powerUs.isPresent());
            if (!powerUs.isPresent()) {
                activity.runOnUiThread(
                    () -> { Toast.makeText(context, "查询时间间隔不小于5分钟", Toast.LENGTH_SHORT).show(); });
            }
            powerUs.ifPresent(power -> {
                powerData.put("CPU", power.getCpu() + "");
                powerData.put("Display", power.getDisplay() + "");
                powerData.put("GPU", power.getGPU() + "");
                powerData.put("Audio", power.getAudio() + "");
                powerData.put("Camera", power.getCamera() + "");
                powerData.put("Gnss", power.getGnss() + "");
                powerData.put("Sensor", power.getSensor() + "");
                powerData.put("Modem", power.getModem() + "");
                powerData.put("WIFI", power.getWifi() + "");
                powerData.put("Bluetooth", power.getBluetooth() + "");
                powerData.put("Others", power.getOthers() + "");
                uihandler.obtainMessage(UPDATE_POWER_QUERY_UI, powerData).sendToTarget();
                Log.d(TAG,
                      DEBUG_CLIENT + power.toString() + " ,Audio: " + power.getAudio() + ",Bluetooth: "
                          + power.getBluetooth() + ",Camera: " + power.getCamera() + ",Cpu : " + power.getCpu()
                          + ",Display : " + power.getDisplay() + ",Gnss : " + power.getGnss() + ",Modem : "
                          + power.getModem() + ",Sensor : " + power.getSensor() + ",Wifi : " + power.getWifi()
                          + ",Gpu : " + power.getGPU() + ",Others : " + power.getOthers());
            });

        } catch (RuntimeException e) {
            Log.e(TAG, DEBUG_CLIENT + " ResourceDiagnosis exception:" + e.getMessage());
        }
    }
}
