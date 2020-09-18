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
import org.kie.pmml.commons.exceptions.KiePMMLException;

import static org.junit.Assert.assertNotNull;

public class KiePMMLMiningModelWithSourcesTest {

    private static final String MINING_MODEL_NAME = "MINING_MODEL_NAME";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static KiePMMLMiningModelWithSources KIE_PMML_MINING_MODEL;

    @BeforeClass
    public static void setup() {
        KIE_PMML_MINING_MODEL = new KiePMMLMiningModelWithSources(MINING_MODEL_NAME, PACKAGE_NAME, Collections.EMPTY_MAP, Collections.emptyList());
        assertNotNull(KIE_PMML_MINING_MODEL);
    }

    @Test(expected = KiePMMLException.class)
    public void evaluate() {
        KIE_PMML_MINING_MODEL.evaluate("KB", Collections.EMPTY_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getOutputFieldsMap() {
        KIE_PMML_MINING_MODEL.getOutputFieldsMap();
    }

}