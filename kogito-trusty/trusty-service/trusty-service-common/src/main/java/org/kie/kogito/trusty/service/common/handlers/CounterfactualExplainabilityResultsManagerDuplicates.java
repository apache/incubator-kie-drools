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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.QueryFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;
import static org.kie.kogito.persistence.api.query.SortDirection.ASC;

@ApplicationScoped
public class CounterfactualExplainabilityResultsManagerDuplicates implements ExplainabilityResultsManager<CounterfactualExplainabilityResult> {

    private static final Logger LOG = LoggerFactory.getLogger(CounterfactualExplainabilityResultsManagerDuplicates.class);

    private ObjectMapper mapper;

    CounterfactualExplainabilityResultsManagerDuplicates() {
        //CDI proxy
    }

    @Inject
    public CounterfactualExplainabilityResultsManagerDuplicates(final ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public void purge(String counterfactualId, Storage<String, CounterfactualExplainabilityResult> storage) {
        List<CounterfactualExplainabilityResult> results = new ArrayList<>(storage.query()
                .sort(List.of(orderBy(CounterfactualExplainabilityResult.COUNTERFACTUAL_SEQUENCE_ID_FIELD, ASC)))
                .filter(List.of(QueryFilterFactory.equalTo(CounterfactualExplainabilityResult.COUNTERFACTUAL_ID_FIELD, counterfactualId)))
                .execute());

        if (results.size() < 2) {
            return;
        }

        final CounterfactualExplainabilityResult latestResult = results.get(results.size() - 1);
        final CounterfactualExplainabilityResult penultimateResult = results.get(results.size() - 2);
        final boolean inputsEqual = equals(latestResult.getInputs(), penultimateResult.getInputs());
        final boolean outputsEqual = equals(latestResult.getOutputs(), penultimateResult.getOutputs());

        if (inputsEqual && outputsEqual) {
            LOG.info("The latest two Counterfactual results are equal. Removing duplicate.");
            //It's plausible, although unlikely, that the FINAL result could have been received before the last INTERMEDIATE.
            if (latestResult.getStage().equals(CounterfactualExplainabilityResult.Stage.FINAL)) {
                removeSolution(storage, penultimateResult);
            } else {
                removeSolution(storage, latestResult);
            }
        }
    }

    private boolean equals(Object o1, Object o2) {
        final JsonNode node1 = mapper.valueToTree(o1);
        final JsonNode node2 = mapper.valueToTree(o2);
        return node1.equals(node2);
    }

    private void removeSolution(Storage<String, CounterfactualExplainabilityResult> storage,
            CounterfactualExplainabilityResult result) {
        LOG.info(String.format("Removing duplicate solution %s, sequence %d",
                result.getSolutionId(),
                result.getSequenceId()));
        storage.remove(result.getSolutionId());
    }
}
