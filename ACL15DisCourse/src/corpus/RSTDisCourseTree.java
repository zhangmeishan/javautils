package corpus;


import java.io.*;
import java.util.*;

import edu.berkeley.nlp.syntax.*;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class RSTDisCourseTree {
	List<String> m_lstAllWords = new ArrayList<String>();
	List<WordPosition> m_lstAllWordPositions = new ArrayList<WordPosition>();
	List<List<String>> m_sentWords = new ArrayList<List<String>>();
	RSTTree m_btRootTree = new RSTTree();
	List<Tree<String>> m_lstCfgTrees = new ArrayList<Tree<String>>();
	
	
	public void clear()
	{
		m_lstAllWords.clear();
		m_lstAllWordPositions.clear();
		m_sentWords.clear();
		m_btRootTree = new RSTTree();
		m_lstCfgTrees.clear();
	}
	

	
	public boolean LoadInstanceFromSingleFile(BufferedReader reader, boolean bCFGTreeZparFormart, boolean bDisTreeZparFormart) throws Exception
	{
		clear();
		List<String> lines = new ArrayList<String>();
		String sLine = "";				
		while((sLine = reader.readLine()) != null)
		{			
			sLine = sLine.trim();
			if(!sLine.isEmpty())
			{
				lines.add(sLine);
				break;
			}
		}
		
		if(lines.size() > 0)
		{
			while((sLine = reader.readLine()) != null)
			{			
				sLine = sLine.trim();
				if(sLine.isEmpty())
				{
					break;
				}
				lines.add(sLine);
			}
		}
		else
		{
			return false;
		}
		
		int linecount = lines.size();
		if(linecount%2 == 0)
		{
			return false;
		}
		
		int sentcount = linecount/2;
		
		Tree<String> disTree = new Tree<String>("");
		if(!bDisTreeZparFormart)
		{
			disTree = PennTreeReader.parseEasy(lines.get(2*sentcount));
		}
		else
		{
			Tree.readFromZparFormat(disTree, lines.get(2*sentcount));
		}
		
		m_btRootTree.BuildTreeFromCFGTree(disTree, bDisTreeZparFormart);
		
		
		if(m_btRootTree.m_nStartWordPosition != 0)
		{
			System.out.println("error input discourse tree");
			return false;
		}
		
		for(int idx = 0; idx < sentcount; idx++)
		{
			String[] curLineWords = lines.get(idx).split("\\s+");
			if(curLineWords[curLineWords.length-1].equals("<S>")
			|| curLineWords[curLineWords.length-1].equals("<P>"))
			{
				List<String> curWords = new ArrayList<String>();
				for(int idy = 0; idy < curLineWords.length; idy++)
				{					
					String curWord = curLineWords[idy];
					curWords.add(curWord);
					m_lstAllWords.add(curWord);
					m_lstAllWordPositions.add(new WordPosition(idx, idy));
				}
				m_sentWords.add(curWords);
				

				
				Tree<String> oneTree = new Tree<String>("");
				if(!bCFGTreeZparFormart)
				{
					PennTreeReader cfgreader = new PennTreeReader(new StringReader(
							lines.get(sentcount+idx)));
					oneTree = cfgreader.next();
				}
				else
				{
					Tree.readFromZparFormat(oneTree, lines.get(sentcount+idx));
					DisCourseTree.NormalizeCFGTreeFromZparInput(oneTree, m_lstAllWords);
					if(!oneTree.getLabel().equalsIgnoreCase("root"))
					{
						Tree<String> newTree = new Tree<String>("ROOT");
						List<Tree<String>> newChildren = new ArrayList<Tree<String>> ();
						newChildren.add(oneTree);
						newTree.setChildren(newChildren);
						oneTree = newTree;
					}
				}
				
				
				

				List<String> terminalWords = oneTree.getTerminalYield();
				
				if(terminalWords.size() != curLineWords.length-1)
				{
					System.out.println("cfg tree do mot match with tne sentence.");
					return false;
				}
				
				for(int idy = 0; idy < curLineWords.length-1; idy++)
				{
					if(!curLineWords[idy].startsWith(terminalWords.get(idy)))
					{
						System.out.println("cfg tree do mot match with tne sentence.");
						return false;
					}
				}
				m_lstCfgTrees.add(oneTree);
				
				if(m_btRootTree.m_nEndWordPosition +1 == m_lstAllWords.size())
				{
					break;
				}
				
				if(m_btRootTree.m_nEndWordPosition +1 < m_lstAllWords.size())
				{
					System.out.println("error input discourse tree");
					return false;
				}
			}
			else
			{
				return false;
			}
		}
				
		return true;
		
	}
	
	// Must be processed by this procedure,  a normalize step from treebank read output.
	public boolean LoadInstanceFromSingleFile(BufferedReader reader, boolean bDisTreeZparFormart) throws Exception
	{
		clear();
		List<String> lines = new ArrayList<String>();
		String sLine = "";				
		while((sLine = reader.readLine()) != null)
		{			
			sLine = sLine.trim();
			if(!sLine.isEmpty())
			{
				lines.add(sLine);
				break;
			}
		}
		
		if(lines.size() > 0)
		{
			while((sLine = reader.readLine()) != null)
			{			
				sLine = sLine.trim();
				if(sLine.isEmpty())
				{
					break;
				}
				lines.add(sLine);
			}
		}
		else
		{
			return false;
		}
		
		int linecount = lines.size();
		if(linecount%2 == 0)
		{
			return false;
		}
		
		int sentcount = linecount/2;
		
		Tree<String> disTree = new Tree<String>("");
		if(!bDisTreeZparFormart)
		{
			disTree = PennTreeReader.parseEasy(lines.get(2*sentcount));
		}
		else
		{
			Tree.readFromZparFormat(disTree, lines.get(2*sentcount));
		}
		
		m_btRootTree.BuildTreeFromCFGTree(disTree, bDisTreeZparFormart);
		
		
		if(m_btRootTree.m_nStartWordPosition != 0)
		{
			System.out.println("error input discourse tree");
			return false;
		}
		
		int idx = 0;
		for(;idx < sentcount; idx++)
		{
			String[] curLineWords = lines.get(idx).split("\\s+");
			if(curLineWords[curLineWords.length-1].equals("<S>")
			|| curLineWords[curLineWords.length-1].equals("<P>"))
			{				
				//Tree<String> oneTree = PennTreeReader.parseEasy(lines.get(sentcount+idx));
				PennTreeReader cfgreader = new PennTreeReader(new StringReader(
						lines.get(sentcount+idx)));
				Tree<String> oneTree = cfgreader.next();
				oneTree.removeEmptyNodes();
				oneTree.removeFunction();
				oneTree.removeDuplicate();

				List<String> terminalWords = oneTree.getTerminalYield();
				List<String> terminalWordPoss = oneTree.getPreTerminalYield();
				
				if(terminalWords.size() != curLineWords.length-1)
				{
					System.out.println("cfg tree do mot match with tne sentence.");
					return false;
				}
				
				List<String> curWords = new ArrayList<String>();
				for(int idy = 0; idy < curLineWords.length-1; idy++)
				{
					String curWord = curLineWords[idy];
					int lastUnderLineSplit = curWord.indexOf("_");
					if( lastUnderLineSplit > 0)
					{
						curWord = curWord.substring(0, lastUnderLineSplit);
					}

					if(!terminalWords.get(idy).equals(curWord))
					{
						System.out.println("cfg tree do mot match with tne sentence.");
						return false;
					}
					else
					{
						
						String curPos = terminalWordPoss.get(idy);
						if(curPos.isEmpty())
						{
							System.out.println("cfg tree error: "  + oneTree.toString());
						}
						curWords.add(curWord + "_" + curPos);
						m_lstAllWords.add(curWord + "_" + curPos);
						m_lstAllWordPositions.add(new WordPosition(idx, idy));
					}					
				}
				
				curWords.add(curLineWords[curLineWords.length-1]);
				m_lstAllWords.add(curLineWords[curLineWords.length-1]);
				m_lstAllWordPositions.add(new WordPosition(idx, curLineWords.length-1));
				m_sentWords.add(curWords);
				m_lstCfgTrees.add(oneTree);
								
				
				if(m_btRootTree.m_nEndWordPosition +1 == m_lstAllWords.size())
				{
					break;
				}
				
				if(m_btRootTree.m_nEndWordPosition +1 < m_lstAllWords.size())
				{
					System.out.println("error input discourse tree");
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		
		if(m_lstCfgTrees.size() != sentcount)
		{
			System.out.println("Should be normalized before processing");
			return false;
		}

				
		return true;
		
	}
	
	
	public void printNormal(PrintWriter writer)
	{
		String outline = "";
		for(int idx = 0; idx < m_sentWords.size(); idx++)
		{
			outline =  m_sentWords.get(idx).get(0);
			for(int idy = 1; idy < m_sentWords.get(idx).size(); idy++)
			{
				outline = outline + " " + m_sentWords.get(idx).get(idy);
			}			
			writer.println(outline.trim());
		}
		
		for(int idx = 0; idx < m_lstCfgTrees.size(); idx++)
		{
			writer.println(m_lstCfgTrees.get(idx).toString());
		}
		String normalstring = m_btRootTree.toString();
		writer.println(normalstring);
		writer.println();
		writer.flush();

	}
	
	
	public void printBinary(PrintWriter writer)
	{
		String outline = "";
		for(int idx = 0; idx < m_lstAllWords.size(); idx++)
		{
			outline = outline + " " + m_lstAllWords.get(idx);
			if(m_lstAllWords.get(idx).equalsIgnoreCase("<P>")
			|| m_lstAllWords.get(idx).equalsIgnoreCase("<S>"))
			{
				writer.println(outline.trim());
				outline = "";
			}
		}		
		for(int idx = 0; idx < m_lstCfgTrees.size(); idx++)
		{
			writer.println(m_lstCfgTrees.get(idx).toString());
		}
		writer.println(m_btRootTree.toBinaryString());
		writer.println();
		writer.flush();
	}
	
	public void printUnlabelBinary(PrintWriter writer)
	{
		String outline = "";
		for(int idx = 0; idx < m_lstAllWords.size(); idx++)
		{
			outline = outline + " " + m_lstAllWords.get(idx);
			if(m_lstAllWords.get(idx).equalsIgnoreCase("<P>")
			|| m_lstAllWords.get(idx).equalsIgnoreCase("<S>"))
			{
				writer.println(outline.trim());
				outline = "";
			}
		}		
		for(int idx = 0; idx < m_lstCfgTrees.size(); idx++)
		{
			writer.println(m_lstCfgTrees.get(idx).toString());
		}
		writer.println(m_btRootTree.toUnlabelBinaryString());
		writer.println();
		writer.flush();
	}
	

	public  void Evaluation(RSTDisCourseTree other, EvaluateMetrics metrs)
	{
		if(other.m_lstAllWords.size() != m_lstAllWords.size())
		{
			System.out.println("word number does not match");
			return;
		}
		for(int idx = 0; idx < m_lstAllWords.size(); idx++)
		{
			String curWordPOS = m_lstAllWords.get(idx);
			String otherWordPos = other.m_lstAllWords.get(idx);
			int lastUnderLineSplitCur = curWordPOS.indexOf("_");
			int lastUnderLineSplitOther = curWordPOS.indexOf("_");
			if(lastUnderLineSplitCur != lastUnderLineSplitOther
			|| (lastUnderLineSplitCur > 0 && !curWordPOS.substring(0, lastUnderLineSplitCur).equals(otherWordPos.substring(0, lastUnderLineSplitCur)))
			)
			{
				System.out.println("word does not match: " + curWordPOS + "\t" + otherWordPos);
				return;
			}
			
			if(lastUnderLineSplitCur == -1)
			{
				if(curWordPOS.equals(otherWordPos)
				&& (curWordPOS.equals("<S>") || curWordPOS.equals("<P>")))
				{
					continue;
				}
				else
				{
					System.out.println("word does not match: " + curWordPOS + "\t" + otherWordPos);
					return;
				}
			}
			
			if(curWordPOS.equals(otherWordPos))
			{
				metrs.poslabel_count_correct++;
			}
			
			metrs.poslabel_count_gold++;
			metrs.poslabel_count_pred++;
		}
		Set<String> curTrees = new HashSet<String>();
		Set<String> otherTrees = new HashSet<String>();
		for(int idx = 0; idx < m_lstCfgTrees.size(); idx++)
		{
			Tree<String> oneTree = m_lstCfgTrees.get(idx);
			oneTree.annotateSubTrees();
			oneTree.initParent();
			List<Tree<String>> allSubTrees = oneTree.getNonTerminals();
			for(Tree<String> aSubTree : allSubTrees)
			{
				if(aSubTree.isLeaf() || aSubTree.isPreTerminal()) continue;
				String theLabel = aSubTree.getLabel();
				String theMark = String.format("sent[%d]#label[%s]bound[%d-%d] ", idx, theLabel, aSubTree.smaller, aSubTree.bigger);
				curTrees.add(theMark);
			}
			
			Tree<String> otherTree = other.m_lstCfgTrees.get(idx);
			otherTree.annotateSubTrees();
			otherTree.initParent();
			List<Tree<String>> otherAllSubTrees = otherTree.getNonTerminals();
			for(Tree<String> aSubTree : otherAllSubTrees)
			{
				if(aSubTree.isLeaf() || aSubTree.isPreTerminal()) continue;
				String theLabel = aSubTree.getLabel();
				String theMark = String.format("sent[%d]#label[%s]bound[%d-%d] ", idx, theLabel, aSubTree.smaller, aSubTree.bigger);
				otherTrees.add(theMark);
			}
		}
		
		metrs.synlabel_count_gold = metrs.synlabel_count_gold + curTrees.size();
		metrs.synlabel_count_pred = metrs.synlabel_count_pred + otherTrees.size();
		
		for(String oneTree : otherTrees)
		{
			if(curTrees.contains(oneTree))metrs.synlabel_count_correct++;
		}
		

		Set<String> curEDUs = new HashSet<String>();
		Set<String> otherEDUs = new HashSet<String>();		
		Set<String> curSPANs = new HashSet<String>();
		Set<String> otherSPANs = new HashSet<String>();
		Set<String> curNUCLEARs = new HashSet<String>();
		Set<String> otherNUCLEARs = new HashSet<String>();
		Set<String> curLABELs = new HashSet<String>();
		Set<String> otherLABELs = new HashSet<String>();
		
		
		List<RSTTree> curSubTrees = m_btRootTree.getAllTrees();
		
		for(RSTTree oneTree: curSubTrees)
		{
			//edu
			if(oneTree.bLeafNode)
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curEDUs.add(theMark);
			}
			
			else {
				if(oneTree.children.size() !=2)
				{
					System.out.println("input children number error");
				}
				//span
				RSTTree left = oneTree.children.get(0);				
				RSTTree right = oneTree.children.get(1);
				String leftLabel = "";
				String rightLabel = "";
				String leftNuclear = "";
				String rightNuclear = "";
				String leftspan = String.format("[%d-%d]", left.m_nStartWordPosition, left.m_nEndWordPosition);
				String rightspan = String.format("[%d-%d]", right.m_nStartWordPosition, right.m_nEndWordPosition);
				
				if(oneTree.m_strRel2par.endsWith("#c"))
				{
					leftLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					rightLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					leftNuclear = "n";
					rightNuclear = "n";
				}
				else if(oneTree.m_strRel2par.endsWith("#l"))
				{
					leftLabel = "span";
					rightLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					leftNuclear = "n";
					rightNuclear = "s";
				}
				else if(oneTree.m_strRel2par.endsWith("#r"))
				{
					leftLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					rightLabel = "span";
					leftNuclear = "s";
					rightNuclear = "n";
				}
				else
				{
					System.out.println("input label error");
				}
				
				curSPANs.add(leftspan);
				curSPANs.add(rightspan);
				
				curNUCLEARs.add(leftNuclear+leftspan);
				curNUCLEARs.add(rightNuclear+rightspan);
				
				curLABELs.add(leftLabel+leftspan);
				curLABELs.add(rightLabel+rightspan);
			}
		}
		
		
		List<RSTTree> otherSubTrees = other.m_btRootTree.getAllTrees();
		for(RSTTree oneTree: otherSubTrees)
		{
			//edu
			if(oneTree.bLeafNode)
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherEDUs.add(theMark);
			}
			else {
				if(oneTree.children.size() !=2)
				{
					System.out.println("input children number error");
				}
				//span
				RSTTree left = oneTree.children.get(0);				
				RSTTree right = oneTree.children.get(1);
				String leftLabel = "";
				String rightLabel = "";
				String leftNuclear = "";
				String rightNuclear = "";
				String leftspan = String.format("[%d-%d]", left.m_nStartWordPosition, left.m_nEndWordPosition);
				String rightspan = String.format("[%d-%d]", right.m_nStartWordPosition, right.m_nEndWordPosition);
				
				if(oneTree.m_strRel2par.endsWith("#c"))
				{
					leftLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					rightLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					leftNuclear = "n";
					rightNuclear = "n";
				}
				else if(oneTree.m_strRel2par.endsWith("#l"))
				{
					leftLabel = "span";
					rightLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					leftNuclear = "n";
					rightNuclear = "s";
				}
				else if(oneTree.m_strRel2par.endsWith("#r"))
				{
					leftLabel = oneTree.m_strRel2par.substring(0, oneTree.m_strRel2par.length()-2);
					rightLabel = "span";
					leftNuclear = "s";
					rightNuclear = "n";
				}
				else
				{
					System.out.println("input label error");
				}
				
				otherSPANs.add(leftspan);
				otherSPANs.add(rightspan);
				
				otherNUCLEARs.add(leftNuclear+leftspan);
				otherNUCLEARs.add(rightNuclear+rightspan);
				
				otherLABELs.add(leftLabel+leftspan);
				otherLABELs.add(rightLabel+rightspan);
			}
		}
		
		metrs.edu_count_gold = metrs.edu_count_gold + curEDUs.size();
		metrs.edu_count_pred = metrs.edu_count_pred + otherEDUs.size();
		
		for(String oneTree : otherEDUs)
		{
			if(curEDUs.contains(oneTree))metrs.edu_count_correct++;
		}
		
		metrs.span_count_gold = metrs.span_count_gold + curSPANs.size();
		metrs.span_count_pred = metrs.span_count_pred + otherSPANs.size();
		
		for(String oneTree : otherSPANs)
		{
			if(curSPANs.contains(oneTree))metrs.span_count_correct++;
		}
		
		metrs.nuclear_count_gold = metrs.nuclear_count_gold + curNUCLEARs.size();
		metrs.nuclear_count_pred = metrs.nuclear_count_pred + otherNUCLEARs.size();
		
		for(String oneTree : otherNUCLEARs)
		{
			if(curNUCLEARs.contains(oneTree))metrs.nuclear_count_correct++;
		}
		
		metrs.dislabel_count_gold = metrs.dislabel_count_gold + curLABELs.size();
		metrs.dislabel_count_pred = metrs.dislabel_count_pred + otherLABELs.size();
		
		for(String oneTree : otherLABELs)
		{
			if(curLABELs.contains(oneTree))metrs.dislabel_count_correct++;
		}
						
	}
	


}
