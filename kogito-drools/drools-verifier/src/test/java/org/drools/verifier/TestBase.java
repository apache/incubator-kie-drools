package org.drools.verifier;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.visitor.PackageDescrVisitor;
import org.junit.Before;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarInputStream;

import static org.junit.Assert.*;

public class TestBase {

    protected VerifierData verifierData;
    protected PackageDescrVisitor packageDescrVisitor;

    @Before
    public void setUp() throws Exception {
        verifierData = VerifierReportFactory.newVerifierData();
        packageDescrVisitor = new PackageDescrVisitor(verifierData,
                Collections.<JarInputStream>emptyList());
    }

    protected PackageDescr getPackageDescr(InputStream resourceAsStream) throws DroolsParserException {
        Reader drlReader = new InputStreamReader(resourceAsStream);
        return new DrlParser(5).parse(drlReader);
    }

    protected void assertContainsVariable(String ruleName, String variableName) {
        Variable variable = verifierData.getVariableByRuleAndVariableName(ruleName,
                variableName);

        assertNotNull(String.format("Could not find Variable : %s ", variableName), variable);
    }

    protected void assertContainsField(String name) {
        Collection<Field> allFields = verifierData.getAll(VerifierComponentType.FIELD);

        for (Field field : allFields) {
            if (name.equals(field.getName())) {
                return;
            }
        }

        fail("Could not find Field");
    }

    protected void assertContainsFields(int amount) {
        Collection<Field> allFields = verifierData.getAll(VerifierComponentType.FIELD);

        assertEquals(amount, allFields.size());
    }
}
