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

import cn.edu.hit.ir.ontology.Ontology;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class EntityMatcherTest {
	
	Ontology ontology = Ontology.getInstance();
	
	EntityMatcher entityMatcher = new EntityMatcher();

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
	
	public void testMatch(String query) {
		MatchedEntitiesSentence mel = entityMatcher.match(ontology, query);
		System.out.println("\nquery: " + query);
		System.out.println(mel);
	}

	@Test
	public void testMatch() {
		testMatch("state");
		testMatch("what are the high points of states surrounding mississippi ?");
		testMatch("give me the states that border utah ?");
		testMatch("how many states have a city named springfield ?");
		testMatch("how big is the city of new york ?");
		testMatch("texas");
		testMatch("bin3");
		testMatch("mississippi");
		testMatch("state");
		testMatch("country");
		testMatch("give me the cities in virginia .");
		testMatch("name the rivers in arkansas .");
		testMatch("how big is the city of new york ?");
		testMatch("how high is the highest point in montana ?");
		testMatch("what states border states that the mississippi runs through ?");
		testMatch("what states border states that the mississippi flows over ?");
		testMatch("how many people are in the state of nevada ?");
		testMatch("how many people are in the nevada state?");
	}
	
	@Test
	public void testMerge() {
		testMatch("how many people are in the state of nevada ?");
		testMatch("how many people are in the nevada state?");
		testMatch("mississippi");
		testMatch("mississippi state");
		testMatch("state");
		testMatch("point");
		testMatch("high point");
		testMatch ("name");
	}
}
