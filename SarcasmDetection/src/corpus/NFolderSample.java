package corpus;


import java.io.*;
import java.util.*;
import java.util.Map.Entry;



public class NFolderSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));		
		String sLine = null;
		List<List<String>> vecInstances = new ArrayList<List<String>>();
		List<String> oneSen = new ArrayList<String>();
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.equals(""))
			{
				if(oneSen.size() > 0)
				{
					vecInstances.add(oneSen);
				}
				oneSen = new ArrayList<String>();
			}
			else
			{
				oneSen.add(sLine);
			}
		}
		
		in.close();
		
		int nFold = Integer.parseInt(args[1]);
		boolean bRandom = false;
		if (nFold < 0) {
			bRandom = true;
			nFold = -nFold;
		}

		if (bRandom) {
			Collections.shuffle(vecInstances, new Random(0));
		}
		
		Random rand = new Random(1);
		int totalInstancesNum = vecInstances.size();
		int intervalNum = (totalInstancesNum + nFold - 1) / nFold;
		String mark = args[2];
		int termNum = 100;
		if(args.length > 3) termNum = Integer.parseInt(args[3]);
		for (int curFold = 0; curFold < nFold; curFold++) {
			int[] bTrain = new int[totalInstancesNum];
			for (int idx = 0; idx < totalInstancesNum; idx++) {
				if(rand.nextDouble() < 0.9)bTrain[idx] = 1;
				else bTrain[idx] = 2;
			}

			for (int idx = curFold * intervalNum; idx < (curFold + 1)
					* intervalNum
					&& idx < totalInstancesNum; idx++) {
				bTrain[idx] = 3;
			}
			
			Map<String, Integer> wordDocumentFreq = new HashMap<String, Integer>();
			int totalDocuments = 0;
			for (int idx = 0; idx < totalInstancesNum; idx++) {
				if (bTrain[idx] == 1)
				{
					Set<String> curInstanceWords = new HashSet<String>();
					List<String> curInstance = vecInstances.get(idx);
					for(int idy = 0; idy < curInstance.size()-1; idy++)
					{
						String curline = curInstance.get(idy);
						String[] words = curline.split("\\s+");
						for(int idz = 1; idz < words.length; idz++)
						{
							curInstanceWords.add(words[idz]);
						}
					}
					
					for(String curWord : curInstanceWords)
					{
						if(!wordDocumentFreq.containsKey(curWord))
						{
							wordDocumentFreq.put(curWord, 0);
						}
						wordDocumentFreq.put(curWord, wordDocumentFreq.get(curWord)+1);
					}
					totalDocuments++;
				}
			}
			
			
			List<List<String>> refinedInstances = new ArrayList<List<String>>();
			for (int idx = 0; idx < totalInstancesNum; idx++) {
				Map<String, Integer> curInstanceWords = new HashMap<String, Integer>();
				int totalWords = 0;
				List<String> curInstance = vecInstances.get(idx);
				for(int idy = 0; idy < curInstance.size()-1; idy++)
				{
					String curline = curInstance.get(idy);
					String[] words = curline.split("\\s+");
					for(int idz = 1; idz < words.length; idz++)
					{
						if(!curInstanceWords.containsKey(words[idz]))
						{
							curInstanceWords.put(words[idz], 0);
						}
						curInstanceWords.put(words[idz], curInstanceWords.get(words[idz])+1);
						totalWords++;
					}
				}
				
				Map<String, Double> curInstanceWordTFITFs = new HashMap<String, Double>();
				for(String curWord : curInstanceWords.keySet())
				{
					double curTFIDF = (curInstanceWords.get(curWord) + 1.0) * 1.0 / (totalWords + 1.0);
					if(wordDocumentFreq.containsKey(curWord))
					{
						curTFIDF = curTFIDF * Math.log( (totalDocuments + 1.0) * 1.0 / (wordDocumentFreq.get(curWord) + 1.0));
					}
					else
					{
						curTFIDF = curTFIDF * Math.log( (totalDocuments + 1.0) * 1.0);
					}
					
					curInstanceWordTFITFs.put(curWord, curTFIDF);
				}
				
				List<Entry<String, Double>> sortedTermWithTfIdfs = MapDoubleSort(curInstanceWordTFITFs);
				
				List<String> refinedInstance = new ArrayList<String>();
				if(curInstance.size() > 1)
				{
					String line1 = "history";
					for(int idy = 0; idy < termNum && idy < sortedTermWithTfIdfs.size(); idy++)
					{
						line1 = line1 + " " + sortedTermWithTfIdfs.get(idy).getKey();
					}
					refinedInstance.add(line1);
				}
				refinedInstance.add(curInstance.get(curInstance.size()-1));
				refinedInstances.add(refinedInstance);
			}

			String outputFile1 = mark
					+ String.format(".train%d.nn", curFold + 1);
			String outputFile2 = mark
					+ String.format(".dev%d.nn", curFold + 1);
			String outputFile3 = mark
					+ String.format(".test%d.nn", curFold + 1);

			PrintWriter writer1 = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile1), "UTF-8"));
			PrintWriter writer2 = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile2), "UTF-8"));
			PrintWriter writer3 = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile3), "UTF-8"));
			int count1 = 0, count2 = 0, count3 = 0;
			for (int idx = 0; idx < totalInstancesNum; idx++) {
				List<String> curInstance = refinedInstances.get(idx);
				if (bTrain[idx] == 1) {	
					count1++;
					for(String oneline : curInstance)
					{
						writer1.println(oneline);
					}
					writer1.println();
				} else if (bTrain[idx] == 2) {	
					count2++;
					for(String oneline : curInstance)
					{
						writer2.println(oneline);
					}
					writer2.println();
				}  else if (bTrain[idx] == 3) {	
					count3++;
					for(String oneline : curInstance)
					{
						writer3.println(oneline);
					}
					writer3.println();
				}
				else
				{
					System.out.println("error....");
				}
				
			}
			System.out.println(String.format("%s:%d, %s:%d, %s:%d", String.format("train%d", curFold + 1), count1, 
					String.format("dev%d", curFold + 1), count2, String.format("test%d", curFold + 1), count3 ));
			writer1.close();
			writer2.close();
			writer3.close();
		}

	}
	
	public static List<Entry<String, Double>> MapDoubleSort(Map<String, Double> input)
	{
		List<Entry<String, Double>> mapintsort = new ArrayList<Entry<String, Double>>(input.entrySet());
		
		Collections.sort(mapintsort, new Comparator(){   
			public int compare(Object o1, Object o2) {    
				Map.Entry obj1 = (Map.Entry) o1;
				Map.Entry obj2 = (Map.Entry) o2;
				
				Double a1 = (Double)obj1.getValue();
				Double a2 = (Double)obj2.getValue();
				
				return a2.compareTo(a1);				
            }   
		}); 
		
		return mapintsort;
	}

}
