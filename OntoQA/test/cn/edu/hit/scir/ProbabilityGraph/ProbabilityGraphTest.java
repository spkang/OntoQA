/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ProbabilityGraph;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.dict.MatchedEntity;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月24日 
 */
public class ProbabilityGraphTest {

	
	ProbabilityGraph graph = null;
	
	
	/**
	 * TODO
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println ("set up ");
	}

	/**
	 * TODO
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println ("tear down");
	}

	
	@Test
	public void testBit () {
		boolean [] bits = {true, false, false};
		int nb = 0;
		for (int i = 0; i < bits.length ; ++i ) {
			if (bits[i]) {
				nb = (nb | ( 1 << (bits.length - i - 1)));
			}
		}
		System.out.println ("nb : " + nb);
	}
	
	@Test
	public void testMissResource() {
//		testMiss ("how many states border the state that borders the most states ?");
//		testMiss ("how tall is the highest point in montana ?");
//		testMiss ("name the states which have no surrounding states ?");
//		testMiss ("through which states does the mississippi flow ?");
//		testMiss ("rivers in new york ?");
//		
//		// 调参数用的
//		testMiss ("what is the capital of new york ?");
//		testMiss ("what is the capital of new york which the mississippi run through?");
//		
//		testMiss ("how many river run through us ?");
//		testMiss ("how many aa run through us which border the adfa ?");
//		testMiss("how many river in new york");
//		testMiss ("which state border mississippi river ?");
		testMiss ("how many states have cities named austin ?");
		
	}

	
	private void testMiss (String query ) {
		System.out.println ("query : " + query);
		graph = new ProbabilityGraph (query);
//		for (List<MatchedEntity> mes : graph.getQueryMatchedEntities()) { 
//			System.out.println("me: " + StringUtils.join(mes, ", "));
//		}
//		System.out.println("Is  Miss resource : " + graph.isMissResource());
//		System.out.println("Has core resource : " + graph.hasCoreMatch());
//		System.out.println("Has core resource : " + graph.findSearchBeginPos(0));
		System.out.println("objs :=================================== ");
		for (List<Object> objs : graph.getCompleteMatchedObjects()) {
			System.out.println("obj : " + StringUtils.join(objs, ","));
		}
		graph.removeIllegalMatchedObjects();
		graph.completeMatchedObjectScore();
		System.out.println("objs : ----------------");
		for (List<Object> objs : graph.getCompleteMatchedObjects()) {
			System.out.println("obj : " + StringUtils.join(objs, ","));
		}
		
		System.out.println("begin Nodes : " + this.graph.getBeginNodes() );
		System.out.println("end   Nodes : " + this.graph.getEndNodes() );
		System.out.println("graph   " + graph.getGraph().toString());
		System.out.println("graph vertex  " + graph.getGraph().vertexSet());
		System.out.println("graph edgeset " + graph.getGraph().edgeSet());
		
		if (this.graph.getGraph() == null )
			return ;
		GraphPathSelector pathSel = new GraphPathSelector (this.graph.getGraph(), this.graph.getBeginNodes(), this.graph.getEndNodes());
		
		//pathSel.findAllPath();
		
	} 
}
