/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.runtime.manager.audit.query;

import java.util.Date;

import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.task.api.AuditTask;

public interface AuditTaskInstanceLogQueryBuilder extends AuditQueryBuilder<AuditTaskInstanceLogQueryBuilder> {

    /**
     * Specify one or more dates as criteria in the query.
     * @param date one or more dates
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder date(Date... date);
   
    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param date the start (early end) of the date range
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder dateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date. 
     * @param date the end (later end) of the date range
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder dateRangeEnd(Date rangeStart);
   
    /**
     * Specify one or more task instance ids to use as a criteria.
     * @param taskId one or more string node instance ids
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder taskId(long... taskId);
    
    
    /**
     * Specify one or more task names to use as a criteria.
     * @param name one or more string node names
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder taskName(String... name);
    
    /**
     * Specify one or more task names to use as a criteria.
     * @param name one or more string node names
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder taskStatus(String... status);
    
    /**
     * Specify one or more task description to use as a criteria.
     * @param description one or more description string
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder description(String... description);
    
    /**
     * Specify one or more work item ids associated with a node to use as a criteria.
     * @param nodeInstanceId one or more long work item ids
     * @return The current query builder instance
     */
    public AuditTaskInstanceLogQueryBuilder workItemId(long... workItemId);

    /**
     * Specify how the results of the query should be ordered. 
     * </p>
     * If this method is not used, the results will be ordered by the
     * id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public AuditTaskInstanceLogQueryBuilder orderBy( OrderBy field );
   
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
     * Further modifications to the {@link AuditTaskInstanceLogQueryBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedQuery} 
     * produced by this method.
     * @return The results of the query
     */
    public ParametrizedQuery<AuditTask> buildQuery();
}
