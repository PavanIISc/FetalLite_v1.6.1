package com.sattvamedtech.fetallite.job;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.sattvamedtech.fetallite.helper.CompressionHelper;
import com.sattvamedtech.fetallite.helper.FileUtils;
import com.sattvamedtech.fetallite.helper.Logger;

import java.io.File;
import java.util.ArrayList;

public class CompressionJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Logger.logInfo("CompressionJobService", "job started");
        ArrayList<String> aFileList = FileUtils.findAllOldLogFiles();
        for (String aFile : aFileList) {
            boolean aCompressed = CompressionHelper.zipFileAtPath(aFile, aFile + ".zip");
            if (aCompressed) {
                FileUtils.deleteRecursive(new File(aFile));
            }
        }
        Logger.logInfo("CompressionJobService", "job completed");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Logger.logInfo("CompressionJobService", "job stopped");
        return true;
    }
}
