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

package org.kie.pmml.models.drools.commons.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class KiePMMLDroolsModelWithSourcesTest {

    private static final KieBase KIE_BASE = new KnowledgeBaseImpl("PMML", null);
    private final static String MODEL_NAME = "modelNaMe";
    private final static String KMODULEPACKAGENAME = getSanitizedPackageName(MODEL_NAME);
    private final static String PKGUUID = "PKGUUID";
    private final static Map<String, String> SOURCES_MAP = new HashMap<>();
    private final static Map<String, String> RULES_SOURCES_MAP = new HashMap<>();

    private KiePMMLDroolsModelWithSources kiePMMLDroolsModelWithSources;

    @Before
    public void setup() {
        kiePMMLDroolsModelWithSources = new KiePMMLDroolsModelWithSources(MODEL_NAME,
                                                                          KMODULEPACKAGENAME,
                                                                          PKGUUID,
                                                                          SOURCES_MAP,
                                                                          RULES_SOURCES_MAP);
    }

    @Test
    public void constructor() {
        assertEquals(MODEL_NAME, kiePMMLDroolsModelWithSources.getName());
    }

    @Test(expected = KiePMMLException.class)
    public void evaluate() {
        kiePMMLDroolsModelWithSources.evaluate(KIE_BASE, Collections.emptyMap());
    }

    @Test(expected = KiePMMLException.class)
    public void getOutputFieldsMap() {
        kiePMMLDroolsModelWithSources.getOutputFieldsMap();
    }

    @Test
    public void getSourcesMap() {
        assertEquals(SOURCES_MAP, kiePMMLDroolsModelWithSources.getSourcesMap());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addToGetSourcesMap() {
        Map<String, String> retrieved = kiePMMLDroolsModelWithSources.getSourcesMap();
        retrieved.put("KEY", "VALUE");
    }

    @Test
    public void addSourceMap() {
        Map<String, String> retrieved = kiePMMLDroolsModelWithSources.getSourcesMap();
        assertTrue(retrieved.isEmpty());
        kiePMMLDroolsModelWithSources.addSourceMap("KEY", "VALUE");
        retrieved = kiePMMLDroolsModelWithSources.getSourcesMap();
        assertTrue(retrieved.containsKey("KEY"));
        assertEquals("VALUE", retrieved.get("KEY"));
    }

    @Test
    public void getKModulePackageName() {
        assertEquals(KMODULEPACKAGENAME, kiePMMLDroolsModelWithSources.getKModulePackageName());
    }
}