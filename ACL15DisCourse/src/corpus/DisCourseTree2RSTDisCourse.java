package corpus;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import mason.utils.MapSort;

import edu.berkeley.nlp.syntax.Tree;

public class DisCourseTree2RSTDisCourse {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		boolean bCoarseGrained = false;
		if(args.length>3 && args[3].equalsIgnoreCase("c"))
		{
			bCoarseGrained = true;
		}
		Map<String, Integer> statRels = new HashMap<String, Integer>();
		
		DisCourseTree disTree = new DisCourseTree();
		while(disTree.LoadInstanceFromSingleFile(reader, false, null))
		{
			disTree.m_btRootTree.RemoveUnaryTree();
			disTree.m_btRootTree.BinarizeTree();
			
			if(bCoarseGrained)disTree.m_btRootTree.CoarseGrained();
			
			RSTDisCourseTree rstDisTree = new RSTDisCourseTree();
			rstDisTree.clear();
						
			for(String curWordPOS : disTree.m_lstAllWords)
			{
				rstDisTree.m_lstAllWords.add(curWordPOS);
			}
			
			for(WordPosition curPosi : disTree.m_lstAllWordPositions)
			{
				rstDisTree.m_lstAllWordPositions.add(curPosi);
			}
			
			for(List<String> wordposs : disTree.m_sentWords)
			{
				List<String> newWordPoss = new ArrayList<String>();
				for(String curWordPOS : wordposs)
				{
					newWordPoss.add(curWordPOS);
				}
				rstDisTree.m_sentWords.add(newWordPoss);
			}
			
			for(Tree<String> cfgTree : disTree.m_lstCfgTrees)
			{
				rstDisTree.m_lstCfgTrees.add(cfgTree);
			}
			
			rstDisTree.m_btRootTree = disTree.m_btRootTree.convert2RSTTree();
			
			rstDisTree.m_btRootTree.getAllRelations(statRels);
			
			rstDisTree.printNormal(writer);
			//writer.println();
		}		
		reader.close();
		writer.close();
		
		
		writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		
		writer.println("Print sent match statistics:");
		List<Entry<String, Integer>> relationstats = MapSort.MapIntegerSort(statRels);
		
		for(int idx = 0; idx < relationstats.size(); idx++)
		{
			Entry<String, Integer> oneItem = relationstats.get(idx);
			writer.println(String.format("%d\t%s\t%d", idx, oneItem.getKey(), oneItem.getValue()));
		}
		
		writer.close();
		
	}
}
