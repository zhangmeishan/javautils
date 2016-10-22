package feat;

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

public class Split2SinglePOSTagFeat {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub	
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out1 = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		PrintWriter out2 = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"), false);
		String sLine = "";
		int v1 = 0, v2 = 0;
		boolean v1sequence = false, v2sequence = false; 
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals("")){
				if(v1sequence){
					out1.println();
				}
				if(v2sequence){
					out2.println();
				}
				continue;
			}
			String[] smallunits = sLine.trim().split("\\s+");
			if(smallunits[smallunits.length-1].equals("#")){
				v1++;
				String outline = "";
				for(int idx = 0; idx < smallunits.length-2; idx++){
					outline = outline + smallunits[idx] + " ";
				}
				outline = outline + smallunits[smallunits.length-2];
				out1.println(outline);
				v1sequence = true;
				v2sequence = false;
			}
			else if(smallunits[smallunits.length-2].equals("#")){
				v2++;
				String outline = "";
				for(int idx = 0; idx < smallunits.length-2; idx++){
					outline = outline + smallunits[idx] + " ";
				}
				outline = outline + smallunits[smallunits.length-1];
				out2.println(outline);
				v2sequence = true;
				v1sequence = false;
			}
		}
		
		System.out.println(String.format("verison1 num: %d", v1));
		System.out.println(String.format("verison2 num: %d", v2));
		out1.close();
		out2.close();

	}
	
}
