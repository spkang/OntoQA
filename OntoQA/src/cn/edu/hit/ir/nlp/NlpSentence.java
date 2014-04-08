/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;


/**
 * A data structure representing the NLP result of a question.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-29
 */

public class NlpSentence {
	
	/**
	 * The original sentence
	 */
	private String sentence;
	
	/**
	 * The array of tokens of the sentence
	 */
	private String[] tokens;
	
	/**
	 * The array of POS tags of the sentence
	 */
	private String[] tags;
	
	/**
	 * The array of stemmed tokens
	 */
	private String[] stems;
	
	public static NlpSentence process(NlpTool nlpTool, String sentence) {
		if (nlpTool == null || sentence == null) {
			return null;
		}
		String[] tokens = nlpTool.tokenize(sentence);
		String[] tags = nlpTool.tag(sentence);
		String[] stems = nlpTool.stem(tokens, tags);
		NlpSentence nlps = new NlpSentence(sentence, tokens, tags, stems);
		return nlps;
	}
	
	private NlpSentence(String sentence, String[] tokens, String[] tags,
			String[] stems) {
		setSentence(sentence);
		setTokens(tokens);
		setTags(tags);
		setStems(stems);
	}

	/**
	 * Get the sentence.
	 *
	 * @return The sentence
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * Set the sentence.
	 *
	 * @param sentence The sentence to set
	 */
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	/**
	 * Get the tokens.
	 *
	 * @return The tokens
	 */
	public String[] getTokens() {
		return tokens;
	}

	/**
	 * Set the tokens.
	 *
	 * @param tokens The tokens to set
	 */
	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	/**
	 * Get the tags.
	 *
	 * @return The tags
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * Set the tags.
	 *
	 * @param tags The tags to set
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
	}

	/**
	 * Set the stems.
	 *
	 * @param stems The stems to set
	 */
	public void setStems(String[] stems) {
		this.stems = stems;
	}

	/**
	 * Get the stems.
	 *
	 * @return The stems
	 */
	public String[] getStems() {
		return stems;
	}
	
	public String getToken(int index) {
		return tokens[index];
	}

}
