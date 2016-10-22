package corpus;

import java.util.*;

import edu.berkeley.nlp.syntax.*;

public class BinaryTree {

	String m_strSalliance;
	//int m_nStartSpan;
	//int m_nEndSpan;
	int m_nStartWordPosition;
	int m_nEndWordPosition;
	String m_strRel2par;
	
	BinaryTree parent;
	List<BinaryTree> children;
//	BinaryTree rightChild;
	boolean bTrueNode;
	boolean bLeafNode;
	public BinaryTree()
	{
		m_strSalliance = "";
		//m_nStartSpan = -1;
		//m_nEndSpan = -1;
		m_nStartWordPosition = -1;
		m_nEndWordPosition = -1;
		m_strRel2par = "";
		parent = null;
		children = new ArrayList<BinaryTree>();
		bTrueNode = true;
		bLeafNode = false;
	}
	
	public BinaryTree getMinSpanTree(int startWordId, int endWordId)
	{
		if(startWordId > endWordId) return null;
		if(m_nStartWordPosition == startWordId
				&& m_nEndWordPosition == endWordId)
		{
			return this;
		}
		else if(m_nStartWordPosition <= startWordId
			&& m_nEndWordPosition >= endWordId)
		{
			
			for(BinaryTree curTree : children)
			{
				if(curTree.m_nStartWordPosition <= startWordId
				&& curTree.m_nEndWordPosition >= endWordId)
				{
					return curTree.getMinSpanTree(startWordId, endWordId);
				}
			}
		}
		else
		{
			return null;
		}
		
		return this;
	}
	
	
	public void RemoveUnaryTree()
	{
		if(bLeafNode)return;
		else if(children.size() == 1)
		{
			//children.clear();
			//for(BinaryTree curChild : children.get(0))
			BinaryTree removed2Tree = children.get(0);
			while(!removed2Tree.bLeafNode && removed2Tree.children.size() == 1)
			{
				removed2Tree = children.get(0);
			}
			
			if(removed2Tree.bLeafNode)
			{
				this.bLeafNode = true;
				this.bTrueNode = true;
				this.children = removed2Tree.children;
				this.m_nEndWordPosition = removed2Tree.m_nEndWordPosition;
				this.m_nStartWordPosition = removed2Tree.m_nStartWordPosition;
				this.m_strRel2par = removed2Tree.m_strRel2par;
				this.m_strSalliance = removed2Tree.m_strSalliance;
				return;
			}
			else
			{
				children.clear();
				for(BinaryTree oneChild : removed2Tree.children)
				{
					oneChild.RemoveUnaryTree();
					oneChild.parent = this;
					children.add(oneChild);
				}
			}
		}
		else
		{
			for(BinaryTree oneChild : children)
			{
				oneChild.RemoveUnaryTree();
			}
		}
	}
	
	public void BuildTreeFromCFGTree(Tree<String> upperTree, boolean bFromZpar)
	{
		if(upperTree.isPreTerminal())
		{
			String theLabel = upperTree.getLabel();
			String theChildLabel = upperTree.getChild(0).getLabel();
			String[] theChildLabelUnits = null;
			if(bFromZpar)theChildLabelUnits = theChildLabel.split("\\s+");
			else
			{
				int splitIndex = theChildLabel.lastIndexOf("-");
				if(splitIndex != -1)
				{
					theChildLabelUnits = new String[2];
					theChildLabelUnits[0] = theChildLabel.substring(0, splitIndex);
					theChildLabelUnits[1] = theChildLabel.substring(splitIndex+1);
				}
				else
				{
					theChildLabelUnits = new String[1];
					theChildLabelUnits[0] = theChildLabel;
				}
			}
			if(theChildLabelUnits.length != 2)
			{
				System.out.println("error terminal label of Tree<String>: " + upperTree.toString());
				return;
			}
			m_nStartWordPosition = Integer.parseInt(theChildLabelUnits[0]);
			m_nEndWordPosition = Integer.parseInt(theChildLabelUnits[1]);
			bTrueNode = true;
			bLeafNode = true;
			if(bFromZpar)
			{
				int lastSplitIndex = theLabel.indexOf("#");
				m_strRel2par = theLabel.substring(0, lastSplitIndex);
			}
			else
			{
				int lastSplitIndex = theLabel.lastIndexOf("#");
				m_strRel2par = theLabel.substring(lastSplitIndex+1);
			}
			
			m_strSalliance = DisCourseLabels.GetFineGrainedLabelHead(m_strRel2par);
			if(m_strSalliance == null)
			{
				System.out.println("Cannot find the m_strSalliance");
			}
			return;
		}
		else
		{
			bLeafNode = false;
			bTrueNode = true;
			String theLabel = upperTree.getLabel();
			
			if(bFromZpar)
			{
				int lastSplitIndex = theLabel.indexOf("#");
				m_strRel2par = theLabel.substring(0, lastSplitIndex);
				if(theLabel.endsWith("*"))
				{
					bTrueNode = false;				
				}
			}
			else
			{
				int lastSplitIndex = theLabel.lastIndexOf("#");
				m_strRel2par = theLabel.substring(lastSplitIndex+1);
				if(m_strRel2par.endsWith("#*"))
				{
					bTrueNode = false;
					m_strRel2par = m_strRel2par.substring(0, m_strRel2par.length()-2);
				}
			}
			
			m_strSalliance = DisCourseLabels.GetFineGrainedLabelHead(m_strRel2par);
			if(m_strSalliance == null)
			{
				System.out.println("Cannot find the m_strSalliance");
			}
			
			int count = 0;
			for(Tree<String> child : upperTree.getChildren())
			{
				BinaryTree oneChild = new BinaryTree();
				oneChild.BuildTreeFromCFGTree(child, bFromZpar);
				children.add(oneChild);
				if(count == 0)
				{
					m_nStartWordPosition = oneChild.m_nStartWordPosition;
				}
				if(count == upperTree.getChildren().size() -1)
				{
					m_nEndWordPosition = oneChild.m_nEndWordPosition;
				}
				count++;
			}
			
			return;
		}
	}
	
	
	
	public void getAllRelations(Map<String, Integer> relationFreqMap)
	{
		if(!relationFreqMap.containsKey(m_strRel2par))
		{
			relationFreqMap.put(m_strRel2par, 0);
		}
		relationFreqMap.put(m_strRel2par, relationFreqMap.get(m_strRel2par)+1);
		if(!bLeafNode)
		{
			for(BinaryTree child : children)
			{
				child.getAllRelations(relationFreqMap);
			}
			//leftChild.getAllRelations(relationFreqMap);
			//rightChild.getAllRelations(relationFreqMap);
		}
	}
	
	
	
	public void CoarseGrained()
	{
		String m_strNewRel2par = DisCourseLabels.GetCoarseGrainedLabels(m_strRel2par);
		
		if(m_strNewRel2par == null)
		{
			System.out.println("Please check corpus");
			return;
		}
		
		m_strRel2par = m_strNewRel2par;
		
		if(!this.bLeafNode)
		{
			for(BinaryTree child : children)
			{			
				child.CoarseGrained();
			}
		}
	}
		
	public RSTTree convert2RSTTree ()
	{
		RSTTree rstTree = new RSTTree();
		
		if(bLeafNode)
		{
			rstTree.bLeafNode = true;
			rstTree.bTrueNode = true;
			rstTree.m_nStartWordPosition = m_nStartWordPosition;
			rstTree.m_nEndWordPosition = m_nEndWordPosition;
			rstTree.m_strRel2par = "leaf";
		}
		else
		{
			BinaryTree left = children.get(0);
			BinaryTree right = children.get(1);
			
			rstTree.bLeafNode = false;
			rstTree.bTrueNode = bTrueNode;
			
			rstTree.m_nStartWordPosition = m_nStartWordPosition;
			rstTree.m_nEndWordPosition = m_nEndWordPosition;
			
			if(left.m_strRel2par.equals("span"))
			{
				rstTree.m_strRel2par = right.m_strRel2par + "#l";
			}
			else if(right.m_strRel2par.equals("span")) 
			{
				rstTree.m_strRel2par = left.m_strRel2par + "#r";
			}
			else if(left.m_strRel2par.equals(right.m_strRel2par))
			{
				rstTree.m_strRel2par = left.m_strRel2par + "#c";
			}
			else
			{
				rstTree.m_strRel2par = right.m_strRel2par + "#l";
			}
			
			rstTree.children = new ArrayList<RSTTree>();
			rstTree.children.add(left.convert2RSTTree());
			rstTree.children.add(right.convert2RSTTree());
		}
		
		
		return rstTree;
	}
	
	public void getAllChildrenRelations(Map<String, Integer> relationFreqMap)
	{
		if(!bLeafNode)
		{
			String curChildrenstr = "";
			for(BinaryTree child : children)
			{
				curChildrenstr = curChildrenstr + " " + child.m_strRel2par + "#" + child.m_strSalliance;
				child.getAllChildrenRelations(relationFreqMap);
			}
			curChildrenstr = "[" + curChildrenstr.trim() + "]";
			
			if(!relationFreqMap.containsKey(curChildrenstr))
			{
				relationFreqMap.put(curChildrenstr, 0);
			}
			relationFreqMap.put(curChildrenstr, relationFreqMap.get(curChildrenstr)+1);
		}
		else
		{
			return;
		}
	}
	
	
	public void getAllRelationIncludeSalences(Map<String, Integer> relationSaFreqMap)
	{
		if(!relationSaFreqMap.containsKey(m_strSalliance+" " +m_strRel2par))
		{
			relationSaFreqMap.put(m_strSalliance+" " +m_strRel2par, 0);
		}
		relationSaFreqMap.put(m_strSalliance+" " +m_strRel2par, relationSaFreqMap.get(m_strSalliance+" " +m_strRel2par)+1);
		if(!bLeafNode)
		{
			for(BinaryTree child : children)
			{
				child.getAllRelationIncludeSalences(relationSaFreqMap);
			}
		}
	}
	
	public void BinarizeTree()
	{
		if(bLeafNode) return;
		
		int headTree = -1;
		for(int idx = 0; idx < children.size(); idx++)
		{
			BinaryTree child = children.get(idx);
			child.BinarizeTree();
			
			if(child.m_strSalliance.equalsIgnoreCase("nucleus"))
			{
				headTree = idx;
			}
		}
		
		if(headTree == -1)
		{
			//System.out.println("error tree: please check");
		}
		else
		{
			headTree = 0;
		}
		
		if(children.size() > 2)
		{
			
			BinaryTree binTree = MergeBinarizeTrees(children, headTree);
			
			if(m_nStartWordPosition != binTree.m_nStartWordPosition
			|| m_nEndWordPosition != binTree.m_nEndWordPosition
		    || bLeafNode != binTree.bLeafNode)
			{
				System.out.println("binarize error: please check");
			}
			this.children.clear();
			this.children.add(binTree.children.get(0));
			this.children.add(binTree.children.get(1));	
		}
		
		
	}
	

		
	public BinaryTree MergeBinarizeTrees(List<BinaryTree> childrenTrees, int head)
	{		
		if(childrenTrees.size() <= 2)
		{			
			System.out.println("Impossible!");
			return null;
		}
		else
		{
			BinaryTree newTree = null;
			BinaryTree left = null;
			BinaryTree right = null;
			BinaryTree middle = childrenTrees.get(head);
			
			for(int idx = head+1; idx < childrenTrees.size(); idx++)
			{
				right = childrenTrees.get(idx);
				newTree = new BinaryTree();
				newTree.m_nStartWordPosition = middle.m_nStartWordPosition;
				newTree.m_nEndWordPosition =right.m_nEndWordPosition;
				newTree.m_strSalliance = middle.m_strSalliance;
				newTree.m_strRel2par = middle.m_strRel2par;
				newTree.children.add(middle);
				newTree.children.add(right);	
				newTree.bTrueNode = false;
				newTree.bLeafNode = false;			
				middle = newTree;
			}
			
			for(int idx = head-1; idx >=0; idx--)
			{
				left = childrenTrees.get(idx);
				newTree = new BinaryTree();
				newTree.m_nStartWordPosition = left.m_nStartWordPosition;
				newTree.m_nEndWordPosition =middle.m_nEndWordPosition;
				newTree.m_strSalliance = middle.m_strSalliance;
				newTree.m_strRel2par = middle.m_strRel2par;
				newTree.children.add(left);
				newTree.children.add(middle);	
				newTree.bTrueNode = false;
				newTree.bLeafNode = false;				
				middle = newTree;				
			}
			
			
			//if(head < childrenTrees.size()-1 && head > 0)
			//{
			//	System.out.println(newTree.toString());
			//}
			return newTree;
		}
	}
	
	public String toBinaryString()
	{
		
		String outstr = "( " + m_strRel2par;
		if(m_strSalliance.equalsIgnoreCase("nucleus"))
		{
			outstr = outstr + "#N" + " ";
		}
		else
		{
			outstr = outstr + "#S" + " ";
		}
				
		if(bLeafNode)
		{
			outstr = outstr + "t " + String.format("%d %d ", m_nStartWordPosition, m_nEndWordPosition);
		}
		else if(children.size() == 1)
		{
			outstr = outstr + "s " + children.get(0).toBinaryString() + " ";
		}
		else if(children.size() == 2)
		{
			BinaryTree leftChild = children.get(0);
			BinaryTree rightChild = children.get(1);
			if(leftChild.m_strSalliance.equalsIgnoreCase("nucleus") 
			&& rightChild.m_strSalliance.equalsIgnoreCase("nucleus"))
			{
				if(this.bTrueNode)outstr = outstr + "l ";
				else outstr = outstr + "l* ";
			}
			else if(leftChild.m_strSalliance.equalsIgnoreCase("nucleus") 
					&& rightChild.m_strSalliance.equalsIgnoreCase("satellite"))
			{
				if(this.bTrueNode)outstr = outstr + "l ";
				else outstr = outstr + "l* ";
			}
			else if(leftChild.m_strSalliance.equalsIgnoreCase("satellite") 
					&& rightChild.m_strSalliance.equalsIgnoreCase("nucleus"))
			{
				if(this.bTrueNode)outstr = outstr + "r ";
				else outstr = outstr + "r* ";
			}
			else if(leftChild.m_strSalliance.equalsIgnoreCase("satellite") 
					&& rightChild.m_strSalliance.equalsIgnoreCase("satellite"))
			{
				//outstr = outstr + "c ";
				if(this.bTrueNode)System.out.println("error");
				else outstr = outstr + "l* ";
			}
			
			outstr = outstr + leftChild.toBinaryString() + " " + rightChild.toBinaryString() + " ";
		}
		else
		{
			System.out.println("error, please binarize the tree first.");
		}
		
		outstr = outstr + ")";
		
		
		
		return outstr;
	}
	
	
	public void UnBinarizeTree()
	{
		if(this.bLeafNode)
		{
			return;
		}
		else
		{
			
			List<BinaryTree> curChildren = collectTrueChildNodes();
			children.clear();
			for(BinaryTree tmpTree : curChildren)
			{
				tmpTree.UnBinarizeTree();
				children.add(tmpTree);
			}
		}
	}
	
	public List<BinaryTree> collectTrueChildNodes()
	{
		List<BinaryTree> trueNodeChildTrees = new ArrayList<BinaryTree>();
		
		if(this.bLeafNode)
		{
			if(!this.bTrueNode)
			{
				System.out.println("Impossible! All terminal trees must be true node");
			}
		}
		else
		{
			for(BinaryTree oneChildTree : this.children)
			{
				if(oneChildTree.bLeafNode)
				{
					if(!oneChildTree.bTrueNode)
					{
						System.out.println("Impossible! All terminal trees must be true node");
					}
					trueNodeChildTrees.add(oneChildTree);
				}
				else if(oneChildTree.bTrueNode)
				{
					trueNodeChildTrees.add(oneChildTree);
				}
				else
				{
					List<BinaryTree> tmpTrees = oneChildTree.collectTrueChildNodes(); 
					for(BinaryTree oneTmpTree: tmpTrees)
					{
						trueNodeChildTrees.add(oneTmpTree);
					}
				}
			}
		}
		
		
		return trueNodeChildTrees;
	}
	
	public String toString()
	{
		String outstr = "(#" + m_strSalliance + "#" + m_strRel2par;
		
		if(!bTrueNode) outstr = outstr + "#*";
		if(bLeafNode)
		{
			outstr = outstr + " " + String.format("%d-%d", m_nStartWordPosition, m_nEndWordPosition) + ")";
		}
		else
		{
			for(BinaryTree theChild : children)
			{
				outstr = outstr + " " + theChild.toString();
			}
			outstr = outstr + ")";
		}
		
		return outstr;
	}
	
	public List<BinaryTree> getAllTermminals()
	{
		List<BinaryTree> leafNodes = new ArrayList<BinaryTree>();
		if(bLeafNode)
		{
			System.out.println("Impossible here for getAllTermminals");
		}
		else
		{
			for(BinaryTree theChild : children)
			{
				if(theChild.bLeafNode)
				{
					leafNodes.add(theChild);
				}
				else
				{
					List<BinaryTree> subLeafNodes = theChild.getAllTermminals();
					for(BinaryTree tmpTree : subLeafNodes)
					{
						leafNodes.add(tmpTree);
					}
				}
			}
		}
		
		
		return leafNodes;
	}
	
	public int ReadBinaryTreeFromTreeBankString(String input, List<List<String>> allWords, int startSentId, int startWordId, List<String> prevWords)
	{
		WordPosition wordPosition = new WordPosition(0, 0);
		
		int result = ReadBinaryTreeFromTreeBankString(this, input, allWords, wordPosition, prevWords);
		return result;
		
	}
	
	
	public int ReadBinaryTreeFromTreeBankString(BinaryTree upperTree, String input, List<List<String>> allWords, WordPosition wordPosition, List<String> prevWords)
	{
		//assert(input.substring(0, 1).equals("("));
		//input = input.trim();
		int length = 0;	
		
		if(wordPosition.startSentId >= allWords.size())
		{
			return 0;
		}
		
		while(input.substring(length, length+1).equals(" "))
		{
			length++;
		}
		
		if(!input.substring(length, length+1).equals("("))
		{
			System.out.println("error: " + input);
			return -1;
		}
		length++;
		
		while(input.substring(length, length+1).equals(" "))
		{
			length++;
		}
		
		//read m_strSalliance;
		String strSalliance = "";
		
		while(!input.substring(length, length+1).equals(" "))
		{
			strSalliance = strSalliance + input.substring(length, length+1);
			length++;
		}
		
		if(strSalliance.equalsIgnoreCase("root")
		|| strSalliance.equalsIgnoreCase("satellite")
		|| strSalliance.equalsIgnoreCase("nucleus"))
		{
			
		}
		else
		{
			System.out.println("error m_strSalliance: " + input);
			return -1;
		}
		
		upperTree.m_strSalliance = strSalliance;
		
		while(input.substring(length, length+1).equals(" "))
		{
			length++;
		}
		
		
		
		String strSpanOrLeaf = "";
		
		if(!input.substring(length, length+1).equals("("))
		{
			System.out.println("error: " + input);
			return -1;
		}
		length++;
		
		while(input.substring(length, length+1).equals(" "))
		{
			length++;
		}
		
		while(!input.substring(length, length+1).equals(" "))
		{
			strSpanOrLeaf = strSpanOrLeaf + input.substring(length, length+1);
			length++;
		}
		
		while(input.substring(length, length+1).equals(" "))
		{
			length++;
		}
		
		String strSpanPosition = "";
		
		while(!input.substring(length, length+1).equals(" ") && !input.substring(length, length+1).equals(")"))
		{
			strSpanPosition = strSpanPosition + input.substring(length, length+1);
			length++;
		}
		
		if(strSpanOrLeaf.equalsIgnoreCase("leaf"))
		{
			//upperTree.m_nStartSpan = upperTree.m_nEndSpan = Integer.parseInt(strSpanPosition);
			upperTree.bLeafNode = true;
		}
		else if(strSpanOrLeaf.equalsIgnoreCase("span"))
		{			
			Integer.parseInt(strSpanPosition);
			upperTree.bLeafNode = false;
			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
			
			strSpanPosition = "";
			
			while(!input.substring(length, length+1).equals(" ") && !input.substring(length, length+1).equals(")"))
			{
				strSpanPosition = strSpanPosition + input.substring(length, length+1);
				length++;
			}
			
			Integer.parseInt(strSpanPosition);
		}
		else
		{
			System.out.println("error span value: " + input);
			return -1;
		}
		
		while(input.substring(length, length+1).equals(" "))
		{
			length++;
		}
		
		if(!input.substring(length, length+1).equals(")"))
		{
			System.out.println("error: " + input);
			return -1;
		}
		length++;
		
		if(!strSalliance.equalsIgnoreCase("root"))
		{
			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
			
			if(!input.substring(length, length+1).equals("("))
			{
				System.out.println("error: " + input);
				return -1;
			} 
			length++;
			String rel2parMark = "";
			
			while(!input.substring(length, length+1).equals(" "))
			{
				rel2parMark = rel2parMark + input.substring(length, length+1);
				length++;
			}
			
			if(!rel2parMark.equalsIgnoreCase("rel2par"))
			{
				System.out.println("error rel2par: " + input);
				return -1;
			}
			
			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
			
			String rel2par = "";
			
			while(!input.substring(length, length+1).equals(" ")&&!input.substring(length, length+1).equals(")"))
			{
				rel2par = rel2par + input.substring(length, length+1);
				length++;
			}
			
			upperTree.m_strRel2par = rel2par;
			
			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
			
			if(!input.substring(length, length+1).equals(")"))
			{
				System.out.println("error: " + input);
				return -1;
			}
			length++;
		}
		else
		{
			upperTree.m_strRel2par = "Root";
		}
		
		upperTree.m_nStartWordPosition = prevWords.size();
		
		if(strSpanOrLeaf.equalsIgnoreCase("leaf"))
		{

			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
			
			if(!input.substring(length, length+1).equals("("))
			{
				System.out.println("error: " + input);
				return -1;
			} 
			length++;
			String textMark = "";
			
			while(!input.substring(length, length+1).equals(" "))
			{
				textMark = textMark + input.substring(length, length+1);
				length++;
			}
			
			if(!textMark.equalsIgnoreCase("text"))
			{
				System.out.println("error text: " + input);
				return -1;
			}
			
			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
			
			if(input.substring(length, length+2).equals("_!"))
			{
				length++;
				length++;
			}
			else
			{
				System.out.println("error text: " + input);
				return -1;
			}
			
			String text = "";
			boolean paraend = false;
			boolean sentend = false;
			
			while(!input.substring(length, length+2).equals("_!"))
			{
				text = text + input.substring(length, length+1);
				length++;
			}
			
			if(text.endsWith("<P>"))
			{
				text = text.substring(0, text.length()-3);
				paraend = true;
			}
			text = normalizetext(text);
			String[] allcurrentwords = text.split("\\s+");
			if(text.replace(" ", "").equalsIgnoreCase("or . . . disapproved by him. . . .\"".replace(" ", "")))
			{
				//System.out.println("start debug");
			}
			List<String> currentNormalizedWords = new ArrayList<String>();
			boolean bValid = true;
			for(int idx = 0; idx < allcurrentwords.length; idx++)
			{
				int sentId = wordPosition.startSentId;
				int wordId = wordPosition.startWordId;
				
				
				if(sentId >= allWords.size())
				{
					List<String> additionalWords = new ArrayList<String>();
					
					int idz = 0;
					while (idz < allcurrentwords[idx].length())
					{
						additionalWords.add(NormalizeStart(allcurrentwords[idx].substring(idz, idz+1)));
						idz++;
					}
					
					for(int idk = 0; idk < additionalWords.size(); idk++)
					{
						prevWords.add(additionalWords.get(idk));
						currentNormalizedWords.add(additionalWords.get(idk));
						if(Character.isLetter(additionalWords.get(idk).toCharArray()[0]))
						{
							bValid = false;
							System.out.println("should modify prime file.");
						}					
					}
					
					
					continue;
				}
	
				int curSentSize = allWords.get(sentId).size();
				boolean bUnitValid = true;
				List<String> additionalEndWords = new ArrayList<String>();
				List<String> additionalStartWords = new ArrayList<String>();
				
				int startWordId = wordId;
				int startSentId = sentId;
				String curStartWord = DeNormalize(allWords.get(startSentId).get(startWordId));
				int startDistance = 0;
				boolean bFindStart = false;
				while(startDistance <= 3)
				{
					if(allcurrentwords[idx].startsWith(curStartWord))
					{
						bFindStart = true;
						break;
					}
					if(startWordId > 0 && startSentId > sentId) break;
					startWordId++;
					if(startWordId == curSentSize)
					{
						startWordId = 0;
						startSentId++;
						if(startSentId >= allWords.size() ) break;
					}
					
					curStartWord = DeNormalize(allWords.get(startSentId).get(startWordId));
					
					startDistance++;
				}
				
				int endWordId = wordId;
				int endSentId = sentId;
				String curEndWord = DeNormalize(allWords.get(endSentId).get(endWordId));
				int endDistance = 0;
				boolean bFindEnd = false;
				
				while(endDistance <= 3)
				{
					if(allcurrentwords[idx].endsWith(curEndWord))
					{
						bFindEnd = true;
						break;
					}
					if(endWordId > 0 && endSentId > sentId) break;
					endWordId++;
					if(endWordId == curSentSize)
					{
						endWordId = 0;
						endSentId++;
						if(endSentId >= allWords.size() ) break;
					}
					
					curEndWord = DeNormalize(allWords.get(endSentId).get(endWordId));
					
					endDistance++;
				}
			
				
				int distance = 0;
				
				if(bFindStart && bFindEnd && endDistance >= startDistance)
				{
					if(idx != 0)
					{
						startWordId = wordId;
						startSentId = sentId;
						distance = endDistance + 1;
					}
					else
					{
						distance = endDistance - startDistance + 1;
					}
				}
				else if(bFindStart && !bFindEnd)
				{
					
					endWordId = startWordId;
					endSentId = startSentId;
					if(idx != 0)
					{
						startWordId = wordId;
						startSentId = sentId;
						distance = startDistance + 1;
					}
					else
					{
						distance = 1;
					}
	
					
					if(idx == allcurrentwords.length-1)
					{
						int idz = curStartWord.length();
						while(idz < allcurrentwords[allcurrentwords.length-1].length())
						{
							additionalEndWords.add(NormalizeEnd(allcurrentwords[allcurrentwords.length-1].substring(idz, idz+1)));
							idz++;
						}
					}
					
				}
				else if(!bFindStart && bFindEnd)
				{
					
					if(idx != 0)
					{
						startWordId = wordId;
						startSentId = sentId;
						distance = endDistance + 1;
					}
					else
					{
						startWordId = endWordId;
						startSentId = endSentId;
						distance = 1;
						int idz = 0;
						while (idz < allcurrentwords[0].length() - curEndWord.length())
						{
							additionalStartWords.add(NormalizeStart(allcurrentwords[0].substring(idz, idz+1)));
							idz++;
						}
					}
				}
				else
				{
					startWordId = wordId;
					startSentId = sentId;
					distance = 0;
					if(idx == 0)
					{
						int idz = 0;
						while (idz < allcurrentwords[0].length())
						{
							additionalStartWords.add(NormalizeStart(allcurrentwords[0].substring(idz, idz+1)));
							idz++;
						}
					}
					else if(idx == allcurrentwords.length-1)
					{
						int idz = 0;
						while(idz < allcurrentwords[allcurrentwords.length-1].length())
						{
							additionalEndWords.add(NormalizeEnd(allcurrentwords[allcurrentwords.length-1].substring(idz, idz+1)));
							idz++;
						}
					}
					else
					{
						bUnitValid = false;
					}
				}
				
				
				for(int idk = 0; idk < additionalStartWords.size(); idk++)
				{
					prevWords.add(additionalStartWords.get(idk));
					currentNormalizedWords.add(additionalStartWords.get(idk));
					if(Character.isLetter(additionalStartWords.get(idk).toCharArray()[0]))
					{
						bUnitValid = false;
						System.out.println("should modify prime file.");
					}
				}
				
				if(startSentId > wordPosition.startSentId)
				{
					System.out.println("debug");
				}
				wordPosition.startSentId = startSentId;
				wordPosition.startWordId = startWordId;
				for(int idk = 0; idk < distance; idk++)
				{
					String curWord = allWords.get(wordPosition.startSentId).get(wordPosition.startWordId);
					prevWords.add(curWord);		
					currentNormalizedWords.add(curWord);
					wordPosition.startWordId++;
					if(wordPosition.startWordId == allWords.get(wordPosition.startSentId).size())
					{
						wordPosition.startSentId++;
						if(wordPosition.startSentId == allWords.size())paraend = true;
						wordPosition.startWordId = 0;
						sentend = true;
						break;
					}
				}
				
				for(int idk = 0; idk < additionalEndWords.size(); idk++)
				{
					prevWords.add(additionalEndWords.get(idk));
					currentNormalizedWords.add(additionalEndWords.get(idk));
					if(Character.isLetter(additionalEndWords.get(idk).toCharArray()[0]))
					{
						bUnitValid = false;
						System.out.println("should modify prime file.");
					}					
				}
				
				if(!bUnitValid)
				{
					bValid = false;
					String outerrorstr1 = allcurrentwords[0];
					for(int idk = 1; idk < allcurrentwords.length; idk++) outerrorstr1 = outerrorstr1 + " " + allcurrentwords[idk];
					String outerrorstr2 = allWords.get(sentId).get(wordId);
					for(int idk = wordId+1; idk < curSentSize; idk++) outerrorstr2 = outerrorstr2 + " " + allWords.get(sentId).get(idk);
					System.out.println("edu not match at " + String.format("%d", idx) +
							" : [" + outerrorstr1 + "]\t[" + outerrorstr2 + "]");
					System.out.flush();
				}								
			}
			
			if(!bValid)
			{
				String outerrorstr1 = allcurrentwords[0];
				for(int idk = 1; idk < allcurrentwords.length; idk++) outerrorstr1 = outerrorstr1 + " " + allcurrentwords[idk];
				String outerrorstr2 = currentNormalizedWords.get(0);
				for(int idk = 1; idk < currentNormalizedWords.size(); idk++) outerrorstr2 = outerrorstr2 + " " + currentNormalizedWords.get(idk);
				System.out.println("edu compare:");
				System.out.println("[" + outerrorstr1 + "]");
				System.out.println("[" + outerrorstr2 + "]");
				System.out.flush();
			}
			else
			{
				
			}
			
			if(wordPosition.startSentId < allWords.size())
			{
				int curWordId =  wordPosition.startWordId;
				String curWord = allWords.get(wordPosition.startSentId).get(curWordId);
				curWord = DeNormalize(curWord);
				while(!bContainLetterOrDigits(curWord))
				{
					curWordId++;	
					if(curWordId == allWords.get(wordPosition.startSentId).size()) break;
					curWord = allWords.get(wordPosition.startSentId).get(curWordId);
					curWord = DeNormalize(curWord);
				}
				if(curWordId == allWords.get(wordPosition.startSentId).size())
				{
					wordPosition.startSentId++;
					if(wordPosition.startSentId == allWords.size())paraend = true;
					wordPosition.startWordId = 0;
					sentend = true;
				}
				
			}

			
			if(sentend&&paraend) prevWords.add("<P>");
			else if(sentend) prevWords.add("<S>");
			
			length++;
			length++;
			
			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
			
			if(!input.substring(length, length+1).equals(")"))
			{
				System.out.println("error: " + input);
				return -1;
			}
			else
			{
				length++;
				while(input.substring(length, length+1).equals(" "))
				{
					length++;
				}
				if(!input.substring(length, length+1).equals(")"))
				{
					System.out.println("error: " + input);
					return -1;
				}
				length++;
				upperTree.m_nEndWordPosition = prevWords.size()-1;
				return length;
			}
		
		}
		
		while (true)
		{
			BinaryTree oneChild = new BinaryTree();
			int childLength = ReadBinaryTreeFromTreeBankString(oneChild, input.substring(length), allWords, wordPosition, prevWords);						
			if(childLength < 0)
			{
				System.out.println("error left tree: " + input.substring(length));
				return -1;
			}
			
			if(childLength == 0)
			{
				upperTree.m_nEndWordPosition = prevWords.size()-1;
				System.out.println("sentence number not match. ");
				return 0;
			}
			
			
			length = length + childLength;
			
			oneChild.parent = upperTree;
			upperTree.children.add(oneChild);
			
			while(input.substring(length, length+1).equals(" "))
			{
				length++;
			}
		
			if(input.substring(length, length+1).equals(")"))
			{
				length++;
				break;
			}
			if(!input.substring(length, length+1).equals("("))
			{
				System.out.println("error child tree: " + input.substring(length));
				return -1;
			}
						
		}
		

		upperTree.m_nEndWordPosition = prevWords.size()-1;
		
		return length;
	}
	
	public static String DeNormalize(String theWord)
	{
		if(theWord.equalsIgnoreCase("''") || theWord.equalsIgnoreCase("``"))
		{
			return "\"";
		}
		
		if(theWord.equalsIgnoreCase("'") || theWord.equalsIgnoreCase("`"))
		{
			return "\'";
		}
		
		if(theWord.equalsIgnoreCase("-LCB-") )
		{
			return "{";
		}
		if(theWord.equalsIgnoreCase("-RCB-") )
		{
			return "}";
		}
		
		if(theWord.equalsIgnoreCase("-LRB-") )
		{
			return "(";
		}
		if(theWord.equalsIgnoreCase("-RRB-") )
		{
			return ")";
		}
		
		String theNewWord = theWord.replace("\\/", "/");
		theNewWord = theNewWord.replace("\\*", "*");
		
		return theNewWord;
	}
	
	public static String NormalizeEnd(String theWord)
	{
		if(theWord.equalsIgnoreCase("\""))
		{
			return "''";
		}
		
		if(theWord.equalsIgnoreCase("\'"))
		{
			return "'";
		}
		
		if(theWord.equalsIgnoreCase("{") )
		{
			return "-LCB-";
		}
		if(theWord.equalsIgnoreCase("}") )
		{
			return "-RCB-";
		}
		
		if(theWord.equalsIgnoreCase("(") )
		{
			return "-LRB-";
		}
		if(theWord.equalsIgnoreCase(")") )
		{
			return "-RRB-";
		}
		
		String theNewWord = theWord.replace("/", "\\/");
		theNewWord = theNewWord.replace("*", "\\*");
		
		return theNewWord;
	}
	
	public static String NormalizeStart(String theWord)
	{
		if(theWord.equalsIgnoreCase("\""))
		{
			return "``";
		}
		
		if(theWord.equalsIgnoreCase("\'"))
		{
			return "`";
		}
		
		if(theWord.equalsIgnoreCase("{") )
		{
			return "-LCB-";
		}
		if(theWord.equalsIgnoreCase("}") )
		{
			return "-RCB-";
		}
		
		if(theWord.equalsIgnoreCase("(") )
		{
			return "-LRB-";
		}
		if(theWord.equalsIgnoreCase(")") )
		{
			return "-RRB-";
		}
		
		String theNewWord = theWord.replace("/", "\\/");
		theNewWord = theNewWord.replace("*", "\\*");
		
		return theNewWord;
	}
	
	public static String normalizetext(String text)
	{
		String newtext = text.trim();
		int indexdot = newtext.indexOf(". . . .");
		while(indexdot != -1)
		{
			newtext = newtext.substring(0, indexdot).trim() + " ... . " + newtext.substring(indexdot+7).trim();
			newtext = newtext.trim();
			indexdot = newtext.indexOf(". . . .");
		}
		
		indexdot = newtext.indexOf(". . .");
		while(indexdot != -1)
		{
			newtext = newtext.substring(0, indexdot).trim() + " ... " + newtext.substring(indexdot+5).trim();
			newtext = newtext.trim();
			indexdot = newtext.indexOf(". . .");
		}
		
		if(newtext.endsWith("\"") || newtext.endsWith("\'"))
		{
			newtext = newtext.substring(0, newtext.length()-1).trim() + " " + newtext.substring(newtext.length()-1);
		}
		
		return newtext;
	}
	
	public static boolean bContainLetterOrDigits(String theWord)
	{
		boolean bContainLetter = false;
		
		char[] allChars = theWord.toCharArray();
		for(int idx = 0; idx < allChars.length; idx++)
		{
			if(Character.isLetterOrDigit(allChars[idx]))
			{
				return true;
			}
		}
		
		
		return bContainLetter;
	}
	
	public List<BinaryTree> getAllTrees()
	{
		List<BinaryTree> allTrees = new ArrayList<BinaryTree>();
		
		if(this.bLeafNode)
		{
			allTrees.add(this);
		}
		else
		{
			for(BinaryTree aChild : this.children)
			{
				List<BinaryTree> aSubTrees = aChild.getAllTrees();
				for(BinaryTree aSmallerChild : aSubTrees)
				{
					allTrees.add(aSmallerChild);
				}
			}
			allTrees.add(this);
		}
		
		
		return allTrees;
	}
}
