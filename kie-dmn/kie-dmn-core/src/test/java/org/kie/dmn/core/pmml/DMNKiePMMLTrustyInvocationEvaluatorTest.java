/*
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.kie.dmn.model.api.DMNElement;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.pmml.DMNKiePMMLTrustyInvocationEvaluator.PMML_FILE_NAME;
import static org.kie.dmn.core.pmml.DMNKiePMMLTrustyInvocationEvaluator.PMML_MODEL_NAME;
import static org.kie.dmn.core.pmml.DMNKiePMMLTrustyInvocationEvaluator.RESULT_OBJECT_NAME;
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
    private static ModelLocalUriId pmmlModelLocalUriId;

    @BeforeEach
    void setup() throws IOException {
        pmmlModelLocalUriId = compilePmml();
        String dmnNS = "dmnNS";
        DMNElement nodeMock = Mockito.mock(DMNElement.class);
        PMMLInfo pmmlInfoMock = Mockito.mock(PMMLInfo.class);

        dmnKiePMMLTrustyInvocationEvaluator = spy(new DMNKiePMMLTrustyInvocationEvaluator(dmnNS,
                                                                                          nodeMock,
                                                                                          pmmlModelLocalUriId,
                                                                                          model,
                                                                                          pmmlInfoMock) {

            protected Optional<String> getOutputFieldNameFromInfo(String resultName) {
                return Optional.of(resultName);
            }

            protected Map<String, Object> getPMMLRequestData(String correlationId, String modelName, String fileName,
                                                         DMNResult dmnr) {
                return getPMMLRequestDataCommon(modelName, fileName);
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
               times(1)).evaluate(model, dmnrMock, classLoader);
    }

    @Test
    public void getOutputFieldValues() {
        Map<String, Object> resultVariables = getMappedValues();
        Map<String, Object> retrieved = dmnKiePMMLTrustyInvocationEvaluator.getOutputFieldValues(resultVariables,
                                                                                                 null);
        resultVariables.forEach((s, value) -> {
            assertThat(retrieved).containsKey(s);
            Object retObject = retrieved.get(s);
            Object expected = NumberEvalHelper.coerceNumber(value);
            assertThat(retObject).isEqualTo(expected);
        });
    }

    @Test
    public void getPredictedValues() {
        final Map<String, Object> values = getMappedValues();
        values.forEach((key, value) -> {
            Map<String, Object> resultVariables = new HashMap<>(values);
            resultVariables.put(RESULT_OBJECT_NAME, key);
            Map<String, Object> retrieved = dmnKiePMMLTrustyInvocationEvaluator.getPredictedValues(resultVariables, null);
            assertThat(retrieved).containsKey(key);
            Object retObject = retrieved.get(key);
            Object expected = NumberEvalHelper.coerceNumber(value);
            assertThat(retObject).isEqualTo(expected);
        });
    }

    @Test
    void evaluate() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        DMNRuntime dmnRuntimeMock = mock(DMNRuntime.class);
        when(dmnRuntimeMock.getRootClassLoader()).thenReturn(classLoader);
        DMNRuntimeEventManager eventManagerMock = mock(DMNRuntimeEventManager.class);
        when(eventManagerMock.getRuntime()).thenReturn(dmnRuntimeMock);
        DMNResult dmnrMock = mock(DMNResult.class);
        Map<String, Object> retrieved = dmnKiePMMLTrustyInvocationEvaluator.evaluate(model, dmnrMock,
                                                                                     classLoader);
        assertThat(retrieved).isNotNull();
    }

//    @Test
//    void compileFile() {
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        Map<String, GeneratedResources> retrieved = dmnKiePMMLTrustyInvocationEvaluator.compileFile(pmmlFileName, classLoader);
//        assertThat(retrieved).isNotNull().isNotEmpty().containsKey("pmml");
//    }

    private static ModelLocalUriId compilePmml() throws IOException {
        URL pmmlUrl =  DMNKiePMMLTrustyInvocationEvaluatorTest.class.getResource(pmmlFileName);
        assertThat(pmmlUrl).isNotNull();
        File pmmlFile = new File(pmmlUrl.getFile());
        assertThat(pmmlFile).isNotNull().exists();
        return EfestoPMMLUtils.compilePMML(pmmlFile, Thread.currentThread().getContextClassLoader());
    }

    private static Map<String, Object> getPMMLRequestDataCommon(String modelName,
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

    private  Map<String, Object> getMappedValues() {
        List<Object> values = getValues();
        AtomicInteger counter = new AtomicInteger(0);
        return values.stream().collect(Collectors.toMap(o -> "Element-" + counter.getAndAdd(1),
                                                        o -> o));
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

}