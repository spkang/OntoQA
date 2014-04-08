/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;

/**
 * A multigraph in which both the loops and multiple edges are permitted.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-5
 */

public class LoopMultiGraph<V, E> 
	extends AbstractBaseGraph<V, E> 
	implements UndirectedGraph<V, E> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new LoopMultiGraph.
	 * 
	 * @param edgeClass
	 *            class on which to base factory for edges
	 */
	public LoopMultiGraph(Class<? extends E> edgeClass) {
		this(new ClassBasedEdgeFactory<V, E>(edgeClass));
	}

	/**
	 * Creates a new LoopMultiGraph with the specified edge factory.
	 * 
	 * @param ef
	 *            the edge factory of the new graph.
	 */
	public LoopMultiGraph(EdgeFactory<V, E> ef) {
		super(ef, true, true);
	}

}
