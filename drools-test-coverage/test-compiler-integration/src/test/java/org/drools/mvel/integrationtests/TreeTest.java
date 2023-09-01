package org.drools.mvel.integrationtests;

import java.util.Collection;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TreeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TreeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testUnbalancedTrees() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_UnbalancedTrees.drl");
        KieSession wm = kbase.newKieSession();
        try {
            wm.insert(new Cheese("a", 10));
            wm.insert(new Cheese("b", 10));
            wm.insert(new Cheese("c", 10));
            wm.insert(new Cheese("d", 10));
            final Cheese e = new Cheese("e", 10);

            wm.insert(e);
            wm.fireAllRules();

            assertThat(e.getPrice()).as("Rule should have fired twice, seting the price to 30").isEqualTo(30);
        } finally {
            wm.dispose();
        }
    }

}
