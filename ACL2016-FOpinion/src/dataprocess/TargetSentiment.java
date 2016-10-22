package dataprocess;

import java.io.*;
import java.util.*;

public class TargetSentiment {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String mainFolder = args[0];
		String output = args[1];
		
		List<String> inDocs = new ArrayList<String>();
		List<String> inLREs = new ArrayList<String>();
		List<String> inGsents = new ArrayList<String>();
		List<String> inMetas = new ArrayList<String>();
		
		String docsFolder = mainFolder + File.separator + "docs";
		String manannsFolder = mainFolder + File.separator + "man_anns";
		String metaannsFolder = mainFolder + File.separator + "meta_anns";
		
		List<File> docFiles = getListFiles(docsFolder);
		
		for(File oneFile : docFiles){
			String curFileName = oneFile.toString();			
			String subname = curFileName.substring(docsFolder.length());
			String curLREFileName = manannsFolder + subname + File.separator + "gateman.mpqa.lre.2.0";
			String curGsentFileName = manannsFolder + subname + File.separator + "gatesentences.mpqa.2.0";
			String curMetaFileName = metaannsFolder + subname;
			
			inDocs.add(curFileName);
			inLREs.add(curLREFileName);
			inGsents.add(curGsentFileName);
			inMetas.add(curMetaFileName);			
		}
		
		int totalDocFileNum = inDocs.size();
		
		System.out.println(String.format("Total doc file number: %d", totalDocFileNum));
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		
		for(int idx = 0; idx < totalDocFileNum; idx++)
		{
			String inDoc = inDocs.get(idx);
			String inLRE = inLREs.get(idx);
			String inGSent = inGsents.get(idx);
			String inMeta = inMetas.get(idx);
			
			process(inDoc, inLRE, inGSent, inMeta, out);
		}
		
		
		out.close();
		
	}
	
	
	
	public static void process(String inDoc, String inLRE, String inGSent, 
			String inMeta, PrintWriter out) throws Exception{
		File inDocFile = new File(inDoc);
		
		FileInputStream inDocFis = new FileInputStream(inDocFile);
		byte[] data = new byte[(int) inDocFile.length()];
		inDocFis.read(data);
		inDocFis.close();
		String inDocStr = new String(data, "UTF-8");
		
	
		File inLREFile = new File(inLRE);
		File inGSentFile = new File(inGSent);
		if(!inLREFile.exists() || !inGSentFile.exists())
		{
			System.out.println("Please check file exist: ");
			System.out.println(inLRE);
			System.out.println(inGSent);
			return;
		}
		

		BufferedReader inLREReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inLRE), "UTF8"));
		BufferedReader inGSentReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(inGSent), "UTF8"));		
		//BufferedReader inMetaReader = new BufferedReader(new InputStreamReader(
		//		new FileInputStream(inMeta), "UTF8"));
		

		String sLine = "";
		
		int[] sentIndexs = new int[inDocStr.length()];
		
		for(int idx = 0; idx < inDocStr.length(); idx++){
			sentIndexs[idx] = -1;
		}
		
		List<String> sents = new ArrayList<String>();
		
		while( (sLine = inGSentReader.readLine()) != null)
		{
			sLine = sLine.trim();
			String[] smallunits = sLine.split("\\s+");
			String[] startend = smallunits[1].split(",");
			if(startend.length != 2)
			{
				System.out.println("error line: " + sLine);
				continue;
			}
			int start = Integer.parseInt(startend[0]);
			int end = Integer.parseInt(startend[1]);
			
			int curSentId = sents.size();
			
			for(int idx = start; idx < end; idx++){
				sentIndexs[idx] = curSentId;
			}
			
			sents.add(inDocStr.substring(start, end));
			//out.println(oneSent);		
		}
		
		Map<String, String> attitudeTargets = new HashMap<String, String>();
		Map<String, String> attitudePhrases = new HashMap<String, String>();
		Map<String, String> attitudeTypes = new HashMap<String, String>();
		Map<String, Integer> attitudeSentIds = new HashMap<String, Integer>();
		
		Map<String, Integer> targetSentIds = new HashMap<String, Integer>();
		Map<String, String> targetPhrases = new HashMap<String, String>();
		
		while( (sLine = inLREReader.readLine()) != null)
		{
			sLine = sLine.trim().toLowerCase();
			if(sLine.startsWith("#")) continue;
			
			String[] smallunits = sLine.split("\\s+");
			if(smallunits.length < 5) continue;
			
			if(smallunits[3].equalsIgnoreCase("gate_target"))
			{
				String[] startend = smallunits[1].split(",");
				if(startend.length != 2)
				{
					System.out.println("error line: " + sLine);
					continue;
				}
				int start = Integer.parseInt(startend[0]);
				int end = Integer.parseInt(startend[1]);
				String curPhrase = inDocStr.substring(start, end);
				String targetId = smallunits[4].substring(4, smallunits[4].length()-1);
				
				targetPhrases.put(targetId, curPhrase);
				targetSentIds.put(targetId, sentIndexs[start]);
			}
			else if(smallunits.length > 6 && smallunits[3].equalsIgnoreCase("gate_attitude") 
					&& smallunits[5].startsWith("attitude-type") && smallunits[5].indexOf("sentiment-") != -1 
					&& smallunits[4].startsWith("target-link")){
				String[] startend = smallunits[1].split(",");
				if(startend.length != 2)
				{
					System.out.println("error line: " + sLine);
					continue;
				}
				int start = Integer.parseInt(startend[0]);
				int end = Integer.parseInt(startend[1]);
				String curPhrase = inDocStr.substring(start, end);
				
				String attitudeId = smallunits[6].substring(4, smallunits[6].length()-1);
				String attitudeType = smallunits[5].substring(15, smallunits[5].length()-1);
				String attitudeTarget = smallunits[4].substring(13, smallunits[4].length()-1);
				if(attitudeTarget.indexOf(",") != -1) attitudeTarget = attitudeTarget.substring(0, attitudeTarget.indexOf(","));
				attitudeTargets.put(attitudeId, attitudeTarget);
				attitudeTypes.put(attitudeId, attitudeType);
				attitudePhrases.put(attitudeId, curPhrase);
				attitudeSentIds.put(attitudeId, sentIndexs[start]);
			}
			else
			{
				continue;
			}
			
		}
		
		for(String attitudeId : attitudeTargets.keySet())
		{
			String targetId = attitudeTargets.get(attitudeId);
			if(targetId.equalsIgnoreCase("none"))continue;
			if(!targetSentIds.containsKey(targetId)){
				System.out.println("please check corpus: " + inLRE);
				System.out.println("attitudeId: " + attitudeId + "\ttargetId: " + targetId);
				continue;
			}
			int targetSentId = targetSentIds.get(targetId);
			int attitudeSentId = attitudeSentIds.get(attitudeId);
			if(targetSentId != attitudeSentId || targetSentId < 0 || attitudeSentId < 0){
				System.out.println("please check corpus: " + inLRE);
				System.out.println("attitudeId: " + attitudeId + "\ttargetId: " + targetId);
				continue;
			}
			out.println(sents.get(targetSentId));
			out.println("Target:\t" + targetPhrases.get(targetId));
			out.println("Attitude:\t" + attitudePhrases.get(attitudeId));
			out.println("Sentiment:\t" + attitudeTypes.get(attitudeId).substring(10));
			out.println();
		}
		
		inLREReader.close();
		inGSentReader.close();
		
	}

	

	public static ArrayList<File> getListFiles(Object obj) {
		File directory = null;
		if (obj instanceof File) {
			directory = (File) obj;
		} else {
			directory = new File(obj.toString());
		}
		ArrayList<File> files = new ArrayList<File>();
		if (directory.isFile()) {
			files.add(directory);
			return files;
		} else if (directory.isDirectory()) {
			File[] fileArr = directory.listFiles();
			for (int i = 0; i < fileArr.length; i++) {
				File fileOne = fileArr[i];
				files.addAll(getListFiles(fileOne));
			}
		}
		return files;
	}
}
