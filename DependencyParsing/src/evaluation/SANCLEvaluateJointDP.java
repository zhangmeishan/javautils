package evaluation;

import java.io.*;
import java.util.*;


import mason.dep.DepInstance;
import mason.dep.SDPCorpusReader;


public class SANCLEvaluateJointDP {

	public static void main(String[] args) throws Exception {
		
		boolean bZhangYue = true; 
		String sLine = "";
		Set<String> IVWords = new TreeSet<String>();
		if(args.length > 2)
		{
			BufferedReader corpusReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(args[2]), "UTF-8"));
			while ((sLine = corpusReader.readLine()) != null) {
				if (sLine.trim().equals(""))
					continue;
				String[] smallunits = sLine.trim().split("\\s+");
				String theWord = smallunits[0];
				IVWords.add(theWord);
			}
			corpusReader.close();
		}
		
		Set<String> EmbWords = new TreeSet<String>();
		if(args.length > 3)
		{
			BufferedReader embReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(args[3]), "UTF-8"));
			while ((sLine = embReader.readLine()) != null) {
				if (sLine.trim().equals(""))
					continue;
				String[] smallunits = sLine.trim().split("\\s+");
				String theWord = smallunits[0];
				String newWord = normalize_to_lowerwithdigit(theWord);
				EmbWords.add(newWord);
			}
			embReader.close();
		}
		
		SDPCorpusReader sdpCorpusReader1 = new SDPCorpusReader(bZhangYue);
		sdpCorpusReader1.Init(args[0]);
		SDPCorpusReader sdpCorpusReader2 = new SDPCorpusReader(bZhangYue);
		sdpCorpusReader2.Init(args[1]);	

		PrintWriter writer  = new PrintWriter(System.out);
		//int iType = Integer.parseInt(args[2]);
		//if(iType == 0)
		//{
			EvaluatePosTagger(sdpCorpusReader1.m_vecInstances,sdpCorpusReader2.m_vecInstances, writer);
			evaluateDep(sdpCorpusReader1.m_vecInstances,sdpCorpusReader2.m_vecInstances, writer, IVWords, EmbWords);
		//}
		//else if(iType == 1)
		//{
		//	EvaluatePosTagger(sdpCorpusReader1.m_vecInstances,sdpCorpusReader2.m_vecInstances, outputFilePos);
		//}
		//else if(iType == 2)
		//{
		//	evaluateDep(sdpCorpusReader1.m_vecInstances,sdpCorpusReader2.m_vecInstances, outputFileDep);
		//}
		writer.close();
	}
	
	public static void EvaluatePosTagger(List<DepInstance> vecInstances1, List<DepInstance> vecInstances2, PrintWriter writer) throws Exception
	{
		int totalWords = 0; int totalCorrectWords = 0; 
		int corrsent = 0;
		int numsent = vecInstances1.size();
		
		if(numsent != vecInstances2.size()) 
		{
			writer.println("Sentence Num do not match.");
			writer.close();
			return;
		}
		
		for(int i = 0; i < numsent; i++)
		{
			DepInstance inst1 = vecInstances1.get(i);
			DepInstance inst2 = vecInstances2.get(i);
			int wordsnum = inst1.forms.size();
			if(inst2.forms.size() != wordsnum)
			{
				writer.println(String.format("Sentence %d is not matched.", i+1));
				writer.close();
				return;
			}
			totalWords += wordsnum;
			int curCorrectNum = 0;
			for(int j = 0; j < wordsnum; j++)
			{
				//if(inst1.postags.get(j).equals(anObject))
				if(inst1.cpostags.get(j).equals(inst2.cpostags.get(j)))
				{
					curCorrectNum++;
				}
			}
			totalCorrectWords += curCorrectNum;
			if(curCorrectNum == wordsnum)corrsent++;
			/*
			if ((i + 1) % 1000 == 0) {
				writer.println(String.format(
						"Pos Accuracy: \t\t%d/%d=%f",
						totalCorrectWords, totalWords,
						totalCorrectWords * 100.0
								/ totalWords));
				writer.println(String.format(
						"All sentence accuracy: \t\t%d/%d=%f",
						corrsent, (i+1),
						corrsent * 100.0
								/ (i+1)));
			}*/
		}
		
		{
			writer.println(String.format(
					"Pos Accuracy: \t\t%d/%d=%f",
					totalCorrectWords, totalWords,
					totalCorrectWords * 100.0
							/ totalWords));
			writer.println(String.format(
					"All sentence accuracy: \t\t%d/%d=%f",
					corrsent, numsent,
					corrsent * 100.0
							/ numsent));
		}
		
		
	}
	
	public static void evaluateDep(List<DepInstance> vecInstances1, List<DepInstance> vecInstances2, PrintWriter output, Set<String> IVWords, Set<String> EmbWords) throws Exception {

		int totalInstances = vecInstances1.size();
		if(totalInstances != vecInstances2.size()) 
		{
			output.println("Sentence Num do not match.");
			output.close();
			return;
		}

		int sent_num_word_all_nopunc_dep_correct_total = 0;
		int sent_num_root_correct_total = 0;
		int word_num_nopunc_total = 0;
		int word_num_nopunc_dep_correct_total = 0;

		int sent_num_word_all_nopunc_deplabel_correct_total = 0;
		int word_num_nopunc_deplabel_correct_total = 0;

		int sent_num_word_all_nopunc_dep_correct_cur = 0;
		int sent_num_root_correct_cur = 0;
		int word_num_nopunc_cur = 0;
		int word_num_nopunc_dep_correct_cur = 0;

		int sent_num_word_all_nopunc_deplabel_correct_cur = 0;
		int word_num_nopunc_deplabel_correct_cur = 0;
		
		int oov_total = 0;
		int oov_uas_correct = 0;
		int oov_las_correct = 0;
		
		int ooe_total = 0;
		int ooe_uas_correct = 0;
		int ooe_las_correct = 0;
		int ooe_iv = 0;
		
		List<Integer> uas_results = new ArrayList<Integer>();
		List<Integer> las_results = new ArrayList<Integer>();
		int i = 0;
		for (; i < totalInstances; i++) {
			DepInstance tmpInstance = vecInstances1.get(i);
			DepInstance other = vecInstances2.get(i);

			if(!tmpInstance.evaluateWithOtherSANCL(other, uas_results, las_results))
			{
				output.println(String.format("Sentence %d is not matched.", i+1));
				output.close();
				return;
			}

			word_num_nopunc_cur = tmpInstance.eval_res[0];
			word_num_nopunc_dep_correct_cur = tmpInstance.eval_res[1];
			word_num_nopunc_deplabel_correct_cur = tmpInstance.eval_res[2];
			sent_num_word_all_nopunc_dep_correct_cur = tmpInstance.eval_res[3];
			sent_num_word_all_nopunc_deplabel_correct_cur = tmpInstance.eval_res[4];
			sent_num_root_correct_cur = tmpInstance.eval_res[5];

			word_num_nopunc_total += word_num_nopunc_cur;
			word_num_nopunc_dep_correct_total += word_num_nopunc_dep_correct_cur;
			word_num_nopunc_deplabel_correct_total += word_num_nopunc_deplabel_correct_cur;
			sent_num_word_all_nopunc_dep_correct_total += sent_num_word_all_nopunc_dep_correct_cur;
			sent_num_word_all_nopunc_deplabel_correct_total += sent_num_word_all_nopunc_deplabel_correct_cur;
			sent_num_root_correct_total += sent_num_root_correct_cur;
			
			for(int idx = 0; idx < tmpInstance.forms.size(); idx++)
			{
				String curWord = tmpInstance.forms.get(idx);
				if(!IVWords.contains(curWord))
				{
					if(uas_results.get(idx) >= 0)
					{
						assert(las_results.get(idx) >= 0);
						oov_total++;
						if(uas_results.get(idx) == 1) oov_uas_correct++;
						if(las_results.get(idx) == 1) oov_las_correct++;
					}
				}
				
				String newWord = normalize_to_lowerwithdigit(curWord);
				if(!EmbWords.contains(newWord))
				{
					if(uas_results.get(idx) >= 0)
					{
						assert(las_results.get(idx) >= 0);
						ooe_total++;
						if(uas_results.get(idx) == 1) ooe_uas_correct++;
						if(las_results.get(idx) == 1) ooe_las_correct++;
						if(IVWords.contains(curWord)) ooe_iv++;
					}
				}
			}
			
			/*
			if ((i + 1) % 1000 == 0) {
				output.println(String.format(
						"CM (excluding punc): \t\t%d/%d=%f",
						sent_num_word_all_nopunc_dep_correct_total, i + 1,
						sent_num_word_all_nopunc_dep_correct_total * 100.0
								/ (i + 1)));
				//output.println(String.format(
				//		"CM_L (excluding punc): \t\t%d/%d=%f",
				//		sent_num_word_all_nopunc_deplabel_correct_total, i + 1,
				//		sent_num_word_all_nopunc_deplabel_correct_total * 100.0
				//				/ (i + 1)));
				output.println(String.format(
						"UAS (excluding punc): \t\t%d/%d=%f",
						word_num_nopunc_dep_correct_total,
						word_num_nopunc_total,
						word_num_nopunc_dep_correct_total * 100.0
								/ word_num_nopunc_total));
				//output.println(String.format(
				//		"LAS (excluding punc): \t\t%d/%d=%f",
				//		word_num_nopunc_deplabel_correct_total,
				//		word_num_nopunc_total,
				//		word_num_nopunc_deplabel_correct_total * 100.0
				//				/ word_num_nopunc_total));
				output.println(String.format(
						"ROOT (excluding punc): \t\t%d/%d=%f",
						sent_num_root_correct_total, i + 1,
						sent_num_root_correct_total * 100.0 / (i + 1)));
				output.println();
			}*/
		}

		{
			//output.println(String.format("CM (excluding punc): \t\t%d/%d=%f",
			//		sent_num_word_all_nopunc_dep_correct_total, i,
			//		sent_num_word_all_nopunc_dep_correct_total * 100.0 / (i)));
			//output.println(String.format("CM_L (excluding punc): \t\t%d/%d=%f",
			//		sent_num_word_all_nopunc_deplabel_correct_total, i,
			//		sent_num_word_all_nopunc_deplabel_correct_total * 100.0
			//				/ (i)));
			output.println(String.format("UAS (excluding punc): \t\t%d/%d=%f",
					word_num_nopunc_dep_correct_total, word_num_nopunc_total,
					word_num_nopunc_dep_correct_total * 100.0
							/ word_num_nopunc_total));
			output.println(String.format("LAS (excluding punc): \t\t%d/%d=%f",
					word_num_nopunc_deplabel_correct_total,
					word_num_nopunc_total,
					word_num_nopunc_deplabel_correct_total * 100.0
							/ word_num_nopunc_total));
			//output.println(String.format("ROOT (excluding punc): \t\t%d/%d=%f",
			//		sent_num_root_correct_total, i, sent_num_root_correct_total
			//				* 100.0 / (i)));
			output.println(String.format("OOV UAS (excluding punc): \t\t%d/%d=%f",
					oov_uas_correct, oov_total, oov_uas_correct
							* 100.0 / oov_total));
			output.println(String.format("OOV LAS (excluding punc): \t\t%d/%d=%f",
					oov_las_correct, oov_total, oov_las_correct
							* 100.0 / oov_total));
			output.println(String.format("OOV Per (excluding punc): \t\t%d/%d=%f",
					oov_total, word_num_nopunc_total, oov_total
							* 100.0 / word_num_nopunc_total));
			output.println(String.format("OOE UAS (excluding punc): \t\t%d/%d=%f",
					ooe_uas_correct, ooe_total, ooe_uas_correct
							* 100.0 / ooe_total));
			output.println(String.format("OOE LAS (excluding punc): \t\t%d/%d=%f",
					ooe_las_correct, ooe_total, ooe_las_correct
							* 100.0 / ooe_total));
			output.println(String.format("OOE Per (excluding punc): \t\t%d/%d=%f",
					ooe_total, word_num_nopunc_total, ooe_total
							* 100.0 / word_num_nopunc_total));
			output.println(String.format("OOEIV Per (excluding punc): \t\t%d/%d=%f",
					ooe_iv, ooe_total, ooe_iv
							* 100.0 / ooe_total));
		}


	}
	
	public static String normalize_to_lowerwithdigit(String s) {
		String lowcase = "";
		for (int i = 0; i < s.length(); i++) {
			char curChar = s.charAt(i);
			String curTmpChar = s.substring(i, i + 1);
			if (Character.isDigit(curChar)) {
				lowcase = lowcase + "0";
			} else if (Character.isLetter(curChar)) {
				lowcase = lowcase + curTmpChar.toLowerCase();
			} else {
				lowcase = lowcase + curTmpChar;
			}
		}
		return lowcase;
	}
}
