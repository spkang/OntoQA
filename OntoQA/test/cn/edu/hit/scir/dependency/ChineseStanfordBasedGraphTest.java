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
		
		testGraphBuild ("《拯救》是谁唱的");
		testGraphBuild ("唱《心太软》的是谁");
		testGraphBuild ("签约周杰伦的公司是哪个？");
		batchFile ();
		
	}
	
	private void testGraphBuild (String query ) {
		ChineseStanfordBasedGraph graph = new ChineseStanfordBasedGraph (query);
		for (List<MatchedEntity> mes : graph.getQueryWordMatchedEntities()) {
			if (mes == null || mes.isEmpty() )
				continue;
			System.out.println(StringUtils.join(mes, ", "));
		}
		//System.out.println ("Graph : " + graph.toString());
	}

}
