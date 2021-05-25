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

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegmentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.models.mining.model.AbstractKiePMMLMiningModelTest.getKiePMMLSegmentation;

public class KiePMMLMiningModelTest {

    private static final String MINING_MODEL_NAME = "MINING_MODEL_NAME";
    private static final MINING_FUNCTION MININGFUNCTION = MINING_FUNCTION.REGRESSION;
    private static KiePMMLMiningModel.Builder BUILDER;
    private static KiePMMLMiningModel KIE_PMML_MINING_MODEL;

    @BeforeClass
    public static void setup() {
        BUILDER = KiePMMLMiningModel.builder(MINING_MODEL_NAME, Collections.emptyList(), MININGFUNCTION);
        assertNotNull(BUILDER);
        KIE_PMML_MINING_MODEL = BUILDER.build();
        assertNotNull(KIE_PMML_MINING_MODEL);
    }

    @Test(expected = KiePMMLException.class)
    public void evaluate() {
        KIE_PMML_MINING_MODEL.evaluate("KB", Collections.EMPTY_MAP);
    }

    @Test
    public void getAlgorithmName() {
        assertNull(KIE_PMML_MINING_MODEL.getAlgorithmName());
        String algorithmName = "algorithmName";
        KIE_PMML_MINING_MODEL = BUILDER.withAlgorithmName(algorithmName).build();
        assertEquals(algorithmName, KIE_PMML_MINING_MODEL.getAlgorithmName());
    }

    @Test
    public void isScorable() {
        assertTrue(KIE_PMML_MINING_MODEL.isScorable());
        KIE_PMML_MINING_MODEL = BUILDER.withScorable(false).build();
        assertFalse(KIE_PMML_MINING_MODEL.isScorable());
    }

    @Test
    public void getSegmentation() {
        assertNull(KIE_PMML_MINING_MODEL.getSegmentation());
        final KiePMMLSegmentation segmentation = getKiePMMLSegmentation("SEGMENTATION_NAME");
        KIE_PMML_MINING_MODEL = BUILDER.withSegmentation(segmentation).build();
        assertEquals(segmentation, KIE_PMML_MINING_MODEL.getSegmentation());
    }
}