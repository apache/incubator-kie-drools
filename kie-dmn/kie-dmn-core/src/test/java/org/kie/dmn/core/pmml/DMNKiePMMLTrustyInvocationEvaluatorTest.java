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
package org.kie.dmn.core.pmml;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.kie.dmn.model.api.DMNElement;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.pmml.api.dto.PMML4Result;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.pmml.DMNKiePMMLTrustyInvocationEvaluator.PMML_FILE_NAME;
import static org.kie.dmn.core.pmml.DMNKiePMMLTrustyInvocationEvaluator.PMML_MODEL_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DMNKiePMMLTrustyInvocationEvaluatorTest {

    private static DMNKiePMMLTrustyInvocationEvaluator dmnKiePMMLTrustyInvocationEvaluator;

    private static final String pmmlFileNameNoSuffix ="LogisticRegression";
    private static final String pmmlFileName = pmmlFileNameNoSuffix + ".pmml";
    private static final String model = "LogisticRegression";

    @BeforeEach
    void setup() throws IOException {
        URL pmmlUrl =  DMNKiePMMLTrustyInvocationEvaluatorTest.class.getResource(pmmlFileName);
        assertThat(pmmlUrl).isNotNull();
        String pmmlFilePath = pmmlUrl.getPath();
        String dmnNS = "dmnNS";
        DMNElement nodeMock = Mockito.mock(DMNElement.class);
        Resource pmmlResourceMock = Mockito.mock(Resource.class);
        when(pmmlResourceMock.getSourcePath()).thenReturn(pmmlFilePath);
        when(pmmlResourceMock.getInputStream()).thenReturn(pmmlUrl.openStream());
        PMMLInfo pmmlInfoMock = Mockito.mock(PMMLInfo.class);
        dmnKiePMMLTrustyInvocationEvaluator = spy(new DMNKiePMMLTrustyInvocationEvaluator(dmnNS,
                                                                                          nodeMock,
                                                                                          pmmlResourceMock,
                                                                                          model,
                                                                                          pmmlInfoMock) {

            protected Optional<String> getOutputFieldNameFromInfo(String resultName) {
                return Optional.of(resultName);
            }

            protected Map<String, Object> getPMMLRequestData(String correlationId, String modelName, String fileName,
                                                         DMNResult dmnr) {
                return getPMMLRequestDataCommon(correlationId, modelName, fileName);
            }
        });
    }

    @Test
    void getPMML4Result() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DMNRuntime dmnRuntimeMock = mock(DMNRuntime.class);
        when(dmnRuntimeMock.getRootClassLoader()).thenReturn(classLoader);
        DMNRuntimeEventManager eventManagerMock = mock(DMNRuntimeEventManager.class);
        when(eventManagerMock.getRuntime()).thenReturn(dmnRuntimeMock);
        DMNResult dmnrMock = mock(DMNResult.class);
        dmnKiePMMLTrustyInvocationEvaluator.getPMMLResult(eventManagerMock, dmnrMock);
        verify(dmnKiePMMLTrustyInvocationEvaluator,
               times(1)).evaluate(model, pmmlFileName, dmnrMock, classLoader);
    }

//    @Test
//    public void getOutputFieldValues() {
//        List<Object> values = getValues();
//        Map<String, Object> resultVariables = new HashMap<>();
//        for (int i = 0; i < values.size(); i++) {
//            resultVariables.put("Element-" + i, values.get(i));
//        }
//        Map<String, Object> retrieved = dmnKiePMMLTrustyInvocationEvaluator.getOutputFieldValues(new PMML4Result(),
//                                                                                                 resultVariables,
//                                                                                                 null);
//        resultVariables.forEach((s, value) -> {
//            assertThat(retrieved).containsKey(s);
//            Object retObject = retrieved.get(s);
//            Object expected = EvalHelper.coerceNumber(value);
//            assertThat(retObject).isEqualTo(expected);
//        });
//    }

//    @Test
//    public void getPredictedValues() {
//        List<Object> values = getValues();
//        values.forEach(value -> {
//            PMML4Result result = getPMML4Result(value);
//            Map<String, Object> retrieved = dmnKiePMMLTrustyInvocationEvaluator.getPredictedValues(result, null);
//            assertThat(retrieved).containsKey(result.getResultObjectName());
//            Object retObject = retrieved.get(result.getResultObjectName());
//            Object expected = EvalHelper.coerceNumber(value);
//            assertThat(retObject).isEqualTo(expected);
//        });
//    }

    @Test
    void evaluate() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DMNRuntime dmnRuntimeMock = mock(DMNRuntime.class);
        when(dmnRuntimeMock.getRootClassLoader()).thenReturn(classLoader);
        DMNRuntimeEventManager eventManagerMock = mock(DMNRuntimeEventManager.class);
        when(eventManagerMock.getRuntime()).thenReturn(dmnRuntimeMock);
        DMNResult dmnrMock = mock(DMNResult.class);
        Map<String, Object> retrieved = dmnKiePMMLTrustyInvocationEvaluator.evaluate(model, pmmlFileName, dmnrMock,
                                                                                     classLoader);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void compileFile() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Map<String, GeneratedResources> retrieved = dmnKiePMMLTrustyInvocationEvaluator.compileFile(pmmlFileName, classLoader);
        assertThat(retrieved).isNotNull().isNotEmpty().containsKey("pmml");
    }

    private static Map<String, Object> getPMMLRequestDataCommon(String correlationId, String modelName,
                                                                String fileName) {
        Map<String, Object> toReturn = new HashMap<>();
        List<String> fields = Arrays.asList("variance", "skewness", "curtosis", "entropy");
        Random random = new Random();
        fields.forEach(field -> {
            double value = (double)random.nextInt(100)/10;
            toReturn.put(field, value);
        });
        toReturn.put(PMML_FILE_NAME, fileName);
        toReturn.put(PMML_MODEL_NAME, modelName);
        return toReturn;
    }

    private List<Object> getValues() {
        Random random = new Random();
        return Stream.of(random.nextInt(),
                         random.nextDouble(),
                         random.nextFloat(),
                         random.nextLong(),
                         new Date())
                .collect(Collectors.toList());
    }

    private PMML4Result getPMML4Result(Object value) {
        PMML4Result toReturn = new PMML4Result();
        String resultName = "resultName";
        toReturn.setResultObjectName(resultName);
        toReturn.addResultVariable(resultName, value);
        return toReturn;
    }
}