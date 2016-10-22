package corpus;


import java.io.*;
import java.util.*;

public class GenerateZparPennSrc {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		

		
		Map<String, Boolean> labelattrs = new HashMap<String, Boolean>();
		List<String> labels = new ArrayList<String>();	
		
		String sLine = "";
		while((sLine = reader.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			String[] units = sLine.split("\\s+");
			
			if(units.length == 3)
			{
				String theLabel = units[1].substring(0, units[1].length()-2) ;
				if(!labelattrs.containsKey(theLabel))
				{
					labelattrs.put(theLabel, false);
					labels.add(theLabel);
				}
				if(units[1].endsWith("#c"))
				{
					labelattrs.put(theLabel, true);
				}

			}
		}		
		
		reader.close();
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		List<String> outlabelpenns = new ArrayList<String>();
		List<String> outlabelcoors = new ArrayList<String>();
		String ourstr = "";
		String ourstrpenn = "";
		String ourstrcoor = "";
		for(int idx = 0; idx < labels.size(); idx++)
		{
			ourstr = ourstr + "\"" + labels.get(idx) + "\", ";
			ourstrpenn = ourstrpenn + "PENN_" + labels.get(idx).toUpperCase().replace("-", "_") + ", ";
			
			if(labelattrs.get(labels.get(idx)))
			{
				ourstrcoor = ourstrcoor + "true, ";
			}
			else
			{
				ourstrcoor = ourstrcoor + "false, ";
			}
			
			if(ourstr.length() > 41)
			{
				writer.println(ourstr.trim());
				outlabelpenns.add(ourstrpenn.trim());
				outlabelcoors.add(ourstrcoor.trim());				
				ourstr = "";
				ourstrpenn = "";
				ourstrcoor = "";
			}
		}
		
		if(ourstr.length() > 0)
		{
			writer.println(ourstr.trim());
			outlabelpenns.add(ourstrpenn.trim());
			outlabelcoors.add(ourstrcoor.trim());				
			ourstr = "";
			ourstrpenn = "";
			ourstrcoor = "";
		}
		
		writer.println();
		writer.println();
		
		for(int idx = 0; idx < outlabelpenns.size(); idx++)
		{
			writer.println(outlabelpenns.get(idx));
		}
		
		
		writer.println();
		writer.println();
		
		for(int idx = 0; idx < outlabelcoors.size(); idx++)
		{
			writer.println(outlabelcoors.get(idx));
		}
		
		writer.close();

	}

}
