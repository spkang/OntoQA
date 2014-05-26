/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ProbabilityGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.graph.LoopMultiGraph;
import cn.edu.hit.ir.graph.PropertyNode;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.ir.ontology.SchemaGraph;
import cn.edu.hit.ir.ontology.ScoredResource;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.scir.EntityMatcher.QueryMatchedEntityWrapper;

import com.hp.hpl.jena.rdf.model.Resource;

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
	
	
	// score configuration
	private Configuration config = null;
	
	//score
	// 为了使路径完整，添加的实体，该分值为添加实体的匹配分值
	private final String ADD_ENTITY_SCORE = "add.entity.score";
	// 为使路径完整，添加属性，该分值为添加属性的匹配分值
	private final String ADD_PROPERTY_SCORE = "add.property.score";
	
	private final String MATCH_PROBABILITY_SCORE = "match.probability.score";
	
	private double addEntityScore = 0.6;
	private double addPropertyScore = 0.4;
	private double matchProbabilityScore = 1.0;
			
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
		initConfig();
		initScore();
		completeMatchedEntities();
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
		graph = new LoopMultiGraph<ProbabilityNode, ProbabilityEdge>(ProbabilityEdge.class);
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
	private List<Object> me2ProbNodeObject(List<MatchedEntity> mes ) {
		if (mes == null || mes.isEmpty() ) return null;
		List<Object> objs = new ArrayList<Object>();
		for (MatchedEntity me : mes ) {
			ProbabilityNode pNode = new ProbabilityNode (me);
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
				if (mes == null || mes.isEmpty())
					continue;
				if (!mes.get(0).isProperty() && !isProperty ) {
					isProperty = !isProperty;
					this.completeMatchedObjects.add(this.me2ProbNodeObject(mes));
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
					objs = me2ProbNodeObject(mes);
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
		for (int i = bits.length-1; i >= 0 ; --i ) {
			if (bits[i]) {
				nb = (nb | ( 1 << i));
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
					logger.info("legal set : " + legalEntities);
					for (Object cur : currObjs) {
						if (legalEntities != null && cur instanceof ProbabilityNode && (legalEntities.contains(((ProbabilityNode)cur).getResource()))) {
							((ProbabilityNode)cur).setLegal(true);
							logger.info("(" + prev + ", " + cur + ", " + next + ")" + "true");
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
					if (this.schemaGraph.isLegalTriple(((ProbabilityNode)prev).getResource(), ((PropertyNode)cur).getProperty(), ((ProbabilityNode)next).getResource())) {
						((ProbabilityNode)prev).setLegal(true);
						((PropertyNode)cur).setLegal(true);
						((ProbabilityNode)next).setLegal(true);
						isExistsLegal = true;
					}
					else {
						if (prevObjs.size() == 1 && nextObjs.size() == 1 && currObjs.size() == 1) {
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
				}
			}
		}
		// 不存在合法  state border river, 属性border是错误的
		if (isExistsLegal) {
			// 删除不合法的元素
			removeIllegalObject (prevObjs);
			removeIllegalObject (currObjs);
			removeIllegalObject (nextObjs);
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
				switch (choice) {
				case 0 : // 0, 0, 0 没有是填充的, 合法检查
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
					}
					
					
					
					break;
				case 1 : // 0, 0, 1, 最后一个是添加的
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
					}
					
					// s, p --> o 
					
					break;
				case 2 : // 0, 1, 0, 中间那个是添加的
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
					}
					
					// s, o --> p
					
					break;
				case 3 : // 0, 1, 1, 不存在， 两个添加的内容不可能
					logger.info("不可能");
					break;
				case 4 : // 1, 0, 0, 第一个是添加的内容
					if (!this.removeIllegalEntityPropEntity(nextObjs, currObjs, prevObjs)) {
						logger.info("需要处理");
					}
					
					// o p --> s
					
					break;
				case 5 : // 1, 0, 1, 第一个和第三个是添加的内容
					if (!this.removeIllegalEntityPropEntity(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
					}
					
					
					break;
				case 6 : // 1, 1, 0 不存在，两个添加的内容不可能连在一起
					logger.info("不可能");
					break;
				case 7 :// 1, 1, 1, 都是填充的，这种情况不可能存在
					logger.info("不可能");
					break;
				default : 
					logger.info("不可能");
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
						logger.info("需要处理");
					}
					break;
				case 1 : // 0, 0, 1, 最后一个是添加的
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
					}
					break;
				case 2 : // 0, 1, 0, 中间那个是添加的
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
					}
					break;
				case 3 : // 0, 1, 1, 不存在， 两个添加的内容不可能
					break;
				case 4 : // 1, 0, 0, 第一个是添加的内容
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
					}
					break;
				case 5 : // 1, 0, 1, 第一个和第三个是添加的内容
					if (!this.removeIllegalPropEntityProp(prevObjs, currObjs, nextObjs)) {
						logger.info("需要处理");
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
	
//	public void calculateProbScore (ProbabilityNode subject, PropertyNode property, ProbabilityNode object) {
//		Set<ScoredResource> scRes = this.schemaGraph.getSubjProp2ObjSet(prevNode.getResource(), curNode.getProperty());
//		boolean exists = false;
//		for (ScoredResource sr : scRes ) {
//			if (sr.resource.equals(nextNode.getResource())) {
//				nextNode.setProbabilityScore(sr.score);
//				exists = true;
//				break;
//			}
//		}
//		if (!exists ) {
//			nextNode.setProbabilityScore(1.0 / nextObjs.size());
//		}
//	}
//	
	
	private boolean isSubjectObject (ProbabilityNode lhs, ProbabilityNode rhs, PropertyNode propterty) {
		if (lhs == null || rhs == null ) return false;
		Set<Resource> propSet = this.schemaGraph.getPropertySet(this.schemaGraph.getSchemaResource(lhs.getResource()), this.schemaGraph.getSchemaResource(rhs.getResource()));
		if (propSet != null && propSet.contains(propterty.getProperty())) {
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
							logger.info("s : " + prevNode.getResource()  + "\tp : " + curNode.getProperty() + "\to : " + nextNode.getResource());
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
									logger.info("1scRes : " + scRes);
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
									logger.info("2scRes : " + scRes);
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
								logger.info ("不做处理～～～");
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
	public int hasCoreMatch () {
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
	}
	
	/**
	 * 从queryMatchedEntitis 中找到从begin开始的第一个不是属性的实体
	 *
	 * @param begin, 开始搜索的位置
	 * @return int, 位置， -1 没有找到
	 */
	public int findSearchBeginPos( int begin ) {
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
	}
	
	/**
	 * 核心搜索算法
	 *
	 * @param 
	 * @return void 
	 */
	public void coreSearch  () {
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
		
	} 

	public List<List<MatchedEntity>> getQueryMatchedEntities() {
		return queryMatchedEntities;
	}
	
	
}