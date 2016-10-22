package corpus;


import java.io.*;
import java.util.*;

public class BMESCorpusGeneration {
	
	
	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8"));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		String sLine = "";
		List<String> words = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();
		while((sLine = reader.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				if(words.size() > 0)
				{
					for(int idx = 0; idx < words.size(); idx++){
						String curWord = words.get(idx);
						String curLabel = "";
						if(labels.get(idx).equals("b")){
							curLabel = "b-seg";
							if(idx == words.size()-1 ||
							  labels.get(idx+1).equals("b"))
							{
								curLabel = "s-seg";
							}
						}
						else
						{
							curLabel = "m-seg";
							if(idx == words.size()-1 ||
							  labels.get(idx+1).equals("b"))
							{
								curLabel = "e-seg";
							}
						}
						writer.println(curWord + " " + curLabel);
					}
					writer.println();
				}
				words = new ArrayList<String>();
				labels = new ArrayList<String>();
			}
			else
			{
				String[] smallunits = sLine.split("\\s+");
				String theWord = smallunits[0];
				String theLabel = smallunits[2].toLowerCase();
				words.add(theWord); labels.add(theLabel);
			}
			
		}
		
		if(words.size() > 0)
		{
			for(int idx = 0; idx < words.size(); idx++){
				String curWord = words.get(idx);
				String curLabel = "";
				if(labels.get(idx).equals("b")){
					curLabel = "b-seg";
					if(idx == words.size()-1 ||
					  labels.get(idx+1).equals("b"))
					{
						curLabel = "s-seg";
					}
				}
				else
				{
					curLabel = "m-seg";
					if(idx == words.size()-1 ||
					  labels.get(idx+1).equals("b"))
					{
						curLabel = "e-seg";
					}
				}
				writer.println(curWord + " " + curLabel);
			}
			writer.println();
		}
		
		reader.close();
		writer.close();
	}
}
