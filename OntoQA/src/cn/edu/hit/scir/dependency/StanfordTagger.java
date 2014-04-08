/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.dependency;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.util.ConfigUtil;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;




/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月9日 
 */
public class StanfordTagger {
	private static Logger logger = Logger.getLogger(StanfordTagger.class);
	private static StanfordTagger instance = null;
	private static final String ENGLISH_MODEL_PATH = "english.tagger.model.path";
	private Configuration config = null;
	private static MaxentTagger tagger = null;
	
	public static StanfordTagger getInstance () {
		if (instance == null )
			return new StanfordTagger ();
		return instance;
	}
	
	private StanfordTagger () {
		initConfig();
		loadMaxentTagger();
	} 
	
	private void initConfig () {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		}catch (ConfigurationException ex) {
			ex.printStackTrace();;
		}
	}
	
	private void loadMaxentTagger () {
		if (this.tagger == null) {
			String taggerModelPath = config.getString(ENGLISH_MODEL_PATH);
			logger.info("tagger model Path : " + taggerModelPath);
			this.tagger = new MaxentTagger(taggerModelPath);
		}
	}
	
	public List<TaggedWord> taggerSentence (String sentence) {
		List<TaggedWord> taggedWds = null;
		if (sentence == null || sentence.isEmpty())
			return null;
		if (this.tagger == null )
			loadMaxentTagger ( );
		if (this.tagger != null ){
			TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
			Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence));
			List<CoreLabel> tokens = tok.tokenize();
			List<HasWord> tokenSent =  new ArrayList<HasWord>();
		
		    for (CoreLabel cl : tokens) {
		    	tokenSent.add(new Word (cl.word()));
		    }  
		   //ArrayList<TaggedWord> tSentence = tagger.tagSentence(tokenSent);
			taggedWds = tagger.tagSentence(tokenSent);
			//logger.info(Sentence.listToString(taggedWds, false));
		}
		return taggedWds;
	}
	
	
	
	
	public static void main (String [] args) {
//		String sentence = "what states border Texas and have a majar river?";
		String sentence = "how many states does the colorado_river flow through ?";
		
		StanfordTagger tagger = StanfordTagger.getInstance();
		List<TaggedWord> taggedWds = tagger.taggerSentence(sentence);
		logger.info("sentence : " + sentence );
		for (TaggedWord tw : taggedWds ) {
			logger.info(tw);
		}
		
		StanfordParser sdParser = new StanfordParser ();
		List<TypedDependency> tdl = null;
		if (sdParser.getLexicalizedParser() != null ) {
			Tree parse = sdParser.getLexicalizedParser().apply(taggedWds);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();	
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			tdl = gs.typedDependenciesCCprocessed();
			for (TypedDependency td : tdl ) {
				logger.info(td.toString());
			}
		}
		
	}
}
