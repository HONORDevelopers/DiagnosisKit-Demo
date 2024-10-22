package com.demo.DiagnosisKit.samples;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.activity.PressureActivity.UiHandler.UPDATE_CPU_UI;
import static com.demo.DiagnosisKit.activity.PressureActivity.UiHandler.UPDATE_IO_UI;
import static com.demo.DiagnosisKit.activity.PressureActivity.UiHandler.UPDATE_MEM_UI;
import static com.demo.DiagnosisKit.activity.PressureActivity.UiHandler.UPDATE_TEM_UI;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.hihonor.mcs.system.diagnosis.core.pressure.CpuWatchPoint;
import com.hihonor.mcs.system.diagnosis.core.pressure.IoWatchPoint;
import com.hihonor.mcs.system.diagnosis.core.pressure.MemoryWatchPoint;
import com.hihonor.mcs.system.diagnosis.core.pressure.PressureCallback;
import com.hihonor.mcs.system.diagnosis.core.pressure.PressurePayload;
import com.hihonor.mcs.system.diagnosis.core.pressure.Resource;
import com.hihonor.mcs.system.diagnosis.core.pressure.TemperatureWatchPoint;
import com.hihonor.mcs.system.diagnosis.manager.PressureDiagnosis;

/* 订阅系统压力状态使用示例 */
public class PressureDemo {
    private static final String TAG = "PressureDemo";
    private PressureCallbackImpl pressureCallbackImpl = null;
    private Context context;
    private PressureDiagnosis pressureDiagnosis;
    private Handler callbackHandler;
    private Handler uiHandler;

    public PressureDemo(Context context, Handler callbackHandler, Handler uiHandler) {
        this.context = context;
        this.pressureDiagnosis = PressureDiagnosis.getInstance(context);
        this.callbackHandler = callbackHandler;
        this.uiHandler = uiHandler;
    }

    public void subscribe() {
        // 获取压力监听的代码示例
        try {
            if (pressureCallbackImpl != null) {
                pressureDiagnosis.unSubscribePressure(pressureCallbackImpl);
            }
            Resource resource = new Resource.Builder()
                                    .withKind(Resource.Kind.RESOURCE_CPU)
                                    .withKind(Resource.Kind.RESOURCE_MEMORY)
                                    .withKind(Resource.Kind.RESOURCE_IO)
                                    .withKind(Resource.Kind.RESOURCE_TEMPERATURE)
                                    .build();
            pressureCallbackImpl = new PressureCallbackImpl();
            pressureDiagnosis.subscribePressure(resource, pressureCallbackImpl, callbackHandler);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe() {
        try {
            pressureDiagnosis.unSubscribePressure(pressureCallbackImpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PressureCallbackImpl implements PressureCallback {
        @Override
        public void onPressureReported(PressurePayload pressurePayload) {
            MemoryWatchPoint memoryMonitor = pressurePayload.getMemoryWatchPoint();
            if (memoryMonitor.getMemoryStatus() != null) {
                Log.d(TAG, DEBUG_CLIENT + "Pressure:" + memoryMonitor);
                try {
                    String tempMem =
                        memoryMonitor.toString().replace("MemoryWatchPoint{memoryStatus=", "").replace("}", "");
                    uiHandler.obtainMessage(UPDATE_MEM_UI, tempMem).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            TemperatureWatchPoint temperatureWatchPoint = pressurePayload.getTemperatureWatchPoint();
            if (temperatureWatchPoint.getTemperatureStatus() != null) {
                Log.d(TAG, DEBUG_CLIENT + "Pressure:" + temperatureWatchPoint);
                try {
                    String tempTempera = temperatureWatchPoint.toString()
                                             .replace("TemperatureWatchPoint{temperatureStatus=", "")
                                             .replace("}", "");
                    uiHandler.obtainMessage(UPDATE_TEM_UI, tempTempera).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CpuWatchPoint cpuWatchPoint = pressurePayload.getCpuWatchPoint();
            if (cpuWatchPoint.getCpuStatus() != null) {
                Log.d(TAG, DEBUG_CLIENT + "Pressure:" + cpuWatchPoint);
                try {
                    String tempCpu = cpuWatchPoint.toString().replace("CpuWatchPoint{cpuStatus=", "").replace("}", "");
                    uiHandler.obtainMessage(UPDATE_CPU_UI, tempCpu).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            IoWatchPoint ioWatchPoint = pressurePayload.getIoWatchPoint();
            if (ioWatchPoint.getIoStatus() != null) {
                Log.d(TAG, DEBUG_CLIENT + "Pressure:" + ioWatchPoint);
                try {
                    String tempIo = ioWatchPoint.toString().replace("IoWatchPoint{ioStatus=", "").replace("}", "");
                    uiHandler.obtainMessage(UPDATE_IO_UI, tempIo).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
