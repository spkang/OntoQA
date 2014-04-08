/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.edu.hit.scir.dependency.StanfordParser;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;


/**
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014-3-4
 */
public class Question {
	private String question = null; 			// the origin quesition
	private String processedQuestion = null; 	// after processed question, such as remove the word "the" 
	private List<CoreLabel> tokens = null; 	 	// the words in processed question
	private List<TaggedWord> taggedWords = null;
	private List<String> stems = null;
	
	private List<TypedDependency> questionDependency = null; 
	private static Logger logger = Logger.getLogger(Question.class);
	private static StanfordParser parser = null;
	private static Question instance = null;
	
	
	//NlpTool 
	
	public static Question getInstance () {
		if (instance == null)
			return new Question();
		return instance;
	}
	
	private Question () {}
	
	public void initialize (String question ) {
		setQuestion(question);
		if (parser == null )
			parser = new StanfordParser();
		setProcessedQuestion(processQuestion());
		
		
		
		if (tokens != null) {
			tokens.clear();
			tokens = null;
		}
		
		if (questionDependency != null ) {
			questionDependency.clear();
			questionDependency = null;
		}
		
		if ( taggedWords != null) {
			taggedWords.clear();
			taggedWords = null;
		}
		
	}
	
	/**
	 * the constructor of Question class
	 * 
	 * @param question, the origin input question
	 */
	private Question (String question ) {
		if (question == null)
			return ;
		this.question = question;
		parser = new StanfordParser();
		processedQuestion = processQuestion();
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
	 * process the question, now, this function remove the word "the" from the origin question
	 * 
	 * @return String, processedQuestion
	 */
	public String  processQuestion () {
		if (question == null)
			return null;
		
		// remove question marks 
		processedQuestion = question.trim().replaceAll("(\\?|\\.|!)$", "");
		
		
		
		// remove all the comma
		processedQuestion = processedQuestion.replaceAll(",", " ");
		
		processedQuestion = processedQuestion.replaceAll("\"", ""); 
		//logger.info("origin question   : " + question);
		
		//processedQuestion = processedQuestion.replaceAll("[\\s|^]the[\\s|$]", " ");
		
		//logger.info("origin question   : " + processedQuestion);
		processedQuestion = replaceShortForms(processedQuestion);
		
		if (processedQuestion != null ) {
			// "  " --> " "
			processedQuestion = processedQuestion.replaceAll("\\s+", " "); 
			processedQuestion = processedQuestion.toLowerCase().trim();
			
			//logger.info("processedQuestion : " + processedQuestion);
		}
		return this.processedQuestion;
	}
	
	/**
	 * set this.question value 
	 * 
	 * @param question, the origin input question
	 */
	public void setQuestion (String question) {
		this.question = question;
	}
	
	/**
	 * get the origin question
	 * 
	 * @return String ,the origin question
	 */
	public String getQuestion () {
		return question;
	}
	
	/**
	 * set this.processedQuestion value
	 * 
	 * @param quesiton, the processedQuestion
	 */
	public void setProcessedQuestion ( String proQuestion ) {
		this.processedQuestion = proQuestion;
	}
	
	/**
	 * get the processed question
	 * 
	 * @return processed question
	 */
	public String getProcessedQuestion () {
		if (this.processedQuestion == null ) {
			return processQuestion ();
		}
		return this.processedQuestion;
	}
	
	/**
	 * set tokens of processed question
	 * 
	 * @param tokens, the input tokens
	 */
	public void setTokens (List<CoreLabel> tokens) {
		this.tokens = tokens;
	}
	
	/**
	 * get the question tokens
	 * 
	 * @return the tokens
	 */
	public List<CoreLabel> getTokens () {
		if (question == null)
			return null;
		if (tokens == null ) {
			tokens = parser.tokenizerString(processedQuestion);
		}
		return tokens;
	}
	
	/**
	 * set the question dependency
	 * 
	 * @param questionDependency
	 */
	public void setDependecy (List<TypedDependency> questionDependency) {
		this.questionDependency =  questionDependency;
	}
	
	/**
	 * get the question dependency
	 * 
	 * @return question dependency
	 */
	public List<TypedDependency> getDependency ()  {
		if (question == null)
			return null;
		if (questionDependency == null ) {
			questionDependency = parser.getDependenciesCCprocessed(getTokens());
		}
		return questionDependency;
	}
	
	public List<TypedDependency> getDependencies (List<? extends HasWord> words )  {
		if (question == null)
			return null;
		if (questionDependency == null ) {
			questionDependency = parser.getTypedDependencies(words);
		}
		return questionDependency;
	} 
	
	public StanfordParser getStanfordParser () {
		return parser;
	}
	
	public List<TypedDependency> getDependencyCCprocessed (List<? extends HasWord> words )  {
		if (question == null)
			return null;
		if (questionDependency == null ) {
			questionDependency = parser.getDependenciesCCprocessed(words);
		}
		return questionDependency;
	}
	
	/**
	 * set the question tagged words
	 * 
	 * @param taggedWords
	 */
	public void setTaggedWord (List<TaggedWord> taggedWords) {
		this.taggedWords = taggedWords;
	}

	/**
	 * get the question tagged words
	 * 
	 * @return the tagged words
	 */
	public List<TaggedWord> getTaggedWord () {
		if (question == null)
			return null;
		if (this.taggedWords == null) {
			taggedWords = parser.getTaggedWords(getTokens());
		}
		return taggedWords;
	}
	
	
	
}
