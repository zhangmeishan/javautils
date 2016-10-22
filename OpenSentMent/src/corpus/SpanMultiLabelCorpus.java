package corpus;

import java.io.*;
import java.util.*;

public class SpanMultiLabelCorpus {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));

		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
				
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))
			{
				out.println();
				continue;
			}
			String[] smallunits = sLine.trim().split("\\s+");
			assert(smallunits.length >= 3);
			//assert(smallunitsLabel.length == 1);
			String theWord = smallunits[0];
			String theFirstLabel = smallunits[smallunits.length-2].toLowerCase();
			String theSecondLabel = smallunits[smallunits.length-1].toLowerCase();
			if(theFirstLabel.startsWith("b-")) theFirstLabel = "b-span";
			if(theFirstLabel.startsWith("i-")) theFirstLabel = "i-span";
			String outline = theWord;
			for(int idx = 1; idx < smallunits.length-2; idx++)
			{
				outline = outline + " " + smallunits[idx];
			}
			outline = outline + " " + theFirstLabel;
			outline = outline + " " + theSecondLabel;
			out.println(outline);
		}
		in.close();
		out.close();
	}

}
