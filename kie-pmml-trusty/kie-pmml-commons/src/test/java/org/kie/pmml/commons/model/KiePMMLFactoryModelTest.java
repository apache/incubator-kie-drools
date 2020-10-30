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

package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.*;

public class KiePMMLFactoryModelTest {

    private KiePMMLFactoryModel kiePMMLFactoryModel;

    @Before
    public void setup() {
        kiePMMLFactoryModel = new KiePMMLFactoryModel("", "", new HashMap<>());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getSourcesMap() {
        Map<String, String> retrieved = kiePMMLFactoryModel.getSourcesMap();
        retrieved.put("KEY", "VALUE");
    }

    @Test
    public void addSourceMap() {
        Map<String, String> retrieved = kiePMMLFactoryModel.getSourcesMap();
        assertTrue(retrieved.isEmpty());
        kiePMMLFactoryModel.addSourceMap("KEY", "VALUE");
        retrieved = kiePMMLFactoryModel.getSourcesMap();
        assertTrue(retrieved.containsKey("KEY"));
        assertEquals("VALUE", retrieved.get("KEY"));
    }


    @Test(expected = KiePMMLException.class)
    public void evaluate() {
        kiePMMLFactoryModel.evaluate("", Collections.emptyMap());
    }

    @Test(expected = KiePMMLException.class)
    public void getOutputFieldsMap() {
        kiePMMLFactoryModel.getOutputFieldsMap();
    }
}