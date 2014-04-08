/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.questionanalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides methods that normalize a question.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-25
 */

public class QuestionNormalizer {
	
	private static QuestionNormalizer instance;
	
	public static QuestionNormalizer getInstance() {
		if (instance == null) {
			instance = new QuestionNormalizer();
		}
		return instance;
	}
	
	private QuestionNormalizer() {}
	
	/**
	 * Normalizes a question string by removing abundant white spaces, replacing
	 * short forms and stemming the question.
	 * 
	 * @param question The question string
	 * @return The normalized question string
	 */
	public String normalize(String question) {
		if (question == null) {
			return null;
		}
		// remove leading and trailing white spaces
		question = question.trim();
		// replace multiple white spaces by a single blank
		question = question.replaceAll("\\s+", " ");
		// replace short forms of "is" and "are"
		question = replaceShortForms(question);
		
		return question;
	}
	
	/**
	 * Replaces short forms of "is" and "are" that occur in combination with
	 * interrogatives.
	 * 
	 * @param question the question string
	 * @return modified question string
	 */
	private String replaceShortForms(String question) {
		// only replace occurences of "'s" and "'re" in combination with
		// interrogatives
		Pattern p = Pattern.compile("(?i)(how|what|which|when|where|who|why)'" +
									"(s|re)");
		Matcher m = p.matcher(question);
		
		if (m.find()) {
			String original = m.group();
			
			String replaced = original.replace("'s", " is");
			replaced = replaced.replace("'re", " are");
			
			return question.replace(original, replaced);
		}
		
		return question;  // no such short forms in the question
	}
	
	/**
	 * Removes the final punctuation mark and quotation marks from the question
	 * string.
	 * 
	 * @param question the question string
	 * @return modified question string
	 */
	public String dropPunctuationMarks(String question) {
		// drop final punctuation mark
		question = question.replaceAll("(\\.|\\?|!)$", "");
		// drop quotation marks
		return question.replaceAll("\"", "");
	}

}
