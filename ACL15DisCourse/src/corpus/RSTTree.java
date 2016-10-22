package corpus;

import java.util.*;

import edu.berkeley.nlp.syntax.*;

public class RSTTree {

	int m_nStartWordPosition;
	int m_nEndWordPosition;
	String m_strRel2par;  // endwith #c, denote both are nuclears; #l, left nuclear, #r right nuclear
	
	RSTTree parent;
	List<RSTTree> children;
//	BinaryTree rightChild;
	boolean bTrueNode;
	boolean bLeafNode;
	public RSTTree()
	{
		m_nStartWordPosition = -1;
		m_nEndWordPosition = -1;
		m_strRel2par = "";
		parent = null;
		children = new ArrayList<RSTTree>();
		bTrueNode = true;
		bLeafNode = false;
	}
	
	public RSTTree getMinSpanTree(int startWordId, int endWordId)
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
			
			for(RSTTree curTree : children)
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
				m_strRel2par = theLabel;
			}
			
			if(!m_strRel2par.equals("leaf"))
			{
				System.out.println("leaf node error");
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
				int lastsplitIndex = theLabel.lastIndexOf("#");
				m_strRel2par = theLabel.substring(0, lastsplitIndex);
				if(theLabel.endsWith("*"))
				{
					bTrueNode = false;
				}
			}
			else
			{
				m_strRel2par = theLabel;
				if(m_strRel2par.endsWith("#*"))
				{
					bTrueNode = false;
					m_strRel2par = m_strRel2par.substring(0, m_strRel2par.length()-2);
				}
			}
						
			int count = 0;
			for(Tree<String> child : upperTree.getChildren())
			{
				RSTTree oneChild = new RSTTree();
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
			
			if(count != 2)
			{
				System.out.println("error, not a binarzied tree.");
			}			
			return;
		}
	}
	
	
	
	public void getAllRelations(Map<String, Integer> relationFreqMap)
	{
		if(!bLeafNode)
		{
			if(!relationFreqMap.containsKey(m_strRel2par))
			{
				relationFreqMap.put(m_strRel2par, 0);
			}
			relationFreqMap.put(m_strRel2par, relationFreqMap.get(m_strRel2par)+1);
			for(RSTTree child : children)
			{
				child.getAllRelations(relationFreqMap);
			}
		}

	}
	
	//ignore temp node
	public String toBinaryString()
	{
		String outstr = "( " + m_strRel2par + " ";				
		if(bLeafNode)
		{
			outstr = "( " + m_strRel2par + " t " + String.format("%d %d ", m_nStartWordPosition, m_nEndWordPosition);
		}
		else if(children.size() == 2)
		{	
			RSTTree leftChild = children.get(0);
			RSTTree rightChild = children.get(1);
			if(m_strRel2par.endsWith("#c"))
			{
				outstr = outstr + "l ";
			}
			else if(m_strRel2par.endsWith("#l"))
			{
				outstr = outstr + "l ";
			}
			else if(m_strRel2par.endsWith("#r"))
			{
				outstr = outstr + "r ";
			}
			else 
			{
				System.out.println("error");
				
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
	
	//ignore temp node
	public String toString()
	{
		String outstr = "(" + m_strRel2par;
		
		if(!bTrueNode) outstr = outstr + "#*";
		if(bLeafNode)
		{
			outstr = outstr + " " + String.format("%d-%d", m_nStartWordPosition, m_nEndWordPosition) + ")";
		}
		else
		{
			for(RSTTree theChild : children)
			{
				outstr = outstr + " " + theChild.toString();
			}
			outstr = outstr + ")";
		}
		
		return outstr;
	}
	
	
	public String toUnlabelBinaryString()
	{
		String outstr = "";				
		if(bLeafNode)
		{
			outstr = "( " + m_strRel2par + " t " + String.format("%d %d ", m_nStartWordPosition, m_nEndWordPosition);
		}
		else if(children.size() == 2)
		{
			outstr = "( " + "const" + m_strRel2par.substring(m_strRel2par.length()-2) + " ";	
			RSTTree leftChild = children.get(0);
			RSTTree rightChild = children.get(1);
			if(m_strRel2par.endsWith("#c"))
			{
				outstr = outstr + "l ";
			}
			else if(m_strRel2par.endsWith("#l"))
			{
				outstr = outstr + "l ";
			}
			else if(m_strRel2par.endsWith("#r"))
			{
				outstr = outstr + "r ";
			}
			else 
			{
				System.out.println("error");
				
			}
			
			outstr = outstr + leftChild.toUnlabelBinaryString() + " " + rightChild.toUnlabelBinaryString() + " ";
		}
		else
		{
			System.out.println("error, please binarize the tree first.");
		}
		
		outstr = outstr + ")";
		
		
		
		return outstr;
	}
	
	public List<RSTTree> getAllTermminals()
	{
		List<RSTTree> leafNodes = new ArrayList<RSTTree>();
		if(bLeafNode)
		{
			System.out.println("Impossible here for getAllTermminals");
		}
		else
		{
			for(RSTTree theChild : children)
			{
				if(theChild.bLeafNode)
				{
					leafNodes.add(theChild);
				}
				else
				{
					List<RSTTree> subLeafNodes = theChild.getAllTermminals();
					for(RSTTree tmpTree : subLeafNodes)
					{
						leafNodes.add(tmpTree);
					}
				}
			}
		}
		
		
		return leafNodes;
	}
	

	
	public List<RSTTree> getAllTrees()
	{
		List<RSTTree> allTrees = new ArrayList<RSTTree>();
		
		if(this.bLeafNode)
		{
			allTrees.add(this);
		}
		else
		{
			for(RSTTree aChild : this.children)
			{
				List<RSTTree> aSubTrees = aChild.getAllTrees();
				for(RSTTree aSmallerChild : aSubTrees)
				{
					allTrees.add(aSmallerChild);
				}
			}
			allTrees.add(this);
		}
		
		
		return allTrees;
	}
}
