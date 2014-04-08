/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

/**
 * An interface of NLP tool.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-29
 */

public interface NlpTool {
	
	/**
	 * Splits a text into sentences.
	 * 
	 * @param text sequence of sentences
	 * @return array of sentences in the text or <code>null</code>, if the
	 * 		   sentence detector is not initialized
	 */
	public String[] sentDetect(String text);
	
	/**
	 * A model-based tokenizer used to prepare a sentence for POS tagging.
	 * 
	 * @param sentence sentence to tokenize
	 * @return array of tokens or <code>null</code>, if the tokenizer is not
	 * 		   initialized
	 */
	public String[] tokenize(String sentence);
	
	/**
	 * Assigns POS tags to an array of tokens that form a sentence.
	 * 
	 * @param tokens array of tokens to be annotated with POS tags
	 * @return array of POS tags or <code>null</code>, if the tagger is not
	 * 		   initialized
	 */
	public String[] tag(String[] tokens);
	
	/**
	 * Assigns POS tags to a sentence of space-delimited tokens.
	 * 
	 * @param sentence sentence to be annotated with POS tags
	 * @return tagged sentence or <code>null</code>, if the tagger is not
	 * 		   initialized
	 */
	public String[] tag(String sentence);
	
	/**
	 * Assigns chunk tags to an array of tokens and POS tags.
	 * 
	 * @param tokens array of tokens
	 * @param tags array of corresponding POS tags
	 * @return array of chunk tags or <code>null</code>, if the chunker is not
	 * 		   initialized
	 */
	public String[] chunk(String[] tokens, String[] tags);
	
	/**
	 * Generates chunk words to an array of tokens and POS tags.
	 * 
	 * @param tokens array of tokens
	 * @param tags array of corresponding POS tags
	 * @return array of chunk words or <code>null</code>, if the chunker is not
	 * 		   initialized
	 */
	public String[] chunkAsWords(String[] tokens, String[] tags);
	
	/**
	 * Converts the verbs to infinitive and the nouns to their singular forms.
	 *
	 * @param tokens array of tokens of a sentence
	 * @param tags array of POS tags of a sentence
	 * @return array of stemmed words
	 */
	public String[] stem(String[] tokens, String[] tags);
	
	
}
