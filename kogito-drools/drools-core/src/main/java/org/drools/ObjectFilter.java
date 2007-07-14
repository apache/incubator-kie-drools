package org.drools;

/**
 * ObjectFilter is used with WorkingMemories to filter out instances during Iteration
 * @author mproctor
 *
 */
public interface ObjectFilter {
    
    /**
     * Returning true means the Iterator accepts, and thus returns, the current Object.
     * @param object
     * @return
     */
    boolean accept(Object object);
}
