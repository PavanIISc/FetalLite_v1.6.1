package com.sattvamedtech.fetallite.miniTestModule;//package hb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class filterLoHiNotch {
    static boolean i0Flag = false;
    static boolean i1Flag = false;
    static boolean i2Flag = false;
    static boolean i3Flag = false;
    static double fXc[][] = null;
    matrixFunctions Matrix = new matrixFunctions();

    public double[][] filterParallel(double[][] input) {

        final double[][] finput = input;

        final double[][] filter_output = new double[DefaultVariableMaster.no_of_Samples][DefaultVariableMaster.no_of_Channels];

        ExecutorService exec = Executors.newFixedThreadPool(DefaultVariableMaster.no_of_Channels);
        for (int cols = 0; cols < DefaultVariableMaster.no_of_Channels; cols++) {
            final int fcols = cols;
            final double[] channel = new double[DefaultVariableMaster.no_of_Samples];
            exec.submit(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < DefaultVariableMaster.no_of_Samples; i++) {
                        channel[i] = finput[i][fcols];
                    }
                    filterLowHiNotchParallel(channel);

                    for (int i = 0; i < DefaultVariableMaster.no_of_Samples; i++) {
                        filter_output[i][fcols] = channel[i];
                    }
                    if (fcols == 0)
                        i0Flag = true;
                    else if (fcols == 1)
                        i1Flag = true;
                    else if (fcols == 2)
                        i2Flag = true;
                    else if (fcols == 3)
                        i3Flag = true;
                }
            });
        }
        System.out.println("***Waiting for process Filter Calculation to finish : " + (new java.text.SimpleDateFormat("H:mm:ss:SSS")).format(java.util.Calendar.getInstance().getTime()));
        try {
            while (true) {
                Thread.sleep(10);
                if (i0Flag && i1Flag && i2Flag && i3Flag) {
                    i0Flag = false;
                    i1Flag = false;
                    i2Flag = false;
                    i3Flag = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exec.shutdown();
        }
        return filter_output;
    }

    private void filterLowHiNotchParallel(double[] channel) {


        double ahigh[] = new double[2];
        double bhigh[] = new double[2];
        for (int i0 = 0; i0 < 2; i0++) {
            bhigh[i0] = DefaultVariableMaster.filter_bhigh0 + DefaultVariableMaster.filter_bhigh_sum * (double) i0;
            ahigh[i0] = 1.0 + DefaultVariableMaster.filter_ahigh_sum * (double) i0;
        }

        Matrix.filterLoHi(channel, ahigh, bhigh, DefaultVariableMaster.filter_zhigh);

        Matrix.filterLoHi(channel, DefaultVariableMaster.filter_alow, DefaultVariableMaster.filter_blow, DefaultVariableMaster.filter_zlow);

        Matrix.filterNotch(channel, DefaultVariableMaster.filter_aNotch, DefaultVariableMaster.filter_bNotch, DefaultVariableMaster.filter_zNotch1, DefaultVariableMaster.filter_zNotch2);
    }


}