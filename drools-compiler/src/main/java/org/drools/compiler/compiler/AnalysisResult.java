package org.drools.compiler.compiler;

import java.util.Set;

/**
 * An interface with the results from the expression/block analysis
 */
public interface AnalysisResult {

    /**
     * Returns the Set<String> of all used identifiers
     * 
     * @return
     */
    Set<String> getIdentifiers();

    /**
     * Returns the array of lists<String> of bound identifiers
     * 
     * @return
     */
    BoundIdentifiers getBoundIdentifiers();

    /**
     * Returns the Set<String> of not bounded identifiers
     * 
     * @return
     */
    Set<String> getNotBoundedIdentifiers();

    /**
     * Returns the Set<String> of declared local variables
     * 
     * @return
     */
    Set<String> getLocalVariables();

    Class<?> getReturnType();

    default boolean isTypesafe() {
        return true;
    }
}
