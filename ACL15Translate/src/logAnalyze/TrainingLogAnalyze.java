package logAnalyze;

import java.io.*;
import java.util.*;

public class TrainingLogAnalyze {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		String sLine;
		List<List<Integer>> errorTakenPlace = new ArrayList<List<Integer>>();
		int startIdx = -1;
		while((sLine = in.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			String[] smallunits = sLine.split("\\s+");
			
			if(smallunits.length == 3 
			   && smallunits[0].equals("Test") 
			   && smallunits[1].equals("iteration"))
			{
				errorTakenPlace.add(new ArrayList<Integer>());
				if(startIdx < 0)
				{
					startIdx = Integer.parseInt(smallunits[2]);
				}
			}
			else if(smallunits.length == 5 
					&& smallunits[0].equals("Training") 
					&& smallunits[1].equals("error")
					&& smallunits[2].equals("at") 
					&& smallunits[3].equals("character"))
			{
				int errorposition = Integer.parseInt(smallunits[4]);
				errorTakenPlace.get(errorTakenPlace.size()-1).add(errorposition);
			}
		}
		
		in.close();
		
		for(int idx = 0; idx < errorTakenPlace.size();idx++)
		{
			int total = 0;
			for(int idy = 0; idy < errorTakenPlace.get(idx).size(); idy++)
			{
				total += errorTakenPlace.get(idx).get(idy);
			}
			
			double average = total * 1.0 / errorTakenPlace.get(idx).size();
			
			System.out.println(String.format("idx:%d  avg:%f", startIdx+idx,average));
		}
		


	}

}
