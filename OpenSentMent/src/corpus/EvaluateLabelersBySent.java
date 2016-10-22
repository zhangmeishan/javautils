package corpus;

import java.io.*;
import java.util.*;

public class EvaluateLabelersBySent {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine_gold = null;
		String sLine_pred1 = null;
		String sLine_pred2 = null;
		
		BufferedReader in_gold = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		int column_gold = Integer.parseInt(args[1]);
		
		BufferedReader in_pred1 = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[2]), "UTF8"));
		int column_pred1 = Integer.parseInt(args[3]);
		
		BufferedReader in_pred2 = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[4]), "UTF8"));		
		int column_pred2 = Integer.parseInt(args[5]);
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[6]), "UTF-8"));
		
		writer.println("discrete\tneural\tlabel");
		
		int manner = 1;
		if(args.length > 7)manner = Integer.parseInt(args[7]);

		SeqMetric metric1 = new SeqMetric();
		SeqMetric metric2 = new SeqMetric();
		List<String> goldLabels = new ArrayList<String>();
		List<String> pred1Labels = new ArrayList<String>();
		List<String> pred2Labels = new ArrayList<String>();
		while ((sLine_gold = in_gold.readLine()) != null
			  && (sLine_pred1 = in_pred1.readLine()) != null 
			  && (sLine_pred2 = in_pred2.readLine()) != null) {
			sLine_gold = sLine_gold.trim();
			sLine_pred1 = sLine_pred1.trim();
			sLine_pred2 = sLine_pred2.trim();
			if(sLine_gold.equals(""))
			{
				if(!sLine_pred1.equals("") || !sLine_pred2.equals(""))
				{
					System.out.println("error: " + sLine_pred1);
					System.out.println("error: " + sLine_pred2);
				}
				if(goldLabels.size() > 0)
				{
					metric1.reset(); metric2.reset();
					LabelEvaluation(goldLabels, pred1Labels, metric1, manner);
					LabelEvaluation(goldLabels, pred2Labels, metric2, manner);
					writer.println(String.format("%f\t%f\ta", metric1.getAccuracy(), metric2.getAccuracy()));
				}
				
				goldLabels = new ArrayList<String>();
				pred1Labels = new ArrayList<String>();
				pred2Labels = new ArrayList<String>();
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

				String[] smallunits_pred1 = sLine_pred1.split("\\s+");
				if(column_pred1 >= 0)
				{
					if(smallunits_pred1.length < column_pred1 + 1)
					{
						System.out.println("error: " + sLine_pred1);
						continue;
					}
					
					pred1Labels.add(smallunits_pred1[column_pred1]);
				}
				else
				{
					if(smallunits_pred1.length < -column_pred1)
					{
						System.out.println("error: " + sLine_pred1);
						continue;
					}
					
					pred1Labels.add(smallunits_pred1[smallunits_pred1.length + column_pred1]);
				}
				
				String[] smallunits_pred2 = sLine_pred2.split("\\s+");
				if(column_pred2 >= 0)
				{
					if(smallunits_pred2.length < column_pred2 + 1)
					{
						System.out.println("error: " + sLine_pred2);
						continue;
					}
					
					pred2Labels.add(smallunits_pred2[column_pred2]);
				}
				else
				{
					if(smallunits_pred2.length < -column_pred2)
					{
						System.out.println("error: " + sLine_pred2);
						continue;
					}
					
					pred2Labels.add(smallunits_pred2[smallunits_pred2.length + column_pred2]);
				}
			}				
		}
		
		if(goldLabels.size() > 0)
		{
			metric1.reset(); metric2.reset();
			LabelEvaluation(goldLabels, pred1Labels, metric1, manner);
			LabelEvaluation(goldLabels, pred2Labels, metric2, manner);
			writer.println(String.format("%f\t%f\ta", metric1.getAccuracy(), metric2.getAccuracy()));		
		}

		//metric.print();
		in_gold.close();
		in_pred1.close();
		in_pred2.close();
		writer.close();
	}

	
	public static void SegEvaluation(List<String> goldLabels, List<String> predLabels, SeqMetric metric, int manner)
	{

		Set<String> goldSegs = getSegs(goldLabels, manner);
		Set<String> otherSegs = getSegs(predLabels, manner);
		metric.overall_label_count = metric.overall_label_count + goldSegs.size();
		metric.predicated_label_count = metric.predicated_label_count + otherSegs.size();
		
		for(String oneSeg : goldSegs)
		{
			if(otherSegs.contains(oneSeg))metric.correct_label_count++;;
		}
	}
	
	
	public static Set<String> getSegs(List<String> labels, int manner)
	{
		Set<String> results = new HashSet<String>();
		int beginId = 0;
		for(int idx = 1; idx < labels.size(); idx++)
		{
			String curLabel = labels.get(idx).toLowerCase();
			if(!curLabel.startsWith("i-"))
			{
				String label = labels.get(beginId);
				String oneSeg = "";
				String primeLabel = labels.get(beginId);
				if(label.startsWith("b-") && label.length() >= 3)
				{
					if (manner == 1 || manner == 2)label = label.substring(2);
					else if(manner == 3 || manner == 4) label = "xx";
					else if(manner == 5 || manner == 6) {
						primeLabel = primeLabel.substring(2);
						label = label.substring(2);
					}
					else if(manner == 7 || manner == 8){
						primeLabel = primeLabel.substring(2);
						label = "xx";
					}
					else System.out.println("error");
					
					if(manner%2 == 1) oneSeg = String.format("[%d,%d]%s", beginId, idx-1, label);
					else oneSeg = String.format("[%d]%s", beginId, label);
					if(manner >= 5 && primeLabel.equals("neutral"))
					{
						
					}
					else
					{
						results.add(oneSeg);
					}
				}
				beginId = idx;
			}
		}
		
		{
			String oneSeg = "";
			String label = labels.get(beginId);
			String primeLabel = labels.get(beginId);
			if(label.startsWith("b-") && label.length() >= 3)
			{
				if (manner == 1 || manner == 2)label = label.substring(2);
				else if(manner == 3 || manner == 4) label = "xx";
				else if(manner == 5 || manner == 6) {
					primeLabel = primeLabel.substring(2);
					label = label.substring(2);
				}
				else if(manner == 7 || manner == 8){
					primeLabel = primeLabel.substring(2);
					label = "xx";
				}
				else System.out.println("error");
				
				if(manner%2 == 1) oneSeg = String.format("[%d,%d]%s", beginId, labels.size()-1, label);
				else oneSeg = String.format("[%d]%s", beginId, label);
				if(manner >= 5 && primeLabel.equals("neutral"))
				{
					
				}
				else
				{
					results.add(oneSeg);
				}
			}
			
		}
		
		
		return results;
	}
	
	
	public static void LabelEvaluation(List<String> goldLabels, List<String> predLabels, SeqMetric metric, int manner)
	{

		metric.overall_label_count = metric.overall_label_count + goldLabels.size();
		//metric.predicated_label_count = metric.predicated_label_count + otherSegs.size();
		for(int idx = 0; idx < goldLabels.size(); idx++)
		{
			String goldLabel = goldLabels.get(idx);
			String predLabel = predLabels.get(idx);
			if(goldLabel.equals(predLabel))metric.correct_label_count++;
		}
	}
}
