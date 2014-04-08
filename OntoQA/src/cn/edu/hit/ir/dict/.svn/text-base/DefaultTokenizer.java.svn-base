/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-28
 */

public class DefaultTokenizer implements Tokenizer {

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.dict.Tokenizer#tokenize(java.lang.String)
	 */
	@Override
	public String[] tokenize(String sentence) {
		if (sentence == null) {
			return null;
		}
		String[] tokens = sentence.split("\\s+");
		return tokens;
		
	}

}
