package org.kie.api.runtime;

/**
 * ObjectFilter is used with WorkingMemories to filter out instances during Iteration
 */
public interface ObjectFilter {

    /**
     * @return true if the Iterator accepts, and thus returns, the current Object. Otherwise false.
     */
    boolean accept(Object object);
}
