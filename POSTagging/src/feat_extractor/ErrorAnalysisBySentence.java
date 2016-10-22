package feat_extractor;

import java.io.*;
import java.util.*;

public class ErrorAnalysisBySentence {

	/**
	 * @param args
	 */
	private static class Sentence {
		String[] words;
		String[] postags;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		List<Sentence> goldSentences = new ArrayList<Sentence>();
		readCorpus(args[0], goldSentences);
		List<Sentence> pred1Sentences = new ArrayList<Sentence>();
		readCorpus(args[1], pred1Sentences);
		List<Sentence> pred2Sentences = new ArrayList<Sentence>();
		readCorpus(args[2], pred2Sentences);

		PrintWriter output = new PrintWriter(System.out);

		if (args.length > 3) {
			output = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(args[3]), "UTF-8"), false);
		}

		//Random rand = new Random(0);
		int positive = 0, negative = 0;
		output.println("discrete	neural	label");
		for (int idx = 0; idx < goldSentences.size(); idx++) {
			//&& rand.nextDouble() < 0.3
			if(goldSentences.get(idx).words.length >= 2 ){
			  int result = Evaluate(output, goldSentences.get(idx), pred1Sentences.get(idx),
					pred2Sentences.get(idx));
			  if(result > 0) positive++;
			  if(result < 0) negative++;
			}
		}
		output.close();
		System.out.println(positive);
		System.out.println(negative);
	}

	public static void readCorpus(String inputFile, List<Sentence> sentences)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inputFile), "UTF-8"));
		String sLine = null;
		List<String> words = new ArrayList<String>();
		List<String> postags = new ArrayList<String>();
		while ((sLine = reader.readLine()) != null) {
			if (sLine.trim().equals("")) {
				if (words.size() > 0) {
					Sentence sentence = new Sentence();

					int wordNum = words.size();
					sentence.words = new String[wordNum];
					sentence.postags = new String[wordNum];
					for (int idx = 0; idx < wordNum; idx++) {
						sentence.words[idx] = words.get(idx);
						sentence.postags[idx] = postags.get(idx);
					}
					sentences.add(sentence);
				}
				words = new ArrayList<String>();
				postags = new ArrayList<String>();
			} else {
				String[] wordElems = sLine.trim().split("\\s+");
				words.add(wordElems[0]);
				postags.add(wordElems[wordElems.length-1]);
			}

		}
			
		reader.close();
	}

	public static int Evaluate(PrintWriter writer, Sentence goldSentences,
			Sentence pred1Sentences, Sentence pred2Sentences) throws Exception {
		int totalWords = goldSentences.words.length;
		int pred1CorrectWords = 0;
		int pred2CorrectWords = 0;
		
		for(int idx = 0; idx < totalWords; idx++){
			if(pred1Sentences.postags[idx].equalsIgnoreCase(goldSentences.postags[idx])){
				pred1CorrectWords++;
			}
			if(pred2Sentences.postags[idx].equalsIgnoreCase(goldSentences.postags[idx])){
				pred2CorrectWords++;
			}
		}


		double acc1 = pred1CorrectWords * 1.0 / totalWords;
		double acc2 = pred2CorrectWords * 1.0 / totalWords;
		writer.println(String.format("%.4f\t%.4f\ta", acc1, acc2));
		
		if(acc1 > acc2){
			return 1;
		}
		else if(acc1 < acc2){
			return -1;
		}
		else{
			return 0;
		}

	}

}
