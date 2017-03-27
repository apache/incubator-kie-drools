/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.api.core;

import org.kie.dmn.api.core.event.DMNRuntimeEventManager;

import java.util.List;

public interface DMNRuntime extends DMNRuntimeEventManager {

    /**
     * Returns a list of all models available to this runtime
     *
     * @return the list of available models. An empty list in
     *         case no model is available.
     */
    List<DMNModel> getModels();

    /**
     * Returns the model registered with the given namespace and
     * model name.
     *
     * @param namespace the name space for the model
     * @param modelName the name of the model
     *
     * @return the corresponding DMN model, or null if none is
     *         registered with the given name and namespace.
     */
    DMNModel getModel( String namespace, String modelName );

    /**
     * Evaluate all decisions for the model, given the context
     *
     * @param model the model to evaluate
     * @param context the context with all the input variables
     *
     * @return the result of the evaluation
     */
    DMNResult evaluateAll( DMNModel model, DMNContext context );

    /**
     * Evaluate the decision identified by the given name and
     * all dependent decisions, given the context
     *
     * @param model the model to evaluate
     * @param decisionName the root decision to evaluate, identified
     *                     by name
     * @param context the context with all the input variables
     *
     * @return the result of the evaluation
     */
    DMNResult evaluateDecisionByName(DMNModel model, String decisionName, DMNContext context );

    /**
     * Evaluate the decision identified by the given ID and
     * all dependent decisions, given the context
     *
     * @param model the model to evaluate
     * @param decisionId the root decision to evaluate, identified
     *                   by ID
     * @param context the context with all the input variables
     *
     * @return the result of the evaluation
     */
    DMNResult evaluateDecisionById(DMNModel model, String decisionId, DMNContext context );

    /**
     * Creates a new empty DMNContext
     *
     * @return a new empty DMNContext
     */
    DMNContext newContext();

}
