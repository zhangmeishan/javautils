package mason;
import java.io.*;
import java.util.*;

public class FindMaxIterationByLog {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		List<String> fileNames = new ArrayList<String>();
		File file=new File(args[0]); 
		if(file.isDirectory())
		{
			for (String oneFile : file.list())
			{
				fileNames.add(args[0] + File.separator +oneFile);
			}
		}
		else if(file.isFile())
		{
			fileNames.add(args[0]);
		}
		
		PrintWriter out = new PrintWriter(System.out);
		if(args.length > 1)
		{
			out = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(args[1]), "UTF-8"));
		}
		
		int maxIter = -1;
		
		if(args.length > 2)
		{
			maxIter = Integer.parseInt(args[2]);
		}
		
		for(String oneFile : fileNames)
		{
			FindOneFile(oneFile, out, maxIter);
		}
		
		out.close();
		

	}
	
	
	public static void FindOneFile(String sFile, PrintWriter out, int maxIter) throws Exception
	{		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(sFile), "UTF8"));	
		String sLine = null;
		int bestIter = -1;
		double bestDevPos = -1;
		double bestDevDep = -1;
		double bestTestPos = -1;
		double bestTestDep = -1;
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.startsWith("Test iteration "))
			{
				
				String[] theUnits = sLine.split("\\s+");
				int curIter = Integer.parseInt(theUnits[2]);
				
				double curDevPos = -1;
				double curDevDep = -1;
				double curTestPos = -1;
				double curTestDep = -1;
				
				while ((sLine = in.readLine()) != null)
				{
					sLine = sLine.trim();
					if(sLine.startsWith("ROOT (excluding punc):"))
					{
						break;
					}
					else if(sLine.startsWith("Pos Accuracy:"))
					{
						int splitIndex = sLine.lastIndexOf("=");
						curDevPos = Double.parseDouble(sLine.substring(splitIndex+1));
					}
					else if(sLine.startsWith("UAS (excluding punc):"))
					{
						int splitIndex = sLine.lastIndexOf("=");
						curDevDep = Double.parseDouble(sLine.substring(splitIndex+1));
					}
				}
				
				while ((sLine = in.readLine()) != null)
				{
					sLine = sLine.trim();
					if(sLine.startsWith("ROOT (excluding punc):"))
					{
						break;
					}
					else if(sLine.startsWith("Pos Accuracy:"))
					{
						int splitIndex = sLine.lastIndexOf("=");
						curTestPos = Double.parseDouble(sLine.substring(splitIndex+1));
					}
					else if(sLine.startsWith("UAS (excluding punc):"))
					{
						int splitIndex = sLine.lastIndexOf("=");
						curTestDep = Double.parseDouble(sLine.substring(splitIndex+1));
					}
				}
				
				if(curDevPos > 0 && curDevDep > 0 && curTestPos > 0 && curTestDep > 0)
				{
					if(curDevDep > bestDevDep)
					{
						bestIter = curIter;
						bestDevPos = curDevPos;
						bestDevDep = curDevDep;
						bestTestPos = curTestPos;
						bestTestDep = curTestDep;
					}
				}
				
				if(maxIter > 0 && curIter > maxIter) break;
			}			
		}
		in.close();
		
		if (bestIter > 0) {
			File file = new File(sFile);
			out.println(file.getName()
					+ String.format(
							"\tbestIter = %d\tbestDevPos = %f\tbestDevDep = %f\tbestTestPos = %f\tbestTestDep = %f,",
							bestIter, bestDevPos, bestDevDep, bestTestPos,
							bestTestDep));
			out.flush();
		}
		

	}

}
