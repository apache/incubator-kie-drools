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
