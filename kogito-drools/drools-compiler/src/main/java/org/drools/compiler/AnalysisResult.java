package org.drools.compiler;

import java.util.Set;

/**
 * An interface with the results from the expression/block analysis
 * 
 * @author etirelli
 */
public interface AnalysisResult {

    /**
     * Returns the Set<String> of all used identifiers
     * 
     * @return
     */
    public Set<String> getIdentifiers();

    /**
     * Returns the array of lists<String> of bound identifiers
     * 
     * @return
     */
    public BoundIdentifiers getBoundIdentifiers();

    /**
     * Returns the Set<String> of not bounded identifiers
     * 
     * @return
     */
    public Set<String> getNotBoundedIdentifiers();

    /**
     * Returns the Set<String> of declared local variables
     * 
     * @return
     */
    public Set<String> getLocalVariables();

}