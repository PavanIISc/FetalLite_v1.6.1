package com.sattvamedtech.fetallite.signalproc;

/**
 * @version SFL 1.0
 * @author kishoresubramanian
 *
 */

public class PCARank3 {
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();

	/**
	 * pca : Finds the rank 3 approximation of the input matrix.
	 * 
	 * @param iA
	 *            : (double[][]) Input data ( m x n : m>n & n>3 )
	 * @return : (double[][]) Rank 3 approximation of input data
	 * @throws Exception
	 *             : Throws appropriate exception if the input arguments do not
	 *             meet the requirements
	 */
	public double[][] pca(double[][] iA) throws Exception {

		/**
		 * Added on 16th march, 2017. To find the top 3 singular values and
		 * reduce computation of SVD by finding all the singular vectors.
		 * 
		 * Involves 3 steps:
		 * 
		 * SVD : A = U * sigma * V'
		 * 
		 * 1. Find Eigen Values and Eigen Vectors of A'*A = V * sigma^2 * V' 2.
		 * Now V and Sigma are found. Find U = A* V * (1/sqrt(Sigma)) 3. Extract
		 * the U, V , Sigma based on top 3 Signular values, to get approx Signal
		 */
		if (iA.length > 0) {
			if (iA.length > iA[0].length) {
				if (iA[0].length > 3) {
					double[][] aInputED = mMatrixFunctions.multiply(mMatrixFunctions.transpose(iA), iA);

					EigenvalueDecomposition aEigDecomposition = new EigenvalueDecomposition(aInputED);

					double[] aEigenValues = aEigDecomposition.getEigenvalues();
					double[][] aEigenVectorM = aEigDecomposition.getV();
					double[][] aEValueInv = new double[iA[0].length][iA[0].length];

					for (int i = 0; i < iA[0].length; i++) {
						aEValueInv[i][i] = 1 / Math.sqrt(aEigenValues[i]);
					}

					double[][] aLeftSingMatrix = mMatrixFunctions.multiply(iA,
							mMatrixFunctions.multiply(aEigenVectorM, aEValueInv));

					double max1 = -10000, max2 = -10000, max3 = -10000;
					int ind1 = 0, ind2 = 0, ind3 = 0;
					for (int i = 0; i < aEigenValues.length; i++) {
						if (aEigenValues[i] >= max1) {
							max3 = max2;
							ind3 = ind2;
							max2 = max1;
							ind2 = ind1;
							ind1 = i;
							max1 = (aEigenValues[i]);
						} else if (aEigenValues[i] >= max2) {
							max3 = max2;
							ind3 = ind2;
							ind2 = i;
							max2 = (aEigenValues[i]);
						} else if (aEigenValues[i] >= max3) {
							ind3 = i;
							max3 = (aEigenValues[i]);
						}
					}

					double[][] aSingularValueFinal = { { 1 / aEValueInv[ind1][ind1], 0, 0 },
							{ 0, 1 / aEValueInv[ind2][ind2], 0 }, { 0, 0, 1 / aEValueInv[ind3][ind3] } };
					double[][] aLeftSingularVectorFinal = new double[iA.length][3];
					double[][] aRightSIngularVectorFinal = new double[iA[0].length][3];

					for (int i = 0; i < iA.length; i++) {
						aLeftSingularVectorFinal[i][0] = aLeftSingMatrix[i][ind1];
						aLeftSingularVectorFinal[i][1] = aLeftSingMatrix[i][ind2];
						aLeftSingularVectorFinal[i][2] = aLeftSingMatrix[i][ind3];
					}

					for (int i = 0; i < iA[0].length; i++) {
						aRightSIngularVectorFinal[i][0] = aEigenVectorM[i][ind1];
						aRightSIngularVectorFinal[i][1] = aEigenVectorM[i][ind2];
						aRightSIngularVectorFinal[i][2] = aEigenVectorM[i][ind3];
					}
					double[][] aApprox = mMatrixFunctions.multiply(aLeftSingularVectorFinal, mMatrixFunctions
							.multiply(aSingularValueFinal, mMatrixFunctions.transpose(aRightSIngularVectorFinal)));

					return aApprox;
				} else {
					throw new Exception("Entered matrix must be minimum Rank 3 : pca");
				}
			} else {
				throw new Exception("Enter tall matrix : pca");
			}
		} else {
			throw new Exception("Enter a non-empty matrix : pca");
		}

	}
}
