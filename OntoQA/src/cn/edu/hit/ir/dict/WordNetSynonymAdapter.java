/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.Set;

import cn.edu.hit.ir.nlp.WordNet;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-4
 */

public class WordNetSynonymAdapter implements SynonymOntology {
	
	WordNet wordNet;
	
	public WordNetSynonymAdapter() {
		wordNet = WordNet.getInstance();
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.dict.SynonymOntology#getSet(java.lang.String)
	 */
	@Override
	public Set<String> getSet(String phrase) {
		Set<String> synSet = wordNet.getSynonymSet(phrase);
		if (synSet != null && synSet.isEmpty()) {
			synSet = null;
		}
		return synSet;
	}

	/**
	 * (non-Javadoc)
	 * @see cn.edu.hit.ir.dict.SynonymOntology#getSet(java.lang.String, java.lang.String)
	 */
	@Override
	public Set<String> getSet(String phrase, String pos) {
		pos = pos.toLowerCase();
		Set<String> synSet = null;
		if (pos.startsWith("n")) {
			synSet = wordNet.getSynonymSet(phrase, WordNet.NOUN);
		} else if (pos.startsWith("v")) {
			synSet = wordNet.getSynonymSet(phrase, WordNet.VERB);
		}
		
		if (synSet != null && synSet.isEmpty()) {
			synSet = null;
		}
		return synSet;
	}

}
