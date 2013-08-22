package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.utils.KieHelper;

public class MultipleIdenticalRulesTest extends CommonTestMethodBase {

    @Test
    @Ignore("Throws UnsupportedOperationException - see https://bugzilla.redhat.com/show_bug.cgi?id=999851")
    public void testCreateKBaseWithThreeIdenticalForallRulesAndThenFireAllRules() {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addFromClassPath("/org/drools/compiler/integrationtests/three_identical_rules_with_forall.drl");
        kieHelper.build().newKieSession().fireAllRules();
    }

}
