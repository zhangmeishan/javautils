package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.*;
import java.io.*;

public class BMES2Segmentation {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8"));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		String sLine = "";
		List<String> characters = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();
		List<String> words = new ArrayList<String>();
		
		while((sLine = reader.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				if(characters.size() > 0){
					getSegmentationsFromLabels(characters, labels, words);
					String printline = ZHUtil.toDBC(words.get(0));
					for(int idx = 1; idx < words.size(); idx++){
						printline = printline + " " + ZHUtil.toDBC(words.get(idx));
					}
					writer.println(printline);
				}
				
				characters = new ArrayList<String>();
				labels = new ArrayList<String>();
				words = new ArrayList<String>();
			}
			else{
				String[] smallunits = sLine.split("\\s+");
				characters.add(smallunits[0]);
				labels.add(smallunits[smallunits.length-1]);
			}
		
		}
		
		
		reader.close();
		writer.close();
	}
	
	
	public static void getSegmentationsFromLabels(List<String> characters, List<String> labels, List<String> words){
		words.clear();
		
		String curWord = characters.get(0);
		for(int idx = 1; idx <= labels.size(); idx++){
			String curLabel = idx < labels.size() ? labels.get(idx).toLowerCase() : "s-null";
			
			if(curLabel.startsWith("b-") || curLabel.startsWith("s-")){
				words.add(curWord);
				curWord = idx < characters.size() ? characters.get(idx).toLowerCase() : "-null-";
			}
			else{
				curWord = curWord + characters.get(idx);
			}
		}
	}

}
