package corpus;

import java.io.*;
import java.util.*;

public class TrainingCorpusGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		Set<String> synlabels = new TreeSet<String>();	
		Set<String> semlabels = new TreeSet<String>();
		Set<String> postags = new TreeSet<String>();
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		String sLine = "";		
		while((sLine = in.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				writer.println();
				continue;
			}
			String[] smallunits = sLine.split("\\s+");
			
			
			
			if(smallunits.length == 5)
			{
				for(int idx = 2; idx <= 3; idx++)
				{
					if(smallunits[idx].endsWith("-"))
					{
						smallunits[idx] = "FAKE";
					}
				}
				if(Integer.parseInt(smallunits[4]) == -1)
				{
					smallunits[2] = "ROOT";
					smallunits[3] = "ROOT";
				}
				else
				{
					synlabels.add(smallunits[2]);
					semlabels.add(smallunits[3]);
				}
				writer.println(smallunits[0] + "\t" + smallunits[1] + "\t" +
						smallunits[4] + "\t" + smallunits[2] + "\t" + smallunits[3]);
				postags.add(smallunits[1]);

			}
			else
			{
				System.out.println("error");				
			}
		}
		
		in.close();
		writer.close();
		
		
		String ourstrname;
		String ourstrenum;
		int num;
		List<String> stringnames = new ArrayList<String>();
		List<String> enumnames = new ArrayList<String>();
		
		writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		
		writer.println("write pos tag set");
		
		stringnames.clear();
		enumnames.clear();
		ourstrname = "";
		ourstrenum = "";
		num = 0;
		List<String> closedTagsStr = new ArrayList<String>();
		String ourclosetype = "";
		for(String curstr : postags)
		{
			
			if(num == 0)ourstrname =  "\"" + curstr + "\",";
			else
			{
				ourstrname =  ourstrname + " \"" + curstr + "\",";
			}
			
			if(num == 0)ourclosetype =  "false,";
			else
			{
				ourclosetype =  ourclosetype + " false,";
			}
			
			if(num == 0)ourstrenum =  "SUTD_TAG_" + curstr.toUpperCase() + ",";
			else
			{
				ourstrenum =  ourstrenum + " SUTD_TAG_" + curstr.toUpperCase() + ",";
			}
			num++;
			if(ourstrenum.length() > 50)
			{
				stringnames.add(ourstrname);
				enumnames.add(ourstrenum);
				closedTagsStr.add(ourclosetype);
				ourstrname = "";
				ourstrenum = "";
				ourclosetype = "";
				num = 0;
			}
		}
		
		if(ourstrenum.length() > 0)
		{
			stringnames.add(ourstrname);
			enumnames.add(ourstrenum);
			closedTagsStr.add(ourclosetype);
			ourstrname = "";
			ourstrenum = "";
			ourclosetype = "";
			num = 0;
		}
		
		for(String curStr : stringnames)
		{
			writer.println(curStr);
		}
		writer.println();
		for(String curStr : enumnames)
		{
			writer.println(curStr);
		}
		
		writer.println();
		for(String curStr : closedTagsStr)
		{
			writer.println(curStr);
		}
		
		writer.println();
		writer.println("write syn label set");
		
		stringnames.clear();
		enumnames.clear();
		ourstrname = "";
		ourstrenum = "";
		num = 0;
		for(String curstr : synlabels)
		{
			
			if(num == 0)ourstrname =  "\"" + curstr + "\",";
			else
			{
				ourstrname =  ourstrname + " \"" + curstr + "\",";
			}
			
			if(num == 0)ourstrenum =  "SUTD_SYN_LAB_" + curstr.toUpperCase() + ",";
			else
			{
				ourstrenum =  ourstrenum + " SUTD_SYN_LAB_" + curstr.toUpperCase() + ",";
			}
			num++;
			if(ourstrenum.length() > 50)
			{
				stringnames.add(ourstrname);
				enumnames.add(ourstrenum);
				ourstrname = "";
				ourstrenum = "";
				num = 0;
			}
		}
		
		if(ourstrenum.length() > 0)
		{
			stringnames.add(ourstrname);
			enumnames.add(ourstrenum);
			ourstrname = "";
			ourstrenum = "";
			num = 0;
		}
		
		for(String curStr : stringnames)
		{
			writer.println(curStr);
		}
		writer.println();
		for(String curStr : enumnames)
		{
			writer.println(curStr);
		}
		
		
		writer.println();
		writer.println("write sem label set");
		
		stringnames.clear();
		enumnames.clear();
		ourstrname = "";
		ourstrenum = "";
		num = 0;
		for(String curstr : semlabels)
		{
			
			if(num == 0)ourstrname =  "\"" + curstr + "\",";
			else
			{
				ourstrname = ourstrname + " \"" + curstr + "\",";
			}
			
			if(num == 0)ourstrenum =  "SUTD_SEM_LAB_" + curstr.toUpperCase() + ",";
			else
			{
				ourstrenum =  ourstrenum +" SUTD_SEM_LAB_" + curstr.toUpperCase() + ",";
			}
			num++;
			if(ourstrenum.length() > 50)
			{
				stringnames.add(ourstrname);
				enumnames.add(ourstrenum);
				ourstrname = "";
				ourstrenum = "";
				num = 0;
			}
		}
		
		if(ourstrenum.length() > 0)
		{
			stringnames.add(ourstrname);
			enumnames.add(ourstrenum);
			ourstrname = "";
			ourstrenum = "";
			num = 0;
		}
		
		for(String curStr : stringnames)
		{
			writer.println(curStr);
		}
		writer.println();
		for(String curStr : enumnames)
		{
			writer.println(curStr);
		}
		
		
		writer.close();
			

	}

}
