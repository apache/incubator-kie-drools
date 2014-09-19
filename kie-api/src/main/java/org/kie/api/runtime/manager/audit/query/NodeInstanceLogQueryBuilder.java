package org.kie.api.runtime.manager.audit.query;

import java.util.Date;

import org.kie.api.query.ParametrizedQuery;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder.OrderBy;

public interface NodeInstanceLogQueryBuilder extends AuditQueryBuilder<NodeInstanceLogQueryBuilder> {

    /**
     * Specify one or more dates as criteria in the query.
     * @param date one or more dates
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder date(Date... date);
   
    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param date the start (early end) of the date range
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder dateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date. 
     * @param date the end (later end) of the date range
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder dateRangeEnd(Date rangeStart);
   
    /**
     * Specify one or more node instance ids to use as a criteria.
     * @param nodeInstanceId one or more string node instance ids
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder nodeInstanceId(String... nodeInstanceId);
    
    /**
     * Specify one or more node ids to use as a criteria.
     * @param nodeId one or more string node ids
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder nodeId(String... nodeId);
    
    /**
     * Specify one or more node names to use as a criteria.
     * @param nodeInstanceId one or more string node names
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder nodeName(String... name);
    
    /**
     * Specify one or more node types to use as a criteria.
     * @param nodeInstanceId one or more string node types
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder nodeType(String... type);
    
    /**
     * Specify one or more work item ids associated with a node to use as a criteria.
     * @param nodeInstanceId one or more long work item ids
     * @return The current query builder instance
     */
    public NodeInstanceLogQueryBuilder workItemId(long... workItemId);

    /**
     * Specify how the results of the query should be ordered. 
     * </p>
     * If this method is not used, the results will be ordered by the
     * id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public NodeInstanceLogQueryBuilder orderBy( OrderBy field );
   
    public static enum OrderBy { 
        // order by process instance id
        processInstanceId, 
        // order by process id
        processId,
    }
    
    /**
     * Create the {@link ParametrizedQuery} instance that can be used
     * to retrieve the results, a {@link List<NodeInstanceLog>} instance.
     * </p>
     * Further modifications to the {@link NodeInstanceLogQueryBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedQuery} 
     * produced by this method.
     * @return The results of the query
     */
    public ParametrizedQuery<NodeInstanceLog> buildQuery();
}
