package com.sattvamedtech.fetallite.miniTestModule;

public class DefaultVariableMaster {

    public static int no_of_Samples = 5000;
    public static int no_of_Channels = 4;
    public static int Fs = 1000;


    /**
     * Inpulse Filter Constant Values
     */

    public static int impulse_No_InitialSamples = 10;
    public static int impulse_Initial_Median_size = 3;
    public static int impluse_Threshold = 4;
    public static int impulse_Percentile = 2;            // ALWAYS < 100
    public static double impulse_Window_percent = 0.06;

    /**
     * Filtering Lo-Hi-Notch Constant Values
     */

    public static double filter_zhigh = -0.98453370859689782;
    public static double filter_bhigh0 = 0.984533708596897;
    public static double filter_bhigh_sum = -1.9690674171937941;
    public static double filter_ahigh_sum = -1.969067417193793;

    public static double filter_alow[] = {1.0, -0.324919696232906};
    public static double filter_blow[] = {0.337540151883547, 0.337540151883547};
    public static double filter_zlow = 0.662459848116453;

    public static int filter_nfact2 = 3;

    public static double filter_aNotch[] = {1.0, -1.8329786119774709, 0.927307768331003};
    public static double filter_bNotch[] = {0.963653884165502, -1.8329786119774709, 0.963653884165502};
    public static double filter_zNotch1 = 0.036346115834507829;
    public static double filter_zNotch2 = 0.036346115834507829 + -1.8013368574543165E-14;
    public static int filter_nfact3 = 6;


    /**
     * Maternal QRS detection
     */

    public static double[] qrs_derivative = {-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static int qrs_derivative_scale = 64;

    public static double qrs_integrator_max = 0.9;
    public static double qrs_integrator_min = 0.1;
    public static double qrs_integrator_ThFactor = 10;


    public static double mqrs_bhigh0 = 0.997493021323278;
    public static double mqrs_bhigh_sum = -1.994986042646556;
    public static double mqrs_ahigh_sum = -1.994986042646556;
    public static double mqrs_zhigh = -0.997493021323276;

    public static double[] mqrs_blow = {0.009337054753656, 0.009337054753656};
    public static double mqrs_alow_sum = -1.981325890492688;
    public static double mqrs_zlow = 0.99066294524634591;

    public static int mqrs_window = 50;
    public static int mqrs_variance_limit = 50;

    // MQRS selection threshold values
    public static int qrsm_high_RRThreshold = 400;
    public static int qrsm_low_RRThreshold = 200;

    public static double qrs_RR_lowPerc = 0.9;
    public static double qrs_RR_highPerc = 1.1;

    public static double qrsm_RR_missPerc = 0.4;

    public static int qrsm_initial_RRcount = 10;
    public static int qrsm_RRcount = 8;


    /**
     * Fetal QRS Detection
     */

    // Use derivative  and integrator mentioned in maternal QRS detection

    public static double[] fqrs_ahigh = {1.000000000000000, -0.987511929907314};
    public static double fqrs_bhigh0 = 0.993755964953657;
    public static double fqrs_bhigh_sum = -1.9875119299073141;
    public static double fqrs_zhigh = -0.99375596495365437;

    public static double[] fqrs_alow = {1.000000000000000, -0.978247159730251};
    public static double[] fqrs_blow = {0.010876420134875, 0.010876420134875};
    public static double fqrs_zlow = 0.98912357986517108;

    public static int fqrs_window = 75;

    public static int fqrs_variance_limit = 25;


    public static int qrsf_initial_RRcount = 9;
    public static int qrsf_RRcount = 8;

    // low and high percentage is found in maternal QRS detection, same is used for this also

    public static double qrsf_RR_missPerc = 1.66;
    public static int qrsf_RRThreshold = 300;


    /**
     * QRSM CANCELLATION
     */

    public static double cancel_qrsBeforePerc = 0.2;
    public static double cancel_qrsAfterPerc = 0.8;
    public static double cancel_qrsAfterTh = 0.5;

    public static double cancel_lastqrsThHighPerc = 0.15;
    public static double cancel_lastqrsThLowPerc = 0.1;
    public static int cancel_nSamplesEnd = 5;


}
