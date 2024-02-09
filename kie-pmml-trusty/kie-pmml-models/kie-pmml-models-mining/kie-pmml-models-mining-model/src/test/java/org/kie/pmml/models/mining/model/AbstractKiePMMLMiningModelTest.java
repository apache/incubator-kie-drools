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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
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
        return new KiePMMLTestingModel("fileName", modelName, Collections.emptyList());
    }
}
