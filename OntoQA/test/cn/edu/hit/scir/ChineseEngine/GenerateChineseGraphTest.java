/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseEngine;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.scir.EntityMatcher.ChineseQueryMatchedEntityWrapper;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月14日 
 */
public class GenerateChineseGraphTest {

	Ontology ontology = Ontology.getInstance();
	private GenerateChineseGraph genGraph = new GenerateChineseGraph (ontology);
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("set up graph");
	}

	@After
	public void tearDown () throws Exception {
		System.out.println ("tear down graph ");
	}
	
	public void testGenerateGraph (String query) {
		System.out.println ("query : " + query);
		ChineseQueryMatchedEntityWrapper emWrapper = new ChineseQueryMatchedEntityWrapper (query);
		System.out.println ("matched Entity : ");
		for (List<MatchedEntity> mes : emWrapper.getMatchedQueryWrapper()) {
			System.out.println ("me : " + StringUtils.join(mes, ", "));
		}
		QueryGraph queryGraph = genGraph.optionalMatch(emWrapper);
		if (queryGraph != null )
		System.out.println ("queryGraph : " + queryGraph.toString());
	}
	

	private void batchFile () throws Exception{
		String fileName = "./data/chinesequestion.txt";
		List<String> lines = FileUtils.readLines(new File (fileName));
		int t = 0;
		for (String line : lines ) {
			testGenerateGraph(line);
			if(++t > 15)
				break;
		}
	}
	
	@Test
	public void test() throws Exception {
		//testGenerateGraph ("")
		batchFile();
	}

}
