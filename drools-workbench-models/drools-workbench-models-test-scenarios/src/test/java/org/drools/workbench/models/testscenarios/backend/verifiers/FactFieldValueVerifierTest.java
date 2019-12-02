/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend.verifiers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import org.drools.workbench.models.testscenarios.backend.SqlDateWrapper;
import org.drools.workbench.models.testscenarios.backend.StringCollection;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.junit.Test;
import org.drools.core.addon.TypeResolver;

import static org.mockito.Mockito.mock;

public class FactFieldValueVerifierTest extends TestCase {

    @Test
    public void testSQLDate() throws Exception {

        SqlDateWrapper sqlDateWrapper = new SqlDateWrapper();
        sqlDateWrapper.setSqlDate(new Date(2012 - 1900, 11, 12));

        HashMap<String, Object> populatedData = new HashMap<String, Object>();
        populatedData.put("sqlDateWrapper", sqlDateWrapper);

        TypeResolver typeResolver = mock(TypeResolver.class);

        FactFieldValueVerifier verifier = new FactFieldValueVerifier(
                populatedData,
                "sqlDateWrapper",
                sqlDateWrapper,
                typeResolver);

        List<VerifyField> fieldValues = new ArrayList<VerifyField>();
        VerifyField verifyField = new VerifyField("sqlDate", "12-DEC-2012", "==");
        fieldValues.add(verifyField);

        verifier.checkFields(fieldValues);

        assertTrue(verifyField.getSuccessResult());
    }

    @Test
    public void testExpectedList() throws Exception {

        final StringCollection collection = new StringCollection();
        collection.getList().add("Edam");
        collection.getList().add("Cheddar");

        final HashMap<String, Object> populatedData = new HashMap<>();
        populatedData.put("collection", collection);

        final TypeResolver typeResolver = mock(TypeResolver.class);

        final FactFieldValueVerifier verifier = new FactFieldValueVerifier(populatedData,
                                                                           "collection",
                                                                           collection,
                                                                           typeResolver);

        final List<VerifyField> fieldValues = new ArrayList<>();
        final VerifyField verifyField = new VerifyField("list", "=[\"Edam\", \"Cheddar\"]", "==");
        fieldValues.add(verifyField);

        verifier.checkFields(fieldValues);

        assertTrue(verifyField.getSuccessResult());
    }

    @Test
    public void testExpectedListComplexValues() throws Exception {

        final StringCollection collection = new StringCollection();
        collection.getList().add("Edam, Mature");
        collection.getList().add("(Cheddar, Premature)");

        final HashMap<String, Object> populatedData = new HashMap<>();
        populatedData.put("collection", collection);

        final TypeResolver typeResolver = mock(TypeResolver.class);

        final FactFieldValueVerifier verifier = new FactFieldValueVerifier(populatedData,
                                                                           "collection",
                                                                           collection,
                                                                           typeResolver);

        final List<VerifyField> fieldValues = new ArrayList<>();
        final VerifyField verifyField = new VerifyField("list", "=[\"Edam, Mature\", \"(Cheddar, Premature)\"]", "==");
        fieldValues.add(verifyField);

        verifier.checkFields(fieldValues);

        assertTrue(verifyField.getSuccessResult());
    }

    @Test
    public void testExpectedListIsEmpty() throws Exception {

        final StringCollection collection = new StringCollection();

        final HashMap<String, Object> populatedData = new HashMap<>();
        populatedData.put("collection", collection);

        final TypeResolver typeResolver = mock(TypeResolver.class);

        final FactFieldValueVerifier verifier = new FactFieldValueVerifier(populatedData,
                                                                           "collection",
                                                                           collection,
                                                                           typeResolver);

        final List<VerifyField> fieldValues = new ArrayList<>();
        final VerifyField verifyField = new VerifyField("list", "=[]", "==");
        fieldValues.add(verifyField);

        verifier.checkFields(fieldValues);

        assertTrue(verifyField.getSuccessResult());
    }

    @Test
    public void testExpectedListHasDifferentContent() throws Exception {

        final StringCollection collection = new StringCollection();
        collection.getList().add("cheddar");

        final HashMap<String, Object> populatedData = new HashMap<>();
        populatedData.put("collection", collection);

        final TypeResolver typeResolver = mock(TypeResolver.class);

        final FactFieldValueVerifier verifier = new FactFieldValueVerifier(populatedData,
                                                                           "collection",
                                                                           collection,
                                                                           typeResolver);

        final List<VerifyField> fieldValues = new ArrayList<>();
        final VerifyField verifyField = new VerifyField("list", "=[\"CHEDDAR\"]", "!=");
        fieldValues.add(verifyField);

        verifier.checkFields(fieldValues);

        assertTrue(verifyField.getSuccessResult());
    }
}
