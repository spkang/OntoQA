/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月17日 
 */
public class QueryAnalyzerTest {

	QueryAnalyzer  queryAnalyzer = new QueryAnalyzer ();
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("set up !");
	}
	
	@After
	public void tearDown () throws Exception{
		System.out.println ("tear dowon !");
	}
	
	@Test
	public void testQueryAnalyzer () {
		System.out.println ("@testQueryAnalyzer");
//		queryAnalyzer("how many rivers in mississippi?");
//		queryAnalyzer("name all the rivers in mississippi?");
//		queryAnalyzer("which state border the most state ?");
		queryAnalyzer("what state has the smallest area ?");
	}
	
	public void queryAnalyzer  (String query ) {
		Object obj = queryAnalyzer.analyze(query);
		System.out.println("res : " + obj);
	}

}
