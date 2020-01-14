/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model;

import java.io.IOException;
import java.io.InputStream;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.PMML4Compiler;
import org.kie.pmml.pmml_4_2.PMML4Unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


public class PMML4UnitImplTest {

    private static PMML4Compiler compiler = new PMML4Compiler();
    private static String TEST_PMML_DIRECTORY = "org/kie/pmml/pmml_4_2/";
    private static String RESOURCE_PATH = TEST_PMML_DIRECTORY +
                                          "single_audit_dectree.pmml";
    private static String CAPITALIZED_DICTIONARY_ENTRY = "Age";
    private static String LOWERCASE_DICTIONARY_ENTRY = "marital";
    private static String NONEXIST_DICTIONARY_ENTRY = "Marital";
    private PMML4Unit pmmlUnit;

    @Before
    public void before() {
        InputStream is = null;

        Resource res = ResourceFactory.newClassPathResource(RESOURCE_PATH);
        assertNotNull(res);

        try {
            is = res.getInputStream();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        assertNotNull(is);

        PMML pmml = compiler.loadModel(PMML4Compiler.PMML, is);
        assertNotNull(pmml);

        pmmlUnit = new PMML4UnitImpl(pmml);
        assertNotNull(pmmlUnit);
    }

    @Test
    public void testFindCapitalizedDataDictionaryEntry() {
        PMMLDataField field = pmmlUnit.findDataDictionaryEntry(CAPITALIZED_DICTIONARY_ENTRY);
        assertNotNull(field);
    }

    @Test
    public void testFindLowercaseDataDictionaryEntry() {
        PMMLDataField field = pmmlUnit.findDataDictionaryEntry(LOWERCASE_DICTIONARY_ENTRY);
        assertNotNull(field);
    }

    @Test
    public void testMissingDataDictionaryEntry() {
        PMMLDataField field = pmmlUnit.findDataDictionaryEntry(NONEXIST_DICTIONARY_ENTRY);
        assertNull(field);
    }
}
