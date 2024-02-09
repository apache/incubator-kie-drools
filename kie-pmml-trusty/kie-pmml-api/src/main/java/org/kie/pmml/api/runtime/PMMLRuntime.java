/**
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
package org.kie.pmml.api.runtime;

import java.util.List;
import java.util.Optional;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.models.PMMLModel;

public interface PMMLRuntime {

    /**
     * Evaluate the model, given the context
     * @param modelName the name of the model to evaluate
     * @param context the context with all the input variables
     * @return the result of the evaluation
     */
    PMML4Result evaluate(final String modelName, final PMMLRuntimeContext context);

    /**
     * Returns a list of all models available to this runtime
     * @return the list of available models. An empty list in
     * case no model is available.
     */
    List<PMMLModel> getPMMLModels(final PMMLRuntimeContext context);

    /**
     * Returns the model registered with the given model name.
     * @param modelName the name of the model
     * @return the corresponding an <code>Optional</code> with
     * the <code>PMMLModel</code> retrieved, or an <b>empty</b> one if none
     * is registered with the given name.
     */
    Optional<PMMLModel> getPMMLModel(final String fileName, final String modelName, PMMLRuntimeContext context);

}
