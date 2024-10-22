package com.demo.DiagnosisKit.samples;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.Utils.fetchLog;
import static com.demo.DiagnosisKit.Utils.getTime;
import static com.demo.DiagnosisKit.activity.ReliabilityActivity.UiHandler.UPDATE_RELIABILITY_FAULT_UI;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.hihonor.mcs.system.diagnosis.core.stability.ANRMetric;
import com.hihonor.mcs.system.diagnosis.core.stability.CrashMetirc;
import com.hihonor.mcs.system.diagnosis.core.stability.FdleakMetric;
import com.hihonor.mcs.system.diagnosis.core.stability.KilledMetric;
import com.hihonor.mcs.system.diagnosis.core.stability.MemoryleakMetric;
import com.hihonor.mcs.system.diagnosis.core.stability.Stability;
import com.hihonor.mcs.system.diagnosis.core.stability.StabilityCallback;
import com.hihonor.mcs.system.diagnosis.core.stability.StabilityPayload;
import com.hihonor.mcs.system.diagnosis.core.stability.ThreadleakMetric;
import com.hihonor.mcs.system.diagnosis.core.stability.TombstoneMetric;
import com.hihonor.mcs.system.diagnosis.manager.FaultDiagnosis;

import java.util.List;

/* 订阅稳定性故障能力使用示例 */
public class StabilityDemo {
    private static final String TAG = "StabilityDemo";
    private StabiltyCallbackImpl stabiltyCallbackImpl = null;
    private Context context;
    private FaultDiagnosis faultDiagnosis;
    private Handler callbackHandler;
    private Handler uiHandler;

    public StabilityDemo(Context context, Handler callbackHandler, Handler uiHandler) {
        this.context = context;
        this.faultDiagnosis = FaultDiagnosis.getInstance(context);
        this.callbackHandler = callbackHandler;
        this.uiHandler = uiHandler;
    }

    public void subscribe() {
        try {
            if (stabiltyCallbackImpl != null) {
                faultDiagnosis.unSubscribeStability(stabiltyCallbackImpl);
            }
            Stability stability = new Stability.Builder()
                                      .withKind(Stability.Kind.ANR)
                                      .withKind(Stability.Kind.CRASH)
                                      .withKind(Stability.Kind.TOMBSTONE)
                                      .withKind(Stability.Kind.FDLEAK)
                                      .withKind(Stability.Kind.THREADLEAK)
                                      .withKind(Stability.Kind.MEMORYLEAK)
                                      .withKind(Stability.Kind.KILLED)
                                      .build();
            stabiltyCallbackImpl = new StabiltyCallbackImpl();
            faultDiagnosis.subscribeStability(stability, stabiltyCallbackImpl, callbackHandler);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe() {
        try {
            faultDiagnosis.unSubscribeStability(stabiltyCallbackImpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class StabiltyCallbackImpl implements StabilityCallback {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onStabilityReported(StabilityPayload stabiltyPayload) {
            StringBuffer faultInfo = new StringBuffer();
            List<ANRMetric> anrs = stabiltyPayload.getAnrMetrics();
            if (anrs == null) {
                return;
            }
            for (ANRMetric anrMetric : anrs) {
                Log.d(TAG, DEBUG_CLIENT + anrMetric.toString());
                String diagInfo = "";
                try {
                    diagInfo = anrMetric.getDiagInfo();
                    String fetchLog = fetchLog(context, diagInfo);
                    String appVersion = anrMetric.getAppVersion();
                    String fg = anrMetric.getFg();
                    int pid = 0;
                    try {
                        pid = anrMetric.getPid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String liveTime = anrMetric.getLifeTime();
                    long happenTime = anrMetric.getHappenTime();
                    String happenTimeStr = getTime(happenTime * 1000);
                    String processName = anrMetric.getProcessName();
                    String trustStack = anrMetric.getTrustStack();
                    faultInfo
                        .append("进程名: "
                                + "\n" + processName + "\n")
                        .append("进程号： "
                                + "\n" + pid + "\n")
                        .append("发生时间: "
                                + "\n" + happenTimeStr + "\n")
                        .append("存活时间："
                                + "\n" + liveTime + "\n")
                        .append("应用版本: "
                                + "\n" + appVersion + "\n")
                        .append("是否前台: "
                                + "\n" + fg + "\n")
                        .append("诊断信息: "
                                + "\n" + diagInfo + "\n")
                        .append("堆栈信息: "
                                + "\n" + trustStack + "\n")
                        .append("日志目录: "
                                + "\n" + fetchLog + "\n");

                } catch (Exception e) {
                    e.printStackTrace();
                    faultInfo.append(anrMetric.toString() + "\n");
                }
            }

            List<CrashMetirc> crashs = stabiltyPayload.getCrashMetircs();
            if (crashs == null) {
                return;
            }
            for (CrashMetirc crashMetirc : crashs) {
                Log.d(TAG, DEBUG_CLIENT + crashMetirc.toString());
                String diagInfo = "";
                try {
                    diagInfo = crashMetirc.getDiagInfo();
                    String fetchLog = fetchLog(context, diagInfo);
                    String appVersion = crashMetirc.getAppVersion();
                    String fg = crashMetirc.getFg();
                    int pid = 0;
                    try {
                        pid = crashMetirc.getPid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String liveTime = crashMetirc.getLifeTime();
                    long happenTime = crashMetirc.getHappenTime();
                    String happenTimeStr = getTime(happenTime * 1000);
                    String processName = crashMetirc.getProcessName();
                    String trustStack = crashMetirc.getTrustStack();
                    faultInfo
                        .append("进程名: "
                                + "\n" + processName + "\n")
                        .append("进程号： "
                                + "\n" + pid + "\n")
                        .append("发生时间: "
                                + "\n" + happenTimeStr + "\n")
                        .append("存活时间："
                                + "\n" + liveTime + "\n")
                        .append("应用版本: "
                                + "\n" + appVersion + "\n")
                        .append("是否前台: "
                                + "\n" + fg + "\n")
                        .append("诊断信息: "
                                + "\n" + diagInfo + "\n")
                        .append("堆栈信息： "
                                + "\n" + trustStack + "\n")
                        .append("日志目录: "
                                + "\n" + fetchLog + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    faultInfo.append(crashMetirc.toString() + "\n");
                }
            }

            List<TombstoneMetric> tombstones = stabiltyPayload.getTombstoneMetrics();
            if (tombstones == null) {
                return;
            }
            for (TombstoneMetric tombstone : tombstones) {
                Log.d(TAG, DEBUG_CLIENT + tombstone.toString());
                String diagInfo = "";
                try {
                    diagInfo = tombstone.getDiagInfo();
                    String fetchLog = fetchLog(context, diagInfo);
                    String appVersion = tombstone.getAppVersion();
                    String fg = tombstone.getFg();
                    int pid = 0;
                    try {
                        pid = tombstone.getPid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long happenTime = tombstone.getHappenTime();
                    String happenTimeStr = getTime(happenTime * 1000);
                    String processName = tombstone.getProcessName();
                    String trustStack = tombstone.getTrustStack();
                    faultInfo
                        .append("进程名: "
                                + "\n" + processName + "\n")
                        .append("进程号："
                                + "\n" + pid + "\n")
                        .append("发生时间: "
                                + "\n" + happenTimeStr + "\n")
                        .append("应用版本: "
                                + "\n" + appVersion + "\n")
                        .append("是否前台: "
                                + "\n" + fg + "\n")
                        .append("诊断信息: "
                                + "\n" + diagInfo + "\n")
                        .append("堆栈信息: "
                                + "\n" + trustStack + "\n")
                        .append("日志目录: "
                                + "\n" + fetchLog + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    faultInfo.append(tombstone.toString() + "\n");
                }
            }

            List<ThreadleakMetric> threadleaks = stabiltyPayload.getThreadleakMetrics();
            if (threadleaks == null) {
                return;
            }
            for (ThreadleakMetric threadleak : threadleaks) {
                Log.d(TAG, DEBUG_CLIENT + threadleak.toString());
                String diagInfo = "";
                try {
                    String appVersion = threadleak.getAppVersion();
                    int pid = 0;
                    try {
                        pid = threadleak.getPid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long happenTime = threadleak.getHappenTime();
                    String happenTimeStr = getTime(happenTime * 1000);
                    String processName = threadleak.getProcessName();
                    diagInfo = threadleak.getDiagInfo();
                    String fetchLog = fetchLog(context, diagInfo);
                    String threadNum = threadleak.getThreadNum() + "";
                    faultInfo
                        .append("进程名: "
                                + "\n" + processName + "\n")
                        .append("进程号： "
                                + "\n" + pid + "\n")
                        .append("发生时间: "
                                + "\n" + happenTimeStr + "\n")
                        .append("线程数量: "
                                + "\n" + threadNum + "\n")
                        .append("应用版本： "
                                + "\n" + appVersion + "\n")
                        .append("诊断信息： "
                                + "\n" + diagInfo + "\n")
                        .append("日志目录: "
                                + "\n" + fetchLog + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    faultInfo.append(threadleak.toString() + "\n");
                }
            }

            List<FdleakMetric> fdleaks = stabiltyPayload.getFdleakMetrics();
            if (fdleaks == null) {
                return;
            }
            for (FdleakMetric fdleak : fdleaks) {
                Log.d(TAG, DEBUG_CLIENT + fdleak.toString());
                try {
                    int pid = 0;
                    try {
                        pid = fdleak.getPid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long happenTime = fdleak.getHappenTime();
                    String happenTimeStr = getTime(happenTime * 1000);
                    String processName = fdleak.getProcessName();
                    String diagInfo = fdleak.getDiagInfo();
                    String fetchLog = fetchLog(context, diagInfo);
                    String fileNum = fdleak.getFileNum() + "";
                    String appVersion = fdleak.getAppVersion();
                    faultInfo
                        .append("进程名: "
                                + "\n" + processName + "\n")
                        .append("进程号："
                                + "\n" + pid + "\n")
                        .append("发生时间: "
                                + "\n" + happenTimeStr + "\n")
                        .append("句柄数量: "
                                + "\n" + fileNum + "\n")
                        .append("应用版本: "
                                + "\n" + appVersion + "\n")
                        .append("诊断信息: "
                                + "\n" + diagInfo + "\n")
                        .append("日志目录: "
                                + "\n" + fetchLog + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    faultInfo.append(fdleak.toString() + "\n");
                }
            }

            List<MemoryleakMetric> memoryleaks = stabiltyPayload.getMemoryleakMetrics();
            if (memoryleaks == null) {
                return;
            }
            for (MemoryleakMetric memoryleak : memoryleaks) {
                Log.d(TAG, DEBUG_CLIENT + memoryleak.toString());
                try {
                    int pid = 0;
                    try {
                        pid = memoryleak.getPid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String diagInfo = memoryleak.getDiagInfo();
                    String fetchLog = fetchLog(context, diagInfo);
                    String appVersion = memoryleak.getAppVersion();
                    long happenTime = memoryleak.getHappenTime();
                    String happenTimeStr = getTime(happenTime * 1000);
                    String processName = memoryleak.getProcessName();
                    String leakType = memoryleak.getLeakType().toString();
                    float slope = memoryleak.getSlope();
                    int vss = memoryleak.getVss();
                    int memInc = memoryleak.getMemInc();
                    int memRatio = memoryleak.getMemRatio();
                    int memTotal = memoryleak.getMemTotal();
                    faultInfo
                        .append("进程名: "
                                + "\n" + processName + "\n")
                        .append("进程号："
                                + "\n" + pid + "\n")
                        .append("发生时间: "
                                + "\n" + happenTimeStr + "\n")
                        .append("应用版本： "
                                + "\n" + appVersion + "\n")
                        .append("内存泄露类型: "
                                + "\n" + leakType + "\n")
                        .append("应用存活时间: "
                                + "\n" + slope + "\n")
                        .append("VSS内存占用: "
                                + "\n" + vss + "\n")
                        .append("内存增量: "
                                + "\n" + memInc + "\n")
                        .append("进程占总内存百分比: "
                                + "\n" + memRatio + "\n")
                        .append("总内存: "
                                + "\n" + memTotal + "\n")
                        .append("诊断信息: "
                                + "\n" + diagInfo + "\n")
                        .append("日志目录: "
                                + "\n" + fetchLog + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    faultInfo.append(memoryleak.toString() + "\n");
                }
            }

            List<KilledMetric> killedMetrics = stabiltyPayload.getKilledMetrics();
            if (killedMetrics == null) {
                return;
            }
            for (KilledMetric killedMetric : killedMetrics) {
                Log.d(TAG, DEBUG_CLIENT + killedMetric.toString());
                try {
                    int pid = 0;
                    try {
                        pid = killedMetric.getPid();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String diagInfo = killedMetric.getDiagInfo();
                    String fetchLog = fetchLog(context, diagInfo);
                    String appVersion = killedMetric.getAppVersion();
                    long happenTime = killedMetric.getHappenTime();
                    String happenTimeStr = getTime(happenTime * 1000);
                    String processName = killedMetric.getProcessName();
                    String type = killedMetric.getType().toString();
                    String trustStack = killedMetric.getTrustStack();
                    faultInfo
                        .append("进程名: "
                                + "\n" + processName + "\n")
                        .append("进程号： "
                                + "\n" + pid + "\n")
                        .append("类型: "
                                + "\n" + type + "\n")
                        .append("应用版本: "
                                + "\n" + appVersion + "\n")
                        .append("发生时间: "
                                + "\n" + happenTimeStr + "\n")
                        .append("诊断信息: "
                                + "\n" + diagInfo + "\n")
                        .append("堆栈信息:"
                                + "\n" + trustStack + "\n")
                        .append("日志目录: "
                                + "\n" + fetchLog + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                    faultInfo.append(killedMetric.toString() + "\n");
                }
            }
            try {
                uiHandler.obtainMessage(UPDATE_RELIABILITY_FAULT_UI, faultInfo.toString()).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
