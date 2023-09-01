package org.kie.internal.runtime.manager.audit.query;


public interface NodeInstanceLogDeleteBuilder extends AuditDateDeleteBuilder<NodeInstanceLogDeleteBuilder> {

    /**
     * Specify one or more work item ids associated with a node to use as a criteria.
     * @param workItemId one or more long work item ids
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder workItemId(long... workItemId);

    /**
     * Specify one or more node instance ids to use as a criteria.
     * @param nodeInstanceId one or more string node instance ids
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder nodeInstanceId(String... nodeInstanceId);

    /**
     * Specify one or more node ids to use as a criteria.
     * @param nodeId one or more string node ids
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder nodeId(String... nodeId);

    /**
     * Specify one or more node names to use as a criteria.
     * @param name one or more string node names
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder nodeName(String... name);

    /**
     * Specify externalId to be used as criteria on the externalId field.
     * @param externalId identifier that defines custom id
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder externalId(String... externalId);

}
