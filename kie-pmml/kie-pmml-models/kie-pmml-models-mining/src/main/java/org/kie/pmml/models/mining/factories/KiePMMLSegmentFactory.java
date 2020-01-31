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
package org.kie.pmml.models.mining.factories;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.mining.Segment;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.mining.segmentation.KiePMMLSegment;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.library.commons.implementations.KiePMMLModelRetriever.getFromDataDictionaryAndModel;
import static org.kie.pmml.models.core.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.models.core.factories.KiePMMLPredicateFactory.getPredicate;

public class KiePMMLSegmentFactory {

    private static final Logger log = Logger.getLogger(KiePMMLSegmentFactory.class.getName());



    public static List<KiePMMLSegment> getSegments(List<Segment> segments, DataDictionary dataDictionary) throws KiePMMLException {
        log.info("getSegments " + segments);
        return segments.stream().map(throwingFunctionWrapper(segment -> getSegment(segment, dataDictionary))).collect(Collectors.toList());
    }

    public static KiePMMLSegment getSegment(Segment segment, DataDictionary dataDictionary) throws KiePMMLException {
        log.info("getSegment " + segment);
        return KiePMMLSegment.builder(getKiePMMLExtensions(segment.getExtensions()),
                                      getPredicate(segment.getPredicate(), dataDictionary),
                                      getFromDataDictionaryAndModel(dataDictionary, segment.getModel()).orElseThrow(() -> new KiePMMLException("Failed to get the KiePMMLModel for segment " + segment.getId())))
                .withWeight(segment.getWeight().doubleValue())
                .build();
    }

    private KiePMMLSegmentFactory() {
    }
}
