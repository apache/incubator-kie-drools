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

package org.kie.pmml.runtime.api.executor;

import java.util.List;
import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;

public interface PMMLRuntime {

    /**
     * Returns a list of all models available to this runtime
     *
     * @return the list of available models. An empty list in
     *         case no model is available.
     */
    List<KiePMMLModel> getModels();

    /**
     * Returns the model registered with the given model name.
     *
     * @param modelName the name of the model
     *
     * @return the corresponding an <code>Optional</code> with
     * the <code>KiePMMLModel</code> retrieved, or an <b>empty</b> one if none
     * is registered with the given name.
     */
    Optional<KiePMMLModel> getModel(String modelName );


    /**
     * Evaluate the model, given the context
     *
     * @param model the model to evaluate
     * @param context the context with all the input variables
     * @param releaseId Used to indirectly retrieve same instance of kiecontainer
     *
     * @return the result of the evaluation
     */
    PMML4Result evaluate(KiePMMLModel model, PMMLContext context, String releaseId ) throws KiePMMLException;


}
