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
package org.kie.pmml.models.mining.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegmentation;

public abstract class AbstractKiePMMLMiningModelTest {

    public static KiePMMLSegmentation getKiePMMLSegmentation(String segmentationName) {
        return KiePMMLSegmentation.builder(segmentationName, Collections.emptyList(),
                                           MULTIPLE_MODEL_METHOD.AVERAGE)
                .withSegments(getKiePMMLSegments())
                .build();
    }

    public static List<KiePMMLSegment> getKiePMMLSegments() {
        return IntStream.range(0, 3)
                .mapToObj(i -> getKiePMMLSegment("SEGMENT-"+i))
                .collect(Collectors.toList());
    }

    public static KiePMMLSegment getKiePMMLSegment(String segmentName) {
        return KiePMMLSegment.builder(segmentName,
                                      Collections.emptyList(),
                                      getKiePMMLSimplePredicate(segmentName + "-PREDICATE"),
                                      getKiePMMLModel(segmentName + "-MODEL"))
                .build();
    }

    public static KiePMMLSimplePredicate getKiePMMLSimplePredicate(String predicateName) {
        return KiePMMLSimplePredicate.builder(predicateName, Collections.emptyList(), OPERATOR.EQUAL).build();
    }

    public static KiePMMLModel getKiePMMLModel(String modelName) {
        return new KiePMMLModel(modelName, Collections.emptyList()) {
            @Override
            public Object evaluate(Object knowledgeBase, Map<String, Object> requestData) {
                return null;
            }
        };
    }
}
