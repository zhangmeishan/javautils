package corpus.martins;

import java.io.*;
import java.util.*;

import corpus.*;

public class Martins2Dep {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));

        int targetNum = 0;
        int opNum = 0;
        int agentNum = 0;
        int nonprojectiveNum = 0;
        Map<String, Integer> labelSet = new HashMap<String, Integer>();
        
        String firstLine = "";
        String sLine = "";
        List<String> parsedInputs = new ArrayList<String>();

        while ((firstLine = in.readLine()) != null) {
            firstLine = firstLine.trim().toLowerCase();
            if (firstLine.isEmpty())
                continue;

            parsedInputs.clear();
            while ((sLine = in.readLine()) != null) {
                sLine = sLine.trim();
                if (sLine.isEmpty()) {
                    break;
                }
                parsedInputs.add(sLine);
            }

            if (parsedInputs.size() == 0) {
                System.out.println("Please check the corpus: no parsed trees.");
                break;
            }

            String[][] parsedInputs2Units = new String[parsedInputs.size()][];
            for (int idx = 0; idx < parsedInputs.size(); idx++) {
                parsedInputs2Units[idx] = parsedInputs.get(idx).split("\\s+");
                if (parsedInputs2Units[idx].length != 10) {
                    System.out.println("Please check the corpus: column does not equal 10.");
                }
            }

            List<String> zhangyue_outs = new ArrayList<String>();
            DepInstance tempInst = new DepInstance();
            tempInst.parseStringByMartins(parsedInputs2Units);
            tempInst.toZhangYueString(zhangyue_outs);
            if (zhangyue_outs.size() != parsedInputs.size()) {
                System.out.println("Error Parsing Tree.....");
                break;
            }

            Map<String, Span> dses = new HashMap<String, Span>();
            Map<String, Span> agents = new HashMap<String, Span>();
            Map<String, Span> targets = new HashMap<String, Span>();
            //Map<String, String> agent2dseId = new HashMap<String, String>();
            //Map<String, String> target2dseId = new HashMap<String, String>();

            // direct subjective expressions
            for (int idposition = 0; idposition < parsedInputs2Units.length; idposition++) {
                if (!parsedInputs2Units[idposition][7].equals("_")) {
                    String[] finedUnits = parsedInputs2Units[idposition][7].split("\\|");
                    for (int idz = 0; idz < finedUnits.length; idz++) {
                        int keyStart = -1;
                        String spankernel = "-1";
                        if (finedUnits[idz].startsWith("*DS-")) {
                            keyStart = "*DS-".length();
                            spankernel = String.format("%d", idposition);
                        } else if (finedUnits[idz].startsWith("DS-")) {
                            keyStart = "DS-".length();
                        }
                        int keyEnd = finedUnits[idz].indexOf("_", keyStart + 1);
                        if (keyStart == -1 || keyEnd == -1 || keyStart > keyEnd) {
                            System.out.println("Error corpus annotation towards dse");
                            continue;
                        }
                        String curKey = finedUnits[idz].substring(keyStart, keyEnd);

                        if (dses.containsKey(curKey)) {
                            if (dses.get(curKey).start > idposition) {
                                dses.get(curKey).start = idposition;
                            } else if (dses.get(curKey).end < idposition) {
                                dses.get(curKey).end = idposition;
                            } else {
                                System.out.println("DSE Span assignation seems strange....");
                                continue;
                            }

                            if (dses.get(curKey).type.equals("-1")) {
                                dses.get(curKey).type = spankernel;
                            }
                        } else {
                            dses.put(curKey, new Span(idposition, idposition, spankernel));
                        }
                    }

                }
            }

            // agent
            for (int idposition = 0; idposition < parsedInputs2Units.length; idposition++) {
                if (!parsedInputs2Units[idposition][8].equals("_")) {
                    String[] finedUnits = parsedInputs2Units[idposition][8].split("\\|");
                    for (int idz = 0; idz < finedUnits.length; idz++) {
                        int keyStart = -1;
                        String spankernel = "-1";
                        if (finedUnits[idz].startsWith("*A-")) {
                            keyStart = "*A-".length();
                            spankernel = String.format("%d", idposition);
                        } else if (finedUnits[idz].startsWith("A-")) {
                            keyStart = "A-".length();
                        }
                        if (keyStart == -1) {
                            System.out.println("Error corpus annotation towards agent");
                            continue;
                        }
                        String curAgentKey = finedUnits[idz].substring(keyStart);

                        if (agents.containsKey(curAgentKey)) {
                            if (agents.get(curAgentKey).start > idposition) {
                                agents.get(curAgentKey).start = idposition;
                            } else if (agents.get(curAgentKey).end < idposition) {
                                agents.get(curAgentKey).end = idposition;
                            } else {
                                System.out.println("Agent Span assignation seems strange....");
                                continue;
                            }

                            if (agents.get(curAgentKey).type.equals("-1")) {
                                agents.get(curAgentKey).type = spankernel;
                            }
                        } else {
                            agents.put(curAgentKey, new Span(idposition, idposition, spankernel));
                        }

                    }

                }
            }

            // target
            for (int idposition = 0; idposition < parsedInputs2Units.length; idposition++) {
                if (!parsedInputs2Units[idposition][9].equals("_")) {
                    String[] finedUnits = parsedInputs2Units[idposition][9].split("\\|");
                    for (int idz = 0; idz < finedUnits.length; idz++) {
                        int keyStart = -1;
                        String spankernel = "-1";
                        if (finedUnits[idz].startsWith("*T-")) {
                            keyStart = "*T-".length();
                            spankernel = String.format("%d", idposition);
                        } else if (finedUnits[idz].startsWith("T-")) {
                            keyStart = "T-".length();
                        }
                        if (keyStart == -1) {
                            System.out.println("Error corpus annotation towards target");
                            continue;
                        }
                        String curTargetKey = finedUnits[idz].substring(keyStart);

                        if (targets.containsKey(curTargetKey)) {
                            if (targets.get(curTargetKey).start > idposition) {
                                targets.get(curTargetKey).start = idposition;
                            } else if (targets.get(curTargetKey).end < idposition) {
                                targets.get(curTargetKey).end = idposition;
                            } else {
                                System.out.println("Target Span assignation seems strange....");
                                continue;
                            }

                            if (targets.get(curTargetKey).type.equals("-1")) {
                                targets.get(curTargetKey).type = spankernel;
                            }
                        } else {
                            targets.put(curTargetKey, new Span(idposition, idposition, spankernel));
                        }

                    }
                }

            }

            DepOpinion curOP = new DepOpinion(tempInst);
            List<Dependency> deps = new ArrayList<Dependency>();
            // dse
            for (String dseKey : dses.keySet()) {
                int dseStartId = dses.get(dseKey).start;
                int dseEndId = dses.get(dseKey).end;
                int dseHeadId = curOP.dep.getSpanLastHead(dseStartId, dseEndId) + 1;
                int spanHeadId = Integer.parseInt(dses.get(dseKey).type) + 1;
                if (spanHeadId == 0) {
                    System.out.println("Cur span does not find a head....");
                }
                String opLabel = "-OP-";
                if (dseHeadId == spanHeadId) {
                    opLabel = opLabel + "match";
                } else {
                    opLabel = opLabel + "unmatch";
                }

                deps.add(new Dependency(spanHeadId, 0, opLabel, dseStartId+1, dseEndId+1));
            }

            for (String agentKey : agents.keySet()) {
                int agentStartId = agents.get(agentKey).start;
                int agentEndId = agents.get(agentKey).end;
                int agentHeadId = curOP.dep.getSpanLastHead(agentStartId, agentEndId) + 1;
                int spanHeadId = Integer.parseInt(agents.get(agentKey).type) + 1;
                if (spanHeadId == 0) {
                    System.out.println("Cur span does not find a head....");
                }
                String agentLabel = "-AGENT-";
                if (agentHeadId == spanHeadId) {
                    agentLabel = agentLabel + "match-";
                } else {
                    agentLabel = agentLabel + "unmatch-";
                }

                int splitIndex = agentKey.indexOf("-");
                String curDSEKey = agentKey.substring(0, splitIndex);

                if (dses.containsKey(curDSEKey)) {
                    int dseHeadId = Integer.parseInt(dses.get(curDSEKey).type) + 1;
                    deps.add(new Dependency(spanHeadId, dseHeadId, agentLabel, agentStartId+1, agentEndId+1));
                }
            }

            for (String targetKey : targets.keySet()) {
                int targetStartId = targets.get(targetKey).start;
                int targetEndId = targets.get(targetKey).end;
                int targetHeadId = curOP.dep.getSpanLastHead(targetStartId, targetEndId) + 1;
                int spanHeadId = Integer.parseInt(targets.get(targetKey).type) + 1;
                if (spanHeadId == 0) {
                    System.out.println("Cur span does not find a head....");
                }
                String targetLabel = "-TARGET-";
                if (targetHeadId == spanHeadId) {
                    targetLabel = targetLabel + "match-";
                } else {
                    targetLabel = targetLabel + "unmatch-";
                }
                int attributeStart = targetKey.indexOf("_");
                int polarityStart = targetKey.indexOf("_", attributeStart+1);
                if (attributeStart == -1) {
                    targetLabel = targetLabel + "-strange";
                    System.out.println("Target key maybe error.....");
                } else {
                    targetLabel = targetLabel + targetKey.substring(polarityStart + 1, polarityStart + 4);
                }

                int splitIndex = targetKey.indexOf("-");
                String curDSEKey = targetKey.substring(0, splitIndex);

                if (dses.containsKey(curDSEKey)) {
                    int dseHeadId = Integer.parseInt(dses.get(curDSEKey).type) + 1;
                    deps.add(new Dependency(spanHeadId, dseHeadId, targetLabel, targetStartId+1, targetEndId+1));
                }
            }

            if (!Dependency.Sort(deps)) {
                continue;
            }

            /*
            if (deps.size() == 0) {
                continue;
            }
            */

            int[] headNums = new int[curOP.wordsize() + 1];
            int[] childNums = new int[curOP.wordsize() + 1];
            for (int idx = 0; idx <= curOP.wordsize(); idx++) {
                headNums[idx] = 0;
                childNums[idx] = 0;
            }
            //Map<Integer, Integer> head2spanStart = new HashMap<Integer, Integer>();
            for (int idx = 0; idx < deps.size(); idx++) {
                curOP.op_arcs.add(deps.get(idx));
                headNums[deps.get(idx).child]++;
                childNums[deps.get(idx).head]++;
                if(!labelSet.containsKey(deps.get(idx).label)){
                    labelSet.put(deps.get(idx).label, 0);
                }
                labelSet.put(deps.get(idx).label, labelSet.get(deps.get(idx).label)+1);

                if (deps.get(idx).label.startsWith("-OP-")) {
                    opNum++;
                } else if (deps.get(idx).label.startsWith("-TARGET-")) {
                    targetNum++;
                } else if (deps.get(idx).label.startsWith("-AGENT-")) {
                    agentNum++;
                } else {
                    System.out.println("impossible....");
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
                //int idy = curOP.op_heads.size();
                if(deps.get(idx).child != curOP.op_forms.get(idx+1)){
                    System.out.println("Error in algorithm, impossible.....");
                }
                curOP.op_forms_start.add(deps.get(idx).cs_start);
                curOP.op_forms_end.add(deps.get(idx).cs_end);
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
            
            for(int idx = 0; idx < op_zhangyue_outs.size(); idx++){
                out.println(op_zhangyue_outs.get(idx));
            }
            out.println();
        }
        
        System.out.println("non projective opinion dependency tree number: " + String.format("%d", nonprojectiveNum));
        System.out.println(String.format("OP num: %d, agent num: %d, target num: %d", opNum, agentNum, targetNum));
        System.out.println("Total opinion dependency size: " + String.format("%d", labelSet.size()));
        for(String oneLabel : labelSet.keySet()){
            System.out.println(String.format("%s %d", oneLabel, labelSet.get(oneLabel)));
        }
        in.close();
        out.close();

    }

}
