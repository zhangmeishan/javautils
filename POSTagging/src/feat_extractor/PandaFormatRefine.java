package feat_extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class PandaFormatRefine {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		String sLine = "", outLine = "";
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				out.println();
				continue;
			}
			
			String[] units = sLine.split("\t");
			
			if(units.length != 3){
				System.out.println("error!");
			}
			
			String theChar = units[0];
			String theTag = units[1];
			String[] theCandidateTags = units[2].split("#");
			
			outLine = theChar + "\t" + theTag + "\t";
			
			for(int idx = 0; idx < theCandidateTags.length; idx++)
			{
				String[] smallunits = theCandidateTags[idx].split(" ");
				if(smallunits.length != 2){
					System.out.println("error!");
				}
				if(idx == 0){
					outLine = outLine + smallunits[0];
				}
				else{
					outLine = outLine + "#" + smallunits[0];
				}
			}
			
			out.println(outLine);
		}
		
		in.close();
		out.close();
	}

}
