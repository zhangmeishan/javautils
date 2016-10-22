package dataprocess;

import java.io.*;
import java.util.*;

import corpus.DepInstance;
import corpus.DepOpinion;
import corpus.Dependency;

public class CorpusGeneration {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        BufferedReader in_sent = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));
        BufferedReader in_dse = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF8"));
        BufferedReader in_atti = new BufferedReader(new InputStreamReader(new FileInputStream(args[2]), "UTF8"));
        BufferedReader in_target = new BufferedReader(new InputStreamReader(new FileInputStream(args[3]), "UTF8"));
        BufferedReader in_agent = new BufferedReader(new InputStreamReader(new FileInputStream(args[4]), "UTF8"));

        PrintWriter out_train = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[5] + ".train"), "UTF-8"));

        PrintWriter out_dev = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[5] + ".dev"), "UTF-8"));

        PrintWriter out_test = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[5] + ".test"), "UTF-8"));
        
        PrintWriter out_invalid = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[5] + ".invalid"), "UTF-8"));

        String firstLine = "";
        String sLine = "";
        List<String> parsedInputs = new ArrayList<String>();

        String dse_describe = "", atti_describe = "", target_describe = "", agent_describe = "";

        int pem_dep = 0, pem_span = 0, pem_total = 0;
        int pam_dep = 0, pam_span = 0, pam_total = 0;

        int invalidSentenceNum = 0;
        int targetNum = 0;
        int opNum = 0;
        int agentNum = 0;
        int nonprojectiveNum = 0;
        while ((firstLine = in_sent.readLine()) != null) {
            firstLine = firstLine.trim().toLowerCase();
            if (firstLine.isEmpty())
                continue;
            String[] headunits = firstLine.split("\\s+");
            if (headunits.length != 4) {
                System.out.println("error first line: " + firstLine);
                break;
            }
            parsedInputs.clear();
            while ((sLine = in_sent.readLine()) != null) {
                sLine = sLine.trim();
                if (sLine.isEmpty()) {
                    break;
                }
                parsedInputs.add(sLine);
            }

            if (parsedInputs.size() == 0) {
                break;
            }

            List<String> zhangyue_outs = new ArrayList<String>();
            DepInstance tempInst = new DepInstance();
            tempInst.parseString(parsedInputs, true);
            tempInst.toZhangYueString(zhangyue_outs);
            if (zhangyue_outs.size() != parsedInputs.size()) {
                System.out.println("Error Parsing Tree.....");
                break;
            }

            DepOpinion curOP = new DepOpinion(tempInst);

            if ((dse_describe = in_dse.readLine()) == null || (atti_describe = in_atti.readLine()) == null || (target_describe = in_target.readLine()) == null
                    || (agent_describe = in_agent.readLine()) == null) {
                System.out.println("Error Parsing Tree.....");
                break;
            }

            dse_describe = dse_describe.trim();
            atti_describe = atti_describe.trim();
            target_describe = target_describe.trim();
            agent_describe = agent_describe.trim();

            Map<String, String> attiTtarget = new HashMap<String, String>();

            if (!atti_describe.isEmpty()) {
                String[] attiBlocks = atti_describe.split("\t");
                for (int idx = 0; idx < attiBlocks.length; idx++) {
                    String[] curBlockUnits = attiBlocks[idx].split(";");
                    String curAtti = "", curTarget = "";
                    for (int idy = 1; idy < curBlockUnits.length; idy++) {
                        if (curBlockUnits[idy].trim().startsWith("id=")) {
                            curAtti = curBlockUnits[idy].trim().substring("id=".length());
                        }
                        if (curBlockUnits[idy].trim().startsWith("targetlink=")) {
                            curTarget = curBlockUnits[idy].trim().substring("targetlink=".length());
                        }
                    }

                    attiTtarget.put(curAtti.trim(), curTarget.trim());
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
                    targetTindex.put(curBlockUnits[1].trim().substring("id=".length()).trim(), curBlockUnits[0].trim());
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
                            curSrc = curBlockUnits[idy].trim().substring("src=".length());
                        }
                    }

                    srcTagent.put(curSrc.trim(), curBlockUnits[0].trim());
                }
            }

            
            if(atti_describe.isEmpty()){
                out_invalid.println("attitude empty: " + firstLine);
                continue;
            }
            
            
            if(dse_describe.isEmpty()){
                out_invalid.println("dse empty: " + firstLine);
                continue;
            }
            
            String[] dseBlocks = dse_describe.split("\t");

            List<Dependency> deps = new ArrayList<Dependency>();

            boolean bValidSentence = true;
            for (int idx = 0; idx < dseBlocks.length; idx++) {
               //if(dseBlocks[idx].indexOf("insubstantial=c") != -1) continue;
                String[] curBlockUnits = dseBlocks[idx].split(";");
                String[] strdseIDs = curBlockUnits[0].trim().split(",");
                int dseStartId = Integer.parseInt(strdseIDs[0].trim());
                int dseEndId = Integer.parseInt(strdseIDs[1].trim());
                int dseHeadId = curOP.dep.getSpanLastHead(dseStartId, dseEndId) + 1;
                if(dseHeadId == 0)bValidSentence = false;
                String curLabel = "-OP-";

                String curPolarity = "";
                for (int idy = 1; idy < curBlockUnits.length; idy++) {
                    if (curBlockUnits[idy].trim().startsWith("src=")) {
                        String dse_agent = curBlockUnits[idy].trim().substring("src=".length());
                        dse_agent = dse_agent.trim();
                        if (dse_agent.isEmpty() || !srcTagent.containsKey(dse_agent)) {
                            continue;
                        }
                        String[] stragtIDs = srcTagent.get(dse_agent).split(",");
                        int agtStartId = Integer.parseInt(stragtIDs[0].trim());
                        int agtEndId = Integer.parseInt(stragtIDs[1].trim());
                        int agtHeadId = curOP.dep.getSpanLastHead(agtStartId, agtEndId) + 1;
                        
                        if(agtHeadId == 0)bValidSentence = false;
                        
                        // here we need add an arc
                        deps.add(new Dependency(agtHeadId, dseHeadId, "-is-from-", -1, -1));
                        //agentNum++;

                    }
                    if (curBlockUnits[idy].trim().startsWith("polarity=")) {
                        curPolarity = curBlockUnits[idy].trim().substring("polarity=".length());
                    }
                    if (curBlockUnits[idy].trim().startsWith("attrlink=")) {
                        String dse_attis = curBlockUnits[idy].trim().substring("attrlink=".length());
                        if (dse_attis.isEmpty()) {
                            continue;
                        }

                        String[] curAttis = dse_attis.split(",");
                        if(curAttis.length > 1){
                            //System.out.println("processing??");
                        }
                        for (String curAtti : curAttis) {
                            if (attiTtarget.containsKey(curAtti.trim()) && !attiTtarget.get(curAtti.trim()).isEmpty()) {
                                String curTarget = attiTtarget.get(curAtti.trim());
                                String[] curTargets = curTarget.trim().split(",");
                                for(String oneTarget : curTargets){
                                    if (targetTindex.containsKey(oneTarget.trim())) {
                                        String[] strtargetIDs = targetTindex.get(oneTarget.trim()).split(",");
                                        int targetStartId = Integer.parseInt(strtargetIDs[0].trim());
                                        int targetEndId = Integer.parseInt(strtargetIDs[1].trim());
                                        int targetHeadId = curOP.dep.getSpanLastHead(targetStartId, targetEndId) + 1;
                                        // here we need add an arc
                                        deps.add(new Dependency(targetHeadId, dseHeadId, "-is-about-", -1, -1));
                                        //targetNum++;
                                        if(targetHeadId == 0)bValidSentence = false;
                                    }
                                }
                            }
                            else{
                                //System.out.println("strange....??");
                            }
                        }
                    }
                }
                
                deps.add(new Dependency(dseHeadId, 0, curLabel + curPolarity, -1, -1));
                //opNum++;
            }
            
            if(!bValidSentence){
                invalidSentenceNum++;
                continue;
            }
            
            if(!Dependency.Sort(deps)){
                continue;
            }
            
            if(deps.size() == 0 ){
                invalidSentenceNum++;
                continue;
            }
            
            int[] headNums = new int[curOP.wordsize()+1];
            int[] childNums = new int[curOP.wordsize()+1];
            for(int idx = 0; idx <= curOP.wordsize(); idx++){
                headNums[idx] = 0;
                childNums[idx] = 0;
            }
            
            for(int idx = 0; idx < deps.size(); idx++){
                curOP.op_arcs.add(deps.get(idx));             
                headNums[deps.get(idx).child]++;
                childNums[deps.get(idx).head]++;
                
                if(headunits[0].equals("train")
                || headunits[0].equals("dev")
                || headunits[0].equals("test")){
                    if(deps.get(idx).label.startsWith("-OP-")){
                        opNum++;
                    }
                    else if(deps.get(idx).label.equals("-is-about-")){
                        targetNum++;
                    }
                    else if(deps.get(idx).label.equals("-is-from-")){
                        agentNum++;
                    }
                    else{
                        System.out.println("impossible....");
                    }
                }
            }
            
            Map<Integer, Integer> formAsROOT = new HashMap<Integer, Integer>();
            formAsROOT.put(0, 1);
            for(int idx = 0; idx < curOP.dep.size(); idx++){
                int curId = idx+1;
                if(headNums[curId] > 0 || childNums[curId] > 0){
                    curOP.op_forms.add(curId);
                    formAsROOT.put(curId, curOP.op_forms.size());
                    for(int idy = 1; idy < headNums[curId]; idy++){
                        curOP.op_forms.add(curId);
                    }
                }
                
            }
            
            for(int idx = 0; idx < deps.size(); idx++){
                int curHeadId =  deps.get(idx).head;
                if(!formAsROOT.containsKey(curHeadId)){
                    System.out.println("Error in algorithm, impossible.....");
                    return;
                }
                curOP.op_heads.add(formAsROOT.get(curHeadId));
                curOP.op_deprels.add(deps.get(idx).label);
            }
            
            //check projective
            boolean bProjective = true;
            for(int idx = 0; idx < curOP.op_heads.size(); idx++){
                int idy = curOP.op_heads.get(idx) - 1;
                
                int min = idx > idy ? idy : idx;
                int max = idx < idy ? idy : idx;
                
                for(int idk = min+1; idk < max; idk++){
                    int curHead = curOP.op_heads.get(idk) - 1;
                    if(curHead > max || curHead < min){
                        //System.out.println("non-projective, please check why.");
                        bProjective = false;
                    }
                }
                
            }
            if(!bProjective){
                nonprojectiveNum++;                
            }
            curOP.bProjective = bProjective;
            List<String> op_zhangyue_outs = new ArrayList<String>();
            curOP.toZhangYueString(op_zhangyue_outs);
            if(headunits[0].equals("train") ) { // || headunits[0].equals("other")){
                for(int idx = 0; idx < op_zhangyue_outs.size(); idx++){
                    out_train.println(op_zhangyue_outs.get(idx));
                }
                out_train.println();
            }
            else if(headunits[0].equals("dev")){
                for(int idx = 0; idx < op_zhangyue_outs.size(); idx++){
                    out_dev.println(op_zhangyue_outs.get(idx));
                }
                out_dev.println();
            }
            else if(headunits[0].equals("test")){
                for(int idx = 0; idx < op_zhangyue_outs.size(); idx++){
                    out_test.println(op_zhangyue_outs.get(idx));
                }
                out_test.println();
            }


        }
        
        System.out.println("invalidSentenceNum: " + String.format("%d", invalidSentenceNum));
        System.out.println("non projective opinion dependency tree number: " + String.format("%d", nonprojectiveNum));
        System.out.println(String.format("OP num: %d, agent num: %d, target num: %d", opNum, agentNum, targetNum));
        in_sent.close();
        in_dse.close();
        in_atti.close();
        in_target.close();
        in_agent.close();
        out_train.close();
        out_dev.close();
        out_test.close();
        out_invalid.close();
        
    }

}
