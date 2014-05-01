/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

	@Test
	public void testBestMatch() {
		System.out.println("@testBestMatch");
		
		testBestMatch("give me the cities in virginia .");
		testBestMatch("name all the rivers in colorado .");
//		testBestMatch("what are the high points of states surrounding mississippi ?");
//		testBestMatch("how high is the highest point in montana ?");
//		testBestMatch("how big is alaska ?");
//		testBestMatch("how many major cities are in arizona ?");
//		testBestMatch("how many people live in austin ?");
//		testBestMatch("how many people live in austin texas ?");
//		testBestMatch("how long is the delaware river ?");
//		testBestMatch("what is the largest city in rhode island ?");
//		testBestMatch("how many cities are there in the usa ?");
		
	}
	
	//@Test
	public void testMergeResources() {
		System.out.println("@testMergeResources");
		
		testBestMatch("how long is the delaware river ?");
		testBestMatch("how long is the river of delaware ?");
		testBestMatch("What is the population of the New York city?");
		testBestMatch("What is the population of the city of New York?");
	}
	
	
//	@Test
	public void testBatchFiels () throws IOException {
		System.out.println("@testBatchFiles");
		final String inputFileName = "data/output/geoquestions.txt";
		final String outputFileName = "data/output/geoquestionsMathcedEntityBin3.txt";
		List<String> questions = FileUtils.readLines(new File(inputFileName));
		List<String> output = new ArrayList<String>();
		String res = "";
		for (String s : questions) {
			res = "@query : " + s + "\n";
			MatchedEntitiesSentence sentence = analyzer.getMatchedEntitiesSentence(s);
			res += sentence.toString();
			res = res.replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
			output.add(res);
		}
		FileUtils.writeLines(new File (outputFileName), output);
	}
}
