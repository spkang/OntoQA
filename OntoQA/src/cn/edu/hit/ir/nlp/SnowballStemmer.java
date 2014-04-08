/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

import org.tartarus.snowball.ext.englishStemmer;

import cn.edu.hit.ir.util.StringUtil;

/**
 * An interface to the Snowball instance for English.
 * 
 * @author bin3 (bin183cs@gmail.com)
 * @version 0.1.0
 * @date 2011-5-25
 */

public class SnowballStemmer {
	private static englishStemmer stemmer;

	private static SnowballStemmer instance = null;

	public static SnowballStemmer getInstance() {
		if (instance == null) {
			instance = new SnowballStemmer();
		}
		return instance;
	}

	private SnowballStemmer() {
		stemmer = new englishStemmer();
	}

	/**
	 * Stems a single English word.
	 * 
	 * @param word The word to be stemmed
	 * @return The stemmed word
	 */
	public String stem(String word) {
		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	/**
	 * Stems all tokens in a string of space-delimited English words.
	 * 
	 * @param tokens The string of tokens to be stemmed
	 * @return The string of stemmed tokens
	 */
	public String stemAllTokens(String tokens) {
		String[] tokenArray = tokens.split("\\s+");
		for (int i = 0; i < tokenArray.length; i++) {
			tokenArray[i] = stem(tokenArray[i]);
		}
		String stemmed = StringUtil.join(tokenArray, " ");
		return stemmed;
	}
}
