package dataprocess;

import java.io.*;
import java.util.*;

public class DocSelection {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Set<String> validDocs = new HashSet<String>();
		BufferedReader in_valid = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[7]), "UTF8"));
		String sLine = "";
		while ((sLine = in_valid.readLine()) != null)
		{
			sLine = sLine.trim();
			
			if(!sLine.isEmpty()){
				validDocs.add(sLine);
			}
		}
		
		in_valid.close();
		
		BufferedReader in_sentIndex = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		BufferedReader in_sent = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF8"));
		BufferedReader in_dse = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[2]), "UTF8"));
		BufferedReader in_ese = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[3]), "UTF8"));
		BufferedReader in_atti = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[4]), "UTF8"));
		BufferedReader in_target = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[5]), "UTF8"));
		BufferedReader in_agent = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[6]), "UTF8"));



		PrintWriter out_sentIndex = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[0] + ".sel"), "UTF8"));
		PrintWriter out_sent = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1] + ".sel"), "UTF8"));
		PrintWriter out_dse = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[2] + ".sel"), "UTF8"));
		PrintWriter out_ese = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[3] + ".sel"), "UTF8"));
		PrintWriter out_atti = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[4] + ".sel"), "UTF8"));
		PrintWriter out_target = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[5] + ".sel"), "UTF8"));
		PrintWriter out_agent = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[6] + ".sel"), "UTF8"));
		

		String  sentIndex = "", rawSent = "", dse_describe = "", ese_describe = "", 
				atti_describe = "",  target_describe = "", agent_describe = "";
		
		List<String>  sentIndexList = new ArrayList<String>();
		List<String>  rawSentList = new ArrayList<String>(); 
		List<String>  dse_describeList = new ArrayList<String>(); 
		List<String>  ese_describeList = new ArrayList<String>(); 
		List<String>  atti_describeList = new ArrayList<String>();  
		List<String>  target_describeList = new ArrayList<String>(); 
		List<String>  agent_describeList = new ArrayList<String>();

		int count = 0;

		String curDocIndex = "";
		boolean curDocValid = false;
		while ((sentIndex = in_sentIndex.readLine()) != null
				&& (rawSent = in_sent.readLine()) != null
				&& (dse_describe = in_dse.readLine()) != null
				&& (ese_describe = in_ese.readLine()) != null
				&& (atti_describe = in_atti.readLine()) != null
				&& (target_describe = in_target.readLine()) != null
				&& (agent_describe = in_agent.readLine()) != null) {

			sentIndex = sentIndex.trim();
			rawSent = rawSent.trim();
			dse_describe = dse_describe.trim();
			ese_describe = dse_describe.trim();
			atti_describe = atti_describe.trim();
			target_describe = target_describe.trim();
			agent_describe = agent_describe.trim();
			
			String[] indexUnits = sentIndex.split("\\s+");
			
			if(!indexUnits[0].equals(curDocIndex)){
				if(sentIndexList.size() > 0){
					if(curDocValid){
						for(int idx = 0; idx < sentIndexList.size(); idx++){
							out_sentIndex.println(sentIndexList.get(idx));
							out_sent.println(rawSentList.get(idx));
							out_dse.println(dse_describeList.get(idx));
							out_ese.println(ese_describeList.get(idx));
							out_atti.println(atti_describeList.get(idx));
							out_target.println(target_describeList.get(idx));
							out_agent.println(agent_describeList.get(idx));
						}
						count++;
					}
				}

				sentIndexList = new ArrayList<String>();
				rawSentList = new ArrayList<String>(); 
				dse_describeList = new ArrayList<String>(); 
				ese_describeList = new ArrayList<String>(); 
				atti_describeList = new ArrayList<String>();  
				target_describeList = new ArrayList<String>(); 
				agent_describeList = new ArrayList<String>();

				curDocIndex = indexUnits[0];
				curDocValid = false;
			}
			
			//if(!atti_describe.isEmpty() || !target_describe.isEmpty()){
			if(validDocs.contains(indexUnits[2])){
				curDocValid = true;
			}
			
			sentIndexList.add(sentIndex);
			rawSentList.add(rawSent);
			dse_describeList.add(dse_describe);
			ese_describeList.add(ese_describe);
			atti_describeList.add(atti_describe);
			target_describeList.add(target_describe);
			agent_describeList.add(agent_describe);
		}
		
		if(sentIndexList.size() > 0){
			if(curDocValid){
				for(int idx = 0; idx < sentIndexList.size(); idx++){
					out_sentIndex.println(sentIndexList.get(idx));
					out_sent.println(rawSentList.get(idx));
					out_dse.println(dse_describeList.get(idx));
					out_ese.println(ese_describeList.get(idx));
					out_atti.println(atti_describeList.get(idx));
					out_target.println(target_describeList.get(idx));
					out_agent.println(agent_describeList.get(idx));
				}
				count++;
			}
		}

		in_sentIndex.close();
		in_sent.close();
		in_dse.close();
		in_ese.close();
		in_atti.close();
		in_target.close();
		in_agent.close();

		out_sentIndex.close();
		out_sent.close();
		out_dse.close();
		out_ese.close();
		out_atti.close();
		out_target.close();
		out_agent.close();
		System.out.println(count);
	}



}
