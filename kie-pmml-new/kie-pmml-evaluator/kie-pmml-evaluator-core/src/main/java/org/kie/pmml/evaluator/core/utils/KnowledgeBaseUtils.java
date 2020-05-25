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
package org.kie.pmml.evaluator.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeBaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseUtils.class);

    private KnowledgeBaseUtils() {
        // Avoid instantiation
    }

    public static List<KiePMMLModel> getModels(final KieBase knowledgeBase) {
        List<KiePMMLModel> models = new ArrayList<>();
        knowledgeBase.getKiePackages().forEach(kpkg -> {
            PMMLPackage pmmlPackage = (PMMLPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML);
            if (pmmlPackage != null) {
                models.addAll(pmmlPackage.getAllModels().values());
            }
        });
        return models;
    }

    public static Optional<KiePMMLModel> getModel(final KieBase knowledgeBase, String modelName) {
        logger.trace("getModels {} {}", knowledgeBase, modelName);
        return getModels(knowledgeBase)
                .stream()
                .filter(model -> Objects.equals(modelName, model.getName()))
                .findFirst();
    }
}
