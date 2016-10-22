package corpus;


import java.io.*;
import java.util.*;

public class EvaluationSegmentation {
	
	
	public static void main(String[] args) throws Exception{
		
		Set<String> ivWords = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8"));
		String sLine = "";
		
		while((sLine = reader.readLine()) != null)
		{
			sLine = sLine.trim();
			String[] smallunits = sLine.split("\\s+");
			
			for(int idx = 0; idx < smallunits.length; idx++){
				ivWords.add(smallunits[idx]);
			}			
		}
		
		reader.close();
		

		BufferedReader greader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF-8"));
		BufferedReader preader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[2]), "UTF-8"));
		String gsLine = "", psLine = "";
		
		int correctW = 0, goldW = 0, predW = 0, correctOOV = 0, allOOV = 0;
		
		while((gsLine = greader.readLine()) != null  && (psLine = preader.readLine()) != null)
		{
			gsLine = gsLine.trim();
			psLine = psLine.trim();
			
			String[] gWords = gsLine.split("\\s+");
			String[] pWords = psLine.split("\\s+");
			
			Set<String> gwordBounds = new HashSet<String>();
			Set<String> pwordBounds = new HashSet<String>();
			
			getBoundary(gWords, gwordBounds);
			getBoundary(pWords, pwordBounds);
			
			for(String curPWord : pwordBounds){
				int endMark = curPWord.indexOf("]");
				String theWord = curPWord.substring(endMark+1);
				if(gwordBounds.contains(curPWord)){
					correctW++;
					if(!ivWords.contains(theWord)){
						correctOOV++;
					}
				}
				
				if(!ivWords.contains(theWord)){
					allOOV++;
				}
			}
			
			predW += pWords.length;
			goldW += gWords.length;
			
		}
		
		greader.close();
		preader.close();
		
	    System.out.println(String.format("Seg Performance: R=%d/%d=%.4f, P=%d/%d=%.4f, F=%.4f, R(OOV)=%d/%d=%.4f", 
			    correctW,goldW,correctW*1.0/goldW,
			    correctW,predW,correctW*1.0/predW,
			    correctW*2.0/(goldW +predW),
			    correctOOV, allOOV, correctOOV*1.0/allOOV
			    ));
	}
	
	
	public static void getBoundary(String[] theWords, Set<String> wordBounds){
		int startp = 0;
		int endp = 0;
		wordBounds.clear();
		for(int idx = 0; idx < theWords.length; idx++){
			endp = startp + theWords[idx].length() - 1;
			wordBounds.add(String.format("[%d,%d]%s", startp, endp, theWords[idx]));
			//bounds.add(String.format("[%d,%d]", startp, endp));
			startp = endp+1;
		}
		
		
	}
}
