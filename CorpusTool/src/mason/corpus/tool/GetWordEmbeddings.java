package mason.corpus.tool;

import java.io.*;
import java.util.*;

public class GetWordEmbeddings {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		Map<String, String> validWords = new TreeMap<String, String>();

		
		Set<String> values = new HashSet<String>();
		
		validWords.put("-UNKNOWN-", "UNKNOWN");
		validWords.put("-NULL-", "PADDING");
		values.add("UNKNOWN");
		values.add("PADDING");
		
		String sline;

		String mathcer = "[.-]*[0-9]+[0-9.,]*";
		//String checkstr = "123a789.550b.23410c.156";
		//String test = checkstr.replaceAll(mathcer, "0");
		while ((sline = input.readLine()) != null) {
			sline = sline.trim();
			String[] splits = sline.split("\\s+");
			
			for(String oneWord : splits)
			{
				String keyWord = oneWord.toLowerCase();
				String valueWord = keyWord.replaceAll(mathcer, "0");
				validWords.put(keyWord, valueWord);
				values.add(valueWord);
			}
		}
		
		input.close();
		
		Map<String, double[]> wordEmbeddings = new HashMap<String, double[]>();
		if(args.length == 3)
		{
			readEmbedFile(args[1], values, wordEmbeddings);
		}
		else
		{
			readEmbedFile(args[1], args[2], values, wordEmbeddings);
		}
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[args.length-1]), "UTF-8"));
		
		int count = 0;
		int total = 0;
		for(String curWord : validWords.keySet())		
		{
			String curValue = validWords.get(curWord);
			if(wordEmbeddings.containsKey(curValue))
			{
				String outline = curWord;
				double[] theEmbedding = wordEmbeddings.get(curValue);
				for(double theValue : theEmbedding)
				{
					outline = outline + String.format(" %f", theValue);
				}
				count++;
				writer.println(outline);
			}
			else
			{
				System.out.println(curWord);
			}
			total++;
		}
		
		writer.close();
		
		System.err.println(String.format("Word number in text: %d, Obtained Emb Number: %d, coverage %f", 
				total, count, count*1.0/total));
		
		
	}

	public static int readEmbedFile(String embedFile,
			Set<String> validWords, Map<String, double[]> wordEmbeddings) {

		int dim = -1;

		if (embedFile != null) {
			BufferedReader input = null;
			try {
				input = new BufferedReader(new InputStreamReader(
						new FileInputStream(embedFile)));
				String sline;

				while ((sline = input.readLine()) != null) {
					sline = sline.trim();
					String[] splits = sline.split("\\s+");

					if (dim == -1) {
						dim = splits.length - 1;

					} else {
						if (dim != splits.length - 1) {
							System.out.println("error input......");
						}
					}
					String theWord = splits[0];
					if(theWord.equals("-SLASH-")) theWord = "\\/";
					if(theWord.equals("-STAR-")) theWord = "*";
					
	
					double[] curWordEmbedding = new double[dim];	
					for (int j = 0; j < dim; ++j)
						curWordEmbedding[j] = Double.parseDouble(splits[j + 1]);
					if(validWords.contains(theWord)) wordEmbeddings.put(theWord, curWordEmbedding);

			
				}
				input.close();
			} catch (Exception e) {

			}
			
		}
		return dim;
	}
	
	
	public static int readEmbedFile(String vocFile, String embedFile,
			Set<String> validWords, Map<String, double[]> wordEmbeddings) {

		int dim = -1;

		if (embedFile != null && vocFile != null) {
			BufferedReader input_voc = null;
			BufferedReader input_emb = null;
			try {
				input_voc = new BufferedReader(new InputStreamReader(
						new FileInputStream(vocFile)));
				input_emb = new BufferedReader(new InputStreamReader(
						new FileInputStream(embedFile)));
				String sline_voc, sline_emb;

				while ((sline_emb = input_emb.readLine()) != null) {
					sline_voc = input_voc.readLine();
					sline_voc = sline_voc.trim();
					sline_emb = sline_emb.trim();
					String[] splits = sline_emb.split("\\s+");

					if (dim == -1) {
						dim = splits.length;

					} else {
						if (dim != splits.length) {
							System.out.println("error input......");
						}
					}
					String theWord = sline_voc;
					if(theWord.equals("-SLASH-")) theWord = "\\/";
					if(theWord.equals("-STAR-")) theWord = "*";
					
	
					double[] curWordEmbedding = new double[dim];	
					for (int j = 0; j < dim; ++j)
						curWordEmbedding[j] = Double.parseDouble(splits[j]);
					if(validWords.contains(theWord)) wordEmbeddings.put(theWord, curWordEmbedding);

			
				}
				input_voc.close();
				input_emb.close();
			} catch (Exception e) {

			}
			
		}
		return dim;
	}

}
