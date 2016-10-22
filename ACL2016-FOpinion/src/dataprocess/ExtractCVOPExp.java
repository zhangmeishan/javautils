package dataprocess;

import java.io.*;
import java.util.*;

import corpus.DepInstance;

public class ExtractCVOPExp {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        String inputFolder = args[4];
        File file = new File(inputFolder);       
        String[] subFilenames = file.list();
        
        Map<String, Set<String>> corpusNames = new HashMap<String, Set<String>>();
        Set<String> allCorpusNames = new HashSet<String>();
        
        String sLine = "";
        
        for (String subFilename : subFilenames) {
            String entirePath =  String.format("%s%s%s", inputFolder, File.separator,
                    subFilename);
            int lastDot = subFilename.lastIndexOf(".");
            
            String suffixKey = subFilename.substring(lastDot+1);
            
            if(corpusNames.containsKey(suffixKey)){
                System.out.println("Suffix Key " + suffixKey + " diplicated!!");
                continue;
            }
            
            corpusNames.put(suffixKey, new HashSet<String>());
            
            BufferedReader idReader = new BufferedReader(new InputStreamReader(new FileInputStream(entirePath), "UTF8"));
            while ((sLine = idReader.readLine()) != null) {
                sLine = sLine.trim().toLowerCase();
                if (!sLine.isEmpty()) {
                    corpusNames.get(suffixKey).add(sLine);
                    allCorpusNames.add(sLine);
                }
            }
    
            idReader.close();
        }

        for(String suffixKey : corpusNames.keySet()){
            BufferedReader in_sent = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));
            BufferedReader in_dse = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF8"));
            BufferedReader in_ese = new BufferedReader(new InputStreamReader(new FileInputStream(args[2]), "UTF8"));
            PrintWriter out_train = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[3]+".train." + suffixKey), "UTF-8"));
            PrintWriter out_test = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[3]+".test." + suffixKey), "UTF-8"));
    
            String firstLine = "";
            String dse_describe = "", ese_describe = "", curPolarity = "", curIntensity = "", curDseIntensity = "", curImplicit = "";
            List<String> parsedInputs = new ArrayList<String>();
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
    
                if ((dse_describe = in_dse.readLine()) == null) {
                    System.out.println("Error Parsing Tree.....");
                    break;
                }
    
                if ((ese_describe = in_ese.readLine()) == null) {
                    System.out.println("Error Parsing Tree.....");
                    break;
                }
    
                if (!allCorpusNames.contains(headunits[3].toLowerCase())) {
                    continue;
                }
    
                PrintWriter out = out_train;
                if(corpusNames.get(suffixKey).contains(headunits[3].toLowerCase())){
                    out = out_test;
                }
    
                List<String> labels = new ArrayList<String>();
                for (int idx = 0; idx < parsedInputs.size(); idx++) {
                    labels.add("o\to\to");
                }
    
                dse_describe = dse_describe.trim();
    
                if (!dse_describe.isEmpty()) {
                    String[] dseBlocks = dse_describe.split("\t");
    
                    for (int idx = 0; idx < dseBlocks.length; idx++) {
                        String[] curBlockUnits = dseBlocks[idx].split(";");
                        String[] strdseIDs = curBlockUnits[0].trim().split(",");
                        int dseStartId = Integer.parseInt(strdseIDs[0].trim());
                        int dseEndId = Integer.parseInt(strdseIDs[1].trim());
    
                        curPolarity = "";
                        curIntensity = "";
                        curDseIntensity = "";
                        curImplicit = "";
                        for (int idy = 1; idy < curBlockUnits.length; idy++) {
                            if (curBlockUnits[idy].trim().startsWith("polarity=")) {
                                curPolarity = curBlockUnits[idy].trim().substring("polarity=".length());
                            }
                            if (curBlockUnits[idy].trim().startsWith("intensity=")) {
                                curIntensity = curBlockUnits[idy].trim().substring("intensity=".length());
                            }
                            if (curBlockUnits[idy].trim().startsWith("dseintensity=")) {
                                curDseIntensity = curBlockUnits[idy].trim().substring("dseintensity=".length());
                            }
                            if (curBlockUnits[idy].trim().startsWith("implicit=")) {
                                curImplicit = curBlockUnits[idy].trim().substring("implicit=".length());
                            }
                        }
    
                        if (curImplicit.equals("true")) {
                            continue;
                        }
    
                        if (curPolarity.isEmpty()) {
                            curPolarity = curDseIntensity;
                        }
    
                        if (curPolarity.isEmpty() || curPolarity.equals("high") || curPolarity.equals("low") || curIntensity.isEmpty()
                                || curIntensity.equals("neutral")) {
                            continue;
                        }
                        
                        if(curPolarity.equals("both") || curPolarity.equals("uncertain-both") || curPolarity.equals("uncertain-neutral")){
                            curPolarity = "neutral";
                        }
                        
                        if(curPolarity.equals("uncertain-positive") ){
                            curPolarity = "positive";
                        }
                        
                        if(curPolarity.equals("uncertain-negative") ){
                            curPolarity = "negative";
                        }
                        
                        if(curIntensity.equals("extreme")){
                            curIntensity = "high";
                        }
    
                        labels.set(dseStartId, String.format("b-dse\tb-%s\tb-%s", curPolarity, curIntensity));
                        for (int idy = dseStartId + 1; idy <= dseEndId; idy++) {
                            labels.set(idy, String.format("i-dse\ti-%s\ti-%s", curPolarity, curIntensity));
                        }
    
                    }
                }
    
                ese_describe = ese_describe.trim();
    
                if (!ese_describe.isEmpty()) {
                    String[] eseBlocks = ese_describe.split("\t");
    
                    for (int idx = 0; idx < eseBlocks.length; idx++) {
                        String[] curBlockUnits = eseBlocks[idx].split(";");
                        String[] streseIDs = curBlockUnits[0].trim().split(",");
                        int eseStartId = Integer.parseInt(streseIDs[0].trim());
                        int eseEndId = Integer.parseInt(streseIDs[1].trim());
                        
                        boolean bValid = true;
                        for (int idy = eseStartId; idy <= eseEndId; idy++) {
                           String curLabel = labels.get(idy);
                           if(!curLabel.equals("o\to\to")){
                               bValid = false;
                           }
                        }
                        
                        if(!bValid)continue;
    
                        curPolarity = "";
                        curIntensity = "";
                        for (int idy = 1; idy < curBlockUnits.length; idy++) {
                            if (curBlockUnits[idy].trim().startsWith("polarity=")) {
                                curPolarity = curBlockUnits[idy].trim().substring("polarity=".length());
                            }
                            if (curBlockUnits[idy].trim().startsWith("intensity=")) {
                                curIntensity = curBlockUnits[idy].trim().substring("intensity=".length());
                            }
                        }
    
                        if (curPolarity.isEmpty() || curPolarity.equals("high") || curPolarity.equals("low") || curIntensity.isEmpty()
                                || curIntensity.equals("neutral")) {
                            continue;
                        }
                        
                        if(curPolarity.equals("both") || curPolarity.equals("uncertain-both") || curPolarity.equals("uncertain-neutral")){
                            curPolarity = "neutral";
                        }
                        
                        if(curPolarity.equals("uncertain-positive") ){
                            curPolarity = "positive";
                        }
                        
                        if(curPolarity.equals("uncertain-negative") ){
                            curPolarity = "negative";
                        }
                        
                        if(curIntensity.equals("extreme")){
                            curIntensity = "high";
                        }
    
                        labels.set(eseStartId, String.format("b-ese\tb-%s\tb-%s", curPolarity, curIntensity));
                        for (int idy = eseStartId + 1; idy <= eseEndId; idy++) {
                            labels.set(idy, String.format("i-ese\ti-%s\ti-%s", curPolarity, curIntensity));
                        }
    
                    }
                }
    
                for (int idx = 0; idx < parsedInputs.size(); idx++) {
                    out.println(parsedInputs.get(idx) + "\t" + labels.get(idx));
                }
    
                out.println();
            }
    
            in_sent.close();
            in_dse.close();
            in_ese.close();
            out_train.close();
            out_test.close();
        }


    }

}
