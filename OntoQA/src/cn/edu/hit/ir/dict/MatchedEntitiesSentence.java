/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cn.edu.hit.ir.nlp.NlpSentence;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;

/**
 * A data structure representing all the matched entities for a sentence.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-27
 */

public class MatchedEntitiesSentence {
	
	public static final int NO_INDEX = -1;
	
	private NlpSentence nlpSentence;

	private String[] tokens;
	
	/**
	 * beginEntitiesList.get(i) store the entities begin with tokens[i]
	 */
	private List<ArrayList<MatchedEntity>> beginEntitiesList;
	/**
	 * endEntitiesList.get(i) store the entities end with tokens[i]
	 */
	private List<ArrayList<MatchedEntity>> endEntitiesList;
	
	private int[] beginIndexes;
	private int[] endIndexes;
	
	private int size;
	
	
	public MatchedEntitiesSentence(NlpSentence nlpSentence) {
		
		setNlpSentence(nlpSentence);
		setTokens(nlpSentence.getTokens());
		initData(getTokenSize());
	}

	/**
	 * Set the nlpSentence.
	 *
	 * @param nlpSentence The nlpSentence to set
	 */
	public void setNlpSentence(NlpSentence nlpSentence) {
		this.nlpSentence = nlpSentence;
	}

	/**
	 * Get the nlpSentence.
	 *
	 * @return The nlpSentence
	 */
	public NlpSentence getNlpSentence() {
		return nlpSentence;
	}

	/**
	 * Get the tokens.
	 *
	 * @return The tokens
	 */
	public String[] getTokens() {
		return tokens;
	}

	/**
	 * Set the tokens.
	 *
	 * @param tokens The tokens to set
	 */
	private void setTokens(String[] tokens) {
		this.tokens = tokens;
	}
	
	/**
	 * Get the entitiesList.
	 *
	 * @return The entitiesList
	 */
	public List<ArrayList<MatchedEntity>> getEntitiesList() {
		return beginEntitiesList;
	}

	/**
	 * Set the entitiesList.
	 *
	 * @param entitiesList The entitiesList to set
	 */
	public void setEntitiesList(List<ArrayList<MatchedEntity>> entitiesList) {
		this.beginEntitiesList = entitiesList;
	}

	/**
	 * Get the indexes.
	 *
	 * @return The indexes
	 */
	public int[] getIndexes() {
		return beginIndexes;
	}

	/**
	 * Set the indexes.
	 *
	 * @param indexes The indexes to set
	 */
	public void setIndexes(int[] indexes) {
		this.beginIndexes = indexes;
	}

	/**
	 * Get the size.
	 *
	 * @return The size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Set the size.
	 *
	 * @param size The size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * Sets the matched entities for a specified token.
	 *
	 * @param index The index of the token.
	 * @param matchedEntities The matched entities
	 */
	public void addMatchedEntities(int index, ArrayList<MatchedEntity> matchedEntities) {
		beginEntitiesList.get(index).addAll(matchedEntities);
		for (MatchedEntity me : matchedEntities) {
			int endIndex = me.getEnd();
			endEntitiesList.get(endIndex).add(me);
		}
	}

	
	/**
	 * Returns the size of tokens.
	 *
	 * @return the size of tokens
	 */
	public int getTokenSize() {
		return tokens.length;
	}
	
	public String getSentence() {
		return nlpSentence.getSentence();
	}
	
	private void initData(int size) {
		beginEntitiesList = new ArrayList<ArrayList<MatchedEntity>>(size);
		endEntitiesList = new ArrayList<ArrayList<MatchedEntity>>(size);

		for (int i = 0; i < size; ++i) {
			beginEntitiesList.add(new ArrayList<MatchedEntity>());
			endEntitiesList.add(new ArrayList<MatchedEntity>());
		}
	}
	
	private boolean removeEntity(MatchedEntity me) {
		boolean hasRemoved = beginEntitiesList.get(me.getBegin()).remove(me);
		if (hasRemoved) {
			endEntitiesList.get(me.getEnd()).remove(me);
		}
		return hasRemoved;
	}
	
	private void addEntity(MatchedEntity me) {
		beginEntitiesList.get(me.getBegin()).add(me);
		endEntitiesList.get(me.getEnd()).add(me);
	}
	
	private int removeEntities(String label, int index) {
		List<MatchedEntity> entities = beginEntitiesList.get(index);
		int cnt = 0;
		if (entities != null) {
			for (int i = 0; i < entities.size(); ++i) {
				MatchedEntity entity = entities.get(i);
				if (entity.getLabel().equals(label)) {
					removeEntity(entity);
					--i;
					++cnt;
				}
			}
		}
		return cnt;
	}
	
	private void mergeEntity(MatchedEntity me1, MatchedEntity me2, MatchedEntity merged) {
		removeEntity(me1);
		removeEntity(me2);
		addEntity(merged);
	}
	
	public void initIndex() {		
		size = beginEntitiesList.size();
		beginIndexes = new int[size];
		endIndexes = new int[size];
		
		int index = NO_INDEX;
		for (int i = size-1; i >= 0; --i) {
			if (beginEntitiesList.get(i).size() > 0) {
				index = i;
			}
			beginIndexes[i] = index;
		}
		
		int endIndex = NO_INDEX;
		for (int i = 0; i < size; ++i) {
			if (endEntitiesList.get(i).size() > 0) {
				endIndex = i;
			}
			endIndexes[i] = endIndex;
		}
	}
	
	/**
	 * Returns the nearest index from the <code>begin</code>(including) to 
	 * the end where the entity list is not empty.
	 *
	 * @param begin the begin index
	 * @return the nearest index or <code>NO_INDEX</code> if not exists
	 */
	public int nextIndex(int begin) {
		if (begin < 0 || begin >= beginIndexes.length) {
			return NO_INDEX;
		}
		return beginIndexes[begin];
	}
	
	public Boolean hasNextIndex(int begin) {
		return nextIndex(begin) != NO_INDEX;
	}
	
	public int prevIndex(int begin) {
		if (begin < 0 || begin >= beginIndexes.length) {
			return NO_INDEX;
		}
		return endIndexes[begin];
	}
	
	public Boolean hasPrevIndex(int begin) {
		return prevIndex(begin) != NO_INDEX;
	}
	
	public void mergeResources(Ontology ontology) {
		initIndex();
		
		int size = beginEntitiesList.size();
		// merge 2 entities in those phrases like "city of New York"
		for (int i = 0; i < size ; ++i) {	
			List<MatchedEntity> mes = beginEntitiesList.get(i);
			for (int j = 0; j < mes.size(); ++j) {
				MatchedEntity cur = mes.get(j);
				if (!cur.isClass()) continue;
				
				int end = cur.getEnd();
				int nextBegin = end + 2;
				if (nextBegin < size && tokens[end+1].equals("of")) {
					List<MatchedEntity> nextMes = beginEntitiesList.get(nextBegin);
					for (int k = 0; k < nextMes.size(); ++k) {
						MatchedEntity next = nextMes.get(k);
						if (next.isInstance() 
								&& ontology.isInstanceOf(next.getResource(), cur.getResource())) {
							// merge
							int numTokens = cur.getNumTokens() + next.getNumTokens() + 1;
							String query = StringUtils.join(
									tokens, " ",
									cur.getBegin(), cur.getBegin() + numTokens);
							MatchedEntity mergedMe = new MatchedEntity(
									next.getResource(), next.getLabel(),
									RDFNodeType.INSTANCE, query,
									next.getScore(), cur.getBegin(),
									numTokens);
							mergeEntity(cur, next, mergedMe);
							removeEntities(next.getLabel(), next.getBegin());
							--j;
							break;
						}
					}
				}
			}
		}
		
		// merge 2 entities in those phrases like "New York city"
		// search reversely
		for (int i = size-1; i >= 0; --i) {
			List<MatchedEntity> mes = endEntitiesList.get(i);
			for (int j = 0; j < mes.size(); ++j) {
				MatchedEntity cur = mes.get(j);
				if (!cur.isClass()) continue;
				
				int end = cur.getBegin();
				int nextBegin = end - 1;
				if (nextBegin >= 0) {
					List<MatchedEntity> nextMes = endEntitiesList.get(nextBegin);
					for (int k = 0; k < nextMes.size(); ++k) {
						MatchedEntity next = nextMes.get(k);
						if (next.isInstance() 
								&& ontology.isInstanceOf(next.getResource(), cur.getResource())) {
							// merge
							int numTokens = cur.getNumTokens() + next.getNumTokens();
							String query = StringUtils.join(
									tokens, " ",
									next.getBegin(), next.getBegin() + numTokens);
							MatchedEntity mergedMe = new MatchedEntity(
									next.getResource(), next.getLabel(),
									RDFNodeType.INSTANCE, query,
									next.getScore(), next.getBegin(),
									numTokens);
							//mergeEntity(cur, next, mergedMe);
							//removeEntities(next.getLabel(), next.getBegin());
							//break;
							removeEntities(next.getLabel(), next.getBegin());
							removeEntity(cur);
							addEntity(mergedMe);
							--j;
							break;
						}
					}
				}
			}
		}
		
	}
	
	public ArrayList<MatchedEntity> getEntities(int index) {
		return beginEntitiesList.get(index);
	}
	
	public String toString() {
		int [] idx = new int [this.beginIndexes.length];
		for (int i = 0; i < this.beginIndexes.length; ++i ) {
			idx[i] = i;
		}
		System.out.println ("begis index : " + Arrays.toString(idx));
		System.out.println ("begin index : " + Arrays.toString(this.beginIndexes));
		System.out.println ("end   index : " + Arrays.toString(this.endIndexes));
//		int index = 0;
//		while (this.hasNextIndex(index)) {
//			System.out.print(index + "\t");
//			index = this.nextIndex(index);
//		}
		StringBuffer sb = new StringBuffer();
		sb.append("[\n");
		for (ArrayList<MatchedEntity> entities : beginEntitiesList) {
			if (entities.size() > 0) {
				sb.append(entities).append("\n");
			}
		}
		/*for (int i = 0; i < resourcesList.size(); ++i) {	
			ArrayList<MatchedEntity> resources = resourcesList.get(i);
			ArrayList<MatchedEntity> properties = propertiesList.get(i);
			if (resources.size() == 0 && properties.size() == 0) continue;
			sb.append(tokens[i]).append(": ");
			sb.append(resources).append(properties).append("\n");;
		}*/
		sb.append("]");
		return sb.toString();
	}
}
