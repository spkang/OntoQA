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
 * @date 2014年5月7日 
 */
public class ChineseQueryNormalizerTest {
	
	private ChineseQueryNormalizer normalizer = ChineseQueryNormalizer.getInstance ();
	
	@Before
	public void setUp () throws Exception{
		System.out.println ("set up");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println("tear down");
	}
	
	@Test
	public void test() {
		System.out.println (normalizer.removePunctuation("中国人  ，。阿的，。，。！？。，，，"));
	}
	
}
