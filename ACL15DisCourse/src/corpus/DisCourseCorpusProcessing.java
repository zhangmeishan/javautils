package corpus;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import edu.berkeley.nlp.syntax.Tree;

import mason.utils.MapSort;


public class DisCourseCorpusProcessing {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String wsj_dir = args[0];
		String dis_dir = args[1];
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		
		PrintWriter writer_edu = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[3]), "UTF-8"));
		

		
		File file = new File(dis_dir);
		
		Map<String, Integer> relationSaFreqMap = new HashMap<String, Integer>();
		Map<String, Integer> relationFreqMap = new HashMap<String, Integer>();
		
		Map<String, Integer> EDUTypeFreqMap = new HashMap<String, Integer>();
		
		EDUTypeFreqMap.put("LeftMatch", 0);
		EDUTypeFreqMap.put("RightMatch", 0);
		EDUTypeFreqMap.put("PerfectMatch", 0);
		EDUTypeFreqMap.put("NotMatch", 0);
		
		
		Map<String, Integer> EDUTypeIGLRPunctFreqMap = new HashMap<String, Integer>();
		
		EDUTypeIGLRPunctFreqMap.put("LeftMatch", 0);
		EDUTypeIGLRPunctFreqMap.put("RightMatch", 0);
		EDUTypeIGLRPunctFreqMap.put("PerfectMatch", 0);
		EDUTypeIGLRPunctFreqMap.put("NotMatch", 0);
		
		for(String disSubFile : file.list())
		{
			String wsjFile = getWSJFileName(wsj_dir, disSubFile);
			DisCourseTree disTree = new DisCourseTree();
			disTree.LoadFromTreeBankFile(wsjFile, file+ File.separator + disSubFile);
			disTree.m_btRootTree.getAllRelationIncludeSalences(relationSaFreqMap);
			disTree.m_btRootTree.getAllRelations(relationFreqMap);
			disTree.printNormal(writer);
			List<BinaryTree> terminalTrees = disTree.m_btRootTree.getAllTermminals();
			
			List<Integer> spanIds = new ArrayList<Integer>();
			for(BinaryTree oneTree : terminalTrees)
			{
				String[] curSpans = disTree.outputGetEDUWords(oneTree);
				String outline = curSpans[0];
				for(int idx = 1; idx < curSpans.length; idx++)
				{
					outline = outline + " " + curSpans[idx];
				}
				writer_edu.println(outline);
				
				Tree<String> eduTree = disTree.getEDUMinCoverCFGTrees(oneTree, spanIds);
				if(spanIds.size() == 2)
				{
					if(eduTree.smaller == spanIds.get(0) && eduTree.bigger == spanIds.get(1))
					{
						EDUTypeFreqMap.put("PerfectMatch", EDUTypeFreqMap.get("PerfectMatch")+1);
					}
					else if(eduTree.smaller == spanIds.get(0) && eduTree.bigger != spanIds.get(1))
					{
						EDUTypeFreqMap.put("LeftMatch", EDUTypeFreqMap.get("LeftMatch")+1);
					}
					else if(eduTree.smaller != spanIds.get(0) && eduTree.bigger == spanIds.get(1))
					{
						EDUTypeFreqMap.put("RightMatch", EDUTypeFreqMap.get("RightMatch")+1);
					}
					else
					{
						EDUTypeFreqMap.put("NotMatch", EDUTypeFreqMap.get("NotMatch")+1);
					}
				}
				else
				{
					System.out.println("error");
				}
				
				
				Tree<String> eduTreeIgnoreLRPunc = disTree.getEDUMinCoverCFGTreeIGLRPuncs(oneTree, spanIds);
				if(spanIds.size() == 2)
				{
					List<String> terminalWords = eduTreeIgnoreLRPunc.getTerminalYield();
					int startId = 0;
					int endId = 0;
					while(!BinaryTree.bContainLetterOrDigits(BinaryTree.DeNormalize(terminalWords.get(terminalWords.size()-1-endId))))
					{
						endId++;
					}
					
					while(!BinaryTree.bContainLetterOrDigits(BinaryTree.DeNormalize(terminalWords.get(startId))))
					{
						startId++;
					}
					
					int newstartId = startId + eduTreeIgnoreLRPunc.smaller;
					int newendId = -endId + eduTreeIgnoreLRPunc.bigger;
					if(newstartId == spanIds.get(0) && newendId == spanIds.get(1))
					{
						EDUTypeIGLRPunctFreqMap.put("PerfectMatch", EDUTypeIGLRPunctFreqMap.get("PerfectMatch")+1);
					}
					else if(newstartId == spanIds.get(0) && newendId != spanIds.get(1))
					{
						EDUTypeIGLRPunctFreqMap.put("LeftMatch", EDUTypeIGLRPunctFreqMap.get("LeftMatch")+1);
					}
					else if(newstartId != spanIds.get(0) && newendId == spanIds.get(1))
					{
						EDUTypeIGLRPunctFreqMap.put("RightMatch", EDUTypeIGLRPunctFreqMap.get("RightMatch")+1);
					}
					else
					{
						EDUTypeIGLRPunctFreqMap.put("NotMatch", EDUTypeIGLRPunctFreqMap.get("NotMatch")+1);
					}
				}
				else
				{
					System.out.println("error");
				}
			}
			writer_edu.println();
		}
		
		
		writer.close();
		writer_edu.close();
		
		writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[4]), "UTF-8"));
		
		writer.println("Print relation statistics:");
		List<Entry<String, Integer>> relationstats = MapSort.MapIntegerSort(relationFreqMap);
		for(int idx = 0; idx < relationstats.size(); idx++)
		{
			Entry<String, Integer> oneItem = relationstats.get(idx);
			writer.println(String.format("%d\t%s\t%d", idx, oneItem.getKey(), oneItem.getValue()));
		}
		
		writer.println();
		writer.println("Print relation and salliance statistics:");
		relationstats = MapSort.MapIntegerSort(relationSaFreqMap);
		
		for(int idx = 0; idx < relationstats.size(); idx++)
		{
			Entry<String, Integer> oneItem = relationstats.get(idx);
			writer.println(String.format("%d\t%s\t%d", idx, oneItem.getKey(), oneItem.getValue()));
		}
		
		writer.println();
		writer.println("Print edu match statistics:");
		relationstats = MapSort.MapIntegerSort(EDUTypeFreqMap);
		
		for(int idx = 0; idx < relationstats.size(); idx++)
		{
			Entry<String, Integer> oneItem = relationstats.get(idx);
			writer.println(String.format("%d\t%s\t%d", idx, oneItem.getKey(), oneItem.getValue()));
		}
		
		writer.println();
		writer.println("Print edu match statistics (ignoring left and right puncts):");
		relationstats = MapSort.MapIntegerSort(EDUTypeIGLRPunctFreqMap);
		
		for(int idx = 0; idx < relationstats.size(); idx++)
		{
			Entry<String, Integer> oneItem = relationstats.get(idx);
			writer.println(String.format("%d\t%s\t%d", idx, oneItem.getKey(), oneItem.getValue()));
		}

		writer.close();
	}
	
	
	public static String getWSJFileName(String wsj_dir, String disFile)
	{
		String path = wsj_dir;
		int splitIndex = disFile.lastIndexOf("wsj_");
		String subPath = disFile.substring(splitIndex+4, splitIndex+6);
		String fileName = "WSJ_" + disFile.substring(splitIndex+4, splitIndex+8) + ".MRG";
				
		return path + File.separator + subPath + File.separator + fileName;
	}

}
