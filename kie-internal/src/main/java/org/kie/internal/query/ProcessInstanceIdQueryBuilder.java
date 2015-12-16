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

package org.kie.internal.query;





/**
 * This is the base interface for all {@link ParametrizedQueryBuilder} implementations. 
 * </p>
 * It includes the basic query functions.
 *
 * @param <T> The type of {@link ParametrizedQueryBuilder} instance being implemented. This type
 * is here to facilitate the building of a fluent interface. 
 * @param <R> The type of the result list being returned by the generated {@link ParametrizedQuery}
 */
public interface ProcessInstanceIdQueryBuilder<T,R> extends ExtendedParametrizedQueryBuilder<T,R> {

    /**
     * Specify one or more process instance ids as criteria in the query
     * @param processInstanceId one or more a process instance ids
     * @return The current query builder instance
     */
    public T processInstanceId(long... processInstanceId);
    
    /**
     * Specify an inclusive range of process instance ids to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null, 
     * then an open-ended range using the non-null range end is used 
     * as the criteria
     * @param processInstanceIdMin the minimal (lower) date to use in the range
     * @param processInstanceIdMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public T processInstanceIdRange(Long processInstanceIdMin, Long processInstanceIdMax);
    
}