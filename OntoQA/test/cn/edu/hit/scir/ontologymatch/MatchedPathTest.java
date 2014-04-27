/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import cn.edu.hit.scir.semanticgraph.SemanticEdge;
import cn.edu.hit.scir.semanticgraph.SemanticNode;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月10日 
 */
public class MatchedPathTest {

	MatchedPath mpath = null;
	
	@Test
	public void test() {
		testMatchedPath ("how many rivers are in the mississippi state?");
		testMatchedPath ("how many major cities are in states surrounding utah ?");
		testMatchedPath ("how many rivers run through texas ?");
		testMatchedPath ("how many rivers run texas ?");
		testMatchedPath ("what are the high points of states surrounding mississippi ?");
		testMatchedPath ("how many people live in the biggest city in new york state ?");
		testMatchedPath ("what are the populations of states through which the mississippi river runs ?");
	}
	
	public void testMatchedPath (String sentence ) {
		mpath = new MatchedPath (sentence);
		mpath.match();
		System.out.println("path : " + mpath.pathNodeToLineString());
	//	System.out.println ("pathNodeMap : " + mpath.getPathNodeMap());
//		System.out.println("mpath : \n" + mpath.toString());
	}
	

	//@Test
	public void testMatchBatchFile () {
		final String IN = "./data/output/geoquestions.txt";
		final String OUT = "./data/output/geoquestion_entityMatch.txt";
		try {
			List<String> questions = FileUtils.readLines(new File (IN));
			List<String> outContent = new ArrayList<String> ();
			String line = "";
			for (String qt : questions) {
				mpath = new MatchedPath (qt);
				mpath.match();
				line += "@query : " + qt + "\n";
				for (PathNode pNode : mpath.getPathNode()) {
					if (pNode.isSemanticEdge()) {
						SemanticEdge edge = (SemanticEdge)pNode.getNode();
						line += "@matchWords   : " + edge.getLinkWords() + "\n";
						line += "#matcheEntity : " + mpath.getPathNodeMap().get(pNode) + "\n\n";
					}
					else {
						SemanticNode node = (SemanticNode)pNode.getNode();
						line += "@matchWords   : " + node.getCoreWords()+ "\n";
						line += "#matcheEntity : " + mpath.getPathNodeMap().get(pNode) + "\n\n";
					}
				}
				line += "\n";
				outContent.add(line);
			}
			FileUtils.writeLines(new File (OUT), "utf-8", outContent);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
