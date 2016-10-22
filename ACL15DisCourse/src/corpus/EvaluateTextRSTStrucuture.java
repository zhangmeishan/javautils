package corpus;

import java.io.*;


public class EvaluateTextRSTStrucuture {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		
		BufferedReader reader_gold = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		
		BufferedReader reader_pred = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1])));
		
		PrintWriter output = new PrintWriter(System.out);
		
		if(args.length > 2)
		{
			output = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(args[2]), "UTF-8"), false);
		}
		
		
		EvaluateMetrics metric = new EvaluateMetrics();
		RSTDisCourseTree disTree_gold = new RSTDisCourseTree();
		RSTDisCourseTree disTree_pred = new RSTDisCourseTree();
		
		while(disTree_gold.LoadInstanceFromSingleFile(reader_gold, true, true)
		   && disTree_pred.LoadInstanceFromSingleFile(reader_pred, true, true))
		{
			disTree_gold.Evaluation(disTree_pred, metric);
		}
		
		metric.print(output);
		reader_gold.close();
		reader_pred.close();
		output.close();
	}

}
