/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.commons.testingutility;

import java.util.List;
import java.util.Map;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

/**
 * <b>Fake</b> model used for testing. It is mapped to <code>PMML_MODEL.TEST_MODEL</code>
 */
public class KiePMMLTestingModel extends KiePMMLModel {

        public static PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TEST_MODEL;
        private static final long serialVersionUID = 9009765353822151536L;

        public KiePMMLTestingModel(String name, List<KiePMMLExtension> extensions) {
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

            public Builder withTransformationDictionary(final KiePMMLTransformationDictionary transformationDictionary) {
                toBuild.transformationDictionary = transformationDictionary;
                return this;
            }

            public Builder withLocalTransformations(final KiePMMLLocalTransformations localTransformations) {
                toBuild.localTransformations = localTransformations;
                return this;
            }
        }
    }