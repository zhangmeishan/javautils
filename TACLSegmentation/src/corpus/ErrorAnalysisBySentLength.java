package corpus;

import java.io.*;
import java.util.*;

public class ErrorAnalysisBySentLength {

	/**
	 * @param args
	 */
	private static class Sentence {
		String[] words;
		String chars;
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

		// Random rand = new Random(0);
		int positive = 0, negative = 0;
		output.println("word	character	label");
		int maxcharlength = 10;
		int[] totalPred1Words = new int[maxcharlength];
		int[] totalPred2Words = new int[maxcharlength];
		int[] totalGoldWords = new int[maxcharlength];
		int[] totalReco1Words = new int[maxcharlength];
		int[] totalReco2Words = new int[maxcharlength];

		for (int idx = 0; idx < maxcharlength; idx++) {
			totalPred1Words[idx] = 0;
			totalPred2Words[idx] = 0;
			totalGoldWords[idx] = 0;
			totalReco1Words[idx] = 0;
			totalReco2Words[idx] = 0;
		}
		List<Integer> resultvalues = new ArrayList<Integer>();
		for (int idx = 0; idx < goldSentences.size(); idx++) {
			int result = Evaluate(goldSentences.get(idx),
					pred1Sentences.get(idx), pred2Sentences.get(idx),
					resultvalues);
			if (result > 0)
				positive++;
			if (result < 0)
				negative++;

			int index = goldSentences.get(idx).chars.length() / 5;
			if (index > 9)
				index = 9;

			totalPred1Words[index] += resultvalues.get(0);
			totalPred2Words[index] += resultvalues.get(1);
			totalGoldWords[index] += resultvalues.get(2);
			totalReco1Words[index] += resultvalues.get(3);
			totalReco2Words[index] += resultvalues.get(4);

		}
		
		for(int idx = 0; idx < maxcharlength; idx++){
			double acc1 = totalReco1Words[idx] * 2.0
					/ (totalGoldWords[idx] + totalPred1Words[idx]);
			double acc2 = totalReco2Words[idx] * 2.0
					/ (totalGoldWords[idx] + totalPred2Words[idx]);
			
			output.println(String.format("%.4f\t%.4f\ta", acc1, acc2));
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

			sentence.chars = "";
			for (int idx = 0; idx < wordNum; idx++) {
				String curWord = wordElems[idx].trim();
				sentence.words[idx] = curWord;
				sentence.chars = sentence.chars + curWord;
			}
			sentences.add(sentence);
		}

		reader.close();
	}

	public static int Evaluate(Sentence goldSentences,
			Sentence pred1Sentences, Sentence pred2Sentences,
			List<Integer> resultvalues) throws Exception {
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

		double acc1 = totalReco1Words * 2.0
				/ (totalGoldWords + totalPred1Words);
		double acc2 = totalReco2Words * 2.0
				/ (totalGoldWords + totalPred2Words);

		resultvalues.clear();
		resultvalues.add(totalPred1Words);
		resultvalues.add(totalPred2Words);
		resultvalues.add(totalGoldWords);
		resultvalues.add(totalReco1Words);
		resultvalues.add(totalReco2Words);

		if (acc1 > acc2) {
			return 1;
		} else if (acc1 < acc2) {
			return -1;
		} else {
			return 0;
		}

	}

}
