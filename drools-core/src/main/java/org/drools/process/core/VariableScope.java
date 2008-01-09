package org.drools.process.core;

import java.util.List;


/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface VariableScope {

    /**
     * Returns the variables used in this scope
     * 
     * @return  a list of variables of this scope
     */
    List<Variable> getVariables();

    /**
     * Returns the names of the variables used in this scope
     * 
     * @return  the variable names of this scope
     */
    String[] getVariableNames();
    
    /**
     * Sets the variables used in this scope
     * 
     * @param variables the variables
     * @throws IllegalArugmentException if <code>variables</code> is null
     */
    void setVariables(List<Variable> variables);
    
}
