/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.semanticgraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月31日 
 */
public class SemanticGraphTest {
	private static Logger logger = Logger.getLogger(SemanticGraphTest.class);
	private SemanticGraph sgraph  = null;
	
	@Before
	public void setUp () throws Exception {
		logger.info("setUp");
	}
	
	@After
	public void tearDown () throws Exception {
		logger.info("tearDown");
	}
	//@Test
	public void test() {
//		testSemanticGraph ("how high is the highest point in the largest state ?");
//		testSemanticGraph ("how many states does the colorado_river flow through ?");
//		testSemanticGraph ("how many states have a higher point than the highest point of the state with the largest capital city in the us ?");
//		testSemanticGraph("how many rivers are in the state that has the most rivers ?");
//		testSemanticGraph("what is the population of the capital of the largest state through which the mississippi runs ?");
//		testSemanticGraph("what is the population of tempe arizona ?");
//		testSemanticGraph("how many people live in austin texas ?");
//		testSemanticGraph("how many people live in the state with the largest population density ?");
//		testSemanticGraph("in what state is mount mckinley ?");
//		testSemanticGraph("what are the largest cities in the states that border the largest state ?");
//		testSemanticGraph("what are the major rivers in the us ?");
//		testSemanticGraph("what is the highest elevation in texas ?");
//		testSemanticGraph("what is the longest river that passes the states that border the state that borders the most states ?");
		
		try {
			testBatchFiels ();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testSentence () {
//		logger.info("answer : " + testSemanticGraph("how many states does tennessee border ?"));
//		logger.info("answer : " + testSemanticGraph("name all the rivers in colorado ?"));
//		logger.info("answer : " + testSemanticGraph("what is the longest river that passes the states that border the state that borders the most states ?"));
//		logger.info("answer : " + testSemanticGraph("can you tell me the capital of texas ?"));
//		logger.info("answer : " + testSemanticGraph("give me the longest river that passes through the us ?"));
//		logger.info("answer : " + testSemanticGraph("which states have points that are higher than the highest point in texas ?"));
//		logger.info("answer : " + testSemanticGraph("how many cities named austin are there in the usa ?"));
//		logger.info("\nanswer : \n" + testSemanticGraph ("what rivers run through the states that border the state with the capital atlanta ?"));
//		logger.info("answer : " + testSemanticGraph ("how many cities named austin are there in the usa ?"));
//		logger.info("answer : " + testSemanticGraph ("how many states border on the state whose capital is boston ?"));
//		logger.info("answer : " + testSemanticGraph ("what is the capital city of the largest state in the us ?"));
//		logger.info("answer : " + testSemanticGraph ("what states have no bordering state ?"));
		logger.info("answer : " + testSemanticGraph ("how long is the delaware river ?"));
//		logger.info("answer : " + testSemanticGraph ("how big is new mexico ?"));
//		logger.info("answer : " + testSemanticGraph(" name all the rivers in colorado ?"));
		
//		logger.info("answer : " + testSemanticGraph ("people in boulder ?"));
//		logger.info("answer : " + testSemanticGraph ("rivers in new york ?"));
		
		logger.info("graph : \n" + this.sgraph.toString());
		
		//logger.info("path : " + StringUtils.join(this.sgraph.searchSemanticGraph(0), " -> "));
	}
	
	
	public void testBatchFiels () throws IOException {
		final String inputFileName = "data/output/geoquestions.txt";
		final String outputFileName = "data/output/geoquestions.path_expand_noun";
		List<String> questions = FileUtils.readLines(new File(inputFileName));
		List<String> output = new ArrayList<String>();
	
		for (String s : questions) {
			output.add("@question : " + s + "\n" + testSemanticGraph(s) + "\n");
		}
		FileUtils.writeLines(new File (outputFileName), output);
	}

	public String testSemanticGraph (String sentence ) {
		logger.info("\n@Sentence : " + sentence);
		sgraph = new SemanticGraph  (sentence);
		logger.info("\nAnswers : \n" + sgraph.buildSemanticGraph());
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < this.sgraph.getGraphSize()  ; ++i) {
			res = this.sgraph.searchSemanticGraph(i);
			if (res != null && !res.isEmpty() )
				break;
		}
		StringBuffer bf = new StringBuffer ();
		if (res != null)
			bf.append(StringUtils.join(res, "->"));
		else 
			bf.append("null");
		return bf.toString();
	} 
	
}
