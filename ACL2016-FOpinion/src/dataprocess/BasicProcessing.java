package dataprocess;

import java.io.*;
import java.util.*;

import corpus.Span;

public class BasicProcessing {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader in_sent = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		BufferedReader in_dse = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF8"));
		BufferedReader in_atti = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[2]), "UTF8"));
		BufferedReader in_target = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[3]), "UTF8"));
		BufferedReader in_agent = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[4]), "UTF8"));

		PrintWriter out_train = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[5]+".train"), "UTF-8"));
		
		PrintWriter out_dev = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[5]+".dev"), "UTF-8"));
		
		int[] train_dev = new int[9471];
		for(int idx = 0; idx < train_dev.length; idx++){
			train_dev[idx] = 1;
		}
		
		for(int idx = 6090; idx < 9466; idx++){
			train_dev[idx] = 0;
		}

		String rawSent = "", dse_describe = "", atti_describe = "", target_describe = "", agent_describe = "";

		int count = 0;
		while ((rawSent = in_sent.readLine()) != null
				&& (dse_describe = in_dse.readLine()) != null
				&& (atti_describe = in_atti.readLine()) != null
				&& (target_describe = in_target.readLine()) != null
				&& (agent_describe = in_agent.readLine()) != null) {

			rawSent = rawSent.trim();
			dse_describe = dse_describe.trim();
			atti_describe = atti_describe.trim();
			target_describe = target_describe.trim();
			agent_describe = agent_describe.trim();
			
			

			if(train_dev[count] == 1){
				processOneLine(rawSent, dse_describe, atti_describe,
						target_describe, agent_describe, out_train);
			}
			else{
				processOneLine(rawSent, dse_describe, atti_describe,
						target_describe, agent_describe, out_dev);
			}
			
			
			count++;


		}

		in_sent.close();
		in_dse.close();
		in_atti.close();
		in_target.close();
		in_agent.close();
		out_train.close();
		out_dev.close();

	}

	public static void processOneLine(String rawSent, String dse_describe,
			String atti_describe, String target_describe,
			String agent_describe, PrintWriter out) throws Exception {
		/*
		 * rawSent = rawSent.replace("'s", " 's"); rawSent =
		 * rawSent.replace("`t", " `t"); rawSent = rawSent.replace("'t", " 't");
		 * rawSent = rawSent.replace(" $", " $ "); rawSent =
		 * rawSent.replace("'re", " 're"); rawSent = rawSent.replace("'m",
		 * " 'm"); rawSent = rawSent.replace("'d", " 'd"); rawSent =
		 * rawSent.replace("'ve", " 've"); rawSent = rawSent.replace("'ll",
		 * " 'll"); List<String> sentwords = Twokenize.tokenize(rawSent);
		 */
		if(dse_describe.trim().isEmpty() || target_describe.trim().isEmpty())return;

		String[] sentwords = rawSent.split("\\s+");

		String[] dseBlocks = dse_describe.split("\t");
		Map<String, String> dseTsrc = new HashMap<String, String>();
		Map<String, String> dseTatti = new HashMap<String, String>();

		for (int idx = 0; idx < dseBlocks.length; idx++) {
			String[] curBlockUnits = dseBlocks[idx].split(";");
			for (int idy = 1; idy < curBlockUnits.length; idy++) {
				if (curBlockUnits[idy].trim().startsWith("src=")) {
					dseTsrc.put(curBlockUnits[0].trim(), curBlockUnits[idy]
							.trim().substring("src=".length()));
				}
				if (curBlockUnits[idy].trim().startsWith("attrlink=")) {
					dseTatti.put(curBlockUnits[0].trim(), curBlockUnits[idy]
							.trim().substring("attrlink=".length()));
				}
			}
		}

		Map<String, String> attiTtarget = new HashMap<String, String>();

		if (!atti_describe.isEmpty()) {
			String[] attiBlocks = atti_describe.split("\t");
			for (int idx = 0; idx < attiBlocks.length; idx++) {
				String[] curBlockUnits = attiBlocks[idx].split(";");
				String curAtti = "", curTarget = "";
				for (int idy = 1; idy < curBlockUnits.length; idy++) {
					if (curBlockUnits[idy].trim().startsWith("id=")) {
						curAtti = curBlockUnits[idy].trim().substring(
								"id=".length());
					}
					if (curBlockUnits[idy].trim().startsWith("targetlink=")) {
						curTarget = curBlockUnits[idy].trim().substring(
								"targetlink=".length());
					}
				}

				attiTtarget.put(curAtti, curTarget);
			}
		}

		Map<String, String> targetTindex = new HashMap<String, String>();
		if (!target_describe.isEmpty()) {
			String[] targetBlocks = target_describe.split("\t");
			for (int idx = 0; idx < targetBlocks.length; idx++) {
				String[] curBlockUnits = targetBlocks[idx].split(";");
				if (curBlockUnits.length != 2) {
					System.out.println("error");
				}
				targetTindex.put(
						curBlockUnits[1].trim().substring("id=".length()),
						curBlockUnits[0].trim());
			}
		}

		Map<String, String> srcTagent = new HashMap<String, String>();
		if (!agent_describe.isEmpty()) {
			String[] agentBlocks = agent_describe.split("\t");
			for (int idx = 0; idx < agentBlocks.length; idx++) {
				String[] curBlockUnits = agentBlocks[idx].split(";");
				String curSrc = "";
				for (int idy = 1; idy < curBlockUnits.length; idy++) {
					if (curBlockUnits[idy].trim().startsWith("src=")) {
						curSrc = curBlockUnits[idy].trim().substring(
								"src=".length());
					}
				}

				srcTagent.put(curSrc, curBlockUnits[0].trim());
			}
		}

		String[][] labels = new String[dseTsrc.size()][sentwords.length];
		//boolean[] bValid = new boolean[labels.length];
		//for (int idx = 0; idx < labels.length; idx++)
		//	bValid[idx] = true;
		int expressionId = 0;
		for (String curKey : dseTsrc.keySet()) {
			String[] twoUnits = curKey.split(",");
			int expression_start = Integer.parseInt(twoUnits[0]);
			int expression_end = Integer.parseInt(twoUnits[1]);

			List<Integer> target_starts = new ArrayList<Integer>();
			List<Integer> target_ends = new ArrayList<Integer>();

			if (!dseTatti.get(curKey).isEmpty() && attiTtarget.size() > 0) {
				String[] curAttis = dseTatti.get(curKey).split(",");
				for (String curAtti : curAttis) {
					if (attiTtarget.containsKey(curAtti.trim())
							&& !attiTtarget.get(curAtti.trim()).isEmpty()) {
						String curTarget = attiTtarget.get(curAtti);
						if (targetTindex.containsKey(curTarget)) {
							twoUnits = targetTindex.get(curTarget).split(",");
							target_starts.add(Integer.parseInt(twoUnits[0]));
							target_ends.add(Integer.parseInt(twoUnits[1]));
						}
					}
				}
			}

			int agent_start = -1;
			int agent_end = -1;
			if (!dseTsrc.get(curKey).isEmpty()) {
				String curSRC = dseTsrc.get(curKey);
				if (srcTagent.containsKey(curSRC)) {
					twoUnits = srcTagent.get(curSRC).split(",");
					agent_start = Integer.parseInt(twoUnits[0]);
					agent_end = Integer.parseInt(twoUnits[1]);
				}
			}

			for (int idx = 0; idx < sentwords.length; idx++) {
				labels[expressionId][idx] = "O";
			}

			labels[expressionId][expression_start] = "B-EXPR";
			for (int idx = expression_start + 1; idx <= expression_end; idx++) {
				labels[expressionId][idx] = "I-EXPR";
			}
			
			if (agent_start >= 0) {
				int newStart = agent_start;
				while(newStart <= agent_end && !labels[expressionId][newStart].equals("O")){
					newStart++;
				}
				
				int newEnd = agent_end;					
				while(newEnd >= newStart && !labels[expressionId][newEnd].equals("O")){
					newEnd--;
				}
						
				boolean bInvalidAgent = false;
				for (int idx = newStart; idx <= newEnd; idx++){
					if(!labels[expressionId][idx].equals("O")){
						//bValid[expressionId] = false;
						bInvalidAgent = true;
						break;
					}
				}
				
				if (!bInvalidAgent && newStart <= newEnd) {
					labels[expressionId][newStart] = "B-AGT";
					for (int idx = newStart + 1; idx <= newEnd; idx++) {
						labels[expressionId][idx] = "I-AGT";
					}
				}
			}

			if (target_starts.size() >= 0) {
				for (int idk = 0; idk < target_starts.size(); idk++) {
					int target_start = target_starts.get(idk);
					int target_end = target_ends.get(idk);

					int newStart = target_start;
					while(newStart <= target_end && !labels[expressionId][newStart].equals("O")){
						newStart++;
					}
					
					int newEnd = target_end;					
					while(newEnd >= newStart && !labels[expressionId][newEnd].equals("O")){
						newEnd--;
					}
					
					boolean bInvalidTarget = false;
					for (int idx = newStart; idx <= newEnd; idx++){
						if(!labels[expressionId][idx].equals("O")){
							//bValid[expressionId] = false;
							bInvalidTarget = true;
							break;
						}
					}
						
					if (!bInvalidTarget && newStart <= newEnd) {
						labels[expressionId][newStart] = "B-TGT";
						for (int idx = newStart + 1; idx <= newEnd; idx++) {
							labels[expressionId][idx] = "I-TGT";
						}
					}
				}
			}


			expressionId++;
		}

		/*
		 * String oneLine = "";
		 * 
		 * for (int idx = 0; idx < sentwords.length; idx++) { oneLine =
		 * sentwords[idx]; for (int idy = 0; idy < labels.length; idy++) {
		 * oneLine = oneLine + "\t" + labels[idy][idx]; } out.println(oneLine);
		 * }
		 */

/*				
		for (int idx = 0; idx < labels.length; idx++){
			if(!bValid[idx])continue;
			for (int idy = 0; idy < labels.length; idy++){
				if(idx == idy) continue;
				if(!bValid[idy])continue;
				int result = valid(labels, idx, idy);
				if(result >= 0){
					bValid[result] = false;
				}
			}
		}
		
		boolean validSent = false;
		
		for (int idx = 0; idx < labels.length; idx++){
			if(bValid[idx]){
				validSent = true;
			}
		}
		
		if(!validSent) return;
		
		String[] finalLabels = new String[sentwords.length];
		for (int idx = 0; idx < sentwords.length; idx++) {
			String curLabel = "O";
			int correctCol = -1;
			for (int idy = 0; idy < labels.length; idy++) {
				if (!bValid[idy])
					continue;
				if (!labels[idy][idx].equals(curLabel)) {
					if (curLabel.equals("O")) {
						curLabel = labels[idy][idx];
						correctCol = idy;
					} 
					else if (labels[idy][idx].equals("O")){
						
					}
					else{
						System.out.println("Strange error, please check");
					}
				}
				else{
					curLabel = labels[idy][idx];
					correctCol = idy;
				}
			}
			
			finalLabels[idx] = curLabel;
		}
		*/
			
		String[] finalLabels = finalLabel(labels);
		
		/*
		for (int idx = 0; idx < sentwords.length; idx++) {
			String curLabel = "O";
			int correctCol = -1;
			for (int idy = 0; idy < labels.length; idy++) {
				if (!bValid[idy])
					continue;
				if (!labels[idy][idx].equals(curLabel)) {
					if (curLabel.equals("O")) {
						curLabel = labels[idy][idx];
						correctCol = idy;
					} 
					else if (labels[idy][idx].equals("O")){
						
					}
					else {
						int curStart = FindStart(labels, idx, idy);
						int curEnd = FindEnd(labels, idx, idy);

						int correctStart = FindStart(labels, idx, correctCol);
						int correctEnd = FindEnd(labels, idx, correctCol);

						if (curStart == correctStart && curEnd == correctEnd) {

						} else if (curStart >= correctStart
								&& curEnd <= correctEnd) {
							bValid[correctCol] = false;
							curLabel = labels[idy][idx];
							correctCol = idy;
						} else if (curStart <= correctStart
								&& curEnd >= correctEnd) {
							bValid[idy] = false;
						} else {
							System.out.println("Strange error, please check");
							validSent = false;
						}

					}
				}
				else{
					curLabel = labels[idy][idx];
					correctCol = idy;
				}
			}

			finalLabels[idx] = curLabel;
		}
		*/
		
		
		//bi to bmes
		String[] refinedLabels = new String[sentwords.length];
		for (int idx = 0; idx < sentwords.length; idx++) {
			if(finalLabels[idx].startsWith("B-")){
				String kernelLabel = finalLabels[idx].substring(2);
				int start = idx;
				int idy = start + 1;
				while(idy < sentwords.length && finalLabels[idy].equals("I-" + kernelLabel)){
					idy++;
				}
				int end = idy -1;
				
				if(start == end){
					refinedLabels[idx] = "S-" + kernelLabel;
				}
				else{
					refinedLabels[start] = "B-" + kernelLabel;
					refinedLabels[end] = "E-" + kernelLabel;
					for(int idz = start + 1; idz < end; idz++){
						refinedLabels[idz] = "M-" + kernelLabel;
					}
				}
				idx = end;
			}
			else if(finalLabels[idx].startsWith("I-")){
				System.out.println("label error, check");
			}
			else{
				refinedLabels[idx] = finalLabels[idx];
			}
		}
		
		/*
		int[] heads = new int[sentwords.length];
		for (int idx = 0; idx < sentwords.length; idx++){
			if(refinedLabels[idx].equals("E-AGT") || refinedLabels[idx].equals("S-AGT")
			 || refinedLabels[idx].equals("E-TGT") || refinedLabels[idx].equals("S-TGT")){
				int curhead = 0;
				for (int idy = 0; idy < labels.length; idy++) {
					if(!bValid[idy])continue;
					if(!labels[idy][idx].equals("O")){
						for(int idz = 0; idz <  sentwords.length; idz++){
							if(labels[idy][idz].endsWith("-EXPR") &&
								((idz + 1) == labels[idy].length	|| !labels[idy][idz+1].endsWith("-EXPR"))){
								if(curhead != idz + 1){
									if(curhead > 0){
										System.out.println("multi heads, please check");
									}
									else{
										curhead = idz + 1;
									}
								}
							}
						}
					}
				}
				if(curhead == 0){
					System.out.println("Could find a head, strange, please check");
				}
				heads[idx] = curhead;
			}
			else
			{
				heads[idx] = 0;
			}
		}*/
		

		String oneLine = "";

		for (int idx = 0; idx < sentwords.length; idx++) {
			int index1 = sentwords[idx].lastIndexOf("/");
			String unit2 = sentwords[idx].substring(index1 + 1);
			String leftPart = sentwords[idx].substring(0, index1);
			int index2 = leftPart.lastIndexOf("/");
			String unit1 = leftPart.substring(index2 + 1);
			String unit0 = leftPart.substring(0, index2);

			oneLine =unit0 + "\t" +unit1 + "\t" + refinedLabels[idx];
			//oneLine = oneLine + String.format("\t%d\t_\t_\t_", heads[idx]);
			
			for (int idy = 0; idy < labels.length; idy++) {
				oneLine = oneLine + "\t" + labels[idy][idx];
			}
			
			out.println(oneLine);
		}

		out.println();
		out.flush();
	}

	public static int FindStart(String[][] labels, int posi, int col) {
		if (posi >= labels[0].length || posi < 0)
			return -1;
		String curLabel = labels[col][posi];

		if (posi == 0) {
			if (curLabel.startsWith("B-")) {
				return posi;
			} else {
				return -1;
			}
		}

		if (curLabel.startsWith("B-")) {
			return posi;
		} else if (curLabel.startsWith("I-")) {
			return FindStart(labels, posi - 1, col);
		} else {
			return -1;
		}
	}

	public static int FindEnd(String[][] labels, int posi, int col) {
		if (posi >= labels[0].length || posi < 0)
			return -1;

		String curLabel = labels[col][posi];

		if (posi == labels[col].length - 1) {
			if (curLabel.startsWith("B-") || curLabel.startsWith("I-")) {
				return posi;
			} else {
				return -1;
			}
		}

		if (!curLabel.startsWith("B-") && !curLabel.startsWith("I-")) {
			return -1;
		}

		String nextLabel = labels[col][posi + 1];

		if (nextLabel.startsWith("B-")) {
			return posi;
		} else if (nextLabel.startsWith("I-")) {
			return FindEnd(labels, posi + 1, col);
		} else {
			return posi;
		}
	}
	
	public static int valid(String[][] labels, int col1, int col2){
		
		int bCol1Invalid = 0;
		int bCol2Invalid = 0;
		int span1 = labels[col1].length;
        int span2 = labels[col1].length;
        int entity1 = 0;
        int entity2 = 0;
		boolean bConflict = false;
		for(int idz = 0; idz <  labels[col1].length; idz++){
			if(!labels[col1][idz].equals(labels[col2][idz])
			&& !labels[col1][idz].equals("O") && !labels[col2][idz].equals("O")){
				bConflict = true;
				int col1Start = FindStart(labels, idz, col1);
				int col1End = FindEnd(labels, idz, col1);

				int col2Start = FindStart(labels, idz, col2);
				int col2End = FindEnd(labels, idz, col2);
				
				int dist1 = col1End - col1Start + 1;
				int dist2 = col2End - col2Start + 1;
				
				

				if(dist1 > dist2){
					bCol1Invalid++;
				}
				else if(dist1 < dist2){
					bCol2Invalid++;
				}
				else if(col1Start < col2Start || col1End > col2End){
					bCol1Invalid++;
				}
				else if(col2Start < col1Start || col2End > col1End){
					bCol2Invalid++;
				}
				
				idz = col2End > col1End ? col1End : col2End;
							
			}
			

		}
		
		for(int idz = 0; idz <  labels[col1].length; idz++){
			if(labels[col1][idz].equals("O")){
				span1--;
			}
			if(labels[col2][idz].equals("O")){
				span2--;
			}
			
			if(labels[col1][idz].startsWith("B-")){
				entity1++;
			}
			if(labels[col2][idz].startsWith("B-")){
				entity2++;
			}
		}
		
				
		int invalid = -1;
		if(bConflict) {
			if(bCol1Invalid == bCol2Invalid){
				invalid = span1 >= span2 ? col1 : col2;
				//return -2;
				//invalid = -2;
			}
			else if(bCol1Invalid > bCol2Invalid){
				invalid = col1;
			}
			else {
				invalid = col2;
			}

		}
		
		
		return invalid;
	}
	

	
	public static String[] finalLabel(String[][] labels){
		
		
		int length = labels[0].length;
		String[] finalLabels= new String[length];
		
		for (int idz = 0; idz < length; idz++) {
			boolean containSpan = false;
			for(int idy = 0; idy < labels.length; idy++){
				 if(!labels[idy][idz].equals("O")){
					 containSpan = true;
					 break;
				 }
			}
			
			if(!containSpan){
				finalLabels[idz] = "O";
				continue;
			}
			
			Span bestSpan = new Span();
			int bestCol = -1;
			
			for(int idy = 0; idy < labels.length; idy++){
				if(!labels[idy][idz].equals("O")){
					int curStart = FindStart(labels, idz, idy);
					int curEnd = FindEnd(labels, idz, idy);
					String curType = labels[idy][idz].substring(2);
					if(bestCol < 0){
						bestSpan.end = curEnd;
						bestSpan.start = curStart;
						bestSpan.type = curType;
						bestCol = idy;
					}
					else if (curEnd - curStart < bestSpan.end - bestSpan.start){						
						for(int idk = bestSpan.start; idk <= bestSpan.end; idk++){
							labels[bestCol][idk] = "O";
						}						
						bestSpan.end = curEnd;
						bestSpan.start = curStart;
						bestSpan.type = curType;
						bestCol = idy;
					}
					else {
						for(int idk = curStart; idk <= curEnd; idk++){
							labels[idy][idk] = "O";
						}
					}
				}
			}
			
			if(idz > bestSpan.start) finalLabels[idz] = "I-" + bestSpan.type;
			else finalLabels[idz] = "B-" + bestSpan.type;
					
		}
				
		return finalLabels;
	}

}
