/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.util.List;

import cn.edu.hit.ir.dict.MatchedEntity;

/**
 * 中文匹配实体的容器	 
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月8日 
 */
public class ChineseQueryMatchedEntityWrapper {
	private List<List<MatchedEntity>> orgMatchedQuery = null;
	private List<List<MatchedEntity>> mergedQuery = null;
	
	private ChineseEntityMatcherEngine cqmEngine = ChineseEntityMatcherEngine.getInstance();
	
	/**
	 * wrapper 构造函数， 对输入的query进行实体匹配
	 *
	 * @param
	 * @return 
	 */
	public ChineseQueryMatchedEntityWrapper (String query ) {
		initResource (query);
	}
	
	private void initResource (String query ) {
		cqmEngine.queryEntityMatcher(query);
		this.orgMatchedQuery = cqmEngine.getMatchedQuery();
	}

	public List<List<MatchedEntity>> getOrgMatchedQuery() {
		return orgMatchedQuery;
	}

	public void setOrgMatchedQuery(List<List<MatchedEntity>> orgMatchedQuery) {
		this.orgMatchedQuery = orgMatchedQuery;
	}

	public List<List<MatchedEntity>> getMergedQuery() {
		return mergedQuery;
	}

	public void setMergedQuery(List<List<MatchedEntity>> mergedQuery) {
		this.mergedQuery = mergedQuery;
	}
	
	public boolean isIndexLegal (int index ) {
		if (index < 0 || index >= this.mergedQuery.size())
			return false;
		return true;
	}
	
	public boolean hasNext (int index) {
		if (isIndexLegal (index))
			return true;
		return false;
	}
	
	public int nextIndex (int index ) {
		if (isIndexLegal (index  + 1))
			return  index + 1;
		return -1;
	}
	
	public List<MatchedEntity> getEntities (int index ) {
		if (isIndexLegal (index)) {
			return this.mergedQuery.get(index);
		}
		return null;
	} 
	
}
