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
package org.kie.pmml.evaluator.core.testingutils;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;

import static org.kie.pmml.api.enums.ResultCode.OK;

public class PMMLTestingModelEvaluator implements PMMLModelEvaluator<KiePMMLTestingModel> {
    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.TEST_MODEL;
    }

    @Override
    public PMML4Result evaluate(KiePMMLTestingModel model, PMMLRuntimeContext context) {
        PMML4Result toReturn = new PMML4Result(context.getRequestData().getCorrelationId());
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }
}
