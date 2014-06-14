/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * The SPARQL class.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-18
 */

public class Sparql {
	public static final String INVALID_SPARQL = "";
	
	public static final int NO_LIMIT = -1;
	
	public static final String SPACE = " ";
	public static final String TAB = "\t";
	public static final String LINE_END = System.getProperty("line.separator", "\n");
	
	private List<String> select;
	private List<String> where;
	private List<String> groupBy;
	private List<String> orderBy;
	private List<Sparql> subQueries;
	private int limit;
	
	public Sparql() {
		select = new ArrayList<String>();
		where = new ArrayList<String>();
		groupBy = new ArrayList<String>();
		orderBy = new ArrayList<String>();
		subQueries = new ArrayList<Sparql>();
		limit = NO_LIMIT;
	}
	
	public static String getCountVar(String name) {
		return name + "_count";
	}
	
	public void addSelect(String name) {
		select.add(name);
	}
	
	public void addFirstSelect(String name) {
		select.remove(name);
		select.add(0, name);
	}
	
	public String addCount(String name) {
		String countVar = getCountVar(name);
		String countString = "(COUNT(DISTINCT " + name + ") AS " + countVar + ")";;
		select.add(countString);
		return countVar;
	}
	
	public int getSelectSize() {
		return select.size();
	}
	
	public void addWhere(String triple) {
		where.add(triple);
	}
	
	public void addWhere(String s, String p, String o) {
		String triple = s + " " + p + " " + o + " .";
		addWhere(triple);
	}
	
	public void addOrder(String name) {
		orderBy.add(name);
	}
	
	public void addDescOrder(String name) {
		orderBy.add("DESC(" + name + ")");
	}
	
	public void addAscOrder(String name) {
		orderBy.add("ASC(" + name + ")");
	}
	
	public void addGroupBy(String name) {
		groupBy.add(name);
	}
	
	public void addSubQuery(Sparql subQuery) {
		subQueries.add(subQuery);
	}
	
	public Sparql getSubQuery() {
		if (subQueries.size() > 0) {
			return subQueries.get(0);
		}
		return null;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public boolean onlySubQuery() {
		return where.size() == 0 && subQueries.size() > 0;
	}
	
	public boolean isValid() {
		return select.size() > 0 && (where.size() > 0 || subQueries.size() > 0);
	}
	
	public String getString() {
		if (!isValid()) {
			return INVALID_SPARQL;
		}
		
		StringBuffer bf = new StringBuffer();
		
		// SELECT
		bf.append("SELECT DISTINCT ").append(StringUtils.join(select, SPACE)).append(LINE_END);
		
		// WHERE
		bf.append("WHERE {").append(LINE_END);
		// add triples
		for (String triple : where) {
			bf.append(TAB).append(triple).append(LINE_END);
		}
		// add sub queries
		for (Sparql subQuery : subQueries) {
			bf.append(TAB).append("{").append(LINE_END);
			bf.append(subQuery.getString());
			bf.append(TAB).append("}").append(LINE_END);
		}
		bf.append("}").append(LINE_END);
		
		// GROUP BY
		if (groupBy.size() > 0) {
			bf.append("GROUP BY ");
			bf.append(StringUtils.join(groupBy, SPACE));
			bf.append(LINE_END);
		}
		
		// ORDER BY
		if (orderBy.size() > 0) {
			bf.append("ORDER BY ");
			bf.append(StringUtils.join(orderBy, SPACE));
			bf.append(LINE_END);
		}
		
		// LIMIT
		if (limit != NO_LIMIT) {
			bf.append("LIMIT ").append(limit).append(LINE_END);
		}
		
		return bf.toString();
	}
	
	public String toString() {
		return getString();
	}

}
