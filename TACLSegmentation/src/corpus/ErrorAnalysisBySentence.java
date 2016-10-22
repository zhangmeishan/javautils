package corpus;

import java.io.*;
import java.util.*;

public class ErrorAnalysisBySentence {

	/**
	 * @param args
	 */
	private static class Sentence {
		String[] words;
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

		Random rand = new Random(0);
		int positive = 0, negative = 0;
		output.println("word	character	label");
		for (int idx = 0; idx < goldSentences.size(); idx++) {
			//&& rand.nextDouble() < 0.3
			if(goldSentences.get(idx).words.length >= 5 ){
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
		while ((sLine = reader.readLine()) != null) {
			if (sLine.trim().equals(""))
				continue;
			String[] wordElems = sLine.trim().split("\\s+");
			Sentence sentence = new Sentence();
			int wordNum = wordElems.length;
			sentence.words = new String[wordNum];

			for (int idx = 0; idx < wordNum; idx++) {
				String curWord = wordElems[idx].trim();
				sentence.words[idx] = curWord;
			}

			sentences.add(sentence);
		}

		reader.close();
	}

	public static int Evaluate(PrintWriter writer, Sentence goldSentences,
			Sentence pred1Sentences, Sentence pred2Sentences) throws Exception {
		int totalPred1Words = pred1Sentences.words.length;
		int totalPred2Words = pred2Sentences.words.length;
		int totalReco1Words = 0;
		int totalReco2Words = 0;
		int totalGoldWords = goldSentences.words.length;

		Set<String> gwordBounds = new HashSet<String>();
		EvaluationSegmentation.getBoundary(goldSentences.words, gwordBounds);

		Set<String> p1wordBounds = new HashSet<String>();
		EvaluationSegmentation.getBoundary(pred1Sentences.words, p1wordBounds);

		Set<String> p2wordBounds = new HashSet<String>();
		EvaluationSegmentation.getBoundary(pred2Sentences.words, p2wordBounds);

		for (String gWord : gwordBounds) {
			if (p1wordBounds.contains(gWord)) {
				totalReco1Words++;
			}
			if (p2wordBounds.contains(gWord)) {
				totalReco2Words++;
			}
		}

		double acc1 = totalReco1Words * 2.0 / (totalGoldWords + totalPred1Words);
		double acc2 = totalReco2Words * 2.0 / (totalGoldWords + totalPred2Words);
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
