package com.demo.DiagnosisKit;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class Utils {
    private static final String TAG = "Utils";
    public static final String DEBUG_CLIENT = "diagnosis debug | demo app client->";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String fetchLog(Context context, String diagInfo) {
        try {
            JSONObject jsonObject = new JSONObject(diagInfo);
            String uriString = jsonObject.getString("privateLogURI");
            Uri uri = Uri.parse(uriString);
            File pathFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(pathFile, uri.getLastPathSegment());
            Log.e(TAG, DEBUG_CLIENT + ", uri:" + uri + ",file:" + file);
            String scheme = uri.getScheme();
            if (!TextUtils.isEmpty(scheme)) {
                try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                    Files.copy(inputStream, Paths.get(file.toString()));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            return pathFile.getAbsolutePath().toString();
        } catch (JSONException | SecurityException | UncheckedIOException e) {
            Log.e(TAG, DEBUG_CLIENT + "fetchLog exception:" + e.getMessage());
        }
        return "";
    }

    public static String getTime(long timeStamp) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
        return dateFormatter.format(timeStamp);
    }

    public static void workTime(long mills) {
        final long currentTime = System.currentTimeMillis();
        int index = 0;
        while (System.currentTimeMillis() <= currentTime + mills) {
            index++;
            index--;
        }
    }
}
