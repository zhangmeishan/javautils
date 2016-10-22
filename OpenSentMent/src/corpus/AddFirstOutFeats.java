package corpus;

import java.io.*;
import java.util.*;

public class AddFirstOutFeats {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine1 = null;
		String sLine2 = null;
		BufferedReader in1 = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		BufferedReader in2 = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"), false);
				
		while ((sLine1 = in1.readLine()) != null && (sLine2 = in2.readLine()) != null) {
			if(sLine1.trim().equals(""))
			{
				out.println();
				continue;
			}
			String[] smallunits = sLine1.trim().split("\\s+");
			String[] smallunitsLabel = sLine2.trim().split("\\s+");
			assert(smallunits.length >= 3);
			//assert(smallunitsLabel.length == 1);
			String theWord = smallunits[0];
			String theFirstLabel = smallunitsLabel[smallunitsLabel.length-1];
			String theSecondLabel = smallunits[smallunits.length-1];
			String outline = theWord;
			for(int idx = 1; idx < smallunits.length-2; idx++)
			{
				outline = outline + " " + smallunits[idx];
			}

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
				outline = outline + " " + curFeat;
			}
			outline = outline + " " + theSecondLabel;
			out.println(outline);
		}
		in1.close();
		in2.close();
		out.close();
	}

}
