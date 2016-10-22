package tool;

import java.io.*;
import java.util.*;

public class AutoCollectFeatures {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//input file should be XXXnNode  name;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8"));
		
		PrintWriter output = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		String sLine;
		while ((sLine = in.readLine()) != null) {
			String normLine = sLine.trim();
			if(normLine.startsWith("sumNodes") || normLine.startsWith("//")){
				continue;
			}
			if(sLine.indexOf("forward") != -1){
				String preffix = "";
				for(int idx = 0; idx < sLine.length(); idx++){					
					if(!Character.isLetter(sLine.charAt(idx))){
						preffix = preffix + sLine.charAt(idx);
					}
					else{
						break;
					}
				}
				String firstnorm = normLine.replace("forward(cg, ", "(");
				firstnorm = firstnorm.replace("atomFeat.", "");
				String outline = preffix + "model->" + firstnorm;
				output.println(outline);
			}
			else{
				output.println(sLine);
			}
			
		
		}
		
		in.close();
		output.close();

	}

}
