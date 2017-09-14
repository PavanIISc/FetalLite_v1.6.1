package com.sattvamedtech.fetallite.miniTestModule;

public class mQRSDetection {
    matrixFunctions Matrix = new matrixFunctions();

    public int[] mQRS(double[][] input) {

        int length = input.length;

        double[] channel1 = new double[length];
        double[] channel2 = new double[length];
        double[] channel3 = new double[length];
        double[] channel4 = new double[length];

        for (int i = 0; i < length; i++) {
            channel1[i] = input[i][0];
            channel2[i] = input[i][1];
            channel3[i] = input[i][2];
            channel4[i] = input[i][3];
        }

        int qrs1[] = maternalQRS(channel1);
        int qrs2[] = maternalQRS(channel2);
        int qrs3[] = maternalQRS(channel3);
        int qrs4[] = maternalQRS(channel4);

        Object[] qrsSelectionInputs = Matrix.channelSelection(qrs1, qrs2, qrs3, qrs4, DefaultVariableMaster.mqrs_variance_limit);

        qrsmSelection qrsMaternal = new qrsmSelection();
        int[] qrsM = qrsMaternal.qrsSelection((int[]) qrsSelectionInputs[0], (int) qrsSelectionInputs[1]);


        return qrsM;
    }


    private int[] maternalQRS(double[] channel) {
        int length = channel.length;
        // differentiate and square
        Matrix.convolutionQRSDetection(channel, DefaultVariableMaster.qrs_derivative);

        /**
         * FIltering 0.8- 3Hz
         */

        double bhigh[] = new double[2];
        double ahigh[] = new double[2];
        for (int i0 = 0; i0 < 2; i0++) {
            bhigh[i0] = DefaultVariableMaster.mqrs_bhigh0 + DefaultVariableMaster.mqrs_bhigh_sum * (double) i0;
            ahigh[i0] = 1.0 + DefaultVariableMaster.mqrs_ahigh_sum * (double) i0;
        }

        Matrix.filterLoHi(channel, ahigh, bhigh, DefaultVariableMaster.mqrs_zhigh);


        double alow[] = new double[2];
        for (int i0 = 0; i0 < 2; i0++) {
            alow[i0] = 1.0 + DefaultVariableMaster.mqrs_alow_sum * (double) i0;
        }

        Matrix.filterLoHi(channel, alow, DefaultVariableMaster.mqrs_blow, DefaultVariableMaster.mqrs_zlow);

        /**
         * Integrator
         */

        double[] integrator = new double[length];

        double sum = 0;

        for (int j = 0; j < DefaultVariableMaster.mqrs_window; j++) {
            sum = sum + channel[DefaultVariableMaster.mqrs_window - j - 1];
        }
        integrator[DefaultVariableMaster.mqrs_window - 1] = sum / DefaultVariableMaster.mqrs_window;

        for (int i = DefaultVariableMaster.mqrs_window; i < length; i++) {
            integrator[i] = integrator[i - 1] + (-channel[i - DefaultVariableMaster.mqrs_window] + channel[i]) / DefaultVariableMaster.mqrs_window;
        }

        /**
         * Find the 90% and 10% value to find the threshold
         */

        double threshold = Matrix.setIntegratorThreshold(integrator);

        /**
         * Peak Detection , Determines the peaks of the signal
         * Just return the first column of the Maxtab. No need the magnitudes.
         */
        int peakLoc[] = Matrix.peakDetection(integrator, threshold);


        int delay = DefaultVariableMaster.mqrs_window / 2;
        int peakLength = peakLoc.length;
        // Check the starting peak is greater than delay/2 or remove nIt
        int count = 0;
        for (int i = 0; i < peakLength; i++) {
            if (peakLoc[i] < delay + 2) {
                count = count + 1;
            }
        }

        int lenQrs = peakLength - count;
        int[] qrs = new int[lenQrs];
        for (int i = 0; i < lenQrs; i++) {
            qrs[i] = peakLoc[i + count] - delay;
        }

        return qrs;
    }


}