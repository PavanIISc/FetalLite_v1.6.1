package com.sattvamedtech.fetallite.signalproc;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @version 23rd June, 2017 Added Condition to check if qrsFinal size is greater
 *          than miss QRS location (aQrsFinal.size() > aIndMissF).
 * 
 * 
 * @version 22nd June, 2017 : Added Condition to interpolate only if 'aQrsFinal'
 *          has more than 2 entires.
 * 
 * 
 * @version 24th May, 2017
 * @author kishoresubramanian
 *
 */
public class QRSSelection {

	int mRRDiff;
	double mRRMean, mRRLowTh, mRRHighTh;

	double mNoRR = Constants.QRS_NO_RR_MEAN;
	double mLowPerc = Constants.QRS_RRLOW_PERC;
	double mHighPerc = Constants.QRS_RRHIGH_PERC;
	// double mVarTh = Constants.QRS_VAR_RR_TH;

	/**
	 * qrsSelection : Select the most likely QRS peaks from a list of possible
	 * QRS peaks.
	 * 
	 * @param iQRS
	 *            : Possible set of QRS locations
	 * @param iStartIndex
	 *            : First index of confirmed QRS in the iQRS array
	 * @param iQRSm
	 *            : Set of maternal QRS locations. ( Empty during finding
	 *            maternal QRS)
	 * @param iInterpolatedLength
	 *            : Length of QRS interpolated at the end in previous iteration
	 * @param iQRSLast
	 *            : Location of last QRS determined in previous iteration
	 * @param iRRMeanLast
	 *            : Mean RR of last 4 QRS determined in previous iteration
	 * @param iNoDetectionFlag
	 *            : (int) '1' if no single channel has been determined to
	 *            contain possible QRS locations else '0'
	 * @return Object[] { aQRSFinal, aInterpolatedLength, aNoDetectionFLag } :
	 *         {(int[]), (int), (int)} Return QRS selected and update Flags.
	 * 
	 */
	public Object[] qrsSelection(int[] iQRS, int iStartIndex, int[] iQRSm, int iInterpolatedLength, int iQRSLast,
			double iRRMeanLast, int iNoDetectionFlag) {

		if (iStartIndex > -1) {
			// Initialization of variables
			int aInterpolatedLength = 0;

			int aMinRRDiff0 = 0, aMinRRDiff1;
			int aIncrement1 = 0, aIncrement2 = 0;
			int aLengthQRS = iQRS.length;

			int aForwardIteration = 0;
			int aCountF = 0;
			int aCountI = 0;

			LinkedList<Integer> aQrsFinal = new LinkedList<Integer>();
			Queue<Integer> aMissF = new LinkedList<Integer>();

			// adding First 2 QRS locations to QRSFinal
			aForwardIteration = iStartIndex;
			aQrsFinal.add(iQRS[aForwardIteration]);
			aQrsFinal.add(iQRS[aForwardIteration + 1]);
			aForwardIteration = aForwardIteration + 2;

			aCountF = 2;

			int aMissFlag = 0;
			double aCountMiss = 0;
			int aMinCheckFlag = 0;
			int aHarmonicCheckFlag = 0;

			int aFindFlag = 0;

			while (aForwardIteration < aLengthQRS) {
				aCountI = aCountF;

				if (aMissFlag == 0) {
					if (aCountF <= mNoRR) {
						mRRMean = (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(0)) / (aCountF - 1);
					} else {
						mRRMean = (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 1 - (int) mNoRR)) / mNoRR;
					}
				} else {
					mRRMean = (aQrsFinal.get(aCountF - 1) - aQrsFinal.get((int) (aCountF - aCountMiss)))
							/ (aCountMiss - 1);
				}

				mRRDiff = iQRS[aForwardIteration] - aQrsFinal.get(aCountF - 1);
				mRRLowTh = mRRMean * mLowPerc;
				mRRHighTh = mRRMean * mHighPerc;

				if (mRRDiff < mRRLowTh) {
					aForwardIteration++;
				} else if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
					aMinRRDiff0 = 10000;
					aMinCheckFlag = 1;
				} else {
					aFindFlag = 0;
					aIncrement1 = aForwardIteration;
					aIncrement2 = aForwardIteration + 1;
					if (aIncrement2 >= aLengthQRS) {
						aForwardIteration = aIncrement2;
						aFindFlag = 1;
					}

					while (aFindFlag == 0) {
						mRRDiff = iQRS[aIncrement2] - iQRS[aIncrement1];

						if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
							aMinRRDiff0 = 10000;
							aHarmonicCheckFlag = 1;
							aFindFlag = 1;
						} else if (mRRDiff < mRRLowTh) {
							aIncrement2++;
							if (aIncrement2 >= aLengthQRS) {
								aForwardIteration = aIncrement2;
								aFindFlag = 1;
							}
						} else if (mRRDiff > mRRHighTh) {
							aIncrement1++;
							if (aIncrement2 == aIncrement1) {
								aIncrement2++;
								if (aIncrement2 >= aLengthQRS) {
									aForwardIteration = aIncrement2;
									aFindFlag = 1;
								}
							}
						}
					}

				} // END of finding if QRS is to be selected.

				if (aHarmonicCheckFlag == 1) {
					LinkedList<Integer> aTemp1 = new LinkedList<Integer>();
					LinkedList<Integer> aTemp2 = new LinkedList<Integer>();

					for (int i = aIncrement1; i < aLengthQRS; i++) {
						if (iQRS[i] <= iQRS[aIncrement1] + mRRHighTh) {
							aTemp1.add(iQRS[i]);
						} else if (iQRS[i] > iQRS[aIncrement1] + mRRHighTh) {
							break;
						}
					}

					for (int i = aIncrement2; i < aLengthQRS; i++) {
						if (iQRS[i] <= iQRS[aIncrement2] + mRRHighTh) {
							aTemp2.add(iQRS[i]);
						} else if (iQRS[i] > iQRS[aIncrement2] + mRRHighTh) {
							break;
						}
					}

					int[] aTempQRS = new int[2];
					for (int i = 0; i < aTemp1.size(); i++) {
						for (int j = 0; j < aTemp2.size(); j++) {
							mRRDiff = aTemp2.get(j) - aTemp1.get(i);

							if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
								aMinRRDiff1 = (int) Math.abs(mRRDiff - mRRMean);
								if (aMinRRDiff1 < aMinRRDiff0) {
									aTempQRS[0] = aTemp1.get(i);
									aTempQRS[1] = aTemp2.get(j);
									aForwardIteration = aIncrement2 + j + 1;
									aMinRRDiff0 = aMinRRDiff1;
								}
							}
						}
					}
					aQrsFinal.add(aTempQRS[0]);
					aQrsFinal.add(aTempQRS[1]);
					aCountF = aCountF + 2;
					aHarmonicCheckFlag = 0;
				} // end addition of harmonic qrs locations.

				if (aMinCheckFlag == 1) {
					LinkedList<Integer> aTemp = new LinkedList<Integer>();
					for (int i = aForwardIteration; i < aLengthQRS; i++) {

						if (iQRS[i] <= iQRS[aForwardIteration] + mRRHighTh) {
							aTemp.add(iQRS[i]);
						} else if (iQRS[i] > iQRS[aForwardIteration] + mRRHighTh) {
							break;
						}
					}
					int aTempQRS = 0;
					int aShift = 0;
					for (int j = 0; j < aTemp.size(); j++) {
						mRRDiff = aTemp.get(j) - aQrsFinal.get(aCountF - 1);

						if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
							aMinRRDiff1 = (int) Math.abs(mRRDiff - mRRMean);
							if (aMinRRDiff1 < aMinRRDiff0) {
								aTempQRS = aTemp.get(j);
								aShift = aForwardIteration + j + 1;
								aMinRRDiff0 = aMinRRDiff1;
							}
						}
					}
					aQrsFinal.add(aTempQRS);
					aMinCheckFlag = 0;
					aCountF++;
					aForwardIteration = aShift;
				} // End addition of next qrs location

				if (aCountI < aCountF) {
					if (aQrsFinal.get(aCountF - 2) - aQrsFinal.get(aCountF - 3) > Constants.QRS_RR_MISS_PERCENT
							* mRRMean) {
						aMissF.add(aCountF - 2);
						aMissFlag = 1;
						aCountMiss = 1;
					}
					if (aMissFlag == 1) {
						aCountMiss++;
						if (aCountMiss == 5) {
							aMissFlag = 0;
						}
					}
				}

			} // End while loop for forward iteration

			/**
			 * Add missed Peaks
			 */

			int aLengthMiss = aMissF.size();
			int aIndMissF = -1;

			int aFactor = 0;
			int aOverlapFlag = 0;
			int aQrsInter;
			int aElementAdded = 0;
			double aDiffMiss, aDiffDenominator;

			if (aLengthMiss > 0) {
				int aFlag = 0;

				for (int i = 0; i < aLengthMiss; i++) {

					aIndMissF = aMissF.remove() + aElementAdded;
					aFlag = 0;
					if (aQrsFinal.size() > aIndMissF && aIndMissF >= 2) {
						aDiffMiss = (aQrsFinal.get(aIndMissF) - aQrsFinal.get(aIndMissF - 1));
						aDiffDenominator = (aQrsFinal.get(aIndMissF - 1) - aQrsFinal.get(aIndMissF - 2));
						aFactor = (int) Math.round(aDiffMiss / aDiffDenominator);
						aOverlapFlag = 0;

						if (aFactor == 2) {
							aQrsInter = FindOverlapMqrsLoc(iQRSm, aQrsFinal.get(aIndMissF - 1),
									aQrsFinal.get(aIndMissF));

							if (aQrsInter > 0) {
								if (aIndMissF <= (int) mNoRR) {
									mRRMean = (aQrsFinal.get(aIndMissF - 1) - aQrsFinal.get(0)) / (aIndMissF - 1);
								} else {
									mRRMean = (aQrsFinal.get(aIndMissF - 1)
											- aQrsFinal.get(aIndMissF - 1 - (int) mNoRR)) / mNoRR;
								}
							}

							mRRDiff = aQrsInter - aQrsFinal.get(aIndMissF - 1) + 1;
							mRRLowTh = mRRMean * mLowPerc;
							mRRHighTh = mRRMean * mHighPerc;

							if ((mRRDiff >= mRRLowTh) && (mRRDiff <= mRRHighTh)) {
								aQrsFinal.add(aIndMissF, aQrsInter + 1);
								aOverlapFlag = 1;
								aFlag = 1;
							}
						}


						if (aOverlapFlag == 0) {
							// aInterpolatedLength = (int) (aInterpolatedLength
							// +
							// aDiffMiss);
							for (int f = aFactor - 1; f >= 1; f--) {
								aQrsInter = (int) (aQrsFinal.get(aIndMissF - 1) + aDiffMiss * f / aFactor);
								aQrsFinal.add(aIndMissF, aQrsInter);
								aFlag = 1;
							}
						}
					}


					if (aFlag == 1) {
						aElementAdded = aElementAdded + (aFactor - 1);
					}
				}
			} // End interpolating

			/**
			 * Interpolate at the end
			 */

			if (aQrsFinal.getLast() <= Constants.QRS_END_VALUE && aQrsFinal.size() >= 2) {

				aInterpolatedLength = aInterpolatedLength + Constants.QRS_END_VALUE - aQrsFinal.getLast();
				int aLenQRS = aQrsFinal.size();
				int aDiffLast = aQrsFinal.get(aLenQRS - 1) - aQrsFinal.get(aLenQRS - 2);

				while (aQrsFinal.getLast() <= Constants.QRS_END_VALUE) {
					aQrsFinal.add(aQrsFinal.getLast() + aDiffLast);
				}
			}

			/**
			 * Backtrack initial peaks
			 */
			LinkedList<Integer> aMissB = new LinkedList<Integer>();
			int aDecrement1 = 0, aDecrement2 = 0;
			aCountI = 0;
			aCountF = 0;
			aMissFlag = 0;
			int aBackIteration = iStartIndex - 1;

			while (aBackIteration >= 0) {
				aCountI = aCountF;

				if (aMissFlag == 0) {
					if (aQrsFinal.size() <= mNoRR) {
						mRRMean = (aQrsFinal.getLast() - aQrsFinal.getFirst()) / (aQrsFinal.size() - 1);
					} else {
						mRRMean = (aQrsFinal.get((int) mNoRR) - aQrsFinal.getFirst()) / mNoRR;
					}
				} else {
					mRRMean = (aQrsFinal.get((int) (aCountMiss - 1)) - aQrsFinal.getFirst()) / (aCountMiss - 1);
				}

				mRRDiff = aQrsFinal.getFirst() - iQRS[aBackIteration];
				mRRLowTh = mLowPerc * mRRMean;
				mRRHighTh = mHighPerc * mRRMean;

				if (mRRDiff < mRRLowTh) {
					aBackIteration--;
				} else if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
					aMinRRDiff0 = 10000;
					aMinCheckFlag = 1;
				} else {
					aFindFlag = 0;
					aDecrement1 = aBackIteration;
					aDecrement2 = aBackIteration - 1;

					if (aDecrement2 <= 0) {
						aFindFlag = 1;
						aBackIteration = aDecrement2;
					}

					while (aFindFlag == 0) {
						mRRDiff = iQRS[aDecrement1] - iQRS[aDecrement2];

						if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
							aMinRRDiff0 = 10000;
							aHarmonicCheckFlag = 1;
							aFindFlag = 1;
						} else if (mRRDiff < mRRLowTh) {
							aDecrement2--;
							if (aDecrement2 <= 0) {
								aFindFlag = 1;
								aBackIteration = aDecrement2;
							}
						} else if (mRRDiff > mRRHighTh) {
							aDecrement1--;
							if (aDecrement1 == aDecrement2) {
								aDecrement2--;

								if (aDecrement2 <= 0) {
									aFindFlag = 1;
									aBackIteration = aDecrement2;
								}
							}
						}

					}

				} // END of finding if QRS is to be selected.

				if (aHarmonicCheckFlag == 1) {
					LinkedList<Integer> aTemp1 = new LinkedList<Integer>();
					LinkedList<Integer> aTemp2 = new LinkedList<Integer>();

					for (int i = aDecrement1; i >= 0; i--) {

						if (iQRS[i] >= iQRS[aDecrement1] - mRRLowTh) {
							aTemp1.add(iQRS[i]);
						} else if (iQRS[i] < iQRS[aDecrement1] - mRRLowTh) {
							break;
						}
					}
					for (int i = aDecrement2; i >= 0; i--) {

						if (iQRS[i] >= iQRS[aDecrement2] - mRRLowTh) {
							aTemp2.add(iQRS[i]);
						} else if (iQRS[i] < iQRS[aDecrement2] - mRRLowTh) {
							break;
						}
					}
					int[] aTempQRS = new int[2];
					int aShift = 0;
					for (int i = 0; i < aTemp1.size(); i++) {
						for (int j = 0; j < aTemp2.size(); j++) {
							mRRDiff = aTemp1.get(i) - aTemp2.get(j);

							if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
								aMinRRDiff1 = (int) Math.abs(mRRDiff - mRRMean);
								if (aMinRRDiff1 < aMinRRDiff0) {
									aTempQRS[0] = aTemp1.get(i);
									aTempQRS[1] = aTemp2.get(j);
									aShift = aDecrement2 - j - 1;
									aMinRRDiff0 = aMinRRDiff1;
								}
							}
						}
					}
					aQrsFinal.addFirst(aTempQRS[0]);
					aQrsFinal.addFirst(aTempQRS[1]);
					aCountF = aCountF + 2;
					aHarmonicCheckFlag = 0;
					aBackIteration = aShift;
					if (aMissB.size() > 0) {
						int aTemp;

						for (int i = 0; i < aMissB.size(); i++) {
							aTemp = aMissB.get(i) + 2;
							aMissB.set(i, aTemp);
						}
					}
				} // end addition of harmonic qrs locations.

				if (aMinCheckFlag == 1) {
					LinkedList<Integer> aTemp = new LinkedList<Integer>();

					for (int i = aBackIteration; i >= 0; i--) {

						if (iQRS[i] >= iQRS[aBackIteration] - mRRLowTh) {
							aTemp.add(iQRS[i]);
						} else if (iQRS[i] < iQRS[aBackIteration] - mRRLowTh) {
							break;
						}
					}
					int aTempQRS = 0;
					int aShift = 0;
					for (int j = 0; j < aTemp.size(); j++) {
						mRRDiff = aQrsFinal.getFirst() - aTemp.get(j);

						if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
							aMinRRDiff1 = (int) Math.abs(mRRDiff - mRRMean);

							if (aMinRRDiff1 < aMinRRDiff0) {
								aTempQRS = aTemp.get(j);
								aShift = aBackIteration - j - 1;
								aMinRRDiff0 = aMinRRDiff1;
							}
						}
					}
					aQrsFinal.addFirst(aTempQRS);
					aMinCheckFlag = 0;
					aCountF++;
					aBackIteration = aShift;
					if (aMissB.size() > 0) {
						int aTemp1;

						for (int i = 0; i < aMissB.size(); i++) {
							aTemp1 = aMissB.get(i) + 1;
							aMissB.set(i, aTemp1);
						}
					}
				} // End addition of next qrs location

				if (aCountI < aCountF) {

					if (aQrsFinal.get(2) - aQrsFinal.get(1) > Constants.QRS_RR_MISS_PERCENT * mRRMean) {
						aMissB.add(2);
						aMissFlag = 1;
						aCountMiss = 1;
					}

					if (aMissFlag == 1) {
						aCountMiss++;
						if (aCountMiss == mNoRR + 1) {
							aMissFlag = 0;
						}
					}
				}

			} // end backtracking of peaks done

			/**
			 * Add missed peak while backtracking
			 * 
			 */

			aLengthMiss = aMissB.size();
			aIndMissF = -1;

			aFactor = 0;
			aOverlapFlag = 0;

			if (aLengthMiss > 0) {

				for (int i = 0; i < aLengthMiss; i++) {
					aIndMissF = aMissB.remove();

					aDiffMiss = (aQrsFinal.get(aIndMissF) - aQrsFinal.get(aIndMissF - 1));
					aDiffDenominator = (aQrsFinal.get(aIndMissF - 1) - aQrsFinal.get(aIndMissF - 2));
					aFactor = (int) Math.round(aDiffMiss / aDiffDenominator);
					aOverlapFlag = 0;

					if (aFactor == 2) {
						aQrsInter = FindOverlapMqrsLoc(iQRSm, aQrsFinal.get(aIndMissF - 1), aQrsFinal.get(aIndMissF));

						if (aQrsInter > 0) {
							if (aIndMissF <= mNoRR) {
								mRRMean = (aQrsFinal.get(aIndMissF - 1) - aQrsFinal.get(0)) / (aIndMissF - 1);
							} else {
								mRRMean = (aQrsFinal.get(aIndMissF - 1) - aQrsFinal.get(aIndMissF - 1 - (int) mNoRR))
										/ mNoRR;
							}
						}
						mRRDiff = aQrsInter - aQrsFinal.get(aIndMissF - 1) + 1;
						mRRLowTh = mRRMean * mLowPerc;
						mRRHighTh = mRRMean * mHighPerc;

						if ((mRRDiff >= mRRLowTh) && (mRRDiff <= mRRHighTh)) {
							aQrsFinal.add(aIndMissF, aQrsInter + 1);
							aOverlapFlag = 1;

						}
					}

					if (aOverlapFlag == 0) {

						for (int f = aFactor - 1; f >= 1; f--) {
							aQrsInter = (int) (aQrsFinal.get(aIndMissF - 1) + aDiffMiss * f / aFactor);
							aQrsFinal.add(aIndMissF, aQrsInter);

						}
					}
					// if (aFlag == 1) {
					// aElementAdded = aElementAdded + (aFactor - 1);
					// }
				}
			} // End interpolating

			/**
			 * Interpolate at the start
			 */

			if (aQrsFinal.getFirst() >= Constants.QRS_START_VALUE && aQrsFinal.size() >= 2) {

				// aInterpolatedLength = aInterpolatedLength +
				// aQrsFinal.getFirst() -
				// Constants.QRS_LENGTH_START_INTERPOLATE;
				int aDiffLast = aQrsFinal.get(1) - aQrsFinal.get(0);

				while (aQrsFinal.getFirst() > Constants.QRS_START_VALUE) {
					aQrsFinal.addFirst(aQrsFinal.getFirst() - aDiffLast);
				}
			}

			int aLengthQrsFinal = aQrsFinal.size();

			int[] aQRSFinal = new int[aLengthQrsFinal];
			for (int i = 0; i < aLengthQrsFinal; i++) {
				aQRSFinal[i] = aQrsFinal.get(i);
			}

			int aNoDetectionFLag = 0;
			return new Object[] { aQRSFinal, aInterpolatedLength, aNoDetectionFLag };

		} else// If no QRS is channel is selected
		{
			if (iNoDetectionFlag == 0 && iInterpolatedLength < Constants.QRS_LENGTH_MAX_INTERPOLATE) {

				int aInterpolatedLength = 0;

				int aMinRRDiff0 = 0, aMinRRDiff1;
				int aIncrement1 = 0, aIncrement2 = 0;
				int aLengthQRS = iQRS.length;

				int aForwardIteration = 0;
				int aCountF = 0;
				int aCountI = 0;

				LinkedList<Integer> aQrsFinal = new LinkedList<Integer>();
				Queue<Integer> aMissF = new LinkedList<Integer>();

				int aMissFlag = 0;
				double aCountMiss = 0;
				int aMinCheckFlag = 0;
				int aHarmonicCheckFlag = 0;

				int aFindFlag = 0;

				// FIRST QRS LOCATION AND MEAN
				aQrsFinal.add(iQRSLast);
				mRRMean = iRRMeanLast;
				aCountF = 1;

				while (iQRS[aForwardIteration] < iQRSLast) {
					aForwardIteration++;
				}

				while (aForwardIteration < aLengthQRS) {
					aCountI = aCountF;

					if (aMissFlag == 0) {
						if (aCountF == 1) {
							mRRMean = mRRMean;
						} else if (aCountF > 1 && aCountF <= mNoRR) {
							mRRMean = (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(0)) / (aCountF - 1);
						} else {
							mRRMean = (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 1 - (int) mNoRR)) / mNoRR;
						}
					} else {
						mRRMean = (aQrsFinal.get(aCountF - 1) - aQrsFinal.get((int) (aCountF - aCountMiss)))
								/ (aCountMiss - 1);
					}

					mRRDiff = iQRS[aForwardIteration] - aQrsFinal.get(aCountF - 1);
					mRRLowTh = mRRMean * mLowPerc;
					mRRHighTh = mRRMean * mHighPerc;

					if (mRRDiff < mRRLowTh) {
						aForwardIteration++;
					} else if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
						aMinRRDiff0 = 10000;
						aMinCheckFlag = 1;
					} else {
						aFindFlag = 0;
						aIncrement1 = aForwardIteration;
						aIncrement2 = aForwardIteration + 1;
						if (aIncrement2 >= aLengthQRS) {
							aForwardIteration = aIncrement2;
							aFindFlag = 1;
						}

						while (aFindFlag == 0) {
							mRRDiff = iQRS[aIncrement2] - iQRS[aIncrement1];

							if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
								aMinRRDiff0 = 10000;
								aHarmonicCheckFlag = 1;
								aFindFlag = 1;
							} else if (mRRDiff < mRRLowTh) {
								aIncrement2++;
								if (aIncrement2 >= aLengthQRS) {
									aForwardIteration = aIncrement2;
									aFindFlag = 1;
								}
							} else if (mRRDiff > mRRHighTh) {
								aIncrement1++;
								if (aIncrement2 == aIncrement1) {
									aIncrement2++;
									if (aIncrement2 >= aLengthQRS) {
										aForwardIteration = aIncrement2;
										aFindFlag = 1;
									}
								}
							}
						}

					} // END of finding if QRS is to be selected.

					if (aHarmonicCheckFlag == 1) {
						LinkedList<Integer> aTemp1 = new LinkedList<Integer>();
						LinkedList<Integer> aTemp2 = new LinkedList<Integer>();

						for (int i = aIncrement1; i < aLengthQRS; i++) {
							if (iQRS[i] <= iQRS[aIncrement1] + mRRHighTh) {
								aTemp1.add(iQRS[i]);
							} else if (iQRS[i] > iQRS[aIncrement1] + mRRHighTh) {
								break;
							}
						}

						for (int i = aIncrement2; i < aLengthQRS; i++) {
							if (iQRS[i] <= iQRS[aIncrement2] + mRRHighTh) {
								aTemp2.add(iQRS[i]);
							} else if (iQRS[i] > iQRS[aIncrement2] + mRRHighTh) {
								break;
							}
						}

						int[] aTempQRS = new int[2];
						for (int i = 0; i < aTemp1.size(); i++) {
							for (int j = 0; j < aTemp2.size(); j++) {
								mRRDiff = aTemp2.get(j) - aTemp1.get(i);

								if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
									aMinRRDiff1 = (int) Math.abs(mRRDiff - mRRMean);
									if (aMinRRDiff1 < aMinRRDiff0) {
										aTempQRS[0] = aTemp1.get(i);
										aTempQRS[1] = aTemp2.get(j);
										aForwardIteration = aIncrement2 + j + 1;
										aMinRRDiff0 = aMinRRDiff1;
									}
								}
							}
						}
						aQrsFinal.add(aTempQRS[0]);
						aQrsFinal.add(aTempQRS[1]);
						aCountF = aCountF + 2;
						aHarmonicCheckFlag = 0;
					} // end addition of harmonic qrs locations.

					if (aMinCheckFlag == 1) {
						LinkedList<Integer> aTemp = new LinkedList<Integer>();

						for (int i = aForwardIteration; i < aLengthQRS; i++) {
							if (iQRS[i] <= iQRS[aForwardIteration] + mRRHighTh) {
								aTemp.add(iQRS[i]);
							} else if (iQRS[i] > iQRS[aForwardIteration] + mRRHighTh) {
								break;
							}
						}
						int aTempQRS = 0;

						for (int j = 0; j < aTemp.size(); j++) {
							mRRDiff = aTemp.get(j) - aQrsFinal.get(aCountF - 1);
							if (mRRDiff >= mRRLowTh && mRRDiff <= mRRHighTh) {
								aMinRRDiff1 = (int) Math.abs(mRRDiff - mRRMean);
								if (aMinRRDiff1 < aMinRRDiff0) {
									aTempQRS = aTemp.get(j);
									aForwardIteration = aForwardIteration + j + 1;
									aMinRRDiff0 = aMinRRDiff1;
								}
							}
						}
						aQrsFinal.add(aTempQRS);
						aMinCheckFlag = 0;
						aCountF++;
					} // End addition of next qrs location

					if (aCountI < aCountF && aCountF >= 3) {

						if (aQrsFinal.get(aCountF - 2) - aQrsFinal.get(aCountF - 3) > Constants.QRS_RR_MISS_PERCENT
								* mRRMean) {
							aMissF.add(aCountF - 2);
							aMissFlag = 1;
							aCountMiss = 1;
						}

						if (aMissFlag == 1) {
							aCountMiss++;
							if (aCountMiss == 5) {
								aMissFlag = 0;
							}
						}
					}

				} // End while loop for forward iteration

				/**
				 * Add missed Peaks
				 */

				int aLengthMiss = aMissF.size();
				int aIndMissF = -1;

				int aFactor = 0;
				int aOverlapFlag = 0;
				int aQrsInter;
				int aElementAdded = 0;
				double aDiffMiss, aDiffDenominator;

				if (aLengthMiss > 0) {
					int aFlag = 0;

					for (int i = 0; i < aLengthMiss; i++) {
						aIndMissF = aMissF.remove() + aElementAdded;
						aFlag = 0;
						if (aQrsFinal.size() > aIndMissF && aIndMissF >= 2) {
							aDiffMiss = (aQrsFinal.get(aIndMissF) - aQrsFinal.get(aIndMissF - 1));
							aDiffDenominator = (aQrsFinal.get(aIndMissF - 1) - aQrsFinal.get(aIndMissF - 2));
							aFactor = (int) Math.round(aDiffMiss / aDiffDenominator);
							aOverlapFlag = 0;

							if (aFactor == 2) {
								aQrsInter = FindOverlapMqrsLoc(iQRSm, aQrsFinal.get(aIndMissF - 1),
										aQrsFinal.get(aIndMissF));
								if (aQrsInter > 0) {
									if (aIndMissF <= mNoRR) {
										mRRMean = (aQrsFinal.get(aIndMissF - 1) - aQrsFinal.get(0)) / (aIndMissF - 1);
									} else {
										mRRMean = (aQrsFinal.get(aIndMissF - 1)
												- aQrsFinal.get(aIndMissF - 1 - (int) mNoRR)) / mNoRR;
									}
								}

								mRRDiff = aQrsInter - aQrsFinal.get(aIndMissF - 1) + 1;
								mRRLowTh = mRRMean * mLowPerc;
								mRRHighTh = mRRMean * mHighPerc;

								if ((mRRDiff >= mRRLowTh) && (mRRDiff <= mRRHighTh)) {
									aQrsFinal.add(aIndMissF, aQrsInter + 1);
									aOverlapFlag = 1;
									aFlag = 1;
								}
							}

							if (aOverlapFlag == 0) {
								// aInterpolatedLength = (int)
								// (aInterpolatedLength
								// + aDiffMiss);
								for (int f = aFactor - 1; f >= 1; f--) {
									aQrsInter = (int) (aQrsFinal.get(aIndMissF - 1) + aDiffMiss * f / aFactor);
									aQrsFinal.add(aIndMissF, aQrsInter);
									aFlag = 1;
								}
							}
						}
						if (aFlag == 1) {
							aElementAdded = aElementAdded + (aFactor - 1);
						}
					}
				} // End interpolating

				/**
				 * Interpolate at the end
				 */

				if (aQrsFinal.getLast() < Constants.QRS_END_VALUE && aQrsFinal.size() >= 2) {

					aInterpolatedLength = Constants.QRS_END_VALUE - aQrsFinal.getLast();
					int aLenQRS = aQrsFinal.size();
					int aDiffLast = aQrsFinal.get(aLenQRS - 1) - aQrsFinal.get(aLenQRS - 2);

					while (aQrsFinal.getLast() < Constants.QRS_END_VALUE) {
						aQrsFinal.add(aQrsFinal.getLast() + aDiffLast);
					}
				}

				int aLengthQrsFinal = aQrsFinal.size();

				int[] aQRSFinal = new int[aLengthQrsFinal];

				for (int i = 0; i < aLengthQrsFinal; i++) {
					aQRSFinal[i] = aQrsFinal.get(i);
				}
				int aNoDetectionFLag = 1;

				return new Object[] { aQRSFinal, aInterpolatedLength, aNoDetectionFLag };
			} else {

				return new Object[] { new int[] {}, 0, 1 };
			}

		}

	}

	public int FindOverlapMqrsLoc(int[] qrsM, int a, int b) {
		// TODO Auto-generated method stub
		int lenM = qrsM.length;
		for (int k = 0; k < lenM - 1; k++) {
			if (qrsM[k] > a) {
				if (qrsM[k] < b && qrsM[k + 1] < b) {
					return -1;
				} else {
					return qrsM[k];
				}
			}
		}
		return -1;
	}

}
