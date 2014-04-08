package cn.edu.hit.scir.dependency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月12日 
 */
public class RelationStatistic {
	private static Logger logger = Logger.getLogger(RelationStatistic.class);
	private static Question questionInstance = Question.getInstance();
	private static StanfordTagger tagger = StanfordTagger.getInstance();
	private static RelationStatistic instance = null;
	
	/**
	 *
	 *
	 * @param
	 * @return 
	 */
	public static RelationStatistic getInstance () {
		if (instance == null )
			return new RelationStatistic ();
		return instance;
	}
	
	private RelationStatistic () {
		
	}
	
	/**
	 * get the specific file content
	 *
	 * @param path, question file path
	 * @return List<String>, file content
	 */
	/**
	 *
	 *
	 * @param 
	 * @return List<String> 
	 */
	public List<String> getFileContent (String path) {
		if (path == null || path.isEmpty())
			return null;
		List<String> content = new ArrayList<String>();
		try {
			
			BufferedReader input = new BufferedReader (new FileReader (new File (path)));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					if (line.trim() == null || line.trim().isEmpty())
						continue;
					content.add(line.trim());
				}
			}
			finally {
				if (input != null)
					input.close();
			}
			
		}catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}catch (IOException ex) {
			ex.printStackTrace();
		}
		return content;
	}
	
	public  List<TaggedWord> getTaggedWords( String question) {

		questionInstance.initialize(question);
		//List<TaggedWord> taggedWds = questionInstance.getTaggedWord ();
		return tagger.taggerSentence(questionInstance.getProcessedQuestion());
	}
	
	public List<TypedDependency> getDependency (List<? extends HasWord>  taggedWords) {
		return questionInstance.getDependencyCCprocessed(taggedWords);
	}

	public void statistic (String inputFileName, String outputFileName ) {
		
		if (inputFileName == null || outputFileName == null) {
			logger.info("parameter error!");
			return ;
		}
		
		List<String> content = getFileContent ( inputFileName );
		if (content == null ) {
			logger.info("File content is null");
			return ;
		}
		// create map for statistic
		// <prep_in, <>>
		Map <String, List<String>> statisticMap = new HashMap <String, List<String>> ();
		
		Integer qtIndex = 1;
		for (String line : content ) {
			List<TaggedWord> taggedWords = getTaggedWords (line.trim());
			List<TypedDependency> dependencies = getDependency (taggedWords);
			
			for (TypedDependency td : dependencies ) {
				if (td.reln().toString().equals("root"))
					continue;
 
				Integer govIndex = td.gov().index();
				Integer depIndex = td.dep().index();
				String govTag  = taggedWords.get(govIndex - 1).tag();
				String depTag  = taggedWords.get(depIndex - 1).tag();
				String key = td.reln().toString() + "#" + govTag + "_" + depTag;
				if (statisticMap.containsKey(key)) {
					List<String> tmp = statisticMap.get(key);
					tmp.add (taggedWords.get(govIndex - 1).word() + "_" + taggedWords.get(depIndex - 1).word() + "_" + qtIndex.toString());
					statisticMap.put(key, tmp);
				}
				else {
					List<String> tmp = new ArrayList<String> ();
					tmp.add(taggedWords.get(govIndex - 1).word() + "_" + taggedWords.get(depIndex - 1).word() + "_" + qtIndex.toString());
					statisticMap.put(key, tmp);
				}
			}
			++qtIndex;
		}
		List<Map.Entry<String, List<String>>> list = new LinkedList<Map.Entry<String, List<String>>> (statisticMap.entrySet());
	
		Collections.sort(list, new Comparator<Map.Entry<String, List<String>>>(){
			public int compare (Map.Entry<String, List<String>> lhs, Map.Entry<String, List<String>> rhs) {
				//if ( lhs.getValue().size() == rhs.getValue().size())
					return lhs.getKey().compareTo(rhs.getKey());
				//return -(lhs.getValue().size() - rhs.getValue().size());
			}
		});
		
		for (Map.Entry<String,List<String>> entry : list )  {
			logger.info(entry.getKey() + "\t\t" + entry.getValue().size());
			for (String s : entry.getValue())
				logger.info(s);
			logger.info("------------------------------------------------");
//			logger.info("value: " + );
		}
		try {
			BufferedWriter output = new BufferedWriter (new FileWriter (new File (outputFileName)));
			try {
				output.write("relation_tag1_tag2\n");
				output.write("----------------------------------------------\n");
				for (Map.Entry<String, List<String>> entry : list ) {
					String [] keys = entry.getKey().split("#");
					if (keys.length == 2 ) {
						output.write(keys[0] + "\n");
						output.write (keys[1] + "\t" + entry.getValue().size() + "\n");
						for (String s : entry.getValue())
							output.write (s + "\n");
						output.write ("----------------------------------------------\n");
					}
				}
			}
			finally {
				if (output != null )
					output.close();
			}
		}catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main (String [] args) {
		RelationStatistic instance = RelationStatistic.getInstance();
		final String INPUT_FILE_NAME = "data/geo880.txt";
		final String OUTPUT_FILE_NAME = "data/output/relationStatistic.txt";
		instance.statistic(INPUT_FILE_NAME, OUTPUT_FILE_NAME);
		logger.info("Finished");
	}
	
}
