package corpus;

import java.io.*;
import java.util.*;


public class CorpusFromOpenTargeted {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		
		String sLine = null;
		List<String> words = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();

		

		
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.equals(""))
			{
				if(words.size() > 0)
				{
					int spanStart = 0;
					int spanEnd = 0;
					
					while(spanStart < words.size()) {						
						for(; spanStart < words.size(); spanStart++){
							String curLabel = labels.get(spanStart);
							if(curLabel.startsWith("b-")){
								break;
							}
						}
						
						if(spanStart == words.size()){
							break;
						}
						
						for(spanEnd = spanStart + 1; spanEnd < words.size(); spanEnd++) {
							String curLabel = labels.get(spanEnd);
							if(curLabel.equals("o") || curLabel.startsWith("b-")){
								break;
							}
						}
						
						for(int idx = 0; idx < words.size(); idx++) {
							String curWord = words.get(idx);
							String curLabel = "o";
							if(idx >= spanStart && idx < spanEnd)
							{
								curLabel = labels.get(idx);
							}
							out.println(curWord + " " + curLabel);
						}
						
						out.println();	
						spanStart = spanEnd;
					}
					
									

				}
				
				words = new ArrayList<String>();
				labels = new ArrayList<String>();
				
			}
			else
			{
				String[] smallunits = sLine.split("\\s+");
				words.add(smallunits[0]);
				labels.add(smallunits[smallunits.length-1]);				
			}
			
		}
			
		in.close();
		out.close();
		
		
		
	}
	
	

}
