package org.kie.internal.task.api;

import java.util.Date;

/**
 * Represents single Task variable entity
 *
 */
public interface TaskVariable {

    public enum VariableType {
        INPUT,
        OUTPUT;
    }

    /**
     * Returns task id that this variable belongs to
     * @return
     */
    Long getTaskId();

    /**
     * Returns process instance id that the task this variable belongs to is owned by
     * This might be null in case ad hoc tasks
     * @return
     */
    Long getProcessInstanceId();

    /**
     * Returns process id that the task this variable belongs to is owned by
     * This might be null in case ad hoc tasks
     * @return
     */
    String getProcessId();

    /**
     * Returns name of the variable
     * @return
     */
    String getName();

    /**
     * Returns value of this variable - its string representation that can be queried
     * @return
     */
    String getValue();

    /**
     * Return type of the variable - either input or output
     * @return
     */
    VariableType getType();

    /**
     * Returns last time this variable was modified
     * @return
     */
    Date getModificationDate();

    void setTaskId(Long taskId);

    void setProcessInstanceId(String processInstanceId);

    void setProcessId(String processId);

    void setName(String name);

    void setValue(String value);

    void setType(VariableType type);

    void setModificationDate(Date modificationDate);
}
