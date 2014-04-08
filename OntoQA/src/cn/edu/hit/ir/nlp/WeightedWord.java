/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

/**
 * A word with a weight.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-18
 */

public class WeightedWord {

	private String word;

	private double weight;
	
	public WeightedWord(String word, double weight) {
		setWord(word);
		setWeight(weight);
	}
	
	/**
	 * Get the word.
	 *
	 * @return The word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Set the word.
	 *
	 * @param word The word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * Get the weight.
	 *
	 * @return The weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Set the weight.
	 *
	 * @param weight The weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
