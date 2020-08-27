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
package org.kie.pmml.models.mining.compiler.executor;

import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.models.mining.compiler.factories.KiePMMLMiningModelFactory;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.KiePMMLMiningModelWithSources;

import static org.kie.pmml.models.mining.compiler.factories.KiePMMLMiningModelFactory.getKiePMMLMiningModel;
import static org.kie.pmml.models.mining.model.KiePMMLMiningModel.PMML_MODEL_TYPE;

/**
 * Default <code>ModelImplementationProvider</code> for <b>Mining</b>
 */
public class MiningModelImplementationProvider implements ModelImplementationProvider<MiningModel, KiePMMLMiningModel> {

    public static final String SEGMENTID_TEMPLATE = "%s_Segment_%s";

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL_TYPE;
    }

    @Override
    public KiePMMLMiningModel getKiePMMLModel(final DataDictionary dataDictionary,
                                              final TransformationDictionary transformationDictionary,
                                              final MiningModel model,
                                              final Object kBuilder) {
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s",
                                                     kBuilder.getClass().getName()));
        }
        return getKiePMMLMiningModel(dataDictionary, transformationDictionary, model, (KnowledgeBuilder) kBuilder);
    }

    @Override
    public KiePMMLMiningModel getKiePMMLModelFromPlugin(final String packageName,
                                                        final DataDictionary dataDictionary,
                                                        final TransformationDictionary transformationDictionary,
                                                        final MiningModel model,
                                                        final Object kBuilder) {
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s",
                                                     kBuilder.getClass().getName()));
        }
        final Map<String, String> sourcesMap =
                KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(dataDictionary, transformationDictionary,
                                                                          model, packageName,
                                                                          (KnowledgeBuilder) kBuilder);
        return new KiePMMLMiningModelWithSources(model.getModelName(), packageName, sourcesMap);
    }
}
