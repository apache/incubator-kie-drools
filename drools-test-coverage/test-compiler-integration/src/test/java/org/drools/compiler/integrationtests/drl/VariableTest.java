package org.drools.compiler.integrationtests.drl;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class VariableTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public VariableTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testVariableDeclaration() {
        final String drl = "rule KickOff\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "then\n" +
                "int i;\n" +
                "end";

        KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, drl);
    }
}
