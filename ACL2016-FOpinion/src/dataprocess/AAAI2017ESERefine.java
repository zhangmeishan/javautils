package dataprocess;


import java.io.*;
import java.util.*;

public class AAAI2017ESERefine {

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
        int eseCount = 0;
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
                
                if(elems[2].equals("B"))eseCount++;
                
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
        System.out.println(eseCount);
        

        BufferedReader in_parse = new BufferedReader(new InputStreamReader(
                new FileInputStream(args[2]), "UTF8"));
        PrintWriter out_answerWithSentID = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(args[3]), "UTF-8"));
        

        int totalESE = 0;
        int correctESE = 0;
        
        while ((sLine1 = in_parse.readLine()) != null){
            sLine1 = sLine1.trim();
            if(sLine1.isEmpty()) continue;
            
            String[] markedUnits = sLine1.toLowerCase().split("\\s+");
            
            if(markedUnits.length != 3){
                System.out.println(sLine1);
                continue;
            }
            
            String markedId = markedUnits[2] + "\t" + markedUnits[1];
            
            List<String[]> parsedInsts = new ArrayList<String[]>();
            
            while ((sLine2 = in_parse.readLine()) != null){
                sLine2 = sLine2.trim();
                if(sLine2.isEmpty()) break;
                
                String[] elems = sLine2.split("\\s+");
                
                if(elems.length != 5){
                    System.out.println(sLine2);
                    continue;
                }
                
                parsedInsts.add(elems);                
            }
            
            if(!orginInstances.containsKey(markedId)){
                continue;
            }
            
            List<String[]> insts = orginInstances.get(markedId);
            List<String> outlabels = new ArrayList<String>();
            
            for(int idx = 0; idx < parsedInsts.size(); idx++){
                outlabels.add("O");
            }
            
            if(markedUnits[0].equals("73") && markedUnits[1].equals("19")){
                System.out.println("debug start");
            }
            
            int direction = 0;
            if(parsedInsts.size() > insts.size()) direction = 1;
            if(parsedInsts.size() < insts.size()) direction = -1;
            int maxDistance = parsedInsts.size() - insts.size();
            if(maxDistance < 0) maxDistance = - maxDistance;
            
            for(int idx = 0; idx < insts.size(); idx++){
                if(insts.get(idx)[2].equals("B")){
                    int start = idx;
                    int outposition = idx + 1;
                    
                    while(outposition < insts.size() && insts.get(outposition)[2].equals("I")){
                        outposition++;
                    }
                    
                    int end = outposition -1;
                    
                    List<Integer> perfectPositions = new ArrayList<Integer>();
                    
                    perfectPositions.add(start);
                    for(int distance = 1; distance <= maxDistance; distance++){
                        int newPosition = start + distance * direction;
                        if(newPosition >= 0 &&  newPosition < parsedInsts.size()){
                            perfectPositions.add(newPosition);
                        }
                        newPosition = start - distance * direction;
                        if(newPosition >= 0 &&  newPosition < parsedInsts.size()){
                            perfectPositions.add(newPosition);
                        }
                    }
                    
                    totalESE++;
                    
                    boolean bFind = false;
                    
                    for(int idy = 0; idy < perfectPositions.size(); idy++){
                        int curPosition = perfectPositions.get(idy);                        
                        int identicalNum = 0;
                        for(int idz = start; idz <= end; idz++){
                            if(curPosition + idz - start  >=  parsedInsts.size()
                                    || curPosition + idz - start < 0){
                                identicalNum = 0;
                                break;
                            }
                            if(insts.get(idz)[0].equalsIgnoreCase(parsedInsts.get(curPosition + idz - start)[0])){
                                identicalNum++;
                            }
                        }
                        if(identicalNum*1.0/(end-start + 1) >= 0.5){
                            bFind = true;
                            for(int idz = start; idz <= end; idz++){
                                outlabels.set(curPosition + idz - start, insts.get(idz)[2]);
                            }
                            
                            break;
                        }
                    }
                    
                    if(!bFind){
                        System.out.println("Check: " + sLine1);
                    }
                    else{
                        correctESE++;
                    }
                    
                    
                    idx = end;
                }
                else{
                    
                }
            }
            

            out_answerWithSentID.println(sLine1);
            for(int idx = 0; idx < parsedInsts.size(); idx++){
                String curOutline = parsedInsts.get(idx)[0] + "\t" + parsedInsts.get(idx)[1]  + "\t" + parsedInsts.get(idx)[2]
                        + "\t" + parsedInsts.get(idx)[3] + "\t" + outlabels.get(idx);   
                out_answerWithSentID.println(curOutline); 
            }
            
            out_answerWithSentID.println();
            
        }
        
        
        
        in_parse.close();       
        out_answerWithSentID.close();
        
        System.out.println(String.format("Acc = %d/%d = %.4f", correctESE, totalESE, correctESE * 100.0 / totalESE));
    }

}
