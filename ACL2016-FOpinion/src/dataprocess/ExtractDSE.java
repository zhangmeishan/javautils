package dataprocess;

import java.io.*;
import java.util.*;

import corpus.DepInstance;

public class ExtractDSE {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        Set<String> corpusNames = new HashSet<String>();
        String sLine = "";

        BufferedReader idReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[3]), "UTF8"));
        while ((sLine = idReader.readLine()) != null) {
            sLine = sLine.trim().toLowerCase();
            if (!sLine.isEmpty()) {
                corpusNames.add(sLine);
            }
        }

        idReader.close();

        BufferedReader in_sent = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));
        BufferedReader in_dse = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[2]), "UTF-8"));

        String firstLine = "";
        String dse_describe = "", curPolarity = "", curIntensity = "";
        List<String> parsedInputs = new ArrayList<String>();
        int sentNum = 0;
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

            if (!corpusNames.contains(headunits[3].toLowerCase())) {
                continue;
            }

            sentNum++;

            dse_describe = dse_describe.trim();

            List<String> labels = new ArrayList<String>();
            for (int idx = 0; idx < parsedInputs.size(); idx++) {
                labels.add("o\to\to");
            }

            if (!dse_describe.isEmpty()) {
                String[] dseBlocks = dse_describe.split("\t");

                for (int idx = 0; idx < dseBlocks.length; idx++) {
                    String[] curBlockUnits = dseBlocks[idx].split(";");
                    String[] strdseIDs = curBlockUnits[0].trim().split(",");
                    int dseStartId = Integer.parseInt(strdseIDs[0].trim());
                    int dseEndId = Integer.parseInt(strdseIDs[1].trim());

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
                }
            }

            for (int idx = 0; idx < parsedInputs.size(); idx++) {
                out.println(parsedInputs.get(idx) + "\t" + labels.get(idx));
            }

            out.println();
        }

        in_sent.close();
        in_dse.close();
        out.close();

        System.out.println(String.format("Total Sent Num: %d", sentNum));
        System.out.println();
        System.out.println("Polarity ");
        for (String curKey : polarityNum.keySet()) {
            System.out.println(String.format("%s: %d", curKey, polarityNum.get(curKey)));
        }
        
        System.out.println();
        System.out.println("Intensity ");
        for (String curKey :intensityNum.keySet()) {
            System.out.println(String.format("%s: %d", curKey, intensityNum.get(curKey)));
        }


    }

}
