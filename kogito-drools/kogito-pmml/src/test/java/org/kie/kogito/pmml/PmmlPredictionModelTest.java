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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PmmlPredictionModelTest {

    private static final PMML4Result  PMML_4_RESULT = new PMML4Result();
    private final static String MODEL_NAME = "MODEL_NAME";
    private final static PMMLModel PMML_MODEL = new PMMLModelInternal(MODEL_NAME);
    private final static PMMLRuntime PMML_RUNTIME = getPMMLRuntime();

    private static PmmlPredictionModel pmmlPredictionModel;

    @BeforeAll
    public static void setup() {
        pmmlPredictionModel = new PmmlPredictionModel(PMML_RUNTIME, MODEL_NAME);
        assertNotNull(pmmlPredictionModel);
    }


    @Test
    void newContext() {
        final Map<String, Object> parameters = getParameters();
        PMMLContext retrieved = pmmlPredictionModel.newContext(parameters);
        assertNotNull(retrieved);
        PMMLRequestData pmmlRequestData = retrieved.getRequestData();
        assertNotNull(retrieved);
        assertEquals(MODEL_NAME,  pmmlRequestData.getModelName());
        final Map<String, ParameterInfo> parameterInfos = pmmlRequestData.getMappedRequestParams();
        assertEquals(parameters.size(), parameterInfos.size());
        parameters.forEach((key, value) -> {
            assertTrue(parameterInfos.containsKey(key));
            ParameterInfo parameterInfo = parameterInfos.get(key);
            assertEquals(value, parameterInfo.getValue());
            assertEquals(value.getClass(), parameterInfo.getType());
        });
    }

    @Test
    void evaluateAll() {
        final Map<String, Object> parameters = getParameters();
        PMMLContext context = pmmlPredictionModel.newContext(parameters);
        assertEquals(PMML_4_RESULT, pmmlPredictionModel.evaluateAll(context));
    }

    @Test
    void getKiePMMLModel() {
        assertEquals(PMML_MODEL, pmmlPredictionModel.getPMMLModel());
    }

    private Map<String, Object> getParameters() {
        final Map<String, Object> toReturn = new HashMap<>();
        IntStream.range(0, 3).forEach(i -> {
            toReturn.put("KEY_" + i, "VALUE_" + i);
        });
        return toReturn;
    }

    private static PMMLRuntime getPMMLRuntime() {
        return new PMMLRuntime() {

            private final List<PMMLModel> models = Collections.singletonList(PMML_MODEL);

            @Override
            public List<PMMLModel> getPMMLModels() {
                return models;
            }

            @Override
            public Optional<PMMLModel> getPMMLModel(String s) {
                return models.stream().filter(model -> model.getName().equals(s)).findFirst();
            }

            @Override
            public PMML4Result evaluate(String s, PMMLContext pmmlContext) {
                return PMML_4_RESULT;
            }

        };
    }

    private static class PMMLModelInternal implements PMMLModel {

        private final String name;
        private final List<MiningField> miningFields = Collections.emptyList();
        private final List<OutputField> outputFields = Collections.emptyList();

        public PMMLModelInternal(String name) {
            this.name = name;
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