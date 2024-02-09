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
package org.kie.pmml.evaluator.core.executor;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;

public interface PMMLModelEvaluator<E extends KiePMMLModel> {

    /**
     * @return the <code>PMMLModelType</code> this <code>PMMLModelExecutor</code>
     * is specific for
     */
    PMML_MODEL getPMMLModelType();

    /**
     * Evaluate the model, given the context
     * It may be <code>null</code> for testing purpose for <b>not drools-related</b> models
     * @param model the model to evaluate
     * @param context the context with all the input variables
     * @return the result of the evaluation
     * @throws KiePMMLInternalException
     */
    PMML4Result evaluate(final E model, final PMMLRuntimeContext context);
}
