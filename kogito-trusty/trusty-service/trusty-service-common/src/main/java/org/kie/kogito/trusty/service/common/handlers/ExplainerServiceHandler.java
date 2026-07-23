/*
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
package org.kie.kogito.trusty.service.common.handlers;

import org.kie.kogito.explainability.api.BaseExplainabilityResult;

/**
 * The handler for a specific type of explanation; decoupling operations specific to type of explanation.
 *
 * @param <R> The Trusty Service explanation type.
 */
public interface ExplainerServiceHandler<R extends BaseExplainabilityResult> {

    /**
     * Checks whether an implementation supports a type of explanation.
     *
     * @param type The Trusty Service result type.
     * @param <T>
     * @return true if the implementation supports the type of explanation.
     */
    <T extends BaseExplainabilityResult> boolean supports(Class<T> type);

    /**
     * Gets the result for an explanation.
     *
     * @param executionId The execution Id for which to retrieve an explanation result.
     * @return The result of an explanation.
     */
    R getExplainabilityResultById(String executionId);

    /**
     * Stores the result for an explanation.
     *
     * @param executionId The execution Id for which to retrieve an explanation result.
     * @param result The result to store.
     */
    void storeExplainabilityResult(String executionId, R result);
}
