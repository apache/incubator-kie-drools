package org.drools.compiler.integrationtests.drl;

import java.util.Collection;

import org.drools.testcoverage.common.model.StockTick;
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
public class DRLCepTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DRLCepTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Test
    public void testEventsInDifferentPackages() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "end\n" +
                "rule r1\n" +
                "when\n" +
                "then\n" +
                "    StockTick st = new StockTick();\n" +
                "    st.setCompany(\"RHT\");\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("drl-cep-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            assertThat(ksession.fireAllRules()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }
}
