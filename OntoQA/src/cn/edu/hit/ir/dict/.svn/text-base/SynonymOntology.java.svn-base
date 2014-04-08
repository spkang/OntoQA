/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.Set;

/**
 * An ontology interface which can get synonyms from.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-4
 */

public interface SynonymOntology {
	/**
	 * Returns the set of synonyms of the given phrase
	 *
	 * @param phrase the phrase
	 * @return the set of synonyms or <code>null</code> if no synonym exists
	 */
	Set<String> getSet(String phrase);
	
	/**
	 * Returns the set of synonyms of the given phrase
	 *
	 * @param phrase the phrase
	 * @param pos its part of speech
	 * @return the set of synonyms or <code>null</code> if no synonym exists
	 */
	Set<String> getSet(String phrase, String pos);
}
