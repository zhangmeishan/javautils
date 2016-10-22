package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

import mason.utils.MapSort;

public class DisCourseTreeProcessing {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		boolean bZparDisFormat = false;
		if(args.length>3 && args[3].equalsIgnoreCase("zpar"))
		{
			bZparDisFormat = true;
		}
		
		Map<String, Integer> sentenceSpanType = new HashMap<String, Integer>();
		
		sentenceSpanType.put("LeftMatch", 0);
		sentenceSpanType.put("RightMatch", 0);
		sentenceSpanType.put("PerfectMatch", 0);
		sentenceSpanType.put("NotMatch", 0);
		
		sentenceSpanType.put("Cross", 0);
		sentenceSpanType.put("NoCross", 0);
		DisCourseTree disTree = new DisCourseTree();
		
		while(disTree.LoadInstanceFromSingleFile(reader, bZparDisFormat, null))
		{
			int startWordId = 0;
			int endWordId = 0;
			
			for(int idx = 0; idx < disTree.m_sentWords.size(); idx++)
			{
				endWordId = startWordId + disTree.m_sentWords.get(idx).size() -1;
				
				BinaryTree spanTree = disTree.m_btRootTree.getMinSpanTree(startWordId, endWordId);
				
				boolean bIntersect = false;
				for(BinaryTree child : spanTree.children)
				{
					if(child.m_nStartWordPosition < startWordId 
					&& child.m_nEndWordPosition >= startWordId 
					&& child.m_nEndWordPosition < endWordId )
					{
						bIntersect = true;
						break;
					}
					
					if(child.m_nStartWordPosition > startWordId 
					&& child.m_nStartWordPosition <= endWordId 
					&& child.m_nEndWordPosition > endWordId )
					{
						bIntersect = true;
						break;
					}
				}
				
				if(bIntersect)
				{
					sentenceSpanType.put("Cross", sentenceSpanType.get("Cross")+1);
				}
				else
				{
					sentenceSpanType.put("NoCross", sentenceSpanType.get("NoCross")+1);
				}
				
				if(spanTree.m_nStartWordPosition == startWordId && spanTree.m_nEndWordPosition == endWordId)
				{
					sentenceSpanType.put("PerfectMatch", sentenceSpanType.get("PerfectMatch")+1);
				}
				else if(spanTree.m_nStartWordPosition == startWordId && spanTree.m_nEndWordPosition != endWordId)
				{
					sentenceSpanType.put("LeftMatch", sentenceSpanType.get("LeftMatch")+1);
				}
				else if(spanTree.m_nStartWordPosition != startWordId && spanTree.m_nEndWordPosition == endWordId)
				{
					sentenceSpanType.put("RightMatch", sentenceSpanType.get("RightMatch")+1);
				}
				else
				{
					sentenceSpanType.put("NotMatch", sentenceSpanType.get("NotMatch")+1);
				}
								
				startWordId = endWordId+1;
			}
			
			disTree.printNormal(writer);
			//writer.println();
		}
		
		reader.close();
		writer.close();
		
		writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		
		writer.println("Print sent match statistics:");
		List<Entry<String, Integer>> relationstats = MapSort.MapIntegerSort(sentenceSpanType);
		
		for(int idx = 0; idx < relationstats.size(); idx++)
		{
			Entry<String, Integer> oneItem = relationstats.get(idx);
			writer.println(String.format("%d\t%s\t%d", idx, oneItem.getKey(), oneItem.getValue()));
		}

		
		writer.close();
	}
}
