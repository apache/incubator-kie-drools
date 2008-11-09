package org.drools.builder;

public interface KnowledgePackageError {
    
    /**
     * Returns the error message
     */
    String getMessage();
    
    /**
     * Returns the lines of the error in the source file
     * @return
     */
    int[] getErrorLines();
}
