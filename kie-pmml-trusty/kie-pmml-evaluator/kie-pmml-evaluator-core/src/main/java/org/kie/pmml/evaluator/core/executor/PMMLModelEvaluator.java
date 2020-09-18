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
package org.kie.pmml.evaluator.core.executor;

import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.evaluator.api.executor.PMMLContext;

public interface PMMLModelEvaluator<E extends KiePMMLModel> {

    /**
     * @return the <code>PMMLModelType</code> this <code>PMMLModelExecutor</code>
     * is specific for
     */
    PMML_MODEL getPMMLModelType();

    /**
     * Evaluate the model, given the context
     * @param knowledgeBase The <code>KieBase</code> we are currently working on.
     * It may be <code>null</code> for testing purpose for <b>not drools-related</b> models
     * @param model the model to evaluate
     * @param context the context with all the input variables
     * @return the result of the evaluation
     * @throws KiePMMLInternalException
     */
    PMML4Result evaluate(final KieBase knowledgeBase, final E model, final PMMLContext context);
}
