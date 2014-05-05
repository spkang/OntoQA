/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ltp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月5日 
 */
public class LtpToolTest {

	private LtpTool tool = LtpUtil.getInstance();
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("set up");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println ("tear down");
	}
	
	@Test
	public void testLtpTool () {
		testLtpTool ("爱上一匹野马，家里却没有那草原！");
		testLtpTool ("刘德华发行过哪些专辑？");
	}
	
	private void testLtpTool (String sentence ) {
		System.out.println (tool.ltpSegment(sentence));
		System.out.println (tool.ltpTag(sentence));
		System.out.println (tool.ltpSegmentTag(sentence));
	} 

}
