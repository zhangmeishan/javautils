package corpus;

import java.io.*;
import java.util.*;

public class RemoveTargetUnalignments {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		//List<CAlignedSentencePair> sentences= new ArrayList<CAlignedSentencePair>();
		Map<String, Double> wordPreferences = new HashMap<String, Double>();
		Map<String, Double> wordFrequence = new HashMap<String, Double>();
		CAlignedSentencePair sentpair = null;
		
		while( (sentpair = CAlignedSentencePair.readFromFile(in)) != null)
		{			
			for(int idx = 0; idx < sentpair.m_tgt_sent.size(); idx++)
			{
				String leftWord = idx>0? sentpair.m_tgt_sent.get(idx-1) : "<START>";
				String rightWord = idx<sentpair.m_tgt_sent.size()-1? sentpair.m_tgt_sent.get(idx+1) : "<END>";
				int leftalign = idx>0? sentpair.m_alignmap_target.get(idx-1) : -1;
				int rightalign = idx<sentpair.m_tgt_sent.size()-1? sentpair.m_alignmap_target.get(idx+1) : -1;
				int curalign = sentpair.m_alignmap_target.get(idx);
				String curWord = sentpair.m_tgt_sent.get(idx);
				
				String leftKey = leftWord + " " + curWord;
				String rightKey = curWord + " " + rightWord;
				if(!wordPreferences.containsKey(leftKey))wordPreferences.put(leftKey, 0.0);
				if(!wordPreferences.containsKey(rightKey))wordPreferences.put(rightKey, 0.0);
				if(!wordFrequence.containsKey(curWord))wordFrequence.put(curWord, 0.0);
				wordFrequence.put(curWord, wordFrequence.get(curWord)+1.0);
				if(curalign == -1)
				{
					wordPreferences.put(leftKey, wordPreferences.get(leftKey)+0.5);
					wordPreferences.put(rightKey, wordPreferences.get(rightKey)+0.5);
				}
				else
				{
					if(curalign < 0)
					{
						System.out.println("error");
					}
					
					if(curalign == rightalign && curalign == leftalign)
					{
						wordPreferences.put(leftKey, wordPreferences.get(leftKey)+0.5);
						wordPreferences.put(rightKey, wordPreferences.get(rightKey)+0.5);
					}
					else if(curalign == rightalign)
					{
						wordPreferences.put(rightKey, wordPreferences.get(rightKey)+1.0);
					}
					else if(curalign == leftalign)
					{
						wordPreferences.put(leftKey, wordPreferences.get(leftKey)+1.0);
					}
				}
			}
		}
		
		in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));		
		
		while( (sentpair = CAlignedSentencePair.readFromFile(in)) != null)
		{			
			int idx = 0;
			while(idx < sentpair.m_tgt_sent.size())
			{
				
				int curalign = sentpair.m_alignmap_target.get(idx);	
				if(curalign >= 0)
				{
					idx++;
					continue;
				}
				String curWord = sentpair.m_tgt_sent.get(idx);
				String leftWord = idx>0? sentpair.m_tgt_sent.get(idx-1) : "<START>";
				int leftalign = idx>0? sentpair.m_alignmap_target.get(idx-1) : -2;
				String leftKey = leftWord + " " + curWord;
				int rightalign = -2;
				String rightWord = "<END>";
				String rightKey = sentpair.m_tgt_sent.get(sentpair.m_tgt_sent.size()-1) + " " + rightWord;
				int idy = idx+1;
				for(; idy < sentpair.m_tgt_sent.size(); idy++)
				{
					if(sentpair.m_alignmap_target.get(idy) != -1)
					{
						rightalign = sentpair.m_alignmap_target.get(idy);
						rightWord = sentpair.m_tgt_sent.get(idy);
						rightKey = sentpair.m_tgt_sent.get(idy-1) + " " + rightWord;
						break;
					}
				}

				int finalalign = curalign;
				if(rightalign == -2 && leftalign == -2) finalalign = 0;
				else if(rightalign == -2) finalalign = leftalign;
				else if(leftalign == -2) finalalign = rightalign;
				else if( (wordPreferences.get(leftKey)/wordFrequence.get(leftWord)) > (wordPreferences.get(rightKey)/wordFrequence.get(rightWord)))
				{					
					finalalign = leftalign;
				}
				else
				{
					finalalign = rightalign;
				}
				
				if(finalalign < 0)
				{
					System.out.println("error");
				}
				
				for(int idk = idx; idk < idy; idk++)
				{
					if(sentpair.m_alignmap_target.get(idk) != -1)
					{
						System.out.println("error");
					}
					sentpair.m_alignmap_target.set(idk, finalalign);
				}				
				idx = idy+1;				
			}
			
			String outline1 = "";
			for(idx = 0; idx < sentpair.m_src_sent.size(); idx++)
			{
				outline1 = outline1 + " " + sentpair.m_src_sent.get(idx);
			}
			
			String outline2 = "";
			for(idx = 0; idx < sentpair.m_alignmap_pos.size(); idx++)
			{
				outline2 = outline2 + " " + String.format("%d", sentpair.m_alignmap_pos.get(idx));
			}
			
			String outline3 = "";
			for(idx = 0; idx < sentpair.m_tgt_sent.size(); idx++)
			{
				outline3 = outline3 + " " + sentpair.m_tgt_sent.get(idx);
			}
			
			String outline4 = "";
			for(idx = 0; idx < sentpair.m_alignmap_target.size(); idx++)
			{
				outline4 = outline4 + " " + String.format("%d", sentpair.m_alignmap_target.get(idx));
			}
			
			writer.println(outline1.trim());
			writer.println(outline2.trim());
			writer.println(outline3.trim());
			writer.println(outline4.trim());
		}
		
		writer.close();

		
	}

}
