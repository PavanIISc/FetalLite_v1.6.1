package com.sattvamedtech.fetallite.interfaces;

import java.util.ArrayList;

public class PlotCallback {

    private static PlotCallback mInstance;
    private ArrayList<PlotInterface> mPlotInterfaces = new ArrayList<>();

    public static PlotCallback getInstance() {
        if (mInstance == null)
            mInstance = new PlotCallback();
        return mInstance;
    }

    public void addPlotInterface(PlotInterface iPlotInterface) {
        mPlotInterfaces.add(iPlotInterface);
    }

    public void removePlotInterface(PlotInterface iPlotInterface) {
        if (mPlotInterfaces.contains(iPlotInterface))
            mPlotInterfaces.remove(iPlotInterface);
    }

    public void sendFetalHeartRateData(float x, float y) {
        if (mPlotInterfaces != null && mPlotInterfaces.size() > 0) {
            for (PlotInterface plotInterface : mPlotInterfaces) {
                plotInterface.plotFetalHeartRate(x, y);
            }
        }
    }

    public void sendMaternalHeartRateData(float x, float y) {
        if (mPlotInterfaces != null && mPlotInterfaces.size() > 0) {
            for (PlotInterface plotInterface : mPlotInterfaces) {
                plotInterface.plotMaternalHeartRate(x, y);
            }
        }
    }

    public void sendUcData(float x, float y) {
        if (mPlotInterfaces != null && mPlotInterfaces.size() > 0) {
            for (PlotInterface plotInterface : mPlotInterfaces) {
                plotInterface.plotUc(x, y);
            }
        }
    }
}
