package com.example.background;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;

public class CleanUpWorker extends Worker {

    private static String TAG = CleanUpWorker.class.getSimpleName();

    public CleanUpWorker(@NonNull Context context,
                         @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        try{
            File outputDirectory = new File(applicationContext.getFilesDir(),
                    Constants.OUTPUT_PATH);
            if (outputDirectory.exists()){
                File [] entries = outputDirectory.listFiles();
                if (entries != null){
                    for (File entry:entries) {
                        String name = entry.getName();
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")){
                            boolean deleted = entry.delete();
                            Log.i(TAG,String.format("Deleted %s - %s",
                                    name, deleted));

                        }
                    }
                }
            }
            return Worker.Result.success();

        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error cleaning up", e);
            return Worker.Result.failure();
        }
    }
}
