/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.EntityMatcher;
import cn.edu.hit.ir.dict.MatchedEntitiesSentence;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.scir.semanticgraph.DGNode;
import cn.edu.hit.scir.semanticgraph.SemanticEdge;
import cn.edu.hit.scir.semanticgraph.SemanticGraph;
import cn.edu.hit.scir.semanticgraph.SemanticNode;

/**
 * a data structure which match the path of the semantic graph 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月10日 
 */
public class MatchedPath {
	private SemanticGraph semanticGraph = null;
	private List<SemanticEdge> path = null;
	private Map <SemanticNode, MatchedEntitiesSentence> snodeMap = null;
	
	private Ontology ontology = Ontology.getInstance();
	
	private EntityMatcher entityMatcher = new EntityMatcher();
	
	private OntologyEntityMatcher oeMatcher = new OntologyEntityMatcher();
	
	private List<PathNode> pathNode = null;
	
	private Map <PathNode, List<MatchedEntity>> pathNodeMap = null;

	
	private static Logger logger = Logger.getLogger(MatchedPath.class);
	
	public MatchedPath () {
		
	}
	
	public MatchedPath (String sentence) {
		init(sentence);
	}
	
	private  void init (String sentence) {
		this.semanticGraph = new SemanticGraph (sentence);
		this.semanticGraph.buildSemanticGraph();
		//logger.info(this.semanticGraph.buildSemanticGraph());
		this.path = this.semanticGraph.searchSemanticGraphPath();
		this.snodeMap = new HashMap <SemanticNode, MatchedEntitiesSentence> ();
		this.pathNode = new ArrayList<PathNode>();
		this.pathNodeMap = new HashMap<PathNode, List<MatchedEntity>>();
	}
	
	
	public void setSentence  (String sentence ) {
		init (sentence);
	} 
	
	public void match () {
		for (SemanticEdge edge : path) {
			if ( ! pathNode.contains(edge.getLhsNode())) {
				pathNode.add(new PathNode (edge.getLhsNode()));
			}
			if (! pathNode.contains(edge)) {
				pathNode.add(new PathNode (edge));
			}
			if ( ! pathNode.contains(edge.getRhsNode())) {
				pathNode.add(new PathNode (edge.getRhsNode()));
			}
			
			matchNode(edge.getLhsNode());
			matchLinkWords (edge);
			matchNode(edge.getRhsNode());
		}
		
		for (PathNode pn : this.pathNode) {
//			logger.info("pathNode : " + pn.toString());
			if ( ! this.pathNodeMap.containsKey(pn) ) {
				this.pathNodeMap.put(pn,  oeMatcher.mathPathNode(ontology, pn));
				for (MatchedEntity me : this.pathNodeMap.get(pn)) {
					me.setPathNode(pn);
				}
			}
		}
		
	}
	
	
	public  void  matchNode (SemanticNode node ) {
		if (node == null )
			return ;
		
		if ( ! this.snodeMap.containsKey(node)) {
			String phrase = StringUtils.join (node.getCoreWords(), " ");
			MatchedEntitiesSentence mel = entityMatcher.match(ontology, phrase);
			//logger.info("match phrase  : " + phrase);
			//logger.info("matced entity : " + mel.toString());
			this.snodeMap.put(node, mel);
		}
	}
	
	public void matchLinkWords (SemanticEdge edge ) {
		List<DGNode> lws = edge.getLinkWords();
		String phrase = "";
		for (DGNode node : lws ) {
			phrase += node.word + " ";
		}
		phrase = phrase.trim();
	//	String phrase = StringUtils.join (edge.getLinkWords(), " ");
		MatchedEntitiesSentence mel = entityMatcher.match (ontology, phrase);
//		logger.info("match linkwords : " + phrase);
//		logger.info("match entities  : " + mel.toString());
	} 
	
	public List<SemanticEdge> getPath () {
		return this.path;
	}
	
	
	
	public List<PathNode> getPathNode() {
		return pathNode;
	}

	public void setPathNode(List<PathNode> pathNode) {
		this.pathNode = pathNode;
	}

	public Map<PathNode, List<MatchedEntity>> getPathNodeMap() {
		return pathNodeMap;
	}

	public void setPathNodeMap(Map<PathNode, List<MatchedEntity>> pathNodeMap) {
		this.pathNodeMap = pathNodeMap;
	}

	public void setPath(List<SemanticEdge> path) {
		this.path = path;
	}

	private boolean isLegalIndex (int index ) {
		if (index < 0 || index >= this.pathNodeMap.size()) {
			return false;
		}
		return  true;
	}
	
	public int nextIndex (int begin) {
		if (!isLegalIndex (begin)) {
			return -1;
		}
		return (++begin);
	}
	
	public boolean hasNextIndex(int begin ) {
		return isLegalIndex (nextIndex(begin));
	}
	
	public int prevIndex (int begin ) {
		if (! isLegalIndex (begin)) {
			return -1;
		}
		return (--begin);
	}
	
	public boolean hasPrevIndex (int begin) {
		return isLegalIndex (prevIndex (begin));
	}
	
	public PathNode getPathNode (int index ) {
		if (index < 0 || index >= this.pathNode.size())
			return null;
		return this.pathNode.get(index);
	}
	
	public SemanticGraph getSemanticGraph() {
		return semanticGraph;
	}

	public void setSemanticGraph(SemanticGraph semanticGraph) {
		this.semanticGraph = semanticGraph;
	}

	public String pathNodeToLineString () {
		StringBuffer bf = new StringBuffer ();
		List<DGNode> allNodes = new ArrayList<DGNode>();
		for (PathNode pNode : this.pathNode) {
			//List<MatchedEntity> mes = this.pathNodeMap.get(pNode);
			if (pNode.isSemanticEdge()) {
				SemanticEdge edge = (SemanticEdge)pNode.getNode();
				allNodes.addAll(edge.getAllDGNodes(edge.getPreEdgeModifiers(), edge.getLinkWords(), edge.getPostEdgeModifiers()));
			}
			else {
				SemanticNode node = (SemanticNode) pNode.getNode();
				allNodes.addAll(node.getAllDGNodes(node.getPreModifiers(), node.getCoreWords(), node.getPostModifiers()));
			}
		}
		
		bf.append(StringUtils.join(allNodes, " ")); 
		
		return bf.toString();
	}
	
	@Override
	public String toString() {
		return "MatchedPath [path=" + path + ", pathNode=" + pathNode
				+ ", pathNodeMap=" + pathNodeMap + "]";
	}

	
}
