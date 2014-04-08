/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

/**
 * An interface of Tokenizer. 
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-28
 */

public interface Tokenizer {

	/**
	 * Returns an array of tokens for a given sentence.
	 *
	 * @param sentence The sentence
	 * @return The array of tokens
	 */
	String[] tokenize(String sentence);
}
