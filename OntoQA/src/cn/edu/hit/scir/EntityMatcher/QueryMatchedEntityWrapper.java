/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.scir.semanticgraph.DGEdge;
import cn.edu.hit.scir.semanticgraph.DGNode;
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
		initMatchedEntityModifiers();
	}
	
	/**
	 * 最终匹配的实体列表里面的实体的修饰词进行提取
	 *
	 * @param 
	 * @return void 
	 */
	private void initMatchedEntityModifiers () {
		List<DGNode> modifiers = this.depGraph.getModifiers();
		if (modifiers == null )
			return ;
		
		for (DGNode node : modifiers ) {
			int findIndex = -1;
			int minPathLen = 1000000;
			for (int idx = 0; idx < this.matchEntityWrapper.size(); ++idx ) {
				List<MatchedEntity> mes  = this.matchEntityWrapper.get(idx);
				MatchedEntity me = mes.get(0);
				List<Integer> meIndexes = this.emEngine.getMatchedEntityDGNodeIndexes(me);
				for (int j = meIndexes.size() - 1; j >= 0; --j ) {
					List<Integer> path = this.depGraph.searchPath(meIndexes.get(j), node.idx);
//					System.out.println ("-----------------------------");
//					for (Integer p : path ) {
//						System.out.println ("path : " + this.depGraph.getVertexNode(p));
//					}
					if (this.isDepConnnectModify(path) || this.isNegativeConnectModify(path)) {
						if (minPathLen >= path.size()) {
//							for (Integer p : path ) {
//								System.out.println ("path : " + this.depGraph.getVertexNode(p));
//							}
							minPathLen = path.size();
							findIndex = idx;
						}
					}
				}
 			}
			if (findIndex != -1) {
				for (MatchedEntity me : this.matchEntityWrapper.get(findIndex)) {
					if (me.getModifizers() == null && !(me.getQuery().indexOf(node.word) != -1 || me.getQuery().indexOf(node.stem) != -1)) {
						List<DGNode> mdf = new ArrayList<DGNode>();
						mdf.add(node);
						me.setModifiers (mdf);
					}
					else if ( !(me.getQuery().indexOf(node.word) != -1 || me.getQuery().indexOf(node.stem) != -1)){
						if (!me.getModifizers().contains(node) ) {
							List<DGNode> mdf = me.getModifizers();
							mdf.add(node);
							me.setModifiers(mdf);
						}
					}
				}
			}
		}
		
		/*for (int idx = this.matchEntityWrapper.size() - 1; idx >= 0; --idx) {
			List<MatchedEntity> mes  = this.matchEntityWrapper.get(idx);
			for (MatchedEntity me : mes ) {
				List<Integer> meIndexes = this.emEngine.getMatchedEntityDGNodeIndexes(me);
				Set<DGNode> meModifiers = new HashSet<DGNode>();
				for (DGNode node : modifiers ) {
					System.out.println ( "modifier : " + node.toString());
					for (Integer meIndex : meIndexes) {
						List<Integer> path = this.depGraph.searchPath(meIndex, node.idx);
						
						for (Integer p : path ) {
							System.out.println ("path : " + this.depGraph.getVertexNode(p));
						}
						// 对path的内容进行限定
						// dep -> advmod : handle : the most how many state
						// dep 
						// advmod -> amod
						if ( this.isDepConnnectModify(path)  ) {
							meModifiers.add(node);
						}
						if ( this.isNegativeConnectModify(path))
							meModifiers.add(node);
					}
				}
				me.setModifiers(new ArrayList<DGNode> (meModifiers));
			}
		}*/
	} 
	
	
	/**
	 * 判断path链接的最高级形容词和名词实体
	 * 
	 * JSS_modifier --> advmod -> amod 	--> core_word
	 * RBS_modifier --> advmod -> amod 	--> core_word
	 * JJS_modifier --> amod 			--> core_word
	 * RBS_modifier --> amod 			--> core_word
	 * JJS_modifier --> advmod -> dep	--> core_word
	 * RBS_modifier --> advmod -> dep	--> core_word
	 * JJS_modifier --> dep				--> core_word
	 * 
	 * @param 
	 * @return boolean 
	 */
	private boolean isDepConnnectModify(List<Integer> path ) {
		if (path == null || path.size () < 2 )
			return false;
		
		// 只判断从core_word出发的两条边或者是一条
		int next = 1;
		int prev = 0;
		int cnt = 0;
		while (next < path.size() ) {
			DGEdge edge = this.depGraph.getEdge(path.get(prev), path.get(next));
			if (edge != null && ( (edge.reln.toLowerCase().equals ("amod") && edge.isDirected) || edge.reln.toLowerCase().equals ("advmod")  || edge.reln.toLowerCase().equals("dep")) ) {
				++cnt;
			}
			prev = next;
			next = next + 1;
			if (cnt >= 1)
				return true;
		}
		return false;
	}
	
	/**
	 * 判断path链接的是不是否定词和实体或是属性
	 * 
	 * no -> det -> core_words
	 * not -> neg -> core_words
	 * @param 
	 * @return boolean 
	 */
	private boolean isNegativeConnectModify (List<Integer> path ) {
		if (path == null || path.size() < 2)
			return false;
		if (   this.depGraph.getEdge(path.get(path.size() - 2), path.get(path.size()-1)).reln.toLowerCase().equals ("det") 
			|| this.depGraph.getEdge(path.get(path.size() - 2), path.get(path.size()-1)).reln.toLowerCase().equals("neg") 
			|| (path.size () < 4 &&  path.get(0) > path.get(path.size() - 1))) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 判断这个句子是不是应该进行count操作
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean isCount () {
		if (this.matchEntityWrapper == null || this.matchEntityWrapper.isEmpty())
			return false;
		int index = this.depGraph.getProcessedQuestion().toLowerCase().indexOf("how many");
		if (index != - 1 ) {
			for (int i = 0; i < this.depGraph.getDgraphSize(); ++i ) {
				if (this.depGraph.getVertexNode(i).word.toLowerCase().equals("how")) 
				{
					if (i + 1 < this.getEntities(0).get(0).getBegin()) {
						return true;
					}
				}
			}
		}
		return false;
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
		return isWrapperIndexLegal ( index );
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

	@Override
	public String toString() {
		return "QueryMatchedEntityWrapper [matchEntityWrapper="
				+ matchEntityWrapper + "]";
	}
	
}
