package corpus;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class NFolderSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		boolean bLine = true;
		if(args.length > 3 && args[3].equals("line")) bLine = false;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));		
		String sLine = null;
		List<String> vecInstances = new ArrayList<String>();
		String oneSen = "";
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))
			{
				if(!oneSen.trim().equals(""))
				{
					vecInstances.add(oneSen.trim());
				}
				oneSen = "";
			}
			else
			{
				if(bLine)
				{
					vecInstances.add(sLine);
				}
				else
				{
					
					oneSen = oneSen + System.getProperty("line.separator") + sLine;
				}
			}
		}
		
		in.close();
		
		int nFold = Integer.parseInt(args[1]);
		boolean bRandom = false;
		if (nFold < 0) {
			bRandom = true;
			nFold = -nFold;
		}
		
		double devPer = Double.parseDouble(args[2]);
		
		Random rand = new Random(0);
		
		if (bRandom) {
			Collections.shuffle(vecInstances, rand);
		}

		int totalInstancesNum = vecInstances.size();
		int intervalNum = (totalInstancesNum + nFold - 1) / nFold;

		for (int curFold = 0; curFold < nFold; curFold++) {
			int[] corpusType = new int[totalInstancesNum];
			
			//train, default
			for (int idx = 0; idx < totalInstancesNum; idx++) {
				corpusType[idx] = 0;
				if(rand.nextDouble() <  devPer)
				{
					corpusType[idx] = 2;
				}
			}

			//test
			int testFold = curFold;
			for (int idx = testFold * intervalNum; idx < (testFold + 1)
					* intervalNum
					&& idx < totalInstancesNum; idx++) {
				corpusType[idx] = 1;
			}
			

			String outputFile0 = args[0]
					+ String.format(".train.%d", curFold + 1);
			String outputFile1 = args[0]
					+ String.format(".test.%d", curFold + 1);
			String outputFile2 = args[0]
					+ String.format(".dev.%d", curFold + 1);

			PrintWriter[] writers = new PrintWriter[3];
			writers[0] = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile0), "UTF-8"));
			writers[1] = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile1), "UTF-8"));
			writers[2] = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile2), "UTF-8"));
			int[] counts = new int[3];
			counts[0] = 0;  counts[1] = 0;  counts[2] = 0; 
			for (int idx = 0; idx < totalInstancesNum; idx++) {
				String curInstance = vecInstances.get(idx);

				writers[corpusType[idx]].println(curInstance);
				counts[corpusType[idx]]++;
				if(!bLine) writers[corpusType[idx]].println();			
			}
			System.out.println(String.format("%s:%d, %s:%d, %s:%d", String.format("train.%d", curFold + 1), counts[0], 
					String.format("test.%d", curFold + 1), counts[1], String.format("dev.%d", curFold + 1), counts[2]));
			
			
			writers[0].close();
			writers[1].close();
			writers[2].close();
		}

	}

}
