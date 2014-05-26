/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ProbabilityGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;

import cn.edu.hit.ir.graph.LoopMultiGraph;
import cn.edu.hit.ir.graph.QueryEdge;
import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.graph.QueryNode;
import cn.edu.hit.ir.util.ConfigUtil;

/**
 * 用于选择路径
 *
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月26日 
 */
public class GraphPathSelector {
	
	private DijkstraShortestPath<ProbabilityNode, ProbabilityEdge> dijkstraPath = null;
	private List<ProbabilityNode> beginVertexs = null;
	private List<ProbabilityNode> endVertexs = null;
	private LoopMultiGraph graph = null;
	private static Logger logger = Logger.getLogger(GraphPathSelector.class);
	
	//private Map<GraphPath<ProbabilityNode, ProbabilityEdge>, Double> graphPathMap  = null;
	
	private GraphPath highestScoredPath = null; 
	
	private Configuration config = null;
	
	private final String SCORE_CONNECT_THETA = "score.connect.theta";
	// 用于query匹配分值和本体匹配分值的加权和 theta*matchScore + (1 - theta)*probabilityScore
	private double connectTheta = 0.5;
	
	public GraphPathSelector (LoopMultiGraph loopMultiGraph, List<ProbabilityNode> beginVertexs, List<ProbabilityNode> endVertexs) {
		setGraph (loopMultiGraph);
		setBeginVertexs (beginVertexs);
		setEndVertexs (endVertexs);
		//this.graphPathMap = new HashMap<GraphPath<ProbabilityNode, ProbabilityEdge>, Double>();
		initConfig();
		initTheta();
		findAllPath();
	}
	
	private void initTheta () {
		this.connectTheta = config.getDouble(this.SCORE_CONNECT_THETA);
	}
	
	private void initConfig () {
		try {
			config = new PropertiesConfiguration (ConfigUtil.getPath(getClass()));
		}catch (ConfigurationException ex ) {
			ex.printStackTrace();
		}
	}
	
	private void findAllPath () {
		if (beginVertexs == null || endVertexs == null || beginVertexs.isEmpty() || endVertexs.isEmpty())
			return ;
		double maxScore = -10000;
		for (ProbabilityNode begin : beginVertexs ) {
			for (ProbabilityNode end : endVertexs ) {
				dijkstraPath = new DijkstraShortestPath (this.getGraph(), begin, end);
				if (dijkstraPath != null ) {
					if (this.dijkstraPath.getPath() != null ) {
						//this.graphPathMap.put(this.dijkstraPath.getPath(), graphPathScore(this.dijkstraPath.getPath()));
						double tmpScore = graphPathScore(this.dijkstraPath.getPath());
						if (tmpScore > maxScore) {
							maxScore = tmpScore;
							this.highestScoredPath = this.dijkstraPath.getPath();
						}
						logger.info("{score : " + tmpScore + "\t[" + begin + " -> " + end + "] = " + this.dijkstraPath.getPath() + "}");
					}
				}
			}
		}
//		for (GraphPath p : this.graphPathMap.keySet()) {
//			logger.info("score : " + this.graphPathMap.get(p) + "\tpath : " + p.toString());
//		}
	}
	
	
	public GraphPath getHighestScoredPath() {
		return highestScoredPath;
	}

	public void setHighestScoredPath(GraphPath highestScoredPath) {
		this.highestScoredPath = highestScoredPath;
	}

	public ProbabilityNode otherVertex(ProbabilityEdge edge, ProbabilityNode node) {
		ProbabilityNode source = (ProbabilityNode)this.graph.getEdgeSource(edge);
		ProbabilityNode target = (ProbabilityNode)this.graph.getEdgeTarget(edge);
		if (node.equals(source)) {
			return target;
		} else if (node.equals(target)) {
			return source;
		} else {
			return null;
		}
	}
	
	public Double graphPathScore (GraphPath path ) {
		if (path == null ) {
			return 0.0;
		}
		List<ProbabilityEdge> edges = path.getEdgeList();
		double matchScore = 0.0;
		double probabilityScore = 0.0;
		
		ProbabilityNode source = (ProbabilityNode)path.getStartVertex();
		ProbabilityNode target = null;
		for (int i = 0; i < edges.size(); ++i ) {
			ProbabilityEdge edge = edges.get(i);
			
			target = this.otherVertex(edge, source);
			if (source != null && target != null ) {
				matchScore += source.getMatchScore() + edge.getWeight() + (i == edges.size()-1 ? target.getMatchScore() : 0.0);;
				probabilityScore += source.getProbabilityScore() + edge.getProbabilityScore() + (i == edges.size()-1 ? target.getProbabilityScore() : 0.0);
			}
			source = target;
			logger.info("edge : " + edge);
		}
		logger.info("matchScore : " + matchScore);
		logger.info("probaScore : " + probabilityScore);
		return connectTheta * matchScore + (1-connectTheta)*probabilityScore;
	}
	
	public List<ProbabilityNode> getBeginVertexs() {
		return beginVertexs;
	}
	public void setBeginVertexs(List<ProbabilityNode> beginVertexs) {
		this.beginVertexs = beginVertexs;
	}
	public List<ProbabilityNode> getEndVertexs() {
		return endVertexs;
	}
	public void setEndVertexs(List<ProbabilityNode> endVertexs) {
		this.endVertexs = endVertexs;
	}
	public LoopMultiGraph getGraph() {
		return graph;
	}
	public void setGraph(LoopMultiGraph graph) {
		this.graph = graph;
	}
	
	
	
	
	
}
