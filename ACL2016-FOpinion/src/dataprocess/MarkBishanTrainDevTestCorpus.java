package dataprocess;

import java.io.*;
import java.util.*;

import corpus.DepInstance;

public class MarkBishanTrainDevTestCorpus {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        Set<String> org_trainCorpusNames = new HashSet<String>();
        String sLine = "";

        BufferedReader trainCorpusId = new BufferedReader(new InputStreamReader(new FileInputStream(args[2]), "UTF8"));
        while ((sLine = trainCorpusId.readLine()) != null) {
            sLine = sLine.trim().toLowerCase();
            if (!sLine.isEmpty()) {
                org_trainCorpusNames.add(sLine);
            }
        }

        trainCorpusId.close();

        Set<String> org_devCorpusNames = new HashSet<String>();

        BufferedReader devCorpusId = new BufferedReader(new InputStreamReader(new FileInputStream(args[3]), "UTF8"));
        while ((sLine = devCorpusId.readLine()) != null) {
            sLine = sLine.trim().toLowerCase();
            if (!sLine.isEmpty()) {
                org_devCorpusNames.add(sLine);
            }
        }

        devCorpusId.close();

        String firstLine = "";
        Set<String> allCorpusNames = new HashSet<String>();
        BufferedReader allCorpusId = new BufferedReader(new InputStreamReader(new FileInputStream(args[4]), "UTF8"));
        BufferedReader pasedSentenceIds = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));

        boolean bContainTarget = false;
        String curDocument = "";
        while ((firstLine = pasedSentenceIds.readLine()) != null) {
            firstLine = firstLine.trim().toLowerCase();
            if (firstLine.isEmpty())
                continue;
            String[] smallunits = firstLine.split("\\s+");
            if (smallunits.length != 3) {
                System.out.println("error first line: " + firstLine);
            }
            while ((sLine = pasedSentenceIds.readLine()) != null) {
                sLine = sLine.trim();
                if (sLine.isEmpty()) {
                    break;
                }
            }

            if (smallunits[1].equals("0")) {
                if (!curDocument.isEmpty() && bContainTarget) {
                    allCorpusNames.add(curDocument);
                }
                curDocument = "";
                bContainTarget = false;
            }

            sLine = allCorpusId.readLine();
            if (sLine == null) {
                System.out.println("error attitude file: " + firstLine);
            }

            curDocument = smallunits[2];
            sLine = sLine.trim();
            if (sLine.indexOf("targetlink=") != -1) {
                bContainTarget = true;
            }

        }

        allCorpusId.close();
        pasedSentenceIds.close();

        Set<String> trainCorpusNames = new HashSet<String>();
        Set<String> devCorpusNames = new HashSet<String>();
        Set<String> otherCorpusNames = new HashSet<String>();

        int trainNum = 0, devNum = 0, otherNum = 0;

        pasedSentenceIds = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));

        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));

        List<String> parsedInputs = new ArrayList<String>();
        while ((firstLine = pasedSentenceIds.readLine()) != null) {
            firstLine = firstLine.trim().toLowerCase();
            if (firstLine.isEmpty())
                continue;
            String[] smallunits = firstLine.split("\\s+");
            if (smallunits.length != 3) {
                System.out.println("error first line: " + firstLine);
            }
            parsedInputs.clear();
            while ((sLine = pasedSentenceIds.readLine()) != null) {
                sLine = sLine.trim();
                if (sLine.isEmpty()) {
                    break;
                }
                parsedInputs.add(sLine);
            }

            if (parsedInputs.size() > 0) {
                List<String> zhangyue_outs = new ArrayList<String>();
                DepInstance tempInst = new DepInstance();
                tempInst.parseString(parsedInputs, true);
                tempInst.toZhangYueString(zhangyue_outs);
                if (zhangyue_outs.size() != parsedInputs.size()) {
                    System.out.println("Error Parsing Tree.....");
                } else {
                    String headMark = "";

                    if (smallunits[1].equals("0")) {
                        if (allCorpusNames.contains(smallunits[2])) {
                            if (org_trainCorpusNames.contains(smallunits[2])) {
                                trainCorpusNames.add(smallunits[2]);
                            } else if (org_devCorpusNames.contains(smallunits[2])) {
                                devCorpusNames.add(smallunits[2]);
                            } else {
                                otherCorpusNames.add(smallunits[2]);
                            }

                        } else {
                            otherCorpusNames.add(smallunits[2]);
                        }
                    }

                    if (trainCorpusNames.contains(smallunits[2])) {
                        trainNum++;
                        headMark = "train ";
                    } else if (devCorpusNames.contains(smallunits[2])) {
                        devNum++;
                        headMark = "dev ";
                    } else if (otherCorpusNames.contains(smallunits[2])) {
                        otherNum++;
                        headMark = "other ";
                    } else {
                        System.out.println("Error first line: " + firstLine);
                    }

                    out.println(headMark + firstLine);

                    for (int idx = 0; idx < zhangyue_outs.size(); idx++) {
                        out.println(zhangyue_outs.get(idx));
                    }
                    out.println();
                }
            } else {
                System.out.println("Error sentence id: could not find its parsed tree!");
            }

        }

        pasedSentenceIds.close();
        out.close();

        System.out.println(String.format("Document num: train %d, dev %d, other %d", trainCorpusNames.size(), devCorpusNames.size(),
                otherCorpusNames.size()));

        System.out.println(String.format("Sentence num: train %d, dev %d, other %d", trainNum, devNum, otherNum));

    }

}
