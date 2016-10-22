package corpus;

import java.io.*;
import java.util.*;

public class SpanCorpus {

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
			String theLabel = smallunits[smallunits.length-1].toLowerCase();
			if(theLabel.startsWith("b-")) theLabel = "b-span";
			if(theLabel.startsWith("i-")) theLabel = "i-span";
			String outline = theWord;
			for(int idx = 1; idx < smallunits.length-1; idx++)
			{
				outline = outline + " " + smallunits[idx];
			}
			outline = outline + " " + theLabel;
			out.println(outline);
		}
		in.close();
		out.close();
	}

}
