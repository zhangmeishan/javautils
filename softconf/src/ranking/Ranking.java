package ranking;

import java.io.*;
import java.util.*;
import java.io.InputStreamReader;

public class Ranking {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		int submission = -1;
		List<Double> scores = new ArrayList<Double>();
		double avgScore = -1;
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))continue;
			sLine = sLine.trim();
			
			if(sLine.startsWith("Submission"))
			{				
				String[] smallunits = sLine.split("\\s+");
				String submissionId = smallunits[1].substring(0, smallunits[1].length()-1);
				submission = Integer.parseInt(submissionId);
			}
			else if(sLine.startsWith("AVG:")){
				String[] smallunits = sLine.split("\\s+");
				if(submission == -1){
					System.out.println("error here");
				}
				avgScore = Double.parseDouble(smallunits[1]);
				if(!smallunits[8].equals("Scores:")){
					System.out.println("error here");
				}
				for(int idx = 9; idx < smallunits.length; idx++){
					scores.add(Double.parseDouble(smallunits[idx]));
				}
				
				//if(submission != -1)
				{
					String outline = String.format("%d\t%.2f\t%.2f", submission, avgScore, scores.get(0));
					double avg = scores.get(0);
					for(int idx = 1; idx < scores.size(); idx++){
						outline = outline + " " + String.format("%.2f", scores.get(idx));
						avg = avg + scores.get(idx);
					}
					avg = avg *1.0 / scores.size();
					if(Math.abs(avg - avgScore) > 0.02){
						System.out.println("error here");
					}
					
					out.println(outline);	
					
					submission = -1;
					scores = new ArrayList<Double>();
					avgScore = -1;
				}
			}
		}
		
		in.close();
		out.close();
		
	}

}
