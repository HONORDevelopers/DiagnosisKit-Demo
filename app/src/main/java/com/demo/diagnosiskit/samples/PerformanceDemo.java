package com.demo.DiagnosisKit.samples;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.Utils.fetchLog;
import static com.demo.DiagnosisKit.Utils.getTime;
import static com.demo.DiagnosisKit.activity.PerformanceActivity.UiHandler.UPDATE_ANIMATION_JANK_UI;
import static com.demo.DiagnosisKit.activity.PerformanceActivity.UiHandler.UPDATE_APP_SKIP_UI;
import static com.demo.DiagnosisKit.activity.PerformanceActivity.UiHandler.UPDTAE_LIST_SWIPELAG_UI;
import static com.demo.DiagnosisKit.activity.SlowFiringActivity.UiHandler.UPDATE_SLOW_FIRING;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.RequiresApi;

import com.hihonor.mcs.system.diagnosis.core.performance.ActivityStartTimeoutMetric;
import com.hihonor.mcs.system.diagnosis.core.performance.AnimationJankFrameMetric;
import com.hihonor.mcs.system.diagnosis.core.performance.AppSkipFrameMetric;
import com.hihonor.mcs.system.diagnosis.core.performance.AppStartupSlowMetric;
import com.hihonor.mcs.system.diagnosis.core.performance.FlingJankFrameMetric;
import com.hihonor.mcs.system.diagnosis.core.performance.Performance;
import com.hihonor.mcs.system.diagnosis.core.performance.PerformanceCallback;
import com.hihonor.mcs.system.diagnosis.core.performance.PerformancePayload;
import com.hihonor.mcs.system.diagnosis.manager.FaultDiagnosis;

import java.util.List;

/* 订阅性能故障能力使用示例 */
public class PerformanceDemo {
    private static final String TAG = "PerformanceDemo";
    private PerformanceCallbackImpl performanceCallbackImpl = null;
    private Context context;
    private FaultDiagnosis faultDiagnosis;
    private Handler callbackHandler;
    private Handler uiHandler;

    public PerformanceDemo(Context context, Handler callbackHandler, Handler uiHandler) {
        this.context = context;
        this.faultDiagnosis = FaultDiagnosis.getInstance(context);
        this.callbackHandler = callbackHandler;
        this.uiHandler = uiHandler;
    }

    public void subscribe() {
        try {
            if (performanceCallbackImpl != null) {
                faultDiagnosis.unSubscribePerformance(performanceCallbackImpl);
            }
            Performance performance = new Performance.Builder()
                                          .withKind(Performance.Kind.ANIMATION_JANK_FRAME)
                                          .withKind(Performance.Kind.FLING_JANK_FRAME)
                                          .withKind(Performance.Kind.APP_SKIP_FRAME)
                                          .withKind(Performance.Kind.APP_STARTUP_SLOW)
                                          .withKind(Performance.Kind.ACTIVITY_START_TIMEOUT)
                                          .build();
            performanceCallbackImpl = new PerformanceCallbackImpl();
            faultDiagnosis.subscribePerformance(performance, performanceCallbackImpl, callbackHandler);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe() {
        try {
            faultDiagnosis.unSubscribePerformance(performanceCallbackImpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PerformanceCallbackImpl implements PerformanceCallback {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onPerformanceReported(PerformancePayload performancePayload) {
            List<AppStartupSlowMetric> bootTooSlowMetrics = performancePayload.getAppStartupSlowMetrics();
            if (bootTooSlowMetrics == null) {
                return;
            }
            for (AppStartupSlowMetric appStartupSlowMetric : bootTooSlowMetrics) {
                Log.d(TAG, DEBUG_CLIENT + appStartupSlowMetric.toString());
            }

            List<AnimationJankFrameMetric> animationJankFrames = performancePayload.getAnimationJankFrameMetrics();
            if (animationJankFrames == null) {
                return;
            }
            for (AnimationJankFrameMetric animationJankFrameMetric : animationJankFrames) {
                Log.d(TAG, DEBUG_CLIENT + animationJankFrameMetric.toString());
                long happenTime = animationJankFrameMetric.getHappenTime();
                String happenTimeStr = getTime(happenTime * 1000);
                String operationType = animationJankFrameMetric.getOperationType().toString();
                String optSceneType = animationJankFrameMetric.getOptSceneType().toString();
                String appVersion = animationJankFrameMetric.getAppVersion();
                String diagInfo = animationJankFrameMetric.getDiagInfo();
                String processName = animationJankFrameMetric.getProcessName();
                int pid = animationJankFrameMetric.getPid();
                StringBuffer sb = new StringBuffer();
                sb.append("AnimationJankFrame"
                          + "\n")
                    .append("发生时间: "
                            + "\n" + happenTimeStr + "\n")
                    .append("进程号: "
                            + "\n" + pid + "\n")
                    .append("进程名: "
                            + "\n" + processName + "\n")
                    .append("应用版本: "
                            + "\n" + appVersion + "\n")
                    .append("操作类型: "
                            + "\n" + operationType + "\n")
                    .append("操作场景: "
                            + "\n" + optSceneType + "\n")
                    .append("诊断信息"
                            + "\n" + diagInfo + "\n");
                try {
                    uiHandler.obtainMessage(UPDATE_ANIMATION_JANK_UI, sb.toString()).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<FlingJankFrameMetric> flingJankFrameMetrics = performancePayload.getFlingJankFrameMetrics();
            if (flingJankFrameMetrics == null) {
                return;
            }
            for (FlingJankFrameMetric flingJankFrameMetric : flingJankFrameMetrics) {
                Log.d(TAG, DEBUG_CLIENT + flingJankFrameMetric.toString());
                String diagInfo = flingJankFrameMetric.getDiagInfo();
                String fetchLog = fetchLog(context, diagInfo);
                long happenTime = flingJankFrameMetric.getHappenTime();
                String happenTimeStr = getTime(happenTime * 1000);
                String operationType = flingJankFrameMetric.getOperationType().toString();
                String optSceneType = flingJankFrameMetric.getOptSceneType().toString();
                String processName = flingJankFrameMetric.getProcessName();
                int pid = flingJankFrameMetric.getPid();
                String appVersion = flingJankFrameMetric.getAppVersion();
                StringBuffer sb = new StringBuffer();
                sb.append("Fling_jank_frame"
                          + "\n")
                    .append("发生时间: "
                            + "\n" + happenTimeStr + "\n")
                    .append("进程号: "
                            + "\n" + pid + "\n")
                    .append("进程名: "
                            + "\n" + processName + "\n")
                    .append("应用版本: "
                            + "\n" + appVersion + "\n")
                    .append("操作类型: "
                            + "\n" + operationType + "\n")
                    .append("操作场景: "
                            + "\n" + optSceneType + "\n")
                    .append("诊断信息"
                            + "\n" + diagInfo + "\n")
                    .append("日志目录: "
                            + "\n" + fetchLog + "\n");
                try {
                    uiHandler.obtainMessage(UPDTAE_LIST_SWIPELAG_UI, sb.toString()).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<AppSkipFrameMetric> appSkipFrameMetrics = performancePayload.getAppSkipFrameMetrics();
            if (appSkipFrameMetrics == null) {
                return;
            }
            for (AppSkipFrameMetric appSkipFrameMetric : appSkipFrameMetrics) {
                Log.d(TAG, DEBUG_CLIENT + appSkipFrameMetric.toString());
                long happenTime = appSkipFrameMetric.getHappenTime();
                String happenTimeStr = getTime(happenTime * 1000);
                String operationType = appSkipFrameMetric.getOperationType().toString();
                String optSceneType = appSkipFrameMetric.getOptSceneType().toString();
                int pid = appSkipFrameMetric.getPid();
                String processName = appSkipFrameMetric.getProcessName();
                String appVersion = appSkipFrameMetric.getAppVersion();
                String diagInfo = appSkipFrameMetric.getDiagInfo();
                StringBuffer sb = new StringBuffer();
                sb.append("AppSkipFrame"
                          + "\n")
                    .append("发生时间: "
                            + "\n" + happenTimeStr + "\n")
                    .append("进程号: "
                            + "\n" + pid + "\n")
                    .append("进程名: "
                            + "\n" + processName + "\n")
                    .append("应用版本: "
                            + "\n" + appVersion + "\n")
                    .append("操作类型: "
                            + "\n" + operationType + "\n")
                    .append("操作场景: "
                            + "\n" + optSceneType + "\n")
                    .append("诊断信息"
                            + "\n" + diagInfo + "\n");
                try {
                    uiHandler.obtainMessage(UPDATE_APP_SKIP_UI, sb.toString()).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<ActivityStartTimeoutMetric> appStartupSlowMetrics =
                performancePayload.getActivityStartTimeoutMetrics();
            if (appStartupSlowMetrics == null) {
                return;
            }
            StringBuffer faultInfo = new StringBuffer();
            for (ActivityStartTimeoutMetric appStartupSlowMetric : appStartupSlowMetrics) {
                Log.d(TAG, DEBUG_CLIENT + appStartupSlowMetric.toString());
                String diagInfo = appStartupSlowMetric.getDiagInfo();
                String fetchLog = fetchLog(context, diagInfo);
                long happenTime = appStartupSlowMetric.getHappenTime();
                String happenTimeStr = getTime(happenTime * 1000);
                String operationType = appStartupSlowMetric.getOperationType().toString();
                String optSceneType = appStartupSlowMetric.getOptSceneType().toString();
                String processName = appStartupSlowMetric.getProcessName();
                int pid = appStartupSlowMetric.getPid();
                String appVersion = appStartupSlowMetric.getAppVersion();
                faultInfo
                    .append("Activity_start_timeout"
                            + "\n")
                    .append("发生时间: "
                            + "\n" + happenTimeStr + "\n")
                    .append("进程号: "
                            + "\n" + pid + "\n")
                    .append("进程名: "
                            + "\n" + processName + "\n")
                    .append("应用版本: "
                            + "\n" + appVersion + "\n")
                    .append("操作类型: "
                            + "\n" + operationType + "\n")
                    .append("操作场景: "
                            + "\n" + optSceneType + "\n")
                    .append("诊断信息"
                            + "\n" + diagInfo + "\n")
                    .append("日志目录: "
                            + "\n" + fetchLog + "\n");
                try {
                    uiHandler.obtainMessage(UPDATE_SLOW_FIRING, faultInfo.toString()).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
