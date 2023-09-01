package org.drools.mvel.integrationtests.waltz;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ReteOOWaltzTest extends Waltz {

    public ReteOOWaltzTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }
}
