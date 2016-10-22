package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CorpusGeneration {
	
	
	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8"));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		String sLine = "";
		
		while((sLine = reader.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				writer.println();
			}
			else
			{
				String[] smallunits = sLine.split("\\s+");
				String theWord = smallunits[0];
				String theLabel = smallunits[2].toLowerCase();
				writer.println(theWord + " " + theLabel + "-seg");
			}
			
		}
		
		reader.close();
		writer.close();
	}
}
