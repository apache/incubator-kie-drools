package org.drools.testcoverage.regression;

import java.io.StringReader;

import java.util.Collection;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;

@RunWith(Parameterized.class)
public class AmbiguousExceptionTest {
    private static final String DRL =
            "   package " + TestConstants.PACKAGE_REGRESSION + "\n\n "
            + " import " + TestConstants.PACKAGE_REGRESSION + ".AmbiguousExceptionTest.Exception\n\n"
            + " rule ruleOne\n"
            + "    when\n "
            + "    then\n"
            + " end\n";

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AmbiguousExceptionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testCompilation() {
        final KieFileSystem fileSystem = KieServices.Factory.get().newKieFileSystem();
        fileSystem.write(
                TestConstants.DRL_TEST_TARGET_PATH,
                KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL)));

        KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, fileSystem, true);
    }

    public static class Exception {
    }
}
