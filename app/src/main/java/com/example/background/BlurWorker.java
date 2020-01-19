package com.example.background;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
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
        Context applicationContext = getApplicationContext();

        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);

//        Bitmap picture = BitmapFactory.decodeResource(applicationContext.getResources(),
//                R.drawable.test);
        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }
            ContentResolver contentResolver = applicationContext.getContentResolver();
            Bitmap picture = BitmapFactory
                    .decodeStream(contentResolver
                            .openInputStream(Uri.parse(resourceUri)));
            //blur the bitmap
            Bitmap output = WorkerUtils.blurBitmap(picture, applicationContext);

            //write bitmap to temp file

            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext, output);
            WorkerUtils.makeStatusNotification("Output is " + outputUri.toString(),
                    applicationContext);
            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, outputUri.toString())
                    .build();
            return Result.success(outputData);


        } catch (Throwable throwable) {
            Log.e(TAG, "Error applying blur!");
            return Result.failure();
        }


    }
}
