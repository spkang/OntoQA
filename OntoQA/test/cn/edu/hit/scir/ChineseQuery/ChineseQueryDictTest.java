/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月13日 
 */
public class ChineseQueryDictTest {

	ChineseQueryDict dict = ChineseQueryDict.getInstance();
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("set up Dict test");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println ("tear down Dict test");
	}
	
	public void containQW (String sentence ) { 
		System.out.println ("sentence : " + sentence );
		System.out.println(sentence + " is contain query word : " + dict.containsQueryWord(sentence));
	}
	
	@Test
	public void test() {
		
		System.out.println ("dict : " + dict.getQueryDict().toString());
		containQW ("什么时候");
	}

}
