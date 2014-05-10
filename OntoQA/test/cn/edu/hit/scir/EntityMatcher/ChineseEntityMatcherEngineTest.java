/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月7日 
 */
public class ChineseEntityMatcherEngineTest {

	private ChineseEntityMatcherEngine engine = ChineseEntityMatcherEngine.getInstance();
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("set up");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println ("tear down");
	}
	
	@Test
	public void test() throws Exception{
		System.out.println ("s2eMap : ");
		//engine.queryEntityMatcher("刘德华的专辑《忘不了》是什么时候发行的 ？");
		///engine.queryEntityMatcher("刘德华是什么星座的歌手 ？");
		matcher ();
	}
	
	public void matcher () throws Exception{
		String fileName = "./data/chinesequestion.txt";
		List<String> lines = FileUtils.readLines(new File (fileName));
		for (String line : lines ) {
			engine.queryEntityMatcher(line);
		}
	}

}
