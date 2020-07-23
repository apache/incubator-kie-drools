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

import org.drools.compiler.lang.descr.PackageDescr;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasSourcesMap;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

/**
 * KIE representation of PMML model that use <b>Drools</b> for implementation
 */
public class KiePMMLDroolsModelWithSources extends KiePMMLDroolsModel implements HasSourcesMap {

    protected Map<String, String> sourcesMap;
    private final String kmodulePackageName;
    private final PackageDescr packageDescr;

    public KiePMMLDroolsModelWithSources(String name, String kmodulePackageName, Map<String, String> sourcesMap, PackageDescr packageDescr) {
        super(name, Collections.emptyList());
        this.sourcesMap = Collections.unmodifiableMap(sourcesMap);
        this.kmodulePackageName = kmodulePackageName;
        this.packageDescr = packageDescr;
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
        return sourcesMap;
    }

    @Override
    public String getKModulePackageName() {
        return kmodulePackageName;
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }
}
