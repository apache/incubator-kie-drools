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
import org.kie.internal.query.ParametrizedUpdate;

/**
 * This interface defines methods that are used by all of the Audit delete query builder implementations.
 * @param <T>
 *
 */
public interface AuditDeleteBuilder<T> extends ParametrizedQueryBuilder<T> {

    /**
     * Specify one or more process instance ids as criteria in the query
     * @param processInstanceId one or more a process instance ids
     * @return The current query builder instance
     */
    public T processInstanceId(long... processInstanceId);

    /**
     * Specify one or more process (definition) id's as criteria in the query
     * @param processId one or more process ids
     * @return The current query builder instance
     */
    public T processId(String... processId);

    /**
     * Specify one or more status that process instance should be in as criteria in the query.
     * It might be supported or not
     * @param statuses @see org.kie.api.runtime.process.ProcessInstance
     * @return The current query builder instance
     */
    T logBelongsToProcessInStatus(Integer... statuses);

    /**
     * Specify deployment id that process instance should be in as criteria in the query.
     * It might me supported or not
     * @param the deployment id
     * @return The current query builder instance
     */
    T logBelongsToProcessInDeployment(String deploymentId);

    /**
     * Specify the number of records to be included per transaction.
     * @param numRecords number of records (0 means all records will be processed into one single transaction)
     * @return The current query builder instance
     */
    T recordsPerTransaction(int numRecords);

    /**
     * Create the {@link ParametrizedUpdate} instance that can be used
     * to execute an update or delete of the entities that this builder is for.
     * </p>
     * Further modifications to this builder instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedUpdate}
     * produced by this method.
     *
     * @return a {@link ParametrizedUpdate} instance that can be executed.
     */
    public ParametrizedUpdate build();

}
