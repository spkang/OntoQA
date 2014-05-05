/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.demo;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.scir.ontologymatch.QueryAnalyzer;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * The OntoQA Demo.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-20
 */

public class OntoQADemo {
	
	public static final String SERVER_PORT = "server.port";
	
	private static Logger logger = Logger.getLogger(OntoQADemo.class);
	
	private Configuration config;
	
	private Server server;
	
	private int port;
	
	public OntoQADemo() {
		initConfig();
		
		server = new Server();
		port = config.getInt(SERVER_PORT);
		
		Connector connector = new SelectChannelConnector();
	    connector.setPort(port);
	    server.setConnectors(new Connector[] { connector });
	    
	    server.setHandler(new OntoQAHandler());
	}
	
	private void initConfig() {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		logger.info("@run");
		
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Runs the demo.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		OntoQADemo demo = new OntoQADemo();
		demo.run();
	}

}

class OntoQAHandler extends AbstractHandler {
	public static final String CMD_PARAMETER = "cmd";
	public static final String QUERY_PARAMETER = "query";
	
	private static Logger logger = Logger.getLogger(OntoQADemo.class);
	
	//private QuestionAnalyzer analyzer; 
	private QueryAnalyzer analyzer;
	private Ontology ontology;
	
	/**
	 * Creates a new instance of OntoQAHandler.
	 */
	public OntoQAHandler() {
		super();

		//analyzer = new QuestionAnalyzer();
		analyzer = new QueryAnalyzer();
		ontology = Ontology.getInstance();
	}
	
	private String getResult(String query) {
		String sparql = analyzer.getSparql(query);
		List<RDFNode> results = ontology.getResultNodes(sparql);
		if (results != null && results.size() > 0) {
			ResultTemplate template = new ResultTemplate(ontology);
			template.addAnswerNodes(results);
			template.addSparql(sparql);
			
			RDFNodesTable table = new RDFNodesTable(ontology, results);
			template.addEntityTable(table);
			
			return template.toString();
		} else {
			return ResultTemplate.getNoAnswerResult();
		}
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jetty.server.Handler#handle(java.lang.String, org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handle(String target, Request baseRequest,
		      HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String query = request.getParameter(QUERY_PARAMETER).trim();
		logger.info("query: " + query);
		
		String result = getResult(query);

		logger.info("result: " + result);
		
		/** Don't transfer to other handler. */
		baseRequest.setHandled(true);
		
		response.setContentType("text/plain;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		
		response.getWriter().println(result);
		response.getWriter().flush();
		response.getWriter().close();
		
	}}
