/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.trusty.service.common.handlers;

import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.Decision;

/**
 * The handler for a specific type of explanation; decoupling operations specific to type of explanation.
 * 
 * @param <R> The Trusty Service explanation type.
 * @param <D> The Explainability Service explanation type.
 */
public interface ExplainerServiceHandler<R extends BaseExplainabilityResult, D extends BaseExplainabilityResultDto> {

    /**
     * Checks whether an implementation supports a type of explanation.
     * 
     * @param type The Trusty Service result type.
     * @param <T>
     * @return true if the implementation supports the type of explanation.
     */
    // See https://issues.redhat.com/browse/FAI-457
    // We will not need the overloaded generic class when we only have one type of DTO
    <T extends BaseExplainabilityResult> boolean supports(Class<T> type);

    /**
     * Checks whether an implementation supports a type of explanation.
     * 
     * @param type The Explainability Service result type.
     * @param <T>
     * @return true if the implementation supports the type of explanation.
     */
    // See https://issues.redhat.com/browse/FAI-457
    <T extends BaseExplainabilityResultDto> boolean supportsDto(Class<T> type);

    /**
     * Converts the result from the Explainability Service to that used by Trusty Service.
     * 
     * @param dto The result from the Explainability Service
     * @param decision The decision for which the explaination was requested
     * @return The result used by Trusty Service
     */
    R explainabilityResultFrom(D dto, Decision decision);

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
