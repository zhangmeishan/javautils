package feat_extractor;

import java.io.*;
import java.util.*;

public class ChineJointLabel2SegPOS {
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		List<String[]> oneSent;
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.trim().equals(""))continue;
		}
	}

}
