package com.example.background;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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


        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
