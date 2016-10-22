package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import edu.berkeley.nlp.syntax.Tree;

public class ExtractCFGFromDisCourseTrees {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		boolean bZparInDisFormat = false;
		if(args.length>2 && args[2].equalsIgnoreCase("zpar"))
		{
			bZparInDisFormat = true;
		}
		
		
		DisCourseTree disTree = new DisCourseTree();
		
		while(disTree.LoadInstanceFromSingleFile(reader, bZparInDisFormat, null))
		{
			//disTree.printNormal(writer);
			//writer.println();
			for(Tree<String> oneTree : disTree.m_lstCfgTrees)
			{
				if(oneTree.getChildren().size() != 1)
				{
					System.out.println(oneTree);
				}
				writer.println(oneTree.getChild(0).toString());
			}
		}
		
		reader.close();
		writer.close();
	}
}
