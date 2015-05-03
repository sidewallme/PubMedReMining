/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentenceParser;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author erichsu
 */
public class sentenceParser {
    public static String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    public static String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
    public static LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
    public static TreebankLanguagePack tlp = lp.getOp().langpack();
    public static GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
    
    public static Iterable<List<? extends HasWord>> sentences;
    
    public String ok(String input){
        
        return input;
    }
    /**
     *
     * @param input
     * @return
     */
    public List<String> sentenceDeparser(String input){
        //String sent2 = ("animals were divided into three groups: 1) rats with alloxan-induced diabetes; 2) diabetic rats treated with isophane insulin (2 iu/day); and 3) matching controls");
      
        // Use the default tokenizer for this TreebankLanguagePack
        Tokenizer<? extends HasWord> toke =
        tlp.getTokenizerFactory().getTokenizer(new StringReader(input));
        List<? extends HasWord> sentence = toke.tokenize();
        
        Tree parseTree = lp.parse(sentence);
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        List<String> val = new ArrayList<String>(); ;
        for (TypedDependency tmp:tdl){
            val.add(tmp.toString());
        }
        return val;
    }
}
