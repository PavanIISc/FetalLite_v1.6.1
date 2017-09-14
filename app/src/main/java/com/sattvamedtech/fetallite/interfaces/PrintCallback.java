package com.sattvamedtech.fetallite.interfaces;

import java.util.ArrayList;

public class PrintCallback {

    private static PrintCallback mInstance;
    private ArrayList<PrintInterface> mPrintInterfaces = new ArrayList<>();

    public static PrintCallback getInstance() {
        if (mInstance == null)
            mInstance = new PrintCallback();
        return mInstance;
    }

    public void addPlotInterface(PrintInterface iPrintInterface) {
        mPrintInterfaces.add(iPrintInterface);
    }

    public void removePlotInterface(PrintInterface iPrintInterface) {
        if (mPrintInterfaces.contains(iPrintInterface))
            mPrintInterfaces.remove(iPrintInterface);
    }

    public void savePrintData(String iPrintData) {
        if (mPrintInterfaces != null && mPrintInterfaces.size() > 0) {
            for (PrintInterface plotInterface : mPrintInterfaces) {
                plotInterface.savePrintData(iPrintData);
            }
        }
    }
}
