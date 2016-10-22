package feat_extractor;

import java.io.*;
import java.util.*;

import mason.utils.PinyinComparator;

public class OpenTargeted {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		boolean left = true;
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))
			{
				out.println();
				left = true;
				continue;
			}
			String[] smallunits = sLine.trim().split("\\s+");
			int length = smallunits.length;
			assert(length >= 3);
			//String curWord = smallunits[0];
			String curWord = normalize(smallunits[0], left);
			if(curWord.equals("-lqt-"))left = false;
			if(curWord.equals("-rqt-"))left = true;
			String label1 = smallunits[length-2].toLowerCase();
			if(label1.startsWith("b_") || label1.startsWith("i_"))
			{
				label1 = label1.substring(0, 1) + "-" + label1.substring(2);
			}
			String label2 = smallunits[length-1].toLowerCase();
			if(label2.length()==1) 
			{
				label2 = "o";
			}
			else
			{
				if(label1.startsWith("b-") || label1.startsWith("i-"))
				{
					label2 = label1.substring(0, 2) + label2;
				}
				else
				{
					System.out.println("error");
				}
			}

			String curoutline = curWord;
			for(int idx = 1; idx < length-2; idx++)
			{
				String curFeat = "[S]" + smallunits[idx];
				curoutline = curoutline + " " + curFeat;
			}

			curoutline = curoutline + " " + label1;
			curoutline = curoutline + " " + label2;
			out.println(curoutline);
		}
		
		out.close();
		in.close();
	}
	
	
	public static String normalize(String s, boolean left) {
		int dot = 0, engchar=0, digitchar = 0;
		for (int i = 0; i < s.length(); i++) {
			char curChar = s.charAt(i);
			if (Character.isDigit(curChar)) {
				digitchar++;
			} else if (Character.isLetter(curChar)) {
				engchar++;
			} else if (curChar == '.'){
				dot++;
			}
		}
		
		if(s.charAt(0) == '@' && engchar+digitchar > 0)
		{
			return "-user-";
		}
		else if(s.length()>4 && dot > 0 && engchar > 0 )
		{
			return "-url-";
		}
		else if(s.equals("\"") || s.equals("'"))
		{
			if(left)
			{
				return "-lqt-";
			}
			else
			{
				return "-rqt-";
			}
		}
		else if(s.equals("nt"))
		{
			return "n't";
		}
		else if(s.equals("s"))
		{
			return "'s";
		}
		else if(s.equals("(") || s.equals("[") || s.equals("{") || s.equals("<") || s.equals("<<"))
		{
			return "-lrb-";
		}
		else if(s.equals(")") || s.equals("]") || s.equals("}") || s.equals(">") || s.equals(">>"))
		{
			return "-rrb-";
		}
		else
		{
			return s;
		}
	}
}
