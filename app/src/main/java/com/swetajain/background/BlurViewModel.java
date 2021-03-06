/*
Created by Sweta Jain on 25/01/2020
 */

package com.swetajain.background;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;

import static com.swetajain.background.Constants.IMAGE_MANIPULATION_WORK_NAME;
import static com.swetajain.background.Constants.KEY_IMAGE_URI;
import static com.swetajain.background.Constants.TAG_OUTPUT;

public class BlurViewModel extends AndroidViewModel {

    private Uri mImageUri;
    private WorkManager workManager;
    private LiveData<List<WorkInfo>> mSavedWorkInfo;


    public BlurViewModel(@NonNull Application application) {
        super(application);
        workManager = WorkManager.getInstance(application);
        mSavedWorkInfo = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT);
    }

    LiveData<List<WorkInfo>> getSavedWorkInfo() {
        return mSavedWorkInfo;
    }
    /**
     * Create the WorkRequest to apply the blur and save the resulting image
     * @param blurLevel The amount to blur the image
     */
    void applyBlur(int blurLevel) {

        Constraints constraints = new Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .setRequiresCharging(true)
                .build();

        //add Work request to cleanup temporary images
        WorkContinuation workContinuation =
                workManager.beginUniqueWork
                        (IMAGE_MANIPULATION_WORK_NAME,
                                ExistingWorkPolicy.REPLACE,
                                OneTimeWorkRequest.from(CleanUpWorker.class));

        //add work request to blur the image

        for (int i = 0 ; i < blurLevel; i++ ){
            OneTimeWorkRequest.Builder blurRequestBuilder =
                   new OneTimeWorkRequest.Builder(BlurWorker.class);
            if (i == 0){
                blurRequestBuilder.setInputData(createInputDataForUri());
            }
            workContinuation = workContinuation.then(blurRequestBuilder.build());
        }
//        OneTimeWorkRequest blurRequest =
//                new OneTimeWorkRequest.Builder(BlurWorker.class)
//                .setInputData(createInputDataForUri())
//                .build();
//        workContinuation = workContinuation.then(blurRequest);

        //add work request to save image to the file system
        OneTimeWorkRequest saveRequest =
                new OneTimeWorkRequest.Builder(SaveImageToFileWorker.class)
                        .setConstraints(constraints)
                        .addTag(TAG_OUTPUT)
                .build();
        workContinuation = workContinuation.then(saveRequest);

        //actually start the work
        workContinuation.enqueue();


    }

    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    /**
     * Setters
     */
    void setImageUri(String uri) {
        mImageUri = uriOrNull(uri);
    }

    /**
     * Getters
     */
    Uri getImageUri() {
        return mImageUri;
    }

    private Data createInputDataForUri(){
        Data.Builder dataBuilder = new Data.Builder();
        if(mImageUri != null){
            dataBuilder.putString(KEY_IMAGE_URI , mImageUri.toString());
        }
        return dataBuilder.build();
    }

    /*
      Cancel work using work's unique name
     */
    void cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME);
    }
}