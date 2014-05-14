/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *   中文的依存图结构，可以用来存储ltp的依存图和Stanford的依存图
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月10日 
 */
public class ChineseBasedGraph {
	private List<ChineseWord> vertexs  = null; // the vertex of the query
	private List<List<GraphNode>> graph = null; // the dependency graph of the query
	private int graphSize = -1; 
	
	private boolean [] visited;
	
	
	public ChineseBasedGraph (List<ChineseWord> vertex, List<GraphNode> relations ) {
		this.vertexs = vertex;
		if (this.vertexs != null )
			this.graphSize = this.vertexs.size();
		initGraph (relations);
	}
	
	
	/**
	 * 申请graph使用的空间
	 *
	 * @param 
	 * @return void 
	 */
	private void mallocGraph () {
		if (graphSize <= 0) 
			return ;
		this.graph = new ArrayList<List<GraphNode>> ();
		this.visited = new boolean [this.graphSize + 1];
		for (int i = 0; i < this.graphSize; ++i ) {
			this.graph.add(new ArrayList<GraphNode>());
			for (int j = 0; j < this.graphSize; ++j ) {
				this.graph.get(i).add(new GraphNode ());
			}
		}
	}
	
	/**
	 *  根据输入的relations对图结构进行初始化。
	 *
	 * @param relations, 链接的点的结构，主要根据gov和dep node进行链接
	 * @return void 
	 */
	private void initGraph  (List<GraphNode> relations ) { 
		if (relations == null || relations.isEmpty() )
			return ;
		
		// malloc the memery of the Chinese based graph 
		mallocGraph ();
		
		for (GraphNode relnNode : relations ) {
			setLinkStatus (relnNode.govNode.idx, relnNode.depNode.idx, relnNode, false);
		}
	}
	
	/**
	 * 设置图节点之间的链接关系和相应的链接内容
	 *
	 * @param row, node节点所在的行
	 * @param col, node节点所在的列
	 * @param node, 输入的节点
	 * @param isDirectedOnly , true : 只设定单项链接， 有向图; false : 双向设定，无向图 
	 * @return boolean , 是否设定成功 
	 */
	public boolean setLinkStatus (int row, int col, GraphNode node, boolean isDirectedOnly ) {
		if ( ! isGraphIndexLegal(row, col))
			return false;
		if (isDirectedOnly )
			this.graph.get(row).get(col).setGraphNode(node);
		else  {
			this.graph.get(row).get(col).setGraphNode(node);
			this.graph.get(col).get(row).setGraphNode(node);
			this.graph.get(col).get(row).isDirected = false; // 这个方向应该不是直接链接的
		}
		return true;
	} 
	
	
	/**
	 * 判断输入的row 和 col 是否是合法的
	 *
	 * @param row, graph index 
	 * @param col, graph index
	 * @return boolean 
	 */
	private boolean isGraphIndexLegal (int row, int col) {
		if ( ! isIndexLegal (row) || ! isIndexLegal (col) )
			return false;
		return true;
	}
	
	/**
	 * 判断index是否合法
	 *
	 * @param index, the single index 
	 * @return boolean 
	 */
	private boolean isIndexLegal (int index ) {
		if (index < 0 || index >= this.graphSize )
			return false;
		return true;
	}
	
	/**
	 * 获得和nodeIndex位置的节点链接的节点
	 *
	 * @param nodeIndex, 要求取的节点
	 * @param directedOnly, 是否只求取直接连接的节点
	 * @return List<ChineseWord> 
	 */
	public List<ChineseWord> getLinkWords (int nodeIndex , boolean directedOnly ) {
		if (! isIndexLegal (nodeIndex))
			return null;
		List<ChineseWord> linkWords = new ArrayList<ChineseWord> ();
		for (int i = 0; i < this.graphSize; ++i ) {
			if (i !=  nodeIndex && ((getGraphNode(nodeIndex, i).isDirected && directedOnly ) || (getGraphNode(nodeIndex, i).status && !directedOnly)) )
				linkWords.add(getVertex (i));
		}
		return linkWords;
	} 
	
	
	/**
	 * 默认是求无向图的链接节点
	 * 
	 * @param 
	 * @return List<ChineseWord> 
	 */
	public List<ChineseWord> getLinkWords (int nodeIndex) {
		return getLinkWords (nodeIndex, false);
	}
	
	
	/**
	 * 获得row, col位置的图节点
	 * 
	 * @param row
	 * @param col
	 * @return GraphNode 
	 */
	public GraphNode getGraphNode (int row, int col ) {
		if (! isGraphIndexLegal (row, col ))
			return null;
		return this.graph.get(row).get(col);
					
	}
	
	/**
	 * 获得给定位置的图定点
	 *
	 * @param index
	 * @return ChineseWord 
	 */
	public ChineseWord getVertex(int index ) {
		if (!isIndexLegal (index))
			return null;
		return this.vertexs.get(index);
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
		for (int u = 0; u < this.graphSize; ++u) {
			if ( src != u && this.getGraphNode(src, u) != null && this.getGraphNode(src, u).status && this.visited[u] == false) {
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
		for (int i = 0; i < this.graphSize; ++i ) {
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
	 * 返回src到des的路径上的关系列表
	 *
	 * @param src, the start point 
	 * @param des, the end point
	 * @return List<String> 
	 */
	public List<String> searchRelationPath ( int src, int des ) {
		List<String> relnPath = new ArrayList<String>();
		List<Integer> path = searchPath (src, des);
		if (path == null || path.isEmpty() || path.size() < 2)
			return null;
		int index = 0;
		while (index + 1 < path.size()) {
			relnPath.add(this.getGraphNode(path.get(index), path.get(index + 1)).reln);
			++index;
		}
		return relnPath;
	}

	
	/**
	 * 返回路径上的关系
	 *
	 * @param path, 路径
	 * @return List<String> 
	 */
	public List<String> searchRelationPath (List<Integer> path) {
		List<String> relnPath = new ArrayList<String>();
		if (path == null || path.isEmpty() || path.size() < 2)
			return null;
		int index = 0;
		while (index + 1 < path.size()) {
			relnPath.add(this.getGraphNode(path.get(index), path.get(index + 1)).reln);
			++index;
		}
		return relnPath;
	}
	
	public List<ChineseWord> getVertexs() {
		return vertexs;
	}


	public void setVertexs(List<ChineseWord> vertexs) {
		this.vertexs = vertexs;
	}


	public int getGraphSize() {
		return graphSize;
	}

	@Override
	public String toString() {
		return "ChineseBasedGraph [vertexs=" + vertexs + ", graph=" + graph
				+ ", graphSize=" + graphSize + "]";
	}
	
}