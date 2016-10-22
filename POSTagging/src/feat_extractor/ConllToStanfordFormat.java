package feat_extractor;

import java.io.*;
import java.util.*;

public class ConllToStanfordFormat {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		String sLine = "";
		List<String> words = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				if(words.size()>0)
				{
					String outline = words.get(0) + "_" + labels.get(0);
					for(int idx = 1; idx < words.size(); idx++)
					{
						outline = outline + " " +  words.get(idx) + "_" + labels.get(idx);
					}
					out.println(outline);
				}
					
				words = new ArrayList<String>();
				labels = new ArrayList<String>();
				continue;
			}
			
			String[] units = sLine.split("\\s+");			
			String theWord = units[0].replace("_", "#");
			String thePOS = units[units.length-1];
			words.add(theWord);
			labels.add(thePOS);
		}
		
		in.close();
		out.close();
	}

}
