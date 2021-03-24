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
package org.kie.pmml.evaluator.core.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.evaluator.core.PMMLContextImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PMMLRuntimeInternalImplTest {

    private PMMLRuntimeInternalImpl pmmlRuntime;

    @Before
    public void setUp() {
        pmmlRuntime = new PMMLRuntimeInternalImpl(null, null);
    }

    @Test
    public void addMissingValuesReplacements() {
        Map<String, Object> missingValueReplacementMap = new HashMap<>();
        missingValueReplacementMap.put("fieldA", "one");
        missingValueReplacementMap.put("fieldB", 2);
        KiePMMLTestingModel model = KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withMissingValueReplacementMap(missingValueReplacementMap)
                .build();
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", "modelName");
        pmmlRequestData.addRequestParam("age", 123);
        pmmlRequestData.addRequestParam("work", "work");
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        missingValueReplacementMap.keySet().forEach(key -> {
            assertFalse(pmmlContext.getRequestData().getMappedRequestParams().containsKey(key));
            assertFalse(pmmlContext.getMissingValueReplacedMap().containsKey(key));
        });
        pmmlRuntime.addMissingValuesReplacements(model, pmmlContext);
        missingValueReplacementMap.forEach((key, value) -> {
            assertTrue(pmmlContext.getRequestData().getMappedRequestParams().containsKey(key));
            final ParameterInfo<?> parameterInfo = pmmlContext.getRequestData().getMappedRequestParams().get(key);
            assertEquals(key, parameterInfo.getName());
            assertEquals(value.getClass(), parameterInfo.getType());
            assertEquals(value, parameterInfo.getValue());
            assertTrue(pmmlContext.getMissingValueReplacedMap().containsKey(key));
            assertEquals(value, pmmlContext.getMissingValueReplacedMap().get(key));
        });
    }

    @Test
    public void executeTargets() {
        // Build model
        String TARGET_NAME = "TARGET_NAME";
        String FIELD_NAME = "FIELD_NAME";
        KiePMMLTarget kiePMMLTarget = KiePMMLTarget.builder(TARGET_NAME, Collections.emptyList())
                .withMin(4.34)
                .withField(FIELD_NAME)
                .build();
        List<KiePMMLTarget> kiePMMLTargets = Arrays.asList(kiePMMLTarget, KiePMMLTarget.builder("NEW_TARGET", Collections.emptyList()).build());
        KiePMMLTestingModel model = KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withKiePMMLTargets(kiePMMLTargets)
                .build();
        // Build PMML4Result
        PMML4Result toModify = new PMML4Result();
        toModify.setResultCode(ResultCode.FAIL.getName());
        toModify.addResultVariable(FIELD_NAME, 4.33);
        assertEquals(4.33, toModify.getResultVariables().get(FIELD_NAME));
        pmmlRuntime.executeTargets(toModify, model);
        assertEquals(4.33, toModify.getResultVariables().get(FIELD_NAME));
        toModify.setResultCode(ResultCode.OK.getName());
        pmmlRuntime.executeTargets(toModify, model);
        assertEquals(4.33, toModify.getResultVariables().get(FIELD_NAME));
        toModify.setResultObjectName(FIELD_NAME);
        pmmlRuntime.executeTargets(toModify, model);
        assertEquals(4.34, toModify.getResultVariables().get(FIELD_NAME));
    }

    private static class KiePMMLTestingModel extends KiePMMLModel {

        public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.REGRESSION_MODEL;

        private KiePMMLTestingModel(String name, List<KiePMMLExtension> extensions) {
            super(name, extensions);
        }

        public static Builder builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
            return new Builder(name, extensions, miningFunction);
        }

        @Override
        public Object evaluate(final Object knowledgeBase, Map<String, Object> requestData) {
            return null;
        }

        public static class Builder extends KiePMMLModel.Builder<KiePMMLTestingModel> {

            private Builder(String name, List<KiePMMLExtension> extensions, MINING_FUNCTION miningFunction) {
                super("TestingModel-", PMML_MODEL_TYPE, miningFunction, () -> new KiePMMLTestingModel(name, extensions));
            }

            Builder withKiePMMLTargets(List<KiePMMLTarget> kiePMMLTargets) {
                toBuild.kiePMMLTargets = kiePMMLTargets;
                return this;
            }
        }
    }
}
