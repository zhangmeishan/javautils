package feat_extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SANCLNormalization {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		boolean bWellFormart = false;
		if(args.length > 3 && args[3].equals("true")) bWellFormart = true;
		Set<String> postags = new TreeSet<String>();
		String sLine = "";
		List<String> oneWords = new ArrayList<String>();
		List<String[]> oneSents = new ArrayList<String[]>();
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				if(oneWords.isEmpty())continue;
				String[] results = NormalizeSent(oneWords, bWellFormart);
				for(int idx = 0; idx < results.length; idx++)
				{
					String oneLine = results[idx];
					String[] allunits = oneSents.get(idx);
					//tag column
					if(results[idx].equals("-url-"))
					{
						oneLine = oneLine + "\t" + "FW";
						postags.add("FW");
					}
					else if(results[idx].equals("-lrb-"))
					{
						oneLine = oneLine + "\t" + "-LRB-";
						postags.add("-LRB-");
					}
					else if(results[idx].equals("-rrb-"))
					{
						oneLine = oneLine + "\t" + "-RRB-";
						postags.add("-RRB-");
					}
					else
					{
						oneLine = oneLine + "\t" + allunits[1];
						postags.add(allunits[1]);
					}
					for(int idy = 2; idy < allunits.length; idy++)
					{
						oneLine = oneLine + "\t" + allunits[idy];
					}
					out.println(oneLine);
				}
				out.println();
				
				oneWords = new ArrayList<String>();
				oneSents = new ArrayList<String[]>();
			}
			else
			{
				String[] allunits = sLine.split("\\s+");
				oneWords.add(allunits[0]);
				oneSents.add(allunits);
			}
		}
		
		in.close();
		out.close();
		
		PrintWriter out2 = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"), false);
		int count = 0;
		String oneposline = "   ";
		for(String postag : postags)
		{
			oneposline = oneposline + "\"" + postag + "\", ";
			if((count +1) % 5 == 0)
			{
				count = 0;
				out2.println(oneposline);
				oneposline = "   ";
			}
			else
			{
				count++;
			}
		}
		
		if(count > 0)
		{
			out2.println(oneposline);
		}

		out2.close();
	}
	
	public static String[] NormalizeSent(List<String> oneWords, boolean bWellFormart)
	{
		boolean bAllWordUppercase = true;
		boolean bLeftQuart = true;
		String[] results = new String[oneWords.size()];
		String url1 = "\\.[a-z]{2,}+";
		Pattern pattern1 = Pattern.compile(url1);
		//Pattern pattern2 = Pattern.compile(url2);
		
		for(int idx = 0; idx < oneWords.size(); idx++)
		{
			String oneWord = oneWords.get(idx);
			
			boolean bIsAlpha = true;
			//boolean bIsContainAlpha = false;
			boolean bAllUppercase = true;
			
			char[] chars = oneWord.toCharArray();
			
			String result = "";
			char lastChar = ' ';
			for(char ch : chars)
			{
				if(Character.isLetter(ch))
				{
					//bIsContainAlpha = true;
					result = result + ch;
					if(Character.isLowerCase(ch))
					{
						bAllUppercase = false;
					}
					lastChar = ch;
				}
				else
				{
					char newch = ch;
					if(Character.isDigit(newch)) newch = '0';
					if(newch != lastChar)
					{
						result = result + newch;
					}
					lastChar = newch;
					bIsAlpha = false;
				}
			}
			
			if(!bAllUppercase) bAllWordUppercase = false;
			
			if(!bWellFormart && chars.length > 4)
			{
				result =  result.toLowerCase();
			}
			
			if(result.equals("i"))
			{
				result = "I";
			}
			
			if(result.equals("nt"))
			{
				result = "not";
			}
			
			if(result.equals("s"))
			{
				result = "'s";
			}
			
			Matcher matcher1 = pattern1.matcher(result.toLowerCase());
			//Matcher matcher2 = pattern2.matcher(result.toLowerCase());
			
			if(!bIsAlpha)
			{
				if(result.equals("\"") || result.equals("'"))
				{
					if(bLeftQuart)
					{
						result = "''";
						bLeftQuart = false;
					}
					else
					{
						result =  "``";
						bLeftQuart = true;
					}
				}
				else if(result.equals("(") || result.equals("[") || result.equals("{") || result.equals("<"))
				{
					result = "-lrb-";
				}
				else if(result.equals(")") || result.equals("]") || result.equals("}") || result.equals(">"))
				{
					result = "-rrb-";
				}
				else if(matcher1.find() )//|| matcher2.find())
				{
					result = "-url-";
				}
			}
			
			results[idx] = result;
		}
			
		if(bAllWordUppercase && results.length > 1)
		{
			for(int idx = 0; idx < results.length; idx++)
			{
				if(results[idx].equals("-lrb-") || results[idx].equals("-rrb-") || results[idx].equals("-url-"))continue;
				results[idx] = results[idx].toLowerCase();
			}
		}
		
		return results;
	}

}
