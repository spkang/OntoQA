/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

import java.util.Set;

import net.didion.jwnl.data.POS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.nlp.WordNet;

/**
 * Tests for the {@link WordNet} class.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-17
 */

public class WordNetTest {
	
	WordNet wordNet = WordNet.getInstance();

	/**
	 * TODO
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * TODO
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/*private void testGetSentSimilarity(String aSent, String bSent) {
		System.out.println("aSent: " + aSent);
		System.out.println("bSent: " + bSent);
		System.out.println("sim: " + wordNet.getSentSimilarity(aSent, bSent));
	}

	@Test
	public void testGetSentSimilarity() {
		testGetSentSimilarity("apple", "banana");
		testGetSentSimilarity("apple", "tree");
		testGetSentSimilarity("apple", "people");
		testGetSentSimilarity("apple", "computer");
		testGetSentSimilarity("i like apple", "i love you");
		testGetSentSimilarity("i like apple", "it is book");
		testGetSentSimilarity("how long", "length");
		testGetSentSimilarity("how large", "area");
	}
	
	private void testGetAttributes(String word) throws JWNLException {
		List<String> attrWords = wordNet.getAttributeWords(word);
		System.out.println(word + ":\t" + attrWords.toString());
	}
	
	@Test
	public void testGetAttributes() {
		System.out.println("@testGetAttributes");
		try {
			testGetAttributes("length");
			testGetAttributes("long");
			testGetAttributes("high");
			testGetAttributes("large");
			testGetAttributes("area");
			testGetAttributes("how long");
			testGetAttributes("how large");
			testGetAttributes("where");
			
			testGetAttributes("elevation");
			testGetAttributes("density");
			testGetAttributes("build");
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void testGetSynonyms(String word, POS pos) {
		System.out.print(word + "/" + pos + ": ");
		String[] synonyms = wordNet.getSynonyms(word, pos);
		if (synonyms != null) {
			for (int i = 0; i < synonyms.length; i++) {
				System.out.print(synonyms[i] + ", ");
			}
		}
		System.out.println();
	}
	
	public void testGetSynonyms(String word) {
		System.out.print(word + ": ");
		Set<String> synonyms = wordNet.getSynonymSet(word);
		System.out.println(synonyms);
		testGetSynonyms(word, WordNet.NOUN);
	}
	
	//@Test
	public void testGetSynonyms() {
		System.out.println("@testGetSynonyms");
		
		testGetSynonyms("tree");
		testGetSynonyms("state");
		testGetSynonyms("country");
		testGetSynonyms("hill");
		testGetSynonyms("mountain");
		testGetSynonyms("many state");
		testGetSynonyms("many states");
		testGetSynonyms("city named");
		testGetSynonyms("city name");
		testGetSynonyms("surrounding");
		testGetSynonyms("surround");
		testGetSynonyms("surround", WordNet.VERB);
		testGetSynonyms("border");
		testGetSynonyms("bordering");
	}
	
	public void testGetSimilarity(String word1, String word2) {
		double ws = wordNet.getSimilarity(word1, word2);
		System.out.println(word1 + ", " + word2 + ": " + ws);
	}
	
	public void testGetSimilarity(String word1, String word2, String pos) {
		double ws = wordNet.getSimilarity(word1, word2, pos);
		System.out.println(word1 + ", " + word2 + ", " + pos + ": " + ws);
	}
	
	public void testGetSimilarity(String[] sentence1, String[] sentence2) {
		double ss = wordNet.getSimilarity(sentence1, sentence2);
		System.out.println(sentence1 + ", " + sentence2 + ": " + ss);
	}
	
	public void testGetSentencesSimilarity(String sentence1, String sentence2) {
		double ss = wordNet.getSentencesSimilarity(sentence1, sentence2);
		System.out.println(sentence1 + ", " + sentence2 + ": " + ss);
	}

	//@Test
	public void testGetSimilarity() {
		testGetSimilarity("apple", "banana");
		testGetSimilarity("state", "country");
		testGetSentencesSimilarity("walk", "run through");
	}
	
	public void testIsCompoundWord(String word) {
		System.out.println(word + ": " + wordNet.isCompoundNoun(word));
	}

	//@Test
	public void testIsCompoundWord() {
		System.out.println("@testIsCompoundWord");
		
		testIsCompoundWord("border");
		testIsCompoundWord("surround");
		testIsCompoundWord("surrounding");
		testIsCompoundWord("surrounding mississippi");
		testIsCompoundWord("surrounding xxx");
		testIsCompoundWord("may be");
		testIsCompoundWord("have to");
	}
	
	//@Test
	public void testMaxMin() {
		System.out.println("@testMaxMin");
		testGetSimilarity("longest", "maximum");
		testGetSimilarity("longest", "minimum");
		testGetSimilarity("shortest", "maximum");
		testGetSimilarity("shortest", "minimum");
		testGetSimilarity("long", "maximum", "a");
		testGetSimilarity("long", "great", "a");
		testGetSimilarity("long", "little", "a");
	}
	
	public void testGetAttributes(String word) {
		Set<String> attributes = wordNet.getAttributes(word);
		System.out.println(word + ": " + attributes);
	}

	@Test
	public void testGetAttributes() {
		System.out.println("@testGetAttributes");
		
		testGetAttributes("length");
		testGetAttributes("long");
		testGetAttributes("short");
		testGetAttributes("high");
		
		testGetAttributes("large");
		testGetAttributes("big");
		testGetAttributes("area");
		
		//testGetSimilarity("size", "area");
		//testGetSimilarity("size", "population");
		//testGetSimilarity("size", "name");
		
		testGetAttributes("how long");
		testGetAttributes("how large");
		testGetAttributes("where");
		
		testGetAttributes("elevation");
		testGetAttributes("density");
		testGetAttributes("dense");
		testGetAttributes("population");
		testGetAttributes("populous");
		testGetSimilarity("density", "dense");
		testGetSimilarity("population", "populous");
	}
}
