package corpus;

import java.io.*;
import java.util.*;


public class CorpusConversion {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		PrintWriter outrelease = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"), false);
		String lastLine = "";
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.equals(""))
			{
				if(!lastLine.equals(""))
				{
					out.println();
					outrelease.println();
				}
				
				lastLine = "";
				continue;
			}
			String[] smallunits = sLine.split("\\s+");
			
			if(smallunits.length < 5)
			{
				System.out.println("error: " + sLine);
			}
			String text = smallunits[4];
			for(int idx  = 5; idx < smallunits.length; idx++)
			{
				text = text + " " + smallunits[idx];
			}
			
			String outline = smallunits[0];
			boolean bsarcasm = true;
			if(smallunits[0].equalsIgnoreCase("history")) bsarcasm = false;
			List<String> toks = Twokenize.tokenizeRawTweetText(text);
			
			boolean left = true;
			for(int idx = 0; idx < toks.size(); idx++)
			{
				String curToken = toks.get(idx);				
				if(bsarcasm && curToken.length() > 1 && curToken.startsWith("#"))continue;
				curToken = normalize(curToken, left);
				if(curToken.equals("-lqt-"))left = false;
				if(curToken.equals("-rqt-"))left = true;
				outline = outline + " " + curToken;
			}

			out.println(outline);
			outrelease.println(smallunits[0] + " " + smallunits[1]);
			lastLine = outline;
		}
		
		out.close();
		outrelease.close();
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
