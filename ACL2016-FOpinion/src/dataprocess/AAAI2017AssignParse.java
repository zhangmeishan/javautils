package dataprocess;


import java.io.*;
import java.util.*;

public class AAAI2017AssignParse {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        
        String sLine1, sLine2;
        BufferedReader in_sentID = new BufferedReader(new InputStreamReader(
                new FileInputStream(args[0]), "UTF8"));
        
        BufferedReader in_answer = new BufferedReader(new InputStreamReader(
                new FileInputStream(args[1]), "UTF8"));
        
        Map<String, List<String[]>>  orginInstances = new HashMap<String, List<String[]>>();
        
        while ((sLine1 = in_sentID.readLine()) != null){
            sLine1 = sLine1.trim();
            if(sLine1.isEmpty()) continue;
            String[] markedUnits = sLine1.toLowerCase().split("\\s+");
            
            if(markedUnits.length != 3){
                System.out.println(sLine1);
                continue;
            }
            
            String markedId = markedUnits[2] + "\t" + markedUnits[1];
            
            List<String[]> inst = new ArrayList<String[]>();
            
            while ((sLine2 = in_answer.readLine()) != null){
                sLine2 = sLine2.trim();
                if(sLine2.isEmpty()) break;
                
                String[] elems = sLine2.split("\\s+");
                
                if(elems.length != 3){
                    System.out.println(sLine2);
                    continue;
                }
                
                inst.add(elems);                
            }
            
            if(orginInstances.containsKey(markedId)){
                System.out.println("File name confilcts: " + sLine1);
            }
            orginInstances.put(markedId, inst);           
        }
                
        in_sentID.close();
        in_answer.close();
        
        System.out.println(orginInstances.size());
        

        BufferedReader in_parse = new BufferedReader(new InputStreamReader(
                new FileInputStream(args[2]), "UTF8"));
        PrintWriter out_answerWithSentID = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(args[3]), "UTF-8"));
        

        int totalToken = 0;
        int correctToken = 0;
        
        while ((sLine1 = in_parse.readLine()) != null){
            sLine1 = sLine1.trim();
            if(sLine1.isEmpty()) continue;
            
            String[] markedUnits = sLine1.toLowerCase().split("\\s+");
            
            if(markedUnits.length != 4){
                System.out.println(sLine1);
                continue;
            }
            
            String markedId = markedUnits[3] + "\t" + markedUnits[2];
            
            List<String[]> parsedInsts = new ArrayList<String[]>();
            
            while ((sLine2 = in_parse.readLine()) != null){
                sLine2 = sLine2.trim();
                if(sLine2.isEmpty()) break;
                
                String[] elems = sLine2.split("\\s+");
                
                if(elems.length != 4){
                    System.out.println(sLine2);
                    continue;
                }
                
                parsedInsts.add(elems);                
            }
            
            if(!orginInstances.containsKey(markedId)){
                System.out.println("Lose the sentence id: " + sLine1);
                continue;
            }
            
            List<String[]> insts = orginInstances.get(markedId);
            
            if(insts.size() != parsedInsts.size()){
                System.out.println("Error of length:\t" + sLine1);
                continue;
            }
            
            out_answerWithSentID.println(markedUnits[1] + "\t" + markedUnits[2] + "\t" + markedUnits[3]);          
            for(int idx = 0; idx < insts.size(); idx++){
                totalToken++;
                if(insts.get(idx)[1].equals(parsedInsts.get(idx)[1]) || 
                   (parsedInsts.get(idx)[0].startsWith("-") && parsedInsts.get(idx)[0].endsWith("B-") && parsedInsts.get(idx)[0].length() == 5)
                   || insts.get(idx)[1].equals("CD")  || insts.get(idx)[1].equals("FW")){
                    correctToken++;
                }
                else{
                    //System.out.println("Error:\t" + sLine1);
                    //System.out.println("insts:\t" + insts.get(idx)[0] + "\t" + insts.get(idx)[1] + "\t" + insts.get(idx)[2]);
                    //System.out.println("parsedInsts:\t" + parsedInsts.get(idx)[0] + "\t" + parsedInsts.get(idx)[1] + "\t" + parsedInsts.get(idx)[2] + "\t" + parsedInsts.get(idx)[3]);
                }
                
                String curOutline = parsedInsts.get(idx)[0] + "\t" + parsedInsts.get(idx)[1] + "\t" + insts.get(idx)[2];   
                out_answerWithSentID.println(curOutline);                
            }
            
            out_answerWithSentID.println();
            
        }
        
        
        
        in_parse.close();       
        out_answerWithSentID.close();
        
        System.out.println(String.format("Acc = %d/%d = %.4f", correctToken, totalToken, correctToken * 100.0 / totalToken));
    }

}
