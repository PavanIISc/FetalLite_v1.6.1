package com.sattvamedtech.fetallite.miniTestModule;

public class qrsmSelection {
    public int[] qrsSelection(int[] qrs, int startIndex) {

        /**
         * Final Selection of qrs
         */


        int dec = 0;
        int iter = 0;
        int inc = 0;
        ;
        int lengthQRS = qrs.length;
        int qrsFinal[] = new int[lengthQRS];
        int lenM = 0;
        int start = 0;


        if (startIndex >= 0) {

            qrsFinal[startIndex] = qrs[startIndex];
            qrsFinal[startIndex + 1] = qrs[startIndex + 1];

            int i = startIndex + 1;
            int count = startIndex + 2;
            double rrDiff0 = 0;
            double rrDiff1 = 0;
            double rrDiff2 = 0;


            while (i < lengthQRS - 2) {
                if (count < DefaultVariableMaster.qrsm_initial_RRcount) {
                    rrDiff0 = 0;
                    for (int j = startIndex + 1; j < count; j++) {
                        rrDiff0 = rrDiff0 + qrsFinal[i] - qrsFinal[i - 1];
                    }
                    rrDiff0 = rrDiff0 / (count - 1);

                } else {
                    rrDiff0 = 0;
                    for (int j = count - DefaultVariableMaster.qrsm_RRcount; j < count; j++) {
                        rrDiff0 = rrDiff0 + qrsFinal[i] - qrsFinal[i - 1];
                    }
                    rrDiff0 = rrDiff0 / DefaultVariableMaster.qrsm_RRcount;
                }

                rrDiff1 = qrs[i + 1] - qrsFinal[count - 1];
                rrDiff2 = qrs[i + 2] - qrsFinal[count - 1];

                if (rrDiff1 > DefaultVariableMaster.qrsm_high_RRThreshold) {
                    if ((rrDiff1 > rrDiff0 * DefaultVariableMaster.qrs_RR_lowPerc) && (rrDiff1 < DefaultVariableMaster.qrs_RR_highPerc * rrDiff0)) {
                        qrsFinal[count] = qrs[i + 1];
                        i = i + 1;
                        count = count + 1;
                    } else if ((rrDiff2 > rrDiff0 * DefaultVariableMaster.qrs_RR_lowPerc) && (rrDiff2 < DefaultVariableMaster.qrs_RR_highPerc * rrDiff0)) {
                        qrsFinal[count] = qrs[i + 2];
                        count = count + 1;
                        i = i + 2;
                    } else if (((qrs[i + 2] - qrs[i + 1]) > DefaultVariableMaster.qrsm_RR_missPerc * rrDiff0) || (qrs[i + 2] - qrs[i + 1] > DefaultVariableMaster.qrsm_low_RRThreshold)) {
                        qrsFinal[count] = qrs[i + 1];

                        i = i + 1;
                        count = count + 1;
                    } else {
                        iter = 0;
                        inc = 0;
                        while (iter == 0) {
                            if ((qrs[i + 2 + inc] - qrs[i + 1] > DefaultVariableMaster.qrsm_RR_missPerc * rrDiff0) || (qrs[i + 2 + inc] - qrs[i + 1]) > DefaultVariableMaster.qrsm_low_RRThreshold) {
                                qrsFinal[count] = qrs[i + 1];
                                i = i + 1 + inc;
                                count = count + 1;
                                iter = 1;
                            } else {
                                inc = inc + 1;
                            }
                        }
                    }


                } else {
                    if ((rrDiff2 > DefaultVariableMaster.qrsm_high_RRThreshold)) {
                        qrsFinal[count] = qrs[i + 2];
                        i = i + 2;
                        count = count + 1;
                    } else {
                        iter = 0;
                        inc = 0;
                        while (iter == 0) {
                            if ((qrs[i + 2 + inc] - qrs[i + 1] > DefaultVariableMaster.qrsm_RR_missPerc * rrDiff0) || (qrs[i + 2] - qrs[i + 1]) > DefaultVariableMaster.qrsm_low_RRThreshold) {
                                qrsFinal[count] = qrs[i + 2];
                                i = i + 2 + inc;
                                count = count + 1;
                                iter = 1;
                            } else {
                                inc = inc + 1;
                                if (i + 2 + inc == lengthQRS) {
                                    i = i + 2 + inc;
                                    iter = 1;
                                }
                            }
                        }
                    }
                }

            }

            // FInd how many peaks are left after the above set of iterations
            int it = 0;
            for (int j = count; j < lengthQRS; j++) {
                if (qrs[j] > qrsFinal[count - 1]) {
                    it = it + 1;
                }
            }

            // Find out how many are qrs peaks and add them to the array
            for (int j = 0; j < it; j++) {
                if ((qrs[i + 1] - qrsFinal[count - 1]) > DefaultVariableMaster.qrsm_high_RRThreshold) {
                    qrsFinal[count] = qrs[i + 1];
                    count = count + 1;
                    i = i + 1;
                } else {
                    i = i + 1;
                }
            }

            int fin = count - 1;
            // backtrack initial peaks

            count = startIndex - 1;

            if (startIndex > 0) {
                i = startIndex;
                while (i > 1) {
                    rrDiff0 = 0;
                    if (count + DefaultVariableMaster.qrsm_RRcount < qrsFinal.length - 1) {
                        for (int j = count + 1; j < count + 9; j++) {
                            rrDiff0 = rrDiff0 + qrsFinal[j + 1] - qrsFinal[j];
                        }
                        rrDiff0 = rrDiff0 / DefaultVariableMaster.qrsm_RRcount;
                    } else {
                        for (int j = count + 1; j < qrsFinal.length - 1; j++) {
                            rrDiff0 = rrDiff0 + qrsFinal[j + 1] - qrsFinal[j];
                        }
                        rrDiff0 = rrDiff0 / (qrsFinal.length - count - 1);
                    }

                    rrDiff1 = qrsFinal[i] - qrs[i - 1];
                    rrDiff2 = qrsFinal[i] - qrs[i - 2];

                    if (rrDiff1 > DefaultVariableMaster.qrsm_high_RRThreshold) {
                        if ((rrDiff1 > rrDiff0 * DefaultVariableMaster.qrs_RR_lowPerc) && (rrDiff1 < DefaultVariableMaster.qrs_RR_highPerc * rrDiff0)) {
                            qrsFinal[count] = qrs[i - 1];
                            i = i - 1;
                            count = count - 1;
                        } else if ((rrDiff2 > rrDiff0 * DefaultVariableMaster.qrs_RR_lowPerc) && (rrDiff2 < DefaultVariableMaster.qrs_RR_highPerc * rrDiff0)) {
                            qrsFinal[count] = qrs[i - 2];
                            count = count - 1;
                            i = i - 2;

                        } else if (((qrs[i - 1] - qrs[i - 2]) > DefaultVariableMaster.qrsm_RR_missPerc * rrDiff0) || ((qrs[i - 1] - qrs[i - 2]) > DefaultVariableMaster.qrsm_low_RRThreshold)) {
                            qrsFinal[count] = qrs[i - 1];
                            count = count - 1;
                            i = i - 1;
                        } else {
                            iter = 0;
                            dec = 1;
                            while ((iter == 0) || (i - 2 - dec) > 0) {
                                if (((qrs[i - 2 - dec] - qrs[i - 1]) > DefaultVariableMaster.qrsm_RR_missPerc * rrDiff0) || ((qrs[i - 2 - dec] - qrs[i - 1]) > DefaultVariableMaster.qrsm_low_RRThreshold)) {
                                    qrsFinal[count] = qrs[i - 1];
                                    i = i - 1 - dec;
                                    count = count - 1;
                                    iter = 1;
                                } else {
                                    dec = dec + 1;
                                }
                            }
                        }

                    } else {
                        if (rrDiff2 > DefaultVariableMaster.qrsm_high_RRThreshold) {
                            qrsFinal[count] = qrs[i - 2];
                            i = i - 2;
                            count = count - 1;
                        } else {
                            iter = 0;
                            inc = 0;
                            while (iter == 0) {
                                if (((qrs[i - 2 - inc] - qrs[i - 2]) > DefaultVariableMaster.qrsm_RR_missPerc * rrDiff0) || ((qrs[i - 2] - qrs[i - 1]) > DefaultVariableMaster.qrsm_low_RRThreshold)) {
                                    qrsFinal[count] = qrs[i - 2];
                                    i = i - 2 - inc;
                                    count = count - 1;
                                    iter = 1;
                                } else {
                                    inc = inc + 1;
                                    if (i - 2 - inc < 0) {
                                        i = i - 2 - inc;
                                        iter = 1;
                                    }
                                }
                            }
                        }
                    }

                } // end while(i>2)

                // obtain first 1-2 peaks

                for (int j = i; i > 1; i--) {
                    if (qrsFinal[count - 1] - qrs[j - 1] > DefaultVariableMaster.qrsm_high_RRThreshold) {
                        qrsFinal[count] = qrs[j - 1];
                        count = count - 1;
                    }
                }
            }


            start = count + 1;


            // start gives the first index of qrs in qrsFinal.
            // Final gives the last index of qrs in qrsFinal.

            lenM = fin - start + 1;


        }
        int QRSM[] = new int[lenM];
        for (int i = 0; i < lenM; i++) {
            QRSM[i] = qrsFinal[i + start];
        }


        return QRSM;
    }
}