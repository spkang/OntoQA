/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

/**
 * A similarity calculator.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-29
 */

public class Similarity extends Scorer {
	
	private static Similarity instance;

	private WordNet wordNet;
	
	public static Similarity getInstance() {
		if (instance == null) {
			instance = new Similarity();
		}
		return instance;
	}
	
	public Similarity() {
		wordNet = WordNet.getInstance();
	}
	
	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.nlp.Scorer#score(java.lang.String, java.lang.String)
	 */
	@Override
	public double score(String word1, String word2) {
		return wordNet.getSimilarity(word1, word2);
	}
	
}


