package feat_extractor;

import java.io.*;
import java.util.*;

import mason.utils.PinyinComparator;

public class ChineseJointST {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		int charlength;
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
				charlength = charlength + words[idx].length();
			}
			
			String[] chnchars = new String[charlength];
			String[] chncharseglabel = new String[charlength];
			String[] chncharposlabel = new String[charlength];
			
			int offset = 0;
			for(int idx = 0; idx < words.length; idx++)
			{
				int curWordLength = words[idx].length();
				for(int idy = 0; idy < curWordLength; idy++)
				{
					chnchars[offset] = words[idx].substring(idy, idy+1);
					if(curWordLength == 1)
					{
						chncharseglabel[offset] = "s-seg";
						chncharposlabel[offset] = "b-" + poss[idx];
					}
					else if(idy == 0)
					{
						chncharseglabel[offset] = "b-seg";
						chncharposlabel[offset] = "b-" + poss[idx];
					}
					else if(idy == curWordLength-1)
					{
						chncharseglabel[offset] = "e-seg";
						chncharposlabel[offset] = "i-" + poss[idx];
					}
					else
					{
						chncharseglabel[offset] = "m-seg";
						chncharposlabel[offset] = "i-" + poss[idx];
					}
					offset++;
				}
			}
			assert(charlength == offset);
			
			for(int idx = -1; idx <= charlength; idx++)
			{			
				String curChar = idx >= 0 && idx < charlength ? chnchars[idx] : "-null-";
				String prev1Char = idx-1 >= 0 && idx-1 < charlength ? chnchars[idx-1] : "-null-";
				String next1Char = idx+1 >= 0 && idx+1 < charlength ? chnchars[idx+1] : "-null-";
				String prev2Char = idx-2 >= 0 && idx-2 < charlength ? chnchars[idx-2] : "-null-";
				String next2Char = idx+2 >= 0 && idx+2 < charlength ? chnchars[idx+2] : "-null-";
			
				String curCharType = "SM";
				String prev1CharType = "SM";
				String next1CharType = "SM";
				
				if(curChar.length()>1) curCharType = "FK";
				else if(Character.isDigit(curChar.charAt(0)))curCharType = "DG";
				else if(Character.isLetter(curChar.charAt(0)))curCharType = "EN";
				else if(PinyinComparator.bChineseCharacter(curChar.charAt(0)))curCharType = "CH";
			
				if(prev1Char.length()>1) prev1CharType = "FK";
				else if(Character.isDigit(prev1Char.charAt(0)))prev1CharType = "DG";
				else if(Character.isLetter(prev1Char.charAt(0)))prev1CharType = "EN";
				else if(PinyinComparator.bChineseCharacter(prev1Char.charAt(0)))prev1CharType = "CH";

				if(next1Char.length()>1) next1CharType = "FK";
				else if(Character.isDigit(next1Char.charAt(0)))next1CharType = "DG";
				else if(Character.isLetter(next1Char.charAt(0)))next1CharType = "EN";
				else if(PinyinComparator.bChineseCharacter(next1Char.charAt(0)))next1CharType = "CH";

				List<String> feats = new ArrayList<String>();
				
				feats.add("[S]F1=" + curChar);
				feats.add("[S]F2=" + prev1Char);
				feats.add("[S]F3=" + next1Char);
				feats.add("[S]F4=" + prev2Char);
				feats.add("[S]F5=" + next2Char);
				
				feats.add("[S]F6=" + curChar + "#" + prev1Char);
				feats.add("[S]F7=" + curChar + "#" + next1Char);
				feats.add("[S]F8=" + curChar + "#" + prev2Char);
				feats.add("[S]F9=" + curChar + "#" + next2Char);
				
				feats.add("[S]F10=" + prev2Char + "#" + prev1Char);
				feats.add("[S]F11=" + prev1Char + "#" + curChar);
				feats.add("[S]F12=" + curChar + "#" + next1Char);
				feats.add("[S]F13=" + next1Char + "#" + next2Char);
				
				feats.add("[S]F14=" + prev1Char + "#" + next1Char);
				
				feats.add("[S]F15=" + prev1Char + "#" + curChar + "#" + next1Char);
				
				feats.add("[S]F16=" + prev2Char + "#" + prev1Char + "#" + curChar);
				feats.add("[S]F17=" + curChar + "#" + next1Char + "#" + next2Char);
				
				feats.add("[S]F18=" + curCharType);
				feats.add("[S]F19=" + prev1CharType);
				feats.add("[S]F20=" + next1CharType);
				
				feats.add("[S]F21=" + prev1CharType + "#" + curCharType);
				feats.add("[S]F22=" + curCharType + "#" + next1CharType);
				
				feats.add("[S]F23=" + prev1CharType + "#" + next1CharType);			
				feats.add("[S]F24=" + prev1CharType + "#" + curCharType + "#" + next1CharType);
				
				String curoutline = curChar;
				for(String curFeat : feats)
				{
					curoutline = curoutline + " " + curFeat;
				}
				
				String curCharSegLabel = idx >= 0 && idx < charlength ? chncharseglabel[idx] : "-padding-";
				curoutline = curoutline + " " + curCharSegLabel;
				String curCharPosLabel = idx >= 0 && idx < charlength ? chncharposlabel[idx] : "-padding-";
				curoutline = curoutline + " " + curCharPosLabel;
				
				out.println(curoutline);
			}
			out.println();
		}
		
		out.close();
		in.close();
	}

}
