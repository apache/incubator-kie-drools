package org.kie.internal.runtime.manager.audit.query;

import java.util.Date;

import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.query.ParametrizedQuery;

public interface VariableInstanceLogQueryBuilder extends AuditQueryBuilder<VariableInstanceLogQueryBuilder>{

    /**
     * Specify one or more dates as criteria in the query
     * @param date one or more dates
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder date(Date... date);
   
    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param date the start (early end) of the date range
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder dateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date. 
     * @param date the end (later end) of the date range
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder dateRangeEnd(Date rangeStart);
   
    /**
     * Specify one or more variable instance ids to use as a criteria.
     * @param variableInstanceId one or more string variable instance ids
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder variableInstanceId(String... variableInstanceId);
   
    /**
     * Specify one or more variable ids to use as a criteria.
     * @param variableId one or more string variable ids
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder variableId(String... variableId);
  
    /**
     * Specify one or more variable values to use as a criteria.
     * @param value one or more string values
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder value(String... value);
   
    /**
     * Specify one or more old (previous) variable values to use as a criteria.
     * @param oldVvalue one or more string old values
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder oldValue(String... oldVvalue);

    /**
     * Specify one or more external ids to use as a criteria. In some cases,
     * the external id is the deployment unit id or runtime manager id.
     * @param externalId one or more string external ids
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder externalId(String... externalId);
    
    /**
     * Only retrieve the most recent ("last") variable instance logs per variable
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder last();
    
    /**
     * Specify how the results of the query should be ordered. 
     * </p>
     * If this method is not used, the results will be ordered by the
     * id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public VariableInstanceLogQueryBuilder orderBy( OrderBy field );
   
    public static enum OrderBy { 
        // order by process instance id
        processInstanceId, 
        // order by process id
        processId,
    }
   
    /**
     * Create the {@link ParametrizedQuery} instance that can be used
     * to retrieve the results, a {@link List<VariableInstanceLog>} instance.
     * </p>
     * Further modifications to the {@link VariableInstanceLogQueryBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedQuery} 
     * produced by this method.
     * @return The results of the query
     */
    public ParametrizedQuery<VariableInstanceLog> buildQuery();
}
