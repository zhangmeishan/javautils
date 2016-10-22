package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

import edu.berkeley.nlp.syntax.*;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class DisCourseTree {
	List<String> m_lstAllWords = new ArrayList<String>();
	List<WordPosition> m_lstAllWordPositions = new ArrayList<WordPosition>();
	List<List<String>> m_sentWords = new ArrayList<List<String>>();
	BinaryTree m_btRootTree = new BinaryTree();
	List<Tree<String>> m_lstCfgTrees = new ArrayList<Tree<String>>();
	
	
	public void clear()
	{
		m_lstAllWords.clear();
		m_lstAllWordPositions.clear();
		m_sentWords.clear();
		m_btRootTree = new BinaryTree();
		m_lstCfgTrees.clear();
	}
	
	
	
	public String[] outputGetEDUWords(BinaryTree leafTree)
	{
		
		if(leafTree.bLeafNode)
		{
			int beginId = leafTree.m_nStartWordPosition;
			int endId = leafTree.m_nEndWordPosition;
			String[] allWords = new String[endId-beginId+1];
			for(int idx = 0; idx < allWords.length; idx++)
			{
				allWords[idx] = m_lstAllWords.get(beginId+idx);
			}
			return allWords;
		}
		else
		{
			return null;
		}
	}
	

	public Tree<String> getEDUMinCoverCFGTrees(BinaryTree leafTree, List<Integer> spanIds)
	{
		spanIds.clear();
		if(leafTree.bLeafNode)
		{
			int beginId = leafTree.m_nStartWordPosition;
			int endId = leafTree.m_nEndWordPosition;
			if(m_lstAllWords.get(endId).equals("<S>")
			|| m_lstAllWords.get(endId).equals("<P>"))
			{
				endId--;
			}
			
			WordPosition beginPosition = m_lstAllWordPositions.get(beginId);
			WordPosition endPosition = m_lstAllWordPositions.get(endId);
			if(beginPosition.startSentId != endPosition.startSentId)
			{
				System.out.println("EDU acrossing sentences");
				return null;
			}
			int sentId = beginPosition.startSentId;
			int startWordId = beginPosition.startWordId;
			int endWordId = endPosition.startWordId;
			Tree<String> cfgTree = m_lstCfgTrees.get(sentId);
			cfgTree.initParent();
			cfgTree.annotateSubTrees();
			spanIds.add(startWordId);
			spanIds.add(endWordId);
			Tree<String> commonAncestor = cfgTree.getTopTreeForSpan(startWordId, endWordId+1);
			if(commonAncestor == null)
			{
				System.out.println("error");
			}
			
			return commonAncestor;
			
		}
		else
		{
			return null;
		}
	}
	
	public Tree<String> getEDUMinCoverCFGTreeIGLRPuncs(BinaryTree leafTree, List<Integer> spanIds)
	{
		spanIds.clear();
		if(leafTree.bLeafNode)
		{
			int beginId = leafTree.m_nStartWordPosition;
			int endId = leafTree.m_nEndWordPosition;
			if(m_lstAllWords.get(endId).equals("<S>")
			|| m_lstAllWords.get(endId).equals("<P>"))
			{
				endId--;
			}
			
			while(!BinaryTree.bContainLetterOrDigits(BinaryTree.DeNormalize(m_lstAllWords.get(endId))))
			{
				endId--;
			}
			
			while(!BinaryTree.bContainLetterOrDigits(BinaryTree.DeNormalize(m_lstAllWords.get(beginId))))
			{
				beginId++;
			}
			
			WordPosition beginPosition = m_lstAllWordPositions.get(beginId);
			WordPosition endPosition = m_lstAllWordPositions.get(endId);
			if(beginPosition.startSentId != endPosition.startSentId)
			{
				System.out.println("EDU acrossing sentences");
				return null;
			}
			int sentId = beginPosition.startSentId;
			int startWordId = beginPosition.startWordId;
			int endWordId = endPosition.startWordId;
			Tree<String> cfgTree = m_lstCfgTrees.get(sentId);
			
			cfgTree.initParent();
			cfgTree.annotateSubTrees();
			spanIds.add(startWordId);
			spanIds.add(endWordId);
			Tree<String> commonAncestor = cfgTree.getTopTreeForSpan(startWordId, endWordId+1);
			if(commonAncestor == null)
			{
				System.out.println("error");
			}
			
			return commonAncestor;
			
		}
		else
		{
			return null;
		}
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
		
		if(bDisTreeZparFormart) m_btRootTree.UnBinarizeTree();
		
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
					NormalizeCFGTreeFromZparInput(oneTree, m_lstAllWords);
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
	public boolean LoadInstanceFromSingleFile(BufferedReader reader, boolean bDisTreeZparFormart, List<Tree<String>> deletedTrees) throws Exception
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
		
		if(bDisTreeZparFormart) m_btRootTree.UnBinarizeTree();
		
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
		
		if(deletedTrees != null)
		{
			for(idx = m_lstCfgTrees.size();idx < sentcount; idx++)
			{
				PennTreeReader cfgreader = new PennTreeReader(new StringReader(
						lines.get(sentcount+idx)));
				Tree<String> oneTree = cfgreader.next();
				oneTree.removeEmptyNodes();
				oneTree.removeFunction();
				oneTree.removeDuplicate();
				deletedTrees.add(oneTree);
			}
		}
		else
		{
			if(m_lstCfgTrees.size() != sentcount)
			{
				System.out.println("Should be normalized before processing");
				return false;
			}
		}
				
		return true;
		
	}
	
	
	public void LoadFromTreeBankFile(String cfgFile, String disFile) throws Exception
	{
		BufferedReader cfgreader = new BufferedReader(new InputStreamReader(
				new FileInputStream(cfgFile)));
		
		PennTreeReader reader = new PennTreeReader(cfgreader);
		
		List<List<String>> allWords = new ArrayList<List<String>>();

		while (reader.hasNext()) {
			Tree<String> tree = reader.next();
			
			if(tree == null)
			{
				System.out.println("strange....");
			}
				
			tree.removeDuplicate();
			tree.removeEmptyNodes();
			tree.removeFunction();
			List<Tree<String>> allPreTerms = tree.getPreTerminals();
			List<String> validWords = new ArrayList<String>();
			
			for(int idx = 0; idx < allPreTerms.size(); idx++)
			{
				if(allPreTerms.get(idx).getLabel().equalsIgnoreCase("-NONE-"))continue;
				validWords.add(allPreTerms.get(idx).getChild(0).getLabel());
			}
			if(validWords.size() > 0)
			{
				allWords.add(validWords);
				m_lstCfgTrees.add(tree);
			}
		}
		
		cfgreader.close();
		
		BufferedReader disreader = new BufferedReader(new InputStreamReader(
				new FileInputStream(disFile)));
		String allstrings = "";
		String sLine = "";				
		while((sLine = disreader.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.isEmpty())continue;
			
			allstrings = allstrings + " " + sLine;
		}
		
		allstrings = allstrings.trim();
				
		disreader.close();
		
		
		int result = m_btRootTree.ReadBinaryTreeFromTreeBankString(allstrings, allWords, 0, 0, m_lstAllWords);
		
		List<String> oneSentence = new ArrayList<String>();
		for(int idx = 0; idx < m_lstAllWords.size(); idx++)
		{
			oneSentence.add(m_lstAllWords.get(idx));
			if(m_lstAllWords.get(idx).equalsIgnoreCase("<P>")
			|| m_lstAllWords.get(idx).equalsIgnoreCase("<S>"))
			{
				m_sentWords.add(oneSentence);
				oneSentence = new ArrayList<String>();
			}
		}
		
		if(m_sentWords.size() != m_lstCfgTrees.size())
		{
			System.out.println("cfg sentences not match, please check");
		}
		else
		{
			int wordcount = 0;
			m_lstAllWordPositions.clear();
			for(int idx = 0; idx < m_sentWords.size(); idx++)
			{
				List<String> curWords = new ArrayList<String>();
				for(int idy = 0; idy < m_sentWords.get(idx).size()-1; idy++)
				{
					curWords.add(m_sentWords.get(idx).get(idy));
					m_lstAllWordPositions.add(new WordPosition(idx, idy));
				}
				String theLastWord = m_sentWords.get(idx).get(m_sentWords.get(idx).size()-1);
				m_lstAllWordPositions.add(new WordPosition(idx, m_sentWords.get(idx).size()-1));
				wordcount += m_sentWords.get(idx).size();
				if(theLastWord.equals("<S>") || theLastWord.equals("<P>"))
				{
					RefineParsingTrees(curWords, m_lstCfgTrees.get(idx));
				}
				else
				{
					System.out.println("the end word error, please check");
				}
				
			}
			
			if(m_btRootTree.m_nStartWordPosition != 0 || m_btRootTree.m_nEndWordPosition != wordcount-1)
			{
				System.out.println("root tree word span error, please check");
			}
		}
				
		if( result == -1)
		{
			System.out.println("some error occurs, please check");
		}
		else if( result == 0)
		{
			System.out.println("sentence not match, please check");
		}
		
		
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
		String beforeBinarize = m_btRootTree.toString();
		writer.println(beforeBinarize);
		m_btRootTree.BinarizeTree();
		m_btRootTree.UnBinarizeTree();
		String afterBinarizeUnBinarize = m_btRootTree.toString();
		//writer.println(afterBinarizeUnBinarize);
		writer.println();
		writer.flush();
		if(!beforeBinarize.equals(afterBinarizeUnBinarize))
		{
			System.out.println("please check binarize and unbinarize");
		}
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
		m_btRootTree.BinarizeTree();
		writer.println(m_btRootTree.toBinaryString());
		writer.println();
		writer.flush();
	}
	
	
	public static boolean RefineParsingTrees(List<String> curWords, Tree<String> cfgTree)
	{
		cfgTree.initParent();
		cfgTree.annotateSubTrees();
		cfgTree.removeFunction();
		cfgTree.removeDuplicate();
		
		List<Tree<String>> allPreTerms = cfgTree.getPreTerminals();
		int startDisId = 0;
		int startCfgId = 0;
		String firstDisWord = curWords.get(0);
		String firstCfgWord = "";
		boolean bFindStart = false;
		for(int idx = 0; idx < allPreTerms.size(); idx++)
		{
			if(allPreTerms.get(idx).getLabel().equalsIgnoreCase("-NONE-"))continue;
			String curCheckWord = allPreTerms.get(idx).getChild(0).getLabel();
			if(firstCfgWord.isEmpty()) firstCfgWord = curCheckWord;
			if(curCheckWord.equals(firstDisWord))
			{
				startCfgId = idx;
				startDisId = 0;
				bFindStart = true;
				break;
			}
		}
		
		for(int idx = 1; !bFindStart && idx < curWords.size(); idx++)
		{
			String curCheckWord = curWords.get(idx);
			if(curCheckWord.equals(firstCfgWord))
			{
				startCfgId = 0;
				startDisId = idx;
				bFindStart = true;
				break;
			}
		}
		
		if(bFindStart)
		{
			if(startCfgId > 0)
			{
				for(int idx = 0; idx < startCfgId; idx++)
				{
					if(allPreTerms.get(idx).getLabel().equalsIgnoreCase("-NONE-"))continue;
					Tree<String> theChild = allPreTerms.get(idx);
					Tree<String> theParent = theChild.parent;
					while(theParent.getChildren().size() == 1)
					{
						theChild = theParent;
						theParent = theChild.parent;
					}
					
					
					List<Tree<String>> childrentrees = theParent.getChildren();
					int idy = 0;
					while(idy < childrentrees.size())
					{
						Tree<String> curOneTree = childrentrees.get(idy);
						if(curOneTree.bigger == theChild.bigger
						&& curOneTree.smaller == theChild.smaller)
						{
							break;
						}
						idy++;
					}
					childrentrees.remove(idy);
					
					System.out.println("Remove: " + theChild.toString());
					
					if(BinaryTree.bContainLetterOrDigits(BinaryTree.DeNormalize(theChild.getTerminalStr())))
					{
						System.out.println("cfg dis word match error");
						return false;
					}
					
				}
			}
			else if (startDisId > 0)
			{
				List<Tree<String>> secondLayer = cfgTree.getChild(0).getChildren();
				for(int idx = startDisId-1; idx >= 0; idx--)
				{
					Tree<String> leafNode = new Tree<String>(getPOSLabel(curWords.get(idx)));
					List<Tree<String>> leafChidlren = new ArrayList<Tree<String>>();
					leafChidlren.add(new Tree<String>(curWords.get(idx)));
					leafNode.setChildren(leafChidlren);
					secondLayer.add(0, leafNode);	
					System.out.println("Add: " + leafNode.toString());
				}
			}
		}
		else
		{
			System.out.println("cfg dis word match error");
			return false;
		}
		
		int endDisId = startDisId+1;
		int endCfgId = startCfgId+1;
		while(endCfgId < allPreTerms.size())
		{
			if(allPreTerms.get(endCfgId).getLabel().equalsIgnoreCase("-NONE-"))
			{
				endCfgId++;
			}
			else
			{
				String curCheckWord = allPreTerms.get(endCfgId).getChild(0).getLabel();
				if(curCheckWord.equals(curWords.get(endDisId)))
				{
					endCfgId++;
					endDisId++;
					if(endDisId == curWords.size())
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
		}
		
		
		
		if(endCfgId == allPreTerms.size() && endDisId < curWords.size())
		{
			List<Tree<String>> secondLayer = cfgTree.getChild(0).getChildren();
			for(int idx = endDisId; idx < curWords.size(); idx++)
			{
				Tree<String> leafNode = new Tree<String>(getPOSLabel(curWords.get(idx)));
				List<Tree<String>> leafChidlren = new ArrayList<Tree<String>>();
				leafChidlren.add(new Tree<String>(curWords.get(idx)));
				leafNode.setChildren(leafChidlren);
				secondLayer.add(leafNode);		
				System.out.println("Add: " + leafNode.toString());
			}
		}
		else if(endCfgId < allPreTerms.size() && endDisId == curWords.size())
		{
			for(int idx = endCfgId; idx < allPreTerms.size(); idx++)
			{
				if(allPreTerms.get(idx).getLabel().equalsIgnoreCase("-NONE-"))continue;
				Tree<String> theChild = allPreTerms.get(idx);
				Tree<String> theParent = theChild.parent;
				while(theParent.getChildren().size() == 1)
				{
					theChild = theParent;
					theParent = theChild.parent;
				}
				
				
				List<Tree<String>> childrentrees = theParent.getChildren();
				int idy = 0;
				while(idy < childrentrees.size())
				{
					Tree<String> curOneTree = childrentrees.get(idy);
					if(curOneTree.bigger == theChild.bigger
					&& curOneTree.smaller == theChild.smaller)
					{
						break;
					}
					idy++;
				}
				childrentrees.remove(idy);
				System.out.println("Remove: " + theChild.toString());
				if(BinaryTree.bContainLetterOrDigits(BinaryTree.DeNormalize(theChild.getTerminalStr())))
				{
					System.out.println("cfg dis word match error");
					return false;
				}
				
			}
		}
		else if(endCfgId == allPreTerms.size() && endDisId == curWords.size())
		{
			
		}
		else
		{
			System.out.println("cfg dis word match error");
			return false;
		}
		
		cfgTree.initParent();
		cfgTree.annotateSubTrees();
		
		
		allPreTerms = cfgTree.getPreTerminals();	
		
		List<String> validWords = new ArrayList<String>();
		
		for(int idx = 0; idx < allPreTerms.size(); idx++)
		{
			if(allPreTerms.get(idx).getLabel().equalsIgnoreCase("-NONE-"))continue;
			validWords.add(allPreTerms.get(idx).getChild(0).getLabel());
		}
		
		if(validWords.size() != curWords.size())
		{
			System.out.println("cfg dis word match error");
			return false;
		}
		else
		{
			for(int idx = 0; idx < curWords.size(); idx++)
			{
				if(!validWords.get(idx).equals(curWords.get(idx)))
				{
					System.out.println("cfg dis word match error");
					return false;
				}
			}
		}
		
		
		
		return true;
		
	}
	
	public static String getPOSLabel(String theWord)
	{
		if(theWord.equals("`"))
		{
			return "``";
		}
		
		if(theWord.equals("'"))
		{
			return "''";
		}
		
		if(theWord.equals("--"))
		{
			return ":";
		}
		
		if(BinaryTree.bContainLetterOrDigits(theWord))
		{
			System.out.println("cannot get the POS label, please check");
		}
		
		if(theWord.equals("-") || theWord.equals(">"))
		{
			System.out.println("strange...");
		}
		
		return theWord;
		
	}
	
	
	public static void NormalizeCFGTreeFromZparInput(Tree<String> tree, List<String> allWords)
	{
		if(tree.isPreTerminal())
		{
			String theLabel = tree.getLabel();
			int lastIndex = theLabel.indexOf("#");			
			String thePOS = theLabel.substring(0, lastIndex);
			if(lastIndex == 0 && theLabel.startsWith("##"))thePOS = "#";
			String theChildLabel = tree.getChild(0).getLabel();
			String[] smallunits = theChildLabel.split("\\s+");
			if(smallunits.length != 2 || !smallunits[0].equals(smallunits[1]))
			{
				System.out.println("terminal tree error");
			}
			String theWord = allWords.get(Integer.parseInt(smallunits[0]));
			int splitIndex = theWord.lastIndexOf("_");
			if(splitIndex > 0)
			{
				theWord = theWord.substring(0, splitIndex);
			}
			tree.setLabel(thePOS);
			tree.getChild(0).setLabel(theWord);
			return;
		}
		else if(!tree.getLabel().endsWith("*"))
		{
			String theLabel = tree.getLabel();
			int lastIndex = theLabel.indexOf("#");
			String theNewLabel = theLabel.substring(0, lastIndex);
			tree.setLabel(theNewLabel);
			List<Tree<String>> trueNodeChildTrees = collectTrueChildNodes(tree);
			tree.getChildren().clear();
			for(Tree<String> oneTree : trueNodeChildTrees)
			{
				NormalizeCFGTreeFromZparInput(oneTree, allWords);
				tree.getChildren().add(oneTree);
			}		
		}
		else
		{
			System.out.println("normalize tree error");
		}
	}
	
	
	
	public static List<Tree<String>> collectTrueChildNodes(Tree<String> curTree)
	{
		List<Tree<String>> trueNodeChildTrees = new ArrayList<Tree<String>>();
		
		if(curTree.isPreTerminal())
		{
			if(curTree.getLabel().endsWith("*"))
			{
				System.out.println("Impossible! All terminal trees must be true node");
			}
		}
		else
		{
			for(Tree<String> oneChildTree : curTree.getChildren())
			{
				if(oneChildTree.isPreTerminal())
				{
					if(oneChildTree.getLabel().endsWith("*"))
					{
						System.out.println("Impossible! All terminal trees must be true node");
					}
					trueNodeChildTrees.add(oneChildTree);
				}
				else if(!oneChildTree.getLabel().endsWith("*"))
				{
					trueNodeChildTrees.add(oneChildTree);
				}
				else
				{
					List<Tree<String>> tmpTrees = collectTrueChildNodes(oneChildTree); 
					for(Tree<String> oneTmpTree: tmpTrees)
					{
						trueNodeChildTrees.add(oneTmpTree);
					}
				}
			}
		}
		
		
		return trueNodeChildTrees;
	}
	
	public  void Evaluation(DisCourseTree other, EvaluateMetrics metrs)
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
		
		m_btRootTree.UnBinarizeTree();
		other.m_btRootTree.UnBinarizeTree();
		
		List<BinaryTree> curSubTrees = m_btRootTree.getAllTrees();
		
		for(BinaryTree oneTree: curSubTrees)
		{
			//edu
			if(oneTree.bLeafNode)
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curEDUs.add(theMark);
			}
			//span
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curSPANs.add(theMark);
			}
			
			//nuclear
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strSalliance, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curNUCLEARs.add(theMark);
			}
			
			//dislabel
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strRel2par, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curLABELs.add(theMark);
			}
		}
		
		
		List<BinaryTree> otherSubTrees = other.m_btRootTree.getAllTrees();
		for(BinaryTree oneTree: otherSubTrees)
		{
			//edu
			if(oneTree.bLeafNode)
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherEDUs.add(theMark);
			}
			//span
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherSPANs.add(theMark);
			}
			
			//nuclear
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strSalliance, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherNUCLEARs.add(theMark);
			}
			
			//dislabel
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strRel2par, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherLABELs.add(theMark);
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
		
/*		
		curEDUs = new HashSet<String>();
		otherEDUs = new HashSet<String>();		
		curSPANs = new HashSet<String>();
		otherSPANs = new HashSet<String>();
		curNUCLEARs = new HashSet<String>();
		otherNUCLEARs = new HashSet<String>();
		curLABELs = new HashSet<String>();
		otherLABELs = new HashSet<String>();
		
		m_btRootTree.BinarizeTree();
		other.m_btRootTree.BinarizeTree();
		
		curSubTrees = m_btRootTree.getAllTrees();
		
		for(BinaryTree oneTree: curSubTrees)
		{
			//span
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curSPANs.add(theMark);
			}
			
			//nuclear
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strSalliance, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curNUCLEARs.add(theMark);
			}
			
			//dislabel
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strRel2par, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				curLABELs.add(theMark);
			}
		}
		
		
		otherSubTrees = other.m_btRootTree.getAllTrees();
		for(BinaryTree oneTree: otherSubTrees)
		{
			//span
			{
				String theMark = String.format("[%d-%d]", oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherSPANs.add(theMark);
			}
			
			//nuclear
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strSalliance, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherNUCLEARs.add(theMark);
			}
			
			//dislabel
			{
				String theMark = String.format("%s[%d-%d]", oneTree.m_strRel2par, oneTree.m_nStartWordPosition, oneTree.m_nEndWordPosition);
				otherLABELs.add(theMark);
			}
		}
		
		
		metrs.spanbin_count_gold = metrs.spanbin_count_gold + curSPANs.size();
		metrs.spanbin_count_pred = metrs.spanbin_count_pred + otherSPANs.size();
		
		for(String oneTree : otherSPANs)
		{
			if(curSPANs.contains(oneTree))metrs.spanbin_count_correct++;
		}
		
		metrs.nuclearbin_count_gold = metrs.nuclearbin_count_gold + curNUCLEARs.size();
		metrs.nuclearbin_count_pred = metrs.nuclearbin_count_pred + otherNUCLEARs.size();
		
		for(String oneTree : otherNUCLEARs)
		{
			if(curNUCLEARs.contains(oneTree))metrs.nuclearbin_count_correct++;
		}
		
		metrs.dislabelbin_count_gold = metrs.dislabelbin_count_gold + curLABELs.size();
		metrs.dislabelbin_count_pred = metrs.dislabelbin_count_pred + otherLABELs.size();
		
		for(String oneTree : otherLABELs)
		{
			if(curLABELs.contains(oneTree))metrs.dislabelbin_count_correct++;
		}	*/			
	}
	


}
