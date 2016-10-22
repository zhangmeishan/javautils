package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

import mason.utils.MapSort;

public class FixedPhraseTableExtraction {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		double threshold = Double.parseDouble(args[2]);
		int maxSrcPhraseLength = 5;
		List<Map<String, Map<String, Double>>> src2tgt = new ArrayList<Map<String, Map<String, Double>>>();
		
		for(int idx = 1; idx <=maxSrcPhraseLength; idx++)
		{
			src2tgt.add(new HashMap<String, Map<String, Double>>());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(args[0]), "UTF-8"));
			
			
			String sLine = "";
			
			while( (sLine = in.readLine()) != null)
			{
				sLine = sLine.trim();
				if(sLine.isEmpty())continue;
				
				int split1 = sLine.indexOf("|||");
				int split2 = sLine.indexOf("|||", split1+1);
				
				if(split1 == -1 || split2 == -1)
				{
					System.out.println(sLine);
					continue;
				}
				
				String[] threeparts = new String[3];
				threeparts[0] = sLine.substring(0, split1).trim();
				threeparts[1] = sLine.substring(split1+3, split2).trim();
				threeparts[2] = sLine.substring(split2+3).trim();
							
				String thesrcpart = threeparts[0].trim();
				String thetgtpart = threeparts[1].trim();
				

				String[] thesrcwords = thesrcpart.split("\\s+");
				String[] thetgtwords = thetgtpart.split("\\s+");
				String[] weights = threeparts[2].trim().split("\\s+");
				if(weights.length != 4)
				{
					System.out.println(sLine);
					continue;
				}
				
				if(thetgtwords.length >= 6 || thetgtwords.length == 0) continue;

								
				boolean bAdd = true;
				if(thesrcwords.length != idx)
				{
					bAdd = false;
				}
				
				double weight1 = Double.parseDouble(weights[0]);
				double weight2 = Double.parseDouble(weights[1]);
				double weight3 = Double.parseDouble(weights[2]);
				double weight4 = Double.parseDouble(weights[3]);
				
				if(!bAdd || weight1 < threshold || weight2 < threshold 
				   || weight3 < threshold || weight4 < threshold )
				{
					bAdd = false;
				}
				
				List<String> thesmallertargets = new ArrayList<String>();
				if(bAdd)
				{
					List<String[]> smallerTargetParts = getAllComponents(thetgtwords, thetgtwords.length-1);
					for(String[] oneItem : smallerTargetParts)
					{
						if(oneItem == null) continue;
						String thesmallertarget = oneItem[0];
						for(int idk = 1; idk < oneItem.length; idk++)
						{
							thesmallertarget = thesmallertarget + " " + oneItem[idk];
						}
						
						thesmallertargets.add(thesmallertarget);
					}
					
					int smallerTargetNumbers = (int) (Math.pow(2, thetgtwords.length) -1); 
					if(smallerTargetNumbers != thesmallertargets.size())
					{
						System.out.println("error");
					}
						
				}
						
				if(bAdd && thesrcpart.equals("对手 。") && thetgtpart.equals("opponents ."))
				{
					System.out.println("debug");
				}
				for(int idy = 1; bAdd && idy < idx; idy++)
				{
					for(int idk = 0; bAdd && idk <= idx-idy; idk++)
					{
						String thesmallersource = thesrcwords[idk];
						for(int idz = idk+1; idz < idk +idy; idz++)
						{
							thesmallersource = thesmallersource + " " + thesrcwords[idz];
						}
						
						if(src2tgt.get(idy-1).containsKey(thesmallersource))
						{
							Map<String, Double>  tempMap = src2tgt.get(idy-1).get(thesmallersource);
							for(String thesmallertarget: thesmallertargets)
							{								
								if(tempMap.containsKey(thesmallertarget))
								{
									bAdd = false;
									break;
								}
									
							}
						}
						
					}
				}
				
				if(bAdd)
				{
					String oneSource = thesrcwords[0];
					for(int idk = 1; idk < thesrcwords.length; idk++)
					{
						oneSource = oneSource + " " + thesrcwords[idk];
					} 
					
					String oneTarget = thetgtwords[0];
					for(int idk = 1; idk < thetgtwords.length; idk++)
					{
						oneTarget = oneTarget + " " + thetgtwords[idk];
					} 
					
					if(!src2tgt.get(idx-1).containsKey(oneSource))
					{
						src2tgt.get(idx-1).put(oneSource, new HashMap<String, Double>());
					}
					
					src2tgt.get(idx-1).get(oneSource).put(oneTarget, weight1*weight2*weight3*weight4);
				}
				
			}
				
			in.close();
		}
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		for(int idx = 1; idx <=maxSrcPhraseLength; idx++)
		{
			for(String sourceKey : src2tgt.get(idx-1).keySet())
			{
				List<Entry<String, Double>> sortTargetValues = MapSort.MapDoubleSort(src2tgt.get(idx-1).get(sourceKey));
				
				for(int idk = 0; idk < sortTargetValues.size(); idk++)
				{
					writer.println(sourceKey + " ==> " + sortTargetValues.get(idk).getKey()
							+ " ==> " + sortTargetValues.get(idk).getValue()); 		
				}					
			}
		}
		
		writer.close();

	}
	
	
	static List<String[]> getAllComponents(String[] theWords, int endx)
	{
		List<String[]> allcomps = new ArrayList<String[]>();
		
		if(endx >= theWords.length || endx < 0)
		{
			System.out.println("impossible error");
			return null;
		}
		else if(endx == 0)
		{
			allcomps.add(null);
			String[] oneItem = new String[1];
			oneItem[0] = theWords[endx];
			allcomps.add(oneItem);
			return allcomps;
		}
		else
		{
			List<String[]> subcomps = getAllComponents(theWords, endx-1);
			for(String[] curItem : subcomps)
			{
				if(curItem == null)
				{
					allcomps.add(null);
					String[] oneItem = new String[1];
					oneItem[0] = theWords[endx];
					allcomps.add(oneItem);					
				}
				else
				{
					allcomps.add(curItem);
					String[] oneItem = new String[curItem.length+1];
					for(int idx = 0; idx < curItem.length; idx++)oneItem[idx] = theWords[idx];
					oneItem[curItem.length] = theWords[endx];
					allcomps.add(oneItem);	
				}
				
				
			}
			return allcomps;
		}
		
	}

}
