package corpus;

import java.io.*;
import java.util.*;

public class EvaluateLabelers {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine_gold = null;
		String sLine_pred = null;
		BufferedReader in_gold = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		int column_gold = Integer.parseInt(args[1]);
		
		BufferedReader in_pred = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[2]), "UTF8"));
		int column_pred = Integer.parseInt(args[3]);
		
		
		List<String> goldLabels = new ArrayList<String>();
		List<String> predLabels = new ArrayList<String>();

		int totalInstances = 0, correctInstances = 0;
		while ((sLine_gold = in_gold.readLine()) != null
			  && (sLine_pred = in_pred.readLine()) != null ) {
			sLine_gold = sLine_gold.trim();
			sLine_pred = sLine_pred.trim();
			if(sLine_gold.equals(""))
			{
				if(!sLine_pred.equals(""))
				{
					System.out.println("error: " + sLine_pred);
				}
				if(goldLabels.size() > 0)
				{
					String goldLabel = goldLabels.get(goldLabels.size()-1);
					String predLabel = predLabels.get(predLabels.size()-1);
					if(goldLabel.equalsIgnoreCase("history") || predLabel.equalsIgnoreCase("history") )
					{
						System.out.println("error ");
					}
					else
					{
						totalInstances++;
						if(goldLabel.equalsIgnoreCase(predLabel))
						{
							correctInstances++;
						}
					}
				}
				
				goldLabels = new ArrayList<String>();
				predLabels = new ArrayList<String>();
				continue;
			}
			else
			{
				String[] smallunits_gold = sLine_gold.split("\\s+");
				if(column_gold >= 0)
				{
					if(smallunits_gold.length < column_gold + 1)
					{
						System.out.println("error: " + sLine_gold);
						continue;
					}
					
					goldLabels.add(smallunits_gold[column_gold]);
				}
				else
				{
					if(smallunits_gold.length < -column_gold)
					{
						System.out.println("error: " + sLine_gold);
						continue;
					}
					
					goldLabels.add(smallunits_gold[smallunits_gold.length + column_gold]);
				}

				String[] smallunits_pred = sLine_pred.split("\\s+");
				if(column_pred >= 0)
				{
					if(smallunits_pred.length < column_pred + 1)
					{
						System.out.println("error: " + sLine_pred);
						continue;
					}
					
					predLabels.add(smallunits_pred[column_pred]);
				}
				else
				{
					if(smallunits_pred.length < -column_pred)
					{
						System.out.println("error: " + sLine_pred);
						continue;
					}
					
					predLabels.add(smallunits_pred[smallunits_pred.length + column_pred]);
				}
			}			
		}
		
		if(goldLabels.size() > 0)
		{
			String goldLabel = goldLabels.get(goldLabels.size()-1);
			String predLabel = predLabels.get(predLabels.size()-1);
			if(goldLabel.equalsIgnoreCase("history") || predLabel.equalsIgnoreCase("history") )
			{
				System.out.println("error ");
			}
			else
			{
				totalInstances++;
				if(goldLabel.equalsIgnoreCase(predLabel))
				{
					correctInstances++;
				}
			}
		}

		System.err.println(String.format("Accuracy:\tP=%d/%d=%.5f",
				correctInstances, totalInstances, correctInstances*1.0/totalInstances));

		in_gold.close();
		in_pred.close();
	}

	
	
}
