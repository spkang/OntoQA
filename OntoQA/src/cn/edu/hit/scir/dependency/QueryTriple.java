package cn.edu.hit.scir.dependency;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月25日 
 */
public class QueryTriple {
	
	public QueryTripleElement subject = null;
	public QueryTripleElement predicate = null;
	public QueryTripleElement object = null;
	
	
	public QueryTriple () {
	}
	
	/**
	 *
	 *
	 * @param
	 * @return 
	 */
	public QueryTriple (QueryTripleElement s, QueryTripleElement p, QueryTripleElement o) {
		subject = s;
		predicate = p;
		object = o;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString  () {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(subject.toString());
		sb.append(predicate.toString());
		sb.append(object.toString());
		return sb.toString();
	}
	
}
