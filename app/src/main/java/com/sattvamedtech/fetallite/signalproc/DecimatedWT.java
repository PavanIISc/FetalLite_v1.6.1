package com.sattvamedtech.fetallite.signalproc;

/**
 *
 * @version 7th July, 2017
 * 			Use only 2 level DWT to find energy.
 * 			Removed percentage energy computation
 *
 * @version 12th Feb, 2017
 *			Created new 3- level DWT to find energy
 * @author kishoresubramanian
 */
public class DecimatedWT
{
	/**
	 * dwt : Find the energy of half of the frequency band
	 * 
	 * @param iInput 	: (double[][]) Input data
	 * @return	: (double[]) Energy
	 */
	public double dwt(double[] iInput)
	{
		
		int aLength_input = iInput.length;
		/** 
		 * LPF and HPF for wavelet transform
		 */
		double[] aLpf = {0.0273330683450780,	0.0295194909257746,	-0.0391342493023831,	0.199397533977394,	0.723407690402421,	0.633978963458212,	0.0166021057645223,	-0.175328089908450,	-0.0211018340247589,	0.0195388827352867};

		
		int aLengthLpf = aLpf.length;
		
		double[] aHpf = new double[aLengthLpf];	
		for (int i=0; i<=aLengthLpf-1 ; i++)
		{
			aHpf[i] = Math.pow(-1, (i+1))*aLpf[aLengthLpf-i-1];		
		}
		
		/**
		 *  WT  :: Level 1
		 */

		double[] aDecomp1 = analyse(iInput,aLpf,aHpf);

		/**
		 * Extract L and H from Decomp1 to pass for next level.
		 */
		int aLen2 = aLength_input/2;
		double aL[] = new double[aLen2];
		double aH[] = new double[aLen2];
		for (int i = 0; i<aLength_input; i++)
		{
			if (i < aLen2)
			{
				aL[i] = aDecomp1[i];
			}
			else
			{
				aH[i-aLen2] = aDecomp1[i];
			}
		}
		
		/**
		 *  WT  :: Level 2
		 */
		double[] aDecompL2 = analyse(aL, aLpf, aHpf);
		double[] aDecompH2 = analyse(aH, aLpf, aHpf);
		
		
//		/**
//		 * Extract LL, LH, HL and HH from DecompL2 and DecompH2 to pass for next level.
//		 */
//		int aLen3 = aLen2/2;
//		double aLL[] = new double[aLen3];
//		double aLH[] = new double[aLen3];
//		double aHL[] = new double[aLen3];
//		double aHH[] = new double[aLen3];
//		
//		
//		for (int i = 0; i<aLen2; i++)
//		{
//			if (i < aLen3)
//			{
//				aLL[i] = aDecompL2[i];
//				aHL[i] = aDecompH2[i];
//			}
//			else
//			{
//				aLH[i-aLen3] = aDecompL2[i];
//				aHH[i-aLen3] = aDecompH2[i];
//			}
//		}
//		
//		
//		
//		/**
//		 *  WT  :: Level 3
//		 */
//		double[] aDecompLL3 = analyse(aLL, aLpf, aHpf);
//		double[] aDecompLH3 = analyse(aLH, aLpf, aHpf);
//		double[] aDecompHL3 = analyse(aHL, aLpf, aHpf);
//		double[] aDecompHH3 = analyse(aHH, aLpf, aHpf);
//		
//		double aSumLL = 0;
//		double aSumLH = 0;
//		double aSumHL = 0;
//		double aSumHH = 0;
//		
//		for (int i = 0; i<aLen3; i++)
//		{
//			aSumLL = aSumLL + aDecompLL3[i]*aDecompLL3[i];
//			aSumLH = aSumLH + aDecompLH3[i]*aDecompLH3[i];
//			aSumHL = aSumHL + aDecompHL3[i]*aDecompHL3[i];
//			aSumHH = aSumHH + aDecompHH3[i]*aDecompHH3[i];
//		}
//		
		
		double aSumL = 0;
		for (int i = 0; i<aLen2 ; i++){
			aSumL = aSumL +  aDecompL2[i]*aDecompL2[i] ;
		}
		
		
	
		return aSumL;
		

	}

	/**
	 * analyse : Do 1-level decimated wavelet transform.
	 * 
	 * @param iInput : (double[]) Input data
	 * @param iLpf	 : (double[]) Low pass filter coefficients
	 * @param iHpf	 : (double[]) High pass filter coefficients
	 * @return       : (double[]) Wavelet transform coefficients
	 */
	private double[] analyse(double[] iInput, double[] iLpf, double[] iHpf) 
	{
		
		int aLenF = iLpf.length;
		int aLen = iInput.length;
		
		double[] aInput_ext = new double[aLen + aLenF]; 
		
		for (int i =0; i<aLen+aLenF; i++)
		{
			if (i >=0 && i <aLen)
			{
				aInput_ext[i] = iInput[i];
			}
			else if (i >=aLen && i< aLen+aLenF)
			{
				aInput_ext[i] = iInput[i-aLen];
			}
		}
		
		
		double[] aConv_ext = new double[aLen+2*aLenF -1];
		for (int i =0; i < aLen +aLenF; i++)
		{
			aConv_ext[i+aLenF-1] = aInput_ext[i];
		}
		
		double[] aLL = new double[aLen + aLenF-1];
		double[] aHH = new double[aLen + aLenF-1];
		
		int j =0;
		double aSumL, aSumH;
		while (j < aLen + aLenF-1)
		{
			aSumL = 0;
			aSumH = 0;
			for (int i =0; i<aLenF; i++)
			{
				aSumL = aSumL + iLpf[i] * aConv_ext[i+j];
				aSumH = aSumH + iHpf[i] * aConv_ext[i+j];
			}
			aLL[j] = aSumL;
			aHH[j] = aSumH;
			j = j+1;
		}
		
		int aLen_down; 
		int aLen_out;
		if (aLen %2 == 0)
		{
			aLen_down = aLen/2;
			aLen_out = aLen;
		}
		else
		{
			aLen_down =aLen/2+1;
			aLen_out = aLen+1;
		}
		

		double[] aLL_down = new double[aLen_down];
		double[] aHH_down = new double[aLen_down];
		
		
		j = 0;
		int i = aLenF-1;
		while (j < aLen_down && i < aLenF + aLen -1)
		{
			aLL_down[j] = aLL[i];
			aHH_down[j] = aHH[i];
			j++;
			i = i+2;
		}
		
		double aOut[] = new double[aLen_out];
		for (i = 0; i <aLen_out; i++)
		{
			if (i <aLen_down)
			{
				aOut[i] = aLL_down[i];
			}
			else
			{
				aOut[i] = aHH_down[i - aLen_down];
			}
		}

		return aOut;
	}
}
