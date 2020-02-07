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
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.mining.Segmentation;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.models.mining.api.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.api.model.segmentation.KiePMMLSegmentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.library.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.models.mining.factories.KiePMMLSegmentFactory.getSegments;

public class KiePMMLSegmentationFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSegmentationFactory.class.getName());



    public static List<KiePMMLSegmentation> getSegmentations(List<Segmentation> segmentations, DataDictionary dataDictionary) throws KiePMMLException {
        logger.info("getSegmentations {}", segmentations);
        return segmentations.stream().map(throwingFunctionWrapper(segmentation -> getSegmentation(segmentation, dataDictionary))).collect(Collectors.toList());
    }

    public static KiePMMLSegmentation getSegmentation(Segmentation segmentation, DataDictionary dataDictionary) throws KiePMMLException {
        logger.info("getSegmentation {}", segmentation);
        return KiePMMLSegmentation.builder(getKiePMMLExtensions(segmentation.getExtensions()),
                                                                          MULTIPLE_MODEL_METHOD.byName(segmentation.getMultipleModelMethod().value()))
                .withSegments(getSegments(segmentation.getSegments(), dataDictionary))
                .build();
    }

    private KiePMMLSegmentationFactory() {
    }
}
