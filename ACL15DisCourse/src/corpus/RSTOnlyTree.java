package corpus;


import java.io.*;
import java.util.*;

import edu.berkeley.nlp.syntax.*;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class RSTOnlyTree {
	List<String> m_lstAllWords = new ArrayList<String>();
	List<WordPosition> m_lstAllWordPositions = new ArrayList<WordPosition>();
	List<List<String>> m_sentWords = new ArrayList<List<String>>();
	RSTTree m_btRootTree = new RSTTree();

		
	public void clear()
	{
		m_lstAllWords.clear();
		m_lstAllWordPositions.clear();
		m_sentWords.clear();
		m_btRootTree = new RSTTree();
	}
		
	
	public void  buildTreeFromRSTDisCourseTree(RSTDisCourseTree tree)
	{
		for(int idx = 0; idx < tree.m_lstAllWords.size(); idx++)
		{
			String curWordPOS = tree.m_lstAllWords.get(idx);
			int splitIndex = curWordPOS.lastIndexOf("_");
			String curWord = curWordPOS;
			if(splitIndex > 0) curWord = curWordPOS.substring(0, splitIndex);
			m_lstAllWords.add(curWord);
			m_lstAllWordPositions.add(new WordPosition(tree.m_lstAllWordPositions.get(idx).startSentId, 
					tree.m_lstAllWordPositions.get(idx).startWordId));
		}
		
		int count = 0;
		for(int idx = 0; idx < tree.m_sentWords.size(); idx++)
		{
			List<String> sentWords = new ArrayList<String>();
			for(int idy = 0; idy < tree.m_sentWords.get(idx).size(); idy++)
			{
				String curWordPOS = tree.m_sentWords.get(idx).get(idy);
				int splitIndex = curWordPOS.lastIndexOf("_");
				String curWord = curWordPOS;
				if(splitIndex > 0) curWord = curWordPOS.substring(0, splitIndex);
				sentWords.add(curWord);
			}
			m_sentWords.add(sentWords);
			count += sentWords.size();
		}
		
		m_btRootTree = tree.m_btRootTree;
		
		if(m_btRootTree.m_nStartWordPosition != 0
				|| m_btRootTree.m_nEndWordPosition != count -1
				|| count != m_lstAllWords.size())
		{
			System.out.println("error conversion");
			
		}
	}
	
	public void ExtractEDUs(List<String> edus)
	{
		List<RSTTree> leaftrees = m_btRootTree.getAllTermminals();
		
		for(RSTTree rstTree :  leaftrees)
		{
			int beginId = rstTree.m_nStartWordPosition;
			int endId = rstTree.m_nEndWordPosition;
			String oneSent = "";
			for(int idx = beginId; idx <= endId; idx++)
			{
				oneSent = oneSent + " " +m_lstAllWords.get(beginId+idx);
			}
			edus.add(oneSent.trim());
		}
	}
	
	
	public boolean LoadInstanceFromSingleFile(BufferedReader reader,  boolean bDisTreeZparFormart) throws Exception
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
		
		int sentcount = linecount-1;
		
		Tree<String> disTree = new Tree<String>("");
		if(!bDisTreeZparFormart)
		{
			disTree = PennTreeReader.parseEasy(lines.get(sentcount));
		}
		else
		{
			Tree.readFromZparFormat(disTree, lines.get(sentcount));
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
		
		String normalstring = m_btRootTree.toString();
		writer.println(normalstring);
		writer.println();
		writer.flush();

	}
	
	public void printSentOnly(PrintWriter writer)
	{
		String outline = "";
		for(int idx = 0; idx < m_sentWords.size(); idx++)
		{
			outline =  m_sentWords.get(idx).get(0);
			for(int idy = 1; idy < m_sentWords.get(idx).size()-1; idy++)
			{
				outline = outline + " " + m_sentWords.get(idx).get(idy);
			}			
			writer.println(outline.trim());
		}
		
		//String normalstring = m_btRootTree.toString();
		//writer.println(normalstring);
		//writer.println();
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
		writer.println(m_btRootTree.toUnlabelBinaryString());
		writer.println();
		writer.flush();
	}
	

	public  void Evaluation(RSTOnlyTree other, EvaluateMetrics metrs)
	{
		if(other.m_lstAllWords.size() != m_lstAllWords.size())
		{
			System.out.println("word number does not match");
			return;
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
