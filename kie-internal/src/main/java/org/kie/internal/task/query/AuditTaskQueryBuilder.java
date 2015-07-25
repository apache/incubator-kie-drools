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

package org.kie.internal.task.query;

import java.util.Date;

import org.kie.api.task.model.Status;
import org.kie.internal.task.api.AuditTask;

public interface AuditTaskQueryBuilder extends TaskAuditQueryBuilder<AuditTaskQueryBuilder, AuditTask> {
  
    /**
     * Specify one or more process (definition) id's as criteria in the query
     * @param processId one or more process ids
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder processId(String... processId);
    
    /**
     * Specify one or more task statuses to use as a criteria.
     * @param status one or more task statuses
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder taskStatus(Status... status);

    /**
     * Specify one or more user ids to use as a criteria 
     * for the actual owner of the task as logged in the task event
     * @param actualOwnerUserId one or more (string) user ids
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder actualOwner(String... actualOwnerUserId);
   
    /**
     * Specify one or more deployment ids to use as a criteria 
     * @param deploymentId one or more (string) deployment ids
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder deploymentId(String... deploymentId);
   
    /**
     * Specify one or more dates to use as a criteria 
     * for the created-on date of a task as logged in the task event
     * @param createdOn one or more dates
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder createdOn(Date... createdOn);
 
    /**
     * Specify an inclusive range of created-on dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param createdOnMin the minimal (lower) date to use in the range
     * @param createdOnMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder createdOnRange(Date createdOnMin, Date createdOnMax);
    
    /**
     * Specify one or more (task) parent ids to use as a criteria 
     * @param parentId one or more longs
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder taskParentId(long... parentId);
    
    /**
     * Specify one or more user ids to use as a criteria 
     * for the creator of the task as logged in the task event
     * @param createdByUserId one or more (string) user ids
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder createdBy(String... createdByUserId);
   
    
    /**
     * Specify one or more dates to use as a criteria 
     * for the activation time of the task as logged in the task event
     * @param activationTime one or more dates
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder activationTime(Date... activationTime);
  
    /**
     * Specify an inclusive range of (task) activation-time dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param activationTimeMin the minimal (lower) date to use in the range
     * @param activationTimeMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder activationTimeRange(Date activationTimeMin, Date activationTimeMax);
    
    /**
     * Specify one or more task description to use as a criteria.
     * @param description one or more description string
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder description(String... description);
   
    /**
     * Specify one or more (task) priorities to use as a criteria.
     * @param priority one or more ints
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder priority(int... priority);
    
    /**
     * Specify one or more task names to use as a criteria.
     * @param name one or more string
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder taskName(String... name);
   
    /**
     * Specify one or more process session ids associated with a task 
     * to use as a criteria 
     * @param processSessionId one or more process session ids
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder processSessionId(long... processSessionId);
   
    /**
     * Specify one or more (task) due dates to use as a criteria 
     * for the activation time of the task as logged in the task event
     * @param dueDate one or more dates
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder dueDate(Date... dueDate);
   
    /**
     * Specify an inclusive range of (task) due dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param dueDateMin the minimal (lower) date to use in the range
     * @param dueDateMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder dueDateRange(Date dueDateMin, Date dueDateMax);
    
    /**
     * Specify one or more (task-related) work item ids to use as a criteria.
     * @param workItemId one or more long work item ids
     * @return The current query builder instance
     */
    public AuditTaskQueryBuilder workItemId(long... workItemId);

    /**
     * Specify which field to use when ordering the results, in ascending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order 
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public AuditTaskQueryBuilder ascending( OrderBy field );
    
    /**
     * Specify which field to use when ordering the results, in descending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order 
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public AuditTaskQueryBuilder descending( OrderBy field );
    
    public static enum OrderBy { 
        // order by task id
        taskId, 
        // order by task created-on date
        createdOn, 
        // order by task activation time
        activationTime, 
        // order by process id
        processId,
        // order by process instance id
        processInstanceId, 
    }
}
