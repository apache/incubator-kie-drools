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
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLSegments;

public class KiePMMLSegmentationTest {

    private static final String SEGMENTATION_NAME = "SEGMENTATION_NAME";
    private static final MULTIPLE_MODEL_METHOD MULTIPLE_MODELMETHOD = MULTIPLE_MODEL_METHOD.MAJORITY_VOTE;
    private static KiePMMLSegmentation.Builder BUILDER;
    private static KiePMMLSegmentation KIE_PMML_SEGMENTATION;

    @BeforeClass
    public static void setup() {
        BUILDER = KiePMMLSegmentation.builder(SEGMENTATION_NAME, Collections.emptyList(),
                                              MULTIPLE_MODELMETHOD);
        assertNotNull(BUILDER);
        KIE_PMML_SEGMENTATION = BUILDER.build();
        assertNotNull(KIE_PMML_SEGMENTATION);
    }

    @Test
    public void getMultipleModelMethod() {
        assertEquals(MULTIPLE_MODELMETHOD, KIE_PMML_SEGMENTATION.getMultipleModelMethod());
    }

    @Test
    public void getSegments() {
        assertNull(KIE_PMML_SEGMENTATION.getSegments());
        final List<KiePMMLSegment> segments = getKiePMMLSegments();
        KIE_PMML_SEGMENTATION = BUILDER.withSegments(segments).build();
        assertEquals(segments, KIE_PMML_SEGMENTATION.getSegments());
    }




}