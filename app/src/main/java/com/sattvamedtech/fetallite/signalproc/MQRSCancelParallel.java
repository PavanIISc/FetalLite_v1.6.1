package com.sattvamedtech.fetallite.signalproc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 
 * @author kishoresubramanian
 *
 */
public class MQRSCancelParallel {
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();

	static boolean i0Flag = false;
	static boolean i1Flag = false;
	static boolean i2Flag = false;
	static boolean i3Flag = false;

	static double[][] mApproxSignal = null;
	static int mNoSamplesQRS, mNoQrs;

	/**
	 * pcaParallized : Perform PCA on each channel in separate threads
	 * 
	 * @param iNoQrs
	 *            : (int) Number of ECG signals
	 * @param iQrsIndexArrI
	 *            : (int[]) Start index of each ECG wave
	 * @param iQrsIndexArrF
	 *            : (int[]) End index of each ECG wave
	 * @param iRowWeighted
	 *            : (double[][]) Each ECG wave extracted multiplied with a
	 *            trapezodial window
	 * @param iRowextract
	 *            : (double[][]) Extract of one ECG wave
	 * @param iInputPCA
	 *            : (double[][]) Signal containing all ECG.
	 * @param iWeightWindowFunction
	 *            : (double[][]) Trapezodial window
	 * @param iNoSamplesQRS
	 *            : (int) Length of each ECG wave
	 * @param iSvdExtract
	 *            : (double[][]) Each column contains one ECG
	 * @param iApproxmSignal:
	 *            (double[][]) Store the rank-3 approximation of each channel
	 * @return : (double[][]) mApproxSignal - Contains the Rank-3 approximation.
	 */
	private double[][] pcaParallized(int iNoQrs, int[] iQrsIndexArrI, int[] iQrsIndexArrF, double[][] iRowWeighted,
			double[][] iRowextract, double[][] iInputPCA, double[][] iWeightWindowFunction, int iNoSamplesQRS,
			double[][] iSvdExtract, double[][] iApproxmSignal) {

		ExecutorService exec = Executors.newFixedThreadPool(Constants.NO_OF_CHANNELS);
		final int aNo_of_channels = Constants.NO_OF_CHANNELS;
		mNoSamplesQRS = iNoSamplesQRS;
		mNoQrs = iNoQrs;

		final int[] aQrsIndexArrI = iQrsIndexArrI;
		final int[] aQrsIndexArrF = iQrsIndexArrF;
		final double[][] aWeightWindowFunction = iWeightWindowFunction;
		final double[][] aInputPCA = iInputPCA;
		mApproxSignal = iApproxmSignal;

		try {
			for (int it = 0; it < aNo_of_channels; it++) {
				final int ait = it;
				exec.submit(new Runnable() {

					@Override
					public void run() {

						double[][] aPcaExtract = new double[mNoSamplesQRS][mNoQrs];
						double[][] aRowExtract = new double[1][mNoSamplesQRS];
						double[][] aRowWeighted = new double[1][mNoSamplesQRS];

						for (int i = 0; i < mNoQrs; i++) {
							int aQrsIndexI = aQrsIndexArrI[i];
							int aQrsIndexF = aQrsIndexArrF[i];
							try {
								aRowExtract = mMatrixFunctions.subMatrix(aInputPCA, aQrsIndexI, aQrsIndexF, ait, ait);
								aRowWeighted = mMatrixFunctions.elementWiseMultiply(aRowExtract, aWeightWindowFunction);

								for (int j = 0; j < mNoSamplesQRS; j++) {
									aPcaExtract[j][i] = aRowWeighted[j][0];
								}

							} catch (Exception e) {

								e.printStackTrace();
							}
						}

						PCARank3 aPCA = new PCARank3();

						double[][] aApproxSignal;
						try {
							aApproxSignal = aPCA.pca(aPcaExtract);

							// putting back the approximation into a single
							// channel
							double[][] aApproxSignalTemp = new double[mNoSamplesQRS][1];
							double[][] aApproxSignalTemp1 = new double[mNoSamplesQRS][1];
							for (int iq = 0; iq < mNoQrs; iq++) {
								int aIwq = aQrsIndexArrI[iq];
								int aFwq = aQrsIndexArrF[iq];

								aApproxSignalTemp = mMatrixFunctions.subMatrix(aApproxSignal, 0, mNoSamplesQRS - 1, iq,
										iq);

								aApproxSignalTemp1 = mMatrixFunctions.elementWiseDivide(aApproxSignalTemp,
										aWeightWindowFunction);

								for (int k = aIwq; k <= aFwq; k++) {
									mApproxSignal[k][ait] = aApproxSignalTemp1[k - aIwq][0];
								}
							} // end approx single channel
						} catch (Exception e) {

							e.printStackTrace();
						}
						double aDifferenceValue = 0;
						double aPercentValue = 0;
						for (int iq = 1; iq < mNoQrs; iq++) {
							int aQrsIndexF = aQrsIndexArrF[iq - 1];
							int aQrsIndexI = aQrsIndexArrI[iq];
							if (aQrsIndexI > aQrsIndexF) {
								aDifferenceValue = mApproxSignal[aQrsIndexI][ait] - mApproxSignal[aQrsIndexF][ait];
								aPercentValue = aDifferenceValue / (aQrsIndexI - aQrsIndexF);
								for (int it = aQrsIndexF + 1; it < aQrsIndexI; it++) {
									mApproxSignal[it][ait] = mApproxSignal[aQrsIndexF][ait]
											+ aPercentValue * (it - aQrsIndexF);
								}
							}
						} // end smoothening

						if (ait == 0)
							i0Flag = true;
						else if (ait == 1)
							i1Flag = true;
						else if (ait == 2)
							i2Flag = true;
						else if (ait == 3)
							i3Flag = true;

					}
				});
			}

			while (true) {
				Thread.sleep(1);
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

		return mApproxSignal;
	}

	/**
	 * cancel : Performs a rank-3 PCA of maternal ECG for each channel of
	 * filtered signal by extracting same length maternal ECG using the maternal
	 * QRS location obtained.
	 * 
	 * @param iInput
	 *            : (double[][]) Filtered signal (Nx4)
	 * @param iQRSm
	 *            : (int[]) Maternal QRS locations (length > 5)
	 * @return : (double[][]) Maternal cancelled signal
	 * @throws Exception:
	 *             Throws Exception if the input arguments do not meet the
	 *             requirements
	 */
	public double[][] cancel(double[][] iInput, int[] iQRSm) throws Exception {
		// input = Nx4

		int aNoQRSm = iQRSm.length;
		if (iInput.length > 0) {
			if (aNoQRSm > 5) {

				double[] aRRms = new double[aNoQRSm - 1]; // RR in milli seconds
				for (int i = 1; i < aNoQRSm; i++) {
					aRRms[i - 1] = (iQRSm[i] - iQRSm[i - 1]) / (double) Constants.FS;
				}

				double aRRmean = mMatrixFunctions.findMeanBetweenDistributionTails(aRRms, Constants.CANCEL_PERCI, Constants.CANCEL_PERCF);

				/**
				 * Initialize the no of points before and after QRS
				 */
				int aNoSamplesBeforeQRS = 0;
				int aNoSamplesAfterQRS = 0;
				if (Math.ceil(Constants.CANCEL_QRS_BEFORE_PERC * Constants.FS) == Constants.CANCEL_QRS_BEFORE_PERC
						* Constants.FS) {
					aNoSamplesBeforeQRS = (int) Math.ceil(Constants.CANCEL_QRS_BEFORE_PERC * Constants.FS);
				} else {
					aNoSamplesBeforeQRS = (int) Math.ceil(Constants.CANCEL_QRS_BEFORE_PERC * Constants.FS) - 1;
				}
				double aNoSamplesTemp = Constants.CANCEL_QRS_AFTER_PERC * (aRRmean - 0.1);

				if (aNoSamplesTemp > Constants.CANCEL_QRS_AFTER_TH) {
					aNoSamplesTemp = Constants.CANCEL_QRS_AFTER_TH;
				}
				if (Math.ceil(aNoSamplesTemp * Constants.FS) == aNoSamplesTemp * Constants.FS) {
					aNoSamplesAfterQRS = (int) Math.ceil(aNoSamplesTemp * Constants.FS);
				} else {
					aNoSamplesAfterQRS = (int) Math.ceil(aNoSamplesTemp * Constants.FS) - 1;
				}

				int aNoSamplesQRS = 1 + aNoSamplesBeforeQRS + aNoSamplesAfterQRS;

				/**
				 * Extend signals to manage first QRS
				 */
				int aInitialQrsIndexArr = 1;

				/**
				 * Extend Signals to manage first QRS
				 */
				// Always the first qrsM > 120, so take only else condition
				int aNoSamplesToLeft = 0;
				if (aNoSamplesBeforeQRS + 1 - iQRSm[0] > 0) {
					aNoSamplesToLeft = aNoSamplesBeforeQRS + 1 - iQRSm[0];
				}

				double[][] aRowExtract = mMatrixFunctions.subMatrix(iInput, 0, 0, 0, Constants.NO_OF_CHANNELS - 1);

				double[][] aRowExtension = mMatrixFunctions.repmat(aRowExtract, aNoSamplesToLeft);
				double[][] aInputExtension = mMatrixFunctions.verticalConcat(aRowExtension, iInput);

				/**
				 * Extend signals to manage last QRS
				 */
				// Always the last qrsM < len - 140, so take only else condition

				int aFinalQrsIndexArr = aNoQRSm;
				double[] aRRMedTemp = new double[Constants.CANCEL_NO_SAMPLES_END];
				double aRRMeanSum = 0;
				for (int i = 0; i < Constants.CANCEL_NO_SAMPLES_END; i++) {
					aRRMedTemp[i] = aRRms[aRRms.length - 1 - i];
					aRRMeanSum = aRRMedTemp[i] + aRRMeanSum;
				}
				double aRRmsMean = aRRMeanSum / Constants.CANCEL_NO_SAMPLES_END;
				double aRRmsMedian = mMatrixFunctions.findMedian(aRRMedTemp);

				double aTempD = (1 - Constants.CANCEL_LASTQRS_TH_HIGH_PERC) * Constants.FS * aRRmsMedian;
				int aTempI = 0;
				if (Math.ceil(aTempD) == aTempD) {
					aTempI = (int) Math.ceil(aTempD);
				} else {
					aTempI = (int) Math.ceil(aTempD) - 1;
				}
				double aNoSamplesAddedEndTemp = 0;
				int aNoSamplesAddedEnd = 0;

				if (iQRSm[aFinalQrsIndexArr - 1] + aTempI < Constants.NO_OF_SAMPLES) {
					// find max
					if (Constants.CANCEL_LASTQRS_TH_LOW_PERC * Constants.FS > Constants.CANCEL_LASTQRS_TH_HIGH_PERC
							* Constants.FS * aRRmsMean) {
						aNoSamplesAddedEndTemp = Constants.CANCEL_LASTQRS_TH_LOW_PERC * Constants.FS;
					} else {
						aNoSamplesAddedEndTemp = Constants.CANCEL_LASTQRS_TH_HIGH_PERC * Constants.FS * aRRmsMean;
					}

					if (Math.ceil(aNoSamplesAddedEndTemp) == aNoSamplesAddedEndTemp) {
						aNoSamplesAddedEnd = (int) Math.ceil(aNoSamplesAddedEndTemp);
					} else {
						aNoSamplesAddedEnd = (int) Math.ceil(aNoSamplesAddedEndTemp) - 1;
					}

					int aRowToExtend = aInputExtension.length - 1 - aNoSamplesAddedEnd - 1;

					// Do replicate the row and add it to the input extension
					double[][] aRowExtended = mMatrixFunctions.subMatrix(aInputExtension, aRowToExtend, aRowToExtend, 0,
							aInputExtension[0].length - 1);

					double[][] aInputExtendFinal = mMatrixFunctions.repmat(aRowExtended, aNoSamplesAddedEnd);
					for (int i = 0; i < aNoSamplesAddedEnd; i++) {
						for (int j = 0; j < aInputExtension[0].length; j++) {
							aInputExtension[i + aRowToExtend + 2][j] = aInputExtendFinal[i][j];
						}
					}

				} // end if qrsm[qf]
				/**
				 * no of samples to add to right of the signal
				 */
				int aNoSamplesToRight = 0;

				if (iQRSm[aFinalQrsIndexArr - 1] + aNoSamplesAfterQRS - Constants.NO_OF_SAMPLES > -1) {
					aNoSamplesToRight = iQRSm[aFinalQrsIndexArr - 1] + aNoSamplesAfterQRS - Constants.NO_OF_SAMPLES + 1;
				}
				double[][] aInputSVD = new double[aInputExtension.length
						+ aNoSamplesToRight][aInputExtension[0].length];
				for (int i = 0; i < aInputExtension.length; i++) {
					for (int j = 0; j < aInputExtension[0].length; j++) {
						aInputSVD[i][j] = aInputExtension[i][j];
					}
				}
				// Do extension if required.
				if (aNoSamplesToRight > 0) {
					double[][] aRowExtractRight = mMatrixFunctions.subMatrix(iInput, Constants.NO_OF_SAMPLES - 1,
							Constants.NO_OF_SAMPLES - 1, 0, Constants.NO_OF_CHANNELS - 1);
					double[][] aReplicateSamplesRight = mMatrixFunctions.repmat(aRowExtractRight, aNoSamplesToRight);

					for (int i = 0; i < aNoSamplesToRight; i++) {
						for (int j = 0; j < Constants.NO_OF_CHANNELS; j++) {
							aInputSVD[i + aInputExtension.length][j] = aReplicateSamplesRight[i][j];
						}
					}
				}

				/**
				 * Added Samples to right :: ALL extensions of signal is done.
				 */

				int aNoSamplesExtend = aInputSVD.length;
				int aNoQrs = aFinalQrsIndexArr - aInitialQrsIndexArr + 1;

				int[] aInitQrsLocations = new int[aNoQrs];
				for (int i = aInitialQrsIndexArr; i <= aFinalQrsIndexArr; i++) {
					aInitQrsLocations[i - aInitialQrsIndexArr] = iQRSm[i - 1];
				}

				/**
				 * Start and end of QRS window
				 */
				int[] aQrsIndexArrI = new int[aNoQrs];
				int[] aQrsIndexArrF = new int[aNoQrs];

				for (int i = 0; i < aNoQrs; i++) {
					aQrsIndexArrI[i] = aInitQrsLocations[i] + aNoSamplesToLeft - aNoSamplesBeforeQRS;
					aQrsIndexArrF[i] = aInitQrsLocations[i] + aNoSamplesToLeft + aNoSamplesAfterQRS;
				}

				double aSvdExtract[][] = new double[aNoSamplesQRS][aNoQrs];

				// add weight function
				double[][] aWeightWindowFunction = mMatrixFunctions.weightFunction(aNoSamplesBeforeQRS,
						aNoSamplesAfterQRS, Constants.FS);
				// double[][] wwg = Matrix.transpose(wwgT);
				double[][] aApproxmSignal = new double[aNoSamplesExtend][Constants.NO_OF_CHANNELS];

				/**
				 * Start loop for doing SVD and substraction
				 */
				double[][] aRowextract = new double[1][aNoSamplesQRS];
				double[][] aRowWeighted = new double[1][aNoSamplesQRS];

				aApproxmSignal = pcaParallized(aNoQrs, aQrsIndexArrI, aQrsIndexArrF, aRowWeighted, aRowextract,
						aInputSVD, aWeightWindowFunction, aNoSamplesQRS, aSvdExtract, aApproxmSignal);

				double[][] aResidueOutput = new double[Constants.NO_OF_SAMPLES][Constants.NO_OF_CHANNELS];
				for (int i = 0; i < Constants.NO_OF_SAMPLES; i++) {
					for (int j = 0; j < Constants.NO_OF_CHANNELS; j++) {
						aResidueOutput[i][j] = aInputSVD[i + aNoSamplesToLeft][j]
								- aApproxmSignal[i + aNoSamplesToLeft][j];
					}
				}

				return aResidueOutput;
			} else {
				throw new Exception("No of mQRS must be atleast 6 : mqrs cancel");
			}
		} else {
			throw new Exception("Enter a non-empty array : mqrs cancel");
		}
	} // end main

} // end class