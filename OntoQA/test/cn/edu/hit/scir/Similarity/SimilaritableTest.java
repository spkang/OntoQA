/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.Similarity;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月8日 
 */
public class SimilaritableTest {
	Similaritable sim = new CharBasedSimilarity ();
	@Test
	public void test() {
		System.out.println (sim.getSimilarity("中国", "美国"));
		System.out.println (sim.getSimilarity("中国", "国家"));
		System.out.println (sim.getSimilarity("中国", "日本"));
	}

}
