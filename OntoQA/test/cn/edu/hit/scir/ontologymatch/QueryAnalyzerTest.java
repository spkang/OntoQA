/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月17日 
 */
public class QueryAnalyzerTest {

	QueryAnalyzer  queryAnalyzer = new QueryAnalyzer (); // spkang
	QuestionAnalyzer analyzer = new QuestionAnalyzer(); // bin3
	
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
		//System.out.println(isEqual("what state has the smallest area ?"));
		//System.out.println(isEqual("what river flows through the most states ?"));
		/*queryAnalyzer("which state border the most state ?");
		queryAnalyzer("what river flows through the most states ?");
		queryAnalyzer("how big is the city of new york ?");
		queryAnalyzer("which river run through the most states ?");
		queryAnalyzer("which capitals are not major cities ?");
		queryAnalyzer ("which states border no other states ?");
		queryAnalyzer ("which states does not border texas ?");*/
//		queryAnalyzer("which rivers do not run through usa ?");
//		queryAnalyzer("which rivers do not run through texas ?");
//		queryAnalyzer("what is the longest river in the state with the highest point ?");
//		queryAnalyzer("which states have cities named austin ?");
		queryAnalyzer("how many rivers do not traverse the state with the capital albany ?");
	}
	
	public boolean isEqual (String query ) {
		System.out.println ("@isEqual, query : " + query);
		List<String> results1 = (List<String>)queryAnalyzer(query);
		List<String> results2 = (List<String>)queryAnalyzerBin(query);
		
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		
		set1.addAll(results1);
		set2.addAll(results2);
		
		return set1.equals(set2);
	}
	
	public Object queryAnalyzer  (String query ) {
		Object obj = queryAnalyzer.analyze(query);
		System.out.println("res : " + obj);
		return obj;
	}
	
	public Object queryAnalyzerBin  (String query ) {
		Object obj = analyzer.analyze(query);
		System.out.println("bin res : " + obj);
		return obj;
	} 

}
