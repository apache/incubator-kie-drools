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
public interface ExtendedParametrizedQueryBuilder<T,R> extends ParametrizedQueryBuilder<T> {

    /**
     * Query criteria which are added to the query after this method
     * are "AND" or "intersection" criteria. In other words, the query
     * will only return results which match all of the criteria added
     * (as opposed to returning results that match <i>any</i> of the
     * criteria added).
     * @return the current query builder instance
     */
    public T newGroup();

    /**
     * Query criteria which are added to the query after this method
     * are "AND" or "intersection" criteria. In other words, the query
     * will only return results which match all of the criteria added
     * (as opposed to returning results that match <i>any</i> of the
     * criteria added).
     * @return the current query builder instance
     */
    public T endGroup();

    /**
     * Create the {@link ParametrizedQuery} instance that can be used
     * to retrieve the results, a {@link List<TaskSummary>} instance.
     * </p>
     * Further modifications to the {@link TaskQueryBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedQuery}
     * produced by this method.
     * @return The results of the query
     */
    public ParametrizedQuery<R> build();
}
