package acl2013;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;


public class CLT2RawSent {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		InputStreamReader inputData = new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8");
		PennTreeReader treeReader = new PennTreeReader(inputData);
		
		PrintWriter output = new PrintWriter(System.out);
		
		if(args.length > 1)
		{
			output = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(args[1]), "UTF-8"), false);
		}
		
		while (treeReader.hasNext()) {
			Tree<String> curTree = treeReader.next();
			List<Tree<String>> allTerminals = curTree.getTerminals();
			
			String theOut = "";
			for(Tree<String> oneTree : allTerminals)
			{
				String theWord = oneTree.getLabel();
				theOut = theOut + theWord;
			}
			
			output.println(theOut.trim());
		
		}
		
		inputData.close();
		output.close();
	}

}
