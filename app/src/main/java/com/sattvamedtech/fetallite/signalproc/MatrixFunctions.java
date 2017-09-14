package com.sattvamedtech.fetallite.signalproc;

import java.util.Arrays;
/**
 * @version 
 * @author kishoresubramanian
 *
 */
public class MatrixFunctions {


	/**
	 * Copies the matrix into a different 2D - array
	 * 
	 * @param iInput
	 *            : (double[][]) Input matrix
	 * @param iOutput
	 *            : (double[][]) Output matrix
	 * @throws Exception
	 *             : Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	public void copy(double[][] iInput, double[][] iOutput) throws Exception {

		if (iInput.length > 0 && iOutput.length > 0) {
			int aLength = iInput.length;
			int aWidth = iInput[0].length;
			if (aLength == iOutput.length && aWidth == iOutput[0].length) {
				for (int i = 0; i < aLength; i++) {
					for (int j = 0; j < aWidth; j++) {
						iOutput[i][j] = iInput[i][j];
					}
				}
			} else {
				throw new Exception("Enter Arrays of same dimension : Copy function");
			}
		} else {
			throw new Exception(" Enter non empty array : Copy function");
		}
	}

	/**
	 * findMedian : Find median of the given Input array.
	 * 
	 * @param iInput
	 *            : (double[]) Input data
	 * @return : (double) Median value of the array
	 * @throws Exception:
	 *             Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	public double findMedian(double[] iInput) throws Exception {

		if (iInput.length > 0) {
			Arrays.sort(iInput);
			int aLen = iInput.length;
			double aMedian = 0;
			if (aLen % 2 == 1) {
				aMedian = iInput[aLen / 2];
			} else {
				aMedian = (iInput[aLen / 2] + iInput[aLen / 2 - 1]) / 2;
			}
			return aMedian;
		} else {
			throw new Exception("Enter non empty Array : findMedian");
		}

	}

	/**
	 * findPercentileValue : Find the value of the entry at the given percentile
	 * location in an array
	 * 
	 * @param iInput
	 *            : (double[]) Input data
	 * @param iPrecentile
	 *            : (int) Percentage to check the distribution tail
	 * @return : (double) Value at the location of percentile
	 * @throws Exception
	 *             : Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	public double findPercentileValue(double[] iInput, int iPrecentile) throws Exception {
		if (iInput.length > 0) {
			if (iPrecentile >= 0 && iPrecentile <= 99) {
				Arrays.sort(iInput);
				int aFinalIndex = iInput.length - (iInput.length * iPrecentile / 100);
				return iInput[aFinalIndex - 1];
			} else {
				throw new Exception("Percentile should be between 0-99 : findPercentileValue");
			}
		} else {
			throw new Exception("Enter non empty array : findPercentileValue");
		}
	}

	/**
	 * multiply : Multiply 2 matrices A and B and return in new matrix C.
	 * 
	 * @param iA		: (double[][]) Input matrix A
	 * @param iB		: (double[][]) Input matrix B
	 * @return 			: (double[][]) Product of A and B
	 * @throws Exception: If the conditions of matrix multiplication are not met
	 */
	public double[][] multiply(double[][] iA, double[][] iB) throws Exception {
		if (iA.length > 0 && iB.length > 0) {
			int aColA = iA[0].length;
			int aRowA = iA.length;
			int aColB = iB[0].length;
			int aRowB = iB.length;
			double aC[][] = new double[aRowA][aColB];
			if (aColA == aRowB) // Matrix multiplication condition mxn and nxp
								// produces mxp;
			{

				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColB; j++) {
						for (int k = 0; k < aRowB; k++) {
							aC[i][j] = aC[i][j] + iA[i][k] * iB[k][j];
						}
					}
				}
				return aC;
			} else {
				throw new Exception("Enter a valid Matrix : multiply");
			}
		} else {
			throw new Exception("Enter non empty matrix : multiply");
		}
	}

	/**
	 * subtractMeanColumn : Find mean of each column and subtract it from the entries of corresponding columns.
	 * 
	 * @param iInput	: (double[][]) Input data
	 * @throws Exception: If input data is empty array
	 */
	public void subtractMeanColumn(double[][] iInput) throws Exception {

		int aRow = iInput.length;
		if (aRow > 0) {
			int aCol = iInput[0].length;

			for (int i = 0; i < aCol; i++) {
				double aMean = 0;
				for (int j = 0; j < aRow; j++) {
					aMean = aMean + iInput[j][i];
				}
				aMean = aMean / aRow;
				for (int j = 0; j < aRow; j++) {
					iInput[j][i] = iInput[j][i] - aMean;
				}
			}
		} else {
			throw new Exception("Enter non empty matrix : subtractMeanColumn");
		}
	}

	/**
	 * setEigenCovarianceMatrix : C = (aT * a)/T
	 * 
	 * @param iInput: (double[][]) Input matrix (A)
	 * @return		: (double[][]) Covariance matrix
	 * @throws Exception: If input data is empty array
	 */
	public double[][] setEigenCovarianceMatrix(double[][] iInput) throws Exception {
		if (iInput.length > 0) {
			int aRow = iInput.length;
			int aCol = iInput[0].length;
			double[][] aCoVariance = new double[aCol][aCol];
			double aSum;

			for (int i = 0; i < aCol; i++) {
				for (int j = 0; j < aCol; j++) {
					aSum = 0;
					for (int k = 0; k < aRow; k++) {
						aSum = aSum + iInput[k][i] * iInput[k][j];
					}
					aCoVariance[i][j] = aSum / aRow;
				}
			}

			return aCoVariance;
		} else {
			throw new Exception("Enter non empty matrix : setEigenCovarianceMatrix");
		}
	}

	/**
	 * multiply_ABtranspose : Multiply matrices 'a' and 'bT' and update the matrix 'a'
	 * with the result
	 * 
	 * @param iA	: (double[][]) Input a
	 * @param iB	: (double[][]) Input b 
	 *  @throws Exception: If the conditions of matrix multiplication are not met
	 */

	public void multiply_ABtranspose(double[][] iA, double[][] iB) throws Exception {
		int aRowA = iA.length;
		int aRowB = iB.length;
		if (aRowA > 0 && aRowB > 0) {
			int aColA = iA[0].length;

			int aColB = iB[0].length;

			double[] aTemp = new double[aColA];
			double aSum;
			if (aColA == aColB) {
				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColA; j++) {
						aTemp[j] = iA[i][j];
					}
					for (int j = 0; j < aColA; j++) {
						aSum = 0;
						for (int k = 0; k < aRowB; k++) {
							aSum = aSum + aTemp[k] * iB[j][k];
						}
						iA[i][j] = aSum;
					}
				}
			} else {
				throw new Exception("Invalid Matrices for multiplication : multiply_ABtranspose");
				// return exception and break
			}
		} else {
			throw new Exception("Enter non empty array : multiply_ABtranspose");
		}
	}

	
	/**
	 * findCumulantMatrixEntries : Steps are as follows:
	 * 				1. Xij = X(:,im) .* X(:,jm);
	 * 				2. CM = (Xij .* X)' * X)/T
	 * 				3. CM = CM - I(:,im)*I(:,jm) - I(:,jm) * I(:,im) - aI;
	 * 
	 * @param iInput	: (double[][]) Input data (X)
	 * @param iIm		: (int) Column index 1
	 * @param iJm		: (int) Column index 2
	 * @param iScale	: (double) Factor to multiply in step 3.(a)
	 * @return			: (double[][]) Cummulant matrix. (CM)
	 * @throws Exception: Entered column indices has to lie within the input matrix dimension 
	 */
	public double[][] findCumulantMatrixEntries(double[][] iInput, int iIm, int iJm, double iScale) throws Exception {
		int aRow = iInput.length;
		if (aRow > 0) {
			int aCol = iInput[0].length;
			if (iIm < aCol && iJm < aCol && (iIm >= 0 && iJm >= 0)) {
				double[][] aTempCM = new double[aCol][aCol];

				// Xij = X(:,im) .* X(:,jm);
				double[] aTempColSquare = new double[aRow];
				for (int i = 0; i < aRow; i++) {
					aTempColSquare[i] = iInput[i][iIm] * iInput[i][iJm];
				}
				// CM = (Xij .* X)' * X)/T
				double aSum;
				for (int i = 0; i < aCol; i++) {
					for (int j = 0; j < aCol; j++) {
						aSum = 0;
						for (int k = 0; k < aRow; k++) {
							aSum = aSum + aTempColSquare[k] * iInput[k][i] * iInput[k][j];
						}
						aTempCM[i][j] = iScale * aSum / aRow;
					}
				}
				// CM = CM - I(:,im)*I(:,jm) - I(:,jm) * I(:,im) - I;
				aTempCM[iIm][iJm] = aTempCM[iIm][iJm] - iScale;
				aTempCM[iJm][iIm] = aTempCM[iJm][iIm] - iScale;
				if (iIm == iJm) {
					for (int i = 0; i < aCol; i++) {
						aTempCM[i][i] = aTempCM[i][i] - iScale;
					}
				}

				return aTempCM;
			} else {
				throw new Exception("Entered index is outside the range of columns : findCumulantMatrixEntries");
			}
		} else {
			throw new Exception("Enter a non empty array : findCumulantMatrixEntries");
		}
	}

/**
 * findGivensTheta : Find theta of the 2-D rotation matrix 
 * 
 * @param iA		: (double[][]) Rotation matrix
 * @return			: (double) Angle of rotation
 * @throws Exception: If input is empty
 */
	public double findGivensTheta(double[][] iA) throws Exception {
		// g = iA;
		// gg = g * g';
		// ton = gg(1,1) - gg(2,2);
		// toff = gg(1,2) + gg(2,1);
		// theta = 0.5*atan2( toff , ton+sqrt(ton*ton+toff*toff) );
		int aRow = iA.length;
		if (aRow > 0) {
			int aCol = iA[0].length;

			double aTon = 0;
			double aToff = 0;
			for (int i = 0; i < aRow; i++) {
				for (int j = 0; j < aRow; j++) {
					if (i == j) {
						for (int k = 0; k < aCol; k++) {
							aTon = aTon + Math.pow(-1, i) * iA[i][k] * iA[j][k];
						}
					} else {
						for (int k = 0; k < aCol; k++) {
							aToff = aToff + iA[i][k] * iA[j][k];
						}
					}
				}
			}
			return 0.5 * Math.atan2(aToff, aTon + Math.sqrt(aTon * aTon + aToff * aToff));
		} else {
			throw new Exception("Enter non empty array : findGivensTheta");
		}
	}

	/**
	 * identity : Create Identity matrix of size
	 * 
	 * @param iSize : (int) Size of identity matrix required
	 * @return		: (double[][]) Identity matrix
	 * @throws Exception : If input size is non-positive
	 */
	public double[][] identity(int iSize) throws Exception {
		if (iSize > 0) {
			double[][] aIdentity = new double[iSize][iSize];
			for (int i = 0; i < iSize; i++) {
				aIdentity[i][i] = 1.0;
			}

			return aIdentity;
		} else {
			throw new Exception("Matrix size has to be positive : identity");
		}
	}

	
	
	/**
	 * elementWiseMultiply : Element wise multiplication of A.*B
	 * 
	 * @param iA	: (double[][]) Input matrix 1
	 * @param iB	: (double[][]) Input matrix 2
	 * @return		: (double[][]) Product of both matrix
	 * @throws Exception : If the dimension of both matrices do not match
	 */
	public double[][] elementWiseMultiply(double[][] iA, double[][] iB) throws Exception {
		int aRowA = iA.length;
		int aRowB = iB.length;

		if (aRowA > 0 && aRowB > 0) {
			int aColA = iA[0].length;

			int aColB = iB[0].length;

			double[][] aOut = new double[aRowA][aColA];
			if (aColA == aColB && aRowA == aRowB) // The dimensions of Matrix A
													// and
													// B
													// must match
			{
				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColA; j++) {
						aOut[i][j] = iA[i][j] * iB[i][j];
					}
				}
				return aOut;
			} else {
				throw new Exception("Enter matrices with valid dimension : elementWiseMultiply");
			}
		} else {
			throw new Exception("Enter non empty array : elementWiseMultiply");
		}
	}

	/**
	 * elementWiseDivide : Element wise division of A./B
	 * 
	 * @param iA	: (double[][]) Input matrix 1
	 * @param iB	: (double[][]) Input matrix 2
	 * @return		: (double[][])  Division output
	 * @throws Exception : If the dimension of both matrices do not match and if any element in matrix 2 is 0.
	 */
	public double[][] elementWiseDivide(double[][] iA, double[][] iB) throws Exception {
		int aRowA = iA.length;
		int aRowB = iB.length;
		if (aRowA > 0 && aRowB > 0) {
			int aColA = iA[0].length;
			int aColB = iB[0].length;

			double[][] aOut = new double[aRowA][aColA];
			if (aColA == aColB && aRowA == aRowB) // The dimensions of Matrix A
													// and
													// B
													// must match
			{
				// System.out.println("The element wise mult matrix is: ");
				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColA; j++) {
						if (iB[i][j] != 0) {
							aOut[i][j] = iA[i][j] / iB[i][j];
						} else {
							throw new Exception("Cannot divide by 0 : elementWiseDivide");
						}
						// System.out.print(dot[i][j]+" ");
					}
					// System.out.println();
				}
				return aOut;
			} else {
				throw new Exception("Enter non empty array : elementWiseDivide");
			}
		} else {
			throw new Exception("Enter matrices with valid dimension : elementWiseDivide");
		}
	}

	/**
	 * transpose : Find Transpose of a matrix
	 * 
	 * @param iA : (double[][]) Input matrix
	 * @return   : (double[][]) 
	 */
	public double[][] transpose(double[][] iA) {
		int aRow = iA.length;
		int aCol = iA[0].length;
		double[][] aTranspose = new double[aCol][aRow];
		for (int i = 0; i < aRow; i++) {
			for (int j = 0; j < aCol; j++) {
				aTranspose[j][i] = iA[i][j];
			}
		}

		return aTranspose;
	}

	/**
	 * subMatrix : Extract a submatrix from a given Matrix
	 * 
	 * @param iA	: (double[][]) Input matrix
	 * @param iRowI	: (int) Start row index to extract submatrix
	 * @param iRowF	: (int) End row index to extract submatrix 
	 * @param iColI	: (int) Start column index to extract submatrix
	 * @param iColF	: (int) End column index to extract submatrix
	 * @return		: (double[][]) The submatrix extracted from the input matrix
	 * @throws Exception : If any of the index is outside the matrix dimension
	 */
	public double[][] subMatrix(double[][] iA, int iRowI, int iRowF, int iColI, int iColF) throws Exception {
		int aRow = iA.length;
		if (aRow > 0) {
			int aCol = iA[0].length;

			double[][] aSubMatrix = new double[(iRowF - iRowI) + 1][(iColF - iColI) + 1];
			// size of the sub matrix
			if (((iRowI >= 0 && iRowI <= iRowF) && (iRowF < aRow && iRowF >= iRowI))
					&& ((iColI >= 0 && iColI <= iColF) && (iColF < aCol && iColF >= iColI)))
			// The boundary conditions must lie within the size of the Matrix
			{
				for (int i = 0; i < iRowF - iRowI + 1; i++) {
					for (int j = 0; j < iColF - iColI + 1; j++) {
						aSubMatrix[i][j] = iA[iRowI + i][iColI + j];
					}
				}
				return aSubMatrix;
			} else {
				throw new Exception("Entered index is outside the range of matrix : subMatrix");
			}
		} else {
			throw new Exception("Enter non empty array : subMatrix");
		}
	}

	/**
	 * verticalConcat : Concatenate Matrix A and B into a tall matrix
	 * 
	 * @param iA	: (double[][]) Input matrix A
	 * @param iB	: (double[][]) Input matrix B
	 * @return		: (double[][]) Concatenate matrix
	 * @throws Exception : If the columns of both do not match
	 */
	public double[][] verticalConcat(double[][] iA, double[][] iB) throws Exception {

		int aRowA = iA.length;

		int aRowB = iB.length;

		if (aRowA > 0 && aRowB == 0) {
			return iA;
		} else if (aRowA == 0 && aRowB > 0) {
			return iB;
		}
		if ((aRowA == 0 && aRowB == 0)) {
			throw new Exception("Enter non empty matrices.");
		}
		int aColA = iA[0].length;
		int aColB = iB[0].length;
		if (aColA != aColB) {
			throw new Exception("Columns of both matrices must be same.");
		} else {
			double[][] iC = new double[aRowA + aRowB][aColA];
			for (int i = 0; i < aRowA + aRowB; i++) {
				for (int j = 0; j < aColA; j++) {
					if (i < aRowA) {
						iC[i][j] = iA[i][j];
					} else {
						iC[i][j] = iB[i - aRowA][j];
					}
				}
			}

			return iC;
		}

	}

	/**
	 * findingPositiveElementsIndex : Find index of positive entries of an array
	 * 
	 * @param iInput : (double[]) Input data
	 * @return : (int[]) Locations of positive entry
	 */
	public int[] findingPositiveElementsIndex(double iInput[]) {

		int aOutput[] = new int[iInput.length];
		int aT = 0;
		int aCount = 0;

		for (int y = 0; y < iInput.length; y++) {
			if (iInput[y] > 0) {
				aOutput[aT] = y;
				++aT;
				if (y == 0) {
					aCount = aCount + 1;
				}
			}
		}

		if (aCount > 0) {
			for (int i = 1; i < aOutput.length; i++) {
				if (aOutput[i] > 0) {
					aCount = aCount + 1;
				} else {
					break;
				}
			}
		} else if (aCount == 0) {
			for (int i = 0; i < aOutput.length; i++) {
				if (aOutput[i] > 0) {
					aCount = aCount + 1;
				} else {
					break;
				}
			}
		}
		if (aCount > 0) {
			int[] aOut = new int[aCount];
			for (int i = 0; i < aCount; i++) {
				aOut[i] = aOutput[i];
			}
			return aOut;
		} else {
			return new int[] {};
		}

	}

	/**
	 * weightFunction : Generate a trapezoidal window function of given length
	 * 
	 * @param nSamplesBeforeQRS	: (int) Length of window before QRS
	 * @param nSamplesAfterQRS	: (int) Length of window after QRS
	 * @param fs				: (int) Sampling frequency
	 * @return					: (double[][]) Trapezoidal window 
	 * @throws Exception		: If nSamplesBeforeQRS < (fs * 14 / 100) & nSamplesAfterQRS < (fs * 6 / 100)
	 */

	public double[][] weightFunction(int nSamplesBeforeQRS, int nSamplesAfterQRS, int fs) throws Exception {
		// integer A, B, C.
		if (fs > 0 && (nSamplesBeforeQRS >= (fs * 14 / 100) && nSamplesAfterQRS >= (fs * 6 / 100))) {
			int nSamplesBefore1 = fs * 6 / 100;
			int nSamplesAfter1 = fs * 6 / 100;
			int nSamplesBefore2 = fs * 8 / 100;
			int nSamplesAfter2 = Math.min(fs * 2 / 100, (nSamplesAfterQRS - nSamplesAfter1));

			int iend1 = nSamplesBeforeQRS - nSamplesBefore1 - nSamplesBefore2;
			int iend2 = iend1 + nSamplesBefore2;
			int istart3 = iend2 + 1;
			int iend3 = iend2 + nSamplesBefore1 + nSamplesAfter1 + 1;
			int iend4 = iend3 + nSamplesAfter2;
			int istart5 = iend4 + 1;
			int iend5 = nSamplesBeforeQRS + nSamplesAfterQRS + 1;
			double wwg[][] = new double[iend5][1];
			int flag = 0;

			double constantValue = 0.2;
			double slopeValue = 0.8;
			for (int i = 0; i < iend1; i++) {
				wwg[i][0] = constantValue;
				flag = i;
			}
			int k = 0;
			while (flag < iend2 && k <= nSamplesBefore2) {
				flag = flag + 1;
				k = k + 1;
				wwg[flag][0] = constantValue + ((slopeValue * (k)) / nSamplesBefore2);
			}

			for (int i = istart3 - 1; i < iend3; i++) {
				wwg[i][0] = 1;
				flag = i;// 12
			}

			k = 1;
			while (flag < iend4 && k <= nSamplesAfter2) {
				flag = flag + 1;
				wwg[flag][0] = (1 - ((slopeValue * (k)) / nSamplesAfter2));
				k = k + 1;
			}

			for (int i = istart5 - 1; i < iend5; i++) {
				wwg[i][0] = constantValue;
				flag = i;
			}

			return wwg;
		} else {
			throw new Exception("Invalid Entries : weightFunction");
		}
	}

	/**
	 * 
	 * @param iInputArr
	 *            : (double[][]) 1xN array (N>0)
	 * @param iN
	 *            : (int) No of times to replicate.(cannot be negative)
	 * @return	: (double[][]) Replicated output
	 * @throws Exception : If the criteria mentioned above is not met
	 */
	public double[][] repmat(double[][] iInputArr, int iN) throws Exception {
		// replicate a row vector to many rows.
		int aRow = iInputArr.length;
		if (aRow > 0) {
			if (aRow == 1) {
				if (iN >= 0) {
					double[][] aExt = new double[iN][iInputArr[0].length];
					for (int i = 0; i < iN; i++) {
						for (int j = 0; j < iInputArr[0].length; j++) {
							aExt[i][j] = iInputArr[0][j];
						}
					}
					return aExt;
				} else {
					throw new Exception("Matrix dimension has to be positive : repmat");
				}
			} else {
				throw new Exception("Input should be a row matrix : repmat");
			}
		} else {
			throw new Exception("Enter non empty matrix : repmat");
		}
	}

/**
 * findMeanBetweenDistributionTails : Find mean of iInpArr between iPercI and iPercF
 * 
 * @param iInpArr	: (double[]) Input data
 * @param iPercI	: (int) Percentile to neglect data at start
 * @param iPercF	: (int) Percentile to neglect data at end
 * @return			: (double) Mean value of data between the two percentile
 * @throws Exception: If any percentile value out of (0-99) and if input is empty
 */
	public double findMeanBetweenDistributionTails(double[] iInpArr, int iPercI, int iPercF) throws Exception {
		int aLen = iInpArr.length;
		if (aLen > 0) {
			if ((iPercI > -1 && iPercI < 100) && (iPercF > -1 && iPercF < 100)) {
				if ((iPercI + iPercF) < 100) {
					double[] aArr = new double[aLen];
					for (int i = 0; i < aLen; i++) {
						aArr[i] = iInpArr[i];
					}
					Arrays.sort(aArr);
					int aInitIndex = 1 + aLen * iPercI / 100;
					int aFinalIndex = aLen - aLen * iPercF / 100;
					double aSum = 0;
					for (int i = aInitIndex - 1; i < aFinalIndex; i++) {
						aSum = aSum + aArr[i];
					}

					return aSum / (aFinalIndex - aInitIndex + 1);
				} else {
					throw new Exception("Input values must be within 100 : findMeanBetweenDistributionTails");
				}
			} else {
				throw new Exception("Input values must be within 100 : findMeanBetweenDistributionTails");
			}
		} else {
			throw new Exception("Enter non empty matrix : findMeanBetweenDistributionTails");
		}
	}

	/**
	 * FILTERING CODES
	 */

	/**
	 * fiterLoHi : Filtering of signals with filters of length 2 and one delay element
	 * 
	 * @param iChannel: (double[]) Input data (greater than 3)
	 * @param iB	: (double[]) Filter
	 * @param iA	: (double[]) Filter
	 * @param iZ	: (double) delay element
	 * @throws Exception	: If input length is less than 3/ filter length not equal to 2
	 */
	public void filterLoHi(double[] iChannel, double[] iA, double[] iB, double iZ) throws Exception {

		int aLength = iChannel.length;
		if (aLength > Constants.FILTER_NFACT2) {
			int aLengthExt = 2 * Constants.FILTER_NFACT2 + aLength;
			double aMirrorExtension[] = new double[aLengthExt];

			mirrorInput(iChannel, aMirrorExtension);
			filter2(aMirrorExtension, iB, iA, aMirrorExtension[0] * iZ);
			reverse(aMirrorExtension);
			filter2(aMirrorExtension, iB, iA, aMirrorExtension[0] * iZ);
			reverse(aMirrorExtension);

			for (int i = 0; i < aLength; i++) {
				iChannel[i] = aMirrorExtension[Constants.FILTER_NFACT2 + i];
			}
		} else {
			throw new Exception("Input length must be greater than 3 : filterLoHi");
		}

	}

	/**
	 * 
	 * @param iChannel	: (double[]) Input data (greater than 6)
	 * @param iB		: (double[]) Filter
	 * @param iA		: (double[]) Filter
	 * @param iZ1		: (double) delay element 1
	 * @param iZ2		: (double) delay element 2
	 * @throws Exception: If input length is less than 6/ filter length not equal to 3
	 */
	public void filterNotch(double[] iChannel, double[] iA, double[] iB, double iZ1, double iZ2) throws Exception {
		int aLength = iChannel.length;
		if (aLength > Constants.FILTER_NFACT3) {
			int aLengthExt = 2 * Constants.FILTER_NFACT3 + aLength;
			double[] aMirrorExtension = new double[aLengthExt];

			mirrorInput(iChannel, aMirrorExtension);
			aMirrorExtension = filter3N(aMirrorExtension, iB, iA, aMirrorExtension[0] * iZ1, aMirrorExtension[0] * iZ2);
			reverse(aMirrorExtension);
			aMirrorExtension = filter3N(aMirrorExtension, iB, iA, aMirrorExtension[0] * iZ1, aMirrorExtension[0] * iZ2);
			reverse(aMirrorExtension);

			for (int i = 0; i < aLength; i++) {
				iChannel[i] = aMirrorExtension[Constants.FILTER_NFACT3 + i];
			}
		} else {
			throw new Exception("Input length must be greater than 6 : filterNotch");
		}

	}

	/**
	 * filter2 : Filtering of signals with filters of length 2 and one delay element
	 * 
	 * @param iInput: (double[]) Input data (greater than 3)
	 * @param iB	: (double[]) Filter
	 * @param iA	: (double[]) Filter
	 * @param iZ	: (double) delay element
	 * @throws Exception	: If input length is less than 3/ filter length not equal to 2
	 */
	public void filter2(double[] iInput, double[] iB, double[] iA, double iZ) throws Exception {
		int aLength = iInput.length;
		int aLengthA = iA.length;
		int aLengthB = iB.length;
		if (aLength > Constants.FILTER_NFACT2) {
			if (aLengthB == 2 && aLengthA == 2) {
				double aTempI, aTempF;
				aTempI = iInput[0];
				iInput[0] = iB[0] * iInput[0] + iZ;

				for (int i = 1; i < aLength; i++) {
					aTempF = iInput[i];
					iInput[i] = iB[0] * iInput[i] + iB[1] * aTempI - iA[1] * iInput[i - 1];
					aTempI = aTempF;
				}
			} else {
				throw new Exception("Filter should be of order 2 : filter2");
			}
		} else {
			throw new Exception("Enter a non empty matrix : filter2");
		}
	}

	/**
	 * filter3N : Filtering of signals with filters of length 3 and different delays used
	 * for notch filter
	 * 
	 * @param iInput	: (double[]) Input data (greater than 6)
	 * @param iB		: (double[]) Filter
	 * @param iA		: (double[]) Filter
	 * @param iDelayN1	: (double) delay element 1
	 * @param iDelayN2	: (double) delay element 2
	 * @return			: (double[]) Filtered signal
	 * @throws Exception: If input length is less than 6/ filter length not equal to 3
	 * 
	 */
	public double[] filter3N(double[] iInput, double[] iB, double[] iA, double iDelayN1, double iDelayN2)
			throws Exception {

		int aLength = iInput.length;
		int aLengthA = iA.length;
		int aLengthB = iB.length;
		if (aLength > Constants.FILTER_NFACT3) {
			if (aLengthA == 3 && aLengthB == 3) {
				// filter operation
				double[] aFilteredOutput = new double[aLength];
				aFilteredOutput[0] = iB[0] * iInput[0] + iDelayN1;
				aFilteredOutput[1] = iB[0] * iInput[1] + iB[1] * iInput[0] - iA[1] * aFilteredOutput[0] + iDelayN2;

				for (int n = 2; n < aLength; n++) {
					aFilteredOutput[n] = (iB[0] * iInput[n]) + (iB[1] * iInput[n - 1]) + (iB[2] * iInput[n - 2])
							- (iA[1] * aFilteredOutput[n - 1]) - (iA[2] * aFilteredOutput[n - 2]);
				}

				return aFilteredOutput;
			} else {
				throw new Exception("Filter must be of order 3 : filter3N");
			}
		} else {
			throw new Exception("Enter non empty array : filter3N");
		}
	}

	/**
	 * mirrorInput : Extends the input on either sides
	 * 
	 * @param iInput			: (double[]) Input data
	 * @param iMirrorExtension	: (double[]) Output to be extended 
	 * @throws Exception		: If iMirrorExtension size is less than iInpt size or if both/either are empty arrays
	 */
	public void mirrorInput(double[] iInput, double[] iMirrorExtension) throws Exception {

		int aLength = iInput.length;
		int aLengthExtension = iMirrorExtension.length;

		if (aLength > 0 && aLengthExtension > 0) {
			if (aLength <= aLengthExtension) {
				int aNfact = (aLengthExtension - aLength) / 2;
				int aNfactEnd = aLength + (aNfact - 1);
				int aNoShift = 2 * aLength + aNfact - 2;
				for (int i = 0; i < aLengthExtension; i++) {
					if (i < aNfact) {
						iMirrorExtension[i] = 2 * iInput[0] - iInput[aNfact - i];
					} else if (i > aNfactEnd) {
						iMirrorExtension[i] = 2 * iInput[aLength - 1] - iInput[aNoShift - i];
					} else {
						iMirrorExtension[i] = iInput[i - aNfact];
					}
				}
			} else {
				throw new Exception("Cannot extend to a smaller array : mirrorInput");
			}
		} else {
			throw new Exception("Enter non empty array : mirrorInput");
		}

	}

	/**
	 * reverse : Reverse the input array
	 * 
	 * @param iInput	: (double[]) Input data
	 * @throws Exception: throws Exception if input is empty array
	 */
	public void reverse(double[] iInput) throws Exception {
		int aLength = iInput.length;
		if (aLength > 0) {
			double aTemp = 0;
			for (int i = 0; i < aLength / 2; i++) {
				aTemp = iInput[i];
				iInput[i] = iInput[aLength - i - 1];
				iInput[aLength - i - 1] = aTemp;
			}
		} else {
			throw new Exception("Enter non empty array : reverse");
		}

	}

	/**
	 * QRS DETECTION fucntions
	 */

	/**
	 * convolutionQRSDetection : Finds derivative and then squares the output 
	 * 
	 * @param iInput : (double[]) Input data
	 * @param iFilter : (double[]) Filter 
	 * @exception	: Throws exception if any input array is empty
	 */

	public void convolutionQRSDetection(double[] iInput, double[] iFilter) throws Exception {

		int aLengthInput = iInput.length;
		int aLengthFilter = iFilter.length;

		if (aLengthInput > 0 && aLengthFilter > 0) {
			int aLengthExtension = aLengthInput + aLengthFilter - 1;

			double aExtension[] = new double[aLengthExtension];

			for (int i = 0; i < aLengthExtension; i++) {
				if (i >= aLengthFilter / 2 && i < aLengthFilter / 2 + aLengthInput)
					aExtension[i] = iInput[i - aLengthFilter / 2];
				else
					aExtension[i] = 0;
			}

			double aSum;
			for (int i = 0; i < aLengthInput; i++) {
				aSum = 0;
				for (int j = 0; j < aLengthFilter; j++) {
					aSum = aSum + iFilter[aLengthFilter - 1 - j] * aExtension[j + i] / Constants.QRS_DERIVATIVE_SCALE;
				}
				iInput[i] = aSum * aSum;
			}
		} else {
			throw new Exception("Enter non empty array : convolutionQRSDetection");
		}
	}

	/**
	 * setIntegratorThreshold : Find the threshold for integrator.
	 * 
	 * @param iIntegrator	: (double[]) Input data
	 * @param scale			: (double) Scale to vary the threshold
	 * @return				: (double) Threshold value
	 * @exception			: Check for scale not equal to 0 and Input not empty array
	 */
	public double setIntegratorThreshold(double[] iIntegrator, double scale) throws Exception {
		int aLength = iIntegrator.length;
		if (aLength > 0) {
			if (scale != 0) {
				double aIntegratorSort[] = new double[aLength];

				for (int i = 0; i < aLength; i++) {
					aIntegratorSort[i] = iIntegrator[i];
				}
				Arrays.sort(aIntegratorSort);

				int aMaxLoc = (int) Math.ceil(aLength * Constants.QRS_INTEGRTOR_MAX);
				int aMinLoc = (int) Math.ceil(aLength * Constants.QRS_INTEGRATOR_MIN);

				double aMaxVal = aIntegratorSort[aMaxLoc];
				double aMinVal = aIntegratorSort[aMinLoc];

				double aThreshold = (aMaxVal - aMinVal) / scale;

				for (int i = 0; i < aLength; i++) {
					if (iIntegrator[i] < aThreshold) {
						iIntegrator[i] = 0;
					}
				}

				return aThreshold;
			} else {
				throw new Exception("Scale has to be non zero : setIntegratorThreshold");
			}
		} else {
			throw new Exception("Enter non empty array : setIntegratorThreshold");
		}
	}

	/**
	 * peakDetection : Peak detection for array with minimum difference of delta between local extremum.
	 * 
	 * @param iInput	: (double[]) Input data
	 * @param iDelta	: (double) Minimum difference between local maximum and local minimum.
	 * @return 			: (int[]) If no peak detected, will return an EMPTY Array.
	 * @exception		: Check for iDelta greater than 0 and Input not empty array
	 */
	public int[] peakDetection(double[] iInput, double iDelta) throws Exception {
		double aMinimum = 100000, aMaximum = -100000;
		double aMaxPos = 0;
		double aLookformax = 1;
		double aThisVar = 0;
		int aCountMax = 0;
		int aCountMin = 0;
		int aLength = iInput.length;
		if (aLength > 0) {
			if (iDelta > 0) {
				double[] aPeakLoc = new double[aLength];

				for (int ind = 0; ind < aLength; ind++) {
					aThisVar = iInput[ind];
					// check max and min are greater and lesser to x[y][0]
					// respectively
					if (aThisVar > aMaximum) {
						aMaximum = aThisVar;
						aMaxPos = ind;
					}
					if (aThisVar < aMinimum) {
						aMinimum = aThisVar;
					}

					if (aLookformax == 1) {
						if (aThisVar < (aMaximum - iDelta)) {
							aPeakLoc[aCountMax] = aMaxPos; // first col has
															// positions
							aCountMax = aCountMax + 1; // next row
							aMinimum = aThisVar;
							aLookformax = 0;
						}
					} else if (aLookformax == 0) {
						if (aThisVar > (aMinimum + iDelta)) {
							aCountMin = aCountMin + 1;
							aMaximum = aThisVar;
							aMaxPos = ind;
							aLookformax = 1;
						}
					}
				}

				int aCount = 0;
				if (aPeakLoc[0] >= 0 && aPeakLoc[1] > 0) {
					aCount = aCount + 1;
				}
				for (int i = 1; i < aPeakLoc.length; i++) {
					if (aPeakLoc[i] > 0) {
						aCount = aCount + 1;
					} else {
						break;
					}
				}

				int[] aPeakLocFinal = new int[aCount];
				for (int i = 0; i < aCount; i++) {
					aPeakLocFinal[i] = (int) (Math.floor(aPeakLoc[i])); // in
																		// case
																		// , we
																		// get
																		// decimal.
				}
				return aPeakLocFinal;
			} else {
				throw new Exception("Scale has to be positive : peakDetection");
			}
		} else {
			throw new Exception("Enter non empty array : peakDetection");
		}
	}

	/**
	 * channelSelection : Select the best channel containing QRS peaks.
	 * 
	 * @param iQRS1		: (int[]) QRS locations in channel 1
	 * @param iQRS2		: (int[]) QRS locations in channel 2
	 * @param iQRS3		: (int[]) QRS locations in channel 3
	 * @param iQRS4		: (int[]) QRS locations in channel 4
	 * @param iVarTh	: (int) Variance threshold 
	 * @param iRRlowTh	: (int) Lower limit of RR range
	 * @param iRRhighTh	: (int) Upper limit of RR range
	 * @return Object[] { qrs, startIndex } :: Will return the best QRS array
	 *         for Maternal/Fetal. If no channel is selected, will return
	 *         concatinated array for all QRS
	 * @throws Exception	: Throws appropriate exception if the input arguments do not
	 *             meet the requirements        
	 * 
	 */
	public Object[] channelSelection(int[] iQRS1, int[] iQRS2, int[] iQRS3, int[] iQRS4, int iVarTh, int iRRlowTh,
			int iRRhighTh) throws Exception {
		/**
		 * Channel selection part
		 */

		int aLen1 = iQRS1.length;
		int aLen2 = iQRS2.length;
		int aLen3 = iQRS3.length;
		int aLen4 = iQRS4.length;

		if (iVarTh > 0 && iRRhighTh > 0 && iRRlowTh > 0) {
			double aInd1 = 0;
			double aInd2 = 0;
			double aInd3 = 0;
			double aInd4 = 0;
			// to get the start index in each channel
			int aStartInd1 = -1;
			int aStartInd2 = -1;
			int aStartInd3 = -1;
			int aStartInd4 = -1;
			// RR mean for each channel
			double aRRmean1 = 0;
			double aRRmean2 = 0;
			double aRRmean3 = 0;
			double aRRmean4 = 0;

			if (aLen1 > 3) {
				int aNIt = aLen1 - 3;
				double aVar1[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				aVarMin = 1000;
				double counter = 0;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS1[i + 1] - iQRS1[i];
					t2 = iQRS1[i + 2] - iQRS1[i + 1];
					t3 = iQRS1[i + 3] - iQRS1[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar1[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar1[i] < iVarTh) {
						aRRTemp = iQRS1[i + 1] - iQRS1[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean1 = aRRmean1 + aRRTemp;
							counter = counter + 1;
							if (aVar1[i] < aVarMin) {
								aVarMin = aVar1[i];
								aStartInd1 = i;
							}
						}
					}
				}
				aRRmean1 = aRRmean1 / counter;
				aInd1 = counter / aNIt;
			}

			if (aLen2 > 3) {
				int aNIt = aLen2 - 3;
				double aVar2[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS2[i + 1] - iQRS2[i];
					t2 = iQRS2[i + 2] - iQRS2[i + 1];
					t3 = iQRS2[i + 3] - iQRS2[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar2[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar2[i] < iVarTh) {
						aRRTemp = iQRS2[i + 1] - iQRS2[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean2 = aRRmean2 + aRRTemp;
							counter = counter + 1;
							if (aVar2[i] < aVarMin) {
								aVarMin = aVar2[i];
								aStartInd2 = i;
							}
						}
					}
				}
				aRRmean2 = aRRmean2 / counter;

				aInd2 = counter / aNIt;
			}

			if (aLen3 > 3) {
				int aNIt = aLen3 - 3;
				double aVar3[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS3[i + 1] - iQRS3[i];
					t2 = iQRS3[i + 2] - iQRS3[i + 1];
					t3 = iQRS3[i + 3] - iQRS3[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar3[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar3[i] < iVarTh) {
						aRRTemp = iQRS3[i + 1] - iQRS3[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean3 = aRRmean3 + 1;
							counter = counter + 1;
							if (aVar3[i] < aVarMin) {
								aVarMin = aVar3[i];
								aStartInd3 = i;
							}
						}
					}
				}
				aRRmean3 = aRRmean3 / counter;
				aInd3 = counter / aNIt;
			}

			if (aLen4 > 3) {
				int aNIt = aLen4 - 3;
				double aVar4[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS4[i + 1] - iQRS4[i];
					t2 = iQRS4[i + 2] - iQRS4[i + 1];
					t3 = iQRS4[i + 3] - iQRS4[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar4[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar4[i] < iVarTh) {
						aRRTemp = iQRS4[i + 1] - iQRS4[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean4 = aRRmean4 + 1;
							counter = counter + 1;
							if (aVar4[i] < aVarMin) {
								aVarMin = aVar4[i];
								aStartInd4 = i;
							}
						}
					}
				}
				aRRmean4 = aRRmean4 / counter;
				aInd4 = counter / aNIt;
			}
			// FInd the maximum value of 'ind'
			// Have to add mean RR value also to this computation to get better
			// estimate of 'ch'

			if (aInd1 == 0 && aInd2 == 0 && aInd3 == 0 && aInd4 == 0) {
				int qrs[] = new int[aLen1 + aLen2 + aLen3 + aLen4];
				for (int i = 0; i < aLen1; i++) {
					qrs[i] = iQRS1[i];
				}
				int shift = aLen1;
				for (int i = 0; i < aLen2; i++) {
					qrs[i + shift] = iQRS2[i];
				}
				shift = shift + aLen2;
				for (int i = 0; i < aLen3; i++) {
					qrs[i + shift] = iQRS3[i];
				}
				shift = shift + aLen3;
				for (int i = 0; i < aLen4; i++) {
					qrs[i + shift] = iQRS4[i];
				}
				Arrays.sort(qrs);
				return new Object[] { qrs, -1 };
			} else {
				double ind = aInd1;
				int length_Final = aLen1;
				int ch = 1;
				double RRmean = 0;
				for (int i = 0; i < aLen1 - 1; i++) {
					RRmean = RRmean + iQRS1[i + 1] - iQRS1[i];
				}
				RRmean = RRmean / (aLen1 - 1);
				if (aInd2 == ind) {
					double RRmean2 = 0;
					for (int i = 0; i < aLen2 - 1; i++) {
						RRmean2 = RRmean2 + iQRS2[i + 1] - iQRS2[i];
					}
					RRmean2 = RRmean2 / (aLen2 - 1);
					if (RRmean < RRmean2) {
						ind = aInd2;
						ch = 2;
						length_Final = aLen2;
						RRmean = RRmean2;
					}
				} else if (aInd2 > ind) {
					ind = aInd2;
					ch = 2;
					length_Final = aLen2;
					double RRmean2 = 0;
					for (int i = 0; i < aLen2 - 1; i++) {
						RRmean2 = RRmean2 + iQRS2[i + 1] - iQRS2[i];
					}
					RRmean = RRmean2 / (aLen2 - 1);
				}
				if (aInd3 == ind) {
					double RRmean3 = 0;
					for (int i = 0; i < aLen3 - 1; i++) {
						RRmean3 = RRmean3 + iQRS3[i + 1] - iQRS3[i];
					}
					RRmean3 = RRmean3 / (aLen3 - 1);
					if (RRmean < RRmean3) {
						ind = aInd3;
						ch = 3;
						length_Final = aLen3;
						RRmean = RRmean3;
					}
				} else if (aInd3 > ind) {
					ind = aInd3;
					ch = 3;
					length_Final = aLen3;
					double RRmean3 = 0;
					for (int i = 0; i < aLen3 - 1; i++) {
						RRmean3 = RRmean3 + iQRS3[i + 1] - iQRS3[i];
					}
					RRmean = RRmean3 / (aLen3 - 1);
				}
				if (aInd4 > ind) {
					double RRmean4 = 0;
					for (int i = 0; i < aLen4 - 1; i++) {
						RRmean4 = RRmean4 + iQRS4[i + 1] - iQRS4[i];
					}
					RRmean4 = RRmean4 / (aLen4 - 1);
					if (RRmean < RRmean4) {
						ind = aInd4;
						ch = 4;
						length_Final = aLen4;
						RRmean = RRmean4;
					}
				} else if (aInd4 > ind) {
					ind = aInd4;
					ch = 4;
					length_Final = aLen4;
					double RRmean4 = 0;
					for (int i = 0; i < aLen4 - 1; i++) {
						RRmean4 = RRmean4 + iQRS4[i + 1] - iQRS4[i];
					}
					RRmean = RRmean4 / (aLen4 - 1);
				}
				/**
				 * Get the start Index and qrs values to find the final QRS.
				 */
				int[] qrs = new int[length_Final];
				int startIndex = -1;
				if (ch == 1) {
					startIndex = aStartInd1;

					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS1[i];
					}
				} else if (ch == 2) {
					startIndex = aStartInd2;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS2[i];
					}
				} else if (ch == 3) {
					startIndex = aStartInd3;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS3[i];
					}
				} else if (ch == 4) {
					startIndex = aStartInd4;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS4[i];
					}
				}

				return new Object[] { qrs, startIndex };
			}
		} else {
			throw new Exception("Threshold has to be positive : channelSelection");
		}
	}

}// close class
