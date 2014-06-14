/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ProbabilityGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.graph.LoopMultiGraph;
import cn.edu.hit.ir.graph.PropertyNode;
import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.graph.QueryNode;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.SchemaGraph;
import cn.edu.hit.ir.ontology.ScoredResource;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.scir.EntityMatcher.QueryMatchedEntityWrapper;

import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.nlp.util.StringUtils;

/**
 * 存储实体路径的图
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月24日 
 */
public class ProbabilityGraph {
	private static Logger logger = Logger.getLogger(ProbabilityGraph.class);
	
	private QueryMatchedEntityWrapper entityWrapper = null;
	
	private List<List<MatchedEntity>> queryMatchedEntities = null;
	
	// 进行补全
	private List<List<Object>> completeMatchedObjects = null;
	
	// 开始节点列表
	private List<ProbabilityNode> beginNodes = null;
	
	// 结束节点列表
	private List<ProbabilityNode> endNodes = null;
	
	// score configuration
	private Configuration config = null;
	
	//score
	// 为了使路径完整，添加的实体，该分值为添加实体的匹配分值
	private final String ADD_ENTITY_SCORE = "add.entity.score";
	// 为使路径完整，添加属性，该分值为添加属性的匹配分值
	private final String ADD_PROPERTY_SCORE = "add.property.score";
	
	// 从query中匹配而来的实体的概率
	private final String MATCH_PROBABILITY_SCORE = "match.probability.score";
	
	// 如果一个三元组是反的，那么对匹配的分值进行罚分
	private final String REVERSE_TRIPLE_PUBLISH_SCORE = "reverse.triple.publish.score";
	
	private QueryGraph queryGraph = null;
	
	private double addEntityScore = 0.6;
	private double addPropertyScore = 0.4;
	private double matchProbabilityScore = 1.0;
	private double reverseTriplePublishScore = 0.2;
	// 这个是存储所有路径的概率和匹配图
	private LoopMultiGraph<ProbabilityNode, ProbabilityEdge> graph;
	
	private SchemaGraph schemaGraph;
	
	private Ontology ontology = Ontology.getInstance();
	
	
	/**
	 * PathGraph 构造函数
	 *
	 * @param
	 * @return 
	 */
	public ProbabilityGraph (String query) {
		initResource(query);
		logger.info("initResouce");
		initConfig();
		logger.info("initConfig");
		initScore();
		logger.info("initScore");
		completeMatchedEntities();
		logger.info("complete matched entities");
		removeIllegalMatchedObjects();
		logger.info("complete removeIllegalMathcedObject");
		completeMatchedObjectScore();
		logger.info("complete matched object score");
		buildGraph();
		logger.info("complete buildGraph");
		setQueryGraph (this.generateQueryGraph());
		logger.info("complete generate query graph");
	}
	
	/**
	 * 生成查询图
	 *
	 * @param 
	 * @return QueryGraph 
	 */
	public QueryGraph generateQueryGraph () {
		if (this.completeMatchedObjects == null || this.completeMatchedObjects.isEmpty())
			return null;
		QueryGraph qGraph = new QueryGraph(null);
		
		if (this.completeMatchedObjects.size() > 2) { 
			GraphPathSelector pathSel = new GraphPathSelector (this.graph, this.beginNodes, this.endNodes);
			
			GraphPath<ProbabilityNode, ProbabilityEdge> bestPath = pathSel.getHighestScoredPath();
			if (bestPath == null )
				return null;
			List<ProbabilityEdge> edges = bestPath.getEdgeList();
			ProbabilityNode source = (ProbabilityNode)bestPath.getStartVertex();
			ProbabilityNode target = null;
			for (int i = 0; i < edges.size(); ++i) {
				ProbabilityEdge edge = (ProbabilityEdge)edges.get(i);
				target = pathSel.otherVertex(edge, source);
				pushEdge (qGraph, source, edge, target);
				source = target;
			}
		}
		else if (this.completeMatchedObjects.size() == 1){
			if(this.completeMatchedObjects.get(0).isEmpty())
				return null;
			ProbabilityNode tmpNode = (ProbabilityNode)this.completeMatchedObjects.get(0).get(0);
			if (tmpNode.isAdded()) {
				qGraph.setSource(this.generateAddedQueryNode(tmpNode));
			}
			else {
				qGraph.setSource(new QueryNode (tmpNode.getMatchedEntity(),this.schemaGraph.getSchemaResource(tmpNode.getResource()), tmpNode.getId(), tmpNode.getMatchScore()));
			}
		}
		if (this.entityWrapper.isCount()) {
			qGraph.setCount(true);
		}
		return qGraph;
	}
	
	public QueryGraph getQueryGraph() {
		return queryGraph;
	}

	public void setQueryGraph(QueryGraph queryGraph) {
		this.queryGraph = queryGraph;
	}

	private void pushEdge (QueryGraph queryGraph, ProbabilityNode source, ProbabilityEdge property, ProbabilityNode target) {
		if (queryGraph == null || source == null || property == null || target == null)
			return ;
		
//		QueryNode qSource = generateQueryNode (source, property, target);
//		QueryNode qTarget = generateQueryNode (source, property, target);
		QueryNode qSource = null;
		QueryNode qTarget = null;
		if (source.isAdded()) {
			qSource = generateAddedQueryNode(source);
		}
		if (target.isAdded())
			qTarget = generateAddedQueryNode(target);
		
		if (qSource == null && qTarget == null ) {
			qSource = this.generateQueryNode(target, property, source);
			qTarget = this.generateQueryNode(source, property, target);
		}
		else if (qSource !=  null && qTarget == null ){
			qTarget = this.generateQueryNode(source, property, target);
		}else if (qSource == null && qTarget != null ) {
			qSource = this.generateQueryNode(target, property, source);
		}
		
		if (qSource !=null && qTarget != null ) {
			//设置查询点
			if (queryGraph.vertexSet().isEmpty()) {
				queryGraph.setSource(qSource);
			}
			queryGraph.pushEdge(qSource, property.getPropertyNode(), qTarget, property.isReverse());
		}
	}
	
	
	private QueryNode generateAddedQueryNode (ProbabilityNode node ) {
		if (node  == null )
			return null;
		if (node.isAdded()) {
			QueryNode queryNode = new QueryNode (node.getResource(), this.schemaGraph.getSchemaResource(node.getResource()), node.getId(), node.getMatchScore());
			return queryNode;
		}
		return null;
	}
	
	private QueryNode generateQueryNode (ProbabilityNode s, ProbabilityEdge p, ProbabilityNode o) {
		if (s == null || p == null || o == null  ) return null;
		
		if (schemaGraph.isComparableProperty(this.schemaGraph.getSchemaResource(s.getResource()), p.getProperty())) {
			QueryNode target = new QueryNode(ontology.literalClass, o.getMatchScore());
			return target;
		}
//		logger.debug("s : " + s + ", p : " + p + ", o : " + o);
//		logger.debug("test : " + this.schemaGraph.getSchemaResource(o.getResource()) + " --------- " + o.getResource() + ", is instance " + ontology.isInstanceOf(o.getResource(), this.schemaGraph.getSchemaResource(o.getResource())));
//		logger.debug("is literal prop : " + schemaGraph.isLiteralProperty(s.getResource(), p.getProperty()));
		if (schemaGraph.isLiteralProperty(s.getResource(), p.getProperty())
				&& ontology.isInstanceOf(o.getResource(), this.schemaGraph.getSchemaResource(o.getResource()))) {	// handles question like "a city named austin"
//			logger.debug("in test : " + this.schemaGraph.getSchemaResource(o.getResource()) + " --------- " + o.getResource() + ", is instance " + ontology.isInstanceOf(o.getResource(), this.schemaGraph.getSchemaResource(o.getResource())));
			QueryNode literalValue = new QueryNode(ontology.literalClass, o.getMatchScore());
			literalValue.setValue(o.getMatchedEntity().getLabel());
			return literalValue;
		}
		QueryNode queryNode = new QueryNode (o.getMatchedEntity(), this.schemaGraph.getSchemaResource(o.getResource()), o.getId(), o.getMatchScore());
		return queryNode;
	} 
	
	/**
	 * 根据query初始化匹配的实体
	 *
	 * @param query
	 * @return void 
	 */
	private void initResource (String query) {
		entityWrapper = new QueryMatchedEntityWrapper (query);
		queryMatchedEntities = this.entityWrapper.getMatchEntityWrapper();
		logger.info("matched Entities : ");
		for (List<MatchedEntity> mes : queryMatchedEntities) {
			logger.info("me : " + StringUtils.join(mes, ", "));
		}
		graph = new LoopMultiGraph<ProbabilityNode, ProbabilityEdge>(ProbabilityEdge.class);
		beginNodes = new ArrayList<ProbabilityNode>();
		endNodes   = new ArrayList<ProbabilityNode>();
		
		this.schemaGraph = ontology.getSchemaGraph();
		this.completeMatchedObjects = new ArrayList<List<Object>> ();
		
	}
	
	
	private void initConfig() {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	} 
	
	
	
	
	/**
	 * 初始化分值
	 *
	 * @param 
	 * @return void 
	 */
	private void initScore () {
		this.addEntityScore   = config.getDouble(this.ADD_ENTITY_SCORE);
		this.addPropertyScore = config.getDouble(this.ADD_PROPERTY_SCORE);
		this.matchProbabilityScore = config.getDouble(this.MATCH_PROBABILITY_SCORE);
		this.reverseTriplePublishScore = config.getDouble(this.REVERSE_TRIPLE_PUBLISH_SCORE);
	}
	
	
	/**
	 * 判断匹配的实体是否是缺少实体的
	 *  like : 
	 *  匹配的实体            state border state border state
	 *                        |      |     |      |     | 
	 *  isMiss(false)	      0  --> 1     0      1     0      (isMiss --> 1)
 	 * @param 
	 * @return boolean 
	 */
	public boolean isMissResource () {	
		boolean isMiss = false;
		for (List<MatchedEntity> mes : this.queryMatchedEntities) {
			if (mes == null || mes.isEmpty())
				continue;
			if (mes.get(0).isProperty() && isMiss) {
				isMiss = !isMiss;
			}
			else if (!mes.get(0).isProperty() && !isMiss ){
				isMiss = ! isMiss;
			}
			else {
				return true;
			}
		}
		return !isMiss;
	}
	
	/**
	 * 实体列表转化成object
	 *
	 * @param 
	 * @return List<Object> 
	 */
	private List<Object> me2ProbNodeObject(List<MatchedEntity> mes, int id) {
		if (mes == null || mes.isEmpty() ) return null;
		List<Object> objs = new ArrayList<Object>();
		for (MatchedEntity me : mes ) {
			ProbabilityNode pNode = new ProbabilityNode (me, id);
			Object obj  = (Object) pNode;
			objs.add(obj);
		}
		return objs;
	}
	
	
	/**
	 * 将属性转化为propertyNode object
	 *
	 * @param 
	 * @return List<Object> 
	 */
	private List<Object> me2PropertyNodeObject (List<MatchedEntity> mes ) {
		if (mes == null || mes.isEmpty()) return null;
		
		List<Object> objs = new ArrayList<Object>();
		for (MatchedEntity me : mes ) {
			PropertyNode pNode = new PropertyNode(me, me.getScore());
			Object obj = (Object)pNode;
			objs.add(obj);
		}
		return objs;
	}
	
	/**
	 * 利用属性和实体进行位置填补
	 * 
	 *
	 * @param 
	 * @return void 
	 */
	public void completeMatchedEntities () {
		if ( isMissResource() ) {
			Set<Resource> nodes = this.schemaGraph.getResourceSet();
			List<ProbabilityNode> probNodes = new ArrayList<ProbabilityNode> (); 
			for (Resource r : nodes) {
				ProbabilityNode tpNode = new ProbabilityNode (r, this.addEntityScore);
				probNodes.add(tpNode);
			}
			
			Set<Resource> propertySet = this.schemaGraph.getPropertySet();
			List<PropertyNode> propertyNodes = new ArrayList<PropertyNode>();
			for (Resource r : propertySet ) {
				PropertyNode node = new PropertyNode (r, this.addPropertyScore);
				propertyNodes.add(node);
			}
			
			boolean isProperty = false;
			int idx = 0;
			while (idx < this.queryMatchedEntities.size()) {
				List<MatchedEntity> mes = this.queryMatchedEntities.get(idx);
				if (mes == null || mes.isEmpty()) {
					++idx;
					continue;
				}
				if (!mes.get(0).isProperty() && !isProperty ) {
					isProperty = !isProperty;
					this.completeMatchedObjects.add(this.me2ProbNodeObject(mes, idx));
					++idx;
				}
				else if (mes.get(0).isProperty() && isProperty ) {
					isProperty = !isProperty;
					this.completeMatchedObjects.add(this.me2PropertyNodeObject(mes));
					++idx;
				}
				else {
					if (isProperty) { // 属性
						this.completeMatchedObjects.add(new ArrayList<Object>(propertyNodes));
					}
					else { // 实体
						this.completeMatchedObjects.add(new ArrayList<Object>(probNodes));
					}
					isProperty = !isProperty;
				}
				// 添加最后的实体，如果queryMatchedEntities最后一个属性
				if (idx == this.queryMatchedEntities.size() && !isProperty) {
					this.completeMatchedObjects.add(new ArrayList<Object>(probNodes));
				}
			}
		} 
		else {
			for (List<MatchedEntity> mes : this.queryMatchedEntities) {
				if (mes == null || mes.isEmpty())
					continue;
				List<Object> objs = null;
				if (! mes.get(0).isProperty()) {
					objs = me2ProbNodeObject(mes, completeMatchedObjects.size());
				}
				else { // 属性
					objs = me2PropertyNodeObject(mes);
				}
				this.completeMatchedObjects.add(objs);
			}
		}
	}
	
	/** 
	 * 根据bits中的真值生成对应的二进制的十进制值， 
	 * like : false, true, false ---> 0, 1, 0 --> 2
	 *
	 * @param bits的布尔形式
	 * @return int 最后的十进制值
	 */
	private int choice (boolean [] bits ) {
		if (bits == null )return 0;
		int nb = 0;
		for (int i = 0; i < bits.length ; ++i ) {
			if (bits[i]) {
				nb = (nb | ( 1 << (bits.length - i - 1)));
			}
		}
		return nb;
	}
	
	
	/**
	 *property list, entity list,  property list 三个列表进行标注是否合法的内容
	 *
	 * @param prevObjs, lhsProperty
	 * @param currObjs, mid entity
	 * @param nextObjs, rhsProperty
	 * @return void 
	 */
	private boolean removeIllegalPropEntityProp (List<Object> prevObjs, List<Object> currObjs, List<Object> nextObjs) {
		if (prevObjs == null || prevObjs.isEmpty() || currObjs == null || currObjs.isEmpty() || nextObjs == null || nextObjs.isEmpty() )
			return false;
		
		boolean isExistsLegal = false;
		for (Object cur : currObjs) {
			if (cur instanceof ProbabilityNode )
				((ProbabilityNode)cur).setLegal(false);
		}
		for (Object prev : prevObjs ) {
			for (Object next : nextObjs ) {
				if (prev instanceof PropertyNode && next instanceof PropertyNode) {
					Set<Resource> legalEntities = schemaGraph.getPropPropSet(((PropertyNode)prev).getProperty(), ((PropertyNode)next).getProperty());
					for (Object cur : currObjs) {
						if (legalEntities != null && cur instanceof ProbabilityNode && (legalEntities.contains(((ProbabilityNode)cur).getResource()))) {
							((ProbabilityNode)cur).setLegal(true);
							//logger.debug("(" + prev + ", " + cur + ", " + next + ")" + "true");
							isExistsLegal = true;
						}
					}
				}
			}
		}
		if (isExistsLegal) {
			removeIllegalObject (currObjs);
		}
		return isExistsLegal;
	} 
	
	
	public Resource instance2Class (Resource instance) {
		if (instance == null ) return null;
		return this.schemaGraph.getSchemaResource(instance);
	}
	
	/**
	 * entity list, property list, entity list 三个列表进行标注是否合法的内容
	 *
	 * @param 
	 * @return boolean, 是否存在合法的三元组, true，存在， false不存在 
	 */
	private boolean removeIllegalEntityPropEntity (List<Object> prevObjs, List<Object> currObjs, List<Object> nextObjs) {
		if (prevObjs == null || prevObjs.isEmpty() || currObjs == null || currObjs.isEmpty() || nextObjs == null || nextObjs.isEmpty() )
			return false;
		boolean isExistsLegal = false;
		for (Object prev : prevObjs) {
			for (Object cur : currObjs) {
				for (Object next : nextObjs) {
//					logger.debug("prev : " + prev);
//					logger.debug("cure : " + cur);
//					logger.debug("next : " + next);
//					logger.debug("next is literal : " +((ProbabilityNode)next).getResource().isLiteral());
					if (this.schemaGraph.isLegalTriple(((ProbabilityNode)prev).getResource(), ((PropertyNode)cur).getProperty(), ((ProbabilityNode)next).getResource())) {
						((ProbabilityNode)prev).setLegal(true);
						((PropertyNode)cur).setLegal(true);
						((ProbabilityNode)next).setLegal(true);
						isExistsLegal = true;
					}
					else {
						;
					}
				}
			}
		}
		
		// 不存在合法  state border river, 属性border是错误的
		if (isExistsLegal) {
			// 删除不合法的元素
			removeIllegalObject (prevObjs);
			removeIllegalObject (currObjs);
			removeIllegalObject (nextObjs);
		}else {
			if (prevObjs.size() == 1 && nextObjs.size() == 1 && currObjs.size() == 1) {
				Object prev = prevObjs.get(0);
				Object next = nextObjs.get(0);
				Set<Resource> propSet = schemaGraph.getPropertySet(instance2Class(((ProbabilityNode)prev).getResource()), instance2Class(((ProbabilityNode)next).getResource()));
				if (propSet == null ) {
					propSet = schemaGraph.getPropertySet(instance2Class(((ProbabilityNode)next).getResource()), instance2Class(((ProbabilityNode)prev).getResource()));
				}
				if (propSet != null && !propSet.contains(((PropertyNode)currObjs.get(0)).getProperty())) {
					currObjs.clear();
					for (Resource pnode : propSet) {
						PropertyNode tn = new PropertyNode(pnode, this.addPropertyScore);
						tn.setLegal(true);
						currObjs.add(tn);
					}
					((ProbabilityNode)prev).setLegal(true);
					((ProbabilityNode)next).setLegal(true);
					isExistsLegal = true;
				}
				
			}
		}
		return isExistsLegal;
	}
	
	/**
	 * 删除不合法的objects
	 *
	 * @param 
	 * @return  
	 */
	private void removeIllegalObject (List<Object> objs ) {
		if (objs == null || objs.isEmpty())
			return ;
		if (objs.get(0) instanceof PropertyNode) {
			for (int i = 0; i < objs.size(); ++i ) {
				Object obj = objs.get(i);
				if (!((PropertyNode)obj).isLegal()) {
					objs.remove(i);
					--i;
				}
			}
		}
		else if (objs.get(0) instanceof ProbabilityNode) {
			for (int i = 0; i < objs.size(); ++i ) {
				Object obj = objs.get(i);
				if (!((ProbabilityNode)obj).isLegal()) {
					objs.remove(i);
					--i;
				}
			}
		}
	}
	
	/**
	 * 对completeMatchedObjects 中添加的实体进行消歧，去除不合法的object
	 *
	 * @param 
	 * @return void 
	 */
	public void removeIllegalMatchedObjects () {
//		logger.debug("completeMatchedObjects : ");
//		for (List<Object> os : this.completeMatchedObjects) {
//			logger.debug("obj : " + StringUtils.join(os, ", "));
//		}
		
		for (int i = 1; i < this.completeMatchedObjects.size() - 1; ++i ) {
			List<Object> prevObjs = this.completeMatchedObjects.get(i-1);
			List<Object> currObjs = this.completeMatchedObjects.get(i);
			List<Object> nextObjs = this.completeMatchedObjects.get(i + 1);
			// Entity
			if (prevObjs.get(0) instanceof ProbabilityNode) {
				boolean [] bits = {((ProbabilityNode)prevObjs.get(0)).isAdded(),
								  ((PropertyNode)currObjs.get(0)).isAdded(),
								   (((ProbabilityNode)nextObjs.get(0)).isAdded())};
				int choice = choice (bits);
//				logger.debug("bits : " + bits[0] + ", " + bits[1] + ", " + bits[2]);
//				logger.debug("choice : " + choice);
				switch (choice) {
				case 0 : // 0, 0, 0 没有是填充的, 合法检查
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					break;
				case 1 : // 0, 0, 1, 最后一个是添加的
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					
					// s, p --> o 
					
					break;
				case 2 : // 0, 1, 0, 中间那个是添加的
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					
					// s, o --> p
					
					break;
				case 3 : // 0, 1, 1, 不存在， 两个添加的内容不可能
					logger.debug("不可能");
					break;
				case 4 : // 1, 0, 0, 第一个是添加的内容
					if (!this.removeIllegalEntityPropEntity(nextObjs, currObjs, prevObjs)) {
						logger.debug("需要处理");
					}
					
					// o p --> s
					
					break;
				case 5 : // 1, 0, 1, 第一个和第三个是添加的内容
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					
					
					break;
				case 6 : // 1, 1, 0 不存在，两个添加的内容不可能连在一起
					logger.debug("不可能");
					break;
				case 7 :// 1, 1, 1, 都是填充的，这种情况不可能存在
					logger.debug("不可能");
					break;
				default : 
					logger.debug("不可能");
					break;
				}
			}
			else { // property
				boolean [] bits = {((PropertyNode)prevObjs.get(0)).isAdded(),
						  ((ProbabilityNode)currObjs.get(0)).isAdded(),
						   (((PropertyNode)nextObjs.get(0)).isAdded())};
				int choice = choice (bits);
				switch (choice) {
				case 0 : // 0, 0, 0 没有是填充的, 合法检查
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					break;
				case 1 : // 0, 0, 1, 最后一个是添加的
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					break;
				case 2 : // 0, 1, 0, 中间那个是添加的
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					break;
				case 3 : // 0, 1, 1, 不存在， 两个添加的内容不可能
					break;
				case 4 : // 1, 0, 0, 第一个是添加的内容
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					break;
				case 5 : // 1, 0, 1, 第一个和第三个是添加的内容
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.debug("需要处理");
					}
					break;
				case 6 : // 1, 1, 0 不存在，两个添加的内容不可能连在一起
					break;
				case 7 :// 1, 1, 1, 都是填充的，这种情况不可能存在
					break;
				default : 
					break;
				}
			}
		}
	}
	
	
	
	
	/**
	 * 判断一个三元组是不是正向的
	 *
	 * @param 
	 * @return boolean 
	 */
	private boolean isSubjectObject (ProbabilityNode lhs, ProbabilityNode rhs, PropertyNode property) {
		if (lhs == null || rhs == null || property == null) return false;
//		logger.debug("is reverse : " + this.schemaGraph.getSchemaResource(lhs.getResource()) + ", " + property + ", " + this.schemaGraph.getSchemaResource(rhs.getResource()));
		Set<Resource> propSet = this.schemaGraph.getPropertySet(this.schemaGraph.getSchemaResource(lhs.getResource()), this.schemaGraph.getSchemaResource(rhs.getResource()));
		if (this.schemaGraph.isLiteralResource(rhs.getResource()))
			return true;
		if (schemaGraph.isLiteralProperty(this.schemaGraph.getSchemaResource(lhs.getResource()), property.getProperty())
				&& ontology.isInstanceOf(rhs.getResource(), this.schemaGraph.getSchemaResource(rhs.getResource()))) {	// handles question like "a city named austin"
			return true;
		}
		if (propSet != null && propSet.contains(property.getProperty())) {
			return true;
		}
		return false;
	}
	

	
	/**
	 * 对经过修正的实体的进行打分
	 *
	 * @param 
	 * @return void 
	 */
	public void completeMatchedObjectScore () {
		for (int i = 1; i < this.completeMatchedObjects.size() - 1; ++i ) {
			List<Object> prevObjs = this.completeMatchedObjects.get(i-1);
			List<Object> currObjs = this.completeMatchedObjects.get(i);
			List<Object> nextObjs = this.completeMatchedObjects.get(i + 1);
			
			// entity
			if (prevObjs.get(0) instanceof ProbabilityNode) {
				for (Object prev : prevObjs) {
					ProbabilityNode prevNode  =((ProbabilityNode)prev);
					for (Object cur : currObjs ) {
						PropertyNode curNode  =((PropertyNode)cur);
						for (Object next : nextObjs ) {
							ProbabilityNode nextNode  =((ProbabilityNode)next);
							
							if (!this.schemaGraph.isLegalTriple(prevNode.getResource(), curNode.getProperty(), nextNode.getResource()))
								continue;
//							logger.debug("s : " + prevNode.getResource()  + "\tp : " + curNode.getProperty() + "\to : " + nextNode.getResource());
							// 0, 0 , 0
							if (!prevNode.isAdded() && !curNode.isAdded() && !nextNode.isAdded()) {
								prevNode.setProbabilityScore(this.matchProbabilityScore);
								curNode.setProbabilityScore(this.matchProbabilityScore);
								nextNode.setProbabilityScore(this.matchProbabilityScore);
							}
							// 0, 0, 1
							else if (!prevNode.isAdded() && !curNode.isAdded() && nextNode.isAdded()) {
								prevNode.setProbabilityScore(this.matchProbabilityScore);
								curNode.setProbabilityScore(this.matchProbabilityScore);
								
								// prevNode is subject ,
								if (this.isSubjectObject(prevNode, nextNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getSubjProp2ObjSet(prevNode.getResource(), curNode.getProperty());
									boolean exists = false;
									if (scRes != null ) {
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(nextNode.getResource())) {
												nextNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										nextNode.setProbabilityScore(1.0 / nextObjs.size());
									}
								}
								// nextNode is subject 
								else if (this.isSubjectObject(nextNode, prevNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getObjProp2SubjSet(prevNode.getResource(), curNode.getProperty());
									boolean exists = false;
									if (scRes != null ) {
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(nextNode.getResource())) {
												nextNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										nextNode.setProbabilityScore(1.0 / nextObjs.size());
									}
								}else {
									logger.error("出现错误！！！！");
									nextNode.setProbabilityScore(1.0 / nextObjs.size());
								}
							}
							// 0, 1, 0
							else if (!prevNode.isAdded() && curNode.isAdded() && !nextNode.isAdded()) {
								prevNode.setProbabilityScore(this.matchProbabilityScore);
								nextNode.setProbabilityScore(this.matchProbabilityScore);
								
								// prevNode is subject ,
								if (this.isSubjectObject(prevNode, nextNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getSubjObj2PropSet(prevNode.getResource(), nextNode.getResource());
									boolean exists = false;
									if (scRes != null ) {
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(curNode.getProperty())) {
												curNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										curNode.setProbabilityScore(1.0 / currObjs.size());
									}
								}
								// nextNode is subject 
								else if (this.isSubjectObject(nextNode, prevNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getSubjObj2PropSet(nextNode.getResource(), prevNode.getResource());
									boolean exists = false;
									if (scRes != null ) {
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(curNode.getProperty())) {
												curNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										curNode.setProbabilityScore(1.0 / currObjs.size());
									}
								}else {
									logger.error("出现错误！！！！");
									curNode.setProbabilityScore(1.0 / currObjs.size());
								}
							}
							// 1, 0, 0
							else if (prevNode.isAdded() && !curNode.isAdded() && !nextNode.isAdded()) {
								curNode.setProbabilityScore(this.matchProbabilityScore);
								nextNode.setProbabilityScore(this.matchProbabilityScore);
								
								// prevNode is subject ,
								if (this.isSubjectObject(prevNode, nextNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getObjProp2SubjSet(nextNode.getResource(), curNode.getProperty());
//									logger.debug("1scRes : " + scRes);
									boolean exists = false;
									if (scRes != null ) {
										
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(prevNode.getResource())) {
												prevNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										prevNode.setProbabilityScore(1.0 / prevObjs.size());
									}
								}
								// nextNode is subject 
								else if (this.isSubjectObject(nextNode, prevNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getSubjProp2ObjSet(nextNode.getResource(), curNode.getProperty());
									logger.debug("2scRes : " + scRes);
									boolean exists = false;
									if (scRes != null ) {
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(prevNode.getResource())) {
												prevNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										prevNode.setProbabilityScore(1.0 / prevObjs.size());
									}
								}else {
									logger.error("出现错误！！！！");
									prevNode.setProbabilityScore(1.0 / prevObjs.size());
								}
								
								
							}
							// 1, 0, 1
							else if (prevNode.isAdded() && !curNode.isAdded() && nextNode.isAdded()) {
								curNode.setProbabilityScore(this.matchProbabilityScore);
								// prevNode is subject ,
								if (this.isSubjectObject(prevNode, nextNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getObjProp2SubjSet(nextNode.getResource(), curNode.getProperty());
									boolean exists = false;
									if (scRes != null ) {
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(prevNode.getResource())) {
												prevNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										prevNode.setProbabilityScore(1.0 / prevObjs.size());
									}
									
									Set<ScoredResource> scRes2 = this.schemaGraph.getSubjProp2ObjSet(prevNode.getResource(), curNode.getProperty());
									boolean exists2 = false;
									if (scRes2 != null ) {
										for (ScoredResource sr : scRes2 ) {
											if (sr.resource.equals(nextNode.getResource())) {
												nextNode.setProbabilityScore(sr.score);
												exists2 = true;
												break;
											}
										}
									}
									if (!exists2 ) {
										nextNode.setProbabilityScore(1.0 / nextObjs.size());
									}
								}
								// nextNode is subject 
								else if (this.isSubjectObject(nextNode, prevNode, curNode)) {
									Set<ScoredResource> scRes = this.schemaGraph.getSubjProp2ObjSet(nextNode.getResource(), curNode.getProperty());
									boolean exists = false;
									if (scRes != null ) {
										for (ScoredResource sr : scRes ) {
											if (sr.resource.equals(prevNode.getResource())) {
												prevNode.setProbabilityScore(sr.score);
												exists = true;
												break;
											}
										}
									}
									if (!exists ) {
										prevNode.setProbabilityScore(1.0 / prevObjs.size());
									}
									
									Set<ScoredResource> scRes2 = this.schemaGraph.getObjProp2SubjSet(prevNode.getResource(), curNode.getProperty());
									boolean exists2 = false;
									if (scRes2 != null ) {
										for (ScoredResource sr : scRes2 ) {
											if (sr.resource.equals(nextNode.getResource())) {
												nextNode.setProbabilityScore(sr.score);
												exists2 = true;
												break;
											}
										}
									}
									if (!exists2 ) {
										nextNode.setProbabilityScore(1.0 / nextObjs.size());
									}
								}else {
									logger.error("出现错误！！！！");
									prevNode.setProbabilityScore(1.0 / prevObjs.size());
									nextNode.setProbabilityScore(1.0 / nextObjs.size());
								}
							}
							else {
								logger.debug ("不做处理～～～");
							}
						}
					}
				}
			}
			// property
			else {
				;
			}
			
		}
	}
	
	
	
	public void buildGraph () {
		if (this.completeMatchedObjects != null && this.completeMatchedObjects.size() > 2){
			Set<ProbabilityNode> beginNodeSet = new HashSet<ProbabilityNode>();
			Set<ProbabilityNode> endNodeSet = new HashSet<ProbabilityNode>();
			for (int i = 1; i < this.completeMatchedObjects.size() - 1; i += 2 ) {
				List<Object> prevObjs = this.completeMatchedObjects.get(i-1);
				List<Object> currObjs = this.completeMatchedObjects.get(i);
				List<Object> nextObjs = this.completeMatchedObjects.get(i + 1);
				
				for (Object prev : prevObjs) {
					ProbabilityNode prevNode  =((ProbabilityNode)prev);
					for (Object cur : currObjs ) {
						PropertyNode curNode  =((PropertyNode)cur);
						for (Object next : nextObjs ) {
							ProbabilityNode nextNode  = ((ProbabilityNode)next);
							
							if (!this.schemaGraph.isLegalTriple(prevNode.getResource(), curNode.getProperty(), nextNode.getResource()))
								continue;
							// 添加头节点
							if (i == 1) {
								beginNodeSet.add(prevNode);
							}
							if (i == this.completeMatchedObjects.size() -2) {
								endNodeSet.add(nextNode);
							}
//							logger.debug("triple : " + prevNode + ", " + curNode + ", " + nextNode);
							// 正向
							if (this.isSubjectObject(prevNode, nextNode, curNode)) {
								pushEdge(prevNode, curNode, nextNode, false);
							}
							// 反向
							else {
								pushEdge(prevNode, curNode, nextNode, true);
							}
						} // for o
					} // for p 
				}// for s
			} // for		
			this.beginNodes.clear();
			this.beginNodes.addAll(beginNodeSet);
			this.endNodes.clear();
			this.endNodes.addAll(endNodeSet);
		}// if
		// 只匹配到一列实体的情况
		else {
			; // generateQueryGraph 中做处理了
		}
	}
	
	
	
	public List<ProbabilityNode> getBeginNodes() {
		return beginNodes;
	}

	public void setBeginNodes(List<ProbabilityNode> beginNodes) {
		this.beginNodes = beginNodes;
	}

	public List<ProbabilityNode> getEndNodes() {
		return endNodes;
	}

	public void setEndNodes(List<ProbabilityNode> endNodes) {
		this.endNodes = endNodes;
	}

	public LoopMultiGraph<ProbabilityNode, ProbabilityEdge> getGraph() {
		return graph;
	}

	public void setGraph(LoopMultiGraph<ProbabilityNode, ProbabilityEdge> graph) {
		this.graph = graph;
	}

	/**
	 * 向graph中添加边
	 *
	 * @param 
	 * @return ProbabilityEdge 
	 */
	public ProbabilityEdge pushEdge (ProbabilityNode source, PropertyNode property, ProbabilityNode target, boolean isReverse) {
		logger.debug("@pushEdge " + source + ", " + property + ", " + target + ", " + isReverse);
		PropertyNode newProp = new PropertyNode(property);
		if (isReverse) {
			newProp.setWeight(property.getWeight() - this.reverseTriplePublishScore);
			logger.debug("@pushEdge reverse : " + source + ", " + newProp + ", " + target + ", " + isReverse);
		}
		if (!graph.containsVertex(source))
			graph.addVertex(source);
		if (!graph.containsVertex(target))
			graph.addVertex(target);
		ProbabilityEdge edge = graph.addEdge(source, target);
		if (edge != null ) {
			edge.set(newProp);
			edge.setReverse(isReverse);
			logger.debug("push edge : " + edge );
		}
		return edge;
	}
	
	public List<List<Object>> getCompleteMatchedObjects() {
		return completeMatchedObjects;
	}

	public void setCompleteMatchedObjects(List<List<Object>> completeMatchedObjects) {
		this.completeMatchedObjects = completeMatchedObjects;
	}

	
	
	/**
	 * 判断给顶的实体是否存在一个核心的匹配
	 * 核心匹配的意思是匹配的是class 或者是instance， 并且只匹配到一个实体且是完全匹配
	 *
	 * @param 
	 * @return int, -1, 不存在, 存在的位置 
	 */
	/*public int hasCoreMatch () {
		int res = -1;
		int i = 0;
		for (List<MatchedEntity> mes : this.queryMatchedEntities) {
			if (mes != null && mes.size() == 1 && !mes.get(0).isProperty()) {
				res = i; 
				break;
			}
			++i;
		}
		return res;
	}*/
	
	/**
	 * 从queryMatchedEntitis 中找到从begin开始的第一个不是属性的实体
	 *
	 * @param begin, 开始搜索的位置
	 * @return int, 位置， -1 没有找到
	 */
	/*public int findSearchBeginPos( int begin ) {
		if (begin < 0 || begin >= this.queryMatchedEntities.size())
			return -1;
		
		int findPos = -1;
		for (int i = begin; i < this.queryMatchedEntities.size(); ++i ) {
			List<MatchedEntity> mes = this.queryMatchedEntities.get(i);
			if (mes != null && mes.size() > 0 && ! mes.get(0).isProperty()) {
				findPos = i;
				break;
			}
		}
		return findPos;
	}*/
	
	public QueryMatchedEntityWrapper getEntityWrapper() {
		return entityWrapper;
	}

	public void setEntityWrapper(QueryMatchedEntityWrapper entityWrapper) {
		this.entityWrapper = entityWrapper;
	}

	/**
	 * 核心搜索算法
	 *
	 * @param 
	 * @return void 
	 */
	/*public void coreSearch  () {
		int pos = this.hasCoreMatch();
		if (pos == -1 ) { // 没有关键匹配的实体
			
			// 查找第一个
			pos = this.findSearchBeginPos(0);
			if (pos == -1 ) { //
				
			}
			else {
				
			}
		}
		else {
			
		}
	}
	
	public void searchLhs () {
		
	}
	
	
	public void searchRhs () {
		
	} */

	public List<List<MatchedEntity>> getQueryMatchedEntities() {
		return queryMatchedEntities;
	}
	
	
}
