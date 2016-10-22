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
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[4]), "UTF-8"), false);
		int manner = 1;
		//if(args.length > 4)manner = Integer.parseInt(args[4]);

		SeqMetric metric = new SeqMetric();
		List<String> goldLabels = new ArrayList<String>();
		List<String> predLabels = new ArrayList<String>();
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
					SegEvaluation(goldLabels, predLabels, metric, manner, out);
				}
				out.println();
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
					
					goldLabels.add(smallunits_gold[column_gold].toLowerCase());
				}
				else
				{
					if(smallunits_gold.length < -column_gold)
					{
						System.out.println("error: " + sLine_gold);
						continue;
					}
					
					goldLabels.add(smallunits_gold[smallunits_gold.length + column_gold].toLowerCase());
				}

				String[] smallunits_pred = sLine_pred.split("\\s+");
				if(column_pred >= 0)
				{
					if(smallunits_pred.length < column_pred + 1)
					{
						System.out.println("error: " + sLine_pred);
						continue;
					}
					
					predLabels.add(smallunits_pred[column_pred].toLowerCase());
				}
				else
				{
					if(smallunits_pred.length < -column_pred)
					{
						System.out.println("error: " + sLine_pred);
						continue;
					}
					
					predLabels.add(smallunits_pred[smallunits_pred.length + column_pred].toLowerCase());
				}
				
			}			
		}
		
		if(goldLabels.size() > 0)
		{
			SegEvaluation(goldLabels, predLabels, metric, manner, out);
		}

		metric.print();
		in_gold.close();
		in_pred.close();
		out.close();
	}

	
	public static void SegEvaluation(List<String> goldLabels, List<String> predLabels, SeqMetric metric, int manner, PrintWriter out)
	throws Exception{

		Set<String> goldSegs = getSegs(goldLabels, manner);
		Set<String> otherSegs = getSegs(predLabels, manner);
		metric.overall_label_count = metric.overall_label_count + goldSegs.size();
		metric.predicated_label_count = metric.predicated_label_count + otherSegs.size();
		Map<Integer, String> correctSegs = new HashMap<Integer, String>();;
		for(String oneSeg : goldSegs)
		{
			if(otherSegs.contains(oneSeg)){
				int endPos = oneSeg.indexOf(",");
				int position = Integer.parseInt(oneSeg.substring(1, endPos));
				correctSegs.put(position, oneSeg);
				metric.correct_label_count++;
			}
		}
		
		for(int idx = 0; idx < goldLabels.size(); idx++)
		{
			if(correctSegs.containsKey(idx))
			{
				out.print(correctSegs.get(idx).toUpperCase() + " ");
			}
		}
	}
	
	
	public static Set<String> getSegs(List<String> labels, int manner)
	{
		Set<String> results = new TreeSet<String>();
		int idx = 0;
		while(idx < labels.size()){
			String curStartLabel = labels.get(idx).toLowerCase();
			if(curStartLabel.startsWith("b-")){
				int idy = idx;
				int endpoint = -1;
				while(idy < labels.size()) {
					String curEndLabel = labels.get(idy).toLowerCase();
					if(curEndLabel.length() < 3
							|| (idy > idx && curEndLabel.startsWith("b-"))
							|| !curEndLabel.substring(2).equals(curStartLabel.substring(2))) {
						endpoint = idy - 1;
						break;
					}

				    endpoint = idy;
					
					idy++;
				}
				String label = curStartLabel;
				if (manner == 1 || manner == 2)label = label.substring(2);
				else if(manner == 3 || manner == 4) label = "xx";
				else System.out.println("error");
				String oneSeg = String.format("[%d,%d]%s", idx, endpoint, label);
				if(manner%2 == 0) oneSeg = String.format("[%d]%s", idx, label);
				results.add(oneSeg);
				idx = endpoint;								
			}
			idx++;
			
		}
		
		return results;
	}
}
