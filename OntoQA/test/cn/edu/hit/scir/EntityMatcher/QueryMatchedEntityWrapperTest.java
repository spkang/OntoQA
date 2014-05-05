/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.dict.MatchedEntity;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月29日 
 */
public class QueryMatchedEntityWrapperTest {

	private QueryMatchedEntityWrapper wrapper = null;
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("set up");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println ("tear down ");
	} 
	
	@Test
	public void testNotNo () {
//		System.out.println (testNotNoWrapper (3, "how many rivers do not traverse the state with the capital albany ?"));
//		System.out.println (testNotNoWrapper (4, "how many states do not have rivers ?"));
//		System.out.println (testNotNoWrapper (5, "which states does not border texas ?"));
//		System.out.println (testNotNoWrapper (6, "which rivers do not run through usa ?"));
//		System.out.println (testNotNoWrapper (7, "which rivers do not run through texas ?"));
//		System.out.println (testNotNoWrapper (8, "which is the highest peak not in alaska ?"));
//		System.out.println (testNotNoWrapper (9, "which capitals are not major cities ?"));
//		System.out.println (testNotNoWrapper (10, "what rivers do not run through tennessee ?"));
//		System.out.println (testNotNoWrapper (11, "what is the longest river that does not run through texas ?"));
//		System.out.println (testNotNoWrapper (12, "name the states which have no surrounding states ?"));
//		System.out.println (testNotNoWrapper (13, "which states border no other states ?"));
//		System.out.println (testNotNoWrapper (14, "what states have no bordering state ?"));
//		System.out.println (testNotNoWrapper (15, "what state has no rivers ?"));
//		System.out.println (testNotNoWrapper (16, "what river is the longest one in the united states ?"));
//		System.out.println (testNotNoWrapper (17, "what is the length of the river that runs through the most number of states ?"));
		
	}
	
	
	private String testNotNoWrapper (int pos, String query) {
		StringBuffer bf = new StringBuffer ();
		bf.append("@query #" + pos + " : " + query +  "\n");
		wrapper = new QueryMatchedEntityWrapper (query);
		for (List<MatchedEntity> me : wrapper.getMatchEntityWrapper()) {
			bf.append("node:" + me.get(0).getQuery() + "\tmeModifier : {" + (me.get(0).getModifizers() == null ? "null" : StringUtils.join(me.get(0).getModifizers(), ",") )+  "}\tme : ( " + StringUtils.join(me, ", ") + ")" + "\n");
		}
		bf.append ("is count : " + wrapper.isCount() + "\n");
		bf.append("-----------------------------------------split line-----------------------------------\n");
		return bf.toString();
	} 
	
//	@Test
	public void testBatchFile () throws Exception {
		System.out.println("@testBatchFiles");
		final String inputFileName = "data/geo880.txt";
		final String outputFileName = "data/output/geoquestionsModifiers2.txt";
		List<String> questions = FileUtils.readLines(new File(inputFileName));
		List<String> output = new ArrayList<String>();
		String res = "";
		for (String s : questions) {
			System.out.println("s :" + s);
			res = testNotNoWrapper (output.size(), s);
			//res = res.replaceAll("http://ir.hit.edu/nli/geo/", "geo:");
			output.add(res);
		}
		FileUtils.writeLines(new File (outputFileName), output);
	} 
	
	@Test
	public void testWrapper () {
		System.out.println (testMEWrapper (0, "how many people in new york city ?"));
		System.out.println (testMEWrapper (1, "how many rivers in new york city ?"));
		System.out.println (testMEWrapper (2, "how many rivers in new york city ?"));
		System.out.println (testMEWrapper (2, "which states have cities named austin ?"));
		
	}
	
	private String testMEWrapper (int pos , String query ) {
		StringBuffer bf = new StringBuffer ();
		bf.append("@query #" + pos + " : " + query +  "\n");
		wrapper = new QueryMatchedEntityWrapper (query);
		for (List<MatchedEntity> me : wrapper.getMatchEntityWrapper()) {
			bf.append("start point : " + me.get(0).getBegin() + "\tme : ( " + StringUtils.join(me, ", ") + ")" + "\n");
		}
		bf.append ("is count : " + wrapper.isCount() + "\n");
		bf.append("-----------------------------------------split line-----------------------------------\n");
		return bf.toString();
	}

}
