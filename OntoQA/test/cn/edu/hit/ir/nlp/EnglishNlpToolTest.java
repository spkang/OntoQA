/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;


import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-4
 */

public class EnglishNlpToolTest {

	EnglishNlpTool nlpTool = EnglishNlpTool.getInstance();
	
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
	
	public void testStem(String sentence) {
		String[] tokens = nlpTool.tokenize(sentence);
		String[] tagss = nlpTool.tag(tokens);
		String[] tags = nlpTool.tag(sentence);
		String[] stems = nlpTool.stem(tokens, tags);
		System.out.println(sentence);
		System.out.println("tokens: " + StringUtils.join(tokens, ", "));
		System.out.println("tagss : " + StringUtils.join(tagss, ", "));
		System.out.println("tags: " + StringUtils.join(tags, ", "));
		System.out.println("stems: " + StringUtils.join(stems, ", "));
	}
	
	@Test
	public void testStem() {
		testStem("give me the states that border utah ?");
	}

}
