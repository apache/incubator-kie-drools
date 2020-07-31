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
package org.kie.pmml.models.mining.compiler.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.Segment;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.getPredicate;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModel;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelFromPlugin;

public class KiePMMLSegmentFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSegmentFactory.class.getName());

    private KiePMMLSegmentFactory() {
    }

    public static List<KiePMMLSegment> getSegments(final DataDictionary dataDictionary,
                                                   final TransformationDictionary transformationDictionary,
                                                   final List<Segment> segments,
                                                   final KnowledgeBuilder kBuilder) {
        logger.debug("getSegments {}", segments);
        return segments.stream().map(segment -> getSegment(dataDictionary, transformationDictionary, segment,
                                                           kBuilder)).collect(Collectors.toList());
    }

    public static KiePMMLSegment getSegment(final DataDictionary dataDictionary,
                                            final TransformationDictionary transformationDictionary,
                                            final Segment segment,
                                            final KnowledgeBuilder kBuilder) {
        logger.debug("getSegment {}", segment);
        return KiePMMLSegment.builder(segment.getId(),
                                      getKiePMMLExtensions(segment.getExtensions()),
                                      getPredicate(segment.getPredicate(), dataDictionary),
                                      getFromCommonDataAndTransformationDictionaryAndModel(dataDictionary,
                                                                                           transformationDictionary,
                                                                                           segment.getModel(),
                                                                                           kBuilder).orElseThrow(() -> new KiePMMLException("Failed to get the KiePMMLModel for segment " + segment.getModel().getModelName())))
                .withWeight(segment.getWeight().doubleValue())
                .build();
    }

    public static Map<String, String> getSegmentsSourcesMap(final String parentPackageName,
                                                            final DataDictionary dataDictionary,
                                                            final TransformationDictionary transformationDictionary,
                                                            final List<Segment> segments,
                                                            final KnowledgeBuilder kBuilder) {
        logger.debug("getSegments {}", segments);
        final Map<String, String> toReturn = new HashMap<>();
        segments.forEach(segment -> toReturn.putAll(getSegmentSourcesMap(parentPackageName,
                                                                         dataDictionary,
                                                                         transformationDictionary, segment,
                                                                         kBuilder)));

        return toReturn;
    }

    public static Map<String, String> getSegmentSourcesMap(
            final String parentPackageName,
            final DataDictionary dataDictionary,
            final TransformationDictionary transformationDictionary,
            final Segment segment,
            final KnowledgeBuilder kBuilder) {
        logger.debug("getSegment {}", segment);
        final String packageName = getSanitizedPackageName(parentPackageName + "." + segment.getId());
        KiePMMLModel kiePmmlModel = getFromCommonDataAndTransformationDictionaryAndModelFromPlugin(
                packageName,
                dataDictionary,
                transformationDictionary,
                segment.getModel(),
                kBuilder)
                .orElseThrow(() -> new KiePMMLException("Failed to get the KiePMMLModel for segment " + segment.getModel().getModelName()));
        if (!(kiePmmlModel instanceof HasSourcesMap)) {
            throw new KiePMMLException("Retrieved KiePMMLModel for segment " + segment.getModel().getModelName() + " " +
                                               "does not implement HasSources");
        }
        return ((HasSourcesMap) kiePmmlModel).getSourcesMap();
    }
}
