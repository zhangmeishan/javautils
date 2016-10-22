package microblog;

import java.util.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;

public class Main {

	
	/**
	 * train command:  -train <train-file> <model-file> <number of iterations> <bNewTrain>
	 * test command:   -test <input-file>  <model-file> <output-file>
	 * @param args
	 */
	public static void main(String[] args) {
	
		// TODO Auto-generated method stub
		//CommandLineParser parser = new PosixParser();
		//try {
			
		//	CommandLine cmd = parser.parse(opts, args);
		/*
		if(args.length<7 || args.length>11){
			System.out.println("parameter error, please input again!");
			System.out.println("-train <train_file> <number of train> <model_file> <number of iterations> <bNewTrain> <search_width> <output_path");
			System.out.println("-test <test-file> <number of test> <model-file>  <output-file> <evaluation_file> <search_width>");
			System.out.println("<train_file> <number of train> <model_file> <number of iterations> <bNewTrain> <search_width> <test-file>  <number of test>");
			
		    return;
		}*/
		if(args[0].trim().equals("-train")){
			String train_file= args[1].trim();
			String model_file= args[2].trim();
			int number_of_train = Integer.parseInt(args[3].trim());
			int number_of_iterations= Integer.parseInt(args[4].trim());			
			boolean bNewTrain= Boolean.parseBoolean(args[5].trim());
			int search_width = Integer.parseInt(args[6].trim());
			String output_path= args[7];
			BeamSearch bs= new BeamSearch(train_file,number_of_train, model_file, number_of_iterations, bNewTrain, search_width, output_path);		
			try {
				bs.trainProcess();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(args[0].trim().equals("-test")){
			String test_file= args[1].trim();
			String model_file= args[2].trim();
			int number_of_test = Integer.parseInt(args[3].trim());
			String out_file= args[4].trim();
			String evaluation_file= args[5].trim();
			int search_width = Integer.parseInt(args[6].trim());
			BeamSearch bs= new BeamSearch(test_file,number_of_test, model_file, out_file, evaluation_file, search_width);	
			bs.testProcess();
		}else {//ͬʱѵ������
			String train_file= args[0].trim();
			String dev_file= args[1].trim();
			String test_file= args[2].trim();
			String model_file= args[3].trim();
			String output_path = args[4].trim();
			int number_of_iterations= Integer.parseInt(args[5].trim());	
			
			int search_width = 16;			
			int number_of_train = -1;
			int number_of_test = -1;
			int number_of_dev = -1;
			boolean bNewTrain= true;
			// 
			//int search_width = Integer.parseInt(args[6].trim());			
			//int number_of_train = Integer.parseInt(args[7].trim());
			//int number_of_test = Integer.parseInt(args[8].trim());	
			//boolean bNewTrain= Boolean.parseBoolean(args[9].trim());
			
			//String charpos_file = args[9].trim();
			//String wordpos_file = args[10].trim();
			BeamSearch bs= new BeamSearch(train_file,number_of_train, model_file, number_of_iterations, 
					bNewTrain, search_width, test_file, number_of_test,
					dev_file, number_of_dev, output_path);	
			try {
				bs.trainDevTestProcess();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
