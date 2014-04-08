/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.dependency;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 *	test the question target recognizer 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月5日
 */
public class QuestionTargetRecognizerTest {
	private static Logger logger = Logger.getLogger(QuestionTargetRecognizerTest.class);
	private QuestionTargetRecognizer qtRecognizer = QuestionTargetRecognizer.getInstance();
	
	@Before
	public void setUp () throws Exception {}
	
	@After 
	public void tearDown () throws Exception {}

	@Test
	public void test() {
		String [] qts = {"what are the major cities in ohio ?",
				  "which states does the chattahoochee river run through ?", 
				  "what states border states that border states that border states that border texas ?",
				  "what state is columbus the capital of ?"};
		String [] expected = {"null", "null", "null", "null"};
		for (int i = 0; i < qts.length; ++i ) {
			//String target = qtRecognizer.recognizeQuestionTarget(qts[i], );
			//logger.info("target : " + target);
			//assertEquals(expected[i], target);
		}
	}

}
