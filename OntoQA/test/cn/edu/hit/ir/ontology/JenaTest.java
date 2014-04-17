/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Jena tests.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class JenaTest {
	
	public static final String DATA_DIR = "test/data/";
	
	Jena jena = new Jena();

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
	
	public void testSearch(String query) {
		RDFNode[] nodes = jena.search(query);
		System.out.println("\nquery: " + query);
		if (nodes != null) {
			for (int i = 0; i < nodes.length; i++) {
				System.out.println(nodes[i].toString());
			}
		} else {
			System.out.println("No result.");
		}
	}
	
	@Test
	public void testRead() {
		String businessFilename = DATA_DIR + "business.rdf";
		String geoFilename = DATA_DIR + "geobase.rdf";
		
//		testSearch("customers");
//		jena.read(businessFilename, "http://localhost:2020/vocab/resource/");
//		testSearch("customers");
//		testSearch("texas");
		jena.read(geoFilename);
		testSearch("customers");
		testSearch("state_texas");
		testSearch("mississippi");
		testSearch("river");
		testSearch("mississippi river");
		testSearch("river mississippi");
		testSearch("river_mississippi");
		testSearch("mississippi_river");
		testSearch("state_mississippi");
		testSearch("rio grande");
		testSearch("grande rio");
		testSearch("new york");
		testSearch("new york city");
		testSearch("city_new_york");
		testSearch("point");
		testSearch("geo points");
	}

}
