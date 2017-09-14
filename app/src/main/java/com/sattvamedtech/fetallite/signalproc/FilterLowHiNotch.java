package com.sattvamedtech.fetallite.signalproc;
//package hb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version SFL 1.0
 * @author kishoresubramanian
 *
 */
public class FilterLowHiNotch {
	static boolean i0Flag = false;
	static boolean i1Flag = false;
	static boolean i2Flag = false;
	static boolean i3Flag = false;

	MatrixFunctions mMatrixFunctions = new MatrixFunctions();

	/**
	 * filterParallel : Performs filtering of each channel by parallelizing into
	 * 4 threads.
	 * 
	 * @param iInput
	 *            : (double[][]) Input data (length greater than FILTERNFACT3)
	 * @return : (double[][]) Filtered data
	 * @exception :
	 *                Throws appropriate exception if the input arguments do not
	 *                meet the requirements
	 */
	public double[][] filterParallel(double[][] iInput) throws Exception {
		if (iInput.length > Constants.FILTER_NFACT3) {
			final double[][] aFinalInput = iInput;

			final double[][] aFinalOutput = new double[Constants.NO_OF_SAMPLES][Constants.NO_OF_CHANNELS];

			ExecutorService aExec = Executors.newFixedThreadPool(Constants.NO_OF_CHANNELS);
			for (int cols = 0; cols < Constants.NO_OF_CHANNELS; cols++) {
				final int aFinalCols = cols;
				final double[] aFinalChannel = new double[Constants.NO_OF_SAMPLES];
				aExec.submit(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < Constants.NO_OF_SAMPLES; i++) {
							aFinalChannel[i] = aFinalInput[i][aFinalCols];
						}
						try {
							filterLowHiNotch(aFinalChannel);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						for (int i = 0; i < Constants.NO_OF_SAMPLES; i++) {
							aFinalOutput[i][aFinalCols] = aFinalChannel[i];
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
			// System.out.println("***Waiting for process Filter Calculation to
			// finish : "+(new
			// java.text.SimpleDateFormat("H:mm:ss:SSS")).format(java.util.Calendar.getInstance().getTime()));
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
				aExec.shutdown();
			}
			return aFinalOutput;
		} else {
			throw new Exception("Input must be of size greater than 6 : filterParallel");
		}
	}

	/**
	 * filterLowHiNotch : Filtering of each channel. This method is
	 * executed parallel for each channel
	 * 
	 * @param iChannel
	 *            : (double[]) Input data (length greater than FILTERNFACT3)
	 * @throws Exception
	 *             : Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	private void filterLowHiNotch(double[] iChannel) throws Exception {

		if (iChannel.length > Constants.FILTER_NFACT3) {

			double aAhigh[] = new double[2];
			double aBhigh[] = new double[2];
			for (int i0 = 0; i0 < 2; i0++) {
				aBhigh[i0] = Constants.FILTER_BHIGH0 + Constants.FILTER_BHIGH_SUM * (double) i0;
				aAhigh[i0] = 1.0 + Constants.FILTER_AHIGH_SUM * (double) i0;
			}

			mMatrixFunctions.filterLoHi(iChannel, aAhigh, aBhigh, Constants.FILTER_ZHIGH);

			mMatrixFunctions.filterLoHi(iChannel, Constants.FILTER_ALOW, Constants.FILTER_BLOW, Constants.FILTER_ZLOW);

			mMatrixFunctions.filterNotch(iChannel, Constants.FILTER_ANOTCH, Constants.FILTER_BNOTCH,
					Constants.FILTER_ZNOTCH1, Constants.FILTER_ZNOTCH2);
		} else {
			throw new Exception("Input must be of size greater than 6 : filterLowHiNotchParallel");
		}
	}

}