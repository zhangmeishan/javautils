package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ExtractWordsFromPOSFile {
	
	
	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		
		String splitchar = "_";
		if(args.length>2 && args[2].equalsIgnoreCase("/"))
		{
			splitchar = "/";
		}
		
		String sLine = "";
		
		while((sLine = reader.readLine()) != null)
		{			
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			
			String[] wordposs = sLine.split("\\s+");
			
			String outline = "";
			
			for(int idx = 0; idx < wordposs.length; idx++)
			{
				int sepIndex = wordposs[idx].indexOf(splitchar);
				if(sepIndex<=0)System.out.println("error occurred");
				String theWord = wordposs[idx].substring(0, sepIndex);
				outline = outline + theWord + " ";
			}
			
			writer.println(outline.trim());
		}
		
		reader.close();
		writer.close();
	}
}
