package org.drools.compiler.integrationtests;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CommandsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CommandsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testSessionTimeCommands() {
        final String drl =
            "package org.drools.compiler.integrationtests \n" +
            "import " + Cheese.class.getCanonicalName() + " \n" +
            "rule StringRule \n" +
            "when \n" +
            "    $c : Cheese() \n" +
            "then \n" +
            "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-esp-test", kieBaseTestConfiguration, drl);
        final KieSession kSession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final KieCommands kieCommands = KieServices.get().getCommands();
            assertThat((long) kSession.execute(kieCommands.newGetSessionTime())).isEqualTo(0L);
            assertThat((long) kSession.execute(kieCommands.newAdvanceSessionTime(2, TimeUnit.SECONDS))).isEqualTo(2000L);
            assertThat((long) kSession.execute(kieCommands.newGetSessionTime())).isEqualTo(2000L);
        } finally {
            kSession.dispose();
        }
    }
}
