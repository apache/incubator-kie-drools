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

import org.kie.internal.query.ProcessInstanceIdQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessIdQueryBuilder;
import org.kie.internal.task.api.TaskVariable;
import org.kie.internal.task.api.TaskVariable.VariableType;
import org.kie.internal.task.api.model.TaskEvent;

public interface TaskVariableQueryBuilder extends ProcessIdQueryBuilder<TaskVariableQueryBuilder, TaskVariable> {

    /**
     * Specify one or more task event ids to use as a criteria
     * @param id one or more task event entity ids
     * @return The current query builder instance
     */
    public TaskVariableQueryBuilder id(long... id);

    /**
     * Specify one or more task instance ids to use as a criteria.
     * @param taskId one or more task ids
     * @return The current query builder instance
     */
    public TaskVariableQueryBuilder taskId(long... taskId);

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
    public TaskVariableQueryBuilder taskIdRange(Long taskIdMin, Long taskIdMax);

    /**
     * Specify one or more process (definition) id's as criteria in the query
     * @param processId one or more process ids
     * @return The current query builder instance
     */
    public TaskVariableQueryBuilder processId(String... processId);

    /**
     * Add one or more (task variable) names as a criteria to the query
     * @param name one or more strings
     * @return the current {@link TaskSummaryQueryBuilder} instance
     */
    public TaskVariableQueryBuilder name(String... name);

    /**
     * Add one or more (task variable) values as a criteria to the query
     * @param value one or more strings
     * @return the current {@link TaskSummaryQueryBuilder} instance
     */
    public TaskVariableQueryBuilder value(String... value);

    /**
     * Add one or more (task variable) types as a criteria to the query
     * @param type one or more {@link VariableType} values
     * @return the current {@link TaskSummaryQueryBuilder} instance
     */
    public TaskVariableQueryBuilder type(VariableType... type);

    /**
     * Specify one or more dates to use as a criteria
     * for the modification date of the task variable
     * @param logTime one or more dates
     * @return The current query builder instance
     */
    public TaskVariableQueryBuilder modificationDate(Date... logTime);

    /**
     * Specify an inclusive range of modification dates to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null,
     * then an open-ended range using the non-null range end is used
     * as the criteria
     * @param modDateMin the minimal (lower) date to use in the range
     * @param modDateMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public TaskVariableQueryBuilder modificationDateRange(Date modDateMin, Date modDateMax);
    /**
     * Specify which field to use when ordering the results, in ascending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public TaskVariableQueryBuilder ascending( OrderBy field );

    /**
     * Specify which field to use when ordering the results, in descending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public TaskVariableQueryBuilder descending( OrderBy field );

    public static enum OrderBy {
        // order by id
        id,
        // order by task id
        taskId,
        // order by process instance id
        processInstanceId,
        // order by modificationDate
        modificationDate
    }

}