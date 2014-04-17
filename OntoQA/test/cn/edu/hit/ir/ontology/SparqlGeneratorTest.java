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

import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-7
 */

public class SparqlGeneratorTest {

	QuestionAnalyzer analyzer;
	
	SparqlGenerator generator;
	
	/**
	 * Creates a new instance of SparqlGeneratorTest.
	 */
	public SparqlGeneratorTest() {
		analyzer = new QuestionAnalyzer();
		generator = new SparqlGenerator(analyzer.getOntology());
		
		generator.addPrefix("http://ir.hit.edu/nli/geo/", "geo");
	}
	
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
	
	public void testGenerate(String question) {	
		//MatchedEntitiesSentence sentence = analyzer.getMatchedEntitiesSentence(question);		
		
		System.out.println("\n@question: " + question);
		//System.out.println(sentence);
		
		QueryGraph queryGraph = analyzer.getQueryGraph(question);
		String sparql = generator.generate(queryGraph);
		System.out.println("\n@sparql");
		System.out.println(sparql);
		System.out.println("------------------------------------------------------------------------------------------------\n");
	}

	@Test
	public void testGenerate() {
		
		testGenerate("how high are the highest points of all the states ?");
		
		testGenerate("how many rivers are in the state that has the most rivers ?");
		
		testGenerate("give me the cities in virginia .");
		testGenerate("name all the rivers in colorado .");
		testGenerate("what are the high points of states surrounding mississippi ?");
		testGenerate("how high is the highest point in montana ?");
		testGenerate("how big is alaska ?");
		testGenerate("how many major cities are in arizona ?");
		testGenerate("how many people live in austin ?");
		testGenerate("how many people live in austin texas ?");
		testGenerate("can you tell me the capital of texas ?");
		testGenerate("give me all the states of usa ?");
		testGenerate("how long is the delaware river ?");
		testGenerate("give me the largest state ?");
		testGenerate("Which river flow over the states surrounding Mississippi?");
		testGenerate("how many cities are in louisiana ?");
		testGenerate("how many major cities are in states bordering nebraska ?");
		testGenerate("how many people are in the state of nevada ?");
		testGenerate("how many major cities are in states bordering nebraska ?");/**/
		
		
		testGenerate("which river runs through the most states?");
		testGenerate("what is the population of new york city?");
		testGenerate("count the states which have elevations lower than what alabama has ?");
		testGenerate("how high is the highest point in Texas ?");
		testGenerate("what are the high points of states surrounding mississippi ?");
		
	}
	
	//@Test
	public void testMaxOrMin() {
		testGenerate("What is the largest state?");
		testGenerate("What is the largest state bordering Texas?");
		testGenerate("What is the smallest state bordering Texas?");
		testGenerate("What is the longest river?");
		testGenerate("What is the longest river runs through Texas?");
		testGenerate("how long is the longest river in the usa ?");
		testGenerate("how long is the shortest river in the usa ?");
		testGenerate("what is the shortest river ?");
		
		testGenerate("What is the most populous state?");
		testGenerate("What is the most dense state?");/**/
	}
	
	//@Test
	public void testMaxOrMin2() {
		testGenerate("what state has the smallest area ?");
	}
	
	@Test
	public void testMaxOrMin3() {
		testGenerate("what is the longest river in the largest state ?");
		testGenerate("which state has the longest river ?");
	}

	//@Test
	public void testName() {
		testGenerate("what is the population of portland maine ?");
		testGenerate("what states have a city named austin ?");
		testGenerate("what states have rivers named colorado ?");
		testGenerate("what is the population of new york city ?");
	}
	
	//@Test
	public void testSrcTgtProp() {
		testGenerate("Which states do Colorado river run through?");
	}
	
	//@Test
	public void testWhere() {
		testGenerate("where is austin ?");
		testGenerate("where is dallas ?");
	}
	
	public void testGenerateSparql(String question) {	
		//MatchedEntitiesSentence sentence = analyzer.getMatchedEntitiesSentence(question);		
		
		System.out.println("\n@question: " + question);
		//System.out.println(sentence);
		
		QueryGraph queryGraph = analyzer.getQueryGraph(question);
		Sparql sparql = generator.generateSparql(queryGraph);
		System.out.println("\n@sparql");
		System.out.println(sparql);
		System.out.println("------------------------------------------------------------------------------------------------\n");
	}
	
	//@Test
	public void testMaxOrMinCount() {
		testGenerateSparql("what state borders the most states ?");
		testGenerateSparql("what rivers traverses the state which borders the most states ?");
	}
	
	
	@Test
	public void testNN () {
		testGenerateSparql ("how many states border the mississippi river ?");
		testGenerateSparql ("how many states does the missouri river run through ?");
		testGenerateSparql ("how big is the city of new york ?");
		testGenerateSparql ("how long is the mississippi river ?");
		testGenerateSparql ("where is the highest mountain of the united states ?");
		testGenerateSparql ("which states does the longest river cross ?");
	}
}
