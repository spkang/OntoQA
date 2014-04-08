/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import cn.edu.hit.ir.ontology.RDFNodeType;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Entity class.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class Entity {
	
	private Resource resource;
	
	private String label;
	
	private Tokenizer tokenizer;
	
	private String[] tokens;
	
	private RDFNodeType type;
	
	public Entity(Resource resource, String label, RDFNodeType type) {
		if (resource == null || label == null) {
			throw new NullPointerException("resource or label shouldn't be null");
		}
		setResource(resource);
		setTokenizer(new DefaultTokenizer());
		setLabel(label);
		setType(type);
	}
	
	/*public Entity(Resource resource, String label) {
		this(resource, label, false);
	}*/

	/**
	 * Get the resource.
	 *
	 * @return The resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Set the resource.
	 *
	 * @param resource The resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Get the label.
	 *
	 * @return The label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label.
	 *
	 * @param label The label to set
	 */
	public void setLabel(String label) {
		this.label = label;
		setTokens(tokenizer.tokenize(label));
	}
	
	/**
	 * Get the tokenizer.
	 *
	 * @return The tokenizer
	 */
	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	/**
	 * Set the tokenizer.
	 *
	 * @param tokenizer The tokenizer to set
	 */
	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	/**
	 * Set the tokens. Notice that the access level is <code>protected</code>.
	 * It's hoped that this function can only be invoked in {@link #setLabel(String)}
	 * after the {@link #label} is updated.
	 * 
	 * @param tokens The tokens to set
	 */
	protected void setTokens(String[] tokens) {
		this.tokens = tokens;
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
	 * Returns the size of tokens.
	 *
	 * @return the size of tokens
	 */
	public int getTokenSize() {
		return tokens.length;
	}
	
	/**
	 * Returns specified token through index.
	 *
	 * @param index the index
	 * @return the token or null if the index is out of bounds
	 */
	public String getToken(int index) {
		if (index < 0 || index >= tokens.length) {
			return null;
		}
		return tokens[index];
	}

	/**
	 * Set the type.
	 *
	 * @param type The type to set
	 */
	public void setType(RDFNodeType type) {
		this.type = type;
	}

	/**
	 * Get the type.
	 *
	 * @return The type
	 */
	public RDFNodeType getType() {
		return type;
	}

	public boolean isClass() {
		return type == RDFNodeType.CLASS;
	}
	
	public boolean isInstance() {
		return type == RDFNodeType.INSTANCE;
	}
	
	public boolean isProperty() {
		return type == RDFNodeType.PROPERTY;
	}
	
	
	public String toString() {
		return resource.toString();
	}
}
