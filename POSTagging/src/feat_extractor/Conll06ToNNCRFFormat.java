package feat_extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Conll06ToNNCRFFormat {

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
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				out.println();
				continue;
			}
			
			String[] units = sLine.split("\\s+");
			
			String theWord = units[1];
			String thePOS = units[3];
			String outline = theWord;
			
			for(int idx = 0; idx < theWord.length(); idx++)
			{
				outline = outline + " " + theWord.substring(idx, idx+1);
			}
			
			outline = outline + " " + thePOS;
			out.println(outline);
		}
		
		in.close();
		out.close();
	}

}
