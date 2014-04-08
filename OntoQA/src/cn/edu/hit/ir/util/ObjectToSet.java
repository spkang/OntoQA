/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.util;

/**
 * A utility class from LingPipe.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-25
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * An <code>ObjectToSet</code> provides a {@link java.util.Map} from
 * arbitrary objects to objects of class {@link java.util.Set}.
 * In addition to methods inherited from {@link java.util.Map},
 * there are methods to add members to a set value and get
 * set values directly.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @param <K> the type of keys for this mapping
 * @param <M> the type of members stored in the value sets for this
 * map
 */
public class ObjectToSet<K,M> extends HashMap<K,Set<M>> {

    static final long serialVersionUID = -5758024598554958671L;

    /**
     * Construct a new object to set mapping.
     */
    public ObjectToSet() {
        super();
    }

    /**
     * Add a member to the value of the set mapped to by
     * the specified key.
     *
     * @param key Key whose set value will have the object.
     * @param member Object to add to the value of the key.
     */
    public void addMember(K key, M member) {
        if (containsKey(key)) {
            get(key).add(member);
        } else {
            HashSet<M> val = new HashSet<M>();
            val.add(member);
            put(key,val);
        }
    }

    /**
     * Removes the specified value from the set of values assigned to
     * the specified key.  If it is the last key, it removes the mapping
     * altogether.
     *
     * @param key Key whose members will have the value removed.
     * @param member Value to remove from the set of members assigned
     * to the key.
     * @return <code>true</code> if the value was in the set of
     * members for the key.
     */
    public boolean removeMember(K key, M member) {
        if (!containsKey(key)) return false;
        boolean result = get(key).remove(member);
        if (get(key).size() == 0)
            remove(key);
        return result;
    }

    /**
     * Adds a set of members to the value of the set mapped to by
     * the specified key.
     *
     * @param key Key whose set value will have the object.
     * @param values Values to add to the set picked out by the key.
     */
    public void addMembers(K key, Set<? extends M> values) {
        Set<M> memberSet = get(key);
        if (memberSet == null)
            put(key,new HashSet<M>(values));
        else
            memberSet.addAll(values);
    }


    /**
     * Returns a set constituting the union of all of the members of
     * the set values for all of the keys.
     *
     * @return The union of the members of the set values.
     */
    public Set<M> memberValues() {
        Set<M> set = new HashSet<M>();
        for (Set<M> members : values())
            set.addAll(members);
        return set;
    }

    /**
     * Returns the set of values for the specified key, or the empty
     * set if there have been none added.  This behavior only differs
     * from the basic {@link #get(Object)} method in that it returns the
     * empty set rather than <code>null</code>.
     */
    public Set<M> getSet(K key) {
        Set<M> result = get(key);
        return result != null
            ? result
            : new HashSet<M>(0);
    }
}

