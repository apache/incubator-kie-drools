package org.drools.compiler.integrationtests;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

@RunWith(Parameterized.class)
public class SubnetworkCEPTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SubnetworkCEPTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Test
    public void testNPEOnFlushingOfUnlinkedPmem() {
        // DROOLS-1285
        final String drl =
                "import " + SubnetworkTest.A.class.getCanonicalName() + "\n" +
                        "import " + SubnetworkTest.B.class.getCanonicalName() + "\n" +
                        "import " + SubnetworkTest.C.class.getCanonicalName() + "\n" +
                        "rule R1 when\n" +
                        "    A()\n" +
                        "    B()\n" +
                        "    not( B() and C() )\n" +
                        "then end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-cep-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactHandle fhA = ksession.insert(new SubnetworkTest.A());
            ksession.insert(new SubnetworkTest.C());
            ksession.fireAllRules();

            ksession.delete(fhA);

            ksession.insert(new SubnetworkTest.A());
            ksession.insert(new SubnetworkTest.B());
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }
}
