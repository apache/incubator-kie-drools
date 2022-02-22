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

import java.util.List;
import java.util.Map;

import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.commons.HasRule;
import org.kie.pmml.commons.model.IsDrools;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;

/**
 * KIE representation of PMML model that use <b>Drools</b> for implementation
 */
public class KiePMMLDroolsModelWithSources extends KiePMMLModelWithSources implements IsDrools,
                                                                                      HasRule {

    private static final long serialVersionUID = -168095076511604775L;
    protected Map<String, String> rulesSourceMap;
    private final String pkgUUID;

    public KiePMMLDroolsModelWithSources(final String modelName,
                                         final String kmodulePackageName,
                                         final List<MiningField> miningFields,
                                         final List<OutputField> outputFields,
                                         final List<TargetField> targetFields,
                                         final Map<String, String> sourcesMap,
                                         final String pkgUUID,
                                         final Map<String, String> rulesSourceMap) {
        super(modelName, kmodulePackageName, miningFields, outputFields, targetFields, sourcesMap, false);
        this.pkgUUID = pkgUUID;
        this.rulesSourceMap = rulesSourceMap;
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
