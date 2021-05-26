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

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Before;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

public class PMMLRuntimeInternalImplTest {

    private PMMLRuntimeInternalImpl pmmlRuntime;

    @Before
    public void setUp() {
        pmmlRuntime = new PMMLRuntimeInternalImpl(null, null);
    }

    public static class KiePMMLTestingModel extends KiePMMLModel {

        public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.REGRESSION_MODEL;
        private static final long serialVersionUID = 9009765353822151536L;

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

            public Builder withKiePMMLTargets(List<KiePMMLTarget> kiePMMLTargets) {
                toBuild.kiePMMLTargets = kiePMMLTargets;
                return this;
            }

            public Builder withFunctionsMap(final Map<String, BiFunction<List<KiePMMLNameValue>, Object, Object>> functionsMap) {
                toBuild.functionsMap = functionsMap;
                return this;
            }
        }
    }
}
