package mason.corpus.tool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LogProcessing {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader logReader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		int iteration = 0;
		
		String sLine = null;
		String sLinePrev = "";
		
		while ((sLine = logReader.readLine()) != null) {
			sLine = sLine.trim();
			
			if(sLinePrev.equals("dev:") && sLine.startsWith("Recall:")){
				String[] smallunits = sLine.split("\\s+");
				double accuracy = Double.parseDouble(smallunits[smallunits.length-1]);
				System.out.println(String.format("( %d, %.2f)", iteration, accuracy*100));
				iteration++;
				if(iteration > 20) break;
			}
			
				
			sLinePrev = sLine;
		}
		
			
		logReader.close();
	}

}
