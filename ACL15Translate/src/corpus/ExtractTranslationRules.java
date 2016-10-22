package corpus;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import mason.utils.MapSort;


public class ExtractTranslationRules {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Map<String, Map<String, Double>> source2target = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Double>> target2source = new HashMap<String, Map<String, Double>>();
		Map<String, Double> sourceFreqs = new HashMap<String, Double>();
		Map<String, Double> targetFreqs = new HashMap<String, Double>();
		Double overallFreq = 0.0;
		
		
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		CAlignedSentencePair sentpair = null;
		System.out.println("Stage 1: Collection rules for sentences");
		int sentCount = 0;
		while( (sentpair = CAlignedSentencePair.readFromFile(in)) != null)
		{
			sentCount++;
			
			for(int idx = 0; idx < sentpair.m_alignmap_source.size()-1;idx++)
			{
				int start = sentpair.m_alignmap_pos.get(idx);
				int end = sentpair.m_alignmap_pos.get(idx+1)-1;
				String sourcewords = sentpair.m_src_sent.get(start);
				for(int idy = start+1; idy <= end; idy++)
				{
					sourcewords = sourcewords + " " + sentpair.m_src_sent.get(idy);
				}
				
				String targetwords = "";
				if(sentpair.m_alignmap_source.get(idx).size() > 0)
				{
					targetwords =  sentpair.m_tgt_sent.get(sentpair.m_alignmap_source.get(idx).get(0));
					for(int idy = 1; idy < sentpair.m_alignmap_source.get(idx).size(); idy++)
					{
						targetwords = targetwords + " " + sentpair.m_tgt_sent.get(sentpair.m_alignmap_source.get(idx).get(idy));
					}
				}
				
				AddMapFreq(sourcewords, targetwords, source2target, 1.0);
				AddMapFreq(sourcewords, sourceFreqs, 1.0);
				
				AddMapFreq(targetwords, sourcewords, target2source, 1.0);
				AddMapFreq(targetwords, targetFreqs, 1.0);
				
				overallFreq = overallFreq + 1.0;
				
			}
			
			
			for(int idx = 0; idx < sentpair.m_alignmap_target.size(); idx++)
			{
				if(sentpair.m_alignmap_target.get(idx) == -1)
				{
					String sourcewords = "";
					String targetwords = sentpair.m_tgt_sent.get(idx);
					AddMapFreq(sourcewords, targetwords, source2target, 1.0);
					AddMapFreq(sourcewords, sourceFreqs, 1.0);
					
					AddMapFreq(targetwords, sourcewords, target2source, 1.0);
					AddMapFreq(targetwords, targetFreqs, 1.0);
					
					overallFreq = overallFreq + 1.0;
				}
			}
			
			if(sentCount%1000 == 0){
				System.out.print(sentCount); System.out.print(" ");
				if(sentCount%20000 == 0){System.out.println();}
				System.out.flush();
			}
			
		}
		System.out.println(sentCount);		
		in.close();
		
		System.out.println("Stage 2: Compute rule probabilities");
		
		TranslationRulesGeneration trg = new TranslationRulesGeneration();
		int maxitem = 0;
		int allitem = 0;
		
		int totalitems = source2target.size();
		int onebase = totalitems/100;
		int processeditem = 0;
		Map<String, Integer> sourceCandidates = new HashMap<String, Integer>();
		for(String sourcestr : source2target.keySet())
		{
			processeditem++;
			if(source2target.get(sourcestr).size() > maxitem)
			{
				maxitem = source2target.get(sourcestr).size();
			}
			sourceCandidates.put(sourcestr, source2target.get(sourcestr).size());
			allitem = allitem + source2target.get(sourcestr).size();
			for(String targetstr : source2target.get(sourcestr).keySet())
			{
								
				String[] targetwords = targetstr.split("\\s+");
				float weight1 = (float)Math.log(source2target.get(sourcestr).get(targetstr)/sourceFreqs.get(sourcestr));
				float weight2 = (float)Math.log(target2source.get(targetstr).get(sourcestr)/targetFreqs.get(targetstr));
				float weight3 = (float)Math.log(source2target.get(sourcestr).get(targetstr)/overallFreq);
				
				String[] sourcewords = sourcestr.trim().split("\\s+");
				int ruleType = 0;
				if(sourcestr.isEmpty() && targetstr.isEmpty())
				{
					System.out.println("error rule");
				}
				if(sourcestr.isEmpty())
				{
					ruleType = 1;
					sourcewords = null;
				}
				else if(targetstr.isEmpty())
				{
					ruleType = -1;
					targetwords = null;
				}
				else
				{
					ruleType = 0;
				}
				
				String source = TranslationRulesGeneration.theSaveWordString(sourcewords);
				trg.AddOneRule(source, targetwords, weight1, weight2, weight3, ruleType);
				
			}
			
			if(processeditem%onebase == 0){
				System.out.print(processeditem/onebase); System.out.print(" ");
				if(sentCount%(20*onebase) == 0){System.out.println();}
				System.out.flush();
			}
		}
		
		double averageitem = allitem*1.0/source2target.size();
		System.out.println(String.format("maxitem: %d, avgitem: %f",  maxitem, averageitem));
		
		List<Entry<String, Integer>> sortedCandidates = MapSort.MapIntegerSort(sourceCandidates);
		
		PrintWriter writer_log = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		
		for(int idx = 0; idx < sortedCandidates.size(); idx++)
		{
			writer_log.println(String.format("[ %s ]\t[%d]", sortedCandidates.get(idx).getKey(), sortedCandidates.get(idx).getValue()));
		}
		
		
		writer_log.close();
	
		
		System.out.println("Stage 3: Writing");
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		trg.println(writer);		
		writer.close();
		
		
	}
	
	
	
	public static void AddMapFreq(String key1, String key2, Map<String, Map<String, Double>> hashmap, double value)
	{
		if(!hashmap.containsKey(key1))
		{
			hashmap.put(key1, new HashMap<String, Double>());
		}
		
		if(!hashmap.get(key1).containsKey(key2))
		{
			hashmap.get(key1).put(key2, 0.0);
		}
		
		hashmap.get(key1).put(key2, hashmap.get(key1).get(key2) + value);
	}
	
	
	public static void AddMapFreq(String key, Map<String, Double> hashmap, double value)
	{
		if(!hashmap.containsKey(key))
		{
			hashmap.put(key, 0.0);
		}
		
		hashmap.put(key, hashmap.get(key) + value);
	}

}
