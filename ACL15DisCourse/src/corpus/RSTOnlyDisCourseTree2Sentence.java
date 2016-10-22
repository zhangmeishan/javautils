package corpus;

import java.io.*;
import java.util.*;

import edu.berkeley.nlp.syntax.Tree;

public class RSTOnlyDisCourseTree2Sentence {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		

		RSTOnlyTree disTree = new RSTOnlyTree();
		while(disTree.LoadInstanceFromSingleFile(reader, false))
		{
			disTree.printSentOnly(writer);
		}		
		reader.close();
		writer.close();
		
	}
}
