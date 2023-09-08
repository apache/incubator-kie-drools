/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.runtime.manager.audit.query;

import org.kie.internal.query.ParametrizedQueryBuilder;
import org.kie.internal.query.ProcessInstanceIdQueryBuilder;

/**
 * This interface defines methods that are used by all of the Audit
 * {@link ParametrizedQueryBuilder} implementations.
 *
 * @param <T> The {@link ParametrizedQueryBuilder} implementation type
 * @param <R> The entity type on which is being queried
 */
public interface ProcessIdQueryBuilder<T,R> extends ProcessInstanceIdQueryBuilder<T,R> {

    /**
     * Specify one or more process (definition) id's as criteria in the query
     * @param processId one or more process ids
     * @return The current query builder instance
     */
    public T processId(String... processId);

}
