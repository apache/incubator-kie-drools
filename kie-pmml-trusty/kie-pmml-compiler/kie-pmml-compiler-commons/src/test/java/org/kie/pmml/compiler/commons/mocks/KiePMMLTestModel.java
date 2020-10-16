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
package org.kie.pmml.compiler.commons.mocks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * <b>Fake</b> model used for testing. It is mapped to <code>PMML_MODEL.TEST_MODEL</code>
 */
public class KiePMMLTestModel extends KiePMMLModel {

    public static final PMML_MODEL PMML_MODEL_TYPE = PMML_MODEL.TEST_MODEL;

    public KiePMMLTestModel() {
        super(UUID.randomUUID().toString(), Collections.emptyList());
    }

    protected KiePMMLTestModel(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    @Override
    public Object evaluate(final Object knowledgeBase, Map<String, Object> requestData) {
        return null;
    }
}
