package org.kie.api.event.process;

import java.util.List;

/**
 * An event when a variable inside a process instance has been changed.
 */
public interface ProcessVariableChangedEvent
    extends
    ProcessEvent {

    /**
     * The unique id of the process variable (definition).
     *
     * @return the variable id
     */
    String getVariableId();

    /**
     * The unique id of the process variable instance (as multiple node instances with the
     * same process variable definition exists).  This is an aggregation of the unique id of
     * the instance that contains the variable scope and the variable id.
     *
     * @return the variable instance id
     */
    String getVariableInstanceId();

    /**
     * The old value of the variable.
     * This may be null.
     *
     * @return the old value
     */
    Object getOldValue();

    /**
     * The new value of the variable.
     * This may be null.
     *
     * @return the new value
     */
    Object getNewValue();
    
    /**
     * List of tags associated with variable that is being changed.
     * @return list of tags if there are any otherwise empty list
     */
    List<String> getTags();

    /**
     * Determines if variable that is being changed has given tag associated with it
     * @param tag name of the tag
     * @return returns true if given tag is associated with variable otherwise false
     */
    boolean hasTag(String tag);

}
