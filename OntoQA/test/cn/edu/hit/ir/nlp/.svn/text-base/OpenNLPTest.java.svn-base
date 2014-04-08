/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;


import static org.junit.Assert.*;

import opennlp.tools.parser.Parse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.nlp.OpenNLP;

/**
 * Tests for the {@link OpenNLP} class.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-15
 */

public class OpenNLPTest {
	private OpenNLP openNLP = OpenNLP.getInstance();

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
	
	@Test
	public void testParse() {
		String sentence = "She was just another freighter from the States , and she seemed as commonplace as her name . ";
		String expected = "(TOP (S (S (NP (PRP She)) (VP (VBD was) (ADVP (RB just)) (NP (NP (DT another) (NN freighter)) (PP (IN from) (NP (DT the) (NNP States)))))) (, ,) (CC and) (S (NP (PRP she)) (VP (VBD seemed) (ADJP (ADJP (RB as) (JJ commonplace)) (PP (IN as) (NP (PRP$ her) (NN name)))))) (. .)))";
		
		//openNLP.createParser(OpenNLP.DEFAUL_PARSER_MODEL);
		Parse parse = openNLP.parse(sentence);
		StringBuffer parseString = new StringBuffer();
	    parse.show(parseString);
		String actual = parseString.toString();
		
		System.out.println("@testParse actual:" + actual);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSentDetect() {
		//openNLP.createSentenceDetector(OpenNLP.DEFAUL_SENT_MODEL);
		String sampleSentences1 = "This is a test. There are many tests, this is the second.";
	    String[] sents = openNLP.sentDetect(sampleSentences1);
	    assertEquals(sents.length,2);
	    assertEquals(sents[0],"This is a test.");
	    assertEquals(sents[1],"There are many tests, this is the second.");
	}
	
	@Test
	public void testTokenize() {
		String sentence = "The driver got badly injured.";
		String[] expecteds = {"The", "driver", "got", "badly", "injured", "."};
		
		//openNLP.createTokenizer(OpenNLP.DEFAUL_TOKEN_MODEL);
		String actuals[] = openNLP.tokenize(sentence);

		assertArrayEquals(expecteds, actuals);
	}
	
	@Test
	public void testTag() {
		String sentence = "The driver got badly injured .";
		String[] tokens = {"The", "driver", "got", "badly", "injured", "."};
		String[] expecteds = {"DT", "NN", "VBD", "RB", "VBN", "."};
		
		//openNLP.createPosTagger(OpenNLP.DEFAUL_POS_MODEL);
		//openNLP.createTokenizer(OpenNLP.DEFAUL_TOKEN_MODEL);
		String[] actuals1 = openNLP.tag(sentence);
		String[] actuals2 = openNLP.tag(tokens);
		assertArrayEquals(expecteds, actuals1);
		assertArrayEquals(expecteds, actuals2);
	}

	@Test
	public void testChunk() {
		String[] tokens = { "Rockwell", "said", "the", "agreement", "calls", "for",
				"it", "to", "supply", "200", "additional", "so-called", "shipsets",
				"for", "the", "planes", "." };

		String[] tags = { "NNP", "VBD", "DT", "NN", "VBZ", "IN", "PRP", "TO", "VB",
				"CD", "JJ", "JJ", "NNS", "IN", "DT", "NNS", "." };

		String[] expecteds = { "B-NP", "B-VP", "B-NP", "I-NP", "B-VP", "B-SBAR",
				"B-NP", "B-VP", "I-VP", "B-NP", "I-NP", "I-NP", "I-NP", "B-PP", "B-NP",
				"I-NP", "O" };
		
		//openNLP.createChunker(OpenNLP.DEFAUL_CHUNKER_MODEL);
		String[] actuals = openNLP.chunk(tokens, tags);

		assertArrayEquals(expecteds, actuals);
	}
	
}
