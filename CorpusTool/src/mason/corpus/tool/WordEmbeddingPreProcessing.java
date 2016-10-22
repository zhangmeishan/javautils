package mason.corpus.tool;

import java.util.*;
import java.io.*;

public class WordEmbeddingPreProcessing {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Map<String, Integer> allWordFreqs = new HashMap<String, Integer>();
		String sLine = "";
		int wordnumber = 0;
		String mathcer = "[0-9]";
		for(int idx = 0; idx < args.length-1; idx++)
		{
			int curwordnumber = 0;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[idx]), "UTF-8"));
			
			while ((sLine = reader.readLine()) != null) {
				String sTempLine = sLine.trim();
				String[] smallitunits = sTempLine.split("\\s+");
				
				for(String curWord: smallitunits)
				{
					String newWord = curWord.toLowerCase().replaceAll(mathcer, "0");
					if(!allWordFreqs.containsKey(newWord))
					{
						allWordFreqs.put(newWord, 0);
					}
					
					allWordFreqs.put(newWord, allWordFreqs.get(newWord)+1);
					curwordnumber++;
				}	
			}
			System.out.println(String.format("Filename: %s \t word number: %d", args[idx], curwordnumber));
			wordnumber = wordnumber + curwordnumber;
			reader.close();
			
		}
		
		System.out.println(String.format("Total word number: %d \t Total word number remove duplicated: %d", wordnumber, allWordFreqs.keySet().size()));
		
		
		int totalcount = 0;
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[args.length-1]), "UTF-8"));
		for(int idx = 0; idx < args.length-1; idx++)
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[idx]), "UTF-8"));
			
			while ((sLine = reader.readLine()) != null) {
				String sTempLine = sLine.trim();
				String[] smallitunits = sTempLine.split("\\s+");
				if(smallitunits.length < 3 || sLine.replaceAll("\\s+", "").length() < 20) continue;
				String outline = "PADDING";
				for(String curWord: smallitunits)
				{
					String newWord = curWord.toLowerCase().replaceAll(mathcer, "0");
					assert(allWordFreqs.containsKey(newWord));
					if(allWordFreqs.get(newWord) <= 10)
					{
						newWord = "UNKNOWN";
					}
					outline = outline + " " + newWord;
				}
				totalcount = totalcount + smallitunits.length;
				outline = outline + " " + "PADDING";
				writer.println(outline);
			}
			reader.close();
			System.out.println(String.format("Filename: %s finished", args[idx]));
		}
		
		System.out.println(String.format("Total word number: %d ", totalcount));
		writer.close();

	}

}
