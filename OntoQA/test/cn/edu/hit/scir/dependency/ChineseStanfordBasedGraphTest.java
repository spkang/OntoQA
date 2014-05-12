/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

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
 * @date 2014年5月12日 
 */
public class ChineseStanfordBasedGraphTest {

	@Before
	public void setup () throws Exception {
		System.out.println ("set up");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println ("tear down");
	}
	
	
	@Test
	public void test() throws Exception {
		String fileName = "./data/chinesequestion.txt";
		List<String> lines = FileUtils.readLines(new File (fileName));
		int t = 0;
		for (String line : lines ) {
			testGraphBuild(line);
			if(++t > 10)
				break;
		}
	}
	
	private void testGraphBuild (String query ) {
		ChineseStanfordBasedGraph graph = new ChineseStanfordBasedGraph (query);
	}

}