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

import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.persistence.api.Storage;

public interface ExplainabilityResultsManager<R extends BaseExplainabilityResult> {

    /**
     * Purge the results storage of any unwanted entries.
     *
     * @param counterfactualId The counterfactual request Id.
     * @param storage The results storage.
     */
    void purge(String counterfactualId, Storage<String, R> storage);
}
