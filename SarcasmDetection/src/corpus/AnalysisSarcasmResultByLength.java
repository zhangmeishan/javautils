package corpus;

import java.io.*;
import java.util.*;


public class AnalysisSarcasmResultByLength {

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


		Map<Integer, Integer> results = new HashMap<Integer, Integer>();
		Map<Integer, Integer> sentLength = new HashMap<Integer, Integer>();

		String goldLabel = "";
		String predLabel = "";
		int length = 0;
		double prob = -1.0;
		int instanceId = 0;
		while ((sLine_gold = in_gold.readLine()) != null
				&& (sLine_pred = in_pred.readLine()) != null) {
			sLine_gold = sLine_gold.trim();
			sLine_pred = sLine_pred.trim();
			if (sLine_gold.equals("")) {
				if (!sLine_pred.equals("") ) {
					System.out.println("error1: " + sLine_pred);
				}
				if (prob >= 0 && prob <= 1.0) {
					sentLength.put(instanceId, length);
					if (goldLabel.equalsIgnoreCase("true")
							&& predLabel.equalsIgnoreCase("true")) {
						results.put(instanceId, 1);
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel.equalsIgnoreCase("false")) {
						results.put(instanceId, 1);
					} else if (goldLabel.equalsIgnoreCase("true")
							&& predLabel.equalsIgnoreCase("false")) {
						results.put(instanceId, 0);
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel.equalsIgnoreCase("true")) {
						results.put(instanceId, 0);
					} else {
						System.out.println("error label: " + goldLabel + " "
								+ predLabel);
					}
				}
				
				
				instanceId++;

				goldLabel = "";
				predLabel = ""; 
				prob = -1.0;
				continue;
			} else {
				String[] smallunits_gold = sLine_gold.split("\\s+");
				String[] smallunits_pred1 = sLine_pred.split("\\s+");
				if (smallunits_pred1.length == 2 ) {
					goldLabel = smallunits_gold[0].toLowerCase();
					predLabel = smallunits_pred1[0].toLowerCase();
					prob = Double.parseDouble(smallunits_pred1[1]);
					length = smallunits_gold.length-1;
				} else {
					goldLabel = "";
					predLabel = "";
					prob = -1.0;
				}

			}
		}

		if (prob >= 0 && prob <= 1.0) {
			sentLength.put(instanceId, length);
			if (goldLabel.equalsIgnoreCase("true")
					&& predLabel.equalsIgnoreCase("true")) {
				results.put(instanceId, 1);
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel.equalsIgnoreCase("false")) {
				results.put(instanceId, 1);
			} else if (goldLabel.equalsIgnoreCase("true")
					&& predLabel.equalsIgnoreCase("false")) {
				results.put(instanceId, 0);
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel.equalsIgnoreCase("true")) {
				results.put(instanceId, 0);
			} else {
				System.out.println("error label: " + goldLabel + " "
						+ predLabel);
			}
		}
		
	
		in_gold.close();
		in_pred.close();


		Map<Integer, Integer> allCountByLen = new HashMap<Integer, Integer>();
		Map<Integer, Integer> correctCountByLen = new HashMap<Integer, Integer>();
		int maxlength = 15;
		for(Integer id : results.keySet())
		{
			int curlength = sentLength.get(id);
			int acc = results.get(id);
			int norlength = (curlength-1)/2;
			//if(norlength < 3) norlength = 3;
			if(norlength > maxlength) norlength = maxlength;
			if(!allCountByLen.containsKey(norlength))
			{
				allCountByLen.put(norlength, 0);
				correctCountByLen.put(norlength, 0);
			}
			allCountByLen.put(norlength, allCountByLen.get(norlength)+1);
			
			if(acc == 1)
			{
				correctCountByLen.put(norlength, correctCountByLen.get(norlength)+1);
			}
		}
		
		//PrintWriter out = new PrintWriter(new OutputStreamWriter(
		//		new FileOutputStream(args[3]), "UTF-8"), false);
		
		for(int idl = 0; idl <= maxlength; idl++)
		{
			if(!allCountByLen.containsKey(idl))continue;
			System.out.println(String.format("( %d, %.2f)", idl+1, 100.0 * correctCountByLen.get(idl) / allCountByLen.get(idl) ));
		}

		for(int idl = 0; idl <= maxlength; idl++)
		{
			if(!allCountByLen.containsKey(idl))continue;
			System.out.println(String.format("( %d, %d)", idl+1, allCountByLen.get(idl) ));
		}
		//out.close();
	}

}
