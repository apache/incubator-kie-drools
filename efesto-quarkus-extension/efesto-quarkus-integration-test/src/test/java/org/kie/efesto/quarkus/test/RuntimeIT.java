/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.efesto.quarkus.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.efesto.common.api.model.FRI.SLASH;
import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

@QuarkusTest
public class RuntimeIT {

    @Inject
    RuntimeManager runtimeManager;


    @Test
    public void testBostonHousingTreeEvaluation() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("crim", 0.00632);
        inputData.put("zn", 18);
        inputData.put("indus", 2.31);
        inputData.put("chas", "0");
        inputData.put("nox", 0.538);
        inputData.put("rm", 6.575);
        inputData.put("age", 65.2);
        inputData.put("dis", 4.0900);
        inputData.put("rad", 1);
        inputData.put("tax", 296);
        inputData.put("ptratio", 15.3);
        inputData.put("b", 396.90);
        inputData.put("lstat", 4.98);
        double expectedResult = 27.4272727272727;
        String modelName = "BostonHousingTreeModel";
        PMMLRuntimeContext runtimeContext = getPMMLPMMLContext(UUID.randomUUID().toString(),
                                                            "BostonHousingTree.pmml", "BostonHousingTreeModel",
                                                            inputData);
        String basePath = runtimeContext.getFileNameNoSuffix() + SLASH + getSanitizedClassName(modelName);
        FRI fri = new FRI(basePath, PMML_STRING);
        EfestoInput efestoInput = new EfestoInputPMML(fri, runtimeContext);

        Collection<EfestoOutput> retrieved = testExecution(runtimeManager, runtimeContext, efestoInput, 1);
        EfestoOutput efestoOutput = retrieved.iterator().next();
        assertTrue(efestoOutput instanceof EfestoOutputPMML);
        PMML4Result pmml4Result = ((EfestoOutputPMML)efestoOutput).getOutputData();
        String targetField = "Predicted_medv";
        assertThat(pmml4Result.getResultVariables().get(targetField)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(targetField)).isEqualTo(expectedResult);

    }



    private Collection<EfestoOutput> testExecution(RuntimeManager runtimeManager, EfestoRuntimeContext runtimeContext, EfestoInput efestoInput, int expectedResults) {
        Collection<EfestoOutput> toReturn = runtimeManager.evaluateInput(runtimeContext, efestoInput);
        assertNotNull(toReturn);
        assertEquals(expectedResults, toReturn.size());
        return toReturn;
    }

    private PMMLRuntimeContext getPMMLPMMLContext(String correlationId, String fileName, String modelName, final Map<String, Object> inputData) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(correlationId, modelName, inputData);
        final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        return new PMMLRuntimeContextImpl(pmmlRequestData, fileName, memoryCompilerClassLoader);
    }

    private static PMMLRequestData getPMMLRequestData(String correlationId, String modelName, Map<String, Object> parameters) {
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        return pmmlRequestDataBuilder.build();
    }

}
