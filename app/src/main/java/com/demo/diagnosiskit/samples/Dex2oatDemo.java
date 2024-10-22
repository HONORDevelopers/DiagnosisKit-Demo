package com.demo.DiagnosisKit.samples;

import static com.demo.DiagnosisKit.Utils.DEBUG_CLIENT;
import static com.demo.DiagnosisKit.activity.Dex2oatActivity.UiHandler.UPDATE_DEX2OAT_UI;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hihonor.mcs.system.diagnosis.core.status.DexoptStatus;
import com.hihonor.mcs.system.diagnosis.manager.AppStatusDiagnosis;

import java.util.List;

/* Dex2oat 查询能力使用示例 */
public class Dex2oatDemo {
    private static final String TAG = "Dex2oatDemo";
    private AppStatusDiagnosis mAppStatusDiagnosis;
    private Context context;
    private Handler uiHandler;
    private static Dex2oatDemo dex2oatDemo = null;

    public Dex2oatDemo(Context context, Handler uiHandler) {
        this.mAppStatusDiagnosis = AppStatusDiagnosis.getInstance(context);
        this.uiHandler = uiHandler;
        this.context = context;
    }

    public static Dex2oatDemo getInstance(Context context, Handler uiHandler) {
        synchronized (Dex2oatDemo.class) {
            if (dex2oatDemo == null) {
                dex2oatDemo = new Dex2oatDemo(context, uiHandler);
            }
            return dex2oatDemo;
        }
    }

    public void query(Activity activity) {
        try {
            List<DexoptStatus> dexoptStatusList = mAppStatusDiagnosis.queryDexoptState();
            if (dexoptStatusList == null) {
                activity.runOnUiThread(() -> { Toast.makeText(context, "查询异常！", Toast.LENGTH_SHORT).show(); });
            }
            if (dexoptStatusList != null) {
                if (dexoptStatusList.size() == 0) {
                    activity.runOnUiThread(
                        () -> { Toast.makeText(context, "查询时间间隔不小于5分钟", Toast.LENGTH_SHORT).show(); });
                }
            }
            dexoptStatusList.forEach(dexopt -> {
                Log.d(TAG,
                      DEBUG_CLIENT + ",dexoptStatus:  PathName->:" + dexopt.getPathName()
                          + ",Status->:" + dexopt.getStatus() + ",Reason->:" + dexopt.getReason() + ",ArtFileSize->:"
                          + dexopt.getArtFileSize() + ",OdexFileSize->:" + dexopt.getOdexFileSize() + ",VdexFileSize->:"
                          + dexopt.getVdexFileSize() + ",instructionSet->:" + dexopt.getInstructionSet());
                uiHandler.obtainMessage(UPDATE_DEX2OAT_UI, dexoptStatusList).sendToTarget();
            });
        } catch (RuntimeException e) {
            Log.e(TAG, DEBUG_CLIENT + "query dex2oat, exception:" + e.getMessage());
        }
    }
}
