/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.questionanalysis;

import cn.edu.hit.ir.nlp.OpenNLP;

/**
 * A data structure representing analyzed question.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-29
 */

public class AnalyzedQuestion {

	private static OpenNLP openNLP = OpenNLP.getInstance();
	
	private String question;
	
	private String[] tokens;
	
	/**
	 * Creates a new instance of AnalyzedQuestion.
	 *
	 */
	public AnalyzedQuestion(String question) {
		// TODO Auto-generated constructor stub

	}

	/**
	 * Set the question.
	 *
	 * @param question The question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * Get the question.
	 *
	 * @return The question
	 */
	public String getQuestion() {
		return question;
	}
	
	private void analyze() {}
}
