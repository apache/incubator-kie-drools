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
package org.kie.pmml.models.regression.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasSourcesMap;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html>Regression</a>
 */
public class KiePMMLRegressionModelWithSources extends KiePMMLRegressionModel implements HasSourcesMap {

    private final String kmodulePackageName;
    protected Map<String, String> sourcesMap;

    public KiePMMLRegressionModelWithSources(String modelName, String kmodulePackageName, Map<String, String> sourcesMap) {
        super(modelName);
        this.sourcesMap = sourcesMap;
        this.kmodulePackageName = kmodulePackageName;
    }

    @Override
    public Object evaluate(final Object knowledgeBase, Map<String, Object> requestData) {
        throw new KiePMMLException("KiePMMLRegressionModelWithSources. is not meant to be used for actual evaluation");
    }

    @Override
    public Map<String, Object> getOutputFieldsMap() {
        throw new KiePMMLException("KiePMMLRegressionModelWithSources. is not meant to be used for actual usage");
    }

    @Override
    public Map<String, String> getSourcesMap() {
        return Collections.unmodifiableMap(sourcesMap);
    }

    @Override
    public void addSourceMap(String key, String value) {
        sourcesMap.put(key, value);
    }

    @Override
    public String getKModulePackageName() {
        return kmodulePackageName;
    }
}
