/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-29
 */

public class EnglishNlpTool implements NlpTool {
	
	private static EnglishNlpTool instance;
	
	OpenNLP openNLP;
	Stemmer stemmer;
	
	public static EnglishNlpTool getInstance() {
		if (instance == null) {
			instance = new EnglishNlpTool();
		}
		return instance;
	}

	/**
	 * Creates a new instance of EnglishNlpTool.
	 */
	private EnglishNlpTool() {
		openNLP = OpenNLP.getInstance();
		stemmer = Stemmer.getInstance();
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#sentDetect(java.lang.String)
	 */
	@Override
	public String[] sentDetect(String text) {
		return openNLP.sentDetect(text);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#tokenize(java.lang.String)
	 */
	@Override
	public String[] tokenize(String sentence) {
		return openNLP.tokenize(sentence);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#tag(java.lang.String[])
	 */
	@Override
	public String[] tag(String[] tokens) {
		return openNLP.tag(tokens);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#tag(java.lang.String)
	 */
	@Override
	public String[] tag(String sentence) {
		return openNLP.tag(sentence);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#chunk(java.lang.String[], java.lang.String[])
	 */
	@Override
	public String[] chunk(String[] tokens, String[] tags) {
		return openNLP.chunk(tokens, tags);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#chunkAsWords(java.lang.String[], java.lang.String[])
	 */
	@Override
	public String[] chunkAsWords(String[] tokens, String[] tags) {
		return openNLP.chunkAsWords(tokens, tags);
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.NLPTool#stem(java.lang.String[], java.lang.String[])
	 */
	@Override
	public String[] stem(String[] tokens, String[] tags) {
		return stemmer.stem(tokens, tags);
	}

}
