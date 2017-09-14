package com.sattvamedtech.fetallite.helper;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class FileUtils {

    private static final int TWO_MONTHS = 60; // Two months approximately in days
    public static final long ONE_DAY = 86400000; // One day in milliseconds 1000 * 60 * 60 * 24

    public static ArrayList<String> findAllOldLogFiles() {
        ArrayList<String> aFileAbsolutePathList = new ArrayList<>();

        File aRoot = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva");
        long aCurrentTime = (new Date()).getTime();
        if (aRoot.exists()) {
            File[] aFiles = aRoot.listFiles();
            for (File aFile : aFiles) {
                int aFileAgeInDays = (int) ((aCurrentTime - aFile.lastModified()) / (ONE_DAY));
                if (aFile.isDirectory() && aFileAgeInDays > TWO_MONTHS)
                    aFileAbsolutePathList.add(aFile.getAbsolutePath());
            }
        }

        return aFileAbsolutePathList;
    }

    public static void deleteRecursive(File iFileOrDirectory) {
        if (iFileOrDirectory != null && iFileOrDirectory.exists()) {
            if (iFileOrDirectory.isDirectory())
                for (File child : iFileOrDirectory.listFiles())
                    deleteRecursive(child);

            iFileOrDirectory.delete();
        }
    }
}
