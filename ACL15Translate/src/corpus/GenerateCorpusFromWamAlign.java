package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class GenerateCorpusFromWamAlign {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		// source language
		BufferedReader in_e = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		//target language
		BufferedReader in_f = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[1]), "UTF-8"));
		//align file  source-target
		BufferedReader in_align = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[2]), "UTF-8"));
		// output file
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[3]), "UTF-8"));
		
		
		double[][] aligns = new double[1024][1024];
		
		String eline, fline, alignline;
		while( (eline = in_e.readLine()) != null &&
			   (fline = in_f.readLine()) != null &&
			   (alignline = in_align.readLine()) != null
				)
		{			
			String[] source_words = eline.trim().split("\\s+");
			String[] target_words = fline.trim().split("\\s+");
			
			for(int idx = 0; idx < source_words.length; idx++)
				for(int idy = 0; idy < target_words.length; idy++)
				{
					aligns[idx][idy] = 0.0;
				}
			
			
			String[] align_units = alignline.trim().split("\\s+");
			
			for(String cur_align : align_units)
			{
				
			}
		}

		
		
		
		in_e.close();
		in_f.close();
		in_align.close();
		writer.close();
	}

}
