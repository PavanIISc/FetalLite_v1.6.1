package com.sattvamedtech.fetallite.process;

import android.os.AsyncTask;
import android.widget.Toast;

import com.sattvamedtech.fetallite.FLApplication;
import com.sattvamedtech.fetallite.helper.ApplicationUtils;
import com.sattvamedtech.fetallite.helper.FileLogger;
import com.sattvamedtech.fetallite.helper.Logger;
import com.sattvamedtech.fetallite.interfaces.PlotCallback;
import com.sattvamedtech.fetallite.signalproc.Constants;


public class PlottingTaskSimplified extends AsyncTask<Void, Void, Void> {

    private int[] mQrsLocations;
    private int[] mHeartRate;
    private double[] mUcData;
    private boolean isPlotHr;
    private boolean isFetal;

    public PlottingTaskSimplified(int[] iQrsLocations, int[] iHeartRate, boolean isFetal) {
        this.isFetal = isFetal;
        mQrsLocations = iQrsLocations;
        mHeartRate = iHeartRate;
        isPlotHr = true;
    }

    public PlottingTaskSimplified(double[] iUcData) {
        mUcData = iUcData;
        isPlotHr = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (isPlotHr)
            plotData();
        else
            plotUcData();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (isPlotHr)
            ApplicationUtils.mHrPlottingFlag = ApplicationUtils.IDLE;
        else
            ApplicationUtils.mUcPlottingFlag = ApplicationUtils.IDLE;
    }

    private void plotData() {
        if (isFetal) {
//            Logger.logInfo("PlottingTaskSimplified", "Iteration " + ApplicationUtils.algoProcessEndCount);
            fetalPlot();
        } else {
            maternalPlot();
        }

        Logger.logInfo("PlottingTaskSimplified", "Plotting started for FQrs & MQrs");

        Logger.logInfo("PlottingTaskSimplified", "Plotting for 15k done at " + (System.currentTimeMillis() - ApplicationUtils.mStartMS));
    }

    private void plotUcData() {
//        sleep(10000 / 3);
//
//        ApplicationUtils.lastUcPlotXValue += com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_UC;
//        PlotCallback.getInstance().sendUcData(ApplicationUtils.lastUcPlotXValue, (float) mUcData[0] * (float) Math.pow(10, 8));
//
//        sleep(10000 / 3);
//
//        ApplicationUtils.lastUcPlotXValue += com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_UC;
//        PlotCallback.getInstance().sendUcData(ApplicationUtils.lastUcPlotXValue, (float) mUcData[1] * (float) Math.pow(10, 8));
//
//        sleep(10000 / 3);

        for (int i =0; i<mUcData.length; i++){
            if (ApplicationUtils.lastUcPlotXValue == 0)
                ApplicationUtils.lastUcPlotXValue += com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_FIRST_ITERATION;
            else
                ApplicationUtils.lastUcPlotXValue += Constants.DIFFERENCE_SAMPLES;
            if (ApplicationUtils.algoProcessEndCount == 0 && i == 0)
                sleep(com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_FIRST_ITERATION);
            else
                sleep(Constants.DIFFERENCE_SAMPLES);

            try {
                PlotCallback.getInstance().sendUcData(ApplicationUtils.lastUcPlotXValue, (float) mUcData[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }

       }
    }

    private void fetalPlot() {
        Logger.logInfo("PlottingTaskSimplified", "fetalPlot");
        Logger.logInfo("PlottingTaskSimplified", "fetalPlot ApplicationUtils.lastFetalPlotIndex: " + ApplicationUtils.lastFetalPlotIndex);
        Logger.logInfo("PlottingTaskSimplified", "fetalPlot Constants.QRS_FETAL_LOCATION.size(): " + Constants.QRS_FETAL_LOCATION.size());
        for (int i = 0; i < mQrsLocations.length; i++) {
            if (ApplicationUtils.lastFetalPlotXValue == 0)
                ApplicationUtils.lastFetalPlotXValue += com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_FIRST_ITERATION;
            else
                ApplicationUtils.lastFetalPlotXValue += Constants.DIFFERENCE_SAMPLES;
            if (ApplicationUtils.algoProcessEndCount == 0 && i == 0)
                sleep(com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_FIRST_ITERATION);
            else
                sleep(Constants.DIFFERENCE_SAMPLES);
            FileLogger.logData(ApplicationUtils.getCurrentTime()+" : PlottingTaskSimplified : Fetal HR : "+mHeartRate[i], "PlottingLogs", FLApplication.mFileTimeStamp);

//            Logger.logInfo("PlottingTaskSimplified", "fetalPlot HR: " + mHeartRate[i]);
            try {
                PlotCallback.getInstance().sendFetalHeartRateData(ApplicationUtils.lastFetalPlotXValue, mHeartRate[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ApplicationUtils.lastFetalPlotIndex++;
        }
    }

    private void maternalPlot() {
        Logger.logInfo("PlottingTaskSimplified", "maternalPlot");
        for (int i = 0; i < mQrsLocations.length; i++) {
            if (ApplicationUtils.lastMaternalPlotXValue == 0)
                ApplicationUtils.lastMaternalPlotXValue += com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_FIRST_ITERATION;
            else
                ApplicationUtils.lastMaternalPlotXValue += Constants.DIFFERENCE_SAMPLES;
            if (ApplicationUtils.algoProcessEndCount == 0 && i == 0)
                sleep(com.sattvamedtech.fetallite.helper.Constants.PLOTTING_DIFFERENCE_FIRST_ITERATION);
            else
                sleep(Constants.DIFFERENCE_SAMPLES);
            FileLogger.logData(ApplicationUtils.getCurrentTime()+" : PlottingTaskSimplified : maternal HR : "+mHeartRate[i], "PlottingLogs", FLApplication.mFileTimeStamp);
            Logger.logInfo("PlottingTaskSimplified", "maternalPlot HR: " + mHeartRate[i]);
            try {
                PlotCallback.getInstance().sendMaternalHeartRateData(ApplicationUtils.lastMaternalPlotXValue, mHeartRate[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ApplicationUtils.lastMaternalPlotIndex++;
        }
    }

    private static void sleep(int iDurationInMilliseconds) {
        FileLogger.logData(ApplicationUtils.getCurrentTime()+" : PlottingTaskSimplified : Sleep for : "+iDurationInMilliseconds, "PlottingLogs", FLApplication.mFileTimeStamp);
        Logger.logInfo("PlottingTaskSimplified", "going to sleep for " + iDurationInMilliseconds);
        try {
            Thread.sleep(iDurationInMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
