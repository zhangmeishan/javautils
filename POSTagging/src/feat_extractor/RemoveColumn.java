package feat_extractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RemoveColumn {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		int column = Integer.parseInt(args[2]);
		String sLine = "";
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.isEmpty())
			{
				out.println();
				continue;
			}
			
			String[] units = sLine.split("\\s+");
			String output = "";
			for(int idx = 0; idx < units.length; idx++){
				if(idx == column)continue;
				output = output + " " + units[idx];
			}
			out.println(output.trim());		
		}

		in.close();
		out.close();
	}

}
