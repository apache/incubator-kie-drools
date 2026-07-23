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
package org.kie.kogito.trusty.service.common;

import java.time.OffsetDateTime;
import java.util.List;

import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.trusty.service.common.models.MatchedExecutionHeaders;
import org.kie.kogito.trusty.storage.api.model.ModelMetadata;
import org.kie.kogito.trusty.storage.api.model.ModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;

/**
 * The trusty service interface.
 * <p>
 * The service exposes the api to CRUD the executions.
 */
public interface TrustyService {

    /**
     * Gets all the headers of the executions that were evaluated within a specified time range.
     *
     * @param from The start datetime.
     * @param to The end datetime.
     * @param limit The maximum (non-negative) number of items to be returned.
     * @param offset The non-negative pagination offset.
     * @param prefix The executionId prefix to be matched in the search.
     * @return The execution headers that satisfy the time range, pagination and prefix conditions and the total number of available results.
     */
    MatchedExecutionHeaders getExecutionHeaders(OffsetDateTime from, OffsetDateTime to, int limit, int offset, String prefix);

    /**
     * Stores a decision.
     *
     * @param executionId The unique execution ID
     * @param decision The decision object.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the executionId is already present in the system.
     */
    void storeDecision(String executionId, Decision decision);

    /**
     * Gets a decision by execution ID.
     *
     * @param executionId The execution ID.
     * @return The decision.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the executionId is not present in the system.
     */
    Decision getDecisionById(String executionId);

    /**
     * Updates a decision. If the decision is not present in the storage, then it is created.
     *
     * @param executionId The execution ID
     * @param decision The decision object.
     */
    void updateDecision(String executionId, Decision decision);

    /**
     * Process a decision. Stores the decision and then send an explainability request if it is enabled.
     *
     * @param executionId The execution ID
     * @param decision The decision object.
     */
    void processDecision(String executionId, Decision decision);

    /**
     * Store the explainability result.
     *
     * @param executionId The execution ID.
     */
    <T extends BaseExplainabilityResult> void storeExplainabilityResult(String executionId, T result);

    /**
     * Gets a explainability result by execution ID.
     *
     * @param executionId The execution ID.
     * @param type The type of explanation to lookup.
     * @return The explainability result.
     */
    <T extends BaseExplainabilityResult> T getExplainabilityResultById(String executionId, Class<T> type);

    /**
     * Stores a Model definition.
     * 
     * @param modelWithMetadata The DMNModel to be stored.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the model is already present in the
     *         system.
     */
    <T extends ModelMetadata, E extends ModelWithMetadata<T>> void storeModel(E modelWithMetadata);

    /**
     * Gets a model by model id.
     *
     * @param modelMetadata The model metadata.
     * @param modelWithMetadataClass: The actual <b>Class</b> of the <code>ModelWithMetadata</code> to return
     * @return The model definition.
     * @throws IllegalArgumentException Throws IllegalArgumentException in case the modelId is not present in the
     *         system.
     */
    <T extends ModelMetadata, E extends ModelWithMetadata<T>> E getModelById(T modelMetadata, Class<E> modelWithMetadataClass);

    /**
     * Requests calculation of the Counterfactuals for an execution.
     *
     * @param executionId The execution ID.
     * @param goals The outputs that are desired from the Counterfactual calculation.
     * @param searchDomains The domains that the Counterfactual calculation can search.
     * @return A empty Counterfactual representing the request.
     * @throws IllegalArgumentException Throws IllegalArgumentException the executionId is not present in the system.
     */
    CounterfactualExplainabilityRequest requestCounterfactuals(String executionId,
            List<NamedTypedValue> goals,
            List<CounterfactualSearchDomain> searchDomains);

    /**
     * Get all Counterfactual requests for an execution.
     *
     * @param executionId The execution ID.
     * @return A list of all of the Counterfactuals for the execution.
     * @throws IllegalArgumentException Throws IllegalArgumentException the executionId is not present in the system.
     */
    List<CounterfactualExplainabilityRequest> getCounterfactualRequests(String executionId);

    /**
     * Gets a specific Counterfactual request for an execution.
     *
     * @param executionId The execution ID.
     * @param counterfactualId The Counterfactual ID.
     * @return A specific Counterfactual request for the execution.
     * @throws IllegalArgumentException Throws IllegalArgumentException the executionId or counterfactualId are not present in the system.
     */
    CounterfactualExplainabilityRequest getCounterfactualRequest(String executionId, String counterfactualId);

    /**
     * Gets the specific Counterfactual results for an execution.
     *
     * @param executionId The execution ID.
     * @param counterfactualId The Counterfactual ID.
     * @return The specific Counterfactual results for the execution or an empty Collection.
     */
    List<CounterfactualExplainabilityResult> getCounterfactualResults(String executionId, String counterfactualId);
}
