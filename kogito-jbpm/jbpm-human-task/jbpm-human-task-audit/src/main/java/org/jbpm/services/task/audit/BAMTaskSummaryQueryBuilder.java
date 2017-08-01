/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.audit;

import java.util.Date;

import org.jbpm.services.task.audit.BAMTaskSummaryQueryBuilder.OrderBy;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.kie.api.task.model.Status;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.query.TaskAuditQueryBuilder;

public interface BAMTaskSummaryQueryBuilder extends TaskAuditQueryBuilder<BAMTaskSummaryQueryBuilder, BAMTaskSummaryImpl> {
   
    /**
     * Specify one or more (task) start dates to use as a criteria.
     * @param startDate one or more dates
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder startDate(Date... startDate);

    /**
     * Specify an inclusive range of (task) start dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param startDateMin the minimal (lower) date to use in the range
     * @param startDateMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder startDateRange(Date startDateMin, Date startDateMax);
    
    /**
     * Specify one or more duration to use as a criteria 
     * @param duration one or more durations
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder duration(long... duration);
  
    /**
     * Specify one or more task statuses to use as a criteria.
     * @param status one or more task statuses
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder taskStatus(Status... status);
   
    /**
     * Specify one or more user ids to use as a criteria 
     * @param userId one or more (string) user ids
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder userId(String... userId);
    
    /**
     * Specify one or more (task) end dates to use as a criteria.
     * @param endDate one or more dates
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder endDate(Date... endDate);
    
    /**
     * Specify an inclusive range of (task) end dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param endDateMin the minimal (lower) date to use in the range
     * @param endDateMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder endDateRange(Date endDateMin, Date endDateMax);
   
    /**
     * Specify one or more dates to use as a criteria 
     * for the created-on date of a task as logged in the task event
     * @param createdOn one or more dates
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder createdOn(Date... createdOn);
 
    /**
     * Specify an inclusive range of (task) creation dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param createdOnMin the minimal (lower) date to use in the range
     * @param createdOnMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder createdOnRange(Date createdOnMin, Date createdOnMax);
    
    /**
     * Specify one or more task names to use as a criteria.
     * @param name one or more string names
     * @return The current query builder instance
     */
    public BAMTaskSummaryQueryBuilder taskName(String... name);
    
    /**
     * Specify which field to use when ordering the results, in ascending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order 
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public BAMTaskSummaryQueryBuilder ascending( OrderBy field );
    
    /**
     * Specify which field to use when ordering the results, in descending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order 
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public BAMTaskSummaryQueryBuilder descending( OrderBy field );
    
    public static enum OrderBy { 
        // order by task id
        taskId, 
        // order by task start date
        startDate, 
        // order by task end date
        endDate, 
        // order by task creation date
        createdDate, 
        // order by task name
        taskName, 
        // order by process instance id
        processInstanceId, 
    }
}
