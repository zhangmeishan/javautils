package corpus;

import java.io.*;
import java.util.*;


public class LogAnalysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Map<String, List<Double>> modelScores = new HashMap<String, List<Double>>();
		Map<String, Double> modelMaxScore = new HashMap<String,Double>();
		List<String> models = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		int resultOption = 1;
		if(args.length > 2) resultOption = 2;
		String sLine = null;
		List<Double> scores = new ArrayList<Double>();
		String modelMark = "";
		double maxScore = -1.0;
		int neglectedCount = 0;
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			String[] smallunits = sLine.split("\\s+");
			if(sLine.indexOf("Labeler") != -1 && smallunits.length < 4)
			{
				if(scores.size() > 0)
				{
					modelScores.put(modelMark, scores);
					modelMaxScore.put(modelMark, maxScore);
					models.add(modelMark);
				}
				modelMark = sLine;
				scores = new ArrayList<Double>();
				maxScore = -1.0;
				neglectedCount = 0;
			}
			else if(sLine.startsWith("regParameter = "))
			{
				modelMark = modelMark +  " (" + sLine + ")";
				neglectedCount = 0;
			}
			else if(sLine.startsWith("Recall:"))
			{
				if(neglectedCount > 10)
				{
					if(resultOption == 1)
					{
						double curScore = Double.parseDouble(smallunits[smallunits.length-1]);
						scores.add(curScore);
						if(maxScore < curScore) maxScore = curScore;
					}
					else
					{
						sLine = in.readLine();
						sLine = sLine.trim();
						if(sLine.startsWith("Recall:"))
						{
							smallunits = sLine.split("\\s+");
							double curScore = Double.parseDouble(smallunits[smallunits.length-1]);
							scores.add(curScore);
							if(maxScore < curScore) maxScore = curScore;
						}
						else
						{
							System.out.println("error!");
						}
					}
				}
				neglectedCount = 0;
			}
			neglectedCount++;
		}
		
		in.close();
		
		if(scores.size() > 0)
		{
			modelScores.put(modelMark, scores);
			modelMaxScore.put(modelMark, maxScore);
			models.add(modelMark);
		}
		modelMark = sLine;
		scores = new ArrayList<Double>();
		maxScore = -1.0;
		neglectedCount = 0;
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		for(String theKey : models)
		{
			out.println(String.format("%s\t%f", theKey, modelMaxScore.get(theKey)));
		}
		
		out.println();
		for(String theKey : models)
		{
			String oneLine = theKey;
			out.println(oneLine);
			oneLine = String.format("%f", modelScores.get(theKey).get(0));
			for (int idx = 1; idx < modelScores.get(theKey).size(); idx++)
			{
				String sep = " ";
				if(idx%10 == 0) sep = "\r\n";
				oneLine = oneLine + sep + String.format("%f", modelScores.get(theKey).get(idx));
			}
			
			out.println(oneLine);
			out.println();
		}

		out.close();

	}

}
