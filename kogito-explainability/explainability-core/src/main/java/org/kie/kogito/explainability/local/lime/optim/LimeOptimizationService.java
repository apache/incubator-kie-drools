/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime.optim;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;

/**
 * A service used to perform LIME hyperparameter optimization.
 * <p>
 * See also {@link LimeConfigOptimizer}.
 */
public interface LimeOptimizationService {

    /**
     * Submit a LimeOptimizationRequest.
     *
     * @param limeOptimizationRequest the request to be sumitted
     * @return whether the request has been accepted or not
     */
    boolean submit(LimeOptimizationRequest limeOptimizationRequest);

    /**
     * Obtain the best config for a given LIME explainer.
     *
     * @param limeExplainer the LIME explainer
     * @return the best config available
     */
    LimeConfig getBestConfigFor(LimeExplainer limeExplainer);
}
