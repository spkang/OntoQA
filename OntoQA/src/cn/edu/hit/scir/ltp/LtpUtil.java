package cn.edu.hit.scir.ltp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import cn.edu.hit.ir.util.ConfigUtil;

public class LtpUtil implements LtpTool {
	private static LtpUtil m_instance;
	
	private CloseableHttpClient m_httpClient; 	//client to do the POST request
	private static final String LTP_HOST = "ltp.host";
	private static final String LTP_PORT = "ltp.port";
	private static final String LTP_PATH = "ltp.path";
	private static final String LTP_CACHHE_SIZE = "ltp.cache.size"; 

	private String m_ltpHost; 					//LTP部署的位置
	private String m_ltpPort;					//LTP服务器的端口号
	private String m_ltpPath;					//LTP服务器分析的路径
	private Configuration config = null;
	private LRUCache<String, String> m_cache;					//LTP分析结果缓存
	
	/**
	 * 构造方法
	 */
	private LtpUtil(){
		m_httpClient = HttpClients.createDefault();	
		//m_ltpHost = "http://127.0.0.1";
		//m_ltpHost = "http://192.168.3.41";	//LTP server run on lab machine
		//m_ltpPort = "12345";	
		//m_ltpPath = "ltp";
		initConfig();
		initData();
		
	}
	
	
	/**
	 *  初始化config
	 *
	 * @param 
	 * @return void 
	 */
	private void initConfig () {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		}
		catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	
	private void initData () {
		if (this.config == null )
			initConfig ();
		this.m_ltpHost = config.getString(this.LTP_HOST);
		this.m_ltpPort = config.getString(this.LTP_PORT);
		this.m_ltpPath = config.getString(this.LTP_PATH);
		int cacheSize  = config.getInt(this.LTP_CACHHE_SIZE);
		
		if (cacheSize < 0 || cacheSize > 10000)
			cacheSize = 50;
		m_cache = new LRUCache<String, String>(cacheSize);
	}
	
	
	
	/**
	 * 单例模式
	 * @return LtpUtil单例
	 */
	public static LtpUtil getInstance(){
		if(null == m_instance){
			m_instance = new LtpUtil();
		}
		return m_instance;
	}
	
	/**
	 * 组装调用LTP的URI，默认为本地，端口为12345
	 * @return
	 */
	private String getLtpUri(){
		//default LTP server URI: http://127.0.0.1:12345/ltp
		return m_ltpHost + ":" + m_ltpPort + "/" + m_ltpPath;
	}
	
	/**
	 * 调用LTP分析句子
	 * @param sentence，待分析的句子
	 * @return LTP分析结果，字符串形式的XML
	 */
	private String analyze(String sentence){
		String cacheResult = getFromCache(sentence);
		if(null != cacheResult){	//sentence analysis result is in cache
			return cacheResult;
		}
		
		//else, call LTP server to analyze the sentence, and put the result to cache
		String uri = getLtpUri();
		HttpPost post = new HttpPost(uri);
		post.addHeader("User-Agent", "Mozilla/5.0");
		
		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
		urlParams.add(new BasicNameValuePair("t", "pos"));
		urlParams.add(new BasicNameValuePair("s", sentence));
		urlParams.add(new BasicNameValuePair("x", "n"));
		
		StringBuffer result = new StringBuffer();
		
		try{
			UrlEncodedFormEntity encodedParams = new UrlEncodedFormEntity(urlParams, "utf-8"); 
			post.setEntity(encodedParams);
			CloseableHttpResponse response = m_httpClient.execute(post);
			if(response.getStatusLine().getStatusCode() != 200){	//LTP server failed to do the analysis
				return null;
			}
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = rd.readLine();
			while(null != line){
				result.append(line);
				line = rd.readLine();
			}
			response.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

		putToCache(sentence, result.toString());
		return result.toString();
	}
	
	/**
	 * 缓存LTP分析结果
	 * @param key，所分析的句子
	 * @param value，分析的结果，字符串形式的XML
	 */
	private void putToCache(String key, String value){
		if( null == m_cache ){
			return;
		}
		else{
			m_cache.put(key, value);
		}
	}
	
	/**
	 * 从缓存中获取LTP分析结果
	 * @param key，所分析的句子
	 * @return 若缓存中无该句的分析结果，返回null;否则返回分析结果
	 */
	private String getFromCache(String key){
		if( null == m_cache || !m_cache.containsKey(key) ){
			return null;
		}
		else{
			return m_cache.get(key);
		}
	}
	
	/**
	 * 对句子进行分词
	 * @param sentence，待分析的句子
	 * @return 分词结果，以List<String>的形式返回
	 */
	private List<String> wordSegment(String sentence){
		List<String> result = new ArrayList<String>();
		
		String xmlString = analyze(sentence);
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlString);
			List list = doc.selectNodes("//word/@cont");
			
			for(Iterator iter = list.iterator(); iter.hasNext();){
				Attribute attr = (Attribute) iter.next();
				String w = attr.getValue();
				result.add(w);
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 词性标注
	 * @param sentence 进行词性标注的句子
	 * @return 分词后每个词的词性，以ArrayList<String>的形式返回
	 */
	private List<String> posTag(String sentence){
		List<String> result = new ArrayList<String>();
		
		String xmlString = analyze(sentence);
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlString);
			List list = doc.selectNodes("//word/@pos");
			
			for(Iterator iter = list.iterator(); iter.hasNext();){
				Attribute attr = (Attribute) iter.next();
				String w = attr.getValue();
				result.add(w);
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see test.LtpTool#ltpSegment(java.lang.String)
	 */
	public List<String> ltpSegment (String sentence) {
		return this.wordSegment(sentence);
	}
	
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see test.LtpTool#ltpTag(java.lang.String)
	 */
	public List<String> ltpTag (String sentence) {
		return this.posTag(sentence);
	}
	
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see test.LtpTool#ltpSegmentTag(java.lang.String)
	 */
	public List<String> ltpSegmentTag (String sentence) {
		List<String> seg = this.wordSegment(sentence);
		List<String> tag = this.posTag(sentence);
		List<String> segTag = new ArrayList<String>();
		if (seg.size() != tag.size())
			return null;
		for (int i = 0; i < seg.size(); ++i ) {
			segTag.add(seg.get(i) + "/" + tag.get(i));
		}
		return segTag;
	}
}
