/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.pmml;

import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.kogito.prediction.PredictionModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

import static org.kie.kogito.pmml.utils.PMMLUtils.getPMMLRequestData;

public class PmmlPredictionModel implements PredictionModel {

    private final PMMLRuntime pmmlRuntime;
    private final KiePMMLModel pmmlModel;

    public PmmlPredictionModel(PMMLRuntime pmmlRuntime, String modelName) {
        this.pmmlRuntime = pmmlRuntime;
        this.pmmlModel = pmmlRuntime.getModel(modelName).orElseThrow(() -> new IllegalStateException("PMML model '" + modelName + "' not found in the inherent PMMLRuntime."));
    }

    @Override
    public PMMLContext newContext(Map<String, Object> variables) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(pmmlModel.getName(), variables);
        return new PMMLContextImpl(pmmlRequestData);
    }


    @Override
    public PMML4Result evaluateAll(PMMLContext context) {
        return pmmlRuntime.evaluate(pmmlModel.getName(), context);
    }

    @Override
    public KiePMMLModel getKiePMMLModel() {
        return pmmlModel;
    }

}
