/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.ir.dict;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.ontology.Ontology;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月25日 
 */
public class SuffixToEntitiesMapTest {
	SuffixToEntitiesMap seMap = new SuffixToEntitiesMap();

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
		Set<Entity> entitySet = seMap.getEntitySet(phrase);
		System.out.println(phrase + ": " + entitySet);
	}
	
	@Test
	public void testGetEntitySet() {
		Ontology ontology = Ontology.getInstance();
		seMap.indexOntology(ontology);
		
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
		testGetEntitySet ("dakota");
	}
}
