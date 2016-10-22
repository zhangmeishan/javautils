package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class POSEvaluater {


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String splitchar = "_";
		if(args.length>3 && args[3].equalsIgnoreCase("/"))
		{
			splitchar = "/";
		}
		
		PrintWriter output = new PrintWriter(System.out);
		
		if(args.length > 2 && !args[2].equals("console"))
		{
			output = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(args[2]), "UTF-8"), false);
		}
		
		int allwordNum = 0;
		int correctwordNum = 0;
		
		BufferedReader reader_pre = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8"));
		BufferedReader reader_gold = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF-8"));
		String sLine1 = null;
		String sLine2 = null;
		while ((sLine1 = reader_pre.readLine()) != null && (sLine2 = reader_gold.readLine()) != null) {
			if (sLine1.trim().equals("") || sLine2.trim().equals(""))
				continue;
			String[] wordElemPreds = sLine1.trim().split("\\s+");
			String[] wordElemGolds = sLine2.trim().split("\\s+");
			if(wordElemPreds.length != wordElemGolds.length)
			{
				System.out.println("evaluation error, please check");
				System.out.println(sLine1);
				System.out.println(sLine2);
				return;
			}
			
			allwordNum = allwordNum + wordElemPreds.length;
			
			for(int idx = 0; idx < wordElemPreds.length; idx++)
			{
				int sepIndex1 = wordElemPreds[idx].indexOf(splitchar);
				int sepIndex2 = wordElemGolds[idx].indexOf(splitchar);
				if(!wordElemPreds[idx].substring(0, sepIndex1).equals((wordElemGolds[idx].substring(0, sepIndex2))))
				{
					System.out.println("evaluation error, please check");
					System.out.println(sLine1);
					System.out.println(sLine2);
					return;
				}
				
				if(wordElemPreds[idx].equals(wordElemGolds[idx]))
				{
					correctwordNum++;
				}
						
			}
			
		}
		
		output.println(String.format("Tagging accuracy: P=%d/%d=%f", 
	    		correctwordNum,allwordNum,correctwordNum*1.0/allwordNum
			    ));

		reader_pre.close();
		reader_gold.close();
		output.close();
	}




}
