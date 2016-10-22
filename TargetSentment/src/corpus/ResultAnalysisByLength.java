package corpus;

import java.io.*;
import java.util.*;

public class ResultAnalysisByLength {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String sLine_gold = null;
		String sLine_pred = null;
		BufferedReader in_gold = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));

		BufferedReader in_pred = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF8"));


		Map<String, SeqMetric> micro_fmeasure = new HashMap<String, SeqMetric>();
		for(int idx = 0; idx < 8; idx++)
		{
			micro_fmeasure.put(String.format("%d", idx), new SeqMetric());
		}


		List<String> goldLabels = new ArrayList<String>();
		List<String> predLabels = new ArrayList<String>();
		while ((sLine_gold = in_gold.readLine()) != null
				&& (sLine_pred = in_pred.readLine()) != null) {
			sLine_gold = sLine_gold.trim();
			sLine_pred = sLine_pred.trim();
			if (sLine_gold.equals("")) {
				if (!sLine_pred.equals("")) {
					System.out.println("error: " + sLine_pred);
				}
				if (goldLabels.size() > 0) {
					SegEvaluation(goldLabels, predLabels, micro_fmeasure);
				}

				goldLabels = new ArrayList<String>();
				predLabels = new ArrayList<String>();
				continue;
			} else {
				String[] smallunits_gold = sLine_gold.split("\\s+");
				goldLabels.add(smallunits_gold[smallunits_gold.length - 1]);

				String[] smallunits_pred = sLine_pred.split("\\s+");
				predLabels.add(smallunits_pred[smallunits_pred.length - 1]);
			}
		}

		if (goldLabels.size() > 0) {
			SegEvaluation(goldLabels, predLabels, micro_fmeasure);
		}


		for(int idx = 0; idx < 8; idx++) {
			String theType = String.format("%d", idx);		
			//micro_fmeasure.get(theType).print();
			System.out.println(String.format("( %d, %.2f)", idx+1, 100 * micro_fmeasure.get(theType).getAccuracy()));

		}
		in_gold.close();
		in_pred.close();
	}

	public static void SegEvaluation(List<String> goldLabels,
			List<String> predLabels, SeqMetric metric) {

		Set<String> goldSegs = getSegs(goldLabels);
		Set<String> otherSegs = getSegs(predLabels);
		metric.overall_label_count = metric.overall_label_count
				+ goldSegs.size();
		metric.predicated_label_count = metric.predicated_label_count
				+ otherSegs.size();

		for (String oneSeg : goldSegs) {
			if (otherSegs.contains(oneSeg))
				metric.correct_label_count++;
		}
	}
	
	public static void SegEvaluation(List<String> goldLabels,
			List<String> predLabels, Map<String, SeqMetric> micro_fmeasure) {

		Set<String> goldSegs = getSegs(goldLabels);
		Set<String> otherSegs = getSegs(predLabels);
		
		int sentenceLength = goldLabels.size();
		int normlength = (sentenceLength-1)/5;
		if(normlength > 7) normlength = 7;
		String theType = String.format("%d", normlength);
		
		micro_fmeasure.get(theType).overall_label_count = micro_fmeasure.get(theType).overall_label_count
				+ goldSegs.size();
		micro_fmeasure.get(theType).predicated_label_count = micro_fmeasure.get(theType).predicated_label_count
				+ otherSegs.size();

		for (String oneSeg : goldSegs) {
			if (otherSegs.contains(oneSeg))
				micro_fmeasure.get(theType).correct_label_count++;
		}
			
	}

	public static Set<String> getSegs(List<String> labels) {
		Set<String> results = new HashSet<String>();
		int i = 0;
		while (i < labels.size()) {
			String curLabel = labels.get(i).toLowerCase();
			if (curLabel.startsWith("b-")) {
				int j = i + 1;
				while (j < labels.size()) {
					String nextLabel = labels.get(j).toLowerCase();
					if (!nextLabel.startsWith("i-")) {
						break;
					}
					j++;
				}

				String label = curLabel.substring(2);
				String oneSeg = String.format("[%d,%d]%s", i, j - 1, label);
				results.add(oneSeg);
				i = j;
			} else {
				i++;
			}

		}

		return results;
	}
}
