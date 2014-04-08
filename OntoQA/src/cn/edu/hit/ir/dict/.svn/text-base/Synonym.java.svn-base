/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import cn.edu.hit.ir.util.ObjectToSet;

/**
 * A synonym management class.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-25
 */

public class Synonym {

	 /**
     * The underlying mapping from phrases to their set of synonyms.
     * It's symmetric in that if the value for
     * <code>synonymDict.getSet(x).contains(y)</code> should be
     * the same as that for then
     * <code/>synonymDict.getSet(y).contains(x)</code>.
     */
    private final ObjectToSet<String,String> synonymDict;
    
    private List<SynonymOntology> ontologies;
    
    
    /**
     * Creates a new instance of Synonym.
     *
     */
    public Synonym() {
    	synonymDict = new ObjectToSet<String,String>();
    	ontologies = new ArrayList<SynonymOntology>();
    }
    
    /**
     * Loads synonyms from a file.
     * Each line in this file means a synonym set, and all the synonyms
     * are separated by ",".
     *
     * @param filePath The path of the file
     */
    public void load(String filePath) {
    	try {
			List<String> lines = FileUtils.readLines(new File(filePath));
			for (String line : lines) {
				String[] synonyms = line.split("\\s*,\\s*");
				for (int i = 0; i < synonyms.length; i++) {
					for (int j = i+1; j < synonyms.length; j++) {
						add(synonyms[i], synonyms[j]);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void addOntology(SynonymOntology ontology) {
    	ontologies.add(ontology);
    }
    
    /**
     * Adds the two phrases as synonyms for one another.  The
     * operation is symmetric, so that they do not need to be added in
     * the reverse order.
     *
     * @param phrase1 First phrase in the synonym pair.
     * @param phrase2 Second phrase in the synonym pair.
     */
    public void add(String phrase1, String phrase2) {
        synonymDict.addMember(phrase1,phrase2);
        synonymDict.addMember(phrase2,phrase1);
    }

    /**
     * Ensure that the two phrases are no longer synonyms of each other.
     * The operation is symmetric, so they do not need to be removed
     * in the reverse order.
     *
     * @param phrase1 First phrase in the synonym pair.
     * @param phrase2 Second phrase in the synonym pair.
     */
    public void remove(String phrase1, String phrase2) {
        synonymDict.removeMember(phrase1,phrase2);
        synonymDict.removeMember(phrase2,phrase1);
    }

    /**
     * Removes all synonym pairs from this synonym matcher.
     */
    public void clear() {
        synonymDict.clear();
    }
    
    /**
     * Returns the synonym set of the given phrase.
     *
     * @param phrase The given phrase
     * @return The synonym set
     */
    public Set<String> getSet(String phrase) {
    	Set<String> synonyms = synonymDict.getSet(phrase);
    	for (SynonymOntology ontology : ontologies) {
    		Set<String> syns = ontology.getSet(phrase);
    		if (syns != null) {
    			synonyms.addAll(syns);
    		}		
		}
    	return synonyms;
    }
    
    /**
	 * Returns the set of synonyms of the given phrase
	 *
	 * @param phrase the phrase
	 * @param pos its part of speech
	 * @return the set of synonyms
	 */
    public Set<String> getSet(String phrase, String pos) {
    	Set<String> synonyms = synonymDict.getSet(phrase);
    	for (SynonymOntology ontology : ontologies) {
    		Set<String> syns = ontology.getSet(phrase, pos);
    		if (syns != null) {
    			synonyms.addAll(syns);
    		}			
		}
    	return synonyms;
    }
}
