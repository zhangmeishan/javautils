package feat_extractor;

import java.io.*;
import java.util.*;

import mason.utils.PinyinComparator;

public class ChineseJointST2Conll {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		int charlength;
		int mode = 0;  // 0, not initialized; 1, seg; 2 pos
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))continue;
			String[] wordposs = sLine.trim().split("\\s+");
			String[] words = new String[wordposs.length];
			String[] poss = new String[wordposs.length];
			charlength = 0;
			for(int idx  = 0; idx < wordposs.length; idx++)
			{
				String wordpos = wordposs[idx];
				int splitIndex = wordpos.lastIndexOf("_");
				if(splitIndex == 0)
				{
					System.out.println("Error: " + sLine);
					return;
				}
				if(splitIndex == -1)
				{
					if(mode == 0){
						mode = 1;
					}
					if(mode != 1){
						System.out.println("First line is pos tagging mode.");
						System.out.println("Error: " + sLine);
						return;
					}
					words[idx] = wordposs[idx];
				}
				else
				{
					if(mode == 0){
						mode = 2;
					}
					if(mode != 2){
						System.out.println("First line is segmentation mode.");
						System.out.println("Error: " + sLine);
						return;
					}
					words[idx] = wordposs[idx].substring(0, splitIndex);
					poss[idx] =  wordposs[idx].substring(splitIndex+1);
				}
				charlength = charlength + words[idx].length();
			}
			
			String[] chnchars = new String[charlength];
			String[] chncharlabel = new String[charlength];
			
			if(mode != 1 && mode != 2){
				System.out.println("Mode Error: " + mode);
				return;
			}
			
			int offset = 0;
			for(int idx = 0; idx < words.length; idx++)
			{
				int curWordLength = words[idx].length();
				for(int idy = 0; idy < curWordLength; idy++)
				{
					chnchars[offset] = words[idx].substring(idy, idy+1);
					if(curWordLength == 1)
					{
						chncharlabel[offset] = mode == 1 ? "s" : "b-" + poss[idx];
					}
					else if(idy == 0)
					{
						chncharlabel[offset] = mode == 1 ? "b" : "b-" + poss[idx];
					}
					else if(idy == curWordLength-1)
					{
						chncharlabel[offset] = mode == 1 ? "e" : "i";
					}
					else
					{
						chncharlabel[offset] = mode == 1 ? "m" : "i";
					}
					offset++;
				}
			}
			assert(charlength == offset);
			
			for(int idx = 0; idx < charlength; idx++)
			{						
				String curoutline = chnchars[idx] + "\t" + chncharlabel[idx];
				
				out.println(curoutline);
			}
			out.println();
		}
		
		out.close();
		in.close();
	}

}
