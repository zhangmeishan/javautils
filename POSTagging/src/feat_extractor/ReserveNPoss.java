package feat_extractor;

import mason.utils.MapSort;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ReserveNPoss {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		String sep = "\t";
		if(args.length > 3 && args[3].equals("li"))
		{
			sep = "_";
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		int maxPOSCandidates = Integer.parseInt(args[2]);
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))
			{
				out.println();
				continue;				
			}
			String[] poscandidates = sLine.trim().split("\\s+");
			
			if(poscandidates.length == 0 )
			{
				System.out.println("error....." + sLine);
				return;
			}
			
			String curOut = poscandidates[0];
			for(int idx = 1; idx < maxPOSCandidates && idx < poscandidates.length; idx++)
			{
				curOut = curOut + sep + poscandidates[idx];
			}
			
			out.println(curOut);
		}
		
		out.close();
		in.close();

	}



}
