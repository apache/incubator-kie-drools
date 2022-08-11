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
package org.kie.kogito.pmml;

import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.kogito.prediction.PredictionModel;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;

import static org.kie.kogito.pmml.PMMLKogito.modelByName;
import static org.kie.kogito.pmml.utils.PMMLUtils.getPMMLRequestData;

public class PmmlPredictionModel implements PredictionModel {

    private final PMMLRuntime pmmlRuntime;
    private final PMMLModel pmmlModel;

    public PmmlPredictionModel(PMMLRuntime pmmlRuntime, String fileName, String modelName) {
        this.pmmlRuntime = pmmlRuntime;
        this.pmmlModel = modelByName(pmmlRuntime, fileName, modelName);
        if (this.pmmlModel == null) {
            String exceptionString = String.format("PMML model %s@%s not found in the inherent " +
                    "PMMLRuntime.", modelName, fileName);
            throw new IllegalStateException(exceptionString);
        }
    }

    @Override
    public PMMLRuntimeContext newContext(Map<String, Object> variables) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(pmmlModel.getName(), variables);
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        return new PMMLRuntimeContextImpl(pmmlRequestData, pmmlModel.getFileName(), memoryCompilerClassLoader);
    }

    @Override
    public PMML4Result evaluateAll(PMMLRuntimeContext context) {
        return pmmlRuntime.evaluate(pmmlModel.getName(), context);
    }

    @Override
    public PMMLModel getPMMLModel() {
        return pmmlModel;
    }

}
