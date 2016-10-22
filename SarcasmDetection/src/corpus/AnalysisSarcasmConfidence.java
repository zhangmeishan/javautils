package corpus;

import java.io.*;
import java.util.*;

public class AnalysisSarcasmConfidence {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String sLine_gold = null;
		String sLine_pred1 = null;
		String sLine_pred2 = null;
		BufferedReader in_gold = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		BufferedReader in_pred1 = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF8"));
		BufferedReader in_pred2 = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[2]), "UTF8"));

		List<String> goldLabels = new ArrayList<String>();
		List<Double> predict1s = new ArrayList<Double>();
		List<Double> predict2s = new ArrayList<Double>();
		String goldLabel = "";
		String predLabel1 = "";
		String predLabel2 = "";
		double prob1 = -1.0, prob2 = -1.0;

		while ((sLine_gold = in_gold.readLine()) != null
				&& (sLine_pred1 = in_pred1.readLine()) != null
				&& (sLine_pred2 = in_pred2.readLine()) != null) {
			sLine_gold = sLine_gold.trim();
			sLine_pred1 = sLine_pred1.trim();
			sLine_pred2 = sLine_pred2.trim();
			if (sLine_gold.equals("")) {
				if (!sLine_pred1.equals("") || !sLine_pred2.equals("")) {
					System.out.println("error1: " + sLine_pred1);
					System.out.println("error2: " + sLine_pred2);
				}
				if (prob1 >= 0 && prob1 <= 1.0) {
					if (predLabel1.equalsIgnoreCase("true")) {
						predict1s.add(prob1);
					} else if (predLabel1.equalsIgnoreCase("false")) {
						predict1s.add(1 - prob1);
					} else {
						System.out.println("error label: " + goldLabel + " "
								+ predLabel1);
					}
				}
				goldLabels.add(goldLabel.toLowerCase());

				if (prob2 >= 0 && prob2 <= 1.0) {
					if (predLabel2.equalsIgnoreCase("true")) {
						predict2s.add(prob2);
					} else if (predLabel2.equalsIgnoreCase("false")) {
						predict2s.add(1 - prob2);
					} else {
						System.out.println("error label: " + goldLabel + " "
								+ predLabel2);
					}
				}

				goldLabel = "";
				predLabel1 = "";
				predLabel2 = "";
				prob1 = -1.0;
				prob2 = -1.0;
				continue;
			} else {
				String[] smallunits_gold = sLine_gold.split("\\s+");
				String[] smallunits_pred1 = sLine_pred1.split("\\s+");
				String[] smallunits_pred2 = sLine_pred2.split("\\s+");
				if (smallunits_pred1.length == 2
						&& smallunits_pred2.length == 2) {
					goldLabel = smallunits_gold[0].toLowerCase();
					predLabel1 = smallunits_pred1[0].toLowerCase();
					prob1 = Double.parseDouble(smallunits_pred1[1]);
					predLabel2 = smallunits_pred2[0].toLowerCase();
					prob2 = Double.parseDouble(smallunits_pred2[1]);
				} else {
					goldLabel = "";
					predLabel1 = "";
					predLabel2 = "";
					prob1 = -1.0;
					prob2 = -1.0;
				}

			}
		}

		if (prob1 >= 0 && prob1 <= 1.0) {
			if (predLabel1.equalsIgnoreCase("true")) {
				predict1s.add(prob1);
			} else if (predLabel1.equalsIgnoreCase("false")) {
				predict1s.add(1 - prob1);
			} else {
				System.out.println("error label: " + goldLabel + " "
						+ predLabel1);
			}
		}
		goldLabels.add(goldLabel.toLowerCase());

		if (prob2 >= 0 && prob2 <= 1.0) {
			if (predLabel2.equalsIgnoreCase("true")) {
				predict2s.add(prob2);
			} else if (predLabel2.equalsIgnoreCase("false")) {
				predict2s.add(1 - prob2);
			} else {
				System.out.println("error label: " + goldLabel + " "
						+ predLabel2);
			}
		}

		in_gold.close();
		in_pred1.close();
		in_pred2.close();
		
		List<Integer> wholes = new ArrayList<Integer>();
		for (int idx = 0; idx < goldLabels.size(); idx++) {
			wholes.add(idx);
		}
		
		Collections.shuffle(wholes, new Random(0));
		

		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[3]), "UTF-8"), false);

		out.println("neural\tdiscrete\tlabel");
		int samplenum = 1000;
		for(int idz = 0; idz < samplenum; idz++){
			int idx = wholes.get(idz);
			goldLabel = goldLabels.get(idx);
			if (goldLabel.equalsIgnoreCase("true")) {
				out.println(String.format("%.4f\t%.4f\ta", predict2s.get(idx),
						predict1s.get(idx)));
			} else if (goldLabel.equalsIgnoreCase("false")){
				out.println(String.format("%.4f\t%.4f\tb", predict2s.get(idx),
						predict1s.get(idx)));
			}
		}

		out.close();
	}

}
