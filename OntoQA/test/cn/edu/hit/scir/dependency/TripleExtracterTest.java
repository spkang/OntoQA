package cn.edu.hit.scir.dependency;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TripleExtracterTest {

	private static Logger logger = Logger.getLogger(TripleExtracterTest.class);
	private static TripleExtracter extracter = TripleExtracter.getInstance();
	@Before
	public void setUp  () {
		logger.info("Before");
	}
	
	@After 
	public void tearDown () {
		logger.info("After");
	}
	
	@Test
	public void test() {
		logger.info("@test");
		testExtractTripleFromSentence ("give me the longest river that passes through the us .");
		testExtractTripleFromSentence ("how high is the highest point in the largest state ?");
	}
	
	public void testExtractTripleFromSentence (String rawSentence) {
		extracter.initSentence(rawSentence);
		extracter.extractTriple();
		
	}

}
