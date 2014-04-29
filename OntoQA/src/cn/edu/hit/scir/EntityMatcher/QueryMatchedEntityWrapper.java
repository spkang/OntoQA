/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.util.List;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.scir.semanticgraph.DependencyGraph;
import cn.edu.hit.scir.semanticgraph.SemanticGraph;

/**
 * 对EntityMatcherEngine匹配的实体进行封装
 * 这个类提供的功能：
 * 	1. 存储匹配的实体列表
 * 	2. 根据用户输入的index返回实体列表
 * 	3. 判断是否存在下一个实体列表
 * 	4. 判断是否存在前一个实体列表
 * 	5. 提供某一个实体是否存在修饰短语
 * 		如： 最高级修饰、否定修饰等
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月29日 
 */
public class QueryMatchedEntityWrapper {
	// 存储query最终匹配的实体列表
	private List<List<MatchedEntity>> matchEntityWrapper = null;
	
	// 没有经过实体修正的匹配的实体列表
	private List<List<MatchedEntity>> orgMatchedQuery = null;
	
	// query对应的依存图
	private DependencyGraph depGraph = null;
	
	// query 对应的语义图
	private SemanticGraph smtcGraph = null;
	
	// 用于求取query匹配实体的引擎
	private EntityMatcherEngine emEngine = EntityMatcherEngine.getInstance();
	
	/**
	 * 构造函数，通过用户输入的查询构造相应的语义图、依存图等结构，然后进行实体的匹配
	 *
	 * @param
	 * @return 
	 */
	public QueryMatchedEntityWrapper(String query) {
		initWrapper(query);
	}
	
	/**
	 * 根据输入的query对这个类的变量进行一些初始化操作
	 *
	 * @param String ,query, user input query
	 * @return void 
	 */
	private  void initWrapper (String query ) {
		this.smtcGraph = new SemanticGraph (query);
		setDepGraph(this.smtcGraph.getDependencyGraph());
		setMatchEntityWrapper(this.emEngine.runEntityMatcherEngine(smtcGraph));
		setOrgMatchedQuery(this.emEngine.getMatchedQuery());
	}
	
	
	
	/**
	 * 判断给定的index前面是否还存在实体
	 * 
	 * @param int index
	 * @return boolean 
	 */
	public boolean hasPrevIndex (int index ) {
		return isWrapperIndexLegal (index -1 );
	} 
	
	/**
	 * 如果存在前一个实体的index，正常返回，否则返回 －1
	 * 
	 * @param 
	 * @return int 
	 */
	public int prevIndex (int index) {
		if (isWrapperIndexLegal (index - 1))
			return (index - 1 );
		return -1;
	}
	
	/**
	 *  判断是否存在下一个实体的id
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean hasNextIndex ( int index ) {
		return isWrapperIndexLegal ( index + 1);
	}
	
	/**
	 * 如果存在下一个实体的index，正常返回，否则返回－1；
	 *
	 * @param 
	 * @return int 
	 */
	public int nextIndex (int index ) {
		if (isWrapperIndexLegal (index + 1))
			return (index + 1);
		return -1;
	}

	/**
	 * 判断输入的index是否是合法的
	 *
	 * @param, int , index 
	 * @return boolean legal : true, illegal : false
	 */
	private boolean isWrapperIndexLegal (int index) {
		if (index < 0 || index >= this.getMatchedEntityWrapperSize()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获得指定index的实体列表
	 *
	 * @param int , index,  the index of matchEntityWrapper index
	 * @return List<MatchedEntity> 
	 */
	public List<MatchedEntity> getEntities (int index ) {
		if (isWrapperIndexLegal (index))
			return this.matchEntityWrapper.get(index);
		return null;
	}
	
	
	public int getMatchedEntityWrapperSize () {
		return this.matchEntityWrapper.size();
	}
	
	public List<List<MatchedEntity>> getMatchEntityWrapper() {
		return matchEntityWrapper;
	}

	public void setMatchEntityWrapper(List<List<MatchedEntity>> matchEntityWrapper) {
		this.matchEntityWrapper = matchEntityWrapper;
	}

	public DependencyGraph getDepGraph() {
		return depGraph;
	}

	public void setDepGraph(DependencyGraph depGraph) {
		this.depGraph = depGraph;
	}

	public SemanticGraph getSmtcGraph() {
		return smtcGraph;
	}

	public void setSmtcGraph(SemanticGraph smtcGraph) {
		this.smtcGraph = smtcGraph;
	}

	public EntityMatcherEngine getEmEngine() {
		return emEngine;
	}

	public void setEmEngine(EntityMatcherEngine emEngine) {
		this.emEngine = emEngine;
	}

	public List<List<MatchedEntity>> getOrgMatchedQuery() {
		return orgMatchedQuery;
	}

	public void setOrgMatchedQuery(List<List<MatchedEntity>> orgMatchedQuery) {
		this.orgMatchedQuery = orgMatchedQuery;
	}
	
}
