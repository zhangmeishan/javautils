package corpus;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

import mason.utils.MapSort;


public class CFGCorpusExcludingRSTTB {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String wsj_dir = args[0];
		String dis_dir = args[1];
		
		Set<String> excludedFileNames = new HashSet<String>();
					
		File file = new File(dis_dir);
		
		for(String disSubFile : file.list())
		{
			String wsjFile = getWSJFileName(wsj_dir, disSubFile);
			excludedFileNames.add(wsjFile.trim());
		}
		
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2]), "UTF-8"));

		int excludeCount = 0;
		for (int idx = 2; idx <= 23; idx++)
		{
			if(idx < 10)
			{
				file = new File(wsj_dir+ File.separator + String.format("0%d", idx));
			}
			else
			{
				file = new File(wsj_dir+ File.separator + String.format("%d", idx));
			}
			
			for(String oneFile : file.list())
			{
				String curSynFile = file+ File.separator + oneFile;
				if(excludedFileNames.contains(curSynFile))
				{
					excludeCount++;
					continue;					
				}
				else
				{
					BufferedReader cfgreader = new BufferedReader(new InputStreamReader(
							new FileInputStream(curSynFile)));
					
					PennTreeReader reader = new PennTreeReader(cfgreader);
					while (reader.hasNext()) {
						Tree<String> tree = reader.next();
						tree.removeEmptyNodes();
						tree.removeDuplicate();
						tree.removeFunction();
						writer.println(tree.toString());
					}
				}
			}
		}
		
		
		System.out.println(String.format("exclude file number: %d", excludeCount));
		
		writer.close();
	}
	
	
	public static String getWSJFileName(String wsj_dir, String disFile)
	{
		String path = wsj_dir;
		int splitIndex = disFile.lastIndexOf("wsj_");
		String subPath = disFile.substring(splitIndex+4, splitIndex+6);
		String fileName = "WSJ_" + disFile.substring(splitIndex+4, splitIndex+8) + ".MRG";
				
		return path + File.separator + subPath + File.separator + fileName;
	}

}
