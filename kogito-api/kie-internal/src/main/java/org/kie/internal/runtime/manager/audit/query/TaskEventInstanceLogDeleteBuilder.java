package org.kie.internal.runtime.manager.audit.query;

import java.util.Date;

import org.kie.internal.query.ParametrizedUpdate;

public interface TaskEventInstanceLogDeleteBuilder extends AuditQueryBuilder<TaskEventInstanceLogDeleteBuilder>{

    /**
     * Specify one or more dates as criteria in the query
     * @param date one or more dates
     * @return The current query builder instance
     */
    public TaskEventInstanceLogDeleteBuilder date(Date... date);
   
    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param date the start (early end) of the date range
     * @return The current query builder instance
     */
    public TaskEventInstanceLogDeleteBuilder dateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date. 
     * @param date the end (later end) of the date range
     * @return The current query builder instance
     */
    public TaskEventInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart);

    /**
     * Create the {@link ParametrizedUpdate} instance that can be used
     * to execute update/delete of {@link AuditTask} instances.
     * </p>
     * Further modifications to the {@link ProcessInstanceLogDeleteBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedUpdate} 
     * produced by this method.
     * @return The results of the update/delete
     */
    public ParametrizedUpdate build();

}
