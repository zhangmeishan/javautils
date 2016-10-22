package corpus;

import java.io.*;
import java.util.*;

public class GizaSentencePariGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = "";
		Map<Integer, String> target_word_map = new HashMap<Integer, String>();
		BufferedReader in_word_target = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		while((sLine = in_word_target.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.trim().isEmpty())continue;
			String[] smallunits = sLine.split("\\s+");
			if(smallunits.length != 3)continue;
			target_word_map.put(Integer.parseInt(smallunits[0]), smallunits[1]);
		}
		
		in_word_target.close();
		
		String[] targetwords = new String[target_word_map.size()+1];
		for(Integer i : target_word_map.keySet())
		{
			targetwords[i] = target_word_map.get(i);
		}
		
		BufferedReader in_word_source = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[1]), "UTF-8"));
		Map<Integer, String> source_word_map = new HashMap<Integer, String>();
		while((sLine = in_word_source.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.trim().isEmpty())continue;
			String[] smallunits = sLine.split("\\s+");
			if(smallunits.length != 3)continue;
			source_word_map.put(Integer.parseInt(smallunits[0]), smallunits[1]);
		}
		in_word_source.close();
		
		String[] sourcewords = new String[source_word_map.size()+1];
		for(Integer i : source_word_map.keySet())
		{
			sourcewords[i] = source_word_map.get(i);
		}
		
		BufferedReader in_sentance_pair = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[2]), "UTF-8"));
		
		PrintWriter writer_src = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[3]), "UTF-8"));	
		
		PrintWriter writer_tgt = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[4]), "UTF-8"));	
		
		
		while((sLine = in_sentance_pair.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			String[] smallunits = sLine.split("\\s+");
			if(smallunits.length != 1) continue;
			
			sLine = in_sentance_pair.readLine();
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			smallunits = sLine.split("\\s+");
	
			//first source then tarrget
			String outline = "";
			int wordid = -1;
			for(int idx = 0; idx < smallunits.length; idx++)
			{
				wordid = Integer.parseInt(smallunits[idx]);
				outline = outline + targetwords[wordid] + " ";
			}
			
			writer_tgt.println(outline.trim());
			
			sLine = in_sentance_pair.readLine();
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			smallunits = sLine.split("\\s+");
			
			outline = "";
			wordid = -1;
			for(int idx = 0; idx < smallunits.length; idx++)
			{
				wordid = Integer.parseInt(smallunits[idx]);
				outline = outline + sourcewords[wordid] + " ";
			}
			
			writer_src.println(outline.trim());
			
		}
		
		in_sentance_pair.close();
		writer_src.close();
		writer_tgt.close();

	}

}
