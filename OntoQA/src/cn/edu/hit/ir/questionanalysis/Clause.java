/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.questionanalysis;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ir.nlp.NlpSentence;

/**
 * A clause class.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-28
 */

public class Clause {

	private NlpSentence nlpSentence;
	
	private List<Integer> indexes;
	
	private List<String> tokens;
	
	public Clause(NlpSentence nlpSentence) {
		setNlpSentence(nlpSentence);
		indexes = new ArrayList<Integer>();
		tokens = new ArrayList<String>();
	}
	
	/**
	 * Set the nlpSentence.
	 *
	 * @param nlpSentence The nlpSentence to set
	 */
	public void setNlpSentence(NlpSentence nlpSentence) {
		this.nlpSentence = nlpSentence;
	}

	/**
	 * Get the nlpSentence.
	 *
	 * @return The nlpSentence
	 */
	public NlpSentence getNlpSentence() {
		return nlpSentence;
	}

	/**
	 * Get the indexes.
	 *
	 * @return The indexes
	 */
	public List<Integer> getIndexes() {
		return indexes;
	}

	/**
	 * Set the indexes.
	 *
	 * @param indexes The indexes to set
	 */
	public void setIndexes(List<Integer> indexes) {
		this.indexes = indexes;
	}

	public void add(int index) {
		indexes.add(index);
		tokens.add(nlpSentence.getTokens()[index]);
	}
	
	public List<String> getTokens() {
		return tokens;
	}
	
}

