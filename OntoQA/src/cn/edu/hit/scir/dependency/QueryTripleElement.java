package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月25日 
 */
public class QueryTripleElement {
	// private String text = null;
	private String target = null;
	private String posTag = null;
	private List<String> modifiers = new ArrayList<String>();
	public QueryTripleElement () {}
	public QueryTripleElement (String target, String posTag, List<String> modifier) {
		this.target = target;
		this.posTag = posTag;
		if (this.modifiers == null )
			this.modifiers = new ArrayList<String> ();
		this.modifiers.clear();
		this.modifiers.addAll(modifiers);
	}
	
	public void setTarget (String target ) {
		this.target = target;
	}
	
	public String getTarget () {
		return this.target;
	}

	public void setPosTag (String posTag) {
		this.posTag = posTag;
	} 
	
	public String getPosTag () {
		return this.posTag;
	}
	
	public void setModifiers (List<String> modifiers) {
		this.modifiers = modifiers;
	}
	
	public List<String> getModifiers () {
		return this.modifiers;
	}
	
	public boolean equals (QueryTripleElement ele) {
		if (target.equals(ele.getTarget()))
			return true;
		return false;
	}
	
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		sb.append("[").append(target + ", ").append(posTag + ", " ).append(StringUtil.join(modifiers, " ")).append("]");
		return sb.toString();
	}
}

