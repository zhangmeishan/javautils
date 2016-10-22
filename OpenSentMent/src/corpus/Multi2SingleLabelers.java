package corpus;

import java.io.*;
import java.util.*;

public class Multi2SingleLabelers {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out1 = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		PrintWriter out2 = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"), false);
		PrintWriter out3 = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[3]), "UTF-8"), false);
		
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))
			{
				out1.println();
				out2.println();
				out3.println();
				continue;
			}
			String[] smallunits = sLine.trim().split("\\s+");
			assert(smallunits.length >= 3);
			String theWord = smallunits[0];
			String theFirstLabel = smallunits[smallunits.length-2];
			String theSecondLabel = smallunits[smallunits.length-1];
			String outline1 = theWord;
			String outline2 = theWord;
			String outline3 = theWord;
			for(int idx = 1; idx < smallunits.length-2; idx++)
			{
				outline1 = outline1 + " " + smallunits[idx];
				outline2 = outline2 + " " + smallunits[idx];
				outline3 = outline3 + " " + smallunits[idx];
			}
			outline1 = outline1 + " " + theFirstLabel;
			outline3 = outline3 + " " + theSecondLabel;
			out1.println(outline1);
			out3.println(outline3);			
			List<String> newFeats = new ArrayList<String>();
			newFeats.add("[S]FNEW1=" + theFirstLabel);
			newFeats.add("[S]FNEW2=" + theFirstLabel + "#" + theWord);
			if(theFirstLabel.substring(1).startsWith("-") && theFirstLabel.length() > 2)
			{
				newFeats.add("[S]FNEW3=" + theFirstLabel.substring(0, 1));
				newFeats.add("[S]FNEW4=" + theFirstLabel.substring(0, 1) + "#" + theWord);
				newFeats.add("[S]FNEW5=" + theFirstLabel.substring(2));
				newFeats.add("[S]FNEW6=" + theFirstLabel.substring(2) + "#" + theWord);
			}
			
			for(String curFeat : newFeats)
			{
				outline2 = outline2 + " " + curFeat;
			}
			outline2 = outline2 + " " + theSecondLabel;
			out2.println(outline2);
		}
		in.close();
		out1.close();
		out2.close();
		out3.close();
	}

}
