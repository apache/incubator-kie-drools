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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.QueryFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;
import static org.kie.kogito.persistence.api.query.SortDirection.ASC;

@ApplicationScoped
public class CounterfactualExplainabilityResultsManagerSlidingWindow implements ExplainabilityResultsManager<CounterfactualExplainabilityResult> {

    private static final Logger LOG = LoggerFactory.getLogger(CounterfactualExplainabilityResultsManagerSlidingWindow.class);

    private final int lengthOfWindow;

    public CounterfactualExplainabilityResultsManagerSlidingWindow(@ConfigProperty(name = "trusty.explainability.counterfactual.results.window.length", defaultValue = "10") int lengthOfWindow) {
        if (lengthOfWindow < 1) {
            throw new IllegalArgumentException("The length of the result window must be at least one.");
        }
        this.lengthOfWindow = lengthOfWindow;
    }

    @Override
    public void purge(String counterfactualId, Storage<String, CounterfactualExplainabilityResult> storage) {
        List<CounterfactualExplainabilityResult> results = new ArrayList<>(storage.query()
                .sort(List.of(orderBy(CounterfactualExplainabilityResult.COUNTERFACTUAL_SEQUENCE_ID_FIELD, ASC)))
                .filter(List.of(QueryFilterFactory.equalTo(CounterfactualExplainabilityResult.COUNTERFACTUAL_ID_FIELD, counterfactualId)))
                .execute());

        //Remove old results from window
        int overflow = results.size() - lengthOfWindow;
        while (overflow > 0) {
            CounterfactualExplainabilityResult result = results.get(--overflow);
            String solutionId = result.getSolutionId();
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Counterfactual results overflow window with size %d by %d.", lengthOfWindow, overflow));
                LOG.info(String.format("Removing stale solution %s, sequence %d", solutionId, result.getSequenceId()));
            }
            storage.remove(solutionId);
        }
    }
}
