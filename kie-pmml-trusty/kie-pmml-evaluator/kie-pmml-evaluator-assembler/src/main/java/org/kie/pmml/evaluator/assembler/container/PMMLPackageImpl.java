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
package org.kie.pmml.evaluator.assembler.container;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kie.api.io.ResourceType;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.container.PMMLPackage;

public class PMMLPackageImpl implements PMMLPackage {

    private Map<String, KiePMMLModel> kiePMMLModelsMap = new HashMap<>();
    private Map<String, KiePMMLModel> kiePMMLModelsByFullClassNameMap = new HashMap<>();

    @Override
    public KiePMMLModel getModelByName(String name) {
        return kiePMMLModelsMap.get(name);
    }

    @Override
    public KiePMMLModel getModelByFullClassName(String fullClassName) {
        return kiePMMLModelsByFullClassNameMap.get(fullClassName);
    }

    /**
     * @return an <b>unmodifiableMap</b> version of the original one
     */
    @Override
    public Map<String, KiePMMLModel> getAllModels() {
        return Collections.unmodifiableMap(kiePMMLModelsMap);
    }

    @Override
    public Map<String, KiePMMLModel> getAllModelsByFullClassName() {
        return Collections.unmodifiableMap(kiePMMLModelsMap);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    @Override
    public void add(KiePMMLModel toAdd) {
        kiePMMLModelsMap.put(toAdd.getName(), toAdd);
        kiePMMLModelsByFullClassNameMap.put(toAdd.getClass().getName(), toAdd);
    }

    @Override
    public void addAll(Collection<KiePMMLModel> toAdd) {
        toAdd.forEach(this::add);
    }

    @Override
    public Iterator<KiePMMLModel> iterator() {
        return kiePMMLModelsMap.values().iterator();
    }
}
