package corpus;

import java.io.*;
import java.util.*;


public class EvaluateSarcasm {

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

		PrintWriter out = null;
		if(args.length > 2) {
			out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"), false);
		}
		String goldLabel = "";
		String predLabel = "";
		double prob = -1.0;
		int totalInstances = 0, correctInstances = 0;
		while ((sLine_gold = in_gold.readLine()) != null
				&& (sLine_pred = in_pred.readLine()) != null) {
			sLine_gold = sLine_gold.trim();
			sLine_pred = sLine_pred.trim();
			if (sLine_gold.equals("")) {
				if (!sLine_pred.equals("")) {
					System.out.println("error: " + sLine_pred);
				}
				if (prob >= 0 && prob <= 1.0) {
					totalInstances++;
					if (goldLabel.equalsIgnoreCase("true")
							&& predLabel.equalsIgnoreCase("true")) {
						correctInstances++;
						if(out != null)out.println(String.format("%f 1", prob));
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel.equalsIgnoreCase("false")) {
						correctInstances++;
						if(out != null)out.println(String.format("%f 0", prob));
					} else if (goldLabel.equalsIgnoreCase("true")
							&& predLabel.equalsIgnoreCase("false")) {
						if(out != null)out.println(String.format("%f 1", 1 - prob));
					} else if (goldLabel.equalsIgnoreCase("false")
							&& predLabel.equalsIgnoreCase("true")) {
						if(out != null)out.println(String.format("%f 0", 1 - prob));
					} else {
						System.out.println("error label: " + goldLabel + " "
								+ predLabel);
					}

				}

				goldLabel = "";
				predLabel = "";
				prob = -1.0;
				continue;
			} else {
				String[] smallunits_gold = sLine_gold.split("\\s+");
				String[] smallunits_pred = sLine_pred.split("\\s+");
				if (smallunits_pred.length == 2) {
					goldLabel = smallunits_gold[0].toLowerCase();
					predLabel = smallunits_pred[0].toLowerCase();
					prob = Double.parseDouble(smallunits_pred[1]);
				} else {
					goldLabel = "";
					predLabel = "";
					prob = -1.0;
				}

			}
		}

		if (prob >= 0 && prob <= 1.0) {
			totalInstances++;
			if (goldLabel.equalsIgnoreCase("true")
					&& predLabel.equalsIgnoreCase("true")) {
				correctInstances++;
				if(out != null)out.println(String.format("%f 1", prob));
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel.equalsIgnoreCase("false")) {
				correctInstances++;
				if(out != null)out.println(String.format("%f 0", prob));
			} else if (goldLabel.equalsIgnoreCase("true")
					&& predLabel.equalsIgnoreCase("false")) {
				if(out != null)out.println(String.format("%f 1", 1 - prob));
			} else if (goldLabel.equalsIgnoreCase("false")
					&& predLabel.equalsIgnoreCase("true")) {
				if(out != null)out.println(String.format("%f 0", 1 - prob));
			} else {
				System.out.println("error label: " + goldLabel + " "
						+ predLabel);
			}

		}

		System.err.println(String.format("Accuracy:\tP=%d/%d=%.5f",
				correctInstances, totalInstances, correctInstances * 1.0
						/ totalInstances));

		in_gold.close();
		in_pred.close();
		if(out != null)out.close();
	}

}
