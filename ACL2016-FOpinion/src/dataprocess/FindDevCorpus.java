package dataprocess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class FindDevCorpus {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        Set<String> allCorpusNames = new HashSet<String>();
        String sLine = "";

        BufferedReader idReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF8"));
        while ((sLine = idReader.readLine()) != null) {
            sLine = sLine.trim().toLowerCase();
            if (!sLine.isEmpty()) {
                allCorpusNames.add(sLine);
            }
        }

        idReader.close();
        
        Set<String> trainCorpusNames = new HashSet<String>();
        idReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF8"));
        while ((sLine = idReader.readLine()) != null) {
            sLine = sLine.trim().toLowerCase();
            if (!sLine.isEmpty()) {
                trainCorpusNames.add(sLine);
            }
        }

        idReader.close();
        
        
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[2]), "UTF-8"));
        
        for(String curCorpus : allCorpusNames){
            if(!trainCorpusNames.contains(curCorpus)){
                out.println(curCorpus);
            }
        }
               
        out.close();
        
        

    }

}
