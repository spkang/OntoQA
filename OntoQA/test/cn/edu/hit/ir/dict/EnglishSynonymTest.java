/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;


import java.util.Set;

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

public class EnglishSynonymTest {
	
	EnglishSynonym synonym = EnglishSynonym.getInstance();
	
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

	public void testGetSet(String phrase) {
		Set<String> syns = synonym.getSet(phrase);
		System.out.println(phrase + ": " + syns);
	}
	
	public void testGetSet(String phrase, String pos) {
		Set<String> syns = synonym.getSet(phrase, pos);
		System.out.println(phrase + "/" + pos + ": " + syns);
	}

	@Test
	public void testGetSet() {
		testGetSet("how many");
		testGetSet("how many people");
		testGetSet("how many people", "WRB");
		testGetSet("how big");
		testGetSet("tree");
		testGetSet("state");
		testGetSet("country");
		testGetSet("hill");
		testGetSet("mountain");
		testGetSet("many");
		testGetSet("many states");
		testGetSet("surrounding", "VBG");
		testGetSet("surround", "VB");
		testGetSet("surrounding");
		testGetSet("surrounding xxx");
		testGetSet ("in");
		testGetSet ("evelation");
		testGetSet ("evelation", "VB");
		testGetSet ("evelations", "NNS");
		testGetSet("high");
		testGetSet("of");
		testGetSet ("cross");
		testGetSet("run", "VB");
		testGetSet("run through" );
		testGetSet("point" );
		testGetSet("flow" );
		testGetSet("flow", "VB" );
		testGetSet("flow", "VBG" );
		testGetSet("flow through" );
		
	}
}
