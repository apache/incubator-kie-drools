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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
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
public class MiningModelImplementationProvider implements ModelImplementationProvider<MiningModel,KiePMMLMiningModel>{

    static final String SEGMENTID_TEMPLATE = "%s_Segment_%s";

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
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        populateMissingIds(model);
        return getKiePMMLMiningModel(dataDictionary, transformationDictionary, model, (KnowledgeBuilder) kBuilder);
    }

    @Override
    public KiePMMLMiningModel getKiePMMLModelFromPlugin(final String packageName,
                                                        final DataDictionary dataDictionary,
                                                        final TransformationDictionary transformationDictionary,
                                                        final MiningModel model,
                                                        final Object kBuilder) {
        if (!(kBuilder instanceof KnowledgeBuilder)) {
            throw new KiePMMLException(String.format("Expecting KnowledgeBuilder, received %s", kBuilder.getClass().getName()));
        }
        populateMissingIds(model);
        try {
            final Map<String, String> sourcesMap = KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(dataDictionary, transformationDictionary, model, packageName, (KnowledgeBuilder) kBuilder);
            return new KiePMMLMiningModelWithSources(model.getModelName(), packageName, sourcesMap);
        } catch (IOException e) {
            throw new KiePMMLException(e);
        }
    }

    /**
     * Recursively populate <code>Segment</code>s with auto generated id
     * if missing in original model
     */
    protected void populateMissingIds(final MiningModel model) {
        final List<Segment> segments =model.getSegmentation().getSegments();
        for (int i = 0; i < segments.size(); i ++) {
            Segment segment = segments.get(i);
            if (segment.getId() == null || segment.getId().isEmpty()) {
                String toSet = String.format(SEGMENTID_TEMPLATE, model.getModelName(), i);
                segment.setId(toSet);
                if (segment.getModel() instanceof MiningModel) {
                    populateMissingIds((MiningModel) segment.getModel());
                }
            }
        }

    }

}
