/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

import javatools.PlingStemmer;

/**
 * A stemmer toolkit using WordNet and PlingStemmer.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-29
 */

public class Stemmer {

	private static Stemmer instance;
	
	private WordNet wordNet;
	
	public static Stemmer getInstance() {
		if (instance == null) {
			instance = new Stemmer();
		}
		return instance;
	}
	
	/**
	 * Creates a new instance of Stemmer.
	 */
	public Stemmer() {
		wordNet = WordNet.getInstance();
	}
	
	//public String stem(String word) {}
	
	/**
	 * Converts the verbs to infinitive and the nouns to their singular forms.
	 *
	 * @param tokens array of tokens of a sentence
	 * @param tags array of POS tags of a sentence
	 * @return array of stemmed words
	 */
	public String[] stem(String[] tokens, String[] tags) {
		if (tokens == null || tags == null) {
			return null;
		}
		if (tokens.length != tags.length) {
			final String msg = "The length of tokens and tags must be equal.";
			throw new IllegalArgumentException(msg);
		}
		
		for (int i = 0; i < tokens.length; i++)
			tokens[i] = tokens[i].toLowerCase();
		
		String[] stems = new String[tokens.length];
		
		for (int i = 0; i < tokens.length; i++) {
			if (tags[i].startsWith("VB")) {
				String stem = wordNet.getLemma(tokens[i], WordNet.VERB);
				if (stem == null) stem = tokens[i];
				stems[i] = stem;
			} else if (tags[i].startsWith("NN")) {
				String stem = PlingStemmer.stem(tokens[i]);
				stems[i] = stem;
			} else {
				stems[i] = tokens[i];
			}
		}
		
		return stems;
	}
}
