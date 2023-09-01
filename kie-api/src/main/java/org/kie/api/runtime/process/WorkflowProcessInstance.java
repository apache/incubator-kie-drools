package org.kie.api.runtime.process;

/**
 * A workflow process instance represents one specific instance of a
 * workflow process that is currently executing.  It is an extension
 * of a <code>ProcessInstance</code> and contains all runtime state
 * related to the execution of workflow processes.
 *
 * @see org.kie.api.runtime.process.ProcessInstance
 */
public interface WorkflowProcessInstance
    extends
    ProcessInstance,
    NodeInstanceContainer {

    /**
     * Returns the value of the variable with the given name.  Note
     * that only variables in the process-level scope will be searched.
     * Returns <code>null</code> if the value of the variable is null
     * or if the variable cannot be found.
     *
     * @param name the name of the variable
     * @return the value of the variable, or <code>null</code> if it cannot be found
     */
    Object getVariable(String name);

    void setVariable(String name, Object value);

}
