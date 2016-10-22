package corpus;

import java.io.*;
import java.util.*;

public class CAlignedSentencePair {
	public List<String> m_src_sent;
	public List<String> m_tgt_sent;
	public List<Integer> m_alignmap_pos;
	public List<Integer> m_alignmap_target;
	public List<List<Integer>> m_alignmap_source;
	
	public CAlignedSentencePair()
	{
		m_src_sent = new ArrayList<String>();
		m_tgt_sent = new ArrayList<String>();
		m_alignmap_pos = new ArrayList<Integer>();
		m_alignmap_target = new ArrayList<Integer>();
		m_alignmap_source = new ArrayList<List<Integer>>();
	}
	public void clear()
	{
		m_src_sent.clear();
		m_tgt_sent.clear();
		m_alignmap_pos.clear();
		m_alignmap_target.clear();
		m_alignmap_source.clear();
	}
	
	
	public 	void ComputeAlignMap()
	{
		m_alignmap_source.clear();

		for(int i = 0; i < m_alignmap_pos.size(); i++)
		{
			List<Integer> onemap = new ArrayList<Integer>();
			for(int j = 0; j < m_alignmap_target.size(); j++)
			{
				if(m_alignmap_target.get(j) == i)
				{
					onemap.add(j);
				}
			}
			m_alignmap_source.add(onemap);
		}
	}
	
	public static CAlignedSentencePair readFromFile(BufferedReader in) throws Exception
	{
		String sLine_source = in.readLine();
		String sLine_source_pos = in.readLine();
		String sLine_target = in.readLine();
		String sLine_target_align = in.readLine();
		
		if(sLine_source == null || sLine_source_pos == null 
		|| sLine_target == null || sLine_target_align == null 
				)
		{
			return null;
		}
		
		String[] source_words = sLine_source.trim().split("\\s+");
		String[] source_pos_strs = sLine_source_pos.trim().split("\\s+");
		String[] target_words = sLine_target.trim().split("\\s+");
		String[] target_word_aligns = sLine_target_align.trim().split("\\s+");
		
		if(target_words.length != target_word_aligns.length
				)
		{
			System.out.println("error input....");
			return null;
		}
		
		CAlignedSentencePair obj = new CAlignedSentencePair();
		
		for(int idx = 0; idx < source_words.length; idx++)obj.m_src_sent.add(source_words[idx]);
		for(int idx = 0; idx < source_pos_strs.length; idx++)obj.m_alignmap_pos.add(Integer.parseInt(source_pos_strs[idx]));
		for(int idx = 0; idx < target_words.length; idx++)obj.m_tgt_sent.add(target_words[idx]);
		for(int idx = 0; idx < target_word_aligns.length; idx++)obj.m_alignmap_target.add(Integer.parseInt(target_word_aligns[idx]));
		
		obj.ComputeAlignMap();
		
		return obj;
	}
}
