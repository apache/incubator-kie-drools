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
package org.kie.pmml.models.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryImpl;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.test.util.filesystem.FileUtils.getFile;

public class AbstractPMMLTest {

    private static final PMMLRuntimeFactory PMML_RUNTIME_FACTORY = new PMMLRuntimeFactoryImpl();

    protected static PMMLRuntime getPMMLRuntime(String fileName) {
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
                                   final String modelName) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        return pmmlRuntime.evaluate(modelName, new PMMLContextImpl(pmmlRequestData));
    }

    protected PMML4Result evaluate(final PMMLRuntime pmmlRuntime,
                                   final Map<String, Object> inputData,
                                   final String modelName,
                                   final Set<PMMLListener> pmmlListeners) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        return pmmlRuntime.evaluate(modelName, new PMMLContextImpl(pmmlRequestData, pmmlListeners));
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
