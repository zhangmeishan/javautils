package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class ReplaceCFGFromDisCourseTrees {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		BufferedReader readerbinarycfg = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		
		
		DisCourseTree disTree = new DisCourseTree();
		String sLine = "";
		while(disTree.LoadInstanceFromSingleFile(reader, true, null))
		{
			//disTree.printNormal(writer);
			//writer.println();
			List<String> output_strs = new ArrayList<String>();
			String outline = "";
			for(int idx = 0; idx < disTree.m_lstAllWords.size(); idx++)
			{
				outline = outline + " " + disTree.m_lstAllWords.get(idx);
				if(disTree.m_lstAllWords.get(idx).equalsIgnoreCase("<P>")
				|| disTree.m_lstAllWords.get(idx).equalsIgnoreCase("<S>"))
				{
					output_strs.add(outline.trim());
					outline = "";
				}
			}
			
			int count = 0;
			for(int idx = 0; idx < disTree.m_lstCfgTrees.size(); idx++)
			{
				//writer.println(m_lstCfgTrees.get(idx).toString());
				while((sLine = readerbinarycfg.readLine()) != null)
				{
					sLine = sLine.trim();
					if(!sLine.isEmpty())
					{
						break;
					}
				}
				if(sLine == null)
				{
					System.out.println("error, not match");
				}
				
				Tree<String> oneTree = new Tree<String>("");
				Tree.readFromZparFormat(oneTree, sLine);
				
				String outcfgtree = toZparFormart(oneTree, count);
				
				List<String> terminalWords = oneTree.getTerminalYield();
				List<Tree<String>> preterminals = oneTree.getPreTerminals();
				
				String worposline = "";
				for(int idy = 0; idy < disTree.m_sentWords.get(idx).size()-1; idy++)
				{
					String theWordPos = disTree.m_sentWords.get(idx).get(idy);
					int lastUnderLineSplit = theWordPos.indexOf("_");
					String theWord = "";
					String thePOS = "";
					if(lastUnderLineSplit > 0)
					{
						theWord = theWordPos.substring(0, lastUnderLineSplit);
						thePOS = theWordPos.substring(lastUnderLineSplit+1);
					}

					if(!terminalWords.get(idy).equals(theWord))
					{
						System.out.println("cfg tree do mot match with tne sentence.");
					}
					String curPOSLabel = preterminals.get(idy).getLabel();
					int lastSplitIndex = curPOSLabel.indexOf("#t");
					if(lastSplitIndex != curPOSLabel.length()-2)
					{
						System.out.println("error");
					}
					else
					{
						curPOSLabel = curPOSLabel.substring(0, lastSplitIndex);
					}
					if(!curPOSLabel.equals(thePOS))
					{
						System.out.println("cfg tree do mot match with tne sentence.");
					}
					
					String newWord = terminalWords.get(idy) + "_" + curPOSLabel;
					disTree.m_sentWords.get(idx).set(idy, newWord);
					disTree.m_lstAllWords.set(count, newWord);
					worposline = worposline + newWord + " ";
					count++;
				}
				
				if(disTree.m_lstAllWords.get(count).equals("<S>") 
				|| disTree.m_lstAllWords.get(count).equals("<P>") )
				{
					worposline = worposline + disTree.m_lstAllWords.get(count);
				}
				else
				{
					System.out.println("error");
				}
				count++;
				output_strs.set(idx, worposline);
				output_strs.add(outcfgtree);
			}
			
			for(int idx = 0; idx < output_strs.size(); idx++)
			{
				writer.println(output_strs.get(idx));
			}
			
			disTree.m_btRootTree.BinarizeTree();
			writer.println(disTree.m_btRootTree.toBinaryString());
			writer.println();
			writer.flush();
			
			//for(Tree<String> oneTree : disTree.m_lstCfgTrees)
			//{
			//	writer.println(oneTree.toString());
			//}
		}
		
		reader.close();
		readerbinarycfg.close();
		writer.close();
	}
	
	public static String toZparFormart(Tree<String> upperTree, int startWordId)
	{
		String output = "( ";
		String theLabel = upperTree.getLabel();
		int lastSplitIndex = theLabel.lastIndexOf("#");
		if(theLabel.endsWith("*"))
		{
			if(lastSplitIndex != theLabel.length()-3)
			{
				System.out.println("Error input tree");
				return "";
			}
		}
		else
		{
			if(lastSplitIndex != theLabel.length()-2)
			{
				System.out.println("Error input tree");
				return "";
			}
		}
		
		output = output + theLabel.substring(0, lastSplitIndex) + " " + theLabel.substring(lastSplitIndex+1) + " ";
		if(upperTree.isLeaf())
		{
			System.out.println("Error input tree");
			return "";
		}
		else if(upperTree.isPreTerminal())
		{
			output = output + String.format("%d %d ", startWordId, startWordId);
		}
		else
		{
			int passedWords = 0;
			for(Tree<String> oneChild : upperTree.getChildren())
			{
				output = output + toZparFormart(oneChild, startWordId + passedWords) + " ";
				passedWords = passedWords + oneChild.getPreTerminals().size();
			}
		}
		
		output = output + ")";
		
		return output;
	}
}
