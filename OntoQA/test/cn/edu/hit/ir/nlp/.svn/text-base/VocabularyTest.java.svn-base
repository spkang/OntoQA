/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-17
 */

public class VocabularyTest {
	
	Vocabulary voc = Vocabulary.getInstance();

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
	public void testIsSmallWord() {
		assertTrue(voc.isSmallWord("small"));
		assertTrue(voc.isSmallWord("short"));
		assertFalse(voc.isSmallWord("big"));
		assertFalse(voc.isSmallWord("long"));
	}

}
