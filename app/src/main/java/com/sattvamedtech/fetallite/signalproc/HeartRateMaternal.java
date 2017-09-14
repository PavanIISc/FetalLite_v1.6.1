package com.sattvamedtech.fetallite.signalproc;

import java.util.LinkedList;
/**
 * 
 * @version 23rd June, 2017
 * 			Added Condition in case first iteration no Peaks detected : (Constants.CURRENT_ITERATION - Constants.LastQRSMIteration) == 1
 * 
 * @version  19th June, 2017
 * 			Added a condition to compute HR, in case of peaks missed in previous iteration.
 * 
 * @version 24th May, 2017
 * @author kishoresubramanian
 *
 */
public class HeartRateMaternal {
	public void heartRate(LinkedList<Integer> iQRS) {

		if (Constants.CURRENT_ITERATION > 0 && Constants.NoDetectionFlagMaternal == 0 && (Constants.CURRENT_ITERATION - Constants.LastQRSMIteration) == 1) {
			int aDiff = iQRS.getFirst() - Constants.QRS_MATERNAL_LOCATION.getLast();
			if (aDiff < 60) {
				iQRS.removeFirst();
			}

			double aRRMean = (Constants.QRS_MATERNAL_LOCATION.getLast() - Constants.QRS_MATERNAL_LOCATION
					.get((Constants.QRS_MATERNAL_LOCATION.size() - (int) Constants.QRS_NO_RR_MEAN - 1)))
					/ Constants.QRS_NO_RR_MEAN;

			if (aDiff > Constants.QRS_RR_MISS_PERCENT * aRRMean) {
				int aFactor = (int) Math.round(aDiff / aRRMean);
				if (aFactor == 2) {
					int temp = Constants.QRS_MATERNAL_LOCATION.getLast();
					iQRS.addFirst(temp + (int) Math.round(aDiff / aFactor));

				}
			}
		}

		if (Constants.CURRENT_ITERATION == 0 || Constants.CURRENT_ITERATION != (Constants.LastQRSMIteration+1) || Constants.NoDetectionFlagMaternal == 1) {
			for (int f = (int) Constants.QRS_NO_RR_MEAN; f < iQRS.size(); f++) {
				Constants.QRS_MATERNAL_LOCATION.add(iQRS.get(f));
				Constants.HR_MATERNAL.add((float) (60 * Constants.FS * Constants.QRS_NO_RR_MEAN
						/ (iQRS.get(f) - iQRS.get((int) (f - Constants.QRS_NO_RR_MEAN)))));

			}
		} else {
			int aCounter = Constants.QRS_MATERNAL_LOCATION.size();
			for (int f = 0; f < iQRS.size(); f++) {
				Constants.QRS_MATERNAL_LOCATION.add(iQRS.get(f));
				Constants.HR_MATERNAL
						.add((float) (60 * Constants.FS * Constants.QRS_NO_RR_MEAN / (Constants.QRS_MATERNAL_LOCATION.getLast()
								- Constants.QRS_MATERNAL_LOCATION.get((int) (aCounter - Constants.QRS_NO_RR_MEAN)))));
				aCounter++;

			}
		}

	}

}
