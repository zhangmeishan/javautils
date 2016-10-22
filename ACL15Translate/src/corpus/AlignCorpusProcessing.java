package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class AlignCorpusProcessing {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in_align = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		PrintWriter writer_src = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));	
		
		PrintWriter writer_tgt = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));	
		
		String sLine = "";
		
		while((sLine = in_align.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			String[] smallunits = sLine.split("\\s+");
			String en_zh_line = "";
			String zh_en_line = "";
			for(int idx = 0; idx < smallunits.length; idx++)
			{
				int splitIndex = smallunits[idx].indexOf("-");
				int targetSeq = Integer.parseInt(smallunits[idx].substring(0, splitIndex));
				int sourceSeq = Integer.parseInt(smallunits[idx].substring(splitIndex+1));
				String en_zh_out = String.format("%d %d", targetSeq, sourceSeq);
				String zh_en_out = String.format("%d %d", sourceSeq, targetSeq);
				en_zh_line = en_zh_line + " " + en_zh_out;
				zh_en_line = zh_en_line + " " + zh_en_out;
			}
			writer_src.println(zh_en_line.trim());
			writer_tgt.println(en_zh_line.trim());
		}
		
		
		in_align.close();
		writer_src.close();
		writer_tgt.close();

	}

}
