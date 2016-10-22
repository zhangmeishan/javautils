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

		String target = "";
		String left = "";
		String right = "";
		String label = "";
		int count = 0;
		int mode = -1; //-1, left; 0, target; 1, right
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.equals(""))
			{
				left = left.trim();
				right = right.trim();
				target = target.trim();
				label = label.trim();
				
				if(!target.equals(""))
				{
					List<String> lefttoks = new ArrayList<String>();
					if(!left.equals(""))
					{
					     lefttoks = Twokenize.tokenizeRawTweetText(left);
					}
					
					List<String> righttoks = new ArrayList<String>();
					if(!right.equals(""))
					{
						righttoks = Twokenize.tokenizeRawTweetText(right);
					}
					
					List<String> targettoks = Twokenize.tokenizeRawTweetText(target);
					
					boolean leftbrac = true;
					for(int idx = 0; idx < lefttoks.size(); idx++)
					{
						String curToken = lefttoks.get(idx);
						curToken = normalize(curToken, leftbrac);
						if(curToken.equals("-lqt-"))leftbrac = false;
						if(curToken.equals("-rqt-"))leftbrac = true;
						
						out.println(curToken + " o");
					}
					
					for(int idx = 0; idx < targettoks.size(); idx++)
					{
						String curToken = targettoks.get(idx);
						curToken = normalize(curToken, leftbrac);
						if(curToken.equals("-lqt-"))leftbrac = false;
						if(curToken.equals("-rqt-"))leftbrac = true;
						
						if(idx == 0)out.println(curToken + " b-" + label);
						else out.println(curToken + " i-" + label);
					}
					
					for(int idx = 0; idx < righttoks.size(); idx++)
					{
						String curToken = righttoks.get(idx);
						curToken = normalize(curToken, leftbrac);
						if(curToken.equals("-lqt-"))leftbrac = false;
						if(curToken.equals("-rqt-"))leftbrac = true;
						
						out.println(curToken + " o");
					}
					
					out.println();
					count++;
				}
				
				target = "";  left = "";  right = ""; label = ""; mode = -1;

			}
			else
			{
				String[] smallunits = sLine.split("\\s+");
				if(smallunits.length != 2){
					System.out.println("error");
				}
				String theLabel = smallunits[1];
				if(theLabel.length() > 2 && mode <= 0)
				{
					mode = 0;
					label = theLabel.substring(2);
				}
				
				if(mode == 0 && theLabel.length() == 1)
				{
					mode = 1;
				}
				
				if(mode == -1) left = left + " " + smallunits[0];
				if(mode == 0) target = target + " " + smallunits[0];
				if(mode == 1) right = right + " " + smallunits[0];				
			}

		}
		
		{
			if(!target.equals(""))
			{
				List<String> lefttoks = new ArrayList<String>();
				if(!left.equals(""))
				{
				     lefttoks = Twokenize.tokenizeRawTweetText(left);
				}
				
				List<String> righttoks = new ArrayList<String>();
				if(!right.equals(""))
				{
					righttoks = Twokenize.tokenizeRawTweetText(right);
				}
				
				List<String> targettoks = Twokenize.tokenizeRawTweetText(target);
				
				boolean leftbrac = true;
				for(int idx = 0; idx < lefttoks.size(); idx++)
				{
					String curToken = lefttoks.get(idx);
					curToken = normalize(curToken, leftbrac);
					if(curToken.equals("-lqt-"))leftbrac = false;
					if(curToken.equals("-rqt-"))leftbrac = true;
					
					out.println(curToken + " o");
				}
				
				for(int idx = 0; idx < targettoks.size(); idx++)
				{
					String curToken = targettoks.get(idx);
					curToken = normalize(curToken, leftbrac);
					if(curToken.equals("-lqt-"))leftbrac = false;
					if(curToken.equals("-rqt-"))leftbrac = true;
					
					if(idx == 0)out.println(curToken + " b-" + label);
					else out.println(curToken + " i-" + label);
				}
				
				for(int idx = 0; idx < righttoks.size(); idx++)
				{
					String curToken = righttoks.get(idx);
					curToken = normalize(curToken, leftbrac);
					if(curToken.equals("-lqt-"))leftbrac = false;
					if(curToken.equals("-rqt-"))leftbrac = true;
					
					out.println(curToken + " o");
				}
				
				out.println();
				count++;
			}
			
			target = "";  left = "";  right = ""; label = ""; mode = -1;
		}
		
		System.out.println(count);
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
