/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.storage.common;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.trusty.storage.api.model.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;

public interface TrustyStorageService {

    String DECISIONS_STORAGE = "decisions";
    String EXPLAINABILITY_RESULTS_STORAGE = "explainability-results";
    String MODELS_STORAGE = "models";

    /**
     * Gets the decision storage.
     *
     * @return The Storage for decisions.
     */
    Storage<String, Decision> getDecisionsStorage();

    /**
     * Gets the explainability result storage.
     *
     * @return The Storage for explainability results.
     */
    Storage<String, ExplainabilityResult> getExplainabilityResultStorage();

    /**
     * Gets the model definition storage.
     *
     * @return The Storage for model definitions.
     */
    Storage<String, DMNModelWithMetadata> getModelStorage();
}
