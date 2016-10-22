package corpus;

import java.io.*;
import java.util.*;

public class AnalysisSarcasmResult {

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

		List<String> goldInputs = new ArrayList<String>();
		Map<Integer, Integer> results1 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> results2 = new HashMap<Integer, Integer>();
		String goldLabel = "";
		String goldInput = "";
		String predLabel1 = "";
		String predLabel2 = "";
		double prob1 = -1.0, prob2 = -1.0;
		int instanceId = 0;
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
					goldInputs.add(goldInput);
					if (goldLabel.equalsIgnoreCase("true")
							&& predLabel1.equalsIgnoreCase("true")) {
						results1.put(instanceId, 1);
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel1.equalsIgnoreCase("false")) {
						results1.put(instanceId, 1);
					} else if (goldLabel.equalsIgnoreCase("true")
							&& predLabel1.equalsIgnoreCase("false")) {
						results1.put(instanceId, 0);
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel1.equalsIgnoreCase("true")) {
						results1.put(instanceId, 0);
					} else {
						System.out.println("error label: " + goldLabel + " "
								+ predLabel1);
					}
				}

				if (prob2 >= 0 && prob2 <= 1.0) {
					if (goldLabel.equalsIgnoreCase("true")
							&& predLabel2.equalsIgnoreCase("true")) {
						results2.put(instanceId, 1);
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel2.equalsIgnoreCase("false")) {
						results2.put(instanceId, 1);
					} else if (goldLabel.equalsIgnoreCase("true")
							&& predLabel2.equalsIgnoreCase("false")) {
						results2.put(instanceId, 0);
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel2.equalsIgnoreCase("true")) {
						results2.put(instanceId, 0);
					} else {
						System.out.println("error label: " + goldLabel + " "
								+ predLabel2);
					}
				}

				instanceId++;

				goldLabel = "";
				predLabel1 = "";
				predLabel2 = "";
				goldInput = "";
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
					goldInput = goldInput + sLine_gold;
				} else {
					if (smallunits_pred1.length == 1
							&& smallunits_pred2.length == 1) {
						goldInput = sLine_gold + "\n";
					}
					goldLabel = "";
					predLabel1 = "";
					predLabel2 = "";
					prob1 = -1.0;
					prob2 = -1.0;
				}

			}
		}

		if (prob1 >= 0 && prob1 <= 1.0) {
			goldInputs.add(goldInput);
			if (goldLabel.equalsIgnoreCase("true")
					&& predLabel1.equalsIgnoreCase("true")) {
				results1.put(instanceId, 1);
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel1.equalsIgnoreCase("false")) {
				results1.put(instanceId, 1);
			} else if (goldLabel.equalsIgnoreCase("true")
					&& predLabel1.equalsIgnoreCase("false")) {
				results1.put(instanceId, 0);
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel1.equalsIgnoreCase("true")) {
				results1.put(instanceId, 0);
			} else {
				System.out.println("error label: " + goldLabel + " "
						+ predLabel1);
			}
		}

		if (prob2 >= 0 && prob2 <= 1.0) {
			if (goldLabel.equalsIgnoreCase("true")
					&& predLabel2.equalsIgnoreCase("true")) {
				results2.put(instanceId, 1);
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel2.equalsIgnoreCase("false")) {
				results2.put(instanceId, 1);
			} else if (goldLabel.equalsIgnoreCase("true")
					&& predLabel2.equalsIgnoreCase("false")) {
				results2.put(instanceId, 0);
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel2.equalsIgnoreCase("true")) {
				results2.put(instanceId, 0);
			} else {
				System.out.println("error label: " + goldLabel + " "
						+ predLabel2);
			}
		}

		in_gold.close();
		in_pred1.close();
		in_pred2.close();

		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[3]), "UTF-8"), false);

		out.println("=============1>2=============");
		for (int idx = 0; idx < goldInputs.size(); idx++) {
			if (results1.get(idx) > results2.get(idx)) 
			{
				int lastIndex = goldInputs.get(idx).indexOf("\n");
				//if (goldInputs.get(idx).indexOf("\n") == -1)
				{
					out.println(goldInputs.get(idx).substring(lastIndex+1));
					out.println();
				}
			}
		}
		out.println();
		out.println("=============1<2=============");
		for (int idx = 0; idx < goldInputs.size(); idx++) {
			if (results1.get(idx) < results2.get(idx)) 
			{
				int lastIndex = goldInputs.get(idx).indexOf("\n");
				//if (goldInputs.get(idx).indexOf("\n") == -1) 
				{
					out.println(goldInputs.get(idx).substring(lastIndex+1));
					out.println();
				}
			}
		}

		out.close();
	}

}
