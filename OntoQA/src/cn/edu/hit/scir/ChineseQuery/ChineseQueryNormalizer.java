/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseQuery;

/**
 *  对中文的查询进行预处理。
 *
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月7日 
 */
public class ChineseQueryNormalizer {
	private static ChineseQueryNormalizer instance = new ChineseQueryNormalizer ();
	
	public static ChineseQueryNormalizer getInstance () {
		return instance;
	}
	
	private ChineseQueryNormalizer () {
		
	}
	
	public String removePunctuation (String query) {
		if (query == null || query.isEmpty() )
			return null;
		query = query.replaceAll("[?？。.，，!！“”\" ]", "");
		return query;
	}
}

