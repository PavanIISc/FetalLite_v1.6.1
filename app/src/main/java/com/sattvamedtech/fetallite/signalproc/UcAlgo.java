package com.sattvamedtech.fetallite.signalproc;

import com.sattvamedtech.fetallite.helper.Logger;

/**
 * 
 * @version 7Th July, 2017
 * 			Added 20 point output for 1 itertion of UC
 * 			Duration of computation reduced to 5000 samples ( 5sec)
 * 			Shift for each iteration changed to 500 samples (0.5 sec)
 *
 * @version 12th Feb, 2017
 *			Created new UC Algorithm
 * @author kishoresubramanian
 *
 */
public class UcAlgo 
{
	
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();
	/**
	 * UcAlgoDwt : Find the UC graph
	 * 
	 * @param iInput : Input data
	 * @return		 : Energy values of 2- low pass subbands 
	 */
	public double[] ucAlgoDwt(double[] iInput) throws Exception
	{
		/**
		 * Input is 15000 array
		 */
		
		for (int i =0; i<Constants.NO_OF_SAMPLES; i++){
			iInput[i] = (iInput[i] * 24 * (Math.pow(2,23)-1) )/ (Math.pow(2,12) * 4.5);

		}
		//Logger.logInfo("UC" , "val = " +iInput[0]);
		int aIter = Constants.NO_OF_PRINT_VALUES;
//		double[] UcPerc = new double[aIter];
//		double[] UcEnergy = new double[aIter];
		long startTime = System.currentTimeMillis();
		
		int aPatchLength = 5000;
		int aPatchShift = 500;

		int aDownsampleScale = 100;
		int aDownsampleLength = aPatchLength/aDownsampleScale;

		double[] aInput = new double[aPatchLength];
		double[] aUcEnergy = new double[aIter];
		for (int i =0; i<aIter; i++)
		{
			for (int k = 0; k<aPatchLength; k++)
			{
				aInput[k] = iInput[k+aPatchShift*i];
			}
			double ahigh[] = {1,	-0.997489878867098};
			double bhigh[] = {0.998744939433549,	-0.998744939433549};
			double zhigh = -0.998744939433532;
	
			mMatrixFunctions.filterLoHi(aInput, ahigh, bhigh, zhigh);
			
			double alow[] = {1	,-0.975177876180649};
			double blow[] = {0.0124110619096754,	0.0124110619096754};
			double zlow = 0.987588938090325;
			
			mMatrixFunctions.filterLoHi(aInput, alow, blow, zlow);
			
			double[] input_dwt = new double[aDownsampleLength];
			for (int k=0; k<aDownsampleLength; k++)
			{
				input_dwt[k] = aInput[k*aDownsampleScale];
			}

			DecimatedWT UcDwt = new DecimatedWT();
			aUcEnergy[i] = UcDwt.dwt(input_dwt);
			
			
		}
		long EndTime = System.currentTimeMillis();
		
		System.out.println("Takes "+ ( EndTime - startTime) +" ms to execute "+aIter+ " iterations.");

		return aUcEnergy;
		
	}
	
	
}
