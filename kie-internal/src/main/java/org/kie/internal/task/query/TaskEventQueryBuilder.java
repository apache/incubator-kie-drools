/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.kie.internal.query.ExtendedParametrizedQueryBuilder;
import org.kie.internal.query.ProcessInstanceIdQueryBuilder;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.internal.task.api.model.TaskEvent.TaskEventType;

public interface TaskEventQueryBuilder extends ProcessInstanceIdQueryBuilder<TaskEventQueryBuilder, TaskEvent> {

    /**
     * Specify one or more messages to use as a criteria.
     * @param message one or more strings
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder message(String... message);

    /**
     * Specify one or more task instance ids to use as a criteria.
     * @param taskId one or more task ids
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder taskId(long... taskId);

    /**
     * Specify an inclusive range of task ids to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null,
     * then an open-ended range using the non-null range end is used
     * as the criteria
     * @param taskIdMin the minimal (lower) taskId to use in the range
     * @param taskIdMax the max (upper) taskId to use in the range
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax);

    /**
     * Specify one or more task event ids to use as a criteria
     * @param id one or more task event entity ids
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder id(long... id);

    /**
     * Specify one or more dates to use as a criteria
     * for the log (creation) date of the task event
     * @param logTime one or more dates
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder logTime(Date... logTime);

    /**
     * Specify an inclusive range of log dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null,
     * then an open-ended range using the non-null range end is used
     * as the criteria
     * @param logTimeMin the minimal (lower) date to use in the range
     * @param logTimeMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder logTimeRange(Date logTimeMin, Date logTimeMax);

    /**
     * Specify one or more user ids to use as a criteria
     * @param userId one or more (string) user ids
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder userId(String... userId);

    /**
     * Specify one or more task event types to use as a criteria
     * @param taskEventType one or more {@link TaskEventType}s
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder type(TaskEventType... taskEventType);

    /**
     * Specify one or more (task-related) work item ids to use as a criteria.
     * @param workItemId one or more long work item ids
     * @return The current query builder instance
     */
    public TaskEventQueryBuilder workItemId(long... workItemId);

    /**
     * Specify which field to use when ordering the results, in ascending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public TaskEventQueryBuilder ascending( OrderBy field );

    /**
     * Specify which field to use when ordering the results, in descending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public TaskEventQueryBuilder descending( OrderBy field );

    public static enum OrderBy {
        // order by task id
        taskId,
        // order by task event date
        logTime,
        // order by process instance id
        processInstanceId,
    }

}