/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.demo;


import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * ResultTemplate tests.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-21
 */

public class ResultTemplateTest {
	
	Ontology ontology = Ontology.getInstance();
	QuestionAnalyzer analyzer = new QuestionAnalyzer();

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
	
	public String getResult(String query) {
		String sparql = analyzer.getSparql(query);
		List<RDFNode> results = ontology.getResultNodes(sparql);
		if (results != null && results.size() > 0) {
			ResultTemplate template = new ResultTemplate(ontology);
			template.addAnswerNodes(results);
			template.addSparql(sparql);
			
			RDFNodesTable table = new RDFNodesTable(ontology, results);
			template.addEntityTable(table);
			
			return template.toString();
		} else {
			return ResultTemplate.getNoAnswerResult();
		}
	}
	
	public void testAll(String query) {
		System.out.println("query: " + query);
		String result = getResult(query);
		System.out.println("result:");
		System.out.println(result);
	}
	
	@Test
	public void testAll() {
		testAll("texas");
		testAll("the biggest city in texas");
		testAll("the biggest city");
	}

}
