package com.sattvamedtech.fetallite.miniTestModule;

import android.os.Environment;
import android.text.TextUtils;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.ExceptionHandling;
import com.sattvamedtech.fetallite.helper.FileLogger;
import com.sattvamedtech.fetallite.helper.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class InitialDataCheck {
    double samplingFrequency = 1000;
    private double mVref = 4.5;
    private double mCheck = Math.pow(2, 23);
    private double mCheckDivide = 2 * mCheck;
    private double mGain = 24;
    private int percentNoisy = 40;
    double[][] input = new double[DefaultVariableMaster.no_of_Samples][DefaultVariableMaster.no_of_Channels];
    private Queue<String> mSampleQueue = new LinkedList<>();
    private long mTotalTime, mStartTime = 0;
    String error_msg = "Please check eletrode contacts and/or sensor unit orientation and position before starting a new test";

    public boolean isValid(List<String> iInput, String iFileDateStamp)
    {
        mStartTime = System.currentTimeMillis();
        mSampleQueue.addAll(iInput);
        populateInputArray();

//        for (int i = 0; i < input.length; i++) {
//            FileLogger.logData(input[i][0] + "," + input[i][1] + "," + input[i][2] + "," + input[i][3], "mini", iFileDateStamp);
//        }

        /**
         * Check for impulses and flatSignal in raw data
         */
        double[][] derivativeSignal = new double[DefaultVariableMaster.no_of_Samples - 1][DefaultVariableMaster.no_of_Channels];

        double impulseThreshold = 10 * Math.pow(10, -3);
        double flatThreshold = 1 * Math.pow(10, -15); // can change to 12.

        int impulseCounter[] = new int[DefaultVariableMaster.no_of_Channels];
        int flatCounter[] = new int[DefaultVariableMaster.no_of_Channels];

        for (int i = 1; i < DefaultVariableMaster.no_of_Samples; i++) {
            derivativeSignal[i - 1][0] = Math.abs(input[i][0] - input[i - 1][0]) * samplingFrequency;
            derivativeSignal[i - 1][1] = Math.abs(input[i][1] - input[i - 1][1]) * samplingFrequency;
            derivativeSignal[i - 1][2] = Math.abs(input[i][2] - input[i - 1][2]) * samplingFrequency;
            derivativeSignal[i - 1][3] = Math.abs(input[i][3] - input[i - 1][3]) * samplingFrequency;

            if (derivativeSignal[i - 1][0] > impulseThreshold) {
                impulseCounter[0] = impulseCounter[0] + 1;
            }
            if (derivativeSignal[i - 1][1] > impulseThreshold) {
                impulseCounter[1] = impulseCounter[1] + 1;
            }
            if (derivativeSignal[i - 1][2] > impulseThreshold) {
                impulseCounter[2] = impulseCounter[2] + 1;
            }
            if (derivativeSignal[i - 1][3] > impulseThreshold) {
                impulseCounter[3] = impulseCounter[3] + 1;
            }

            if (derivativeSignal[i - 1][0] < flatThreshold) {
                flatCounter[0] = flatCounter[0] + 1;
            }
            if (derivativeSignal[i - 1][1] < flatThreshold) {
                flatCounter[1] = flatCounter[1] + 1;
            }
            if (derivativeSignal[i - 1][2] < flatThreshold) {
                flatCounter[2] = flatCounter[2] + 1;
            }
            if (derivativeSignal[i - 1][3] < flatThreshold) {
                flatCounter[3] = flatCounter[3] + 1;
            }


        }

        Logger.logInfo("InitialDataCheck", "impulseCounter: " + impulseCounter[0] + "," + impulseCounter[1] + "," + impulseCounter[2] + "," + impulseCounter[3]);
        Logger.logInfo("InitialDataCheck", "flatCounter: " + flatCounter[0] + "," + flatCounter[1] + "," + flatCounter[2] + "," + flatCounter[3]);

        int percentImpluse = 15;
        int percentFlat = 20;
        for (int i = 0; i < DefaultVariableMaster.no_of_Channels; i++) {
            if (flatCounter[i] / DefaultVariableMaster.no_of_Samples * 100 > percentFlat) {
                Logger.logDebug("Flat Signal in Channel", i + "");
                if (ExceptionHandling.getInstance().getExceptionListener() != null) {
                    ExceptionHandling.getInstance().getExceptionListener().onException(new Exception(error_msg));
                }
                return false;
            }
            if (impulseCounter[i] / DefaultVariableMaster.no_of_Samples * 100 > percentImpluse) {
                Logger.logDebug("Impulse Signal in Channel", i + "");
                if (ExceptionHandling.getInstance().getExceptionListener() != null) {
                    ExceptionHandling.getInstance().getExceptionListener().onException(new Exception(error_msg));
                }
                return false;
            }
        }

        /**
         * Check for floor noise levels
         * 1. Detect QRS Mother peaks on filtered data
         * 2. Check for 3- 20ms patchs between 2 maternal QRS locations
         */

        filterLoHiNotch filt = new filterLoHiNotch();
        double[][] Ecg_filt = filt.filterParallel(input);


        mQRSDetection mqrsDet = new mQRSDetection();
        int[] qrsM = mqrsDet.mQRS(Ecg_filt);

        if (qrsM.length < 2) {
            Logger.logDebug("No Maternal Detected", "");
            if (ExceptionHandling.getInstance().getExceptionListener() != null) {
                ExceptionHandling.getInstance().getExceptionListener().onException(new Exception(error_msg));
            }
            return false;
        }

        int noPatches = (qrsM.length - 1) * 3;

        int patchLoc[] = new int[noPatches];
        int patchSize = 20;
        int factor = 4;
        int count = -1;
        for (int i = 0; i < qrsM.length - 1; i++) {
            for (int j = (factor - 1); j > 0; j--) {
                patchLoc[count + j] = qrsM[i] + (qrsM[i + 1] - qrsM[i]) / factor * j;
            }
            count = count + (factor - 1);
        }

        int signChangeCounter[][] = new int[noPatches][DefaultVariableMaster.no_of_Channels];
        double[][] signChangeMaxValue = new double[noPatches][DefaultVariableMaster.no_of_Channels];
        double[][] absoluteDiff = new double[noPatches][DefaultVariableMaster.no_of_Channels];

        double[] maxValue = new double[noPatches];
        double[] minValue = new double[noPatches];

        double[] maxValuesSignChange = new double[noPatches];
        double[] minValuesSignChange = new double[noPatches];

        double noiseThreshold = 10 * Math.pow(10, -6);

        int noOfNoise[] = new int[DefaultVariableMaster.no_of_Channels];
        int noOfNoiseSign[] = new int[DefaultVariableMaster.no_of_Channels];

        double maxVal, diff;
        int ind, signFlag, countSignChangePN, countSignChangeNP, countMaxDiff, countMaxSignDiff;
        for (int k = 0; k < DefaultVariableMaster.no_of_Channels; k++) {
            countMaxDiff = 0;
            countMaxSignDiff = 0;
            for (int i = 0; i < noPatches; i++) {
                maxValue[i] = -100;
                minValue[i] = 100;
                maxValuesSignChange[i] = 0;
                minValuesSignChange[i] = 0;
            }
            maxVal = -1000;
            for (int i = 0; i < noPatches; i++) {
                ind = patchLoc[i];
                for (int j = 0; j < patchSize; j++) {
                    if (Ecg_filt[ind + j][k] > maxValue[i]) {
                        maxValue[i] = Ecg_filt[ind + j][k];
                    }

                    if (Ecg_filt[ind + j][k] < minValue[i]) {
                        minValue[i] = Ecg_filt[ind + j][k];
                    }
                }
                signFlag = 0;
                countSignChangePN = 0;
                countSignChangeNP = 0;
                if (Ecg_filt[ind + 1][k] - Ecg_filt[ind][k] < 0) {
                    signFlag = -1;
                } else {
                    signFlag = 1;
                }

                for (int j = 2; j < patchSize; j++) {
                    if (Ecg_filt[ind + j][k] - Ecg_filt[ind + j - 1][k] < 0) {
                        if (signFlag == 1) {
                            signFlag = -1;
                            countSignChangePN = countSignChangePN + 1;
                            maxValuesSignChange[countSignChangePN] = Ecg_filt[ind + j - 1][k];
                        } else {
                            if (signFlag == -1) {
                                signFlag = 1;
                                countSignChangeNP = countSignChangeNP + 1;
                                minValuesSignChange[countSignChangeNP] = Ecg_filt[ind + j - 1][k];
                            }
                        }
                    }
                }
                if (countSignChangeNP > countSignChangePN) {
                    signChangeCounter[i][k] = countSignChangePN;
                    for (int j = 0; j < countSignChangePN; j++) {
                        diff = Math.abs(minValuesSignChange[j] - maxValuesSignChange[j]);
                        if (diff > maxVal) {
                            maxVal = diff;
                        }
                    }
                } else {
                    signChangeCounter[i][k] = countSignChangeNP;
                    for (int j = 0; j < countSignChangeNP; j++) {
                        diff = Math.abs(minValuesSignChange[j] - maxValuesSignChange[j]);
                        if (diff > maxVal) {
                            maxVal = diff;
                        }
                    }
                }

                signChangeMaxValue[i][k] = maxVal;
                absoluteDiff[i][k] = Math.abs(maxValue[i] - minValue[i]);
                if (absoluteDiff[i][k] > noiseThreshold) {
                    countMaxDiff = countMaxDiff + 1;
                }
                if (signChangeMaxValue[i][k] > noiseThreshold && signChangeCounter[i][k] > 1) {
                    countMaxSignDiff = countMaxSignDiff + 1;
                }
            }


            noOfNoise[k] = countMaxDiff;
            noOfNoiseSign[k] = countMaxSignDiff;
            Logger.logInfo("InitialDataCheck", "countMaxSignDiff: " + countMaxSignDiff);
            if (countMaxSignDiff > percentNoisy / 100 * noPatches) {
                Logger.logDebug("Channel- ", +k + " is noisy!!!");
                mTotalTime = mStartTime - System.currentTimeMillis();
                return false;
            }

            mTotalTime = System.currentTimeMillis() - mStartTime;
            Logger.logDebug("MiniTest", mTotalTime + "");
        }
        return true;
    }

    private void populateInputArray() {
        int aInputArrayCounter = 0;
        Logger.logDebug("InitialDataCheck", "populate started");
        String aSample = getNextValidSample();
        Logger.logDebug("InitialDataCheck", "mTempTest: " + aSample);
        if (!TextUtils.isEmpty(aSample)) {
            int aLastIndex = Character.getNumericValue(aSample.charAt(0));
            Logger.logDebug("InitialDataCheck", "inside if");
            feedInputArray(aSample, aInputArrayCounter);
            Logger.logDebug("InitialDataCheck", "starting for");
            for (aInputArrayCounter++; aInputArrayCounter < DefaultVariableMaster.no_of_Samples; aInputArrayCounter++) {
                aSample = getNextValidSample();
                if (!TextUtils.isEmpty(aSample)) {
                    int aCurrentIndex = Character.getNumericValue(aSample.charAt(0));
                    int aIndexDiff = aCurrentIndex - aLastIndex;
                    if (aIndexDiff <= 0)
                        aIndexDiff += 10;
                    if (aIndexDiff == 1 || aIndexDiff == -9) {
                        feedInputArray(aSample, aInputArrayCounter);
                    } else {
                        aInputArrayCounter += aIndexDiff - 1;
                        feedInputArray(aSample, aInputArrayCounter);
                        interpolate(aInputArrayCounter, aInputArrayCounter - aIndexDiff);
                    }
                    aLastIndex = Character.getNumericValue(aSample.charAt(0));
                } else {
                    Logger.logDebug("InitialDataCheck", "empty sample inside for");
                }
            }
        } else
            Logger.logDebug("InitialDataCheck", "empty sample");
    }

    private String getNextValidSample() {
        String aValidSample = "";
        if (mSampleQueue.size() > 0) {
            do { // find next valid sample
                aValidSample = mSampleQueue.remove();
            } while (aValidSample.length() != 25 && mSampleQueue.size() > 0);
        }
        return aValidSample;
    }

    private void interpolate(int iCurrentInputIndex, int iStartIndex) {
        Logger.logInfo("DataSocketIntentService", "Interpolating...");
        Logger.logInfo("DataSocketIntentService", "current_input_index: " + iCurrentInputIndex);
        Logger.logInfo("DataSocketIntentService", "mStartIndex: " + iStartIndex);
        for (int k = iStartIndex + 1; k < iCurrentInputIndex; k++) {
            for (int aInputChannelIndex = 0; aInputChannelIndex < 4; aInputChannelIndex++) {
                ApplicationUtils.mInputArray[k][aInputChannelIndex] = ApplicationUtils.mInputArray[iStartIndex][aInputChannelIndex] + (ApplicationUtils.mInputArray[iCurrentInputIndex][aInputChannelIndex] - ApplicationUtils.mInputArray[iStartIndex][aInputChannelIndex]) / (iCurrentInputIndex - iStartIndex) * (k - iStartIndex); // crashed here on 2nd time
            }
        }
    }


    private void feedInputArray(String iInputString, int iInputIndex) {
        for (int aInputChannelIndex = 0; aInputChannelIndex < 4; aInputChannelIndex++) {
            ApplicationUtils.mInputArray[iInputIndex][aInputChannelIndex] = stringToDouble(iInputString.substring(6 * aInputChannelIndex + 1, 6 * aInputChannelIndex + 7), iInputIndex, aInputChannelIndex);
            if (aInputChannelIndex == 0) {
                ApplicationUtils.mInputArrayUc[iInputIndex] = ApplicationUtils.mInputArray[iInputIndex][aInputChannelIndex];
            }
        }
    }

    private double doubleConvDouble(double iInput, int iArrayCount, int iChannelCount) {
        double aOut;
        if (iInput >= mCheck) {
            aOut = (iInput - mCheckDivide) * mVref / (mCheck - 1) / mGain;
            input[iArrayCount][iChannelCount] = (iInput - mCheckDivide) * mVref / (mCheck - 1) / mGain;
        } else {
            aOut = iInput / (mCheck - 1) / mGain * mVref;
            input[iArrayCount][iChannelCount] = iInput / (mCheck - 1) / mGain * mVref;
        }
        return aOut;
    }

    private double stringToDouble(String iInput, int iArrayCount, int iChannelCount) {
        return doubleConvDouble(new BigInteger(iInput, 16).doubleValue(), iArrayCount, iChannelCount);
    }

    public void logDataOld(String input, String iFileName) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva");
            File ActivityLog = new File(Environment.getExternalStorageDirectory().getPath() + "/sattva", "mini-" + FLApplication.mPatientId + iFileName + ".txt");

            if (root.isDirectory()) {
                // Logger.logDebug("root exists","root exists");
                if (ActivityLog.exists()) {

                    FileWriter outFile = new FileWriter(ActivityLog, true);
                    // Logger.logDebug("writing","writing " +
                    // ActivityLog.getAbsolutePath());
                    PrintWriter out = new PrintWriter(outFile);
                    out.print(input);
                    out.flush();
                    out.close();
                } else {
                    // Logger.logDebug("writing2","writing2");
                    if (ActivityLog.createNewFile()) {
                        FileWriter outFile = new FileWriter(ActivityLog, true);
                        PrintWriter out = new PrintWriter(outFile);
                        out.print(input);
                        out.flush();
                        out.close();
                    }
                }
            } else {
                if (root.mkdir()) {
                    if (ActivityLog.createNewFile()) {

                        FileWriter outFile = new FileWriter(ActivityLog, true);

                        PrintWriter out = new PrintWriter(outFile);
                        out.println(input);
                        out.flush();
                        // Logger.logDebug("writing3","writing3");
                        out.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
