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
package  org.kie.pmml.models.clustering.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasSourcesMap;

public class KiePMMLClusteringModelWithSources extends KiePMMLClusteringModel implements HasSourcesMap {

    private static final long serialVersionUID = 193846378174637261L;

    private final String kmodulePackageName;
    protected Map<String, String> sourcesMap;

    public KiePMMLClusteringModelWithSources(String modelName, String kmodulePackageName, Map<String, String> sourcesMap) {
        super(modelName);
        this.sourcesMap = sourcesMap;
        this.kmodulePackageName = kmodulePackageName;
    }

    @Override
    public Object evaluate(final Object knowledgeBase, final Map<String, Object> requestData) {
        throw new KiePMMLException("KiePMMLClusteringModelWithSources is not meant to be used for actual evaluation");
    }

    @Override
    public Map<String, Object> getOutputFieldsMap() {
        throw new KiePMMLException("KiePMMLClusteringModelWithSources is not meant to be used for actual usage");
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
