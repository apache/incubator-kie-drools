package org.drools.verifier.visitor;


import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.TestBase;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PatternDescrVisitorTest extends TestBase {


    @Test
    public void testVisitFieldVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest.drl"));

        assertNotNull(packageDescr);

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsFields(1);

        assertContainsField("age");
        assertContainsVariable("Test", "var");
    }

    @Test
    public void testVisitPatternVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest2.drl"));

        assertNotNull(packageDescr);

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsVariable("Test", "var");
    }

}
