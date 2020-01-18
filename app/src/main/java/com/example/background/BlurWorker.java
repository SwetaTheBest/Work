package com.example.background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.background.workers.WorkerUtils;

import java.io.FileNotFoundException;


public class BlurWorker extends Worker {
    public BlurWorker(@NonNull Context context,
                      @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private static final String TAG = BlurWorker.class.getSimpleName();
    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext  = getApplicationContext();
        Bitmap picture = BitmapFactory.decodeResource(applicationContext.getResources(),
                R.drawable.test);
        //blur the bitmap
        Bitmap output = WorkerUtils.blurBitmap(picture,applicationContext);

        //write bitmap to temp file
        try {
            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext,output);
            WorkerUtils.makeStatusNotification("Output is " + outputUri.toString(),
                    applicationContext);
            return Result.success();

        } catch (Throwable throwable) {
            Log.e(TAG,"Error applying blur!" );
            return Result.failure();
        }



    }
}
