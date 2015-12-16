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

import static org.mockito.Mockito.mock;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.drools.core.base.TypeResolver;
import org.drools.workbench.models.testscenarios.backend.SqlDateWrapper;
import org.drools.workbench.models.testscenarios.shared.VerifyField;

public class FactFieldValueVerifierTest extends TestCase {
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
}
