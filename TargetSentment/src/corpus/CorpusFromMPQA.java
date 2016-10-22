package corpus;

import java.io.*;


public class CorpusFromMPQA {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		
		String sLineSentence = null;
		String lineTarget = null;
		String lineAttitute = null;
		String lineSentiment = null;
		
		while ((sLineSentence = in.readLine()) != null) {
			sLineSentence = sLineSentence.trim();
			if(sLineSentence.equals(""))continue;
			
		
			
			while( (lineTarget = in.readLine()) != null)
			{
				lineTarget = lineTarget.trim();
				if(!lineTarget.startsWith("Target:"))
				{
					sLineSentence = sLineSentence + " " + lineTarget.trim();
				}
				else{
					break;
				}
			}
			
			while( (lineAttitute = in.readLine()) != null)
			{
				lineAttitute = lineAttitute.trim();
				if(!lineAttitute.startsWith("Attitude:"))
				{
					lineTarget = lineTarget + " " + lineAttitute.trim();
				}
				else{
					break;
				}
			}
			
			while( (lineSentiment = in.readLine()) != null)
			{
				lineSentiment = lineSentiment.trim();
				if(!lineSentiment.startsWith("Sentiment:"))
				{
					lineAttitute = lineAttitute + " " + lineSentiment.trim();
				}
				else{
					break;
				}
			}
			
			
			if(!lineTarget.startsWith("Target:") || !lineAttitute.startsWith("Attitude:") || !lineSentiment.startsWith("Sentiment:")){
				System.out.println(lineTarget);
				System.out.println(lineAttitute);
				System.out.println(lineSentiment);
				continue;
			}
			
			String strTarget = lineTarget.substring(7).trim();
			
			int targetIndexStart = sLineSentence.indexOf(strTarget);
			//int targetIndexEnd = targetIndexStart + strTarget.length();
			if(targetIndexStart == -1)
			{
				System.out.println("Target is not found: " + lineTarget);
				System.out.println("Sentence: " + sLineSentence);
				continue;
			}
			else
			{
				sLineSentence = sLineSentence.replace(strTarget, " " + strTarget + " " );
			}
			sLineSentence = sLineSentence.trim();
			String[] words = sLineSentence.split("\\s+");
			String[] targets = strTarget.split("\\s+");
			String sentiment = lineSentiment.substring(10).trim();
			String resultedSentiment = "neural";
			
			int startIndex = FindTargetIndex(words, targets);
			if(startIndex == -1)
			{
				System.out.println("Target is not found: " + lineTarget);
				System.out.println("Sentence: " + sLineSentence);
				continue;
			}
			int endIndex = startIndex + targets.length;
			if(sentiment.equalsIgnoreCase("neg")){
				resultedSentiment = "negative";
			}
			else if(sentiment.equalsIgnoreCase("pos")){
				resultedSentiment = "positive";
			}
			else{
				System.out.println("error sentiment: " + sentiment);
				continue;
			}
			
			
			String[] labels = new String[words.length];
			for(int idx = 0; idx < words.length; idx++) {
				
				if(idx == startIndex){
					labels[idx] = "b-" + resultedSentiment;
				}
				else if (idx > startIndex && idx < endIndex){
					labels[idx] = "i-" + resultedSentiment;
				}
				else{
					labels[idx] = "o";
				}
			}
			
			
			for(int idx = 0; idx < words.length; idx++) {
				out.println(words[idx] + " " + labels[idx]);
			}
			
			out.println();		
		}
			
		in.close();
		out.close();
		
		
		
	}
	
	
	public static int FindTargetIndex(String[] words, String[] targets){
		for(int i = 0; i + targets.length <= words.length; i++){
			boolean bMatch = true;
			for(int j = 0; j < targets.length; j++){
				if(!words[i+j].equals(targets[j]))
				{
					bMatch = false;
					break;
				}
			}
			
			if(bMatch){
				return i;
			}
		}
				
		return -1;
	}

}
