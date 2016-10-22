package corpus;

import java.io.*;
import java.util.*;

public class TranslationRulesGeneration {

	/**
	 * @param args
	 */
	Map<String, Integer> m_mapTranslateEnd = new HashMap<String, Integer>();
	Map<String, Integer> m_mapTranslateStart = new HashMap<String, Integer>();
	
	List<String> m_mapTranslateSources = new ArrayList<String>();
	List<String[]> m_mapTranslateRules = new ArrayList<String[]>();
	List<Float> m_mapTranslateRuleScore1s = new ArrayList<Float>();
	List<Float> m_mapTranslateRuleScore2s = new ArrayList<Float>();
	List<Float> m_mapTranslateRuleScore3s = new ArrayList<Float>();
	List<Integer> m_mapTranslateRuleTypes = new ArrayList<Integer>();
	int m_nTranslationRuleNum = 0;
	
	
	public void println(PrintWriter writer ) throws Exception
	{
	   writer.println("translation table rules" );
	   writer.println(m_nTranslationRuleNum);
	   int i = 0;
	   while(i < m_nTranslationRuleNum)
	   {
		   String sourcePhrase = m_mapTranslateSources.get(i);
		   int start_pos = m_mapTranslateStart.get(sourcePhrase);
		   int end_pos = m_mapTranslateEnd.get(sourcePhrase);
		   writer.println(sourcePhrase);
		   writer.println(end_pos - start_pos + 1);
		   for(int j = start_pos; j <= end_pos; j++)
		   {
			   writer.println(theSaveWordString(m_mapTranslateRules.get(j))); 
			   writer.println(String.format("%f\t%f\t%f", m_mapTranslateRuleScore1s.get(j), m_mapTranslateRuleScore2s.get(j), m_mapTranslateRuleScore3s.get(j)));
		   }
		   i = end_pos + 1;
	   }
	}
	
	
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//target()
		TranslationRulesGeneration trg = new TranslationRulesGeneration();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		String sLine = "";
		
		while( (sLine = in.readLine()) != null)
		{
			sLine = sLine.trim();
			int firstIndex = sLine.indexOf("<->");
			String theTargetStr = sLine.substring(0, firstIndex).trim();
			firstIndex = firstIndex + 3;
			int secondIndex = sLine.indexOf("<->", firstIndex);
			String theSourceStr = sLine.substring(firstIndex, secondIndex).trim();
			secondIndex = secondIndex +3;
			int thirdIndex = sLine.indexOf("<->", secondIndex);
			float weight1= Float.parseFloat(sLine.substring(secondIndex, thirdIndex).trim());
			thirdIndex = thirdIndex +3;
			int fourthIndex = sLine.indexOf("<-->", thirdIndex);
			float weight2= Float.parseFloat(sLine.substring(thirdIndex, fourthIndex).trim());
			float weight3= Float.parseFloat(sLine.substring(fourthIndex+4).trim());
			String[] sourcewords = theSourceStr.split("\\s+");
			String[] targetwords = theTargetStr.split("\\s+");
			if(theSourceStr.equals("_TRANS_SLF_"))
			{
				sourcewords = targetwords;
			}
			if(theSourceStr.equals("_INS_"))
			{
				sourcewords = null;
			}
			String source = theSaveWordString(sourcewords);
			
			int ruletype = 0;
			if(targetwords == null)ruletype = -1;
			if(sourcewords == null)
			{
				System.out.println("debug");
			}
			trg.AddOneRule(source, targetwords, weight1, weight2, weight3, ruletype);
		}
		
		trg.println(writer);
				
		in.close();
		writer.close();

	}
	
	
	public void AddOneRule(String source, String[] target,
			float weight1, float weight2, float weight3, int ruletype)
	{
		if(FindOneRule(source, target) != -1) return;
		String sourcestr;
		if(m_mapTranslateEnd.containsKey(source))
		{
			int end = m_mapTranslateEnd.get(source);

			m_mapTranslateSources.add(end+1, source);
			m_mapTranslateRules.add(end+1, target);
			m_mapTranslateRuleScore1s.add(end+1, weight1);
			m_mapTranslateRuleScore2s.add(end+1, weight2);
			m_mapTranslateRuleScore3s.add(end+1, weight3);
			m_mapTranslateRuleTypes.add(end+1, ruletype);
			m_nTranslationRuleNum++;

			m_mapTranslateEnd.put(source, m_mapTranslateEnd.get(source)+1);
			int i = end+2;
			assert(end+2 == m_mapTranslateEnd.get(source)+2);
			while(i<m_nTranslationRuleNum)
			{
				sourcestr = m_mapTranslateSources.get(i);
				m_mapTranslateEnd.put(sourcestr, m_mapTranslateEnd.get(sourcestr)+1);
				m_mapTranslateStart.put(sourcestr, m_mapTranslateStart.get(sourcestr)+1);
				i = m_mapTranslateEnd.get(sourcestr)+1;
			}
		}
		else
		{
			m_mapTranslateSources.add(source);
			m_mapTranslateRules.add(target);
			m_mapTranslateRuleScore1s.add(weight1);
			m_mapTranslateRuleScore2s.add(weight2);
			m_mapTranslateRuleScore3s.add(weight3);
			m_mapTranslateRuleTypes.add(ruletype);
			m_nTranslationRuleNum++;
			m_mapTranslateStart.put(source,m_nTranslationRuleNum-1);
			m_mapTranslateEnd.put(source,m_nTranslationRuleNum-1);
		}
	}

	public int FindOneRule(String source, String[] target)
	{
		if(m_mapTranslateEnd.containsKey(source))
		{
			for(int i = m_mapTranslateStart.get(source); i<=m_mapTranslateEnd.get(source); i++)
			{
				//boolean bIdendifal = true;
				String currentstr = theSaveWordString(m_mapTranslateRules.get(i));
				String comparestr = theSaveWordString(target);
				boolean bIdendifal = currentstr.equals(comparestr);
				/*if(m_mapTranslateRules.get(i).length != target.length) bIdendifal = false;
				else
				{
					for(int j = 0; j < target.length; j++)
					{
						if((m_mapTranslateRules.get(i))[j] != target[j])
						{
							bIdendifal = false;
							break;
						}
					}
				}*/
				if(bIdendifal) return i;
			}
		}

		return -1;
	}

	public static String theSaveWordString(String[] theWords)
	{
		if(theWords == null) return "[  ]";
		String theline = "[ ";
		for(int idx = 0; idx < theWords.length; idx++)
		{
			theline = theline + "[" + theWords[idx] + "]";
			if (idx!=theWords.length-1) theline = theline + " , ";
		}
		theline = theline + " ]";
		
		return theline;
	}

}
