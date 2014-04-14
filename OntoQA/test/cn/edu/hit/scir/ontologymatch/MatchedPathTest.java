/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import org.junit.Test;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月10日 
 */
public class MatchedPathTest {

	MatchedPath mpath = null;
	
	@Test
	public void test() {
		testMatchedPath ("how many rivers are in the mississippi state?");
		testMatchedPath ("how many major cities are in states bordering utah ?");
	}
	
	public void testMatchedPath (String sentence ) {
		mpath = new MatchedPath (sentence);
		mpath.match();
		System.out.println("mpath : \n" + mpath.toString());
	}
	
}
