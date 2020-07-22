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

import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.pmml.commons.exceptions.KiePMMLException;

import static org.junit.Assert.*;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class KiePMMLDroolsModelWithSourcesTest {

    private static final KieBase KIE_BASE = new KnowledgeBaseImpl("PMML", null);
    private final static String MODEL_NAME = "modelNaMe";
    private final static String KMODULEPACKAGENAME = getSanitizedPackageName(MODEL_NAME);
    private final static Map<String, String> SOURCES_MAP = new HashMap<>();
    private final static PackageDescr PACKAGE_DESCR = new PackageDescr();

    private KiePMMLDroolsModelWithSources kiePMMLDroolsModelWithSources;

    @Before
    public void setup() {
        kiePMMLDroolsModelWithSources = new KiePMMLDroolsModelWithSources(MODEL_NAME, KMODULEPACKAGENAME, SOURCES_MAP, PACKAGE_DESCR);
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

    @Test
    public void getKModulePackageName() {
        assertEquals(KMODULEPACKAGENAME, kiePMMLDroolsModelWithSources.getKModulePackageName());
    }

    @Test
    public void getPackageDescr() {
        assertEquals(PACKAGE_DESCR, kiePMMLDroolsModelWithSources.getPackageDescr());
    }
}