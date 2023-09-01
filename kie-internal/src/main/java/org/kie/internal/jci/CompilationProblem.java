package org.kie.internal.jci;

/**
 * An abstract definition of a compilation problem
 */
public interface CompilationProblem {

    /**
     * is the problem an error and compilation cannot continue
     * or just a warning and compilation can proceed
     * 
     * @return
     */
    boolean isError();

    /**
     * name of the file where the problem occurred
     * 
     * @return
     */
    String getFileName();

    /**
     * position of where the problem starts in the source code
     * 
     * @return
     */
    int getStartLine();
    int getStartColumn();

    /**
     * position of where the problem stops in the source code
     * 
     * @return
     */
    int getEndLine();
    int getEndColumn();

    /**
     * the description of the problem
     * 
     * @return
     */
    String getMessage();

}
