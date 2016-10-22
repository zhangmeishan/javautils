package dataprocess;

import java.io.*;
import java.util.*;

import corpus.DepInstance;

public class ExtractOPExp {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        Set<String> corpusNames = new HashSet<String>();
        String sLine = "";

        BufferedReader idReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[4]), "UTF8"));
        while ((sLine = idReader.readLine()) != null) {
            sLine = sLine.trim().toLowerCase();
            if (!sLine.isEmpty()) {
                corpusNames.add(sLine);
            }
        }

        idReader.close();

        BufferedReader in_sent = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));
        BufferedReader in_dse = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF8"));
        BufferedReader in_ese = new BufferedReader(new InputStreamReader(new FileInputStream(args[2]), "UTF8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[3]), "UTF-8"));

        String firstLine = "";
        String dse_describe = "", ese_describe = "", curPolarity = "", curIntensity = "", curDseIntensity = "", curImplicit = "";
        List<String> parsedInputs = new ArrayList<String>();
        int sentNum = 0;
        int dseNum = 0;
        int eseNum = 0;
        Map<String, Integer> polarityNum = new HashMap<String, Integer>();
        Map<String, Integer> intensityNum = new HashMap<String, Integer>();
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

            if (!corpusNames.contains(headunits[3].toLowerCase())) {
                continue;
            }

            sentNum++;

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

                    if (!polarityNum.containsKey(curPolarity)) {
                        polarityNum.put(curPolarity, 0);
                    }
                    polarityNum.put(curPolarity, polarityNum.get(curPolarity) + 1);

                    if (!intensityNum.containsKey(curIntensity)) {
                        intensityNum.put(curIntensity, 0);
                    }
                    intensityNum.put(curIntensity, intensityNum.get(curIntensity) + 1);
                    dseNum++;
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

                    if (!polarityNum.containsKey(curPolarity)) {
                        polarityNum.put(curPolarity, 0);
                    }
                    polarityNum.put(curPolarity, polarityNum.get(curPolarity) + 1);

                    if (!intensityNum.containsKey(curIntensity)) {
                        intensityNum.put(curIntensity, 0);
                    }
                    intensityNum.put(curIntensity, intensityNum.get(curIntensity) + 1);

                    eseNum++;
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
        out.close();

        System.out.println(String.format("Total Sent Num: %d", sentNum));
        System.out.println(String.format("Total DSE Num: %d", dseNum));
        System.out.println(String.format("Total ESE Num: %d", eseNum));
        System.out.println();
        System.out.println("Polarity ");
        for (String curKey : polarityNum.keySet()) {
            System.out.println(String.format("%s: %d", curKey, polarityNum.get(curKey)));
        }

        System.out.println();
        System.out.println("Intensity ");
        for (String curKey : intensityNum.keySet()) {
            System.out.println(String.format("%s: %d", curKey, intensityNum.get(curKey)));
        }

    }

}
