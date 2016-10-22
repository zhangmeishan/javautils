package corpus;

import java.io.*;
import java.util.*;

public class ErrorAnalysisByWordLength {

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

		output.println("word	character	label");
		int maxcharlength = 16;
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

		for (int idx = 0; idx < goldSentences.size(); idx++) {
			Set<String> gwordBounds = new HashSet<String>();
			EvaluationSegmentation.getBoundary(goldSentences.get(idx).words, gwordBounds);

			Set<String> p1wordBounds = new HashSet<String>();
			EvaluationSegmentation.getBoundary(pred1Sentences.get(idx).words, p1wordBounds);

			Set<String> p2wordBounds = new HashSet<String>();
			EvaluationSegmentation.getBoundary(pred2Sentences.get(idx).words, p2wordBounds);
			
			for (String gWord : gwordBounds) {
				int curWordLenth = getWordLength(gWord);
				if(curWordLenth > maxcharlength) curWordLenth = maxcharlength;
				if (p1wordBounds.contains(gWord)) {
					totalReco1Words[curWordLenth-1]++;
				}
				if (p2wordBounds.contains(gWord)) {
					totalReco2Words[curWordLenth-1]++;
				}
				totalGoldWords[curWordLenth-1]++;
			}

			for (String pWord : p1wordBounds) {
				int curWordLenth = getWordLength(pWord);
				if(curWordLenth > maxcharlength) curWordLenth = maxcharlength;
				totalPred1Words[curWordLenth-1]++;
			}
			
			for (String pWord : p2wordBounds) {
				int curWordLenth = getWordLength(pWord);
				if(curWordLenth > maxcharlength) curWordLenth = maxcharlength;
				totalPred2Words[curWordLenth-1]++;
			}
		}
		
		for(int idx = 0; idx < maxcharlength; idx++){
			double acc1 = totalReco1Words[idx] * 2.0
					/ (totalGoldWords[idx] + totalPred1Words[idx]);
			double acc2 = totalReco2Words[idx] * 2.0
					/ (totalGoldWords[idx] + totalPred2Words[idx]);
			
			output.println(String.format("%.4f\t%.4f\ta", acc1, acc2));
		}
		output.close();
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

	public static int getWordLength(String wordBound) throws Exception {
		int endMark = wordBound.indexOf("]");
		String theWord = wordBound.substring(endMark+1);
		
		return theWord.length();
	}

}
