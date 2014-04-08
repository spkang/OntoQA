package cn.edu.hit.scir.dependency;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;

public interface StanfordNlpTool {
	
	/**
	 * A model-based tokenizer used to prepare a sentence for POS tagging.
	 * 
	 * @param sentence sentence to tokenize
	 * @return array of tokens or <code>null</code>, if the tokenizer is not
	 * 		   initialized
	 */
	
	public List<CoreLabel> tokenize (String sentence );
	
	
	/**
	 * return the tokens by String array type
	 *
	 * @param  sentence , the sentence which is going to processed
	 * @return String[] 
	 */
	public String[] token (String sentence );
	
	/**
	 * Assigns POS tags to an array of tokens that form a sentence by using the stanford tagger
	 * 
	 * @param tokens array of tokens to be annotated with POS tags
	 * @return array of POS tags or <code>null</code>, if the tagger is not
	 * 		   initialized
	 */
	public String[] tag(List<CoreLabel> tokens);
	
	
	/**
	 * get the default tags from the stanford parser
	 *
	 * @param List<CoreLabel>, tokens, the tokens of the sentence
	 * @return String[] , tags of the input sentence 
	 */
	public String [] defaultTag (List<CoreLabel> tokens);
	
	/**
	 * Assigns POS tags to a sentence of space-delimited tokens.
	 * 
	 * @param sentence sentence to be annotated with POS tags
	 * @return tagged sentence or <code>null</code>, if the tagger is not
	 * 		   initialized
	 */
	public String[] tag(String sentence);
	
	/**
	 * get the default tags from the stanford parser by the given sentence
	 *
	 * @param String, sentence, the sentence to tag.
	 * @return String[], the tags of the input sentnce 
	 */
	public String[] defaultTag (String sentence);
	
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
	
	
	/**
	 *	tag the sentence and return the tagged word list
	 *
	 * @param  sentence, the processed question
	 * @return List<TaggedWord> , the tagged words
	 */
	public List<TaggedWord> taggedWord  (String sentence);
	
	
	/**
	 * get default tagged word from the stanford parser
	 *
	 * @param String, sentence, the sentence to tagged
	 * @return List<TaggedWord>, the tagged words list 
	 */
	public List<TaggedWord> defaultTaggedWord (String sentence);
	
	
	/**
	 *	using the tagged words to get a dependency relations
	 *
	 * @param  words, the tagged words
	 * @return List<TypedDependency> 
	 */
	public List<TypedDependency> typedDependenciesCCprocessed(List<? extends HasWord> taggedWords);
	
	
	/**
	 * using the tagged words to get the typed dependencies which not CCprocessed
	 *
	 * @param words, the taggedwords
	 * @return List<TypedDependency> 
	 */
	public List<TypedDependency> typedDependencies (List<? extends HasWord> taggedWords); 

	
}
