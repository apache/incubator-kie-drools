package org.drools.compiler.integrationtests.drl;

import java.util.Collection;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Parameterized.class)
public class ExceptionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ExceptionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    public static class TestException extends RuntimeException {
        public TestException(String message) {
            super(message);
        }
    }

    @Test
    public void testReturnValueException() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + TestException.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "function String throwException( ) {\n" +
                "    throw new TestException( \"this should throw an exception\" );\n" +
                "}\n" +
                "\n" +
                "rule \"Throw ReturnValue Exception\"\n" +
                "    when\n" +
                "        Cheese( type == ( throwException( ) ) )\n" +
                "    then\n" +
                "\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("exception-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Cheese brie = new Cheese("brie", 12);

            assertThatThrownBy(() -> {
                ksession.insert(brie);
                ksession.fireAllRules();
            }).hasRootCauseInstanceOf(TestException.class);
        } finally {
            ksession.dispose();
        }
    }

}
