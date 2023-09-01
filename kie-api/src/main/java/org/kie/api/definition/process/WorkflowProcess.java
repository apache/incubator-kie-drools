package org.kie.api.definition.process;

/**
 * A WorkflowProcess is a type of Process that uses a flow chart (as a collection of Nodes and Connections)
 * to model the business logic.
 */
public interface WorkflowProcess
    extends
    Process,
    NodeContainer {

}
