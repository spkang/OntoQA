/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.EntityMatcher;
import cn.edu.hit.ir.dict.MatchedEntitiesSentence;
import cn.edu.hit.ir.ontology.Ontology;
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

	
	private static Logger logger = Logger.getLogger(MatchedPath.class);
	public MatchedPath (String sentence) {
		init(sentence);
	}
	
	private  void init (String sentence) {
		if (this.semanticGraph == null )
			this.semanticGraph = new SemanticGraph (sentence);
		logger.info(this.semanticGraph.buildSemanticGraph());
		this.path = this.semanticGraph.searchSemanticGraphPath();
		this.snodeMap = new HashMap <SemanticNode, MatchedEntitiesSentence> ();
	}
	
	
	public void match () {
		for (SemanticEdge edge : path) {
			matchNode(edge.getLhsNode());
			matchNode(edge.getRhsNode());
		}
	}
	
	public void  matchNode (SemanticNode node ) {
		if (node == null )
			return ;
		if ( ! this.snodeMap.containsKey(node)) {
			String phrase = StringUtils.join (node.getCoreWords(), " ");
			MatchedEntitiesSentence mel = entityMatcher.match(ontology, phrase);
			logger.info("match phrase  : " + phrase);
			logger.info("matced entity : " + mel.toString());
			this.snodeMap.put(node, mel);
		}
	}
	
	public List<SemanticEdge> getPath () {
		return this.path;
	}
	
	public String toString () {
		StringBuffer bf = new StringBuffer();
		bf.append("{\n");
		for (SemanticNode node : this.snodeMap.keySet()) {
			bf.append("[\n");
			bf.append ("key : " + node.toString() + "\n");
			bf.append("val : " + this.snodeMap.get(node) + "\n]");
		}
		return bf.toString();
	}
}
