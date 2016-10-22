package corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FindMaxDevIterationTestScoreDiscourse {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String markDIS = "DIS:";
		String markNUCLEAR= "NUCLEAR:";
		String markSPAN = "SPAN:";
		String markEDU = "EDU:";
		String markSYN = "SYN:";
		int maxIteration = 1000;
		String modelName = null;
		if(args.length > 1)
		{
			try
			{
				maxIteration = Integer.parseInt(args[1]);
			}
			catch (Exception e)
			{
				modelName = args[1];
			}
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		String sLine = null;

		double bestDevSYN = 0.0, bestDevEDU = 0.0, bestDevSPAN = 0.0, bestDevNUCLEAR = 0.0, bestDevDIS = 0.0;
		double bestTestSYN = 0.0, bestTestEDU = 0.0, bestTestSPAN = 0.0, bestTestNUCLEAR = 0.0, bestTestDIS = 0.0;
		int bestIteration = -1;
		
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.startsWith("Test iteration "))
			{
				boolean bDevParse = false;
				int curIteration = -1;
				String[] units = sLine.split("\\s+");
				curIteration = Integer.parseInt(units[units.length-1]);
				if(curIteration > maxIteration) break;
				
				double curDevSYN = 0.0, curDevEDU = 0.0, curDevSPAN = 0.0, curDevNUCLEAR = 0.0, curDevDIS = 0.0;
				double curTestSYN = 0.0, curTestEDU = 0.0, curTestSPAN = 0.0, curTestNUCLEAR = 0.0, curTestDIS = 0.0;
				double curscore = 0.0;
				
				while ((sLine = in.readLine()) != null) {
					sLine = sLine.trim();
					if(sLine.indexOf("Parsing has finished successfully.") != -1)
					{
						bDevParse = !bDevParse;
					}									

					if(sLine.startsWith(markDIS))
					{
						int sfscoreIndex = sLine.lastIndexOf("F=");
						String sscorestr = sLine.substring(sfscoreIndex + 2).trim();
						curscore = Double.parseDouble(sscorestr);
						if(bDevParse)
						{
							curDevDIS = curscore;
						}
						else
						{
							curTestDIS = curscore;
						}
					}
					else if(sLine.startsWith(markNUCLEAR))
					{
						int sfscoreIndex = sLine.lastIndexOf("F=");
						String sscorestr = sLine.substring(sfscoreIndex + 2).trim();
						curscore = Double.parseDouble(sscorestr);
						if(bDevParse)
						{
							curDevNUCLEAR = curscore;
						}
						else
						{
							curTestNUCLEAR = curscore;
						}
					}
					else if(sLine.startsWith(markSPAN))
					{
						int sfscoreIndex = sLine.lastIndexOf("F=");
						String sscorestr = sLine.substring(sfscoreIndex + 2).trim();
						curscore = Double.parseDouble(sscorestr);
						if(bDevParse)
						{
							curDevSPAN = curscore;
						}
						else
						{
							curTestSPAN = curscore;
						}
					}					
					else if(sLine.startsWith(markEDU))
					{
						int sfscoreIndex = sLine.lastIndexOf("F=");
						String sscorestr = sLine.substring(sfscoreIndex + 2).trim();
						curscore = Double.parseDouble(sscorestr);
						if(bDevParse)
						{
							curDevEDU = curscore;
						}
						else
						{
							curTestEDU = curscore;
						}
					}
					else if(sLine.startsWith(markSYN))
					{
						int sfscoreIndex = sLine.lastIndexOf("F=");
						String sscorestr = sLine.substring(sfscoreIndex + 2).trim();
						curscore = Double.parseDouble(sscorestr);
						if(bDevParse)
						{
							curDevSYN = curscore;
						}
						else
						{
							curTestSYN = curscore;
						}
					}
					
					if(curDevDIS > 0.0001 && curDevNUCLEAR > 0.0001 && curDevSPAN > 0.0001 && curDevEDU > 0.0001 && curDevSYN > 0.0001 &&
							curTestDIS > 0.0001 && curTestNUCLEAR > 0.0001 && curTestSPAN > 0.0001 && curTestEDU > 0.0001 && curTestSYN > 0.0001)
					{
						break;
					}

				}
				
				if(curDevDIS > bestDevDIS || (curDevDIS == bestDevDIS && curDevNUCLEAR > bestDevNUCLEAR)
				  || (curDevDIS == bestDevDIS && curDevNUCLEAR == bestDevNUCLEAR && curDevSPAN > bestDevSPAN)
				  || (curDevDIS == bestDevDIS && curDevNUCLEAR == bestDevNUCLEAR && curDevSPAN == bestDevSPAN && curDevEDU > bestDevEDU)
				  || (curDevDIS == bestDevDIS && curDevNUCLEAR == bestDevNUCLEAR && curDevSPAN == bestDevSPAN && curDevEDU == bestDevEDU && curDevSYN > bestDevSYN))
				{
					bestIteration = curIteration;
					bestDevDIS = curDevDIS;
					bestDevNUCLEAR = curDevNUCLEAR;
					bestDevSPAN = curDevSPAN;
					bestDevEDU = curDevEDU;
					bestDevSYN = curDevSYN;
					
					bestTestDIS = curTestDIS;
					bestTestNUCLEAR = curTestNUCLEAR;
					bestTestSPAN = curTestSPAN;
					bestTestEDU = curTestEDU;
					bestTestSYN = curTestSYN;
					if(modelName != null)
					{
						String modelFileName = String.format("%s.%d", modelName, curIteration);
						
						File file = new File(modelFileName);
						String absoluteFilePath = file.getAbsolutePath();
						int lastIndex = absoluteFilePath.lastIndexOf(File.separator);
						String targetFileName = absoluteFilePath.substring(0, lastIndex+1) + "joint.best";
						
						File filebest = new File(targetFileName);
						if (file.isFile() && file.exists()) {
							if (filebest.isFile() && filebest.exists()) { 
								filebest.delete();  							
							    file.renameTo(filebest);
						    }
					    }
						
						//System.out.println("Rename file " + modelFileName + " to " + targetFileName);
						
					}
				}
				else
				{
					if(modelName != null)
					{
						String modelFileName = String.format("%s.%d", modelName, curIteration);
						//System.out.println("Delete file " + modelFileName + "......");
						File file = new File(modelFileName);
						if (file.isFile() && file.exists()) {  
					        file.delete();   
					    } 
					}
				}
				
				
			}

		}
		
		in.close();
		
		bestDevDIS = (bestDevDIS * 10000) /100;
		bestDevNUCLEAR = (bestDevNUCLEAR * 10000) /100;
		bestDevSPAN = (bestDevSPAN * 10000) /100;
		bestDevEDU = (bestDevEDU * 10000) /100;
		bestDevSYN = (bestDevSYN * 10000) /100;

		bestTestDIS = (bestTestDIS * 10000) /100;
		bestTestNUCLEAR = (bestTestNUCLEAR * 10000) /100;
		bestTestSPAN = (bestTestSPAN * 10000) /100;
		bestTestEDU = (bestTestEDU * 10000) /100;
		bestTestSYN = (bestTestSYN * 10000) /100;

		
		String outStr = String.format("best iter: %2d:  DEV(DIS:%.2f NUCLEAR:%.2f SPAN:%.2f EDU:%.2f SYN:%.2f)\tTest(DIS:%.2f NUCLEAR:%.2f SPAN:%.2f EDU:%.2f SYN:%.2f)", 
				bestIteration, bestDevDIS, bestDevNUCLEAR, bestDevSPAN, bestDevEDU, bestDevSYN, bestTestDIS, bestTestNUCLEAR, bestTestSPAN, bestTestEDU, bestTestSYN);
		System.out.println(outStr);
		


	}

}
