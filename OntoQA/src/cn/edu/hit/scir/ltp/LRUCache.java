//package cn.edu.hit.ir.qa.util;
package cn.edu.hit.scir.ltp;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	
	private int cacheSize;
	
	public LRUCache(int size){
		super(16, (float)0.75, true);	//true for access-order
		this.cacheSize = size;
	}
	
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest){
		return size() >= cacheSize;
	}
}
