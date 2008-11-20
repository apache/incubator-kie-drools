package org.drools.builder;

/**
 * A reported error during the build process.
 *
 */
public interface KnowledgeBuilderError {

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
