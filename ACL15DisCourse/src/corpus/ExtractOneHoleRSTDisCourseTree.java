package corpus;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import mason.utils.MapSort;

import edu.berkeley.nlp.syntax.Tree;

public class ExtractOneHoleRSTDisCourseTree {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		

		
		Map<String, Integer> knownWords = new HashMap<String, Integer>();
		

		
		RSTDisCourseTree disTree = new RSTDisCourseTree();
		while(disTree.LoadInstanceFromSingleFile(reader, false, false))
		{
			for(int idx=0; idx < disTree.m_lstAllWords.size(); idx++)
			{
				String theWordPos = disTree.m_lstAllWords.get(idx);
				int index = theWordPos.indexOf("_");
				if(index > 0)
				{
					String theWord = theWordPos.substring(0, index);
					if(!knownWords.containsKey(theWord))
					{
						knownWords.put(theWord, 0);
					}
					knownWords.put(theWord, knownWords.get(theWord)+1);
				}
			}
		}		
		reader.close();
		
		int cutFreq = 0;
		if(args.length > 2)
		{
			cutFreq = Integer.parseInt(args[2]);
		}
		List<Entry<String, Integer>> relationstats = MapSort.MapIntegerSort(knownWords);
		
		List<String> knownWordIds = new ArrayList<String>();
		for(Entry<String, Integer> keyValuePair : relationstats)
		{
			if(keyValuePair.getValue() > cutFreq)
			{
				knownWordIds.add(keyValuePair.getKey());
			}
		}

		knownWordIds.add(0, "-NULL-");
		knownWordIds.add(0, "-UNKNOWN-");
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		int embedding = knownWordIds.size();
		writer.println(String.format("embedding %d", embedding));
		
		for(int idx = 0; idx < embedding; idx++)
		{
			String outline = knownWordIds.get(idx);
			
			for(int idy = 0; idy < embedding; idy++)
			{
				if(idx == idy)
				{
					outline = outline + " 1";
				}
				else
				{
					outline = outline + " 0";
				}
			}
			writer.println(outline.trim());
		}
		writer.close();
		
	}
}
