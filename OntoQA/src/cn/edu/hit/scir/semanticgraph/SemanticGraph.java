/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import cn.edu.hit.scir.dependency.GraphSearchType;
import cn.edu.hit.scir.dependency.StanfordEnglishNlpTool;
import cn.edu.hit.scir.dependency.StanfordNlpTool;

public class SemanticGraph {
	private static Logger logger = Logger.getLogger(SemanticGraph.class);
	private List<SemanticNode> sgVertexs = null; // semantic graph vertex node
	private DependencyGraph dependencyGraph = null;
	private int graphSize = -1;
	private List<ArrayList<SemanticEdge>> semanticGraph = null;
	private Map<DGNode, SemanticNode> vertexMap = null; // this is a map of the DGNode(noun node) to the SemanticNode
	private boolean[] visited;
	
	public SemanticGraph ( DependencyGraph dependencyGraph ) {
		this.dependencyGraph = dependencyGraph;
		this.sgVertexs = new ArrayList<SemanticNode>();
		this.semanticGraph = new ArrayList<ArrayList<SemanticEdge>>();
		if (this.dependencyGraph.getVertexs() != null )
			this.graphSize = this.dependencyGraph.getNounVertexs().size();
		else 
			this.graphSize = 0;
		this.vertexMap = new HashMap <DGNode, SemanticNode> ();
		initGraph();
	}
	

	public SemanticGraph (String sentence) {
		if (sentence == null || sentence.isEmpty())
			return ;
		StanfordNlpTool tool = StanfordEnglishNlpTool.getInstance();
		this.dependencyGraph = new DependencyGraph (tool, sentence);
		this.sgVertexs = new ArrayList<SemanticNode> ();
		this.semanticGraph = new ArrayList<ArrayList<SemanticEdge>>();
		if (this.dependencyGraph.getVertexs() != null )
			this.graphSize = this.dependencyGraph.getNounVertexs().size();
		else 
			this.graphSize = 0;
		this.vertexMap = new HashMap <DGNode, SemanticNode> ();
		initGraph();
	}
	
	
	
	public void initGraph() {
		if (this.graphSize < 0) {
			final String msg = "size must be a nonnegative integer. size: "
					+ graphSize;
			throw new IllegalArgumentException(msg);
		}
		// malloc the space of the semantic graph 
		mallocGraph();
		
		// init the vertex map and the sgVertexs
		initVertexMap();
		visited = new boolean[this.graphSize + 1];
		
	}

	private void initVertexMap () {
		// List<DGNode> 
		List<DGNode> orgNounNode = this.dependencyGraph.getNounVertexs();
		
		Integer semanticNodeIndex = 0;
		for (DGNode node : orgNounNode) {
			if ( ! this.vertexMap.containsKey(node)) {
				this.sgVertexs.add(new SemanticNode(null, null, null, semanticNodeIndex, false, false));
				this.vertexMap.put(node, this.sgVertexs.get(semanticNodeIndex));
				++semanticNodeIndex;
			}
		}
		
		logger.info("vertex map : ");
		Set<DGNode> keySet = this.vertexMap.keySet();
		for (DGNode node : keySet ) {
			logger.info("key : " + node );
			logger.info("value : " + this.vertexMap.get(node));
		} 
//		logger.info("root index : " + this.dependencyGraph.getRootIndex());
//		logger.info("root note is : " + this.dependencyGraph.getVertexNode(this.dependencyGraph.getRootIndex()));
	}
	
	
	private void mallocGraph () {
		// malloc graph
		for (int i = 0; i < this.graphSize; ++i ) {
			this.semanticGraph.add(new ArrayList<SemanticEdge>());
			for (int j = 0; j < this.graphSize; ++j) {
				this.semanticGraph.get(i).add(new SemanticEdge());
			}
		}
	}
	
	
	private SemanticNode generateSemanticNode (DGNode node) {
		if (node == null )
			return null;
		if (!this.vertexMap.containsKey(node)) {
			String msg = "the vertexMap does not contain the node !!" + node.toString() ;
			throw new IllegalArgumentException (msg);
		} 
		SemanticNode snode = vertexMap.get(node);
		List<DGNode> preModifiers = this.dependencyGraph.search(node,  GraphSearchType.PRE_NOUN_DFS);
		List<DGNode> postModifiers = this.dependencyGraph.search(node, GraphSearchType.POST_NOUN_DFS);
		List<DGNode> coreWords = new ArrayList<DGNode>();
		coreWords.add(node);
		snode.setPreModifiers(preModifiers);
		snode.setPostModifiers(postModifiers);
		snode.setCoreWords(coreWords);
		return snode;
	}
	
	private SemanticEdge generateEdge (DGNode lhsNode, DGNode rhsNode, DGNode linkNode, List<DGNode> preEdgeModifiers, List<DGNode> postEdgeModifiers) {
		if (lhsNode == null && rhsNode == null || linkNode == null )
			return null;
		SemanticNode lhsSemanticNode = generateSemanticNode (lhsNode);
		SemanticNode rhsSemanticNode = generateSemanticNode (rhsNode);
		
		logger.info("lhsSemanticNode : " + lhsSemanticNode);
		logger.info("rhsSemanticNode : " + rhsSemanticNode);
		
		List<DGNode>  linkWords = new ArrayList<DGNode> ();
		linkWords.add( linkNode);
		
		SemanticEdge edge = getEdge (this.vertexMap.get(lhsNode).getIndex(), this.vertexMap.get(rhsNode).getIndex());
		
		edge.setLhsNode(lhsSemanticNode);
		edge.setRhsNode(rhsSemanticNode);
		edge.setLinkWords(linkWords);
		edge.setPreEdgeModifiers(preEdgeModifiers);
		edge.setPostEdgeModifiers(postEdgeModifiers);
		edge.setConnected(true);
		// 是否应该设置成双向的？
		edge.setReversed(false);
		return edge;
	}

	public void setEdge (int row , int col, SemanticEdge edge) {
		if (!isDimLegal(row, col)) {
			String msg = "the input parameters are illegal! " +" row = " + row + ", col = " + col ;
			throw new IllegalArgumentException(msg);
		}
		this.semanticGraph.get(row).set(col, edge);
	}
	
	public SemanticEdge getEdge (int row, int col ) {
		if (!isDimLegal(row, col)) {
			String msg = "the input parameters are illegal! " +" row = " + row + ", col = " + col ;
			throw new IllegalArgumentException(msg);
		}
		return this.semanticGraph.get(row).get(col);
	}
	
	private boolean isDimLegal (int row, int col  ){
		if (row < 0 || row >= this.graphSize || col < 0 || col >= this.graphSize)
			return false;
		return true;
	} 
	
	
	/** 
	 * generate the semantic node for the semantic graph 
	 *
	 * @param DGNode, nounNode, the traverse noun node by the given link node 
	 * @param List<DGNode>, edgeModifiers, the linknode edge modifier
	 * @param DGNode, linkNode,the core edge node 
	 * @return void 
	 */
//	private SemanticNode generateNode (DGNode nounNode, List<DGNode> edgeModifiers, DGNode linkNode, int linkNodeIndex) {
//		if (edgeModifiers == null || edgeModifiers.isEmpty() || linkNode == null )
//			return null;
//		
//		// query node or the blank node, blank node is using for like "name all the rivers in the us"
//		if (nounNode == null && linkNodeIndex == 0) {
//			if (linkNode.idx == 0)  { // name all the rivers in the us , we need a blankNode A, A -> name -> river -> in -> us
//				SemanticNode newNode = new SemanticNode (null, null, null, this.generateNextSemanticNodeIndex() , true, true); // pre, post, core, index, isQuery, isBlank
//				this.mapVertexs.add(newNode);
//				return newNode;
//			}
//			else {
//				if (edgeModifiers != null ) {
//					
//				}
//				else { // 
//					;
//				}
//			}
//		}
//		return null;
//			
//	}
//	
//	private Integer generateNextSemanticNodeIndex () {
//		return (this.semanticNodeIndex++);
//	}
	
	
	/**
	 * buuid the SemanticGraph 
	 *
	 * @param null 
	 * @return String, for test 
	 */
	public String buildSemanticGraph () {
		if (dependencyGraph == null) {
			throw  new IllegalArgumentException("the dependency graph handle is null");
		}
		
		List<DGNode> loopNode = this.dependencyGraph.getLoopVertexs();
		
		StringBuffer bf = new StringBuffer ();
		String line = "";
		logger.info("loopNode size : " + loopNode.size());
		for (DGNode lnode : loopNode ) {
			if (lnode.tag.toUpperCase().startsWith(this.dependencyGraph.VERB)) {
				logger.info("lnode: " + lnode);
				List<DGNode> preModifiers = this.dependencyGraph.search(lnode, GraphSearchType.PRE_VERB_DFS);
				boolean isPreStopedByNoun = this.dependencyGraph.isStopedByNoun();
				DGNode preNounNode = null;
				if (isPreStopedByNoun)
					preNounNode= new DGNode (this.dependencyGraph.getStopNounNode());
				List<DGNode> postModifiers = this.dependencyGraph.search (lnode, GraphSearchType.POST_VERB_DFS);
				boolean isPostStopedByNoun = this.dependencyGraph.isStopedByNoun();
				DGNode postNounNode = null;
				if (isPostStopedByNoun )
					postNounNode = new DGNode( this.dependencyGraph.getStopNounNode());
				//List<DGNode> dfsVerb = this.dependencyGraph.search(lnode, GraphSearchType.VERB_DFS);
				// situation one : stop at noun same time
				
				if (isPreStopedByNoun && isPostStopedByNoun) {
					logger.info("preNoun : " + preNounNode);
					logger.info("postNoun : " + postNounNode);
					
					SemanticEdge edge = generateEdge (preNounNode, postNounNode, lnode, preModifiers, postModifiers);
					this.setEdge(this.vertexMap.get(preNounNode).getIndex(), this.vertexMap.get(postNounNode).getIndex(), edge);
					
				}
				else if ( !isPreStopedByNoun && isPostStopedByNoun) {
					logger.info("preNoun : " + preNounNode);
					logger.info("postNoun : " + postNounNode);
				}
				else if (isPreStopedByNoun && !isPostStopedByNoun){
					logger.info("preNoun : " + preNounNode);
					logger.info("postNoun : " + postNounNode);
				}
				
				line = "Modifiers : {" + StringUtils.join(preModifiers, " ~ ") + " flag: " + isPreStopedByNoun + " SN: " + preNounNode + " }" + " -> { " + lnode.toString() + " } <- {" + StringUtils.join(postModifiers, " ~ ") + " flag: " + isPostStopedByNoun + " SN: " + postNounNode +" }" ;
				//System.out.println("Modifiers : {" + StringUtils.join(preModifiers, " ~ ") + " stopedNoun : " + isPreStopedByNoun + " }" + " -> { " + lnode.toString() + " } <- {" + StringUtils.join(postModifiers, " ~ ") + "stopedNoun : " + isPostStopedByNoun + " }" );
				//line += "\n{ dfsVerb : " + StringUtils.join(dfsVerb, ", ") + "}\n";
				bf.append(line).append("\n");
			}
			else { // preposition 
				List<DGNode> preModifiers = this.dependencyGraph.search(lnode, GraphSearchType.PRE_IN_DFS);
				boolean isPreStopedByNoun = this.dependencyGraph.isStopedByNoun();
				DGNode preNounNode = this.dependencyGraph.getStopNounNode();
				List<DGNode> postModifiers = this.dependencyGraph.search (lnode, GraphSearchType.POST_IN_DFS);
				boolean isPostStopedByNoun = this.dependencyGraph.isStopedByNoun();
				DGNode postNounNode = this.dependencyGraph.getStopNounNode();
				line = "Modifiers : {" + StringUtils.join(preModifiers, " ~ ") + " flag : " + isPreStopedByNoun + " SN : " + preNounNode + " }" + " -> { " + lnode.toString() + " } <- {" + StringUtils.join(postModifiers, " ~ ") + " flag : " + isPostStopedByNoun + " SN : " + postNounNode +" }" ;
				//line = "Modifiers : {" + StringUtils.join(preModifiers, " ~ ") + " stopedNoun : " + isPreStopedByNoun + " }" + " -> { " + lnode.toString() + " } <- {" + StringUtils.join(postModifiers, " ~ ") + " stopedNoun : " + isPostStopedByNoun + " }" ;
//				System.out.println("Modifiers : {" + StringUtils.join(preModifiers, " ~ ") + " stopedNoun : " + isPreStopedByNoun + " }" + " -> { " + lnode.toString() + " } <- {" + StringUtils.join(postModifiers, " ~ ") + " stopedNoun : " + isPostStopedByNoun + " }" );
				bf.append(line).append("\n");
				
				if (isPreStopedByNoun && isPostStopedByNoun) {
					logger.info("in preNoun : " + preNounNode);
					logger.info("in postNoun : " + postNounNode);
					
					SemanticEdge edge = generateEdge (preNounNode, postNounNode, lnode, preModifiers, postModifiers);
					this.setEdge(this.vertexMap.get(preNounNode).getIndex(), this.vertexMap.get(postNounNode).getIndex(), edge);
				}
				else {
					logger.info("in preNoun : " + preNounNode);
					logger.info("in postNoun : " + postNounNode);
				}
			}
		}
		return bf.toString();
	}
	
	
	public List<String> searchSemanticGraph (int v) {
		if (this.graphSize < 2)
			return null;
		for (int i = 0; i < this.graphSize; ++i ) {
			visited[i] = false;
		}
		List <String> path = new ArrayList<String>();
		dfs(v, path);
		return path;
	} 
	
	/**
	 * deep first search for the semantic graph
	 *
	 * @param int v, the begin point to search 
	 * @return void 
	 */
	public void dfs (int v, List<String> path) {
		if (v < 0 || v >= this.graphSize) {
			String msg = "the parameter v is out of range! v = " + v;
			throw new IllegalArgumentException (msg);
		}
		
		visited[v] = true;
		if (this.sgVertexs.get(v) == null)
			return ;
		if (this.sgVertexs.get(v).getCoreWords() == null)
			return ;
		path.add(this.sgVertexs.get(v).getCoreWords().get(0).toString());
		//path.add(String.valueOf(v));
		for (int u = 0; u < this.graphSize; ++u ) {
			SemanticEdge edge = getEdge (v, u);
			if (edge.isConnected() && visited[u] == false) {
				//path.add(edge.getLinkWords().get(0).toString());
				path.add(String.valueOf(edge.getLinkWords().get(0).word));
				dfs(u, path);
			}
		}	
	}

	public List<SemanticNode> getSgVertexs() {
		return sgVertexs;
	}


	public void setSgVertexs(List<SemanticNode> sgVertexs) {
		this.sgVertexs = sgVertexs;
	}


	public DependencyGraph getDependencyGraph() {
		return dependencyGraph;
	}


	public void setDependencyGraph(DependencyGraph dependencyGraph) {
		this.dependencyGraph = dependencyGraph;
	}


	public int getGraphSize() {
		return graphSize;
	}


	public void setGraphSize(int graphSize) {
		this.graphSize = graphSize;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("semantic graph vertexs : \n");
		sb.append("{\n" + StringUtils.join(this.sgVertexs, ", ") + "\n}\n");
		sb.append("[\n");
		for (int i = 0; i < this.semanticGraph.size(); i++) {
			sb.append(i + ": [");
			for (SemanticEdge edge : this.semanticGraph.get(i)) {
				if (edge.isConnected()) {
					sb.append(1 + "\t");
					logger.info("edge : { \n" + edge.toString() + " }\n");
				}
				else 
					sb.append(0 + "\t");
			}
			sb.append("]\n");
		}
		sb.append("]\n");
		return sb.toString();
	}
}

