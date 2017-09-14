package com.sattvamedtech.fetallite.miniTestModule;

import java.util.Arrays;


public class matrixFunctions {
    /* Some comments about the class Matrix
	 * Matrix operations are performed on 2D arrays
	 *
	 */

    /**
     * Copies the matrix into a different 2D - array
     *
     * @param input  -- 2D array
     * @param output -- Copy of the input
     */
    public void copy(double[][] input, double[][] output) {
        int length = input.length;
        int width = input[0].length;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                output[i][j] = input[i][j];
            }
        }
    }


    /**
     * Find median
     */
    public double findMedian(double[] input_array) {

        Arrays.sort(input_array);
        int len = input_array.length;
        double median = 0;
        if (len % 2 == 1) {
            median = input_array[len / 2];
        } else {
            median = (input_array[len / 2] + input_array[len / 2 - 1]) / 2;
        }
        return median;

    }

    public double findPercentileValue(double[] input_array, int precentile) {
        Arrays.sort(input_array);
        int finalIndex = input_array.length - (input_array.length * precentile / 100);
        return input_array[finalIndex - 1];
    }

    /* Some comments about multi function
     *
     * @param 2D array- A and B both , of type double
     * computes the matrix multiplication of A and B
     *	returns the resultant matrix which can be used for further calc
     */
    public double[][] multi(double[][] a, double[][] b) {

        int aCol = a[0].length;
        int aRow = a.length;
        int bCol = b[0].length;
        int bRow = b.length;
        double c[][] = new double[aRow][bCol];
        if (aCol == bRow) //Matrix multiplication condition mxn and nxp produces mxp;
        {


            for (int i = 0; i < aRow; i++) {
                for (int j = 0; j < bCol; j++) {
                    for (int k = 0; k < bRow; k++) {
                        c[i][j] = c[i][j] + a[i][k] * b[k][j];
                    }
                }
            }

        } else {
            System.out.println("Enter a valid Matrix");
        }
        return c;
    }


    public float[][] multiF(float[][] a, float[][] b) {

        int aCol = a[0].length;
        int aRow = a.length;
        int bCol = b[0].length;
        int bRow = b.length;
        float c[][] = new float[aRow][bCol];
        if (aCol == bRow) //Matrix multiplication condition mxn and nxp produces mxp;
        {


            for (int i = 0; i < aRow; i++) {
                for (int j = 0; j < bCol; j++) {
                    for (int k = 0; k < bRow; k++) {
                        c[i][j] = c[i][j] + a[i][k] * b[k][j];
                    }
                }
            }

        } else {
            System.out.println("Enter a valid Matrix");
        }
        return c;
    }


    public double[][] multiFD(float[][] a, float[][] b) {

        int aCol = a[0].length;
        int aRow = a.length;
        int bCol = b[0].length;
        int bRow = b.length;
        double c[][] = new double[aRow][bCol];
        if (aCol == bRow) //Matrix multiplication condition mxn and nxp produces mxp;
        {


            for (int i = 0; i < aRow; i++) {
                for (int j = 0; j < bCol; j++) {
                    for (int k = 0; k < bRow; k++) {
                        c[i][j] = c[i][j] + a[i][k] * b[k][j];
                    }
                }
            }

        } else {
            System.out.println("Enter a valid Matrix");
        }
        return c;
    }

    public float[][] multi_index(float[][] Ql, float[][] UTl, int i, int n) {

        int row = UTl.length;

        //double c[][]=new double[row][Col];
        float[] templ = new float[row];
        float norm = 0;

        for (int k = 0; k < n; k++) {
            for (int t = 0; t < row; t++) {
                templ[t] = UTl[t][k];
            }
            for (int l = i; l < row; l++) {
                norm = 0;
                for (int m = i; m < row; m++) {
                    norm = norm + templ[m] * Ql[l][m];
                }
                UTl[l][k] = norm;
            }
        }


        return UTl;

    }


    /* Some comments about sum function
     *
     * @param 2D array 'A' of type double
     * computes the sum of the elements in the matrix
     *	returns the resultant matrix which can be used for further calc
     */
    public double sum(double[][] a) {
        int n = a.length;
        int m = a[0].length;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                {
                    sum = sum + a[i][j];
                }
            }
        }
        // System.out.println("The sum of all elements is:"+sum);
        return sum;
    }


	/* Some comments about 'Mean' function
	 * 
	 * @param 2D array 'A' of type double
	 * calculates the mean of the matrix and subtracts the mean from each element
	 *	returns the resultant matrix which can be used for further calc
	 */

    /**
     * USED IN JADE
     *
     * @param a
     */
    public void subtractMeanColumn(double[][] a) {

        int row = a.length;
        int col = a[0].length;

        for (int i = 0; i < col; i++) {
            double mean = 0;
            for (int j = 0; j < row; j++) {
                mean = mean + a[j][i];
            }
            mean = mean / row;
            for (int j = 0; j < row; j++) {
                a[j][i] = a[j][i] - mean;
            }
        }
    }

    /**
     * USED IN JADE
     * DO :: C = (aT * a)/T
     *
     * @param a
     * @return
     */
    public double[][] setEigenCovarianceMatrix(double[][] a) {
        int row = a.length;
        int col = a[0].length;
        double[][] coVariance = new double[col][col];
        double sum;

        for (int i = 0; i < col; i++) {
            for (int j = 0; j < col; j++) {
                sum = 0;
                for (int k = 0; k < row; k++) {
                    sum = sum + a[k][i] * a[k][j];
                }
                coVariance[i][j] = sum / row;
            }
        }


        return coVariance;
    }

    /**
     * USED IN JADE
     * Multiply matrices 'a' and 'bT' and update the matrix 'a' with the result
     *
     * @param a
     * @param b
     */

    public void multiply_ABtranspose(double[][] a, double[][] b) {
        int aRow = a.length;
        int aCol = a[0].length;

        int bRow = b.length;
        int bCol = b[0].length;

        double[] temp = new double[aCol];
        double sum;
        if (aCol == bCol) {
            for (int i = 0; i < aRow; i++) {
                for (int j = 0; j < aCol; j++) {
                    temp[j] = a[i][j];
                }
                for (int j = 0; j < aCol; j++) {
                    sum = 0;
                    for (int k = 0; k < bRow; k++) {
                        sum = sum + temp[k] * b[j][k];
                    }
                    a[i][j] = sum;
                }
            }
        } else {
            // return exception and break
        }


    }

    /**
     * USED IN JADE
     *
     * @param im -- Extract from X(:,im)
     * @param x  -- input for finding CM
     * @param jm -- subtracting element for identity matrix extract
     * @return
     */
    public double[][] findCumulantMatrixEntries(double[][] x, int im, int jm, double scale) {
        int length = x.length;
        int size = x[0].length;
        double[][] tempCM = new double[size][size];

        double[] temp_xColsquare = new double[length];
        for (int i = 0; i < length; i++) {
            temp_xColsquare[i] = x[i][im] * x[i][jm];
        }


        double sum;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sum = 0;
                for (int k = 0; k < length; k++) {
                    sum = sum + temp_xColsquare[k] * x[k][i] * x[k][j];
                }
                tempCM[i][j] = scale * sum / length;
            }
        }

        tempCM[im][jm] = tempCM[im][jm] - scale;
        tempCM[jm][im] = tempCM[jm][im] - scale;
        if (im == jm) {
            for (int i = 0; i < size; i++) {
                tempCM[i][i] = tempCM[i][i] - scale;
            }
        }

        return tempCM;
    }

    /**
     * USED IN JADE // or deleted as not required.
     *
     * @param a
     * @return
     */
    public double elementwiseSquareSum(double[][] a) {
        int aCol = a[0].length;
        int aRow = a.length;

        double sum = 0;

        for (int i = 0; i < aRow; i++) {
            for (int j = 0; j < aCol; j++) {
                sum = sum + a[i][j] * a[i][j];
            }
        }
        return sum;

    }

    /**
     * USED IN JADE
     *
     * @param size
     * @return
     */
    public double findGivensTheta(double[][] a) {

        int aRow = a.length;
        int aCol = a[0].length;

        double tOn = 0;
        double tOff = 0;
        for (int i = 0; i < aRow; i++) {
            for (int j = 0; j < aRow; j++) {
                if (i == j) {
                    for (int k = 0; k < aCol; k++) {
                        tOn = tOn + Math.pow(-1, i) * a[i][k] * a[j][k];
                    }
                } else {
                    for (int k = 0; k < aCol; k++) {
                        tOff = tOff + a[i][k] * a[j][k];
                    }
                }
            }
        }
        return 0.5 * Math.atan2(tOff, tOn + Math.sqrt(tOn * tOn + tOff * tOff));

    }

    /* Some comments about 'Identity' function
     *
     * @param size of the required Identity Matrix
     * Produces an Identity Matrix of size specified by the input- 'size'
     *	returns the resultant matrix which can be used for further calc
     */
    public double[][] identity(int size) {

        double[][] identity = new double[size][size];
        for (int i = 0; i < size; i++) {
            identity[i][i] = 1.0;
        }

        return identity;
    }

    /* Some comments about 'ElementWiseMult' function
     *
     * @param 2D Arrays A and B of type double
     * Computes the element wise multiplication of A and B
     *	returns the resultant matrix which can be used for further calc
     */
    public double[][] ElementWiseMult(double[][] a, double[][] b) {
        int aCol = a[0].length;
        int aRow = a.length;
        int bCol = b[0].length;
        int bRow = b.length;
        double[][] dot = new double[aRow][aCol];
        if (aCol == bCol && aRow == bRow) //The dimensions of Matrix A and B must match
        {
            //System.out.println("The element wise mult matrix is: ");
            for (int i = 0; i < aRow; i++) {
                for (int j = 0; j < aCol; j++) {
                    dot[i][j] = a[i][j] * b[i][j];
                    //System.out.print(dot[i][j]+" ");
                }
                //System.out.println();
            }
            return dot;
        } else {
            System.out.println("dim mismatch for element wise * ");
            return dot;
        }
    }

    public double[][] ElementWiseDivide(double[][] a, double[][] b) {
        int aCol = a[0].length;
        int aRow = a.length;
        int bCol = b[0].length;
        int bRow = b.length;
        double[][] dot = new double[aRow][aCol];
        if (aCol == bCol && aRow == bRow) //The dimensions of Matrix A and B must match
        {
            //System.out.println("The element wise mult matrix is: ");
            for (int i = 0; i < aRow; i++) {
                for (int j = 0; j < aCol; j++) {
                    dot[i][j] = a[i][j] / b[i][j];
                    //System.out.print(dot[i][j]+" ");
                }
                //System.out.println();
            }
            return dot;
        } else {
            System.out.println("dim mismatch for element wise * ");
            return dot;
        }
    }


    /* Some comments about 'Transpose' function
     *
     * @param 2D Array A of type double
     * Produces the transpose of the input matrix
     *	returns the resultant matrix which can be used for further calc
     */
    public double[][] transpose(double[][] a) {
        int row = a.length;
        int col = a[0].length;
        double[][] transpose = new double[col][row];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                transpose[j][i] = a[i][j];
            }
        }

        return transpose;
    }


    /* Some comments about 'submatrix' function
     *
     * @param 2D Array A of type double, the boundary of the submatrix
     * ie, from r1 to r2 and from c1 to c2
     * Computes the element wise multiplication of A and B
     *	returns the resultant matrix which can be used for further calc
     */
    public double[][] submatrix(double[][] a, int r1, int r2, int c1, int c2) {
        int aRow = a.length;
        int aCol = a[0].length;

        double[][] subMatrix = new double[(r2 - r1) + 1][(c2 - c1) + 1]; //size of the sub matrix
        if (((r1 >= 0 && r1 <= r2) && (r2 < aRow && r2 >= r1)) && ((c1 >= 0 && c1 <= c2) && (c2 < aCol && c2 >= c1)))//The boundary conditions must lie within the size of the Matrix
        {
            for (int i = 0; i < r2 - r1 + 1; i++) {
                for (int j = 0; j < c2 - c1 + 1; j++) {
                    subMatrix[i][j] = a[r1 + i][c1 + j];
                }
            }

        } else {
            System.out.println("Enter valid matrix dimention");
        }

        return subMatrix;
    }


    public double[][] verticalConcat(double[][] a, double[][] b) {


        if (a.length > 0 && b.length == 0) {
            return a;
        } else if (a.length == 0 && b.length > 0) {
            return b;
        } else if (a[0].length == b[0].length) {
            double[][] c = new double[a.length + b.length][a[0].length];
            for (int i = 0; i < a.length + b.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    if (i < a.length) {
                        c[i][j] = a[i][j];
                    } else {
                        c[i][j] = b[i - a.length][j];
                    }
                }
            }

            return c;
        } // end both present
        else {
            return null;
        }

    }


    /**
     * USED IN IMPULSE FILTER
     */
    public int[] findingPositiveElementsIndex(double inp[]) {

        int output[] = new int[inp.length];
        int t = 0;
        int count = 0;


        for (int y = 0; y < inp.length; y++) {
            if (inp[y] > 0) {
                output[t] = y;
                ++t;
                if (y == 0) {
                    count = count + 1;
                }
            }
        }


        if (count > 0) {
            for (int i = 1; i < output.length; i++) {
                if (output[i] > 0) {
                    count = count + 1;
                } else {
                    break;
                }
            }
        } else if (count == 0) {
            for (int i = 0; i < output.length; i++) {
                if (output[i] > 0) {
                    count = count + 1;
                } else {
                    break;
                }
            }
        }
        if (count > 0) {
            int[] out = new int[count];
            for (int i = 0; i < count; i++) {
                out[i] = output[i];
            }
            return out;
        } else {
            return null;
        }

    }


    // FOR SVD functions

    public double[][] r_QRtransR(double beta, double[] rightHouseholderVector, double[][] A, double[] qrTempRow, int row, int col, int k) {

        // compute yr = beta*A*ur

        for (int l = 0; l < row; l++) {
            qrTempRow[l] = 0;
            for (int j = k + 1; j < col; j++) {
                qrTempRow[l] = qrTempRow[l] + beta * rightHouseholderVector[j] * A[l][j];
            }
        }
        // compute A = (A - yr*ur);

        for (int i = k; i < row; i++) {
            for (int j = k + 1; j < col; j++) {
                A[i][j] = A[i][j] - rightHouseholderVector[j] * qrTempRow[i];
            }
        }
        return A;
    }

    public double[][] q_QRtransR(double beta, double[] rightHouseholderVector, double[][] QR, double[] qrTempCol, int col, int k) {
        // compute yqr^T = beta * ur^T * Qtilde

        for (int l = 0; l < col; l++) {
            qrTempCol[l] = 0;
            for (int j = k + 1; j < col; j++) {
                qrTempCol[l] = qrTempCol[l] + beta * rightHouseholderVector[j] * QR[j][l];
            }
        }

        // compute Qtilde = Qtilde - ur*yqr^T

        for (int i = 0; i < col; i++) {
            for (int j = k + 1; j < col; j++) {
                QR[j][i] = QR[j][i] - rightHouseholderVector[j] * qrTempCol[i];
            }
        }
        return QR;
    }

    public double[][] r_QRtransL(double beta, double[] leftHouseholderVector, double[][] A, double[] qrTempCol, int row, int col, int iter) {

        // compute y^T = u^T * A
        for (int l = 0; l < col; l++) {
            qrTempCol[l] = 0;
            for (int j = iter; j < row; j++) {
                qrTempCol[l] = qrTempCol[l] + beta * leftHouseholderVector[j] * A[j][l];
            }
        }

        // compute A = (A - u*y^T);

        for (int i = iter; i < col; i++) {
            for (int j = iter; j < row; j++) {
                A[j][i] = A[j][i] - leftHouseholderVector[j] * qrTempCol[i];
            }
        }

        return A;
    }

    public double[][] q_QRtransL(double beta, double[] leftHouseholderVector, double[][] leftQR, double[] qrTempRow, int row, int iter) {
        // compute yq = beta*Q*u
        for (int l = 0; l < row; l++) {
            qrTempRow[l] = 0;
            for (int j = iter; j < row; j++) {
                qrTempRow[l] = qrTempRow[l] + beta * leftHouseholderVector[j] * leftQR[j][l];
            }
        }
        // compute Q = Q - yql * u^T;

        for (int i = 0; i < row; i++) {
            for (int j = iter; j < row; j++) {
                leftQR[j][i] = leftQR[j][i] - qrTempRow[i] * leftHouseholderVector[j];
            }
        }

        return leftQR;
    }

    public double[][] givensL(double[][] B, int n, int k, double a, double b) {

        double r = Math.sqrt(a * a + b * b);
        double c = a / r;
        double s = -b / r;

        double S0, S1;

        for (int i = 0; i < n; i++) {
            S0 = B[k + 0][i];
            S1 = B[k + 1][i];

            B[k][i] = c * S0 - s * S1;
            B[k + 1][i] = s * S0 + c * S1;

        }


        return B;
    } // end givensL

    public double[][] givensR(double[][] B, int n, int k, double a, double b) {

        double r = Math.sqrt(a * a + b * b);
        double c = a / r;
        double s = -b / r;

        double S0, S1;
        for (int i = 0; i < n; i++) {
            S0 = B[i][k];
            S1 = B[i][k + 1];
            B[i][k] = c * S0 - s * S1;    // check sign of s
            B[i][k + 1] = s * S0 + c * S1;    // -ve in this or above line

        }
        return B;
    } // end givensL

    /**
     * USED IN MQRS CANCEL
     */

    public double[][] weightFunction(int nSamplesBeforeQRS, int nSamplesAfterQRS, int fs) {
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
            flag = i;//12
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

    }

    public double[][] repmat(double[][] inp, int row, int col) {
        // replicate a row vector to many rows.
        double[][] ext = new double[row][inp[0].length];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < inp[0].length; j++) {
                ext[i][j] = inp[0][j];
            }
        }
        return ext;
    }

    public double findMeanBetweenDistributionTails(double[] v1, int perci, int percf) {
        int len = v1.length;
        double[] v = new double[len];
        for (int i = 0; i < len; i++) {
            v[i] = v1[i];
        }
        Arrays.sort(v);
        int ii = 1 + len * perci / 100;
        int fi = len - len * percf / 100;
        double sum = 0;
        for (int i = ii - 1; i < fi; i++) {
            sum = sum + v[i];
        }

        return sum / (fi - ii + 1);
    }


    /**
     * FILTERING CODES
     */

    public void filterLoHi(double[] channel, double[] a, double[] b, double z) {

        int length = channel.length;

        int length_ext = 2 * DefaultVariableMaster.filter_nfact2 + length;
        double mirror_extension[] = new double[length_ext];

        mirrorInput(channel, mirror_extension, DefaultVariableMaster.filter_nfact2);
        filter2(mirror_extension, b, a, mirror_extension[0] * z);
        reverse(mirror_extension, length_ext);
        filter2(mirror_extension, b, a, mirror_extension[0] * z);
        reverse(mirror_extension, length_ext);

        for (int i = 0; i < length; i++) {
            channel[i] = mirror_extension[DefaultVariableMaster.filter_nfact2 + i];
        }

    }


    public void filterNotch(double[] channel, double[] a, double[] b, double z1, double z2) {
        int length = channel.length;
        int length_ext = 2 * DefaultVariableMaster.filter_nfact3 + length;
        double[] mirror_extension = new double[length_ext];

        mirrorInput(channel, mirror_extension, DefaultVariableMaster.filter_nfact3);
        mirror_extension = filter3N(mirror_extension, b, a, mirror_extension[0] * z1, mirror_extension[0] * z2);
        reverse(mirror_extension, length_ext);
        mirror_extension = filter3N(mirror_extension, b, a, mirror_extension[0] * z1, mirror_extension[0] * z2);
        reverse(mirror_extension, length_ext);

        for (int i = 0; i < length; i++) {
            channel[i] = mirror_extension[DefaultVariableMaster.filter_nfact3 + i];
        }


    }

    /**
     * filtering of signals with filters of length 2 and one delay element
     */
    public void filter2(double[] input, double[] b, double[] a, double z) {
        int length = input.length;
        double tempI, tempF;
        tempI = input[0];

        input[0] = b[0] * input[0] + z;

        for (int i = 1; i < length; i++) {
            tempF = input[i];
            input[i] = b[0] * input[i] + b[1] * tempI - a[1] * input[i - 1];
            tempI = tempF;
        }
    }

    /**
     * filtering of signals with filters of length 3 and different delays used
     * for notch filter
     */
    public double[] filter3N(double[] input, double[] b, double[] a,
                             double delayN1, double delayN2) {

        int length = input.length;

        // filter operation
        double[] filtered_output = new double[length];
        filtered_output[0] = b[0] * input[0] + delayN1;
        filtered_output[1] = b[0] * input[1] + b[1] * input[0] - a[1] * filtered_output[0]
                + delayN2;

        for (int n = 2; n < length; n++) {
            filtered_output[n] = (b[0] * input[n]) + (b[1] * input[n - 1])
                    + (b[2] * input[n - 2]) - (a[1] * filtered_output[n - 1])
                    - (a[2] * filtered_output[n - 2]);
        }

        return filtered_output;

    }


    public void mirrorInput(double[] input, double[] mirror_extension, int nfact) {

        int length = input.length;
        int nIteration = length + 2 * (nfact);
        int nfact_end = length + (nfact - 1);
        int nShift = 2 * length + nfact - 2;
        for (int i = 0; i < nIteration; i++) {
            if (i < nfact) {
                mirror_extension[i] = 2 * input[0] - input[nfact - i];
            } else if (i > nfact_end) {
                mirror_extension[i] = 2 * input[length - 1] - input[nShift - i];
            } else {
                mirror_extension[i] = input[i - nfact];
            }
        }


    }

    public void reverse(double[] input_array, int length) {

        double temp = 0;
        for (int i = 0; i < length / 2; i++) {
            temp = input_array[i];
            input_array[i] = input_array[length - i - 1];
            input_array[length - i - 1] = temp;
        }

    }


    /**
     *  QRS DETECTION fucntions
     */

    /**
     * Finds derivative and then squares the output
     */
    public void convolutionQRSDetection(double[] input, double[] filter) {
        int length_input = input.length;
        int length_filter = filter.length;
        int length_extension = length_input + length_filter - 1;

        double extension[] = new double[length_extension];

        for (int i = 0; i < length_extension; i++) {
            if (i >= length_filter / 2 && i < length_filter / 2 + length_input)
                extension[i] = input[i - length_filter / 2];
            else
                extension[i] = 0;
        }

        double sum;
        for (int i = 0; i < length_input; i++) {
            sum = 0;
            for (int j = 0; j < length_filter; j++) {
                sum = sum + filter[length_filter - 1 - j] * extension[j + i] / DefaultVariableMaster.qrs_derivative_scale;
            }
            input[i] = sum * sum;
        }
    }


    public double setIntegratorThreshold(double[] integrator) {
        int length = integrator.length;
        double integrator_sort[] = new double[length];

        for (int i = 0; i < length; i++) {
            integrator_sort[i] = integrator[i];
        }
        Arrays.sort(integrator_sort);

        int maxLoc = (int) Math.ceil(length * DefaultVariableMaster.qrs_integrator_max);
        int minLoc = (int) Math.ceil(length * DefaultVariableMaster.qrs_integrator_min);

        double maxVal = integrator_sort[maxLoc];
        double minVal = integrator_sort[minLoc];

        double threshold = (maxVal - minVal) / DefaultVariableMaster.qrs_integrator_ThFactor;

        for (int i = 0; i < length; i++) {
            if (integrator[i] < threshold) {
                integrator[i] = 0;
            }
        }

        return threshold;
    }

    /**
     * Peak detection for array
     */
    public int[] peakDetection(double[] input, double delta) {
        double minimum = 100000, maximum = -100000;
        double maxpos = 0;
        double lookformax = 1;
        double thisVar = 0;
        int countMax = 0;
        int countMin = 0;
        double[] peakLoc = new double[input.length];

        for (int ind = 0; ind < input.length; ind++) {
            thisVar = input[ind];
            //check max and min are greater and lesser to x[y][0] respectively
            if (thisVar > maximum) {
                maximum = thisVar;
                maxpos = ind;
            }
            if (thisVar < minimum) {
                minimum = thisVar;
            }

            if (lookformax == 1) {
                if (thisVar < (maximum - delta)) {
                    peakLoc[countMax] = maxpos;            //first col has positions
                    countMax = countMax + 1;                        //next row
                    minimum = thisVar;
                    lookformax = 0;
                }
            } else if (lookformax == 0) {
                if (thisVar > (minimum + delta)) {
                    countMin = countMin + 1;
                    maximum = thisVar;
                    maxpos = ind;
                    lookformax = 1;
                }
            }
        }

        int count = 0;
        if (peakLoc[0] >= 0 && peakLoc[1] > 0) {
            count = count + 1;
        }
        for (int i = 1; i < peakLoc.length; i++) {
            if (peakLoc[i] > 0) {
                count = count + 1;
            } else {
                break;
            }
        }

        int[] peakLoc_fin = new int[count];
        for (int i = 0; i < count; i++) {
            peakLoc_fin[i] = (int) (Math.floor(peakLoc[i])); // in case , we get decimal.
        }
        return peakLoc_fin;
    }

    public Object[] channelSelection(int[] qrs1, int[] qrs2, int[] qrs3, int[] qrs4, int threshold) {
        /**
         * Channel selection part
         */

        int len1 = qrs1.length;
        int len2 = qrs2.length;
        int len3 = qrs3.length;
        int len4 = qrs4.length;

        double ind1 = 0;
        double ind2 = 0;
        double ind3 = 0;
        double ind4 = 0;
        // to get the start index in each channel
        int startInd1 = -1;
        int startInd2 = -1;
        int startInd3 = -1;
        int startInd4 = -1;
        if (len1 > 3) {
            int nIt = len1 - 3;
            double var1[] = new double[nIt];
            double t1, t2, t3, mean;
            for (int i = 0; i < nIt; i++) {
                t1 = qrs1[i + 1] - qrs1[i];
                t2 = qrs1[i + 2] - qrs1[i + 1];
                t3 = qrs1[i + 3] - qrs1[i + 2];

                mean = (t1 + t2 + t3) / 3;

                var1[i] = Math.sqrt(((t1 - mean) * (t1 - mean) + (t2 - mean) * (t2 - mean) + (t3 - mean) * (t3 - mean)) / 2);
                if (var1[i] < threshold) {
                    ind1 = ind1 + 1;
                    if (startInd1 == -1) {
                        startInd1 = i;
                    }
                }
            }
            ind1 = ind1 / nIt;
        }

        if (len2 > 3) {
            int nIt = len2 - 3;
            double var2[] = new double[nIt];
            double t1, t2, t3, mean;
            for (int i = 0; i < nIt; i++) {
                t1 = qrs2[i + 1] - qrs2[i];
                t2 = qrs2[i + 2] - qrs2[i + 1];
                t3 = qrs2[i + 3] - qrs2[i + 2];

                mean = (t1 + t2 + t3) / 3;

                var2[i] = Math.sqrt(((t1 - mean) * (t1 - mean) + (t2 - mean) * (t2 - mean) + (t3 - mean) * (t3 - mean)) / 2);
                if (var2[i] < threshold) {
                    ind2 = ind2 + 1;
                    if (startInd2 == -1) {
                        startInd2 = i;
                    }
                }
            }
            ind2 = ind2 / nIt;
        }

        if (len3 > 3) {
            int nIt = len3 - 3;
            double var3[] = new double[nIt];
            double t1, t2, t3, mean;
            for (int i = 0; i < nIt; i++) {
                t1 = qrs3[i + 1] - qrs3[i];
                t2 = qrs3[i + 2] - qrs3[i + 1];
                t3 = qrs3[i + 3] - qrs3[i + 2];

                mean = (t1 + t2 + t3) / 3;

                var3[i] = Math.sqrt(((t1 - mean) * (t1 - mean) + (t2 - mean) * (t2 - mean) + (t3 - mean) * (t3 - mean)) / 2);
                if (var3[i] < threshold) {
                    ind3 = ind3 + 1;
                    if (startInd3 == -1) {
                        startInd3 = i;
                    }
                }
            }
            ind3 = ind3 / nIt;
        }

        if (len4 > 3) {
            int nIt = len4 - 3;
            double var4[] = new double[nIt];
            double t1, t2, t3, mean;
            for (int i = 0; i < nIt; i++) {
                t1 = qrs4[i + 1] - qrs4[i];
                t2 = qrs4[i + 2] - qrs4[i + 1];
                t3 = qrs4[i + 3] - qrs4[i + 2];

                mean = (t1 + t2 + t3) / 3;

                var4[i] = Math.sqrt(((t1 - mean) * (t1 - mean) + (t2 - mean) * (t2 - mean) + (t3 - mean) * (t3 - mean)) / 2);
                if (var4[i] < threshold) {
                    ind4 = ind4 + 1;
                    if (startInd4 == -1) {
                        startInd4 = i;
                    }
                }
            }
            ind4 = ind4 / nIt;
        }
        // FInd the maximum value of 'ind'
        // Have to add mean RR value also to this computation to get better estimate of 'ch'
        double ind = ind1;
        int length_Final = len1;
        int ch = 1;
        double RRmean = 0;
        for (int i = 0; i < len1 - 1; i++) {
            RRmean = RRmean + qrs1[i + 1] - qrs1[i];
        }
        RRmean = RRmean / (len1 - 1);
        if (ind2 == ind) {
            double RRmean2 = 0;
            for (int i = 0; i < len2 - 1; i++) {
                RRmean2 = RRmean2 + qrs2[i + 1] - qrs2[i];
            }
            RRmean2 = RRmean2 / (len2 - 1);
            if (RRmean < RRmean2) {
                ind = ind2;
                ch = 2;
                length_Final = len2;
                RRmean = RRmean2;
            }
        } else if (ind2 > ind) {
            ind = ind2;
            ch = 2;
            length_Final = len2;
            double RRmean2 = 0;
            for (int i = 0; i < len2 - 1; i++) {
                RRmean2 = RRmean2 + qrs2[i + 1] - qrs2[i];
            }
            RRmean = RRmean2 / (len2 - 1);
        }
        if (ind3 == ind) {
            double RRmean3 = 0;
            for (int i = 0; i < len3 - 1; i++) {
                RRmean3 = RRmean3 + qrs3[i + 1] - qrs3[i];
            }
            RRmean3 = RRmean3 / (len3 - 1);
            if (RRmean < RRmean3) {
                ind = ind3;
                ch = 3;
                length_Final = len3;
                RRmean = RRmean3;
            }
        } else if (ind3 > ind) {
            ind = ind3;
            ch = 3;
            length_Final = len3;
            double RRmean3 = 0;
            for (int i = 0; i < len3 - 1; i++) {
                RRmean3 = RRmean3 + qrs3[i + 1] - qrs3[i];
            }
            RRmean = RRmean3 / (len3 - 1);
        }
        if (ind4 > ind) {
            double RRmean4 = 0;
            for (int i = 0; i < len4 - 1; i++) {
                RRmean4 = RRmean4 + qrs4[i + 1] - qrs4[i];
            }
            RRmean4 = RRmean4 / (len4 - 1);
            if (RRmean < RRmean4) {
                ind = ind4;
                ch = 4;
                length_Final = len4;
                RRmean = RRmean4;
            }
        } else if (ind4 > ind) {
            ind = ind4;
            ch = 4;
            length_Final = len4;
            double RRmean4 = 0;
            for (int i = 0; i < len4 - 1; i++) {
                RRmean4 = RRmean4 + qrs4[i + 1] - qrs4[i];
            }
            RRmean = RRmean4 / (len4 - 1);
        }
        /**
         * Get the start Index and qrs values to find the final QRS.
         */
        int[] qrs = new int[length_Final];
        int startIndex = -1;
        if (ch == 1) {
            startIndex = startInd1;

            for (int i = 0; i < length_Final; i++) {
                qrs[i] = qrs1[i];
            }
        } else if (ch == 2) {
            startIndex = startInd2;
            for (int i = 0; i < length_Final; i++) {
                qrs[i] = qrs2[i];
            }
        } else if (ch == 3) {
            startIndex = startInd3;
            for (int i = 0; i < length_Final; i++) {
                qrs[i] = qrs3[i];
            }
        } else if (ch == 4) {
            startIndex = startInd4;
            for (int i = 0; i < length_Final; i++) {
                qrs[i] = qrs4[i];
            }
        }


        return new Object[]{qrs, startIndex};
    }


}//close class










