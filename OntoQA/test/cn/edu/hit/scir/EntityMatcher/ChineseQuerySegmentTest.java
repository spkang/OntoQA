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

import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月11日 
 */
public class ChineseQuerySegmentTest {

	@Before
	public void setUp () throws Exception {
		System.out.println("set up");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println("tear down");
	}
	
	public void test(String query) {
		System.out.println("query : " + query);
		ChineseQuerySegment seg = new ChineseQuerySegment (query);
		List<String> wds = seg.getWords(); 
		List<String> mws = seg.getMergedWords();
		/*wds.set(wds.size()-1, wds.get(wds.size()-1) + "啊");
		seg.setWords(wds);
		System.out.println ("wds : " + StringUtils.join(wds, ", "));
		System.out.println ("mws : " + StringUtils.join(mws, ", "));
		System.out.println ("mqs : " + seg.getMergedQuery());
		
		
		
		seg.splitWord ();
		*/
		System.out.println ("wds : " + StringUtils.join(wds, ", "));
		System.out.println ("mws : " + StringUtils.join(mws, ", "));
		System.out.println ("mqs : " + seg.getMergedQuery());
	}
	
	
	@Test
	public void testSplit () {
		test("张信哲发行了多少张专辑");
		test("刘德华是什么星座的歌手");
	}
	
//	@Test
	public void matcher () throws Exception{
		String fileName = "./data/chinesequestion.txt";
		List<String> lines = FileUtils.readLines(new File (fileName));
		for (String line : lines ) {
			test(line);
		}
	}

}
