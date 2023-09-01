package org.drools.compiler.integrationtests.equalitymode;

import java.util.Collection;

import org.drools.compiler.integrationtests.drl.AbstractDeclareTest;
import org.drools.testcoverage.common.util.EngineTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DeclareEqualityModeTest extends AbstractDeclareTest {

    public DeclareEqualityModeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations(EngineTestConfiguration.ALPHA_NETWORK_COMPILER_FALSE,
                                                           EngineTestConfiguration.EQUALITY_MODE,
                                                           EngineTestConfiguration.CLOUD_MODE,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_OFF,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_FLOW,
                                                           EngineTestConfiguration.EXECUTABLE_MODEL_PATTERN);
    }
}
