/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.experiment;

/**
 * Test example.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-7
 */

public class Example {

	private String id;
	private String query;
	private String sparql;
	private String answer;
	private String sparqlOut;
	
	private String status;
	private String sparqlStatus;
	
	public Example(String id, String query, String sparql, String answer, String sparqlOut) {
		setId(id);
		setQuery(query);
		setSparql(sparql);
		setAnswer(answer);
		setSparqlOut(sparqlOut);
		
		setSparqlStatus("");
	}
	
	public Example(String id, String query, String sparql, String answer) {
		this(id, query, sparql, answer, "");
	}
	
	/**
	 * Get the id.
	 *
	 * @return The id
	 */
	public String getId() {
		return id;
	}
	/**
	 * Set the id.
	 *
	 * @param id The id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Get the query.
	 *
	 * @return The query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * Set the query.
	 *
	 * @param query The query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	/**
	 * Get the sparql.
	 *
	 * @return The sparql
	 */
	public String getSparql() {
		return sparql;
	}
	/**
	 * Set the sparql.
	 *
	 * @param sparql The sparql to set
	 */
	public void setSparql(String sparql) {
		this.sparql = sparql;
	}
	/**
	 * Get the answer.
	 *
	 * @return The answer
	 */
	public String getAnswer() {
		return answer;
	}
	/**
	 * Set the answer.
	 *
	 * @param answer The answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * Set the sparqlOut.
	 *
	 * @param sparqlOut The sparqlOut to set
	 */
	public void setSparqlOut(String sparqlOut) {
		this.sparqlOut = sparqlOut;
	}

	/**
	 * Get the sparqlOut.
	 *
	 * @return The sparqlOut
	 */
	public String getSparqlOut() {
		return sparqlOut;
	}

	/**
	 * Set the status.
	 *
	 * @param status The status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Get the status.
	 *
	 * @return The status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Set the sparqlStatus.
	 *
	 * @param sparqlStatus The sparqlStatus to set
	 */
	public void setSparqlStatus(String sparqlStatus) {
		this.sparqlStatus = sparqlStatus;
	}

	/**
	 * Get the sparqlStatus.
	 *
	 * @return The sparqlStatus
	 */
	public String getSparqlStatus() {
		return sparqlStatus;
	}
	
	public boolean isTodoStatus() {
		return CorpusTool.TODO_STATUS.equals(sparqlStatus);
	}
}
