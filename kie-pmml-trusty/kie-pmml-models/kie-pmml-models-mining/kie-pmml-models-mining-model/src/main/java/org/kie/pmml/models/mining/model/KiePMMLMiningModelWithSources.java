/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.mining.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;

/**
 * @see <a href=http://dmg.org/pmml/v4-3/MultipleModels.html>MiningModel</a>
 */
public class KiePMMLMiningModelWithSources extends KiePMMLModelWithSources implements HasNestedModels {

    private static final long serialVersionUID = -1375185422040275122L;
    protected List<KiePMMLModel> nestedModels;

    public KiePMMLMiningModelWithSources(final String fileName,
                                         final String modelName,
                                         final String kmodulePackageName,
                                         final List<MiningField> miningFields,
                                         final List<OutputField> outputFields,
                                         final List<TargetField> targetFields,
                                         final Map<String, String> sourcesMap,
                                         final List<KiePMMLModel> nestedModels) {
        super(fileName, modelName, kmodulePackageName, miningFields, outputFields, targetFields, sourcesMap, false);
        this.nestedModels = Collections.unmodifiableList(nestedModels);
    }

    @Override
    public List<KiePMMLModel> getNestedModels() {
        return nestedModels;
    }
}