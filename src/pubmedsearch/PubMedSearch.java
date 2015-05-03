/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pubmedsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static pubmedremining.PubMedReMining.fetcherHub;
import static pubmedremining.PubMedReMining.query;
import sentenceFetcher.sentenceFetcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import static sentenceFetcher.sentenceFetcher.dictPath;

/**
 *
 * @author erichsu
 */
public class PubMedSearch {
    public static String entity1 = "diabetes";
    public static String entity2 = "insulin";
    public static int limit = 1000000;
    public static int qnumber = 20000;
    public static HashMap<String, Integer> verbs = new HashMap();
    public static HashMap<List<TaggedWord>, Integer> relationSize = new HashMap();
    public static HashMap<List<TaggedWord>, ArrayList<String>> relationSet = new HashMap();
    //public static HashMap<List<TaggedWord>, ArrayList<String>> relationAll = new HashMap();
    
    /**
     *
     */
    public static sentenceFetcher fetcherHub = new sentenceFetcher();
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        
        String modelfile = "english-bidirectional-distsim.tagger";
        
        String absfile = entity1+"+"+entity2+"+"+"abs.txt";
        String idsfile = entity1+"+"+entity2+"+"+"ids.txt";
        
        
        query(entity1, entity2);
        MaxentTagger tagger = new MaxentTagger(modelfile);
        
        mining(tagger, absfile, idsfile);

    }
    public static void freqVerbs(){
        Map<String, Integer> sortedVerbs = sortByValues(verbs); 
        for(String verb : sortedVerbs.keySet()){
            int co = sortedVerbs.get(verb);
            System.out.println(verb+" "+co);
        }
    }
    public static void mining(MaxentTagger tagger,String filename, String ids) throws FileNotFoundException, IOException{
        String resfile = entity1+"+"+entity2+"+"+"res.txt";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        BufferedReader br2 = new BufferedReader(new FileReader(ids));
        
        PrintWriter wr = new PrintWriter("result+"+filename, "UTF-8");
        PrintWriter wr2 = new PrintWriter("raw+"+filename, "UTF-8");
        
        String line = br.readLine();
        String idline = br2.readLine();
        List<TaggedWord> val = new ArrayList<>();
        List<TaggedWord> key = new ArrayList<>();
        int lineId = 0;
        while(line!=null){
            lineId++;
            
            StringReader rd = new StringReader(line);
            List<List<HasWord>> sentences = MaxentTagger.tokenizeText(rd);         
            
            //writing raw data to raw file
            val = tagging(tagger,sentences);
            wr2.println(val);
            
            key = sentenceMining(val);
            if(key.size()>0){
                updateRelationSet(key, line+" ["+idline+"]");
            }
            

            line = br.readLine();
            idline = br2.readLine();
        }
        resultPrinter(resfile);
        br.close();
        wr.close();
        wr2.close();
    }
    public static void resultPrinter(String fileval) throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter wr = new PrintWriter(fileval, "UTF-8");
        Map<List<TaggedWord>, Integer> sortedRelations = sortByValues(relationSize);
        List<String> val = new ArrayList<String>();
        for(List<TaggedWord> temp: sortedRelations.keySet()){
            wr.println(temp);
            val = relationSet.get(temp);
            for(String t:val){
                wr.println(t);
            }
            wr.println();
        }
    }
    
    public static void updateRelationSet(List<TaggedWord> key, String line){
        ArrayList<String> init = new ArrayList<>();
        if(relationSet.containsKey(key)){
            
            relationSet.get(key).add(line);
            int size = relationSize.get(key);
            relationSize.put(key, ++size);
            
        }
        else{
            init.add(line);
            relationSet.put(key, init);
            relationSize.put(key, 1);
        }
    } 
    public static List<TaggedWord> sentenceMining(List<TaggedWord> line){
        List<TaggedWord> res = new ArrayList<>();
        int t1 = -1;
        int t2 = -1;
        
        for(int i=0;i<line.size();i++){
            if(line.get(i).word().contains(entity1)){
                t1=i;
            }
            if(line.get(i).word().contains(entity2)){
                t2=i;
            }    
        }
        int tmin = t1>t2?t2:t1;
        int tmax = t2>t1?t2:t1;
        //System.out.println(tmin + " "+ tmax);
        if(tmax-tmin>4|| tmax<=tmin || tmin<0 || tmax<0) return res;
        for (int t=tmin;t<tmax;t++){
            
            if(line.get(t).tag().contains("VB")){
                res.add(line.get(t));
            }
        }
        
        return res;
    }
    
    public static void dataProcess(List<TaggedWord> input){
        
        String temp;
        for(int i=0;i<input.size();i++){
            if(input.get(i).tag().contains("VB")){
                temp = input.get(i).word();
                if(!verbs.containsKey(temp)){
                    verbs.put(temp, 1);
                }
                else{
                    int co = verbs.get(temp);
                    verbs.put(temp, ++co);
                }
            }
        }
    }
    
    public static void query(String entity1, String entity2) throws UnsupportedEncodingException, IOException{
        fetcherHub.queryResult(entity1, entity2, limit , qnumber);        
    }
    
    public static List<TaggedWord> tagging(MaxentTagger tagger, List<List<HasWord>> sentences){
        List<HasWord> sentence = sentences.get(0);
        return tagger.tagSentence(sentence);
    }
    
    private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, (Object o2, Object o1) -> ((Comparable) ((Map.Entry) (o1)).getValue())
               .compareTo(((Map.Entry) (o2)).getValue()));

       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
    }
    
}
