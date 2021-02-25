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
package org.kie.pmml.models.drools.commons.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.HasRule;

/**
 * KIE representation of PMML model that use <b>Drools</b> for implementation
 */
public class KiePMMLDroolsModelWithSources extends KiePMMLDroolsModel implements HasRule {

    protected Map<String, String> sourcesMap;
    protected Map<String, String> rulesSourceMap;
    private final String kmodulePackageName;
    private final String pkgUUID;

    public KiePMMLDroolsModelWithSources(String name,
                                         String kmodulePackageName,
                                         String pkgUUID,
                                         Map<String, String> sourcesMap,
                                         Map<String, String> rulesSourceMap) {
        super(name, Collections.emptyList());
        this.sourcesMap = sourcesMap;
        this.kmodulePackageName = kmodulePackageName;
        this.pkgUUID = pkgUUID;
        this.rulesSourceMap = rulesSourceMap;
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

    @Override
    public String getPkgUUID() {
        return pkgUUID;
    }

    @Override
    public Map<String, String> getRulesSourcesMap() {
        return rulesSourceMap;
    }

}
