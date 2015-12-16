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

import org.kie.internal.query.ProcessInstanceIdQueryBuilder;


public interface TaskAuditQueryBuilder<T, R> extends ProcessInstanceIdQueryBuilder<T, R> {
   
    /**
     * Specify one or more task instance ids to use as a criteria.
     * @param taskId one or more task ids
     * @return The current query builder instance
     */
    public T taskId(long... taskId);
    
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
    public T taskIdRange(Long taskIdMin, Long taskIdMax);
   
    /**
     * Specify one or more task event ids to use as a criteria 
     * @param id one or more task event entity ids
     * @return The current query builder instance
     */
    public T id(long... id);
   
}
