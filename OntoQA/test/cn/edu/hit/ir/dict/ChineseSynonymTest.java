/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.ir.dict;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月8日 
 */
public class ChineseSynonymTest {

	Synonym syn = ChineseSynonym.getInstance();
	
	@Test
	public void test() {
		testSyn ("天秤座");
		testSyn ("水瓶座");
		testSyn ("星座");
	}
	
	public void testSyn (String word ) {
		for (String s : syn.getSet(word)) {
			System.out.println ("s : " + s);
		}
	}

}
