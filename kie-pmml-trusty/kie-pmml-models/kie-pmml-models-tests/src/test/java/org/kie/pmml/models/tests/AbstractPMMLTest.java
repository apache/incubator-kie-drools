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
package org.kie.pmml.models.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;
import org.kie.pmml.evaluator.core.service.PMMLRuntimeInternalImpl;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;
import org.kie.pmml.evaluator.utils.SPIUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.util.FileUtils.getFile;
import static org.kie.pmml.commons.Constants.PMML_SUFFIX;

public class AbstractPMMLTest {

    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = SPIUtils.getPMMLRuntimeFactory(false);

    protected static PMMLRuntime getPMMLRuntime(String fileName) {
        fileName += PMML_SUFFIX;
        File pmmlFile = getFile(fileName);
        return PMML_RUNTIME_FACTORY.getPMMLRuntimeFromFile(pmmlFile);
    }

    protected static PMMLRequestData getPMMLRequestData(String modelName, Map<String, Object> parameters) {
        String correlationId = "CORRELATION_ID";
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        return pmmlRequestDataBuilder.build();
    }

    protected PMML4Result evaluate(final PMMLRuntime pmmlRuntime,
                                   final Map<String, Object> inputData,
                                   final String fileName,
                                   final String modelName) {
        return evaluate(pmmlRuntime, inputData, fileName, modelName, Collections.emptySet());
    }

    protected PMML4Result evaluate(final PMMLRuntime pmmlRuntime,
                                   final Map<String, Object> inputData,
                                   final String fileName,
                                   final String modelName,
                                   final Set<PMMLListener> pmmlListeners) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        PMMLRuntimeContext context = new PMMLRuntimeContextImpl(pmmlRequestData, fileName, pmmlListeners,
                                                             memoryCompilerClassLoader);
        context.getGeneratedResourcesMap().putAll(((PMMLRuntimeInternalImpl)pmmlRuntime).getGeneratedResourcesMap());
        return pmmlRuntime.evaluate(modelName, context);
    }

    protected PMMLListenerTest getPMMLListener() {
        return new PMMLListenerTest();
    }

    protected void commonValidateListeners(final Set<PMMLListener> toValidate, final List<PMMLStep> expectedSteps) {
        toValidate.forEach(listener -> commonValidateListener((PMMLListenerTest) listener, expectedSteps));
    }

    private void commonValidateListener(final PMMLListenerTest toValidate, final List<PMMLStep> expectedSteps) {
        assertThat(toValidate.getSteps()).isEqualTo(expectedSteps);
    }

    protected static class PMMLListenerTest implements PMMLListener {

        private List<PMMLStep> steps = new ArrayList<>();

        public List<PMMLStep> getSteps() {
            return steps;
        }

        @Override
        public void stepExecuted(PMMLStep step) {
            steps.add(step);
        }
    }

}
