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

import java.util.List;

/**
 * Instances of this class can be built by various {@link ParametrizedQueryBuilder} implemetnations, 
 * including those in the jbpm-audit or jbpm-human-task-core modules. 
 * </p>
 * {@link ParametrizedQueryBuilder} implementations are responsible for allowing a user
 * to dynamically (at compile-time) specify the criteria to be used in querying various
 * data sources.
 * </p>
 * The result of the {@link ParametrizedQueryBuilder} is an instance of this class, which contains
 * the criteria specified in the {@link ParametrizedQueryBuilder} instance. When the 
 * {@link ParametrizedQuery#getResultList()} method is called, the specified query will 
 * be executed, returning a (possibly empty) list of the data that fulfills the query criteria. 
 * 
 * @param <T> The type being queried and of the (results) list being returned.
 */
public interface ParametrizedQuery<T> {

    /**
     * Execute the query and return the list of entities found by the query.
     * @return The query result
     */
    List<T> getResultList(); 
    
}
