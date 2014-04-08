/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

import java.util.Collection;
import java.util.List;

import cn.edu.hit.ir.ontology.Path;
import cn.edu.hit.ir.questionanalysis.Clause;

/**
 * A calculator for Similarity and Distance.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-30
 */

abstract class Scorer {
	
	private static final String[] EMPTY_STRINGS = new String[0];
	
	/**
	 * Returns the score of two words.
	 *
	 * @param word1 The first word
	 * @param word2 The second word
	 * @return The score of the two words
	 */
	abstract public double score(String word1, String word2);
	
	private double scoreHalf(String[] tokens1, String[] tokens2) {
		if (tokens1 == null || tokens1.length == 0
				|| tokens2 == null || tokens2.length == 0) {
			return 0;
		}
		
		double ss = 0;
		for (int i = 0; i < tokens1.length; ++i) {
			double maxWs = 0;
			for (int j = 0; j < tokens2.length; ++j) {
				double ws = score(tokens1[i], tokens2[j]);
				if (maxWs < ws) {
					maxWs = ws;
				}
			}
			ss += maxWs;
		}
		ss = ss / tokens1.length;
		return ss;
	}
	
	/**
	 * Returns the similarity of two sentences.
	 *
	 * @param tokens1 array of tokens of the first sentence
	 * @param tokens2 array of tokens of the second sentence
	 * @return The similarity of the two sentences
	 */
	public double score(String[] tokens1, String[] tokens2) {
		double half1 = scoreHalf(tokens1, tokens2);
		double half2 = scoreHalf(tokens2, tokens1);
		double ss = (half1 + half2) / 2;
		return ss;
	}

	public double score(Collection<String> tokens1, Collection<String> tokens2) {
		return score(tokens1.toArray(EMPTY_STRINGS),
				tokens2.toArray(EMPTY_STRINGS));
	}
	
	public double score(Clause clause, Path path) {
		if (clause == null || path == null) {
			return 0;
		}
		
		List<String> tokens = clause.getTokens();
		List<String> labels = path.getLabels();
		return score(tokens, labels);
	}
}
