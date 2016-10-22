package feat_extractor;

import java.io.*;
import java.util.*;

import mason.utils.PinyinComparator;

public class ChineseSTReplaceTags {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);

		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))continue;
			String[] wordposs = sLine.trim().split("\\s+");
			String[] words = new String[wordposs.length];
			String[] poss = new String[wordposs.length];
			String outline = "";
			for(int idx  = 0; idx < wordposs.length; idx++)
			{
				String wordpos = wordposs[idx];
				int splitIndex = wordpos.lastIndexOf("_");
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
				
				outline = outline + words[idx] + "_FK ";
			}
			
			out.println(outline.trim());
		}
		
		out.close();
		in.close();
	}

}
