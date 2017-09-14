package com.sattvamedtech.fetallite.helper;

import android.os.Environment;

import com.sattvamedtech.fetallite.FLApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileLogger {

    public static void logData(String iData, String iLogTypePrefix, String iFileDateStamp) {
        try {
            File aRootDir = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva");
            File aTestDir = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + iFileDateStamp);
            //File aTestDir = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + FLApplication.mTestId + iFileDateStamp);
            File aActivityLog = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + iFileDateStamp, iLogTypePrefix + "-" + iFileDateStamp + ".txt");
            //File aActivityLog = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva" + File.separator + FLApplication.mTestId + iFileDateStamp, iLogTypePrefix + "-" + FLApplication.mTestId + iFileDateStamp + ".txt");


            if (!aRootDir.isDirectory()) {
                if (!aRootDir.mkdir()) {
                    return;
                }
            }
            if (!aTestDir.isDirectory()) {
                if (!aTestDir.mkdir()) {
                    return;
                }
            }
            if (!aActivityLog.exists()) {
                if (!aActivityLog.createNewFile()) {
                    return;
                }
            }

            FileWriter outFile = new FileWriter(aActivityLog, true);
            PrintWriter out = new PrintWriter(outFile);
            out.println(iData);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
