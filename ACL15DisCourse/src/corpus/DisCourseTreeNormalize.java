package corpus;

import java.io.*;
import java.util.*;

import edu.berkeley.nlp.syntax.Tree;

public class DisCourseTreeNormalize {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		
		boolean bZparInDisFormat = false;
		if(args.length>3 && args[3].equalsIgnoreCase("zpar"))
		{
			bZparInDisFormat = true;
		}
		
		boolean bZparOutDisFormat = false;
		if(args.length>4 && args[4].equalsIgnoreCase("zpar"))
		{
			bZparOutDisFormat = true;
		}
		
		DisCourseTree disTree = new DisCourseTree();
		List<Tree<String>> deletedTrees = new ArrayList<Tree<String>>();
		while(disTree.LoadInstanceFromSingleFile(reader, bZparInDisFormat, deletedTrees))
		{
			if(bZparOutDisFormat) disTree.printBinary(writer);
			else
			{
				disTree.printNormal(writer);
			}
			//writer.println();
		}		
		reader.close();
		writer.close();
		
		writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));
		
		for(Tree<String> oneTree : deletedTrees)
		{
			writer.println(oneTree.toString());
		}
		
		writer.close();
	}
}
