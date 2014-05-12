

package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ir.nlp.OpenNLP;
import cn.edu.hit.ir.nlp.Stemmer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;

/** 
 *  an implementation of the stanfordnlptool  class 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月27日 
 */
public class StanfordEnglishNlpTool implements StanfordNlpTool {
	private static StanfordEnglishNlpTool instance = null;
	private static StanfordParser parser = null;
	
	private StanfordTagger tagger = null;
	private OpenNLP openNlp;
	private Stemmer stemmer;
	
	/**
	 * get the instance of 
	 *
	 * @param 
	 * @return StanfordEnglishNlpTool
	 */
	public static StanfordEnglishNlpTool getInstance  () {
		if (instance == null ) {
			instance = new StanfordEnglishNlpTool ();
		}
		return instance;
	}
	
	private StanfordEnglishNlpTool () {
		init();
	}
	
	private void init () {
		openNlp = OpenNLP.getInstance();
		tagger = StanfordTagger.getInstance();
		stemmer = Stemmer.getInstance();
		if (parser == null ) {
			parser = new StanfordParser();
		}
	}		
	

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#tokenize(java.lang.String)
	 */
	@Override
	public List<CoreLabel> tokenize(String sentence) {
		if (sentence == null )
			return null;
		return parser.tokenizerString(sentence);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#token(java.lang.String)
	 */
	@Override
	public String[] token (String sentence ) {
		if (sentence == null )
			return null;
		List<CoreLabel> cTokens = tokenize (sentence);
		String[] tokens = new String[cTokens.size()];
		int idx = 0;
		for (CoreLabel cl : cTokens ) {
			tokens[idx++] = cl.word();
		}
		return tokens;
	}
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#tag(java.lang.String[])
	 */
	@Override
	public String[] tag(List<CoreLabel> tokens) {
		if (tokens == null)
			return null;
		String sentence = "";
		for (CoreLabel cl : tokens)
			sentence += cl.word() + " ";
		sentence = sentence.trim();
		List<TaggedWord> taggedWds = tagger.taggerSentence(sentence);
		String [] res = new String[taggedWds.size()];
		int idx = 0;
		for (TaggedWord tw : taggedWds) {
			res[idx++] = tw.tag();
		}
		return res;
	}
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#defaultTag(java.util.List)
	 */
	@Override
	public String[] defaultTag ( List<CoreLabel> tokens) {
		if (tokens == null)
			return null;
		List<TaggedWord> taggedWords = parser.getTaggedWords(tokens); 
		if (taggedWords == null)
			return null;
		String[] tags = new String[taggedWords.size()];
		int idx = 0;
		for (TaggedWord tw : taggedWords) {
			tags[idx++] = tw.tag();
		}
		return tags;
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#tag(java.lang.String)
	 */
	@Override
	public String[] tag(String sentence) {
		if (sentence == null)
			return null;
		List<TaggedWord> taggedWds = tagger.taggerSentence(sentence);
		String[] tags = new String[taggedWds.size()];
		int idx = 0;
		for (TaggedWord tw : taggedWds ) {
			tags[idx++] = tw.tag();
		}
		return tags;
	}
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#defaultTag(java.lang.String)
	 */
	@Override
	public String[] defaultTag (String sentence) {
		if (sentence == null )
			return null;
		List<TaggedWord> taggedWords =  parser.getTaggedWords(parser.tokenizerString(sentence));
		if (taggedWords == null )
			return null;
		String[] tags = new String[taggedWords.size()];
		for (int idx = 0; idx < taggedWords.size(); ++idx )
			tags[idx] = taggedWords.get(idx).tag();
		return tags;
	}
	

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#chunk(java.lang.String[], java.lang.String[])
	 */
	@Override
	public String[] chunk(String[] tokens, String[] tags) {
		return openNlp.chunk(tokens, tags);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#chunkAsWords(java.lang.String[], java.lang.String[])
	 */
	@Override
	public String[] chunkAsWords(String[] tokens, String[] tags) {
		return openNlp.chunkAsWords(tokens, tags);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#stem(java.lang.String[], java.lang.String[])
	 */
	@Override
	public String[] stem(String[] tokens, String[] tags) {
		return stemmer.stem(tokens, tags);
	}
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#taggedWord(java.lang.String)
	 */
	@Override
	public List<TaggedWord> taggedWord (String sentence) {
		if (sentence == null )
			return null;
		return  tagger.taggerSentence(sentence);
	}
	
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#taggedWord(java.lang.String[], java.lang.String[])
	 */
	@Override
	public List<TaggedWord> taggedWord (String [] words, String [] tags) {
		if (words == null || tags == null || words.length != tags.length)
			return null;
		List<TaggedWord> taggedWord = new ArrayList<TaggedWord> ();
		for (int i = 0; i < words.length; ++i ) {
			taggedWord.add(new TaggedWord (words[i], tags[i]));
		}
		return taggedWord;
	}
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#defaultTaggedWord(java.lang.String)
	 */
	@Override
	public List<TaggedWord> defaultTaggedWord (String sentence) {
		if (sentence == null)
			return null;
		return new ArrayList<TaggedWord> (parser.getTaggedWords(parser.tokenizerString(sentence)));
	}
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#typedDependenciesCCprocessed(java.util.List)
	 */
	@Override
	public List<TypedDependency> typedDependenciesCCprocessed(List<? extends HasWord> taggedWords) {
		if (taggedWords == null )
			return null;
		return parser.getDependenciesCCprocessed(taggedWords);
	}
	
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.scir.dependency.StanfordNlpTool#typedDependencies(java.util.List)
	 */
	@Override
	public List<TypedDependency> typedDependencies (List<? extends HasWord> taggedWords) {
		if (taggedWords == null )
			return null;
		return parser.getTypedDependencies(taggedWords);
				
	}
}