/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentenceFetcher;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import static pubmedsearch.PubMedSearch.limit;
import static pubmedsearch.PubMedSearch.qnumber;

/**
 *
 * @author erichsu
 */
public class sentenceFetcher {
    public static String dictPath= "/Users/erichsu/Documents/CSDATA/allTitleAbs_1_to_1052";

    

    public void queryResult(String entity1, String entity2,int limit , int qnumber) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        List<String> ids= new ArrayList<>();
        List<String> abs= new ArrayList<>();
        String idsFile = entity1+"+"+entity2+"+ids"+".txt";
        String absFile = entity1+"+"+entity2+"+abs"+".txt";
        PrintWriter writerIds = new PrintWriter(idsFile, "UTF-8");
        PrintWriter writerAbs = new PrintWriter(absFile, "UTF-8");
        String currId = "";
        int count = 0;
        int qcount = 0;
        try (
            BufferedReader br = new BufferedReader(new FileReader(dictPath))) {
            String line = br.readLine();
            while (line != null) {
                //if(count++>limit) return;
                line = line.toLowerCase();
                String[] tokens = line.split(Pattern.quote("."));
              
                for(String tmp:tokens){
                    
                    if (tmp.contains(entity1) && tmp.contains(entity2)){
                        if(qcount++> qnumber) return;
                        writerIds.println(currId);
                        writerAbs.println(tmp);
                    }
                       
                }
                currId = line;
                line = br.readLine();
            }
            
            writerIds.close();
            writerAbs.close();
        }
       
    }
}
