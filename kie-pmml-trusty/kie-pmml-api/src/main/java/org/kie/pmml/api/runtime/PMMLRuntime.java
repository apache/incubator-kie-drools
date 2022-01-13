/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.api.runtime;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.models.PMMLModel;

public interface PMMLRuntime {

    /**
     * Evaluate the model, given the context
     * @param modelName the name of the model to evaluate
     * @param context the context with all the input variables
     * @return the result of the evaluation
     */
    PMML4Result evaluate(final String modelName, final PMMLContext context);

    /**
     * Returns a list of all models available to this runtime
     * @return the list of available models. An empty list in
     * case no model is available.
     */
    List<PMMLModel> getPMMLModels();

    /**
     * Returns the model registered with the given model name.
     * @param modelName the name of the model
     * @return the corresponding an <code>Optional</code> with
     * the <code>PMMLModel</code> retrieved, or an <b>empty</b> one if none
     * is registered with the given name.
     */
    Optional<PMMLModel> getPMMLModel(final String modelName);

    /**
     * Add the given <code>PMMLListener</code> to the current <code>PMMLRuntime</code>
     * That listener, in turn, will be added to any <code>PMMLContext</code> passed
     * to the <code>evaluate</code> method
     * @param toAdd
     */
    void addPMMLListener(final PMMLListener toAdd);

    /**
     * Remove the given <code>PMMLListener</code> from the current <code>PMMLRuntime</code>.
     * That listener, in turn, will not be added anymore to <code>PMMLContext</code>s passed
     * to the <code>evaluate</code> method
     * @param toRemove
     */
    void removePMMLListener(final PMMLListener toRemove);

    /**
     * Returns an <b>unmodifiable set</b> of the <code>PMMLListener</code>s registered with the
     * current instance
     */
    Set<PMMLListener> getPMMLListeners();
}
