/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.event.ListSelectionEvent;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.questionanalysis.QuestionNormalizer;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.scir.dependency.GraphSearchType;
import cn.edu.hit.scir.dependency.StanfordNlpTool;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.StringUtils;

/**
 * a dependency graph for the origin question , it help constructing the
 * semantic graph
 * 
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月27日
 */
public class DependencyGraph {
	public final String NOUN = "NN";
	public final String VERB = "VB";
	public final String IN   = "IN";
	
	public final String JJS = "JJS"; // JJS Adjective, superlative
	public final String JJ = "JJ"; // Adjective
	public final String JJR = "JJR"; // JJR Adjective, comparative
	
	public final String RB = "RB"; // RB Adverb
	public final String RBR = "RBR"; // RBR Adverb, comparative
	public final String RBS = "RBS"; // RBS Adverb, superlative
	
	
	
	private static Logger logger = Logger.getLogger(DependencyGraph.class);
	private static Configuration config = null;
	private static final String EXPAND_NOUN_DICT_PATH = "dict.files";
	private String orgQuestion = null; // original question
	private String processedQuestion = null; // processed question ,remove punt
												// marks and others
	private String[] tokens = null;
	private String[] tags = null;
	private String[] stems = null;
	private List<TypedDependency> typedDependency = null;
	
	private Map<DGNode, DegreeNode> dpDegreeMap = null;

	private boolean[] visited = null;
	private List<DGNode> subPath = null;

	private List<DGNode> nounVertexs = null;
	private Set<String> expandNounSet = null; // expand the noun scope
	private List<DGNode> verbVertexs = null;
	private List<DGNode> prepositionVertexs = null;
	private List<DGNode> loopVertexs = null;
	private boolean isStopedByNoun = false; // it is used for indicate if search stop by the occur the noun word
	private DGNode stopNounNode = null;
	// Dependency Graph size information
	private Integer dgraphSize;
	// private Integer dgraphCol;

	// ervery word in the question is stored in vertexs
	private List<DGNode> vertexs = null;
	private Set<Integer> nounIdxSet = null;

	private Integer rootIndex = 0;
	
	private static QuestionNormalizer qtNormalizer = QuestionNormalizer
			.getInstance();
	// store the dependency relations
	public DGEdge[][] graph = null;
	
	private StanfordNlpTool nlpTool = null;
	
	public DependencyGraph(StanfordNlpTool tool, String orgQuestion) {
		if (orgQuestion == null || orgQuestion.isEmpty())
			return;
		this.orgQuestion = orgQuestion;
		
		initGraph(tool);
		
	}
	
	
	/**
	 *
	 *
	 * @param 
	 * @return void 
	 */
	public void loadExpandNounDict () {
		String filePath = config.getString( this.EXPAND_NOUN_DICT_PATH );
		try {
			List<String> nounWords = FileUtils.readLines( new File(filePath)) ;
			if (this.expandNounSet == null )
				this.expandNounSet = new HashSet<String> ();
			this.expandNounSet.clear();
			this.expandNounSet.addAll(nounWords);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * initConfig, initialize the configuration for dependency graph 
	 *
	 * @param 
	 * @return void 
	 */
	public void initConfig () {
		try {
			config  = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		}catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * normalize the question
	 * 
	 * @param init
	 *            the processed question by using the question normalizer
	 * @return void
	 */
	private void initQuestion() {
		if (orgQuestion == null)
			return;
		processedQuestion = qtNormalizer.dropPunctuationMarks(this.orgQuestion);
		if (processedQuestion != null) {
			processedQuestion = processedQuestion.toLowerCase();
			processedQuestion = processedQuestion.replace("can you tell me", " ");
			processedQuestion = processedQuestion.replace("could you tell me", " ");
			processedQuestion = processedQuestion.replace("can you tell me about", " ");
			processedQuestion = processedQuestion.replace("tell me", " ");
			processedQuestion = processedQuestion.replace("number of", "how many");
			processedQuestion = processedQuestion.replace("high point", "highest point");
			processedQuestion = processedQuestion.replace("high points", "highest point");
			processedQuestion = qtNormalizer.normalize(processedQuestion);
		}
	}

	/**
	 * initialize the dependency graph degree map, call this method must after the call the <em>buildGraph</em>
	 *
	 * @param null
	 * @return void 
	 */
	private void initDegreeMap () {
		if (this.dpDegreeMap == null )
			this.dpDegreeMap = new HashMap <DGNode, DegreeNode> ();

		for (DGNode node : this.getVertexs()) {
			if (this.dpDegreeMap.containsKey(node)) {
				continue;
			}
			else {
				int inDegree = 0;
				int outDegree = 0;
				for (int i = 0; i < this.dgraphSize; ++i) {
					if (this.graph[node.idx][i] != null && this.graph[node.idx][i].isDirected )
						++outDegree;
					if (this.graph[i][node.idx] != null && this.graph[i][node.idx].isDirected)
						++inDegree;
				}
				DegreeNode dn = new DegreeNode(inDegree, outDegree);
				this.dpDegreeMap.put(node, dn);
			}
		}
		
	}
	
	/**
	 * init the whole resource of the graph, used when setOrgQuestion
	 * 
	 * @param null
	 * @return void
	 */
	private void initGraph(StanfordNlpTool tool) {

		initConfig();
		loadExpandNounDict();
		this.nlpTool = tool;
		
		this.isStopedByNoun = false;
		this.rootIndex = 0;
		initQuestion();

		setTokens(tool.token(processedQuestion));
		setTags(tool.defaultTag(processedQuestion));
		boolean defaultTaggerFlag = true;
		for (int i = 0; i < this.tokens.length; ++i ) {
			if (this.tokens[i].equals("border")) {
				this.tags[i] = "VBG";
				defaultTaggerFlag = false;
			}
			if (this.tokens[i].equals("borders") ) {
				this.tags[i] = "VBZ";
				defaultTaggerFlag = false;
			}
			if (this.tokens[i].equals("state")) {
				this.tags[i] = "NN";
				defaultTaggerFlag = false;
			}
			if (this.tokens[i].equals("states")) {
				this.tags[i] = "NNS";
				defaultTaggerFlag = false;
			} 
		}
		setStems(tool.stem(this.tokens, this.tags));

		if (tokens == null || tags == null || stems == null
				|| tokens.length != tags.length
				|| tokens.length != stems.length || tags.length != stems.length)
			logger.error("the tokens,tags, stems error");

		setDgraphSize(tokens.length);
		if (defaultTaggerFlag ) {
		// using default tagger for dependency parsing
			setTypedDependency(tool.typedDependencies(tool.tokenize(processedQuestion)));
		}
		else {
			setTypedDependency(tool.typedDependencies(tool.taggedWord(this.tokens, this.tags)));
		}
		
		//setTypedDependency(tool.typedDependencies(tool.taggedWord(processedQuestion)));
		// malloc the graph with the size of N*N, where N is the number of
		// tokens
		mallocGraph();

		initVertexs();
		
		initNounIdxSet();
		
		buildGraph();

		initDfs();

		initDegreeMap ();
		
//		for (DGNode node : this.vertexs) {
//			logger.info("node: " + node.toString());
//		}
		
		// for (int i = 0; i < this.dgraphRow; ++i ) {
		// for (int j = 0 ; j < this.dgraphCol; ++j ) {
		// DGEdge edge = getEdge (i, j);
		// //if (edge == null )
		// System.out.print(edge == null ? "null" : edge.toString() + "\t");
		// }
		// System.out.println();
		// }

	}

	/**
	 * initialize the data structure "visited" and "path" for dfs method
	 * 
	 * @param null
	 * @return void
	 */
	private void initDfs() {
		visited = new boolean[vertexs.size()];
//		path = new int[vertexs.size()];
		subPath = new ArrayList<DGNode>();
		for (int i = 0; i < vertexs.size(); ++i) {
			visited[i] = false;
//			path[i] = -1;
		}
	}

	/**
	 * init the vertex by using the information of processed question
	 * 
	 * @param null
	 * @return void
	 */
	private void initVertexs() {
		if (tokens == null) {
			logger.error("tokens is null");
			return;
		}
		vertexs = new ArrayList<DGNode>();
		nounVertexs = new ArrayList<DGNode>();
		verbVertexs = new ArrayList<DGNode>();
		prepositionVertexs = new ArrayList<DGNode>();
		loopVertexs = new ArrayList<DGNode>();
		
		for (int i = 0; i < tokens.length; ++i) {
			DGNode node = new DGNode(tokens[i], stems[i], tags[i], i);
			vertexs.add(node);
			if (tags[i].toUpperCase().startsWith(NOUN) || this.expandNounSet.contains(tokens[i].toLowerCase()) 
						|| this.expandNounSet.contains(stems[i].toLowerCase())) {
				nounVertexs.add(node);
			}
			else if (tags[i].toUpperCase().startsWith(VERB)) {
				verbVertexs.add(node);
				loopVertexs.add(node);
			}
			else if (tags[i].toUpperCase().startsWith(IN)) {
				prepositionVertexs.add(node);
				loopVertexs.add(node);
			}
		}
		
		for (DGNode node : verbVertexs) {
			if ( (node.idx + 1) < vertexs.size() && vertexs.get(node.idx + 1).tag.toUpperCase().startsWith(IN)) {
				prepositionVertexs.remove(vertexs.get(node.idx + 1));
				loopVertexs.remove(this.vertexs.get(node.idx + 1));
			}
			if ((node.idx + 2) < vertexs.size() && vertexs.get(node.idx + 1).tag.toUpperCase().startsWith("EX") && vertexs.get(node.idx+2).tag.toUpperCase().startsWith(IN)) {
				loopVertexs.remove(this.vertexs.get(node.idx + 2));
			}
		}
	}
	
	/**
	 * init the noun idx set from the vertexs 
	 *
	 * @param null 
	 * @return a set of noun node ids 
	 */
	private void initNounIdxSet () {
		if (this.nounIdxSet == null )
			this.nounIdxSet = new HashSet<Integer>();
		this.nounIdxSet.clear();
		for (DGNode nd : getNounVertexs()) {
			if (!nounIdxSet.contains(nd.idx)) {
				nounIdxSet.add(nd.idx);
			}
		}
	}

	/**
	 * construct an adjacency matrix for storing the original graph
	 * 
	 * @param null
	 * @return void
	 */
	private void buildGraph() {
		for (TypedDependency td : typedDependency) {
			String reln = td.reln().toString().toLowerCase();
			if (reln.equals("root")) {
				this.rootIndex = td.dep().index() - 1;
				//logger.info("relations : " + td.toString());
				continue;
			}
			//logger.info("relations : " + td.toString());
			Integer govIdx = td.gov().index() - 1;
			Integer depIdx = td.dep().index() - 1;
			DGNode gov = new DGNode(getVertexNode(govIdx));
			DGNode dep = new DGNode(getVertexNode(depIdx));
			DGEdge edge = new DGEdge(reln, true, gov, dep, true);
			setEdge(govIdx, depIdx, edge);
			if (govIdx != depIdx) {
				// set the undirected edge
				setEdge(depIdx, govIdx, new DGEdge(reln, false, gov, dep, true));
			}
		}
	}

	/**
	 * traverse the graph start at the given point v
	 * 
	 * @param v
	 *            , the start point of dfs method
	 * @return void
	 */
	private void dfs(Integer v) {
		if (v < 0 || v >= this.dgraphSize) {
			logger.error("v is out of range!");
			return;
		}

		visited[v] = true; // visited the v vertex
		subPath.add(getVertexNode(v));
		for (int u = 0; u < vertexs.size(); ++u) {
			DGEdge edge = getEdge(v, u);
			if (edge != null && edge.isDirected == true && edge.status == true
					&& visited[u] == false) {
//				path[u] = v;
				dfs(u);
			}
		}
	}

	private void dfsNounNode(Integer v) {
		if (v < 0 || v >= this.dgraphSize) {
			logger.error("v is out of range!");
			return;
		}

		visited[v] = true; // visited the v vertex
		subPath.add(getVertexNode(v));
		for (int u = 0; u < vertexs.size(); ++u) {
			DGEdge edge = getEdge(v, u);
			if (edge != null && edge.isDirected == true && edge.status == true
					&& visited[u] == false) {
				// path[u] = v;
				dfsNounNode(u);
			}
		}
	}

	private void preDfsNounNode(Integer v, Integer bound) {
		if (v < 0 || v >= this.dgraphSize || bound < 0
				|| bound >= this.dgraphSize) {
			logger.error("parameter is out of range!");
			return;
		}

		visited[v] = true;

		if (v != bound)
			subPath.add(getVertexNode(v));
		
		if (getVertexNode(v).tag.toUpperCase().startsWith(this.IN) || getVertexNode(v).tag.toUpperCase().startsWith(this.VERB)){
			return ;
		}
		
		for (int u = bound - 1; u >= 0; --u) {
			DGEdge edge = getEdge(v, u);
			if (edge != null && edge.isDirected == true && edge.status == true
					&& visited[u] == false) {
				preDfsNounNode(u, bound);
			}
		}

	}

	private void postDfsNounNode(Integer v, Integer bound) {
		if (v < 0 || v >= vertexs.size() || bound < 0
				|| bound >= this.vertexs.size()) {
			logger.error("parameter is out of range!");
			return;
		}

		visited[v] = true;
		if (v != bound)
			subPath.add(getVertexNode(v)); 
		
		if (getVertexNode(v).tag.toUpperCase().startsWith(this.IN) || getVertexNode(v).tag.toUpperCase().startsWith(this.VERB)){
			return ;
		}
		for (int u = bound + 1; u < vertexs.size(); ++u) {
			DGEdge edge = getEdge(v, u);
			// edge.isDirected == true &&
			if (edge != null && edge.isDirected == true && edge.status == true
					&& visited[u] == false) {
				// path[u] = v;
				postDfsNounNode(u, bound);
			}
		}
	}

	private void preDfsVerbNode(Integer v, Integer bound) {
		if (v < 0 || v >= this.dgraphSize || bound < 0 || bound >= this.dgraphSize) {
			logger.error("v is out of range");
			return;
		}

		visited[v] = true;
		
		if (this.nounIdxSet.contains(v)) {
			this.isStopedByNoun = true;
			this.stopNounNode = new DGNode (vertexs.get(v));
			return;
		}
		if (v != bound)
			subPath.add(getVertexNode(v));

		
		for (int u = bound - 1; u >= 0 && !this.isStopedByNoun; --u) { // vertexs.size()
			DGEdge edge = getEdge(v, u);
			if (edge != null  && edge.status == true && visited[u] == false && (edge.isDirected == true || 
					edge.reln.toLowerCase().equals("rcmod") || edge.reln.toLowerCase().equals("vmod") )) { // || edge.reln.toLowerCase().equals("cop") 
				preDfsVerbNode(u, bound);
			}
		}
	}

	private void postDfsVerbNode(Integer v, Integer bound){
		if (v < 0 || v >= this.dgraphSize || bound < 0 || bound >= this.dgraphSize) {
			logger.error("v is out of range");
			return;
		}
		
		visited[v] = true;
		//System.out.println("visit : " + getVertexNode(v).toString());

		if (this.nounIdxSet.contains(v)) {
			this.isStopedByNoun = true;
			this.stopNounNode = new DGNode (vertexs.get(v));
			return;
		}
		
		if (v != bound)
			subPath.add(getVertexNode(v));
		
		
		for (int u = bound + 1;  u < vertexs.size() && !this.isStopedByNoun;  ++u) { // vertexs.size()
			DGEdge edge = getEdge(v, u);
			if (edge != null && edge.status == true && visited[u] == false && (edge.isDirected == true || 
					edge.reln.toLowerCase().equals("rcmod") || edge.reln.toLowerCase().equals("vmod")) ) { //|| edge.reln.toLowerCase().equals("cop") , handle "what is the capital city of the us?"
				postDfsVerbNode(u, bound);
			}
		}
	}

	private void dfsVerbNode(Integer v) {
		if (v < 0 || v >= this.dgraphSize) {
			logger.error("v is out of range");
			return;
		}

		visited[v] = true;
		subPath.add(getVertexNode(v));
		//System.out.println("visit : " + getVertexNode(v).toString());

		if (this.nounIdxSet.contains(v)) {
			this.isStopedByNoun = true;
			this.stopNounNode = new DGNode (vertexs.get(v));
			return;
		}
		for (int u = 0; u < vertexs.size(); ++u) { // vertexs.size()
			DGEdge edge = getEdge(v, u);
			if (edge != null && edge.isDirected.equals(true)  && edge.status.equals(true)  && visited[u] == false) {
				dfsVerbNode(u);
			}
		}
	}
	
	
	/**
	 * a method search from the left of node v, which v is a preposition word, undirected search
	 *
	 * @param Integer v, the search word origin
	 * @param Integer bound, search, from where
	 * @return void 
	 */
	private void preDfsPrepositionNode (Integer v, Integer bound) {
		if (v < 0 || v >= this.dgraphSize || bound < 0 || bound >= this.dgraphSize ) {
			logger.error("v is out of range");
			return;
		}
		
		visited[v] = true;
		if (v != bound)
			subPath.add(getVertexNode(v));
		
		if (this.nounIdxSet.contains(v) ) { //|| (this.getVertexNode(v).tag.toUpperCase().equals("PRP") && this.getVertexNode(v).word.toLowerCase().equals("us")) 
			this.isStopedByNoun = true;
			this.stopNounNode = new DGNode (vertexs.get(v));
			return ;
		}
		
//		if (this.isStopedByNoun && (getVertexNode(v).tag.toUpperCase().startsWith(this.IN) || getVertexNode(v).tag.toUpperCase().startsWith(this.VERB))) {
//			logger.info("special return at node : " + getVertexNode(v));
//			return ;
//		}
		
		for (int u = bound - 1; u >= 0 && !this.isStopedByNoun ; --u) {
			DGEdge edge = getEdge(v, u);
			// not used the isDirected condition, in order to search left
			if ( edge != null && edge.status.equals(true) && visited[u] == false) {
				preDfsPrepositionNode(u, bound);
			}
		} 
	}
	
	
	/**
	 * search from the v, begin at the bound 
	 *
	 * @param Integer v, the search word origin
	 * @param Integer bound, search, from where
	 * @return void 
	 */
	private void postDfsPrepositionNode (Integer v, Integer bound) {
		if (v < 0 || v >= this.dgraphSize || bound < 0 || bound >= this.dgraphSize) {
			logger.error("v is out of range");
			return;
		}
		
		visited[v] = true;
		if (v != bound)
			subPath.add(getVertexNode(v));
		
		if (this.nounIdxSet.contains(v)  ) { //|| (this.getVertexNode(v).tag.toUpperCase().equals("PRP") && this.getVertexNode(v).word.toLowerCase().equals("us"))
			this.isStopedByNoun = true;
			this.stopNounNode = new DGNode (vertexs.get(v));
			return ;
		}
		
//		if (this.isStopedByNoun && (getVertexNode(v).tag.toUpperCase().startsWith(this.IN) || getVertexNode(v).tag.toUpperCase().startsWith(this.VERB))) {
//			logger.info("special return at node : " + getVertexNode(v));
//			return ;
//		}
		
		for (int u = bound + 1; u < this.dgraphSize && !this.isStopedByNoun; ++u) {
			DGEdge edge = getEdge (v, u) ;
			if (edge != null && edge.status.equals(true) && visited[u] == false){
				postDfsPrepositionNode (u, bound);
			}
		}
	}

	
	private void dfsPrepositionNode (Integer v) {
		if (v < 0 || v >= this.dgraphSize ) {
			logger.error("v is out of range");
			return;
		}
		
		visited[v] = true;
		subPath.add(getVertexNode(v));
		if ( this.nounIdxSet.contains(v)) {
			return ;
		}
		for (int u = 0; u < this.dgraphSize; ++u) {
			DGEdge edge = getEdge (v, u);
			if (edge != null && edge.status.equals(true) && visited[u] == false) {
				dfsPrepositionNode (u);
			}
 		}
	}
	
//	public List<DGNode> search (DGNode node, GraphSearchType gsType, boolean isRemovedThe) {
//		
//	} 
	
	/**
	 * search the dependency graph by the graph search type
	 *
	 * @param  node, the begin search node
	 * @param  gsType , the search type, the type can see <em>GraphSearchType class</em> 
	 * @return List<DGNode> , return the graph search type path
	 */
	public List<DGNode> search(DGNode node, GraphSearchType gsType) {
		
		Integer bound = node.idx;
		//handle like the "how many states does tennessee border ?"
		if (gsType == GraphSearchType.PRE_VERB_DFS || gsType == GraphSearchType.POST_VERB_DFS ) {
			DFS (node.idx , bound, GraphSearchType.VERB_DFS);
			for (DGNode nd : this.subPath ) {
				DGEdge edge = this.getEdge(node.idx, nd.idx);
				if(edge == null ) // self to self , eg: graph[2][2] should equals null
					continue;
				// handle like the how many states does tennessee border ?
				if (edge.reln.toLowerCase().equals("aux") && node.idx > nd.idx && nd.tag.toUpperCase().startsWith(this.VERB))  {
					bound = nd.idx;
				}
			} 
		}
		
//		logger.info("search type : " + gsType.toString() + " idx : " + node.idx + "  bound : " + bound);
		
		DFS(node.idx, bound, gsType);
		return new ArrayList<DGNode>(getSubPath());
	}
	
	/**
	 * a dfs traversal method
	 *
	 * @param v, the begin search index
	 * @param gsType, the search type , the type can see <em>GraphSearchType class</em>
	 * @return void 
	 */
	public void DFS (Integer v , Integer bound,  GraphSearchType gsType) {
		if (this.subPath == null )
			this.subPath = new ArrayList<DGNode>();
		this.subPath.clear();
		for (int i = 0; i < visited.length; ++i ) {
			visited[i] = false;
			//path[i] = -1;
		}
		
		this.isStopedByNoun = false;
		this.stopNounNode = null;
		
		if (gsType == GraphSearchType.PRE_NOUN_DFS) {
			preDfsNounNode (v,bound);
		}
		else if (gsType == GraphSearchType.POST_NOUN_DFS ) {
			postDfsNounNode(v, bound);
		}
		else if (gsType == GraphSearchType.NOUN_DFS) {
			dfsNounNode(v);
		}
		else if (gsType == GraphSearchType.PRE_VERB_DFS) {
			preDfsVerbNode(v, bound);
		}
		else if (gsType == GraphSearchType.POST_VERB_DFS) {
			postDfsVerbNode (v, bound);
		}
		else if (gsType == GraphSearchType.VERB_DFS){
			dfsVerbNode(v);
		}
		else if (gsType == GraphSearchType.PRE_IN_DFS){
			preDfsPrepositionNode (v, bound);
		}
		else if ( gsType == GraphSearchType.POST_IN_DFS){
			postDfsPrepositionNode(v, bound);
		}
		else if ( gsType == GraphSearchType.IN_DFS) {
			dfsPrepositionNode(v);
		}
		else {
			dfs(v);
		}
	}
	
	
	/**
	 * 深度优先查找两点之间的路径
	 *
	 * @param 
	 * @return void 
	 */
	private void dfsShortestPath (int src, int des, List<Integer> path ) {
		if (src ==  des) {
			return ;
		}
		this.visited[src] = true;
		for (int u = 0; u < this.dgraphSize; ++u) {
			if ( src != u && this.graph[src][u] != null && this.graph[src][u].status && this.visited[u] == false) {
				path.set(u, src);
				dfsShortestPath (u, des, path);
			}
		}
	} 
	
	/**
	 * find the shortest path between src and des
	 *
	 * @param int src, the start point
	 * @param int des, the destination point
	 * @return List<Integer> the shortest path between src and des
	 */
	public List<Integer> searchPath (int src, int des ){
		List<Integer> path = new ArrayList<Integer> ();
		for (int i = 0; i < this.dgraphSize; ++i ) {
			this.visited[i] =false;
			path.add(0);
		}
		dfsShortestPath (src, des, path);
		List<Integer> resPath = new ArrayList<Integer> ();
		int pre = des;
		resPath.add(des);
		while (pre != src) {
			pre = path.get(pre);
			resPath.add(pre);
		}
		Collections.reverse(resPath);
		return resPath;
	}
	
	/**
	 * 判断在 src 到 des 的路径上是否存在动词，用来区分两个实体是否可以合并，如果存在，则不能合并
	 *
	 * @param int src, the search start point
	 * @param int des, the search end point
	 * @return boolean 
	 */
	public boolean isContainVerbInPath (int src, int des) {
		List<Integer> path = searchPath (src, des);
		for (Integer i : path ) {
			if (i != src && i != des && this.getVertexNode(i).tag.toUpperCase().startsWith(this.VERB)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 判断一个路径上是否包含某个tag， 不包含起点和终点
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean isContainTagInPath (int src, int des, String tag ) {
		if (src < 0 || src >= this.dgraphSize || des < 0 || des >= this.dgraphSize || tag == null )
			return false;
		List<Integer> path = searchPath (src, des);
		for (Integer p : path ) {
			if (p != src && p != des &&  this.getVertexNode(p).tag.toUpperCase().equals(tag.toUpperCase()))
				return true;
		}
		return false;
	}
	
	/**
	 * 判断一个路径从src 到 des 的路径上是否包含一个特定的词
	 * 
	 *
	 * @param int src, the start point 
	 * @param int dex, the ent point
	 * @param word, the search word
	 * @param tag , the word tag
	 * @return boolean 
	 */
	public boolean isContainWordInPath (int src, int des, String word, String tag) {
		if (src < 0 || src >= this.dgraphSize || des < 0 || des >= this.dgraphSize || word == null ||tag == null)
			return false;
		List<Integer> path = searchPath (src, des);
		for (Integer p : path ) {
			if (p != src && p != des && this.getVertexNode(p).tag.toUpperCase().equals(tag.toUpperCase()) && this.getVertexNode(p).word.toLowerCase().equals(word.toLowerCase()))
				return true;
		}
		return false;
	}

	public boolean isContainWordInPath (int src, int des , DGNode node) {
		if (node == null ) return false;
		return isContainWordInPath (src, des, node.word, node.tag); 
	}
	
	/**
	 * get the in degree value the specific node
	 *
	 * @param DGNode, node, the dependency graph node to get in degree 
	 * @return int ,the in degree of the given node
	 */
	public int getDGNodeInDegree (DGNode node) {
		if (node  == null )
			return -1;
		if (this.dpDegreeMap.containsKey(node)) {
			return this.dpDegreeMap.get(node).inDegree;
		}
		return 0;
	}
	
	/**
	 * 获得和一个词链接的所有的词，不考虑方向
	 *
	 * @param 
	 * @return List<DGNode> 
	 */
	public List<DGNode> getLinkedWords ( int pos ) {
		if ( pos < 0 || pos >= this.dgraphSize )
			return null;
		
		List<DGNode> linkWords = new ArrayList<DGNode>();
		for (int i = 0; i < this.dgraphSize; ++i ) {
			if (pos != i && this.graph[pos][i] != null && this.graph[pos][i].status ) {
				linkWords.add(getVertexNode (i));
			}
		}
		return linkWords;
	}
	
	
	
	
	/**
	 * 获得给定pos位置的verb之后，计算其链接的sub和obj
	 *
	 * @param 
	 * @return List<DGNode> 
	 */
	public List<DGNode> getSubObjNode (int pos) {
		if (pos < 0 || pos >= this.dgraphSize)
			return null;
		
		//    获得和这个动词链接的词
		List<DGNode> linkedWords = this.getLinkedWords(pos);
		if (linkedWords== null || linkedWords.isEmpty())
			return null;
		DGNode nsubjNode = null;
		DGNode objNode = null;
		boolean dobjFlag = false;
		for (DGNode node : linkedWords) {
			if (node.tag.toUpperCase().startsWith(this.NOUN)) {
				//nsubj
				if (this.graph[pos][node.idx] != null && this.graph[pos][node.idx].isDirected && this.graph[pos][node.idx].reln.toLowerCase().equals("nsubj")) {
					nsubjNode = this.getVertexNode(node.idx);
				}
				// rcmod
				else if (this.graph[pos][node.idx] != null && this.graph[node.idx][pos].isDirected && this.graph[node.idx][pos].reln.toLowerCase().equals("rcmod")) {
					objNode = node;
				}
				// handle : which states have cities named austin ?
				else if (this.graph[pos][node.idx] != null && this.graph[pos][node.idx].isDirected && this.graph[pos][node.idx].reln.toLowerCase().equals("dobj")) {
					objNode = node;
					dobjFlag = true;
				}
				// dep : handle how many states does the mississippi river run through ?
				else if (this.graph[pos][node.idx] != null && this.graph[pos][node.idx].isDirected && this.graph[pos][node.idx].reln.toLowerCase().equals("dep")) {
					if (!dobjFlag)
						objNode = node;
				}
			}
			else if (node.tag.toUpperCase ().startsWith(this.IN)) {
				for (int i = 0; i < this.dgraphSize; ++i ) {
					if (i != node.idx && this.graph[node.idx][i] != null && this.graph[node.idx][i].isDirected && this.graph[node.idx][i].reln.toLowerCase().equals("pobj") 
							&& this.getVertexNode(i).tag.toUpperCase().startsWith(this.NOUN)){
						
						objNode = this.getVertexNode(i);
						break;
					}
				}
 			}
			// 及时停止循环
			//if (nsubjNode != null && objNode != null )
				//break;
		}
		if (nsubjNode != null && objNode != null ) {
			List<DGNode> subobjNodes = new ArrayList<DGNode>();
//			while (nsubjNode.prevIndex != -1) {
//				nsubjNode = this.getVertexNode(nsubjNode.prevIndex);
//			}
//			while (objNode.prevIndex)
			subobjNodes.add(nsubjNode);
			subobjNodes.add(objNode);
			return subobjNodes;
		}
		return null;
	}
	
	/**
	 * 判断pos为位置的动词是否在它链接的两个名词中间，
	 * 如果不是，如果动词在两个名词前面不需要处理，
	 * 如果在两个名词后面，则需要处理
	 * VB a b，返回false，
	 * a VB b，返回false
	 * a b VB，返回true 
	 * @param pos, verpos,
	 * @return boolean 
	 */
	public boolean isVerbPositionIllegal (int pos ) {
		return  this.isVerbPositionIllegal(pos, this.getSubObjNode(pos));
	}
	
	public boolean isVerbPositionIllegal (int pos, List<DGNode> subobjNodes) {
		if (pos < 0 || pos >= this.dgraphSize || subobjNodes == null)
			return false;
		if (subobjNodes != null && subobjNodes.size() == 2 && subobjNodes.get(0) != null && subobjNodes.get(1) != null && subobjNodes.get(0).idx < pos && subobjNodes.get(1).idx < pos)
			return true;
		return false;
	}
	
	/**
	 * 判断一个tag是不是最高级修饰符
	 *
	 * @param String modifierTag
	 * @return boolean 
	 */
	public boolean isSuperModifier (String modifierTag ) {
		if (modifierTag == null || modifierTag.isEmpty() )
			return false;
		if (modifierTag.toUpperCase().equals(this.JJS) || modifierTag.toUpperCase().equals(this.RBS))
			return true;
		return false;
	}
	
	/**
	 *  判断一个DGNode 是不是最高级修饰符
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean isSuperModifier (DGNode node) {
		if (node == null )
			return false;
		return isSuperModifier (node.tag);
	}
	
	/**
	 * 判断一个词和词性组合成的对是不是否定修饰符
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean isNegativeModifier (String word, String tag) {
		if (word == null || tag == null )
			return false;
		if (word.toLowerCase().equals("not") && tag.toUpperCase().equals (this.RB)) {
			return true;
		}
		
		if (word.toLowerCase().equals("no") && tag.toUpperCase().equals("DT")) {
			return true;
		}
		return false;
	}
	

	/**
	 *  判断一个给定的DGNode是不是一个否定修饰符
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean isNegativeModifier (DGNode node ) {
		if (node == null )
			return false;
		return isNegativeModifier (node.word, node.tag);
	}
	
	
	public List<DGNode> getModifiers () {
		List<DGNode> modifiers = new ArrayList<DGNode>();
		for (DGNode node : this.getVertexs()) {
			if (isSuperModifier (node) || isNegativeModifier (node)) {
				modifiers.add (new DGNode (node));
			}
		}
		return (modifiers.isEmpty() ? null : modifiers);
	}
	
	
	/**
	 * get the out degree value the specific node
	 *
	 * @param DGNode, node, the dependency graph node to get out degree
	 * @return int, the out degre of the given node
	 */
	public int getDGNodeOutDegree (DGNode node ) {
		if (node  == null )
			return -1;
		if (this.dpDegreeMap.containsKey(node)) {
			return this.dpDegreeMap.get(node).outDegree;
		}
		return 0;
	}
	
	public List<DGNode> getSubPath() {
		return this.subPath;
	}

	public List<DGNode> getNounVertexs() {
		return this.nounVertexs;
	}

	public List<DGNode> getVerbVertexs() {
		return this.verbVertexs;
	}

	/**
	 * get the vertex node by the given index
	 * 
	 * @param index
	 *            , vertex list index
	 * @return DGNode
	 */
	public DGNode getVertexNode(Integer index) {
		if (index < 0 || index >= vertexs.size()) {
			logger.error("Illeagal parameter of index : " + index.toString());
			return null;
		}
		return vertexs.get(index);
	}

	/**
	 * judge the graph dimension is legal
	 * 
	 * @param row
	 *            , col, the dimension of the specified position in the graph
	 * @return boolean , is the graph dimension is legal
	 */
	private boolean isGraphDimLegal(Integer row, Integer col) {
		if (row < 0 || col < 0 || row >= this.dgraphSize
				|| col >= this.dgraphSize) {
			return false;
		}
		return true;
	}

	/**
	 * get the [row, col] position element in the graph
	 * 
	 * @param row
	 *            , the graph row
	 * @param col
	 *            , the graph col
	 * @return DGEdge
	 */
	public DGEdge getEdge(Integer row, Integer col) {
		if (!isGraphDimLegal(row, col)) {
			throw new IllegalArgumentException("Illegal argument : "
					+ row.toString() + ", " + col.toString());
		}
		return graph[row][col];
	}

	/**
	 * 
	 * 
	 * @param row
	 *            , the graph row
	 * @param col
	 *            , the graph col
	 * @param dgEdge
	 *            , the element which is signing for the graph unit
	 * @return void
	 */
	public void setEdge(Integer row, Integer col, DGEdge dgEdge) {
		if (!isGraphDimLegal(row, col)) {
			throw new IllegalArgumentException("Illegal argument : "
					+ row.toString() + ", " + col.toString());
		}
		graph[row][col] = dgEdge;
	}

	/**
	 * malloc space for the graph
	 * 
	 * @param row
	 *            , the row size of the graph
	 * @param col
	 *            , the col size of the graph
	 * @return void
	 */
	private void mallocGraph() {
		if (graph == null) {
			graph = new DGEdge[this.dgraphSize][this.dgraphSize];
		}
	}

	/**
	 * set the orgQuestion
	 * 
	 * @param
	 * @return void
	 */
	public void setOrgQuestion(StanfordNlpTool tool, String orgQuestion) {
		if (orgQuestion == null || orgQuestion.isEmpty())
			return;
		this.orgQuestion = orgQuestion;

		initGraph(tool);
	}

	/**
	 * 
	 * 
	 * @param
	 * @return String
	 */
	public String getProcessedQuestion() {
		return processedQuestion;
	}

	/**
	 * 
	 * 
	 * @param
	 * @return List<DGNode>
	 */
	public List<DGNode> getVertexs() {
		return vertexs;
	}

	/**
	 * 
	 * 
	 * @param
	 * @return void
	 */
	public void setVertexs(List<DGNode> vertexs) {
		this.vertexs = vertexs;
	}

	/**
	 * 
	 * 
	 * @param
	 * @return DGEdge[][]
	 */
	public DGEdge[][] getGraph() {
		return graph;
	}

	/**
	 * set the graph
	 * 
	 * @param DGEdge
	 *            [][], graph
	 * @return void
	 */
	public void setGraph(DGEdge[][] graph) {
		this.graph = graph;
	}

	/**
	 * get the org question
	 * 
	 * @param null
	 * @return String
	 */
	public String getOrgQuestion() {
		return orgQuestion;
	}

	/**
	 * get the tokens of the processed question
	 * 
	 * @param null
	 * @return String[], the tokens
	 */
	public String[] getTokens() {
		return tokens;
	}

	/**
	 * set the tokens
	 * 
	 * @param the
	 *            specified tokens
	 * @return void
	 */
	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	/**
	 * get tags of the processed qestion
	 * 
	 * @param null
	 * @return String[] , the tags of the question
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * set the tags
	 * 
	 * @param the
	 *            specified question
	 * @return void
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
	}

	/**
	 * get stem of the given processed question
	 * 
	 * @param null
	 * @return String[] the stems
	 */
	public String[] getStems() {
		return stems;
	}

	/**
	 * set the stems value
	 * 
	 * @param the
	 *            given stem
	 * @return void
	 */
	public void setStems(String[] stems) {
		this.stems = stems;
	}

	public List<TypedDependency> getTypedDependency() {
		return typedDependency;
	}

	public void setTypedDependency(List<TypedDependency> typedDependency) {
		this.typedDependency = typedDependency;
	}

	public Integer getDgraphSize() {
		return dgraphSize;
	}

	public void setDgraphSize(Integer dgraphSize) {
		this.dgraphSize = dgraphSize;
	}

	public boolean isStopedByNoun() {
		return isStopedByNoun;
	}

	public void setStopedByNoun(boolean isStopedByNoun) {
		this.isStopedByNoun = isStopedByNoun;
	}

	public Integer getRootIndex() {
		return rootIndex;
	}

	public void setRootIndex(Integer rootIndex) {
		this.rootIndex = rootIndex;
	}

	public List<DGNode> getPrepositionVertexs() {
		return prepositionVertexs;
	}

	public void setPrepositionVertexs(List<DGNode> prepositionVertexs) {
		this.prepositionVertexs = prepositionVertexs;
	}

	public List<DGNode> getLoopVertexs() {
		return loopVertexs;
	}

	public void setLoopVertexs(List<DGNode> loopVertexs) {
		this.loopVertexs = loopVertexs;
	}

	public DGNode getStopNounNode() {
		return stopNounNode;
	}

	public void setStopNounNode(DGNode stopNounNode) {
		this.stopNounNode = stopNounNode;
	}
}

class DegreeNode {
	public int inDegree; // in degree 
	public int outDegree; // out degree
	
	public DegreeNode () {
		this.inDegree = 0;
		this.outDegree = 0;
	}
	
	public DegreeNode (int inDegree, int outDegree ) {
		this.inDegree = inDegree;
		this.outDegree = outDegree;
	}
}
