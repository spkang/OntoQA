/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.graph.PropertyNode;
import cn.edu.hit.ir.graph.QueryEdge;
import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.graph.QueryNode;
import cn.edu.hit.ir.nlp.Vocabulary;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.SchemaGraph;
import cn.edu.hit.ir.ontology.Sparql;
import cn.edu.hit.scir.EntityMatcher.ChineseQueryMatchedEntityWrapper;
import cn.edu.hit.scir.EntityMatcher.QueryMatchedEntityWrapper;
import cn.edu.hit.scir.dependency.ChineseWord;
import cn.edu.hit.scir.semanticgraph.DGNode;

import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月14日 
 */



public class GenerateChineseSparql {
	
	public static final String NO_SPARQL = "";
	
	private static Logger logger = Logger.getLogger(GenerateChineseSparql.class);
	
	private Ontology ontology;
	private SchemaGraph schemaGraph;
	private Vocabulary vocabulary;
	
	private Sparql sparql;
	private Sparql subQuery;
	
	private int varCount = 0;
	
	private Map<String, String> prefixMap;
	
	//private List<String> triples;
	
	private Set<String> nameSet;
	
	private Map<QueryNode, String> node2varMap;
	
	//private MatchedPath matchedPath = null;
//	private QueryMatchedEntityWrapper meWrapper = null;
	private ChineseQueryMatchedEntityWrapper meWrapper = null;
	
	private String[] tokens;
	private String[] tags;
	//private String[] stems;
	
	public GenerateChineseSparql(Ontology ontology) {
		this.ontology = ontology;
		schemaGraph = ontology.getSchemaGraph();
		vocabulary = Vocabulary.getInstance();
		
		prefixMap = new HashMap<String, String>();
		nameSet = new HashSet<String>();
		node2varMap = new HashMap<QueryNode, String>();
	}
	
	private void initData() {		
		nameSet.clear();
		node2varMap.clear();
		varCount = 0;
	}

	private void initSentence (List<ChineseWord> sentenceVertexs) {
		if (sentenceVertexs == null ) {
			logger.error("sentence vertexs is null!");
			return ;
		}
		
		this.tokens = new String[sentenceVertexs.size()];
		this.tags  = new String[sentenceVertexs.size()];
		for (int i = 0; i < sentenceVertexs.size(); ++i ) {
			tokens[i] = sentenceVertexs.get(i).word;
			tags[i] = sentenceVertexs.get(i).tag;
		}
	}
	
//	private void initSentence(MatchedEntitiesSentence sentence) {
//		NlpSentence nlpSent = sentence.getNlpSentence();
//		tokens = nlpSent.getTokens();
//		stems = nlpSent.getStems();
//		tags = nlpSent.getTags();
//	}
	
	public void addPrefix(String prefix, String shortName) {
		prefixMap.put(prefix, shortName);
	}
	
	public String shortenUri(String uri) {
		Set<String> prefixes = prefixMap.keySet();
		
		for (String prefix : prefixes) {
			if (uri.startsWith(prefix)) {
				String name = prefixMap.get(prefix);
				return uri.replace(prefix, name + ":");
			}
		}
		return uri;
	}
	
	/**
	 * Handles query like "What is the longest river?" or 
	 * "What is the most populous state?".
	 *
	 * @param node
	 */
//	private boolean handleMaxOrMin (QueryNode node ) {
//		MatchedEntity entity = node.getEntity();
//		if (entity == null ) return false;
//		
//		if (!ontology.isClass(node.getResource())) return false;
//		
//		logger.debug("@handleMaxOrMin node : " + node);
//		
//		if (entity.getModifizers() != null ) {
//			// jjs or rbs
//			logger.debug("node : modifier : " + StringUtils.join(entity.getModifizers(), ", "));
//			for (DGNode mNode : entity.getModifizers()) {
//				if (this.meWrapper.getDepGraph().isSuperModifier(mNode) && !mNode.word.toLowerCase().equals("most")) {
//					Resource subject = node.getResource();
//					//String abj = stems[idx1];
//					String abj = mNode.stem;
//					Resource cmpProp = schemaGraph.getComparableProperty(subject, abj);
//					String s = "?" + node.toString();
//					String p = getName(cmpProp);
//					String var = nextVar();
//					sparql.addWhere(s, p, var);
//					
//					if (vocabulary.isSmallWord(abj)) {
//						sparql.addAscOrder(var);
//					} else {
//						sparql.addDescOrder(var);
//					}
//					sparql.addSelect(var);	// TODO test
//					sparql.setLimit(1);
//					return true;
//				}
//			} // for 
//		} // if 
//		return false;
//	}
	
	
	
	/**
	 * Handles query like "what state has the smallest area ?".
	 *
	 * @param s
	 * @param p
	 * @param o
	 */
	private boolean handleMaxOrMin (QueryNode s, PropertyNode p, QueryNode o ) {
		Resource subject = s.getResource();
		Resource property = p.getProperty();
		Resource object = o.getResource();


		logger.info("handle MaxOrMin s p o :  entity : " + p.getEntity());
		
		MatchedEntity entity = p.getEntity();
		
		if (entity == null ) return false;
		
		if (entity.getModifizers() != null ) {
			for (DGNode node : entity.getModifizers()) {
				if (this.meWrapper.getCnStanfordBasedGraph().isSupperModifier(node)) {
					logger.info("Modify Node : " + node.toString());
//						String abj = stems[idx];
					
					String groupName = getName(s);
					String countName = getName(o);
					
					//sparql.addSelect(groupName);
					String countVar = sparql.addCount(countName);
					sparql.addGroupBy(groupName);
					
					if (this.meWrapper.getCnStanfordBasedGraph().isMinModifier(node) ) {
						sparql.addAscOrder(countVar);
					} else {
						sparql.addDescOrder(countVar);
					}
					sparql.setLimit(1);
					return true;
					
//					String var = getLiteralVar(o);
//					if (vocabulary.isSmallWord(abj)) {
//						sparql.addAscOrder(var);
//					} else {
//						sparql.addDescOrder(var);
//					}
//					sparql.addSelect(var);	// TODO test
//					sparql.setLimit(1);
//					return true;
				}
			}
		}
			
		return false;
	}
	
	
	public String generate(QueryGraph graph, List<ChineseWord > vertexs, ChineseQueryMatchedEntityWrapper meWrapper ) {
		/*sparql = new Sparql();
		
		if (graph == null || !graph.hasQuery()) {
			return sparql.toString();
		}
		
		initData();
		initSentence(graph.getSentence());
		
		QueryNode source = graph.getSource();
		String target = getName(source);
		
		Set<QueryEdge> edges = graph.edgeSet();
		if (edges != null && edges.size() > 0) {
			for (QueryEdge edge : edges) {
				QueryNode s = graph.getEdgeSource(edge);
				QueryNode t = graph.getEdgeTarget(edge);
				PropertyNode p = edge.getPropertyNode();
				if (!edge.isReverse()) {
					generate(s, p, t);
					// Handles query like "what state has the smallest area ?"
					handleMaxOrMin(s, p, t);
				} else {
					generate(t, p, s);
				}
			}
		} else {
			
		}
		
		if (!graph.isCount()) {
			sparql.addSelect(target);
		} else {
			sparql.addCount(target);
		}

		return sparql.getString();*/
		setQueryMatchedEntityWrapper (meWrapper);
		sparql = generateSparql(graph, vertexs);
		return sparql.getString();
	}
	
	public void setQueryMatchedEntityWrapper (ChineseQueryMatchedEntityWrapper meWrapper ) {
		this.meWrapper = meWrapper;
	}
	
	/**
	 *  判断一个实体是不是存在否定修饰
	 *
	 * @param 
	 * @return boolean 
	 */
//	private boolean isNotNoModifier (MatchedEntity me ) {
//		if (me == null )
//			return false;
//		if (me.getModifizers() == null || me.getModifizers().isEmpty())
//			return false;
//		for (DGNode node : me.getModifizers()) {
//			if (this.meWrapper.getDepGraph().isNegativeModifier(node)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * 判断一个三元组是不是存在否定修饰，不论是subject, predicate, object
	 *
	 * @param 
	 * @return boolean 
	 */
//	public boolean isNotNoModifier (QueryNode s, PropertyNode p, QueryNode o) {
//		if (s == null || p == null || o == null )
//			return false;
//		if (isNotNoModifier(s.getEntity()) || isNotNoModifier(p.getEntity()) ||isNotNoModifier(o.getEntity()))
//			return true;
//		return false;
//	}
	
	public void generate(QueryNode s, PropertyNode p, QueryNode o) {
		String triple = "";
		String subject = getName(s);
		String property = getName(p.getProperty());
		String object = getName(o);
		triple = subject + " " + property + " " + object + " .";
		sparql.addWhere(triple);
	}
	
	
	
	public String getName(Resource r) {		
		String uri = shortenUri(r.getURI());
		return uri;
	}
	
	public String getName(QueryNode node) {
		Resource r = node.getResource();
		
		// if it's literal value
		if (node.isLiteralValue()) {
			return "\"" + node.getValue() + "\"";
		}
		
		// if it's literal class
		if (ontology.isLiteralClass(r)) {
			return getLiteralVar(node);
		}
		
		// if it's a class
		if (ontology.isClass(r)) {		
			String name = "?" + node.toString();
			if (!nameSet.contains(name)) {
				nameSet.add(name);
				String c = getName(r);
				String triple = name + " a " + c + " .";
				sparql.addWhere(triple); 
				
				//handleMaxOrMin(node);
			}
			return name;
		// if it's a instance
		} else {
			String name = "?" + node.toString();
			if (!nameSet.contains(name)) {
				nameSet.add(name);
				String value = "\"" + ontology.getLabel(r) + "\"";
				sparql.addWhere(name, Ontology.RDFS_LABEL, value);
				String c = getName(node.getSchemaResource());
				sparql.addWhere(name, "a", c);
			}
			return name;
		}
	}
	
	public String getLiteralVar(QueryNode literal) {
		if (node2varMap.containsKey(literal)) {
			return node2varMap.get(literal);
		} else {
			String var = nextVar();
			node2varMap.put(literal, var);
			return var;
		}
	}
	
	public String nextVar() {
		return "?x_" + (varCount++);
	}
	
	public QueryNode otherVertex(QueryGraph graph, QueryEdge edge, QueryNode node) {
		QueryNode source = graph.getEdgeSource(edge);
		QueryNode target = graph.getEdgeTarget(edge);
		if (node.equals(source)) {
			return target;
		} else if (node.equals(target)) {
			return source;
		} else {
			return null;
		}
	}
	
	public QueryEdge getOneEdge(Set<QueryEdge> edges) {
		if (edges == null) return null;
		
		Iterator<QueryEdge> it = edges.iterator();
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
		
	/**
	 * Given the source, finds the target node in the path.
	 *
	 * @param graph the graph
	 * @param source the source
	 * @return the target node in the path
	 */
	public QueryNode findTarget(QueryGraph graph, QueryNode source) {
		if (! graph.vertexSet().contains(source))
			return null;
		Set<QueryEdge> edges = graph.edgesOf(source);
		// if only exists one node
		if (edges.size() == 0) {
			return source;
		}
		QueryEdge curEdge = getOneEdge(edges);
		QueryNode cur = source;
		while (cur != null) {
			QueryNode other = otherVertex(graph, curEdge, cur);
			edges = graph.edgesOf(other);
			if (edges.size() == 1) {
				return other;
			}
			for (QueryEdge edge : edges) {
				if (!edge.equals(curEdge)) {
					cur = other;
					curEdge = edge;
					break;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * // TODO
	 * Handles query like "the state borders the most states" or 
	 * "the state borders the most number of states".
	 *
	 * @param node
	 */
	
	private boolean handleMaxOrMinCount(QueryNode s, QueryNode t) {
		MatchedEntity sEntity = s.getEntity();
		MatchedEntity tEntity = t.getEntity();
		if (sEntity == null || tEntity == null ) return false;
		
//		int sEnd = sEntity.getEnd();
//		int tBegin = tEntity.getBegin();
//		int idx1 = tBegin - 1;

		logger.info("@handleMaxOrMinCount : s:  " + s.toString() + "\tt : " + t.toString()); 
		// Handle query like "the state borders the most states".
		if (tEntity.getModifizers() == null )
			return false;
		logger.debug("modifier : " + StringUtils.join(tEntity.getModifizers()));
		for (DGNode node : tEntity.getModifizers()) {
			if (this.meWrapper.getCnStanfordBasedGraph().isMaxModifier(node) || this.meWrapper.getCnStanfordBasedGraph().isMinModifier(node) ) { //idx1 > sEnd && tags[idx1].equals("RBS")
				//String abj = stems[idx1];
				//String abj = node.stem;
				String groupName = "?" + s.toString();
				String countName = "?" + t.toString();
				
				//sparql.addSelect(groupName);
				String countVar = sparql.addCount(countName);
				sparql.addGroupBy(groupName);
				
				if (this.meWrapper.getCnStanfordBasedGraph().isMinModifier(node) ) {
					sparql.addAscOrder(countVar);
				} else {
					sparql.addDescOrder(countVar);
				}
				sparql.setLimit(1);
				return true;
			}
		}
		
		// Handle query like "the state borders the most number of states"
		// TODO
		
		return false;
	}
	
		
	public void generateSubQuery(QueryNode node) {
		subQuery = sparql;
		String name = getName(node);
		subQuery.addSelect(name);
		
		sparql = new Sparql();
		sparql.addSubQuery(subQuery);
	}
	
	public Sparql mergeSubQuery(Sparql sparql) {
		Sparql subQuery = sparql.getSubQuery();
		if (subQuery != null && subQuery.getSelectSize() == 1) {
			sparql = subQuery;
		}
		return sparql;
	}
	
	public Sparql generateSparql(QueryGraph graph, List<ChineseWord> vertexs) {
		
		
		sparql = new Sparql();
		
		if (graph == null || !graph.hasQuery()) {
			return sparql;
		}
		
		logger.info("query graph : <--" + graph.toString() + "-->"); //debug
		logger.debug("schema Graph : <--" + schemaGraph.toString() + "-->"); // debug
		
		
		initData();
		initSentence(vertexs);
		
		QueryNode source = graph.getSource();
		QueryNode target = findTarget(graph, source);
		
		// From the target to the source
		QueryNode cur = target;
		// Handles query like "What is the longest river?"
//		if (handleMaxOrMin(cur)) {
//			generateSubQuery(cur);
//		}
		logger.debug("cur : " + cur.toString());
		QueryEdge preEdge = null;
		while (!cur.equals(source)) {
			Set<QueryEdge> edges = graph.edgesOf(cur);
			for (QueryEdge edge : edges) {
				if (!edge.equals(preEdge)) {
					
					QueryNode other = otherVertex(graph, edge, cur);
					
					logger.info("other: " + other + ", cur: " + cur);
					
					QueryNode s = graph.getEdgeSource(edge);
					QueryNode t = graph.getEdgeTarget(edge);
					PropertyNode p = edge.getPropertyNode();
					logger.info("s : " + s.toString()); // debug
					logger.info("p : " + p.toString());
					logger.info("t : " + t.toString());
					if (!edge.isReverse()) {
						generate(s, p, t);
						// Handles query like "what state has the smallest area ?"
						if (handleMaxOrMin(s, p, t)) {
							generateSubQuery(s);
						}
					} else {
						generate(t, p, s);
					}
					
					// Handles query like "the river runs through the most states"
					if (handleMaxOrMinCount(other, cur)) {
						generateSubQuery(other);
					}
//					
//					// Handles query like "What is the longest river?"
//					if (handleMaxOrMin(other)) {
//						generateSubQuery(other);
//					}
					
					preEdge = edge;
					cur = other;
					break;
				}
			}
		}
		
		String focus = getName(source);
		
		if (sparql.onlySubQuery()) {
			sparql = subQuery;
			sparql.addFirstSelect(focus);
		} else {			
			if (!graph.isCount()) {
				sparql.addSelect(focus);
			} else {
				sparql.addCount(focus);
			}
		}
		
		return sparql;
	}
}
