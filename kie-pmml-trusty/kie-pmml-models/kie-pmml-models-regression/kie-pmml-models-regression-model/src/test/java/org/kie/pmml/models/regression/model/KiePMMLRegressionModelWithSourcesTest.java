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

package org.kie.pmml.models.regression.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KiePMMLRegressionModelWithSourcesTest {

    private static final String MODEL_NAME = "MODEL_NAME";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private final static Map<String, String> SOURCES_MAP = new HashMap<>();

    private KiePMMLRegressionModelWithSources kiePMMLRegressionModelWithSources;

    @Before
    public void setup() {
        kiePMMLRegressionModelWithSources = new KiePMMLRegressionModelWithSources(MODEL_NAME, PACKAGE_NAME, SOURCES_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void evaluate() {
        kiePMMLRegressionModelWithSources.evaluate("KB", Collections.EMPTY_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getOutputFieldsMap() {
        kiePMMLRegressionModelWithSources.getOutputFieldsMap();
    }


    @Test
    public void getSourcesMap() {
        assertEquals(SOURCES_MAP, kiePMMLRegressionModelWithSources.getSourcesMap());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addToGetSourcesMap() {
        Map<String, String> retrieved = kiePMMLRegressionModelWithSources.getSourcesMap();
        retrieved.put("KEY", "VALUE");
    }

    @Test
    public void addSourceMap() {
        Map<String, String> retrieved = kiePMMLRegressionModelWithSources.getSourcesMap();
        assertTrue(retrieved.isEmpty());
        kiePMMLRegressionModelWithSources.addSourceMap("KEY", "VALUE");
        retrieved = kiePMMLRegressionModelWithSources.getSourcesMap();
        assertTrue(retrieved.containsKey("KEY"));
        assertEquals("VALUE", retrieved.get("KEY"));
    }

}