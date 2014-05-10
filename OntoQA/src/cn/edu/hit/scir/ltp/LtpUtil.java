package cn.edu.hit.scir.ltp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.w3c.dom.Element;

import cn.edu.hit.ir.util.ConfigUtil;

public class LtpUtil implements LtpTool {
	private static LtpUtil m_instance;
	
	private CloseableHttpClient m_httpClient; 	//client to do the POST request
	private final String LTP_HOST = "ltp.host";
	private final String LTP_PORT = "ltp.port";
	private final String LTP_PATH = "ltp.path";
	private final String LTP_CACHHE_SIZE = "ltp.cache.size"; 

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
	
	@Override
	public String analyze (String sentence) {
		return analyze (sentence, "pos", false, "utf-8");
	}
	
	public String analyze (String sentence, String task) {
		return analyze (sentence, task, false, "utf-8");
	} 
	
	public String analyze (String sentence, String task, boolean xmlInput) {
		return analyze (sentence, task, xmlInput, "utf-8");
	}
	
	@Override
	public String analyze (String sentence, boolean xmlInput) {
		return analyze (sentence, "dp", xmlInput, "utf-8");
	}
	
	/**
	 * 调用LTP分析句子
	 * @param content，待分析的句子
	 * @param task : 分析的任务 : 用以指明分析目标，
	 * 		task 可以为
	 * 			分词			ws
	 * 			词性标注		pos
	 * 			命名实体识别	ner
	 * 			依存句法分析	dp
	 * 			语义角色标注	srl
	 * 			或者全部任务	all
	 * 
	 * @param xmlInput :  指定输入的内容是句子还是xml
	 * @param encoding : 指定编码
	 * @return LTP分析结果，字符串形式的XML
	 */
	
	public String analyze(String content, String task, boolean xmlInput, String encoding){
		if (!xmlInput) {
			String cacheResult = getFromCache(content);
			if(null != cacheResult){	//sentence analysis result is in cache
				return cacheResult;
			}
		}
		
		//else, call LTP server to analyze the sentence, and put the result to cache
		String uri = getLtpUri();
		HttpPost post = new HttpPost(uri);
		post.addHeader("User-Agent", "Mozilla/5.0");
		
		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
		urlParams.add(new BasicNameValuePair("t", task));
		urlParams.add(new BasicNameValuePair("s", content));
		if (!xmlInput)
			urlParams.add(new BasicNameValuePair("x", "n"));
		else 
			urlParams.add(new BasicNameValuePair("x", "y"));
		urlParams.add(new BasicNameValuePair("c", encoding));
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
		if (!xmlInput)
		putToCache(content, result.toString());
		return result.toString();
	}
	
	
	/**
	 *  根据句子、词、词性来构造一个xml字符串，根据该函数可以将修改词性后的xml内容传递给ltp，来获得更改词性后的依存分析结果
	 * 
	 * @param line, 句子的内容
	 * @param words, 句子经过分词后的结果
	 * @param tags, 每个词的词性
	 * @return String 能够被ltp接受的xml格式的字符串
	 */
	private String buildLTMLFromWords(String line,List<String> words, List<String> tags)  {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.newDocument();

            Element xml4nlp = document.createElement("xml4nlp");
            document.appendChild(xml4nlp);

            Element note = document.createElement("note");
            note.setAttribute("sent",   "y");
            note.setAttribute("word",   "y");
            note.setAttribute("pos",    "y");
            note.setAttribute("ne",     "n");
            note.setAttribute("parser", "n");
            note.setAttribute("srl",    "n");
            xml4nlp.appendChild(note);

            Element doc = document.createElement("doc");
            xml4nlp.appendChild(doc);

            Element para = document.createElement("para");
            para.setAttribute("id", "0");
            doc.appendChild(para);

            int sentId = 0;
            Element sent = document.createElement("sent");
            sent.setAttribute("id", String.valueOf(sentId));
            sent.setAttribute("cont", line.replaceAll(" ", ""));
            para.appendChild(sent);

            for (int i = 0; i < words.size(); ++ i) {
                Element word = document.createElement("word");

                StringBuilder sb = new StringBuilder();
                sb.append(i);
                word.setAttribute("id",   sb.toString());
                word.setAttribute("cont", words.get(i)); 
                word.setAttribute("pos",  tags.get(i));
                sent.appendChild(word);
                
               /* if(words[i].equals("。")||words[i].equals("！"))
                {
                	sentId++;
                	sent = document.createElement("sent");
                    sent.setAttribute("id", String.valueOf(sentId));
                    para.appendChild(sent);
                }*/
            }

            StringWriter xmlResultResource = new StringWriter();

            Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(xmlResultResource)
                    );

            return xmlResultResource.getBuffer().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
	@Override
	public List<String> ltpSegment (String sentence) {
		return this.wordSegment(sentence);
	}
	
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see test.LtpTool#ltpTag(java.lang.String)
	 */
	@Override
	public List<String> ltpTag (String sentence) {
		return this.posTag(sentence);
	}
	
	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see test.LtpTool#ltpSegmentTag(java.lang.String)
	 */
	@Override
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
