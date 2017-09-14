package com.sattvamedtech.fetallite.signalproc;

/**
 * 
 * @author kishoresubramanian
 *
 */
public class FQRSDetection {
	private static int mLength;
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();
	static boolean i1Flag = false;
	static boolean i2Flag = false;
	static boolean i3Flag = false;
	static boolean i4Flag = false;

	double[] mChannel1;
	double[] mChannel2;
	double[] mChannel3;
	double[] mChannel4;

	int[] mQRS1, mQRS2, mQRS3, mQRS4;

	/**
	 * fQRS : Determines the fetal QRS in ICA2 output
	 * 
	 * @param iInput
	 *            : (double[][]) Input data
	 * @param iQrsM
	 *            : (int[]) Maternal QRS locations of the current iteration
	 * @param iInterpolatedLength
	 *            : (int) Interpolated length at the end of previous iteration
	 * @param iQRSLast
	 *            : (int) Last QRS location detected in previous iteration
	 * @param iRRMeanLast
	 *            : (int) RR mean using the last 4 QRS peaks detected in
	 *            previous iteration
	 * @param iNoDetectionFlag
	 *            : (int) '1' if no single channel has been determined to
	 *            contain possible QRS locations else '0'
	 * @return Object[] { aQRSFinal, aInterpolatedLength, aNoDetectionFLag } :
	 *         {(int[]), (int), (int)} Return QRS selected and update Flags.
	 * @throws Exception
	 *             : Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	public Object[] fQRS(double[][] iInput, int[] iQrsM, int iInterpolatedLength, int iQRSLast, double iRRMeanLast,
			int iNoDetectionFlag) throws Exception {

		final double[][] aInput = iInput;
		mLength = iInput.length;
		if (mLength > 0) {
			if (iInput[0].length == 4) {
				mChannel1 = new double[mLength];
				mChannel2 = new double[mLength];
				mChannel3 = new double[mLength];
				mChannel4 = new double[mLength];

				Thread aQRSDet1 = new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < mLength; i++) {
							mChannel1[i] = aInput[i][0];
						}
						try {
							mQRS1 = fetalQRS(mChannel1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						i1Flag = true;

					}
				});

				Thread aQRSDet2 = new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < mLength; i++) {
							mChannel2[i] = aInput[i][1];
						}
						try {
							mQRS2 = fetalQRS(mChannel2);
						} catch (Exception e) {
							e.printStackTrace();
						}
						i2Flag = true;

					}
				});
				Thread aQRSDet3 = new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < mLength; i++) {
							mChannel3[i] = aInput[i][2];
						}
						try {
							mQRS3 = fetalQRS(mChannel3);
						} catch (Exception e) {
							e.printStackTrace();
						}
						i3Flag = true;

					}
				});
				Thread aQRSDet4 = new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < mLength; i++) {
							mChannel4[i] = aInput[i][3];
						}
						try {
							mQRS4 = fetalQRS(mChannel4);
						} catch (Exception e) {
							e.printStackTrace();
						}
						i4Flag = true;

					}
				});
				aQRSDet1.start();
				aQRSDet2.start();
				aQRSDet3.start();
				aQRSDet4.start();

				while (true) {
					Thread.sleep(1);
					if (i1Flag && i2Flag && i3Flag && i4Flag) {
						i1Flag = false;
						i2Flag = false;
						i3Flag = false;
						i4Flag = false;
						break;
					}
				}

				Object[] qrsSelectionInputs = mMatrixFunctions.channelSelection(mQRS1, mQRS2, mQRS3, mQRS4,
						Constants.FQRS_VARIANCE_THRESHOLD, Constants.FQRS_RR_LOW_TH, Constants.FQRS_RR_HIGH_TH);

				QRSSelection aQrsSelect = new QRSSelection();
				Object[] aQrsSelected = aQrsSelect.qrsSelection((int[]) qrsSelectionInputs[0],
						(int) qrsSelectionInputs[1], iQrsM, iInterpolatedLength, iQRSLast, iRRMeanLast,
						iNoDetectionFlag);

				return aQrsSelected;
			} else {
				throw new Exception("Input mus have four channels : fQRS");
			}
		} else {
			throw new Exception("Enter a non-empty array : fQRS");
		}
	}

	/**
	 * fetalQRS : Determines possible QRS locations in the given data
	 * 
	 * @param iChannel
	 *            : (double[]) Input data
	 * @return : (int[]) Possible maternal QRS in the given input
	 * @throws Exception:
	 *             Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */

	private int[] fetalQRS(double[] iChannel) throws Exception {

		if (mLength > 0) {
			// differentiate and square
			mMatrixFunctions.convolutionQRSDetection(iChannel, Constants.QRS_DERIVATIVE);

			/**
			 * FIltering 2 - 3.5 Hz
			 */

			double bhigh[] = new double[2];
			for (int i0 = 0; i0 < 2; i0++) {
				bhigh[i0] = Constants.FQRS_BHIGH0 + Constants.FQRS_BHIGH_SUM * (double) i0;
			}

			mMatrixFunctions.filterLoHi(iChannel, Constants.FQRS_AHIGH, bhigh, Constants.FQRS_ZHIGH);

			// Have to add 6th order filter

			mMatrixFunctions.filterLoHi(iChannel, Constants.FQRS_ALOW, Constants.FQRS_BLOW, Constants.FQRS_ZLOW);

			/**
			 * Integrator
			 */

			double[] integrator = new double[mLength];

			double sum = 0;

			for (int j = 0; j < Constants.FQRS_WINDOW; j++) {
				sum = sum + iChannel[Constants.FQRS_WINDOW - j - 1];
			}
			integrator[Constants.FQRS_WINDOW - 1] = sum / Constants.FQRS_WINDOW;

			for (int i = Constants.FQRS_WINDOW; i < mLength; i++) {
				integrator[i] = integrator[i - 1]
						+ (-iChannel[i - Constants.FQRS_WINDOW] + iChannel[i]) / Constants.FQRS_WINDOW;
			}
			/**
			 * Find the 90% and 10% value to find the threshold
			 */

			double threshold = mMatrixFunctions.setIntegratorThreshold(integrator, Constants.FQRS_INTEGRATOR_THRESHOLD_SCALE);

			/**
			 * Peak Detection , not sure about return type have to change it
			 * Just return the first column of the Maxtab. No need the
			 * magnitudes.
			 */
			int peakLoc[] = mMatrixFunctions.peakDetection(integrator, threshold);

			int delay = Constants.FQRS_WINDOW / 2;
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
		{
			throw new Exception("Enter a non-empty array : fetalQRS");
		}
	}

}