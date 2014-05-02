/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.ir.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.scir.EntityMatcher.EntityMatcherEngine;
import cn.edu.hit.scir.semanticgraph.SemanticGraph;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月28日 
 */
public class QueryGraphTest {
	public EntityMatcherEngine emEngine = EntityMatcherEngine.getInstance();
	public QueryGraph queryGraph;
	public SemanticGraph smtcGraph = null;
	private static Logger logger = Logger.getLogger(QueryGraphTest.class);
	
	@Before
	public void setUp () throws Exception {
		logger.info("set up!");
	}
	
	@After
	public void tearDown ()  throws Exception {
		logger.info("tear down !");
	} 
	
	@Test
	public void testGenerateQueryGraph () {
		generate ("what state borders texas and have a major river ?");

		generate ("how many rivers run through the mississippi state ?");
		generate ("how many rivers flow through the new york state ?");
		generate ("how big is the city of new york ?");
		generate ("what is the population of new york city ?");
		generate  ("how big is north dakota ?");
		generate ("what is the highest point in the state of oregon ?");
		generate ("which states does the mississippi river run through ?");
		generate ("what cities are in states that border texas ?");
		generate  ("what are the high points of states surrounding mississippi ?");
		generate ("how many inhabitants does montgomery have ?");
		generate ("what is the river that cross over ohio ?");
		generate ("how many citizens in boulder ?");
		generate ("how many major cities are in texas ?");
		generate ("how many major rivers cross ohio ?");
		generate ("what is the adjacent state of california ?");
		generate ("of the states washed by the mississippi river which has the lowest point ?");
		generate  ("how many states border colorado and border new mexico ?");
		generate  ("name the state washed by mississippi and has a major city named texas.");
	}
	
	public void generate (String sentence ) {
		smtcGraph = new SemanticGraph (sentence );
		emEngine.runEntityMatcherEngine(smtcGraph);
		
		List<List<MatchedEntity>> matchedEntities = emEngine.getMatchedQuery();
		int begin = -1;
		int end = 0;
		Map<Integer, List<MatchedEntity>> matchedEntityMap = new HashMap<Integer, List<MatchedEntity>>();
		List<Integer> subobjIndex = new ArrayList<Integer>();
		for (int i = 0; i < this.smtcGraph.getDependencyGraph().getDgraphSize(); ++i ) {
			
			if ( matchedEntities.get(i) == null || matchedEntities.get(i).isEmpty() ||  this.smtcGraph.getDependencyGraph().getVertexNode(i).prevIndex != -1){
				continue;
			}
			if (matchedEntities.get(i) != null && !matchedEntities.get(i).isEmpty() && (matchedEntities.get(i).get(0).isInstance() || matchedEntities.get(i).get(0).isClass()) && this.smtcGraph.getDependencyGraph().getVertexNode(i).prevIndex == -1 ) {
				if ( begin == -1 )
					begin = i;
				end = i;
				subobjIndex.add(i);
			}
			logger.info("Node : " + this.smtcGraph.getDependencyGraph().getVertexNode(i).toString() + "\t mach : " + StringUtils.join(matchedEntities.get(i), ","));
			matchedEntityMap.put(i,  matchedEntities.get(i));
		}
		for (int i = 0; i < subobjIndex.size(); ++i ) {
			for (int j = i + 1; j < subobjIndex.size(); ++j ) {
				List<Integer> path = this.smtcGraph.getDependencyGraph().searchPath(subobjIndex.get(i), subobjIndex.get(j));
				logger.info("(" + subobjIndex.get(i) + " -> " + subobjIndex.get(j) + ") path : ");
				for (Integer k : path) {
					if (matchedEntityMap.containsKey(k)) {
						logger.info("k = " + k  + "\tme : " + StringUtils.join(matchedEntityMap.get(k), ", "));
					}
					else {
						logger.info("k = " + k + "\tnode : " + this.smtcGraph.getDependencyGraph().getVertexNode(k));
					}
				}
				
			}
		}
		
		
		
	}
	
	
}
