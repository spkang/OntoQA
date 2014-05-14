/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseEngine;

import static org.junit.Assert.fail;

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
 * @date 2014年5月14日 
 */
public class ChineseQueryAnalyzerTest {

	public ChineseQueryAnalyzer queryAnalyzer = new ChineseQueryAnalyzer();
	
	@Before
	public void set () throws Exception {
		System.out.println ("set up");
	}
	
	@After
	public void tear () throws Exception {
		System.out.println ("tear down");
	}
	
	@Test
	public void test() throws Exception {
		batchFile();
	}
	
	private void batchFile () throws Exception{
		String fileName = "./data/chinesequestion.txt";
		List<String> lines = FileUtils.readLines(new File (fileName));
		int t = 0;
		for (String line : lines ) {
			queryAnalyzer(line);
			if(++t > 1)
				break;
		}
	}
	
	public Object queryAnalyzer  (String query ) {
		Object obj = queryAnalyzer.analyze(query);
		System.out.println("res : " + obj);
		return obj;
	}
}
