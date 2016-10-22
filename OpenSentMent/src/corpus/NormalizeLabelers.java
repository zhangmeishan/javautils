package corpus;

import java.io.*;
import java.util.*;

public class NormalizeLabelers {

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
		int count = 0;		
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))
			{
				out.println();
				continue;
			}
			String[] smallunits = sLine.trim().split("\\s+");
			assert(smallunits.length >= 3);
			String theWord = smallunits[0];
			String theFirstLabel = smallunits[smallunits.length-2].toLowerCase();
			String theSecondLabel = smallunits[smallunits.length-1].toLowerCase();
			while(theSecondLabel.startsWith("b-b-") || theSecondLabel.startsWith("i-i-"))
			{
				theSecondLabel = theSecondLabel.substring(2);
			}
			if(theSecondLabel.startsWith("b-i-") || theSecondLabel.startsWith("i-b-"))
			{
				System.out.println(sLine);
			}
			String outline = theWord;
			for(int idx = 1; idx < smallunits.length-2; idx++)
			{
				outline = outline + " " + smallunits[idx];
			}
			if(theFirstLabel.endsWith("person") || theFirstLabel.endsWith("organization"))
			{
				if(theSecondLabel.startsWith("b-") || theSecondLabel.startsWith("i-"))
				{
					
				}
				else
				{
					System.out.println(sLine);
				}
				if(theFirstLabel.startsWith("b-"))count++;
			}
			else
			{
				theFirstLabel = "o"; theSecondLabel = "o";
			}
			outline = outline + " " + theFirstLabel;
			outline = outline + " " + theSecondLabel;
			out.println(outline);

		}
		in.close();
		out.close();
		System.out.println(count);
	}

}
