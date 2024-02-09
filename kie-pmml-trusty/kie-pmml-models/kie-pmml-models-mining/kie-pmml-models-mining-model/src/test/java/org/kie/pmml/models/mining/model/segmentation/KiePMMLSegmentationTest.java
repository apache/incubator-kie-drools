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
package org.kie.pmml.models.mining.model.segmentation;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLSegments;

public class KiePMMLSegmentationTest {

    private static final String SEGMENTATION_NAME = "SEGMENTATION_NAME";
    private static final MULTIPLE_MODEL_METHOD MULTIPLE_MODELMETHOD = MULTIPLE_MODEL_METHOD.MAJORITY_VOTE;
    private static KiePMMLSegmentation.Builder BUILDER;
    private static KiePMMLSegmentation KIE_PMML_SEGMENTATION;

    @BeforeAll
    public static void setup() {
        BUILDER = KiePMMLSegmentation.builder(SEGMENTATION_NAME, Collections.emptyList(),
                                              MULTIPLE_MODELMETHOD);
        assertThat(BUILDER).isNotNull();
        KIE_PMML_SEGMENTATION = BUILDER.build();
        assertThat(KIE_PMML_SEGMENTATION).isNotNull();
    }

    @Test
    void getMultipleModelMethod() {
        assertThat(KIE_PMML_SEGMENTATION.getMultipleModelMethod()).isEqualTo(MULTIPLE_MODELMETHOD);
    }

    @Test
    void getSegments() {
        assertThat(KIE_PMML_SEGMENTATION.getSegments()).isNull();
        final List<KiePMMLSegment> segments = getKiePMMLSegments();
        KIE_PMML_SEGMENTATION = BUILDER.withSegments(segments).build();
        assertThat(KIE_PMML_SEGMENTATION.getSegments()).isEqualTo(segments);
    }




}