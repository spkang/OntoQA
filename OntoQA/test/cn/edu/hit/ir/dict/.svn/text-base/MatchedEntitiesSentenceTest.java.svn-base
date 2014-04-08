/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-6
 */

public class MatchedEntitiesSentenceTest {

	QuestionAnalyzer analyzer = new QuestionAnalyzer();
	
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

	public void testBestMatch(String question) {
		System.out.println("question: " + question);
		MatchedEntitiesSentence sentence = analyzer.getMatchedEntitiesSentence(question);
		System.out.println(sentence);
	}

	//@Test
	public void testBestMatch() {
		System.out.println("@testBestMatch");
		
		/*testBestMatch("give me the cities in virginia .");
		testBestMatch("name all the rivers in colorado .");
		testBestMatch("what are the high points of states surrounding mississippi ?");
		testBestMatch("how high is the highest point in montana ?");
		testBestMatch("how big is alaska ?");
		testBestMatch("how many major cities are in arizona ?");
		testBestMatch("how many people live in austin ?");
		testBestMatch("how many people live in austin texas ?");*/
		testBestMatch("how long is the delaware river ?");
	}
	
	@Test
	public void testMergeResources() {
		System.out.println("@testMergeResources");
		
		testBestMatch("how long is the delaware river ?");
		testBestMatch("how long is the river of delaware ?");
		testBestMatch("What is the population of the New York city?");
		testBestMatch("What is the population of the city of New York?");
	}
}
