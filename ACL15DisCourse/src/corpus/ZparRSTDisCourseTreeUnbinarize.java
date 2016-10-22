package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ZparRSTDisCourseTreeUnbinarize {

	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		
		RSTDisCourseTree disTree = new RSTDisCourseTree();
		
		while(disTree.LoadInstanceFromSingleFile(reader, true, true))
		{
			disTree.printNormal(writer);
		}
		
		reader.close();
		writer.close();
	}
}
