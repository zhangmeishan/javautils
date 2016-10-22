package corpus;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import mason.utils.MapSort;

import edu.berkeley.nlp.syntax.Tree;

public class DisCourseTreeBinStatNormalize {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
				
		boolean bCoarseGrained = false;
		if(args.length>2 && args[2].equalsIgnoreCase("c"))
		{
			bCoarseGrained = true;
		}
		Map<String, Integer> childRels = new HashMap<String, Integer>();
		DisCourseTree disTree = new DisCourseTree();
		int count = 0;
		while(disTree.LoadInstanceFromSingleFile(reader, false, null))
		{
			count++;
			if(disTree.m_btRootTree.bLeafNode || disTree.m_btRootTree.getAllTermminals().size() == 1)
			{
				continue;
			}
			disTree.m_btRootTree.BinarizeTree();
			
			if(bCoarseGrained)disTree.m_btRootTree.CoarseGrained();
			
			disTree.m_btRootTree.getAllChildrenRelations(childRels);
			//disTree.printNormal(writer);
			//writer.println();
		}		
		reader.close();
		System.out.println(count);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		List<Entry<String, Integer>> relationstats = MapSort.MapIntegerSort(childRels);
		
		for(int idx = 0; idx < relationstats.size(); idx++)
		{
			Entry<String, Integer> oneItem = relationstats.get(idx);			
			int choose = -1;
			int keylength = oneItem.getKey().length();
			String[] smallunits = oneItem.getKey().substring(1, keylength-1).split("\\s+");
			if(smallunits.length != 2)
			{
				System.out.println("stat error");
			}
			if(smallunits[0].equals("span#Nucleus"))choose = 1;
			else if(smallunits[1].equals("span#Nucleus"))choose = 0; 
			else if(smallunits[0].equals(smallunits[1]))choose = 0; 
			writer.println(String.format("%d\t%s\t%d\t%d", idx, oneItem.getKey(), oneItem.getValue(), choose));
		}
		writer.close();
	}
}
