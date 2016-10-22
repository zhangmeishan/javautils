package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mason.utils.PinyinComparator;

public class ExtractSegmentationFeat {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))continue;
			String sentence = sLine.trim().replaceAll("\\s+", "");
			String[] words = sLine.trim().split("\\s+");
			String[] chars = new String[sentence.length()];
			String[] tags = new String[sentence.length()];

			int startpos = 0;
			for(int idx  = 0; idx < words.length; idx++)
			{
				String curwordtmp = words[idx];
				String curword = ZHUtil.toDBC(curwordtmp);
				if(!curword.equals(curwordtmp))
				{
					//System.out.println("check");
				}
					
				if(curword.length() == 1){
					chars[startpos] = curword;
					tags[startpos] = "s-seg";
				}
				else{
					chars[startpos] = curword.substring(0, 1);
					tags[startpos] = "b-seg";
					
					chars[startpos + curword.length() - 1] = curword.substring(curword.length() - 1);
					tags[startpos+ curword.length() - 1] = "e-seg";
					
					for(int idy = 1; idy < curword.length() - 1; idy++){
						chars[startpos + idy] = curword.substring(idy, idy+1);
						tags[startpos+ idy] = "m-seg";
					}
				}
				
				startpos = startpos + curword.length();
			}
			
			if(startpos != sentence.length())
			{
				System.out.println("error");
			}
			
			
			for(int idx  = 0; idx < chars.length; idx++)
			{
				String prev2char = idx >= 2? chars[idx-2] : "#START#";
				String prev1char = idx >= 1? chars[idx-1] : "#START#";
				String curchar = chars[idx];
				String next1char = idx+1 < chars.length ? chars[idx+1] : "#END#";
				String next2char = idx+2 < chars.length ? chars[idx+2] : "#END#";
				
			   List<String> curfeatures = new ArrayList<String>();
			   curfeatures.add("P2@"+prev2char);
			   curfeatures.add("P1@"+prev1char);
			   curfeatures.add("U@"+curchar);
			   curfeatures.add("N1@"+next1char);
			   curfeatures.add("N2@"+next2char);
			   curfeatures.add("BiP1U@" + prev1char + "#" + curchar);
			   curfeatures.add("BiN1U@" + next1char + "#" + curchar);
			   curfeatures.add("BiP1N1@" + prev1char + "#" + next1char);
			   curfeatures.add("TriP1UN1@" + prev1char + "#" + curchar + "#" + next1char);

			   String curcharType = characterType(curchar);
			   String prev1charType = characterType(prev1char);
			   String next1charType = characterType(next1char);
			   
			   curfeatures.add("TypeP1@"+prev1charType);
			   curfeatures.add("TypeU@"+curcharType);
			   curfeatures.add("TypeN1@"+next1charType);
			   curfeatures.add("TriTypeP1UN1@" + prev1charType + "#" + curcharType + "#" + next1charType);
			   
			   if(prev1char.equals(curchar)){
				   curfeatures.add("Dul1@Yes");
			   }
			   else{
				   curfeatures.add("Dul1@No");
			   }
			   
			   if(prev2char.equals(curchar)){
				   curfeatures.add("Dul2@Yes");
			   }
			   else{
				   curfeatures.add("Dul2@No");
			   }
			   
			   String output = curchar; 
			   output = output + " [T1]" + curchar+prev1char;
			   output = output + " [T2]" + curchar+prev1char+prev2char;
			   
			   for(String thefeat : curfeatures)
			   {
				   output = output + " [S]" + thefeat;
			   }
			   output = output + " " + tags[idx];
			   out.println(output.trim());
			}
			
			out.println();
		}
		
		out.close();
		in.close();
	}
	
	
	public static String characterType(String input){
		if(input.length() > 1) return "NULL";
		char c = input.charAt(0);
		if(PinyinComparator.bChineseCharacter(c)){
			return "CH";
		}
		else if (Character.isDigit(c)){
			return "DG";
		}
		else if(Character.isLetter(c)){
			return "EN";
		}
		
		return "SP";
	}

}
