/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;
import cn.edu.hit.scir.dependency.StanfordEnglishNlpTool;
import cn.edu.hit.scir.dependency.StanfordNlpTool;
import edu.stanford.nlp.semgraph.SemanticGraph;


/**
 * a wraper for the stanford semantic graph
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月28日 
 */
public class StanfordSemanticGraph {
	private SemanticGraph stanfordSemanticGraph = null;
	private DependencyGraph dependencyGraph = null;
	
	public StanfordSemanticGraph (String sentence ) {
		this.initResource(sentence);
	}
	
	/**
	 * initialize the sentence
	 *
	 * @param the input sentence
	 * @return void 
	 */
	public void initResource (String sentence ) {
		StanfordNlpTool tool = StanfordEnglishNlpTool.getInstance();
		this.dependencyGraph = new DependencyGraph(tool, sentence);
		this.stanfordSemanticGraph = new SemanticGraph (this.dependencyGraph.getTypedDependency());
	}
	
	
	
}
