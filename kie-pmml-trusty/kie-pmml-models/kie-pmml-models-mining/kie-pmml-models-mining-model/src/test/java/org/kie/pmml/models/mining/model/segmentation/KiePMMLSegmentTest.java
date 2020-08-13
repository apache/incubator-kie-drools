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

package org.kie.pmml.models.mining.model.segmentation;

import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLModel;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLSimplePredicate;

public class KiePMMLSegmentTest {


    private static final KiePMMLModel KIE_PMML_MODEL = getKiePMMLModel("MODEL_NAME");
    private static final KiePMMLPredicate KIE_PMML_PREDICATE = getKiePMMLSimplePredicate("SIMPLE_PREDICATE");
    private static final String SEGMENT_NAME = "SEGMENT_NAME";
    private static KiePMMLSegment.Builder BUILDER;
    private static KiePMMLSegment KIE_PMML_SEGMENT;

    @BeforeClass
    public static void setup() {
        BUILDER = KiePMMLSegment.builder(SEGMENT_NAME, Collections.emptyList(),
                                         KIE_PMML_PREDICATE, KIE_PMML_MODEL);
        assertNotNull(BUILDER);
        KIE_PMML_SEGMENT = BUILDER.build();
        assertNotNull(KIE_PMML_SEGMENT);
    }


    @Test
    public void getWeight() {
        final double weight = 33.45;
        assertEquals(1.0, KIE_PMML_SEGMENT.getWeight(), 0.0);
        KIE_PMML_SEGMENT = BUILDER.withWeight(weight).build();
        assertEquals(weight, KIE_PMML_SEGMENT.getWeight(), 0.0);
    }

    @Test
    public void getKiePMMLPredicate() {
        assertEquals(KIE_PMML_PREDICATE, KIE_PMML_SEGMENT.getKiePMMLPredicate());
    }

    @Test
    public void getModel() {
        assertEquals(KIE_PMML_MODEL, KIE_PMML_SEGMENT.getModel());
    }



}