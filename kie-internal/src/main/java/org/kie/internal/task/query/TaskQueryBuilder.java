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
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.audit.query.ProcessIdQueryBuilder;
import org.kie.internal.task.api.model.SubTasksStrategy;

/**
 * An instance of this class is used to dynamically
 * create a query to retrieve {@link TaskSummary} instances. 
 * </p>
 * One of the main motivations behind this class is that 
 * adding new methods to this method provides a (factorial) 
 * increase in ways to query for {@link TaskSummary} instances 
 * without unnecessarily cluttering up the interface, unlike 
 * the deprecated "get*" method signatures, 
 */
public interface TaskQueryBuilder extends ProcessIdQueryBuilder<TaskQueryBuilder, TaskSummary> {

    /**
     * Add one or more activation times as a criteria to the query
     * @param activationTime one or more {@link Date} values
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder activationTime(Date... activationTime);

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
    public TaskQueryBuilder activationTimeRange(Date activationTimeMin, Date activationTimeMax);

    /**
     * Add one or more (actual) task owner ids as a criteria to the query
     * @param actualOwnerUserId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder actualOwner(String... actualOwnerUserId);

    /**
     * Add whether or not the task is archived as a criteria to the query
     * @param archived a boolean
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder archived(boolean archived);

    /**
     * Add one or more initiator user ids as a criteria to the query
     * </p>
     * The initiator is also the user who created the task.
     * @param createdById
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder createdBy(String... createdById);

    /**
     * Add one or more creation dates as a criteria to the query
     * @param createdOnDate one or more {@link Date} values
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder createdOn(Date... createdOnDate);

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
    public TaskQueryBuilder createdOnRange(Date createdOnMin, Date createdOnMax);
    
    /**
     * Add one or more deployment ids as a criteria to the query
     * @param deploymentId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder deploymentId(String... deploymentId);

    /**
     * Add one or more descriptions as a criteria to the query
     * @param description one or more strings
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder description(String... description);

    /**
     * Add one or more expiration times as a criteria to the query
     * @param expirationTime one or more {@link Date} values
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder expirationTime(Date... expirationTime);

    /**
     * Specify an inclusive range of (task) expiration-time dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param expirationTimeMin the minimal (lower) date to use in the range
     * @param expirationTimeMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public TaskQueryBuilder expirationTimeRange(Date expirationTimeMin, Date expirationTimeMax);

    /**
     * Add one or more (task) form names as a criteria to the query
     * @param formName one or more strings
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder formName(String... formName);

    /**
     * Add one or more (task) names as a criteria to the query
     * @param name one or more strings
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder name(String... name);

    /**
     * Add one or more work item ids as a criteria to the query
     * @param processSessionId one or more longs
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder processSessionId(long... processSessionId);

    /**
     * Add whether or not the task is skippable as a criteria to the query
     * @param skippable a boolean
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder skippable(boolean skippable);

    /**
     * Add one or more statuses as a criteria to the query
     * @param status
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder status(Status... status);

    /**
     * Add one or more subjects as a criteria to the query
     * @param subject one or more strings
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder subject(String... subject);

    /**
     * Add one or more (task) sub-task-strategies as a criteria to the query
     * @param subTasksStrategy one or more {@link SubTasksStrategy} values
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder subTaskStrategy(SubTasksStrategy... subTasksStrategy);

    /**
     * Add one or more task ids as a criteria to the query
     * @param taskId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder taskId(long... taskId); 
  
    /**
     * Specify an inclusive range of (task) ids to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param taskIdMin the minimal (lower) date to use in the range
     * @param taskIdMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public TaskQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax);
    
    /**
     * Add one or more (task) parent ids as a criteria to the query
     * @param taskParentId one or more longs
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder taskParentId(long... taskParentId);

    /**
     * Add one or more (task) types as a criteria to the query
     * @param taskType one or more strings
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder taskType(String... taskType);

    /**
     * Add one or more work item ids as a criteria to the query
     * @param workItemId one or more longs
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder workItemId(long... workItemId);

    /**
     * Add one or more priorities as a criteria to the query
     * @param priority one or more ints
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder priority(int... priority);

    /**
     * Add one or more business administrator (user) ids as a criteria to the query
     * @param businessAdminId one or more strings
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder businessAdmin(String... businessAdminId);

    /**
     * Add one or more potential owner ids as a criteria to the query
     * @param potentialOwnerId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder potentialOwner(String... potentialOwnerId);

    /**
     * Add one or more stake holder (user) ids as a criteria to the query
     * @param stakeHolderId one or more strings
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder stakeHolder(String... stakeHolderId);

    /**
     * Order the results in ascending order by the given parameter
     * </p>
     * results are ordered by default by task id.
     * @param orderBy
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder ascending(OrderBy orderBy);

    /**
     * Order the results in descending order by the given parameter
     * </p>
     * results are ordered by default by task id.
     * @param orderBy
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder descending(OrderBy orderBy);
    
    /**
    /**
     * An enum used to specify the criteria for ordering the results of the query
     */
    public static enum OrderBy { 
        taskId, 
        processInstanceId,
        taskName, 
        taskStatus, 
        createdOn, 
        createdBy;
    }
}