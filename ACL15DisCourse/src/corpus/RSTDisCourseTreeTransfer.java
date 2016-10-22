package corpus;

import java.io.*;
import java.util.*;

import edu.berkeley.nlp.syntax.Tree;

public class RSTDisCourseTreeTransfer {

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
		
		boolean bZparOutDisFormat = false;
		if(args.length>3 && args[3].equalsIgnoreCase("zpar"))
		{
			bZparOutDisFormat = true;
		}
		
		RSTDisCourseTree disTree = new RSTDisCourseTree();
		while(disTree.LoadInstanceFromSingleFile(reader, bZparInDisFormat))
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
		
	}
}
