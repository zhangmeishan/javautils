package feat_extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SANCLPostProcessing {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		String sLine = "";
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				out.println();
			}
			else
			{
				String[] allunits = sLine.split("\\s+");
				if(allunits[0].equalsIgnoreCase("-url-"))
				{
					allunits[1] = "NNP";
				}
				out.println(allunits[0] + "\t" +allunits[1] + "\t" +allunits[2] + "\t" +allunits[3]);
			}
		}
		
		in.close();
		out.close();
		
	}
}
