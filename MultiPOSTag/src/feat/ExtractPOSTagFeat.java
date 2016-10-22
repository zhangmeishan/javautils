package feat;


import java.io.*;
import java.util.*;

import net.sourceforge.pinyin4j.PinyinHelper;


public class ExtractPOSTagFeat {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
	
		String language = args[3];
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"), false);		
		ExtractFeatures(args[0], 0 , language, out);
		ExtractFeatures(args[1], 1 , language, out);
		out.close();

	}
	
	public static void ExtractFeatures(String inputFile, int labelId, String language, PrintWriter out) throws Exception
	{
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile), "UTF8"));
		
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))continue;
			String[] wordposs = sLine.trim().split("\\s+");
			String[] words = new String[wordposs.length];
			String[] poss = new String[wordposs.length];

			for(int idx  = 0; idx < wordposs.length; idx++)
			{
				String wordpos = wordposs[idx];
				int splitIndex = wordpos.lastIndexOf("/");
				if(splitIndex == 0)
				{
					System.out.println("error: " + sLine);
					return;
				}
				if(splitIndex == -1 ||  splitIndex == wordpos.length()-1)
				{
					words[idx] = wordposs[idx];
					poss[idx] = "_";
				}
				else
				{
					words[idx] = wordposs[idx].substring(0, splitIndex);
					poss[idx] =  wordposs[idx].substring(splitIndex+1);
				}
			}
			
			
			for(int idx  = 0; idx < wordposs.length; idx++)
			{
				String prev2word = idx >= 2? words[idx-2] : "#START#";
				String prev1word = idx >= 1? words[idx-1] : "#START#";
				String curword = words[idx];
				String next1word = idx+1 < wordposs.length ? words[idx+1] : "#END#";
				String next2word = idx+2 < wordposs.length ? words[idx+2] : "#END#";
				
			   List<String> curfeatures = new ArrayList<String>();
			   curfeatures.add("P2@"+prev2word);
			   curfeatures.add("P1@"+prev1word);
			   curfeatures.add("U@"+curword);
			   curfeatures.add("N1@"+next1word);
			   curfeatures.add("N2@"+next2word);
			   curfeatures.add("BiP1U@" + prev1word + "#" + curword);
			   curfeatures.add("BiN1U@" + next1word + "#" + curword);
			   curfeatures.add("BiP1N1@" + prev1word + "#" + next1word);
			   int fixlength = 5;
			   if(language.equalsIgnoreCase("chinese"))
			   {
				   fixlength = 3;
			   }
			   for(int idy = 1; idy <= fixlength && idy < curword.length(); idy++)
			   {
				   String premark = String.format("PRE%d@", idy);
				   String sufmark = String.format("SUF%d@", idy);
				   curfeatures.add(premark + curword.substring(0, idy));
				   curfeatures.add(sufmark + curword.substring(curword.length()-idy));
			   }
			   
			   if(language.equalsIgnoreCase("chinese"))
			   {
				   for(int idy = 0; idy < curword.length(); idy++)
				   {
					   curfeatures.add("curChar@" + curword.substring(idy, idy+1));
				   }
				   
				   curfeatures.add(String.format("length@%d", curword.length() > 5 ? 5:curword.length() ));
				   
				   curfeatures.add("firstlastchars@" + curword.substring(0, 1) + curword.substring(curword.length()-1));
				   curfeatures.add("firstbichars@" + curword.substring(0, 1) + (idx >= 1? words[idx-1].substring(0,1) : "#START#"));
			   }
			   

			   String output = curword;
			   
			   for(int idy = 0; idy < curword.length(); idy++){
				   char curChar =  curword.charAt(idy);
				   String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(curChar);
				   
				   if(pinyin != null)
				   {
					   output = output + " [C]" + curChar;
				   }
				   else if(Character.isDigit(curChar))
				   {
					   output = output + " [C]0";
				   }
				   else if(Character.isLetter(curChar))
				   {
					   if(Character.isLowerCase(curChar))
					   {
						   output = output + " [C]a";
					   }
					   else
					   {
						   output = output + " [C]A";
					   }
				   }
				   else
				   {
					   output = output + " [C]" + curChar;
				   }
			   }
			   
			   for(String thefeat : curfeatures)
			   {
				   output = output + " [S]" + thefeat;
			   }
			   
			   
			   
			   if(labelId == 1)output = output + " # " + poss[idx];
			   else output = output + " " + poss[idx] + " #";
			   out.println(output.trim());
			}
			
			out.println();
		}
		
		
		in.close();
	}

}
