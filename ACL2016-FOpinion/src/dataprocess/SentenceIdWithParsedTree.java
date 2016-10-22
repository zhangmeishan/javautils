package dataprocess;


import java.io.*;
import java.util.*;

import corpus.DepInstance;

public class SentenceIdWithParsedTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in_sentId = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		
		BufferedReader in_parsedTree = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF8"));

		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		String sentId;
		String sLine;
		while ((sentId = in_sentId.readLine()) != null){
			sentId = sentId.trim();
			String[] smallunits = sentId.split("\\s+");
			if(smallunits.length != 3){
				System.out.println("Error sentence id!");
				break;
			}
			List<String> parsedInputs = new ArrayList<String>();
			
			while ((sLine = in_parsedTree.readLine()) != null){
				sLine = sLine.trim();
				if(!sLine.isEmpty()){
					parsedInputs.add(sLine);
					break;
				}
			}
						
			while ((sLine = in_parsedTree.readLine()) != null){
				sLine = sLine.trim();
				if(sLine.isEmpty()){					
					break;
				}
				parsedInputs.add(sLine);
			}
			
			if(parsedInputs.size() > 0){
				List<String> zhangyue_outs = new ArrayList<String>();
				DepInstance tempInst = new DepInstance();
				tempInst.parseString(parsedInputs, false);
				tempInst.toZhangYueString(zhangyue_outs);
				if(zhangyue_outs.size() != parsedInputs.size()){
					System.out.println("Error Parsing Tree.....");
				}
				else{
					out.println(sentId);
					for(int idx = 0; idx < zhangyue_outs.size(); idx++){
						out.println(zhangyue_outs.get(idx));
					}
					out.println();
				}
			}
			else{
				System.out.println("Error sentence id: could not find its parsed tree!");
			}
				
			
		}
		
		
		in_sentId.close();
		in_parsedTree.close();
		out.close();
	}

}
