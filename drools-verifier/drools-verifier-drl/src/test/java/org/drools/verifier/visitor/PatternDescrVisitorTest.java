package org.drools.verifier.visitor;


import org.drools.drl.ast.descr.PackageDescr;
import org.drools.verifier.TestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternDescrVisitorTest extends TestBase {


    @Test
    void testVisitFieldVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsFields(1);

        assertContainsField("age");
        assertContainsVariable("Test", "var");
    }

    @Test
    void testVisitPatternVariableRestriction() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("PatternDescrVisitorTest2.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        assertContainsVariable("Test", "var");
    }

}
