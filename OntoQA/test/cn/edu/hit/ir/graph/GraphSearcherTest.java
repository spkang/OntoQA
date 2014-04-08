/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;


import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.dict.MatchedEntitiesSentence;
import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-7
 */

public class GraphSearcherTest {

	QuestionAnalyzer analyzer;
	
	GraphSearcher graphSearcher;
	
	/**
	 * Creates a new instance of GraphSearcherTest.
	 */
	public GraphSearcherTest() {
		analyzer = new QuestionAnalyzer();
		graphSearcher = new GraphSearcher(analyzer.getOntology());
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

	public void testBestMatch(String question) {
		System.out.println("\nquestion: " + question);
		MatchedEntitiesSentence sentence = analyzer.getMatchedEntitiesSentence(question);
		QueryGraph queryGraph = graphSearcher.bestMatch(sentence);
		System.out.println("sentence:\n" + sentence);
		System.out.println("queryGraph:\n" + queryGraph);
		
		List<QueryGraph> graphs = graphSearcher.getGraphs();
		System.out.println("\ngraphs:");
		for (QueryGraph graph : graphs) {
			System.out.println(graph);
		}
		System.out.println("\nbest query graph is : " );
		QueryGraph bestQG = graphSearcher.bestMatch(sentence);
		System.out.println(bestQG);
		System.out.println("------------------------------------------------------------------------------------------------\n");
		
		
	}

	@Test
	public void testBestMatch() {
		System.out.println("@testBestMatch");
		
		testBestMatch("give me the cities in virginia .");
		testBestMatch("how many people live in austin ?");
		testBestMatch("name all the rivers in colorado .");
		testBestMatch("what are the high points of states surrounding mississippi ?");
		testBestMatch("how big is alaska ?");
		testBestMatch("how many major cities are in arizona ?");
		testBestMatch("how many people live in austin ?");
		testBestMatch("how many people live in austin texas ?");
		testBestMatch("can you tell me the capital of texas ?");
		testBestMatch("give me all the states of usa ?");
		testBestMatch("give me the largest state ?");
		testBestMatch("Which river flow over the states surrounding Mississippi?");
		testBestMatch("how long is the delaware river ?");
		testBestMatch("How high is the highest point in Texas?");
		testBestMatch("how many rivers run through the states bordering colorado ?");
		testBestMatch("how many major cities are in states bordering nebraska ?");
		testBestMatch("can you tell me the capital of texas ?");
		testBestMatch("what's the highest point of Texas");
		testBestMatch("how high is the highest point in montana ?");
		testBestMatch("how big is the city of new york ?");
		
		testBestMatch("what states have cities named dallas ?");
		testBestMatch("what is the population of portland maine ?");
		testBestMatch("what states have a city named austin ?");
	}
}
