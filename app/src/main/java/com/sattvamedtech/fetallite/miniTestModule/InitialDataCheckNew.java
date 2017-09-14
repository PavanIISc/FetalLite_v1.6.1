//package com.sattvamedtech.fetallite.miniTestModule;
//
//public class InitialDataCheckNew
//{
//	static double impulseThreshold = 10*Math.pow(10, -3);
//	static double flatThreshold = 1*Math.pow(10, -12); // can change to 12.
//	static int percentImpluse = 5;
//	static int percentFlat = 10;
//
//	static double noiseThreshold = 10* Math.pow(10, -6);
//	static int percentNoisy = 40;
//	public static void main (String[] args)
//	{
//		csvread aCsv = new csvread();
//
//		double[][] input = aCsv.csv();
//		int signal_Length = 5000;
//		int noChannels = 4;
//		double samplingFrequency = 1000;
////		double[][] input = new double[signal_Length][noChannels];
//
//
//		/**
//		 * Check for impulses and flatSignal in raw data
//		 */
//		double[][] derivativeSignal = new double[signal_Length-1][noChannels];
//
//
//
//		int impulseCounter[] = new int[noChannels];
//		int flatCounter[] = new int[noChannels];
//
//		for (int i = 1; i<signal_Length; i++)
//		{
//			derivativeSignal[i-1][0] = Math.abs(input[i][0] - input[i-1][0]) * samplingFrequency;
//			derivativeSignal[i-1][1] = Math.abs(input[i][1] - input[i-1][1]) * samplingFrequency;
//			derivativeSignal[i-1][2] = Math.abs(input[i][2] - input[i-1][2]) * samplingFrequency;
//			derivativeSignal[i-1][3] = Math.abs(input[i][3] - input[i-1][3]) * samplingFrequency;
//
//			if (derivativeSignal[i-1][0] > impulseThreshold)
//			{
//				impulseCounter[0] = impulseCounter[0]+1;
//			}
//			if (derivativeSignal[i-1][1] > impulseThreshold)
//			{
//				impulseCounter[1] = impulseCounter[1]+1;
//			}
//			if (derivativeSignal[i-1][2] > impulseThreshold)
//			{
//				impulseCounter[2] = impulseCounter[2]+1;
//			}
//			if (derivativeSignal[i-1][3] > impulseThreshold)
//			{
//				impulseCounter[3] = impulseCounter[3]+1;
//			}
//
//			if (derivativeSignal[i-1][0] < flatThreshold)
//			{
//				flatCounter[0] = flatCounter[0]+1;
//			}
//			if (derivativeSignal[i-1][1] < flatThreshold)
//			{
//				flatCounter[1] = flatCounter[1]+1;
//			}
//			if (derivativeSignal[i-1][2] < flatThreshold)
//			{
//				flatCounter[2] = flatCounter[2]+1;
//			}
//			if (derivativeSignal[i-1][3] < flatThreshold)
//			{
//				flatCounter[3] = flatCounter[3]+1;
//			}
//
//
//		}
//
//
//		for (int i =0; i<noChannels; i++)
//		{
//			if (flatCounter[i]/signal_Length*100 > percentFlat )
//				System.out.println("Flat Signal in Channel :"+i);
//			if (impulseCounter[i]/signal_Length*100 > percentImpluse )
//				System.out.println("Impulse Signal in Channel :"+i);
//		}
//
//		/**
//		 * Check for floor noise levels
//		 * 1. Detect QRS Mother peaks on filtered data
//		 * 2. Check for 3- 20ms patchs between 2 maternal QRS locations
//		 */
//
//		FilterLoHiNotch filt = new FilterLoHiNotch();
//		double[][] Ecg_filt = filt.filterParallel(input);
//
//
//		MQRSDetection mqrsDet = new MQRSDetection();
//		int[] qrsM = mqrsDet.mQRS(Ecg_filt);
//
//		if (qrsM.length <2)
//		{
//			System.out.println("no Maternal Detected");
//		}
//
//		int noPatches = (qrsM.length-1)*3;
//
//		int patchLoc[] = new int[noPatches];
//		int patchSize = 20;
//		int factor = 4;
//		int count = -1;
//		for (int i = 0; i<qrsM.length-1; i++)
//		{
//			for (int j = (factor-1); j>0; j--)
//			{
//				patchLoc[count+j] = qrsM[i] + (qrsM[i+1] - qrsM[i])/factor * j;
//			}
//			count = count + (factor-1);
//		}
//
//		int signChangeCounter[][] = new int[noPatches][noChannels];
//		double[][] signChangeMaxValue = new double [noPatches][noChannels];
//		double[][] absoluteDiff = new double [noPatches][noChannels];
//
//		double[] maxValue = new double[noPatches];
//		double[] minValue = new double[noPatches];
//
//		double[] maxValuesSignChange = new double[noPatches];
//		double[] minValuesSignChange = new double[noPatches];
//
//
//
//		int noOfNoise[] = new int[noChannels];
//		int noOfNoiseSign[] = new int[noChannels];
//
//		double maxVal, diff;
//		int ind, signFlag, countSignChangePN, countSignChangeNP , countMaxDiff, countMaxSignDiff;
//		for (int k = 0; k<noChannels; k++)
//		{
//			countMaxDiff = 0;
//			countMaxSignDiff = 0;
//			for (int i = 0; i<noPatches; i++)
//			{
//				maxValue[i] = -100;
//				minValue[i] = 100;
//				maxValuesSignChange[i] = 0;
//				minValuesSignChange[i] = 0;
//			}
//			maxVal = -1000;
//
//			for (int i = 0; i<noPatches; i++)
//			{
//				ind = patchLoc[i];
//				for (int j = 0; j<patchSize; j++)
//				{
//					if (Ecg_filt[ind+j][k] > maxValue[i])
//					{
//						maxValue[i] = Ecg_filt[ind+j][k];
//					}
//
//					if (Ecg_filt[ind+j][k] < minValue[i])
//					{
//						minValue[i] = Ecg_filt[ind+j][k];
//					}
//				}
//
//				signFlag = 0;
//				countSignChangePN = 0;
//				countSignChangeNP = 0;
//
//				if (Ecg_filt[ind+1][k] - Ecg_filt[ind][k] <0)
//				{
//					signFlag = -1;
//				}
//				else
//				{
//					signFlag = 1;
//				}
//
//				for (int j = 2; j<patchSize; j++)
//				{
//					if (Ecg_filt[ind+j][k] - Ecg_filt[ind+j-1][k] <0)
//					{
//						if (signFlag == 1)
//						{
//							signFlag = -1;
//							countSignChangePN = countSignChangePN +1;
//							maxValuesSignChange[countSignChangePN] = Ecg_filt[ind+j-1][k];
//						}
//						else
//						{
//							if (signFlag == -1)
//							{
//								signFlag = 1;
//								countSignChangeNP = countSignChangeNP +1;
//								minValuesSignChange[countSignChangeNP] = Ecg_filt[ind+j-1][k];
//							}
//						}
//					}
//				}
//				if (countSignChangeNP > countSignChangePN)
//				{
//					signChangeCounter[i][k] = countSignChangePN;
//		            for (int j = 0; j<countSignChangePN; j++)
//		            {
//		                diff  = Math.abs(minValuesSignChange[j] - maxValuesSignChange[j]);
//		                if (diff > maxVal)
//		                {
//		                    maxVal = diff;
//		                }
//		            }
//				}
//				else
//				{
//					signChangeCounter[i][k] = countSignChangeNP;
//		            for (int j = 0; j<countSignChangeNP; j++)
//		            {
//		                diff  = Math.abs(minValuesSignChange[j] - maxValuesSignChange[j]);
//		                if (diff > maxVal)
//		                {
//		                    maxVal = diff;
//		                }
//		            }
//				}
//
//				signChangeMaxValue[i][k] = maxVal;
//				absoluteDiff[i][k] = Math.abs(maxValue[i] - minValue[i]);
//				if (absoluteDiff[i][k] > noiseThreshold)
//				{
//					countMaxDiff = countMaxDiff + 1;
//				}
//				if (signChangeMaxValue[i][k] > noiseThreshold  && signChangeCounter[i][k] >1)
//				{
//					countMaxSignDiff = countMaxSignDiff + 1;
//				}
//			}
//
//
//
//		    noOfNoise[k] = countMaxDiff;
//		    noOfNoiseSign[k] = countMaxSignDiff;
//		    if (countMaxSignDiff > percentNoisy/100* noPatches)
//		    	System.out.println("Channel- "+k+" is noisy!!!");
//
//
//
//		}
//
//
//
//	}
//}
