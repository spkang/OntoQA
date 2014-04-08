package cn.edu.hit.scir.dependency;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import edu.stanford.nlp.ling.CoreLabel;

public class StanfordParserTest {
	private static Logger logger = Logger.getLogger(StanfordParserTest.class);
	StanfordParser sdParser = new StanfordParser();
	
	/**
	 * set up test
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp () throws Exception {
		logger.info("setUp");
	}
	
	
	/**
	 * tear down test
	 * 
	 * @throws Exception
	 */
	@After 
	public void tearDown () throws Exception {
		logger.info("tearDown");
	}
	
	/**
	 * test stanford parser
	 */
	@Test
	public void testParser () {
		String [] testData = {"which states does the missouri river run through ?"};
		String [] exTokens = {"which", "states", "does", "the", "missouri", "river", "run", "through", "?"};
		
		List<CoreLabel> tokens = sdParser.tokenizerString(testData[0]);
		for (int i = 0; i < exTokens.length; ++i ) {
			//assertEquals (tokens.get(i), exTokens[i]);
			System.out.println("# : " + tokens.get(i).tag().toString());
		}
	}

}
