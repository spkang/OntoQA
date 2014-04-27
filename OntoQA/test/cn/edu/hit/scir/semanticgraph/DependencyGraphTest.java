/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.semanticgraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.scir.dependency.GraphSearchType;
import cn.edu.hit.scir.dependency.StanfordEnglishNlpTool;
import cn.edu.hit.scir.dependency.StanfordNlpTool;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月28日 
 */
public class DependencyGraphTest {
	private static Logger logger = Logger.getLogger(DependencyGraphTest.class);
	
	private DependencyGraph dgraph = null;
	
	@Before
	public void setUp () throws Exception {
		logger.info("setup dependency graph test");
	}
	
	@After
	public void tearDown () throws Exception {
		logger.info("tear down dependency graph test");
	}
	
	
	
	@Test
	public void test() {
		logger.info("@run test");
		//initGraph ("which state border the state that the mississippi flows over ?");
		//initGraph ("how many rivers are in the state that has the most rivers ?");
		//initGraph ("how many rivers are in the state ?");
		//initGraph("count the states which have elevations lower than what alabama has ?");
		//initGraph ("how high are the highest points of all the states ?");
		//initGraph ("how many states border colorado and border new mexico ?");
		initGraph ("through which states does the mississippi flow ?");
		
		
		List<Integer> path = this.dgraph.searchPath(2, 5);
		String out = "";
		for (Integer i : path) {
			out += this.dgraph.getVertexs().get(i).word + " -> "; 
		}
		out = out.substring(0, out.length() - 3).trim();
		System.out.println ("path : " + out);
		logger.info("is exists verbs : " + this.dgraph.isContainVerbInPath(2, 5));
		//testVertexs();
		//testDfs();
	}
	
	public void testVertexs () {
		logger.info("@testVertexs");
		List<DGNode> vertexs = dgraph.getVertexs();
		for (DGNode node : vertexs) {
			logger.info(node.toString());
		}
	}
	
	private void initGraph (String sentence) {
		StanfordNlpTool tool = StanfordEnglishNlpTool.getInstance();
		dgraph = new DependencyGraph(tool, sentence);
	}
	
	public String getPathString (List<DGNode> subPath) {
		//logger.info("***************************************");
		//logger.info("Path begin");
		List<String> pt = new ArrayList<String>();
		for (DGNode nd : subPath) {
			pt.add(nd.word + "-" + nd.idx);
			//System.out.print(nd.word + "-" + nd.idx + " --> ");
		}
		return StringUtils.join(pt, " -> ");
		//System.out.println();
		//logger.info("Path end");
	}
	
	private void search(int v, GraphSearchType gst) {
		dgraph.DFS(v, v, gst);
		List<DGNode> subPath = dgraph.getSubPath();
		//logger.info("Node: " + node.toString());
		//showPath(subPath);
		logger.info("Path : " + getPathString(subPath));
	}
	
	public void testDfs () {
		logger.info("noun node : ");
		List<DGNode> nounVertexs = dgraph.getNounVertexs();
		for (DGNode node : nounVertexs) {
			logger.info("NOUN_DFS : ");
			search(node.idx, GraphSearchType.NOUN_DFS);
			
			logger.info("PRE_NOUN_DFS : ");
			search(node.idx, GraphSearchType.PRE_NOUN_DFS);
			
			logger.info("POST_NOUN_DFS : ");
			search(node.idx, GraphSearchType.POST_NOUN_DFS);
		}
		
		logger.info("verb node : ");
		List<DGNode> verbVertexs = dgraph.getVerbVertexs();
		for (DGNode node : verbVertexs) {
			logger.info("VERB_DFS : ");
			logger.info("verb node : " + node.toString());
			search(node.idx, GraphSearchType.VERB_DFS);
			
			logger.info("PRE_VERB_DFS : ");
			search(node.idx, GraphSearchType.PRE_VERB_DFS);
			
			logger.info("POST_VERB_DFS : ");
			search(node.idx, GraphSearchType.POST_VERB_DFS);
		}
		
		
		logger.info("preposition node : ");
		//List<DGNode> inNode
		
	}

}
