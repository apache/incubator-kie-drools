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

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLModel;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLSimplePredicate;

public class KiePMMLSegmentTest {


    private static final KiePMMLModel KIE_PMML_MODEL = getKiePMMLModel("MODEL_NAME");
    private static final KiePMMLPredicate KIE_PMML_PREDICATE = getKiePMMLSimplePredicate("SIMPLE_PREDICATE");
    private static final String SEGMENT_NAME = "SEGMENT_NAME";
    private static KiePMMLSegment.Builder BUILDER;
    private static KiePMMLSegment KIE_PMML_SEGMENT;

    @BeforeAll
    public static void setup() {
        BUILDER = KiePMMLSegment.builder(SEGMENT_NAME, Collections.emptyList(),
                                         KIE_PMML_PREDICATE, KIE_PMML_MODEL);
        assertThat(BUILDER).isNotNull();
        KIE_PMML_SEGMENT = BUILDER.build();
        assertThat(KIE_PMML_SEGMENT).isNotNull();
    }


    @Test
    void getWeight() {
        final double weight = 33.45;
        assertThat(KIE_PMML_SEGMENT.getWeight()).isCloseTo(1.0, Offset.offset(0.0));
        KIE_PMML_SEGMENT = BUILDER.withWeight(weight).build();
        assertThat(KIE_PMML_SEGMENT.getWeight()).isCloseTo(weight, Offset.offset(0.0));
    }

    @Test
    void getKiePMMLPredicate() {
        assertThat(KIE_PMML_SEGMENT.getKiePMMLPredicate()).isEqualTo(KIE_PMML_PREDICATE);
    }

    @Test
    void getModel() {
        assertThat(KIE_PMML_SEGMENT.getModel()).isEqualTo(KIE_PMML_MODEL);
    }



}