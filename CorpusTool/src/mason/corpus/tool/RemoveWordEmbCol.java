package mason.corpus.tool;

import java.io.*;
import java.util.*;


public class RemoveWordEmbCol {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		
		PrintWriter output = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		String sLine = null;
		
		int column = Integer.parseInt(args[2]);
		
		
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.length() < 2) continue;
			String[] smallunits = sLine.split("\\s+");
			
			String outline = "";
			for(int i = 0; i < smallunits.length; i++){
				if(i == column) continue;
				outline = outline + " " + smallunits[i];
			}
			
			outline = outline.trim();
			output.println(outline);
		}
		
		in.close();
		output.close();

	}

}
