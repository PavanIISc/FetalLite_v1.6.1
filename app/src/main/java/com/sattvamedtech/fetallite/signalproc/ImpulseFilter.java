package com.sattvamedtech.fetallite.signalproc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @version 27th June, 2017
 * 			Impulse Elimination : Fixed ArrayOutOfBounds Error due to 'aIndexInitial'.
 * 								Added Condition to check 'aIndexInitial > 0'
 *
 * @version 22rd June, 2017
 * 			Impulse Elimination : Fixed ArrayOutOfBounds Error due to 'aIndexFinal'.
 * 								Added Condition to check 'aIndexFinal+1 < Constants.NO_OF_SAMPLES'
 * 
 * @version 24th May, 2017
 * @author kishoresubramanian
 *
 */
public class ImpulseFilter {
	/**
	 * Input = N x 4 signal
	 */
	static boolean i0Flag = false;
	static boolean i1Flag = false;
	static boolean i2Flag = false;
	static boolean i3Flag = false;

	MatrixFunctions mMatrixFunctions = new MatrixFunctions();
	double[][] mInputBoundryCheck = new double[Constants.NO_OF_SAMPLES][Constants.NO_OF_CHANNELS];
	int mWindowMedian = (int) Math.floor((Constants.IMPULSE_WINDOW_PERCENT * Constants.FS));
	double[][] mImpulseRemoved = new double[Constants.NO_OF_SAMPLES][Constants.NO_OF_CHANNELS];

	/**
	 * impulseFilterParallel : Performs impulse filtering of each channel by
	 * parallelizing into 4 threads.
	 * 
	 * @param iInput
	 *            : (double[][]) Input data
	 * @return : (double[][]) Filtered data
	 * @throws Exception:
	 *             Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	public double[][] impulseFilterParallel(double[][] iInput) throws Exception {
		// creating a copy of input array to avoid errors due to java copy by
		// reference.
		if (iInput.length > 0) {
			if (iInput.length == Constants.NO_OF_SAMPLES && iInput[0].length == Constants.NO_OF_CHANNELS) {
				mMatrixFunctions.copy(iInput, mInputBoundryCheck);

				setBoundryCondition();

				ExecutorService aExec = Executors.newFixedThreadPool(Constants.NO_OF_CHANNELS);
				try {
					for (int aCols = 0; aCols < Constants.NO_OF_CHANNELS; aCols++) {
						final double[][] aFinalInputBoundryCheck = mInputBoundryCheck;
						final int aFinalCols = aCols;
						final double[] aChannelInputElimitation = new double[Constants.NO_OF_SAMPLES];

						aExec.submit(new Runnable() {
							@Override
							public void run() {
								for (int i = 0; i < Constants.NO_OF_SAMPLES; i++) {
									aChannelInputElimitation[i] = aFinalInputBoundryCheck[i][aFinalCols];
								}

								try {
									impulseElimination(aChannelInputElimitation, Constants.IMPULSE_THRESHOLD,
											mWindowMedian, Constants.IMPULSE_PERCENTILE);
								} catch (Exception e) {

									e.printStackTrace();
								}

								for (int i = 0; i < Constants.NO_OF_SAMPLES; i++) {
									mImpulseRemoved[i][aFinalCols] = aChannelInputElimitation[i];
								}

								if (aFinalCols == 0)
									i0Flag = true;
								else if (aFinalCols == 1)
									i1Flag = true;
								else if (aFinalCols == 2)
									i2Flag = true;
								else if (aFinalCols == 3)
									i3Flag = true;
							}
						});

					}
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
					aExec.shutdown();
				}

				return mImpulseRemoved;
			} else {
				throw new Exception("Enter arrays of required dimension : impulseFilterParallel");
			}
		} else {
			throw new Exception("Enter non-empty array : impulseFilterParallel");
		}
	}
	// main ends

	/**
	 * setBoundaryCondition : Set the first ten samples to the median of the
	 * following three
	 * 
	 * @throws Exception
	 *             : This handles the exception thrown in 'findMedian' method
	 */
	private void setBoundryCondition() throws Exception {
		for (int i = 0; i < Constants.NO_OF_CHANNELS; i++) {
			double[] aTempBoundryArray = new double[Constants.IMPULSE_INITIAL_MEDIAN_SIZE];
			int aIndex = 0;
			for (int j = Constants.IMPULSE_NO_INITIAL_SAMPLES; j < Constants.IMPULSE_NO_INITIAL_SAMPLES
					+ Constants.IMPULSE_INITIAL_MEDIAN_SIZE; j++) {
				aTempBoundryArray[aIndex] = mInputBoundryCheck[j][i];
				++aIndex;
			}

			double aMedianTempArray = mMatrixFunctions.findMedian(aTempBoundryArray);

			for (int j = 0; j < Constants.IMPULSE_NO_INITIAL_SAMPLES; j++) {
				mInputBoundryCheck[j][i] = aMedianTempArray;
			}
		}
	}

	/**
	 * Impulse Elimination function : This is the function which is executed in
	 * parallel to remove impulse noise.
	 * 
	 * 
	 * @param iInput
	 *            : (double[]) containing the raw data
	 * @param iThreshold
	 *            : (int) The scale to obtain the threshold
	 * @param iWindow
	 *            : (int) Window size to find median
	 * @param iPercentile
	 *            : (int) Percentage to check the distribution tail
	 * @throws Exception
	 *             : Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	private void impulseElimination(double[] iInput, int iThreshold, int iWindow, int iPercentile) throws Exception {

		int aLengthInput = iInput.length;

		if (aLengthInput > 0) {

			if (iWindow > 0) {

				if (iPercentile > -1 && iPercentile < 100) {
					if (iWindow % 2 == 0) {
						iWindow++;
					}
					double aMedianInput[] = medianFilter1D(iInput, iWindow);
					double[] aAbsoluteMedianInput = new double[aLengthInput];

					// Finding absolute value of the difference of median and
					// input.Helps to
					// find the threshold for the filter.
					for (int i = 0; i < aLengthInput; i++) {
						aAbsoluteMedianInput[i] = Math.abs(iInput[i] - aMedianInput[i]);
					}

					int[] aIndex = mMatrixFunctions.findingPositiveElementsIndex(aAbsoluteMedianInput);

					double[] aTempAbsoluteMedian = new double[aIndex.length];

					for (int r = 0; r < aIndex.length; r++) {
						aTempAbsoluteMedian[r] = aAbsoluteMedianInput[aIndex[r]];
					}

					double aMaxAbsoluteMedian = mMatrixFunctions.findPercentileValue(aTempAbsoluteMedian, iPercentile);

					double aThresholeAbsolute = iThreshold * aMaxAbsoluteMedian;

					double[] aMedianThresholdedArray = new double[aAbsoluteMedianInput.length];

					for (int i = 0; i < aAbsoluteMedianInput.length; i++) {
						aMedianThresholdedArray[i] = (aAbsoluteMedianInput[i] - (aThresholeAbsolute));
					}
					int aIndexThresholded[] = mMatrixFunctions.findingPositiveElementsIndex(aMedianThresholdedArray);

					if (aIndexThresholded != null) {
						int i = 0;
						while (i < aIndexThresholded.length) {
							int aIndexInitial = aIndexThresholded[i];
							while ((i < aIndexThresholded.length - 1)
									&& (aIndexThresholded[i + 1] == aIndexThresholded[i] + 1)) {
								i = i + 1;
							}
							double aIndexFinal = aIndexThresholded[i];
							if (aIndexFinal + 1 < Constants.NO_OF_SAMPLES && aIndexInitial > 0) {
								double aTempMedian = (iInput[(int) Math.max(aIndexInitial - 1, 1)]
										+ iInput[(int) (Math.min(aIndexFinal + 1, aLengthInput))]) / 2;

								for (int ind = aIndexInitial; ind <= aIndexFinal; ind++) {
									iInput[ind - 1] = aTempMedian;
								}
							}
							i = i + 1;
						}
					}
				} else {
					throw new Exception("Percentile should be between 0-99 : impulseElimination");
				}
			} else {
				throw new Exception("Window size has to be positive : impulseElimination");
			}
		} else {
			throw new Exception("Enter non-empty array : impulseElimination");
		}

	}

	/**
	 * medianFilter1D : Append median of the first/last 'Window' values are
	 * assumed to the left and right of 'Input'. Find median across the signal
	 * with moving window of size 'Window' and update.
	 * 
	 * @param iInputMedian1D
	 *            : (double[]) Input data
	 * @param iWindow
	 *            : (int) Window size to find median
	 * @return : (double[]) Returns the median filtered output
	 * @throws Exception
	 *             : Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	private double[] medianFilter1D(double[] iInputMedian1D, int iWindow) throws Exception {

		int aLengthInput = iInputMedian1D.length;
		if (aLengthInput > 0) {

			if (iWindow > 0) {

				int aLengthExt = iWindow / 2;

				aLengthExt = Math.min(aLengthExt, aLengthInput);

				double[] aExtensionInitial = new double[iWindow];
				double[] aExtensionFinal = new double[iWindow];
				for (int i = 0; i < iWindow; i++) {
					aExtensionInitial[i] = iInputMedian1D[i];
					aExtensionFinal[i] = iInputMedian1D[aLengthInput - (iWindow - i)];
				}

				double aMedianExtensionInitial = mMatrixFunctions.findMedian(aExtensionInitial);
				double aMedianExtensionFinal = mMatrixFunctions.findMedian(aExtensionFinal);

				int aLengthBoundryExtended = aLengthInput + 2 * aLengthExt;
				double[] aInputBoundryExtended = new double[aLengthBoundryExtended];

				for (int i = 0; i < aLengthExt; i++) {
					aInputBoundryExtended[i] = aMedianExtensionInitial;
					aInputBoundryExtended[aLengthInput + aLengthExt + i] = aMedianExtensionFinal;
				}

				for (int i = 0; i < aLengthInput; i++) {
					aInputBoundryExtended[aLengthExt + i] = iInputMedian1D[i];
				}

				// median1D function starts here

				int aWindow;
				if (iWindow % 2 == 0) { // if even
					aWindow = iWindow / 2;
				} else {
					aWindow = (iWindow - 1) / 2;
				}

				double aInputFinalExtension[] = new double[2 * aWindow + aLengthBoundryExtended];
				for (int i = 0; i < aLengthBoundryExtended; i++) {
					aInputFinalExtension[i + aWindow] = aInputBoundryExtended[i];
				}

				double aMeidanOutExtended[] = new double[aLengthBoundryExtended];
				double[] aMedianArray = new double[iWindow];

				for (int i = 0; i < aLengthBoundryExtended; i++) {
					int index = 0;
					for (int k = i; k < i + iWindow; k++) {
						aMedianArray[index] = aInputFinalExtension[k];
						index = index + 1;
					}
					aMeidanOutExtended[i] = mMatrixFunctions.findMedian(aMedianArray);
				}

				double[] aMedianOutput = new double[aLengthInput];

				for (int i = 0; i < aLengthInput; i++) {
					aMedianOutput[i] = aMeidanOutExtended[i + aLengthExt];
				}

				return aMedianOutput;
			} else {
				throw new Exception("Window Size has to be positive : medianFilter1D");
			}
		} else {
			throw new Exception("Enter non-empty array : medianFilter1D");
		}

	} // end function

}