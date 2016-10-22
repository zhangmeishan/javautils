package corpus;

import java.io.*;
import java.util.*;

import edu.berkeley.nlp.syntax.Tree;

public class RSTDisCourseTree2Only {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		

		RSTDisCourseTree disTree = new RSTDisCourseTree();
		while(disTree.LoadInstanceFromSingleFile(reader, false))
		{
			RSTOnlyTree disOnlyTree = new RSTOnlyTree();
			disOnlyTree.buildTreeFromRSTDisCourseTree(disTree);
			disOnlyTree.printNormal(writer);
		//writer.println();
		}		
		reader.close();
		writer.close();
		
	}
}
