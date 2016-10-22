package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SplitDev {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		double perc = Double.parseDouble(args[3]);
		boolean bLine = true;
		if(args.length > 4 && args[4].equals("dp")) bLine = false;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));		
		String sLine = null;
		List<String> corpus = new ArrayList<String>();
		String oneSen = "";
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))
			{
				if(!oneSen.trim().equals(""))
				{
					corpus.add(oneSen.trim());
				}
				oneSen = "";
			}
			else
			{
				if(bLine)
				{
					corpus.add(sLine);
				}
				else
				{
					
					oneSen = oneSen + System.getProperty("line.separator") + sLine;
				}
			}
		}
		
		in.close();
		
		int selectNum = (int) (corpus.size() * perc);
		
		//for(int i = 0; i < sampleNum; i++)
		{
			//String outFileName = String.format("%s.%d", args[0], i);
			PrintWriter out1 = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(args[1]), "UTF-8"));
			PrintWriter out2 = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(args[2]), "UTF-8"));
			
			//Collections.shuffle(corpus, new Random(i));
			for(int curSeq = 0; curSeq < corpus.size(); curSeq++)
			{
				if(curSeq < selectNum)
				{
					out1.println(corpus.get(curSeq));
					if(!bLine)
					{
						out1.println();
					}
				}
				else
				{
					out2.println(corpus.get(curSeq));
					if(!bLine)
					{
						out2.println();
					}
				}
			}
			
			out1.close();
			out2.close();
		}
		

	}

}
