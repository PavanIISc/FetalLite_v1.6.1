package com.sattvamedtech.fetallite.signalproc;

/**
 * @version SFL 1.0
 * @author kishoresubramanian
 *
 */
public class MQRSDetection {
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();
	static boolean i1Flag = false;
	static boolean i2Flag = false;
	static boolean i3Flag = false;
	static boolean i4Flag = false;

	private static int mLength;

	double[] mChannel1;
	double[] mChannel2;
	double[] mChannel3;
	double[] mChannel4;

	int[] mQRS1, mQRS2, mQRS3, mQRS4;

	/**
	 * mQRS : Determines the maternal QRS in ICA1 output.
	 * 
	 * @param iInput
	 *            : (double[][]) ICA1 Output data
	 * @return : (int[]) maternal QRS locations array
	 * @throws Exception:
	 *             Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	public int[] mQRS(double[][] iInput) throws Exception {

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
							mQRS1 = maternalQRS(mChannel1);
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
							mQRS2 = maternalQRS(mChannel2);
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
							mQRS3 = maternalQRS(mChannel3);
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
							mQRS4 = maternalQRS(mChannel4);
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
						Constants.MQRS_VARIANCE_THRESHOLD, Constants.MQRS_RR_LOW_TH, Constants.MQRS_RR_HIGH_TH);

				QRSSelection aQrsSelect = new QRSSelection();
				Object[] aQrsSelected = aQrsSelect.qrsSelection((int[]) qrsSelectionInputs[0],
						(int) qrsSelectionInputs[1], new int[] {}, 0, 0, 0, -1);

				return (int[]) aQrsSelected[0];
			} else {
				throw new Exception("Input must have 4 channels : mQRS");
			}
		} else

		{
			throw new Exception("Input a non-empty array : mQRS");
		}
	}

	/**
	 * maternalQRS : Determines possible QRS locations in the given data
	 * 
	 * @param iChannel
	 *            : (double[]) Input data
	 * @return : (int[]) Possible maternal QRS in the given input
	 * @throws Exception:
	 *             Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	private int[] maternalQRS(double[] iChannel) throws Exception {

		if (mLength > 0) {
			// differentiate and square
			mMatrixFunctions.convolutionQRSDetection(iChannel, Constants.QRS_DERIVATIVE);

			/**
			 * FIltering 0.8- 3Hz
			 */

			double aBhigh[] = new double[2];
			double aAhigh[] = new double[2];
			for (int i0 = 0; i0 < 2; i0++) {
				aBhigh[i0] = Constants.MQRS_BHIGH0 + Constants.MQRS_BHIGH_SUM * (double) i0;
				aAhigh[i0] = 1.0 + Constants.MQRS_AHIGH_SUM * (double) i0;
			}

			mMatrixFunctions.filterLoHi(iChannel, aAhigh, aBhigh, Constants.MQRS_ZHIGH);

			double aAlow[] = new double[2];
			for (int i0 = 0; i0 < 2; i0++) {
				aAlow[i0] = 1.0 + Constants.MQRS_ALOW_SUM * (double) i0;
			}

			mMatrixFunctions.filterLoHi(iChannel, aAlow, Constants.MQRS_BLOW, Constants.MQRS_ZLOW);

			/**
			 * Integrator
			 */

			double[] aIntegrator = new double[mLength];

			double aSum = 0;

			for (int j = 0; j < Constants.MQRS_WINDOW; j++) {
				aSum = aSum + iChannel[Constants.MQRS_WINDOW - j - 1];
			}
			aIntegrator[Constants.MQRS_WINDOW - 1] = aSum / Constants.MQRS_WINDOW;

			for (int i = Constants.MQRS_WINDOW; i < mLength; i++) {
				aIntegrator[i] = aIntegrator[i - 1]
						+ (-iChannel[i - Constants.MQRS_WINDOW] + iChannel[i]) / Constants.MQRS_WINDOW;
			}

			/**
			 * Find the 90% and 10% value to find the threshold
			 */

			double aThreshold = mMatrixFunctions.setIntegratorThreshold(aIntegrator,
					Constants.MQRS_INTEGRATOR_THRESHOLD_SCALE);

			/**
			 * Peak Detection , Determines the peaks of the signal Just return
			 * the first column of the Maxtab. No need the magnitudes.
			 */
			int aPeakLoc[] = mMatrixFunctions.peakDetection(aIntegrator, aThreshold);

			int aDelay = Constants.MQRS_WINDOW / 2;
			int aPeakLength = aPeakLoc.length;
			// Check the starting peak is greater than delay/2 or remove nIt
			int aCount = 0;
			for (int i = 0; i < aPeakLength; i++) {
				if (aPeakLoc[i] < aDelay + 2) {
					aCount = aCount + 1;
				}
			}

			int aLenQrs = aPeakLength - aCount;
			int[] aQrs = new int[aLenQrs];
			for (int i = 0; i < aLenQrs; i++) {
				aQrs[i] = aPeakLoc[i + aCount] - aDelay;
			}

			return aQrs;
		} else {
			throw new Exception("Enter a non-empty array : maternalQRS");
		}
	}

}