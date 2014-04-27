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

import cn.edu.hit.ir.ontology.Ontology;

/**
 * PrefixToEntityMap tests.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-27
 */

public class PrefixToEntitiesMapTest {
	
	PrefixToEntitiesMap peMap = new PrefixToEntitiesMap();

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
	
	public void testGetEntitySet(String phrase) {
		Set<Entity> entitySet = peMap.getEntitySet(phrase);
		System.out.println(phrase + ": " + entitySet);
	}
	
	@Test
	public void testGetEntitySet() {
		Ontology ontology = Ontology.getInstance();
		peMap.indexOntology(ontology);
		
		testGetEntitySet("texas");
		testGetEntitySet("clark");
		testGetEntitySet("clark fork");
		testGetEntitySet("fork");
		
		testGetEntitySet ("rhode island");
		testGetEntitySet ("rhode_island");
		testGetEntitySet ("island rhode");
		testGetEntitySet ("island_rhode");
		testGetEntitySet ("rhode");
		testGetEntitySet ("island");
		testGetEntitySet ("state");
		testGetEntitySet ("new york");
		testGetEntitySet ("new");
		testGetEntitySet ("york");
		testGetEntitySet ("york new");
		testGetEntitySet ("run through");
		testGetEntitySet ("run");
		testGetEntitySet ("through");
		testGetEntitySet ("austin texas");
		testGetEntitySet ("austin");
		testGetEntitySet ("texas");
		testGetEntitySet ("texas austin");
		testGetEntitySet ("delaware river");
		testGetEntitySet ("delaware");
		testGetEntitySet ("river delaware");
		testGetEntitySet ("high");
		testGetEntitySet ("high point");
		testGetEntitySet ("point");
		testGetEntitySet ("oregon");
	}

}
