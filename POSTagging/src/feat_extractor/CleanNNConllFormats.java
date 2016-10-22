package feat_extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CleanNNConllFormats {

	/**
	 * @param args
	 */
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
				continue;
			}
			
			String[] units = sLine.split("\\s+");
			String biChar = units[1].substring(4);
			if(biChar.length() > 2){
				biChar = "<s>" + biChar.substring(0,1); 
			}
			else{
				biChar = biChar.substring(1,2) + biChar.substring(0,1);
			}
			out.println(units[0] + " " + "[T1]" + biChar + " " + units[units.length-1]);
			
		}
		
		in.close();
		out.close();

	}

}
