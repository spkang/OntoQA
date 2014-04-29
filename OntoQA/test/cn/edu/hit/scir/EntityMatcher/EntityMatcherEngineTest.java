/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.dict.Entity;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.dict.StringToEntitiesMap;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.scir.semanticgraph.DGNode;
import cn.edu.hit.scir.semanticgraph.SemanticGraph;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月24日 
 */
public class EntityMatcherEngineTest {

	Ontology ontology = Ontology.getInstance();
	EntityMatcherEngine emEngine =  EntityMatcherEngine.getInstance();

	
	StringToEntitiesMap s2eMap = new StringToEntitiesMap ();
	
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("setUp ~");
		s2eMap.indexOntology(ontology);
	} 
	
	@After
	public void tearDown () throws Exception {
		System.out.println ("tear Down !");
	}
	
	@Test
	public void testMergeEntities () {
		
	}
	
	@Test
	public void testRearrange () {
		System.out.println (testRearrangeMatchedEntity ( 0 , "through which states does the mississippi run ?"));
		System.out.println (testRearrangeMatchedEntity ( 0 , "which is the density of the state that the largest river in the united states runs through ?"));
		System.out.println (testRearrangeMatchedEntity ( 0 , "through which states does the longest river in texas run ?"));
		System.out.println (testRearrangeMatchedEntity ( 0 , "what states border states which the mississippi runs through ?"));
	}
	
	private String testRearrangeMatchedEntity (int pos, String query) {
		StringBuffer bf = new StringBuffer ();
		bf.append("@query #" + pos + " : " + query + "\n");
		SemanticGraph smtcGraph = new SemanticGraph (query);
		List<List<MatchedEntity>> mes = emEngine.runEntityMatcherEngine(smtcGraph);
		for (List<MatchedEntity> me : mes ) {
			if (me.isEmpty()) {
				bf.append("me : " + me);
				continue;
			}
			bf.append("node : " + me.get(0).getQuery() + "\tme : " + "(" + StringUtils.join(me, ", ") + ")\n"); 
		}
		bf.append("-----------------------------------------split line--------------------------------------\n");
		return bf.toString().replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
	}
	
	
	private String mergeEntities (String query ) {
		System.out.println("@query : " + query);
		SemanticGraph smtcGraph = new SemanticGraph (query);
		//smtcGraph.buildSemanticGraph();
		emEngine.runEntityMatcherEngine(smtcGraph);
//		emEngine.setSemanticGraph(smtcGraph);
//		emEngine.matchQuery(smtcGraph.getDependencyGraph().getVertexs());
//		emEngine.mergeEntities(smtcGraph.getDependencyGraph().getVertexs());
//		emEngine.mergeEntities();
		StringBuffer bf = new StringBuffer();
		//bf.append("@query : " + query + "\n");
		for (int i = 0 ; i < smtcGraph.getDependencyGraph().getVertexs().size(); ++i ) {
			if (emEngine.getMatchedQuery().get(i) == null || emEngine.getMatchedQuery().get(i).isEmpty() || smtcGraph.getDependencyGraph().getVertexNode(i).prevIndex != -1) {
				continue;
			}
			bf.append("node : " + smtcGraph.getDependencyGraph().getVertexNode(i).word + "\tmerge : (" + StringUtils.join(emEngine.getMatchedQuery().get(i), ",") + ")" + "\n");
		}
		bf.append("-----------------------------------------split line--------------------------------------\n");
		return bf.toString().replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
	}
	
//	@Test
	public void testMergeEntitiesBatchFiels () throws IOException {
		System.out.println("@testMergeEntitiesBatchFiels");
		final String inputFileName = "data/geo880.txt";
		final String outputFileName = "data/output/geoquestionsMergeEntityRearrange.txt";
		List<String> questions = FileUtils.readLines(new File(inputFileName));
		List<String> output = new ArrayList<String>();
		String res = "";
		for (String s : questions) {
			//res = "@query #" + output.size() + " : " + s + "\n";
			//res += mergeEntities (s);
			//res = res.replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
			res = testRearrangeMatchedEntity(output.size(), s);
			output.add(res);
			
		}
		FileUtils.writeLines(new File (outputFileName), output);
	}
	
	
	@Test
	public void testMatchQuery () {
		System.out.println("@testMatchQuery");
//		System.out.println(matchQuery ("how many rivers run through the mississippi state ?"));
//		System.out.println(mergeEntities ("how many rivers run through the mississippi state ?"));
//		System.out.println(matchQuery ("how many rivers flow through the new york state ?"));
//		System.out.println(mergeEntities ("how many rivers flow through the new york state ?"));
//		System.out.println(matchQuery ("how big is the city of new york ?"));
//		System.out.println(mergeEntities ("how big is the city of new york ?"));
//		System.out.println(matchQuery ("what is the population of new york city ?"));
//		System.out.println(mergeEntities ("what is the population of new york city ?"));
//		System.out.println(matchQuery ("how big is north dakota ?"));
//		System.out.println(mergeEntities ("how big is north dakota ?"));
//		System.out.println(matchQuery ("what is the highest point in the state of oregon ?"));
//		System.out.println(mergeEntities ("what is the highest point in the state of oregon ?"));
//		System.out.println(mergeEntities ("which states does the mississippi river run through ?"));
//		
//		System.out.println(matchQuery ("what cities are in states that border texas ?"));
//		System.out.println(mergeEntities ("what cities are in states that border texas ?"));
//		
//		System.out.println(matchQuery ("what rivers are in states that border texas ?"));
//		System.out.println(mergeEntities ("what rivers are in states that border texas ?"));
//		
//		System.out.println(mergeEntities ("what are the high points of states surrounding mississippi ?"));
//		System.out.println(mergeEntities ("how many inhabitants does montgomery have ?"));
//		System.out.println(mergeEntities ("what is the river that cross over ohio ?"));
//		System.out.println(mergeEntities ("how many citizens in boulder ?"));
//		System.out.println(mergeEntities ("how many major cities are in texas ?"));
//		System.out.println(mergeEntities ("how many major rivers cross ohio ?"));
//		System.out.println(mergeEntities ("what is the adjacent state of california ?"));
//		System.out.println(mergeEntities ("of the states washed by the mississippi river which has the lowest point ?"));
//		System.out.println(mergeEntities ("how many states border colorado and border new mexico ?"));
	
	}
	
	public String matchQuery (String query) {
		System.out.println("@query : " + query);
		SemanticGraph smtcGraph = new SemanticGraph (query);
		smtcGraph.buildSemanticGraph();
		emEngine.setSemanticGraph(smtcGraph);
		emEngine.matchQuery(smtcGraph.getDependencyGraph().getVertexs());
		StringBuffer bf = new StringBuffer();
		bf.append("@query : " + query);
		for (DGNode node : smtcGraph.getDependencyGraph().getVertexs()) {
			if (node.matchedEntitySet != null)
				bf.append ("node: " + node.toString() + "\n" + StringUtils.join(node.matchedEntitySet, ", ") + "\n");
		}
		
		return bf.toString().replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
	}
	
	
	
	
	
	//@Test
	public void testStr2EntityMap () {
		str2EntityMap ("rhode island");
		str2EntityMap ("rhode_island");
		str2EntityMap ("island rhode");
		str2EntityMap ("island_rhode");
		str2EntityMap ("rhode");
		str2EntityMap ("island");
		str2EntityMap ("state");
		str2EntityMap ("new york");
		str2EntityMap ("new");
		str2EntityMap ("york");
		str2EntityMap ("york new");
		str2EntityMap ("mississippi state");
		
	}
	
	private void  str2EntityMap (String word) {
		Set<Entity> entitySet = s2eMap.getEntitySet(word);
		StringBuffer bf = new StringBuffer ();
		bf.append("\n@Word : " + word + "\n");
		for (Entity ety : entitySet ) {
			bf.append(ety.toString () + ", ");
		}
		System.out.println(bf.toString().replaceAll("http://ir.hit.edu/nli/geo/", "geo:"));
	}
	
	
	//@Test
	public void test() {
		System.out.println ("@testEntityMatcherEngine");
//		entityMatch ("how many rivers run through the new york states ?");
		String res = entityMatch ("how many states have a city named springfield ?");
		res = res.replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
		System.out.println("res : " + res);
	}
	
	//@Test
	public void testBatchFiels () throws IOException {
		System.out.println("@testBatchFiles");
		final String inputFileName = "data/output/geoquestions.txt";
		final String outputFileName = "data/output/geoquestionsMathcedEntity.txt";
		List<String> questions = FileUtils.readLines(new File(inputFileName));
		List<String> output = new ArrayList<String>();
		String res = "";
		for (String s : questions) {
			res = entityMatch (s);
			res = res.replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
			output.add(res);
		}
		FileUtils.writeLines(new File (outputFileName), output);
	}

	
	public String entityMatch (String sentence ) {
		 StringBuffer bf = new StringBuffer ();
		 SemanticGraph smtcGraph = new SemanticGraph (sentence);
		 smtcGraph.buildSemanticGraph();
		 emEngine.setSemanticGraph(smtcGraph);
		 emEngine.mather();
		 
		 Map<DGNode, List<List<MatchedEntity>>> matchNodeEntityMap = emEngine.getMatchedEntityMap();
		 
		 String [] matchName = {"CompleteMatch : ", "SynonymMatch  : ", "OntologyMatch : ", "VotedMatch    : "};
		 bf.append("@query : " + sentence);
		 for (DGNode node : emEngine.getSentenceVertex()) {
//			 System.out.println ("\nnode : " + node.toString() );
			 bf.append("\nNode: " + node.toString() + "\n");
			 List<List<MatchedEntity>> entityList = matchNodeEntityMap.get(node);
			 
			 int i = 0;
			 for (List<MatchedEntity> entities : entityList) {
				 if (entities.size() > 5) {
					 entities = entities.subList(0, 5);
				 }
				 bf.append(matchName[i++] + StringUtils.join(entities, ", ") + "\n");
				 //System.out.println (matchName[i++] + StringUtils.join(entities, ", "));
			 }
		 }

		 bf.append("---------------------------------------split line-----------------------------------------\n");
		 return bf.toString();
	}

}
