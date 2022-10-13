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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

import static org.assertj.core.api.Assertions.assertThat;

class PmmlPredictionModelTest {

    private static final PMML4Result PMML_4_RESULT = new PMML4Result();

    private final static String FILE_NAME = "FILE_NAME";
    private final static String MODEL_NAME = "MODEL_NAME";
    private final static PMMLModel PMML_MODEL = new PMMLModelInternal(FILE_NAME, MODEL_NAME);
    private final static PMMLRuntime PMML_RUNTIME = getPMMLRuntime();

    private static PmmlPredictionModel pmmlPredictionModel;

    @BeforeAll
    public static void setup() {
        pmmlPredictionModel = new PmmlPredictionModel(PMML_RUNTIME, FILE_NAME, MODEL_NAME);
        assertThat(pmmlPredictionModel).isNotNull();
    }

    @Test
    void newContext() {
        final Map<String, Object> parameters = getParameters();
        PMMLRuntimeContext retrieved = pmmlPredictionModel.newContext(parameters);
        assertThat(retrieved).isNotNull();
        PMMLRequestData pmmlRequestData = retrieved.getRequestData();
        assertThat(retrieved).isNotNull();
        assertThat(pmmlRequestData.getModelName()).isEqualTo(MODEL_NAME);
        final Map<String, ParameterInfo> parameterInfos = pmmlRequestData.getMappedRequestParams();
        assertThat(parameters).hasSameSizeAs(parameterInfos);
        assertThat(parameters).allSatisfy((key, value) -> {
            assertThat(parameterInfos).containsKey(key);
            ParameterInfo parameterInfo = parameterInfos.get(key);
            assertThat(parameterInfo.getValue()).isEqualTo(value);
            assertThat(parameterInfo.getType()).isEqualTo(value.getClass());
        });
    }

    @Test
    void evaluateAll() {
        final Map<String, Object> parameters = getParameters();
        PMMLRuntimeContext context = pmmlPredictionModel.newContext(parameters);
        assertThat(pmmlPredictionModel.evaluateAll(context)).isEqualTo(PMML_4_RESULT);
    }

    @Test
    void getKiePMMLModel() {
        assertThat(pmmlPredictionModel.getPMMLModel()).isEqualTo(PMML_MODEL);
    }

    private Map<String, Object> getParameters() {
        final Map<String, Object> toReturn = new HashMap<>();
        IntStream.range(0, 3).forEach(i -> {
            toReturn.put("KEY_" + i, "VALUE_" + i);
        });
        return toReturn;
    }

    private static PMMLRuntime getPMMLRuntime() {
        final KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        return new PMMLRuntime() {

            private final List<PMMLModel> models = Collections.singletonList(PMML_MODEL);
            private final Set<PMMLListener> pmmlListeners = new HashSet<>();

            @Override
            public List<PMMLModel> getPMMLModels(PMMLRuntimeContext context) {
                return models;
            }

            @Override
            public Optional<PMMLModel> getPMMLModel(String fileName, String modelName, PMMLRuntimeContext context) {
                return models.stream().filter(model -> model.getFileName().equals(fileName) &&
                        model.getName().equals(modelName))
                        .findFirst();
            }

            @Override
            public PMML4Result evaluate(String s, PMMLRuntimeContext pmmlContext) {
                return PMML_4_RESULT;
            }

        };
    }

    private static class PMMLModelInternal implements PMMLModel {

        private final String fileName;
        private final String name;
        private final List<MiningField> miningFields = Collections.emptyList();
        private final List<OutputField> outputFields = Collections.emptyList();

        public PMMLModelInternal(String fileName, String name) {
            this.fileName = fileName;
            this.name = name;
        }

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<MiningField> getMiningFields() {
            return miningFields;
        }

        @Override
        public List<OutputField> getOutputFields() {
            return outputFields;
        }
    }

}
