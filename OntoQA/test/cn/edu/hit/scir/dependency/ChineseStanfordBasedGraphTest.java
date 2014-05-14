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

import cn.edu.hit.ir.dict.MatchedEntity;
import edu.stanford.nlp.util.StringUtils;

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
	
	private void batchFile () throws Exception{
		String fileName = "./data/chinesequestion.txt";
		List<String> lines = FileUtils.readLines(new File (fileName));
		int t = 0;
		for (String line : lines ) {
			testGraphBuild(line);
//			if(++t > 15)
//				break;
		}
	}
	
	@Test
	public void test() throws Exception {
		
//		testGraphBuild ("《拯救》是谁唱的");
//		testGraphBuild ("唱《心太软》的是谁");
//		testGraphBuild ("签约周杰伦的公司是哪个？");
//		testGraphBuild ("天秤座的歌手有哪些？");
		testGraphBuild ("刘德华是哪里的歌手");
		testGraphBuild ("刘德华的专辑忘不了的是什么时候发行的");
		testGraphBuild ("唱七里香的人是谁");
		testGraphBuild ("孙燕姿在2009年后发行了多少专辑");
		testGraphBuild ("陈奕迅即将发行的专辑的叫什么");
		testGraphBuild ("浙江的歌手有哪些");
		testGraphBuild ("吴莫愁是什么星座的");
		testGraphBuild ("周杰伦的唱片公司叫什么");
		//batchFile ();
		
	}
	
	private void testGraphBuild (String query ) {
		ChineseStanfordBasedGraph graph = new ChineseStanfordBasedGraph (query);
		for (List<MatchedEntity> mes : graph.getQueryWordMatchedEntities()) {
			if (mes == null || mes.isEmpty() )
				continue;
//			System.out.println(StringUtils.join(mes, ", "));
			for (MatchedEntity me : mes ) {
				System.out.println ("isTarget : " + me.isQueryTarget() + "\t me : " + me.toString());
			}
		}
		//System.out.println ("Graph : " + graph.toString());
	}

}
