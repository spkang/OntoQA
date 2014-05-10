/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.dependency;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.util.ConfigUtil;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseGrammaticalStructure;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;

/**
 * this class is a package for StanfordParser
 * 
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014-3-3
 */
public class StanfordParser {
	
	private static Logger logger = Logger.getLogger(StanfordParser.class);
	private static final String ENGLISH_MODEL_PATH = "english.parser.model.path";
	private static final String CHINESE_MODEL_PATH = "chinese.parser.model.path";
	private Configuration config = null;
	private LexicalizedParser lparser = null;
	//private static StanfordTagger tagger = StanfordTagger.getInstance();
	
	// 是不是中文的parser
	private boolean isChinese = false;
	
	/**
	 * constructor of StanfordParser
	 */
	public StanfordParser () {
		this(false);
	}
	
	
	/**
	 * 根据输入的布尔变量来判断生成英文的parser还是中文的parser
	 *
	 * @param boolean isChinese, 是否是中文的parser， 默认是英文的parser
	 * @return 
	 */
	public StanfordParser (boolean isChinese ) {
		this.isChinese = isChinese;
		initConfig ();
		lparser = loadLexicalizedParser ();
	}
	
	
	/**
	 * get an instance of LexicalizedParser 
	 * @return an instance of LexiaclizedParser
	 */
	public LexicalizedParser getLexicalizedParser () {
		return lparser;
	}
	
	/**
	 * initConfig, initialize the configuration for StanfordParser
	 * @pragma 
	 * 
	 * */
	public void initConfig () {
		try {
			logger.info(getClass());
			logger.info("Path : " + ConfigUtil.getPath(getClass()));
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		}
		catch (ConfigurationException e ) {
			e.printStackTrace ();
		}
	}
	
	/**
	 * load the LexicalizedParser
	 * @return an instance of LexicalizedParser
	 */
	public LexicalizedParser loadLexicalizedParser () {
		if ( lparser == null ) {
			String modelPath = "";
			if (this.isChinese) {
				modelPath = config.getString( this.CHINESE_MODEL_PATH );
			}
			else {
				modelPath = config.getString( this.ENGLISH_MODEL_PATH );
			}
			logger.info("modelPath : " + modelPath);
			lparser = LexicalizedParser.loadModel(modelPath);
			
		}
		return lparser;
	}
	
	/**
	 * Tokenized the input sentence
	 * @param sentence, a raw input string sentence for tokenizing
	 * @return List<CoreLabel> , the tokenized tokens of the input sentence
	 */
	public List<CoreLabel> tokenizerString (String sentence ) {
		 TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		 Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence)); 
		 return new ArrayList<CoreLabel>(tok.tokenize());
	}
	
	/**
	 * get the list relation typed dependences of the input words
	 * @param words, the words after tokenized sentence
	 * @return a list of typed dependences 
	 */
	
	public List<TypedDependency> getDependenciesCCprocessed (List<? extends HasWord> words ) {
		if (words == null || words.isEmpty() ) 
			return null;
		List<TypedDependency> tdl = new ArrayList<TypedDependency>();
		if (lparser != null ) {
			GrammaticalStructure gs = getGrammaticalStructure(words);
			
			tdl = gs.typedDependenciesCCprocessed();
		}
		return tdl;
	} 
	
	
	/**
	 * get the typed denpendency for the sentence words
	 *
	 * @param  the taggedwords of the given sentence 
	 * @return List<TypedDependency> 
	 */
	public List<TypedDependency> getTypedDependencies ( List<? extends HasWord> words ) {
		if (words == null  || words.isEmpty() )
			return null;
		
		GrammaticalStructure gs = getGrammaticalStructure (words);
		List<TypedDependency> tdl = new ArrayList<TypedDependency> (gs.typedDependencies());
		return tdl;
	} 
	
	/**
	 * get a grammaticalStrucure by using words
	 * 
	 * @param words , the taggedwords array for parsering
	 * @return GrammaticalStructure 
	 */
	public GrammaticalStructure  getGrammaticalStructure (List<? extends HasWord> words) {
		if (words == null || words.isEmpty())
			return null;
		if (lparser == null )
			lparser = loadLexicalizedParser();
		Tree parse = lparser.apply(words);
		TreebankLanguagePack tlp = new PennTreebankLanguagePack ();
		GrammaticalStructureFactory gsf  = tlp.grammaticalStructureFactory();
		return gsf.newGrammaticalStructure(parse);
	}
	
	/**
	 * get the list tags of the input words
	 * @param words, the tokenized sentence 
	 * @return the list of tagged words
	 */
	public List<TaggedWord> getTaggedWords (List<? extends HasWord> words ) {
		if (words == null || words.isEmpty())
			return null;
		if (lparser == null ) 
			lparser = loadLexicalizedParser();
		List<TaggedWord> resTaggedWords = null;
		if (lparser != null ) {
			Tree parseTree = lparser.apply(words);
			resTaggedWords = parseTree.taggedYield();
		}
		return resTaggedWords;
	}
//	
//	public List<TypedDependency> getTypedDependencies (String sentence) {
//		if (sentence == null  || sentence.isEmpty())
//			return null;
//		List<CoreLabel> words = tokenizerString(sentence);
//		return 
//		
//	}
	
	public List<TypedDependency> getChineseDependency (List<? extends HasWord> words) {
		if (words == null || words.isEmpty())
			return null;
		if (lparser == null ) 
			lparser = loadLexicalizedParser();
		Tree parseTree = lparser.apply(words);
		ChineseTreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
	    //GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    ChineseGrammaticalStructure gs = new ChineseGrammaticalStructure(parseTree);
	    List<TypedDependency> tdl = new ArrayList<TypedDependency>(gs.typedDependencies());
	    return tdl;
	}
	
	/**
	 * print the tree
	 * 	
	 * @param tree, which is going to print
	 */
	public void printTree (Tree tree) {
		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		tp.printTree(tree);
	}
	
	public static void main (String [] args) {
		logger.info("In main function");
		StanfordParser parser = new StanfordParser (true);
		//String sentence = "how many states does the colorado_river flow through ?";
		//String sentence = "刘德华 的 专辑 忘不了 是 什么 时候 发行 的";
//		String sentence = "王菲 的 哪个 专辑 包含 的 歌曲 最 多";
		String sentence = "我们是一家人 属于 谁 的 专辑 ";
//		String sentence = "What states border texas?";
		List<CoreLabel> tokens = parser.tokenizerString(sentence);
		for (CoreLabel cl : tokens ) {
			logger.info(cl.value());
		}
		
		List<TypedDependency> dependency = parser.getChineseDependency(tokens);
		for (TypedDependency td : dependency ) {
			logger.info(td.toString() + " -> " + td.gov().taggedLabeledYield()  + " -> "  + td.dep().taggedLabeledYield());
		}
		
		
		List<TaggedWord> taggedWords = parser.getTaggedWords(tokens);
		for (TaggedWord tw : taggedWords ) {
			logger.info("taggedWords : " + tw.toString());
		}
		
		/*Tree treeParser = parser.getLexicalizedParser().apply(tokens);
		parser.printTree(treeParser);*/
	}
}
