/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.questionanalysis;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * QuestionNormalizer tests.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-25
 */

public class QuestionNormalizerTest {

	QuestionNormalizer qn = QuestionNormalizer.getInstance();
	
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
	public void testNormalize() {
		String[] questions = { "How many rivers does Alaska have?",
				"Give me the cities in Virginia." };
		String[] expecteds = { "how many river do alaska have",
				"give me the city in virginia" };
		for (int i = 0; i < questions.length; i++) {
			String nq = qn.normalize(questions[i]);
			System.out.println("Q:" + questions[i] + ", NQ:" + nq);
			assertEquals(expecteds[i], nq);
		}
	}

}
