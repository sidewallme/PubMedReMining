/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pubmedremining;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sentenceParser.*;
import sentenceFetcher.*;
import static sentenceFetcher.sentenceFetcher.dictPath;

/**
 *
 * @author Jiarui Xu
 */
public class PubMedReMining {
     /**
     * @param args the command line arguments
     */
    public static sentenceParser parsingHub = new sentenceParser();
    public static sentenceFetcher fetcherHub = new sentenceFetcher();
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        // TODO code application logic here
        
        //String sentence = "diabetes can be treated by insulin";
        query("diabetes", "insulin");
        
        
    }
    public static void query(String entity1, String entity2) throws UnsupportedEncodingException, IOException{
        
        String idsFile = entity1+"+"+entity2+"+ids"+".txt";
        String absFile = entity1+"+"+entity2+"+abs"+".txt";
        String resultFile = entity1+"+"+entity2+"+result"+".txt";
        
        fetcherHub.queryResult("diabetes", "insulin", 1000000000, 10000);
        
        BufferedReader br = new BufferedReader(new FileReader(absFile));
        PrintWriter wr = new PrintWriter(resultFile, "UTF-8");

        String line = br.readLine();
        int count = 0;
        while (line != null && count<=100) {
            count++;
            //if(count%10==0)
                System.out.println("Currently Processing Line: "+count);
            List<String> res = parsingHub.sentenceDeparser(line);
            List<String> val = new ArrayList();
            val = mining(res, entity1, entity2);
            System.out.println(res);
            if(val.size()!=0)
                wr.println(count+": "+val);
            line = br.readLine();
        }
        wr.close();
        
    }
    public static List<String>  mining(List<String> sentence, String entity1, String entity2){
        List<String> res = new ArrayList<String>();
        List<String> entityOneLink = new ArrayList<String>();
        List<String> entityTwoLink = new ArrayList<String>();
        
        String collection = "";
        
        for(String tmp:sentence){
            if(tmp.contains(entity1) && tmp.contains(entity2)){
                res.add(extractPair(tmp,entity1,entity2,"get both"));
            }
            else if (tmp.contains(entity1)){
                entityOneLink.add(extractPair(tmp, entity1, entity2, "excluding one"));
            }
            else if (tmp.contains(entity2)){
                entityTwoLink.add(extractPair(tmp, entity1, entity2, "excluding two"));
                
            }
        }
        //System.out.println(entityOneLink);
        //System.out.println(entityTwoLink);
        for(String tmp:sentence){
            String curr = extractPair(tmp, entity1, entity2, "get both");
            for(String one:entityOneLink){
                for(String two:entityTwoLink){
                    if(one.equals(two) == false && curr.contains(one) && curr.contains(two)){
                        System.out.println(entityTwoLink);
                        res.add(entity1+"-"+one);
                        res.add(curr);
                        res.add(entity2+"-"+two);
                    }
                }
            }
        }
        
        return res;
    }
    public static String extractPair(String tuple, String entity1, String entity2, String op){
        String pattern = "\\(.*?\\)";
        String tmp = "";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(tuple);
        if (m.find( )) {
            tmp = m.group();
        }
        
        String first = "";
        String second = "";
        int idx = tmp.indexOf(", ");
        
        //Process first and second in tuple
        first = tmp.substring(1,idx);
        first = first.substring(0, first.lastIndexOf("-"));
        
        second = tmp.substring(idx+2,tmp.length()-1);
        second = second.substring(0, second.lastIndexOf("-"));

        if(op.equals("get both")){
            return "["+first+"-"+second+"]";
        }
        else if(op.equals("excluding one")){
            return first.contains(entity1)?second:first;
        }
        else
            return first.contains(entity2)?second:first;
    }
    
}
